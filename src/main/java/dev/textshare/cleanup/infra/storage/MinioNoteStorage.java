package dev.textshare.cleanup.infra.storage;

import dev.textshare.cleanup.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MinioNoteStorage {

    private static final Logger log = LoggerFactory.getLogger(MinioNoteStorage.class);

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioNoteStorage(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.bucketName = properties.bucketName();
    }

    public void delete(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );


            log.debug("Deleted note from minIO storage: {}", key);
        } catch (ErrorResponseException e) {
            if ("NoSuchBucket".equals(e.errorResponse().code())) {
                log.error("Bucket does not exist: {}", bucketName);
                throw new IllegalStateException("MinIO bucket does not exist: " + bucketName, e);
            }
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                log.debug("Object already deleted or doesnt exist: {}", key);
                return;
            }
            log.error("MinIO error while deleting: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete from MinIO", e);
        } catch (Exception e){
            log.error("Unexpected error in MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete from MinIO", e);
        }

    }
}
