package ch.hftm.control;


import java.util.List;
import java.util.Optional;

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
            var blogFile = blogFileRepository.findByIdOptional(id);
            return blogFile;
        }

        public List<BlogFile> getBlogFiles() {
            var blogFiles = blogFileRepository.listAll();
            return blogFiles;
        }

        @Transactional
        public long addBlogFile(NewBlogFileDto blogFileDto) {
            BlogFile blogFile = blogFileMapper.toValidBlogFile(blogFileDto);
            blogFileRepository.persist(blogFile);
            return blogFile.getId();
        }

        @Transactional
        public void removeBlogFile(BlogFile blogFile) {
            blogFileRepository.delete(blogFile);
        }
}
