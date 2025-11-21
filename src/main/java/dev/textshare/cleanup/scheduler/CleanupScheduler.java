package dev.textshare.cleanup.scheduler;

import dev.textshare.cleanup.config.CleanupProperties;
import org.springframework.stereotype.Component;
import dev.textshare.cleanup.service.NoteCleanupService;

@Component
public class CleanupScheduler {
    private final NoteCleanupService noteCleanupService;
    private final CleanupProperties cleanupProperties;

    public CleanupScheduler(NoteCleanupService noteCleanupService, CleanupProperties cleanupProperties) {
        this.noteCleanupService = noteCleanupService;
        this.cleanupProperties = cleanupProperties;
    }
}
