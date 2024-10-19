package com.github.alexeylapin.photorelay.config.def.factory;

import com.drew.metadata.Directory;
import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.condition.MetadataTagPredicate;
import com.github.alexeylapin.photorelay.config.def.ConditionFactory;
import com.github.alexeylapin.photorelay.config.def.HandlerDef;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.function.Predicate;

public class MetadataConditionFactory implements ConditionFactory {

    @Override
    public boolean supports(String type) {
        return "metadata".equals(type);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Predicate<HandleContext> create(HandlerDef.ConditionDef conditionDef) {
        Map<String, String> with = conditionDef.getWith();
        if (with == null) {
            throw new IllegalArgumentException("with is required");
        }

        String directoryName = with.get("tag-group-name");

        Class<? extends Directory> directoryClass = null;
        String directoryClassValue = with.get("tag-group-class");
        if (directoryClassValue != null) {
            directoryClass = (Class<? extends Directory>) Class.forName(directoryClassValue);
        }

        Integer tagId = null;
        String tagIdValue = with.get("tag-id");
        if (tagIdValue != null) {
            tagId = Integer.decode(tagIdValue);
        }

        String pattern = with.get("tag-value-pattern");

        return new MetadataTagPredicate(directoryName, directoryClass, null, tagId, pattern);
    }

}
