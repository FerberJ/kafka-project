package ch.hftm.control;

import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import ch.hftm.control.dto.BlogFileDto.NewBlogFileDto;
import ch.hftm.control.dto.BlogFileDto.UpdateBlogFileDto;
import ch.hftm.entity.Blog;
import ch.hftm.entity.BlogFile;
import ch.hftm.entity.GetResponse;
import ch.hftm.exception.MinioFileNotAddedException;
import ch.hftm.utils.Validation;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@Dependent
public class FileService {
    @Inject
    BlogFileService blogFileService;

    @Inject
    MinioService minioService;

    @Inject
    Validation validation;

    public BlogFile addFile(FileUpload file, String bucket)
            throws IOException, NoSuchAlgorithmException, MinioFileNotAddedException {
        byte[] fileContent = Files.readAllBytes(file.uploadedFile());
        String hashString = validation.getHashCode(fileContent, "SHA-256");
        String newFileName = UUID.randomUUID().toString();
        String displayName = file.fileName();
        String contentType = file.contentType();

        // Create Dto
        NewBlogFileDto newBlogFileDto = new NewBlogFileDto(newFileName, bucket, hashString, displayName);

        // Check if file with same Hashcode already exists
        // If it does just create a new BlogFile entry
        String existingFileName = blogFileService.searchHashString(hashString, bucket);
        if (existingFileName != "") {
            newBlogFileDto.setFilename(existingFileName); // Overwrite filename with already existing filename
            BlogFile blogFile = blogFileService.addBlogFile(newBlogFileDto);
            return blogFile;
        }

        // Add new File to minio
        try {
            minioService.addFile(fileContent, newFileName, bucket, contentType);
            BlogFile blogFile = blogFileService.addBlogFile(newBlogFileDto);
            return blogFile;
        } catch (Exception e) {
            throw new MinioFileNotAddedException(e);
        }

    }

    public List<BlogFile> getFiles() {
        return blogFileService.getBlogFiles();
    }

    public Optional<BlogFile> getFile(long id) {
        return blogFileService.getBlogFile(id);
    }

    public GetResponse getFileObject(long id) throws NotFoundException, MinioFileNotAddedException {
        BlogFile blogFile = blogFileService.getBlogFile(id)
                .orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));

        try {
            GetResponse getResponse = minioService.getFile(blogFile.getFilename(), blogFile.getBucket());
            getResponse.setBlogFile(blogFile);
            return getResponse;
        } catch (Exception e) {
            throw new MinioFileNotAddedException(e);
        }
    }

    public BlogFile updateDisplayname(UpdateBlogFileDto blogFileDto, long id) throws NotFoundException {
        blogFileService.updateDisplayname(blogFileDto, id);
        return blogFileService.getBlogFile(id)
                .orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));
    }

    public void removeFile(long id) throws NotFoundException, Exception {
        BlogFile blogFile = blogFileService.getBlogFile(id)
            .orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));

        blogFileService.removeBlogFile(id);
        
        String filename = blogFileService.searchHashString(blogFile.getHashcode(), blogFile.getBucket());
        if (filename != "") {
            minioService.deleteFile(filename, blogFile.getBucket());
        }
    }
}
