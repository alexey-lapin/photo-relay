package com.github.alexeylapin.photorelay.config.def.factory;

import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.config.def.ConditionFactory;
import com.github.alexeylapin.photorelay.config.def.HandlerDef;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class CompositeConditionFactory implements ConditionFactory {

    private final Collection<ConditionFactory> delegates;

    public CompositeConditionFactory(@NonNull Collection<ConditionFactory> delegates) {
        this.delegates = delegates;
    }

    @Override
    public boolean supports(String type) {
        return true;
    }

    @Override
    public Predicate<HandleContext> create(HandlerDef.ConditionDef conditionDef) {
        List<HandlerDef.ConditionDef> andList = conditionDef.getAnd();
        List<HandlerDef.ConditionDef> orList = conditionDef.getOr();
        HandlerDef.ConditionDef not = conditionDef.getNot();

        if (andList != null && !andList.isEmpty()) {
            if (andList.size() > 1) {
                Predicate<HandleContext> predicate = create(andList.get(0));
                for (int i = 1; i < andList.size(); i++) {
                    predicate = predicate.and(create(andList.get(i)));
                }
                return predicate;
            } else {
                return create(conditionDef);
            }
        } else if (orList != null && !orList.isEmpty()) {
            if (orList.size() > 1) {
                Predicate<HandleContext> predicate = create(orList.get(0));
                for (int i = 1; i < orList.size(); i++) {
                    predicate = predicate.or(create(orList.get(i)));
                }
                return predicate;
            } else {
                return create(conditionDef);
            }
        } else if (not != null) {
            return create(not).negate();
        } else {
            return byType(conditionDef);
        }
    }

    private Predicate<HandleContext> byType(HandlerDef.ConditionDef conditionDef) {
        String type = conditionDef.getType();
        if ("always".equals(type)) {
            return context -> true;
        } else if ("never".equals(type)) {
            return context -> false;
        }

        for (ConditionFactory delegate : delegates) {
            if (delegate.supports(type)) {
                return delegate.create(conditionDef);
            }
        }
        throw new IllegalArgumentException("unsupported condition type: " + type);
    }

}
