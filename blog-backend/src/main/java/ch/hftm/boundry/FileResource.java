package ch.hftm.boundry;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import ch.hftm.control.FileService;
import ch.hftm.entity.BlogFile;
import ch.hftm.entity.GetResponse;
import ch.hftm.exception.MinioFileNotAddedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("files")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class FileResource {
    @Inject
    FileService fileService;

    public static final String BUCKET_NAME = "blogfiles";

    @POST
    @APIResponses({ @APIResponse(responseCode = "400", description = "Invalid input"),
    @APIResponse(responseCode = "500", description = "Internal Server Error"),
    @APIResponse(responseCode = "503", description = "Minio Service Unavailable"),
    @APIResponse(responseCode = "201", description = "File created", content = {
    @Content(mediaType = "application/json", schema = @Schema(implementation = BlogFile.class))}) // Replace @Schema with @SchemaRef
    })
    public Response addFile(@RestForm("file") FileUpload file) {
        try {
            BlogFile blogFile = fileService.addFile(file, BUCKET_NAME);
            return Response.status(201).entity(blogFile).build();
        } catch (IOException e) {
            return Response.status(500).entity("Internal Server Error").build();
        } catch (NoSuchAlgorithmException e) {
            return Response.status(500).entity("Internal Server Error").build();
        } catch (MinioFileNotAddedException e) {
            return Response.status(503).entity("Minio Service Unavailable").build();
        } catch (Exception e) {
            return Response.status(400).entity("Invalid input").build();
        }
    }

    @GET
    @APIResponses({@APIResponse(responseCode = "200", description = "Files found", content = {
    @Content(mediaType = "application/json", schema = @Schema(implementation = BlogFile.class, type = SchemaType.ARRAY))})
    })
    public List<BlogFile> getFiles() {
        return fileService.getFiles();
    }

    @GET
    @Path("{id}")
    @APIResponses({ @APIResponse(responseCode = "404", description = "File not found"),
    @APIResponse(responseCode = "200", description = "File found", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = BlogFile.class))})
    })
    public Response getFile(@PathParam("id") long id) {
        try {
            BlogFile blogFile = fileService.getFile(id)
                .orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));
            return Response.ok(blogFile).build();
        } catch (Exception e) {
            return Response.status(404).entity(e.getMessage().toString()).build();
        }
    }

    @GET
    @Path("{id}/object")
    @APIResponses({ @APIResponse(responseCode = "404", description = "File not found"),
    @APIResponse(responseCode = "500", description = "Internal Server Error"),
    @APIResponse(responseCode = "503", description = "Minio Service Unavailable"),
    @APIResponse(responseCode = "200", description = "File found")
    })
    public Response getFileObject(@PathParam("id") long id, @QueryParam("download") boolean download) {
        try {
            GetResponse getResponse =  fileService.getFileObject(id);
            
            if (download) {
                return Response.ok(getResponse.getStream())
                .header("Content-Disposition", "attachment; filename=\"" + getResponse.getBlogFile().getDisplayname() + "\"")
                .build();
            } else {
                return Response.ok(getResponse.getStream())
                .header("Content-Type", getResponse.getContentType())
                .build();
            }
        } catch (NotFoundException e) {
            return Response.status(404).entity(e.getMessage().toString()).build();
        } catch (MinioFileNotAddedException e) {
            return Response.status(503).entity("Minio Service Unavailable").build();
        } catch (Exception e) {
            return Response.status(500).entity("Internal Server Error").build(); 
        }
    }
}
