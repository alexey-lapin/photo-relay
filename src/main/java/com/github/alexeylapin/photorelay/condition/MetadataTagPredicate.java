package com.github.alexeylapin.photorelay.condition;

import com.drew.metadata.Directory;
import com.github.alexeylapin.photorelay.HandleContext;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MetadataTagPredicate implements Predicate<HandleContext> {

    private final String directoryName;
    private final Class<? extends Directory> directoryClass;
    private final String tagName;
    private final Integer tagId;
    private final Pattern pattern;

    public MetadataTagPredicate(String directoryName,
                                Class<? extends Directory> directoryClass,
                                String tagName,
                                Integer tagId,
                                String pattern) {
        this.directoryName = directoryName;
        this.directoryClass = directoryClass;
        this.tagName = tagName;
        this.tagId = tagId;
        this.pattern = pattern == null ? null : Pattern.compile(pattern);
    }

    @Override
    public boolean test(HandleContext handleContext) {
        Iterable<? extends Directory> directories;
        if (directoryClass != null) {
            directories = handleContext.getMetadata().getDirectoriesOfType(directoryClass);
        } else {
            directories = handleContext.getMetadata().getDirectories();
        }

        String value = null;
        for (Directory directory : directories) {
            if (directoryName == null || directoryName.equalsIgnoreCase(directory.getName())) {
                String directoryValue = directory.getDescription(tagId);
                if (directoryValue != null) {
                    value = directoryValue;
                    break;
                }
            }
        }

        if (pattern == null) {
            return value == null;
        }

        if (value == null) {
            return false;
        }

        return pattern.matcher(value).matches();
    }

}
