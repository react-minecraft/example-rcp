package com.example.rcp;

import com.g4vrk.react.api.rcp.environment.RCPEnvironment;
import com.g4vrk.react.api.rcp.provider.RCPProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point named by {@code mainClass} in {@code rcp-metadata.yml}.
 *
 * <p>The plugin builds it with the environment of this RCP and calls {@link #onEnable()}
 * once the server is ready. Everything registered through the environment is removed by
 * the plugin on disable, so {@link #onDisable()} has nothing to undo here.</p>
 */
public final class ExampleRCP extends RCPProvider {

    private final SnapDetector detector = new SnapDetector();

    public ExampleRCP(final @NotNull RCPEnvironment environment) {
        super(environment);
    }

    @Override
    public void onEnable() {
        final RCPEnvironment environment = singletonEnvironment();
        environment.listen(new SnapListener(environment, detector));
        environment.registerCommand(new SnapCommand(detector));
        environment.status("Snap threshold " + SnapDetector.SNAP_DEGREES + "°");
        environment.logger().info("Example RCP is watching for aim snaps");
    }

    @Override
    public void onReload() {
        detector.clear();
    }

}
