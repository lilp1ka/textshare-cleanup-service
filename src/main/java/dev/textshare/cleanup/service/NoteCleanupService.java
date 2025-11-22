package dev.textshare.cleanup.service;

import dev.textshare.cleanup.infra.redis.RedisNoteRepository;
import dev.textshare.cleanup.infra.storage.MinioNoteStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class NoteCleanupService {

    private final int BATCH_SIZE_UNLIMITED = -1;

    private static final Logger log = LoggerFactory.getLogger(NoteCleanupService.class);

    private final RedisNoteRepository redisNoteRepository;
    private final MinioNoteStorage minioNoteStorage;

    public NoteCleanupService(RedisNoteRepository redisNoteRepository, MinioNoteStorage minioNoteStorage) {
        this.redisNoteRepository = redisNoteRepository;
        this.minioNoteStorage = minioNoteStorage;

    }

    public void cleanup() {
        long timestamp = Instant.now().toEpochMilli();
        Set<String> expiredKeys = redisNoteRepository.findExpiredKeys(timestamp, 100); //TODO: smth with batchSize

        if(expiredKeys.isEmpty()){
            log.info("No expired keys");
            return;
        }

        int deleted = 0;
        int errors = 0;

        for (String key : expiredKeys) {
            try {
                String s3key = redisNoteRepository.getS3Key(key);

                if (s3key == null) {
                    redisNoteRepository.deleteFromZsetAndHset(key);
                    deleted++;
                    continue;
                }


                minioNoteStorage.delete(s3key);
                redisNoteRepository.deleteFromZsetAndHset(key);
                deleted++;
            } catch (Exception e) {
                log.error("Failed to delete note: {}", key, e);
                errors++;
            }
        }
        log.info("Cleanup completed: deleted notes: {}, errors: {}", deleted, errors);
    }
}

