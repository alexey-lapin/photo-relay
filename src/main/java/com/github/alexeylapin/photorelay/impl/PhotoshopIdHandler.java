package com.github.alexeylapin.photorelay.impl;

import com.drew.metadata.photoshop.PhotoshopDirectory;
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
public class PhotoshopIdHandler implements Handler {

    private final int order;
    private final Path baseTragetPath;

    public PhotoshopIdHandler(int order, Path baseTragetPath) {
        this.order = order;
        this.baseTragetPath = baseTragetPath;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean isEligible(HandleContext handleContext) {
        Collection<PhotoshopDirectory> directories = handleContext.getMetadata()
                .getDirectoriesOfType(PhotoshopDirectory.class);
        String photoshopCaptionDigest = null;
        for (PhotoshopDirectory directory : directories) {
            String photoshopCaptionDigestValue = directory.getString(PhotoshopDirectory.TAG_CAPTION_DIGEST);
            if (photoshopCaptionDigestValue != null) {
                photoshopCaptionDigest = photoshopCaptionDigestValue;
            }
        }
        return photoshopCaptionDigest != null;
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
