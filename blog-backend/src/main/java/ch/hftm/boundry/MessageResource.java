package ch.hftm.boundry;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import ch.hftm.control.BlogService;
import ch.hftm.entity.Blog;
import ch.hftm.entity.Message;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;

public class MessageResource {
    @Inject
    BlogService blogService;

    @Blocking
    @Incoming("validation-response")
    public void validateContent(Message payload) {
        Blog blog = new Blog();
        blog.setId(payload.getId());
        blog.setValid(payload.isValid());

        blogService.updateIsValid(blog);
    }
}
