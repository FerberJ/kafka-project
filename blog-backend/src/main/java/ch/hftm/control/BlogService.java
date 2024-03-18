package ch.hftm.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.hftm.control.dto.BlogDto.NewBlogDto;
import ch.hftm.control.mapper.BlogMapper;
import ch.hftm.entity.Blog;
import ch.hftm.entity.BlogFile;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Dependent
public class BlogService {
    @Inject
    BlogRepository blogRepository;

    @Inject
    BlogMapper blogMapper;

    @Inject
    BlogFileService blogFileService;

    public List<Blog> getBlogs() {
        var blogs = blogRepository.listAll();
        return blogs;
    }

    public Optional<Blog> getBlog(long id) {
        var blog = blogRepository.findByIdOptional(id);
        return blog;
    }

    @Transactional
    public long addBlog(NewBlogDto blogDto) {
        Blog blog = blogMapper.toValidBlog(blogDto);
        blogRepository.persist(blog);
        return blog.getId();
    }

    @Transactional
    public void updateIsValid(Blog blog) {
        blogRepository.update("valid = ?1 where id = ?2", blog.isValid(), blog.getId());
    }

    @Transactional
    public void updateBlog(NewBlogDto newBlogDto, long id) {
        Blog blog = blogRepository.findById(id);
        blog.setContent(newBlogDto.getContent());
        blog.setTitle(newBlogDto.getTitle());

        List<BlogFile> emptyFile = new ArrayList<>();
        blog.setFiles(emptyFile);

        for (int blogFileInt : newBlogDto.getFiles()) {
           BlogFile blogFile = blogFileService.getBlogFile(blogFileInt).orElseGet(null);
           if (blogFile != null) {
            blog.addBlogFile(blogFile);
           }
        }
    }

    @Transactional
    public void addBlogFile(long blogId, long blogFileId) {
        Blog blog = blogRepository.findById(blogId);

        BlogFile blogFile = blogFileService.getBlogFile(blogFileId).orElse(null);
        if (blogFile != null) {
            blog.addBlogFile(blogFile);
        }
    }

    @Transactional
    public void removeBlog(long id) {
        blogRepository.deleteById(id);
    }
}
