package ch.hftm.control.mapper;

import ch.hftm.control.dto.BlogFileDto.NewBlogFileDto;
import ch.hftm.control.dto.BlogFileDto.UpdateBlogFileDto;
import ch.hftm.entity.BlogFile;
import jakarta.enterprise.context.Dependent;

@Dependent
public class BlogFileMapper {
    public BlogFile toValidBlogFile(NewBlogFileDto blogFileDto) {
        return new BlogFile(blogFileDto.getFilename(), blogFileDto.getBucket(), blogFileDto.getDisplayname(),
                blogFileDto.getHashcode());
    }

    public BlogFile toValidBlogFile(UpdateBlogFileDto blogFileDto) {
        BlogFile blogFile = new BlogFile();
        blogFile.setDisplayname(blogFileDto.getDisplayname());
        return blogFile;
    }
}
