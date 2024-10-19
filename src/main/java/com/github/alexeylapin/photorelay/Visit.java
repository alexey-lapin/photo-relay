package com.github.alexeylapin.photorelay;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

@Getter
@Slf4j
public class Visit implements FileVisitor<Path> {

    private final Path basePath;
    private final Collection<Handler> handlers;

    public Visit(@NonNull Path basePath, @NonNull Collection<Handler> handlers) {
        this.basePath = basePath;
        this.handlers = handlers;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (isWritable(file)) {
            Path relative = basePath.relativize(file);
            Metadata metadata;
            try (InputStream inputStream = Files.newInputStream(file)) {
                metadata = ImageMetadataReader.readMetadata(inputStream);
            } catch (ImageProcessingException ex) {
                log.error("failed to read metadata: {}", file, ex);
//                throw new IOException("failed to read metadata", ex);
                return FileVisitResult.CONTINUE;
            }

            for (Handler handler : handlers) {
                HandleContext handleContext = new HandleContext(file, relative, attrs, metadata, handler.getName());
                if (handler.isEligible(handleContext)) {
                    handler.handle(handleContext);
                    break;
                }
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        log.error("visitFileFailed: {}", file, exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    private boolean isWritable(Path path) {
        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.APPEND)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
