package ch.hftm.boundry;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import ch.hftm.control.MinioService;
import ch.hftm.entity.GetResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/minio")
public class MinioResource {

    @Inject
    MinioService minioService;

    public static final String BUCKET_NAME = "test";

    @POST
    public String addObject(@RestForm("file") FileUpload file, @PathParam("blogid") int blogid) throws Exception {
        return minioService.addFile(file, BUCKET_NAME);
    }

 
    @DELETE
    @Path("/{filename}")
    public String deleteObject(@PathParam("filename") String filename) throws Exception {
        return minioService.deleteFile(filename, BUCKET_NAME);
    }

    @GET
    @Path("/{filename}")
    public Response getObject(@PathParam("filename") String filename, @QueryParam("download") boolean download) {
        try {
            

        GetResponse getResponse = minioService.getFile(filename, BUCKET_NAME);
 
            // Create Header
            if (!download) {
                return Response.ok(getResponse.stream)
                        .header("Content-Type", getResponse.contentType)
                        .build();
            } else {
                return Response.ok(getResponse.stream)
                        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                        .build();
            }

         
    } catch (Exception e) {
        return Response.noContent().build();
    }
    }}


