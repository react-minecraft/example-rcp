package com.example.rcp;

import com.g4vrk.react.api.config.ConfigFile;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * The example's {@code actions.yml} configures {@code /react snap} and its status in {@code /react rcps}.
 * <p>
 * The plugin copies the file from the jar on first start and fills keys a newer jar adds.
 */
@ConfigSerializable
public record ExampleActions(String status, List<String> tracking, List<String> cleared) {

    public static @NotNull ExampleActions load(
            final @NotNull Path directory,
            final @NotNull Logger logger
    ) throws ConfigurateException {

        final String language = "en".equalsIgnoreCase(System.getProperty("user.language")) ? "en" : "ru";
        return ConfigFile.builder(ExampleActions.class)
                .path(directory.resolve("actions.yml"))
                .defaults(ExampleActions.class.getClassLoader(), language + "/actions.yml")
                .logger(logger)
                .build()
                .load();

    }

}
