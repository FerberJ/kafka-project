package ch.hftm.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import ch.hftm.entity.GetResponse;
import ch.hftm.utils.Validation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.MinioException;

@ApplicationScoped
public class MinioService {
    private static final long PART_SIZE = 50 * 1024 * 1024;

    @Inject
    MinioClient minioClient;

    @Inject
    BlogFileService blogFileService;

    @Inject
    Validation validation;

    // When adding a new File the following steps will be made:
    // - Check if bucket exists already, if not it wil be created
    // - Create hashstring of Content from the FileUpload
    // - Create a random UUID wich will be used as filename
    // - Check if File with same Hashcode already exists
    // - If yes just add blogFile entry
    // - If not, create File in Minio and add blogFile entry
    public ObjectWriteResponse addFile(byte[] filecontent, String filename, String bucketname, String contentType)
            throws MinioException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        // Check if bucket already exist
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketname).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketname).build());
        }
        /*
         * byte[] content = Files.readAllBytes(file.uploadedFile());
         * String hashString = validation.getHashCode(content, "SHA-256"); // Get
         * Hashstring of content
         * String filename = UUID.randomUUID().toString(); // UUID is used as Filename
         * NewBlogFileDto newBlogFileDto = new NewBlogFileDto(filename,bucketName,
         * hashString, file.fileName());
         * 
         * String searchedFilename = blogFileService.searchHashString(hashString,
         * bucketName); // Check if file has already been uploaded
         * if (searchedFilename != "") {
         * newBlogFileDto.setFilename(searchedFilename); // Overwrite the filename
         * blogFileService.addBlogFile(newBlogFileDto);
         * return bucketName + "/" + searchedFilename;
         * }
         */

        // Add new File to minio
        InputStream is = new ByteArrayInputStream(filecontent);
        ObjectWriteResponse response = minioClient
                .putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketname)
                                .object(filename)
                                .contentType(contentType)
                                .stream(is, -1, PART_SIZE)
                                .build());
        // blogFileService.addBlogFile(newBlogFileDto);
        return response; // response.bucket() + "/" + response.object();

    }

    public String deleteFile(String filename, String bucketName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build());
            blogFileService.removeFromNameAndBucket(bucketName, filename);
            return "File deleted successfully: " + filename;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public GetResponse getFile(String filename, String bucketName)
            throws MinioException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        GetResponse getResponse = new GetResponse();

        // Check if the object exists
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filename)
                        .build());

        // If the object exists, retrieve it
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filename)
                        .build());

        // Set appropriate headers for the response
        getResponse.setContentType(stat.contentType());
        getResponse.setStream(stream);

        return getResponse;
    }
}
