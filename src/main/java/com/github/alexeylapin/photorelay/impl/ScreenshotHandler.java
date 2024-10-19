package com.github.alexeylapin.photorelay.impl;

import com.drew.metadata.exif.ExifDirectoryBase;
import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.Handler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

@Slf4j
@Deprecated
public class ScreenshotHandler implements Handler {

    private final int order;
    private final Path baseTragetPath;

    public ScreenshotHandler(int order, Path baseTragetPath) {
        this.order = order;
        this.baseTragetPath = baseTragetPath;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean isEligible(HandleContext handleContext) {
        Collection<ExifDirectoryBase> directories = handleContext.getMetadata()
                .getDirectoriesOfType(ExifDirectoryBase.class);
        boolean isScreenshot = false;
        for (ExifDirectoryBase directory : directories) {
            String value = directory.getDescription(ExifDirectoryBase.TAG_USER_COMMENT);
            if (value != null && value.equalsIgnoreCase("screenshot")) {
                isScreenshot = true;
                break;
            }
        }
        return isScreenshot;
    }

    @Override
    public void handle(HandleContext handleContext) throws IOException {
        Path path = handleContext.getRelativePath();
        Path parent = path.getParent();
        String[] parts = path.getFileName().toString().split("\\.");
        Path newPath = parent.resolve(Paths.get("..", "screenshot", parts[0] + "-screenshot." + parts[1]));
        Path targetPath = baseTragetPath.resolve(newPath).normalize();
        Path targetPathParent = targetPath.getParent();
        if (!Files.isDirectory(targetPathParent)) {
            Files.createDirectories(targetPathParent);
        }
        Path sourcePath = handleContext.getAbsolutePath();
        log.info("moving {} to {}", sourcePath, targetPath);
        Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
    }

}
