package com.codreal.chatservice.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class FileCleanupService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleFileDeletion(Path filePath) {
        // LÊN LỊCH XÓA FILE SAU 10 GIỜ
        scheduler.schedule(() -> {
            try {
                Files.deleteIfExists(filePath);
                System.out.println("File " + filePath.getFileName() + " has been deleted.");
            } catch (IOException e) {
                System.err.println("Failed to delete file " + filePath.getFileName());
            }
        }, 10, TimeUnit.HOURS);
    }
}
