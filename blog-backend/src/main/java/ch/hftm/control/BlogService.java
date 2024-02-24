package ch.hftm.control;

import java.util.List;
import java.util.Optional;

import ch.hftm.entity.Blog;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Dependent
public class BlogService {
    @Inject
    BlogRepository blogRepository;

    public List<Blog> getBlogs() {
        var blogs = blogRepository.listAll();
        return blogs;
    }

    public Optional<Blog> getBlog(long id) {
        var blog = blogRepository.findByIdOptional(id);
        return blog;
    }

    @Transactional
    public long addBlog(Blog blog) {
        blogRepository.persist(blog);
        return blog.getId();
    }

    @Transactional
    public void updateIsValid(Blog blog) {
        blogRepository.update("valid = ?1 where id = ?2", blog.isValid(), blog.getId());
    }
}
