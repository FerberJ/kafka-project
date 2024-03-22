package ch.hftm.control;

import java.util.ArrayList;
import java.util.List;

import ch.hftm.control.dto.BlogDto.NewBlogDto;
import ch.hftm.control.mapper.BlogMapper;
import ch.hftm.entity.Blog;
import ch.hftm.entity.BlogFile;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

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

    public Blog getBlog(long id) throws NotFoundException {
        var blog = blogRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException());
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
    public void updateBlog(NewBlogDto newBlogDto, long id) throws NotFoundException {
        Blog blog = blogRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException());
        blog.setContent(newBlogDto.getContent());
        blog.setTitle(newBlogDto.getTitle());

        blog.setFiles(new ArrayList<>());

        for (int blogFileInt : newBlogDto.getFiles()) {
           BlogFile blogFile = blogFileService.getBlogFile(blogFileInt).orElseGet(null);
           if (blogFile != null) {
            blog.addBlogFile(blogFile);
           }
        }
    }

    @Transactional
    public void addBlogFile(long blogId, long blogFileId) throws NotFoundException {
        Blog blog = blogRepository.findByIdOptional(blogId)
            .orElseThrow(()-> new NotFoundException("Blog with id " + blogId + " not found"));

        BlogFile blogFile = blogFileService.getBlogFile(blogFileId)
        .orElseThrow(() -> new NotFoundException("File with id " + blogFileId + " not found"));
        
        blog.addBlogFile(blogFile);
    }

    @Transactional
    public void removeBlog(long id) throws NotFoundException  {
        blogRepository.findByIdOptional(id)
            .orElseThrow(()-> new NotFoundException("Blog with id " + id + " not found"));
        blogRepository.deleteById(id);
    }
}
