package com.github.alexeylapin.photorelay.impl;

import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.Handler;
import lombok.NonNull;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DefaultHandler implements Handler {

    private final int order;
    private final String name;
    private final Predicate<HandleContext> predicate;
    private final Consumer<HandleContext> action;

    public DefaultHandler(int order,
                          @NonNull String name,
                          @NonNull Predicate<HandleContext> predicate,
                          @NonNull Consumer<HandleContext> action) {
        this.order = order;
        this.name = name;
        this.predicate = predicate;
        this.action = action;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEligible(HandleContext handleContext) throws IOException {
        return predicate.test(handleContext);
    }

    @Override
    public void handle(HandleContext handleContext) throws IOException {
        action.accept(handleContext);
    }

}
