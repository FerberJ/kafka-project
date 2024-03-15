package ch.hftm.control.mapper;

import java.util.ArrayList;
import java.util.List;

import ch.hftm.control.BlogFileService;
import ch.hftm.control.dto.BlogDto.NewBlogDto;
import ch.hftm.entity.Blog;
import ch.hftm.entity.BlogFile;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class BlogMapper {
    @Inject
    BlogFileService blogFileService;

    public Blog toValidBlog(NewBlogDto blogDto) {
        List<BlogFile> blogFiles = new ArrayList<>();
        for (int fileId : blogDto.getFiles()) {
            BlogFile blogFile = blogFileService.getBlogFile(fileId)
                    .orElse(null);
            if (blogFile != null) {
                blogFiles.add(blogFile);
            }
        }
        return new Blog(blogDto.getTitle(), blogDto.getContent(), blogFiles);
    }
}
