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

    public ObjectWriteResponse addFile(byte[] filecontent, String filename, String bucketname, String contentType)
            throws MinioException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        // Check if bucket already exist
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketname).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketname).build());
        }

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
        return response;
    }

    public void deleteFile(String filename, String bucketName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build());
            return;
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
