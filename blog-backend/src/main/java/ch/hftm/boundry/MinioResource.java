package ch.hftm.boundry;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import ch.hftm.control.MinioService;
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
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/minio")
public class MinioResource {
    private static final long PART_SIZE = 50* 1024 * 1024;
    public static final String BUCKET_NAME = "test";
    public static final String NO_SUCH_KEY = "NoSuchKey";

    @Inject
    MinioClient minioClient;



    @POST
    public String addObject(@RestForm("file") FileUpload file) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }
        byte[] content = Files.readAllBytes(file.uploadedFile());
        try (InputStream is = new ByteArrayInputStream(content)) {
            ObjectWriteResponse response = minioClient
                    .putObject(
                            PutObjectArgs.builder()
                                    .bucket(BUCKET_NAME)
                                    .object(file.fileName())
                                    .contentType(file.contentType())
                                    .stream(is, -1, PART_SIZE)
                                    .build());
            return response.bucket() + "/" + response.object();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @DELETE
    @Path("/{filename}")
    public String deleteObject(@PathParam("filename") String filename) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(filename)
                            .build());
            return "Object deleted successfully: " + filename;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @GET
    @Path("/{filename}")
    public Response getObject(@PathParam("filename") String filename, @QueryParam("download") boolean download) {
        try {
            // Check if the object exists
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(filename)
                            .build());

            // If the object exists, retrieve it
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(filename)
                            .build());

            // Set appropriate headers for the response
            String contentType = stat.contentType();

            // Create Header
            if (!download) {
                return Response.ok(stream)
                        .header("Content-Type", contentType)
                        .build();
            } else {
                return Response.ok(stream)
                        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                        .build();
            }

        } catch (MinioException e) {
            // Handle MinioException appropriately
            return Response.status(Response.Status.NOT_FOUND).entity("Object not found: " + filename).build();
        } catch (Exception e) {
            // Handle other exceptions appropriately
            return Response.serverError().entity("Failed to retrieve object: " + filename).build();
        }
    }
}
