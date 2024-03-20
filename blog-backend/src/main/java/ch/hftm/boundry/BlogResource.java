package ch.hftm.boundry;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import ch.hftm.control.BlogService;
import ch.hftm.control.dto.BlogDto.NewBlogDto;
import ch.hftm.entity.Blog;
import ch.hftm.entity.BlogFile;
import ch.hftm.entity.Message;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.reactive.messaging.Channel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("blogs")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class BlogResource {
    @Inject
    BlogService blogService;


    @Inject
    @Channel("vaidate-content")
    Emitter<Message> emitter;

    @GET
    @APIResponses({@APIResponse(responseCode = "200", description = "Blogs found", content = {
    @Content(mediaType = "application/json", schema = @Schema(implementation = Blog.class, type = SchemaType.ARRAY))})
    })
    public List<Blog> getBlogs() {
        return this.blogService.getBlogs();
    }
    
    @POST
    @APIResponses({ @APIResponse(responseCode = "400", description = "Invalid input"),
    @APIResponse(responseCode = "500", description = "Internal Server Error"),
    @APIResponse(responseCode = "201", description = "Blog created") // Replace @Schema with @SchemaRef
    })
    public Response addBlog(NewBlogDto blog, @Context UriInfo uriInfo) {
        long id = this.blogService.addBlog(blog);
        var uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build();

        Message message = new Message(id, false, blog.getContent());

        emitter.send(message);
        Response response = Response.created(uri).build();
        return response;
    } 

    @PUT
    @Path("{id}")
    @APIResponses({ @APIResponse(responseCode = "404", description = "Blog not found"),
    @APIResponse(responseCode = "500", description = "Internal Server Error"),
    @APIResponse(responseCode = "200", description = "Update successfull")
    })
    public Response updateBlog(NewBlogDto blog, @PathParam("id") long id) {
        try {
            blogService.updateBlog(blog, id);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(404).entity(e.getMessage().toString()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Internal Server Error").build(); 
        }
    }

    @PUT
    @Path("{blogId}/{blogFileId}")
    @APIResponses({ @APIResponse(responseCode = "404", description = "Blog not found"),
    @APIResponse(responseCode = "500", description = "Internal Server Error"),
    @APIResponse(responseCode = "200", description = "File added successfully")
    })
    public Response addBlogFile(@PathParam("blogId") long blogId, @PathParam("blogFileId") long blogFileId) {
        try {
            blogService.addBlogFile(blogId, blogFileId);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(404).entity(e.getMessage().toString()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Internal Server Error").build(); 
        }
    }

    @GET
    @Path("{id}")
    @APIResponses({ @APIResponse(responseCode = "404", description = "Blog not found"),
    @APIResponse(responseCode = "200", description = "Blog found", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = Blog.class))})
    })
    public Response getBlog(long id) {
        try {
            Blog blog = this.blogService.getBlog(id);
            return Response.ok(blog).build();
        } catch (NotFoundException e) {
            return Response.status(404).entity(e.getMessage().toString()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Internal Server Error").build(); 
        }
    }

    @DELETE
    @Path("{id}")
    @APIResponses({ @APIResponse(responseCode = "404", description = "Blog not found"),
    @APIResponse(responseCode = "500", description = "Internal Server Error"),
    @APIResponse(responseCode = "204", description = "Successfully deleted")
    })
    public Response removeBlog(long id) {
        try {
            blogService.removeBlog(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(404).entity(e.getMessage().toString()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Internal Server Error").build(); 
        }
    }
}
