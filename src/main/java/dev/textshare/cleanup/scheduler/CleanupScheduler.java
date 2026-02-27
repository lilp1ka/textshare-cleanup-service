package dev.textshare.cleanup.scheduler;

import dev.textshare.cleanup.config.CleanupProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import dev.textshare.cleanup.service.NoteCleanupService;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class CleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(CleanupScheduler.class);


    private final NoteCleanupService noteCleanupService;
    private final CleanupProperties cleanupProperties;

    public CleanupScheduler(NoteCleanupService noteCleanupService, CleanupProperties cleanupProperties) {
        this.noteCleanupService = noteCleanupService;
        this.cleanupProperties = cleanupProperties;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS) //todo: move to cfg
    public void runCleanup(){
        if(!cleanupProperties.enabled()){
            log.debug("Cleanup is disabled");
            return;
        }

        Instant start = Instant.now();
        log.info("Starting scheduled cleanup...");
        noteCleanupService.cleanup();

        Duration duration = Duration.between(start, Instant.now());

        log.info("Cleanup completed in {} ms", duration.toSeconds());
    }
}
