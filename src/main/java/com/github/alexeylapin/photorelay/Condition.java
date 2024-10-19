package com.github.alexeylapin.photorelay;

import java.util.function.Predicate;

public interface Condition {

    Predicate<HandleContext> toPredicate();

}
