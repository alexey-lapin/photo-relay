package com.github.alexeylapin.photorelay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class Scheduler {

    private final Visit visit;

    public Scheduler(Visit visit) {
        this.visit = visit;
    }

    @Scheduled(fixedDelay = 10000)
    public void run() throws IOException {
        log.debug("running");
        Files.walkFileTree(visit.getBasePath(), visit);
    }

}
