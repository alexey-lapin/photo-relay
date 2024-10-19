package com.github.alexeylapin.photorelay.config.def;

import com.github.alexeylapin.photorelay.HandleContext;

import java.util.function.Predicate;

public interface ConditionFactory {

    boolean supports(String type);

    Predicate<HandleContext> create(HandlerDef.ConditionDef conditionDef);

}
