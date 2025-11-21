package dev.textshare.cleanup.infra.storage;
import io.minio.MinioClient;
import org.springframework.stereotype.Component;
import dev.textshare.cleanup.config.MinioProperties;

@Component
public class MinioNoteStorage {
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioNoteStorage(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.bucketName = properties.bucketName();
    }
}
