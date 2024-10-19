package com.github.alexeylapin.photorelay.impl;

import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.Handler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Deprecated
public class OkHandler implements Handler {

    private final int order;
    private final Path baseTragetPath;

    public OkHandler(int order, Path baseTragetPath) {
        this.order = order;
        this.baseTragetPath = baseTragetPath;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean isEligible(HandleContext handleContext) throws IOException {
        return true;
    }

    @Override
    public void handle(HandleContext handleContext) throws IOException {
        Path path = handleContext.getRelativePath();
        Path targetPath = baseTragetPath.resolve(path);
        Path targetPathParent = targetPath.getParent();
        if (!Files.isDirectory(targetPathParent)) {
            Files.createDirectories(targetPathParent);
        }
        Path sourcePath = handleContext.getAbsolutePath();
        log.info("moving {} to {}", sourcePath, targetPath);
        Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
    }

}
