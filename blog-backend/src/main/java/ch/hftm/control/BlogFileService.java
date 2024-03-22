package ch.hftm.control;

import java.util.List;
import java.util.Optional;

import ch.hftm.control.dto.BlogFileDto.NewBlogFileDto;
import ch.hftm.control.dto.BlogFileDto.UpdateBlogFileDto;
import ch.hftm.control.mapper.BlogFileMapper;
import ch.hftm.entity.BlogFile;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Dependent
public class BlogFileService {
    @Inject
    BlogFileRepository blogFileRepository;

    @Inject
    BlogFileMapper blogFileMapper;

    public Optional<BlogFile> getBlogFile(long id) {
        return blogFileRepository.findByIdOptional(id);
    }

    public List<BlogFile> getBlogFiles() {
        return blogFileRepository.listAll();
    }

    @Transactional
    public BlogFile addBlogFile(NewBlogFileDto blogFileDto) {
        BlogFile blogFile = blogFileMapper.toValidBlogFile(blogFileDto);
        blogFileRepository.persist(blogFile);
        return blogFile;
    }

    @Transactional
    public void removeBlogFile(long id) {
        blogFileRepository.delete("id = ?1", id);
    }

    @Transactional
    public String searchHashString(String hashString, String bucket) {
        List<BlogFile> blogFiles = blogFileRepository.find("hashcode = ?1 and bucket = ?2", hashString, bucket).list();
        if (blogFiles.size() > 0) {
            return blogFiles.get(0).getFilename();
        } else {
            return "";
        }
    }

    @Transactional
    public void updateDisplayname(UpdateBlogFileDto blogFileDto, long id) {
     blogFileRepository.update("displayname = ?1 where id = ?2", blogFileDto.getDisplayname(), id);
    }
}
