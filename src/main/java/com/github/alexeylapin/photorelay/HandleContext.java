package com.github.alexeylapin.photorelay;

import com.drew.metadata.Metadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@RequiredArgsConstructor
@Getter
public class HandleContext {

    private final Path absolutePath;
    private final Path relativePath;
    private final BasicFileAttributes attrs;
    private final Metadata metadata;
    @With
    private final String handlerName;

}
