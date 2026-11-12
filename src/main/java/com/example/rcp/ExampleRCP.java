package com.example.rcp;

import com.g4vrk.react.api.rcp.environment.RCPEnvironment;
import com.g4vrk.react.api.rcp.provider.RCPProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

/**
 * Entry point named by {@code mainClass} in {@code rcp-metadata.yml}.
 *
 * <p>The plugin builds it with the environment of this RCP and calls {@link #onEnable()}
 * once the server is ready. Everything registered through the environment is removed by
 * the plugin on disable, so {@link #onDisable()} has nothing to undo here.</p>
 */
public final class ExampleRCP extends RCPProvider {

    private final SnapDetector detector = new SnapDetector();
    private final SnapCommand snap = new SnapCommand(detector);

    public ExampleRCP(final @NotNull RCPEnvironment environment) {

        super(environment);

    }

    @Override
    public void onEnable() throws ConfigurateException {

        final RCPEnvironment environment = singletonEnvironment();
        environment.listen(new SnapListener(environment, detector));
        snap.register(environment.commands());
        applyActions(environment);
        environment.logger().info("Example RCP is watching for aim snaps");

    }

    @Override
    public void onReload() throws ConfigurateException {

        detector.clear();
        applyActions(singletonEnvironment());

    }

    private void applyActions(final @NotNull RCPEnvironment environment) throws ConfigurateException {

        final ExampleActions actions = ExampleActions.load(environment.directory(), environment.logger());
        snap.reload(environment.actions(), actions);
        environment.status(actions.status().replace("{degrees}", Float.toString(SnapDetector.SNAP_DEGREES)));

    }

}
