package com.github.alexeylapin.photorelay.action;

import com.github.alexeylapin.photorelay.HandleContext;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MoveAction implements Consumer<HandleContext> {

    private final Path baseTragetPath;
    private final Pattern sourceNamePattern;
    private final Path targetDir;
    private final String targetName;

    public MoveAction(@NonNull Path baseTragetPath,
                      @NonNull String sourceNamePattern,
                      @NonNull Path targetDir,
                      @NonNull String targetName) {
        this.baseTragetPath = baseTragetPath;
        this.sourceNamePattern = Pattern.compile(sourceNamePattern);
        this.targetDir = targetDir;
        this.targetName = targetName;
    }

    @SneakyThrows
    @Override
    public void accept(HandleContext handleContext) {
        Path sourceRelativePath = handleContext.getRelativePath();

        Matcher matcher = sourceNamePattern.matcher(sourceRelativePath.getFileName().toString());
        if (!matcher.find()) {
            throw new IllegalArgumentException("file name does not match pattern: " + sourceRelativePath.getFileName());
        }
        String targetFileName = matcher.replaceAll(targetName);

        Path sourcePathParent = sourceRelativePath.getParent();

        Path targetPath = baseTragetPath.resolve(sourcePathParent.resolve(targetDir).resolve(targetFileName)).normalize();

        Path targetPathParent = targetPath.getParent();

        if (!Files.isDirectory(targetPathParent)) {
            Files.createDirectories(targetPathParent);
        }

        Path sourcePath = handleContext.getAbsolutePath();
        log.info("[{}] moving {} to {}", handleContext.getHandlerName(), sourcePath, targetPath);
        Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
    }

}
