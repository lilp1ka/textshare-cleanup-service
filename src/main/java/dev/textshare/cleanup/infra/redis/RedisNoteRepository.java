package dev.textshare.cleanup.infra.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisNoteRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisNoteRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


}
