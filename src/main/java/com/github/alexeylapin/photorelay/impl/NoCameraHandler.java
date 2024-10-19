package com.github.alexeylapin.photorelay.impl;

import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.Handler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

@Slf4j
@Deprecated
public class NoCameraHandler implements Handler {

    private final int order;
    private final Path baseTragetPath;

    public NoCameraHandler(int order, Path baseTragetPath) {
        this.order = order;
        this.baseTragetPath = baseTragetPath;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean isEligible(HandleContext handleContext) {
        String cameraMake = null;
        String cameraModel = null;
        Collection<ExifDirectoryBase> directories = handleContext.getMetadata()
                .getDirectoriesOfType(ExifDirectoryBase.class);
        for (ExifDirectoryBase directory : directories) {
            String cameraMakeValue = directory.getString(ExifDirectoryBase.TAG_MAKE);
            if (cameraMakeValue != null) {
                cameraMake = cameraMakeValue;
            }
            String cameraModelValue = directory.getString(ExifDirectoryBase.TAG_MODEL);
            if (cameraModelValue != null) {
                cameraModel = cameraModelValue;
            }
        }
        Collection<QuickTimeMetadataDirectory> directories2 = handleContext.getMetadata()
                .getDirectoriesOfType(QuickTimeMetadataDirectory.class);
        for (QuickTimeDirectory directory : directories2) {
            String cameraMakeValue = directory.getString(QuickTimeMetadataDirectory.TAG_MAKE);
            if (cameraMakeValue != null) {
                cameraMake = cameraMakeValue;
            }
            String cameraModelValue = directory.getString(QuickTimeMetadataDirectory.TAG_MODEL);
            if (cameraModelValue != null) {
                cameraModel = cameraModelValue;
            }
        }
        return cameraMake == null && cameraModel == null;
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
