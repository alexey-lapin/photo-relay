package com.github.alexeylapin.photorelay;

import java.io.IOException;

public interface Handler {

    int getOrder();

    default String getName() {
        return null;
    }

    boolean isEligible(HandleContext handleContext) throws IOException;

    void handle(HandleContext handleContext) throws IOException;

}
