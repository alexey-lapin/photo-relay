package com.github.alexeylapin.photorelay.config.def.factory;

import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.action.MoveAction;
import com.github.alexeylapin.photorelay.config.def.ActionFactory;
import com.github.alexeylapin.photorelay.config.def.HandlerDef;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class FilesActionFactory implements ActionFactory {

    @Override
    public boolean supports(String type) {
        return "move".equals(type);
    }

    @Override
    public Consumer<HandleContext> create(HandlerDef.ActionDef actionDef) {
        Map<String, String> with = actionDef.getWith();
        if (with == null) {
            throw new IllegalArgumentException("with is required");
        }

        String baseTargetPathValue = with.get("base-target-dir");
        if (baseTargetPathValue == null) {
            throw new IllegalArgumentException("base-target-dir is required");
        }
        Path baseTargetPath = Paths.get(baseTargetPathValue);

        String sourceNamePattern = with.getOrDefault("source-name-pattern", "(.*)");
        Path targetDir = Paths.get(with.getOrDefault("target-dir", "."));
        String targetName = with.getOrDefault("target-name", "$1");

        return new MoveAction(baseTargetPath, sourceNamePattern, targetDir, targetName);
    }

}
