package ch.hftm.control;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import ch.hftm.entity.GetResponse;
import jakarta.enterprise.context.ApplicationScoped;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;

@ApplicationScoped
public class MinioService {
    private static final long PART_SIZE = 50 * 1024 * 1024;

    private final MinioClient minioClient = MinioClient.builder()
            .endpoint("http://localhost:9000")
            .credentials("RBkZYDO1WQB7U5C2KxwL", "GiaG65DeUjpFXU9jMVBgriB7RqBNcs48s60gwmUC")
            .build();

    public String addFile(FileUpload file, String bucketName) throws Exception {

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        byte[] content = Files.readAllBytes(file.uploadedFile());
        try (InputStream is = new ByteArrayInputStream(content)) {
            ObjectWriteResponse response = minioClient
                    .putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(file.fileName())
                                    .contentType(file.contentType())
                                    .stream(is, -1, PART_SIZE)
                                    .build());
            return response.bucket() + "/" + response.object();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String deleteFile(String filename, String bucketName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build());
            return "File deleted successfully: " + filename;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public GetResponse getFile(String filename, String bucketName) throws Exception {
        GetResponse getResponse = new GetResponse();

        try {
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
            getResponse.contentType = stat.contentType();
            getResponse.stream = stream;

            return getResponse;
        } catch (Exception e) {
            // Handle other exceptions appropriately
            throw new Exception(e);
        }

    }

}
