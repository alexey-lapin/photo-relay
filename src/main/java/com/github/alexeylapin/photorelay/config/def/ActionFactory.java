package com.github.alexeylapin.photorelay.config.def;

import com.github.alexeylapin.photorelay.HandleContext;

import java.util.function.Consumer;

public interface ActionFactory {

    boolean supports(String type);

    Consumer<HandleContext> create(HandlerDef.ActionDef actionDef);

}
