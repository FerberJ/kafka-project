package ch.hftm.boundry;

import java.util.List;

import ch.hftm.control.BlogFileService;
import ch.hftm.control.dto.BlogFileDto.NewBlogFileDto;
import ch.hftm.entity.BlogFile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("blogfiles")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class BlogFileResource {
    @Inject
    BlogFileService blogFileService;

    @GET
    public List<BlogFile> getBlogFiles() {
        return this.blogFileService.getBlogFiles();
    }

    @POST
    public Response addBlogFile(NewBlogFileDto blogFileDto, @Context UriInfo uriInfo) {
        long id = this.blogFileService.addBlogFile(blogFileDto).getId();
        var uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build();

        Response response = Response.created(uri).build();
        return response;
    }

    @GET
    @Path("{id}")
    public BlogFile getBlogFile(long id) {
        try {
            BlogFile blogFile = this.blogFileService.getBlogFile(id)
                .orElseThrow(() -> new NotFoundException("Blog with id " + id + " not found"));
                return blogFile;
        } catch (Exception e) {
            return null;
        }
    }
}
