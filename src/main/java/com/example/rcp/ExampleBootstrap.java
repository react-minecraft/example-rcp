package com.example.rcp;

import com.g4vrk.react.api.extension.ExtensionId;
import com.g4vrk.react.api.extension.bootstrap.ExtensionBootstrap;
import com.g4vrk.react.api.extension.bootstrap.ExtensionBootstrapContext;
import org.jetbrains.annotations.NotNull;

/**
 * Declares the identity of the example provider independently of its display name.
 */
public final class ExampleBootstrap implements ExtensionBootstrap {

    @Override
    public void bootstrap(final @NotNull ExtensionBootstrapContext context) {

        context.identify(ExtensionId.rcp("example"), 1);

    }

}
