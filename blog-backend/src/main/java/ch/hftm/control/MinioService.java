package ch.hftm.control;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;

import org.jboss.resteasy.reactive.multipart.FileUpload;
import jakarta.inject.Inject;
import ch.hftm.entity.GetResponse;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;


public class MinioService {
    private static String bucketName = "test";
    private static final long PART_SIZE = 50 * 1024 * 1024;


    @Inject
    MinioClient minioClient2;

    public String addFile(FileUpload file) throws Exception {
        if (!minioClient2.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient2.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        byte[] content = Files.readAllBytes(file.uploadedFile());
        try (InputStream is = new ByteArrayInputStream(content)) {
            ObjectWriteResponse response = minioClient2
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

    public String deleteFile(String filename) throws Exception {
        try {
            minioClient2.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build());
            return "File deleted successfully: " + filename;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }



    public GetResponse getFile(String filename) throws Exception {
        GetResponse getResponse = new GetResponse();

        try {
            // Check if the object exists
            StatObjectResponse stat = minioClient2.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build());

            // If the object exists, retrieve it
            InputStream stream = minioClient2.getObject(
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

