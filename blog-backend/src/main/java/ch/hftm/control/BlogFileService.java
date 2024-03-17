package ch.hftm.control;


import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import ch.hftm.control.dto.BlogFileDto.NewBlogFileDto;
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
        public void removeBlogFile(BlogFile blogFile) {
            blogFileRepository.delete(blogFile);
        }

        @Transactional
        public void removeFromNameAndBucket(String bucket, String filename) {
            blogFileRepository.delete("bucket = ?1 and filename = ?2", bucket, filename);
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
}
