package ch.hftm.boundry;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import ch.hftm.control.BlogService;
import ch.hftm.control.dto.BlogDto.NewBlogDto;
import ch.hftm.entity.Blog;
import ch.hftm.entity.Message;


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
    public List<Blog> getBlogs() {
        return this.blogService.getBlogs();
    }
    
    @POST
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
    public Response updateBlog(NewBlogDto blog, @PathParam("id") long id) {
        blogService.updateBlog(blog, id);
        return Response.ok().build();
    }

    @PUT
    @Path("{blogId}/{blogFileId}")
    public Response addBlogFile(@PathParam("blogId") long blogId, @PathParam("blogFileId") long blogFileId) {
        blogService.addBlogFile(blogId, blogFileId);
        return Response.ok().build();
    }

    @GET
    @Path("{id}")
    public Blog getBlog(long id) {
        try {
            Blog blog = this.blogService.getBlog(id)
            .orElseThrow(() -> new NotFoundException("Blog with id " + id + " not found"));
            return blog;
        } catch (Exception e) {
            return null;
        }
    }

    @DELETE
    @Path("{id}")
    public void removeBlog(long id) {
        blogService.removeBlog(id);
    }
}
