package dev.textshare.cleanup.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cleanup")
public record CleanupProperties(
        int intervalMinutes,
        int batchSize,
        boolean enabled
) {
}
