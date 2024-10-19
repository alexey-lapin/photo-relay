package com.github.alexeylapin.photorelay.config.def.factory;

import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.config.def.ActionFactory;
import com.github.alexeylapin.photorelay.config.def.HandlerDef;
import lombok.NonNull;

import java.util.Collection;
import java.util.function.Consumer;

public class CompositeActionFactory implements ActionFactory {

    private final Collection<ActionFactory> delegates;

    public CompositeActionFactory(@NonNull Collection<ActionFactory> delegates) {
        this.delegates = delegates;
    }

    @Override
    public boolean supports(String type) {
        return true;
    }

    @Override
    public Consumer<HandleContext> create(HandlerDef.ActionDef actionDef) {
        for (ActionFactory delegate : delegates) {
            if (delegate.supports(actionDef.getType())) {
                return delegate.create(actionDef);
            }
        }
        throw new IllegalArgumentException("unsupported action type: " + actionDef.getType());
    }

}
