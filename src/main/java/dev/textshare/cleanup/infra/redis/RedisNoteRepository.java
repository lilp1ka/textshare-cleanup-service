package dev.textshare.cleanup.infra.redis;

import dev.textshare.cleanup.scheduler.CleanupScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class RedisNoteRepository {
    private final RedisTemplate<String, String> redisTemplate;


    private static final Logger log = LoggerFactory.getLogger(RedisNoteRepository.class);


    public RedisNoteRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Set<String> findExpiredKeys(long now, int batchSize){
        Set<String> expiredKeys;

        if(batchSize > 0){
            expiredKeys = redisTemplate.opsForZSet().rangeByScore("notes:expiry", 0, now, 0, batchSize);
        }
        else {
            expiredKeys = redisTemplate.opsForZSet().rangeByScore("notes:expiry", 0, now);
        }
        log.debug(expiredKeys.toString());

        return expiredKeys;

    }

    public void deleteFromZsetAndHset(String code){
        redisTemplate.delete(code);
        redisTemplate.opsForZSet().remove("notes:expiry", code);
    }

    public String getS3Key(String code){
        Object s3key = redisTemplate.opsForHash().get(code, "link_text");

        return s3key != null ? s3key.toString() : null;
    }

}
