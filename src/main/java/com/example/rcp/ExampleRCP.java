package com.example.rcp;

import com.g4vrk.functionalActions.defaults.DefaultActions;
import com.g4vrk.functionalActions.parser.ActionParser;
import com.g4vrk.functionalActions.parser.impl.SimpleActionParser;
import com.g4vrk.functionalActions.registry.ActionRegistry;
import com.g4vrk.functionalActions.registry.impl.SimpleActionRegistry;
import com.g4vrk.react.api.rcp.environment.RCPEnvironment;
import com.g4vrk.react.api.rcp.provider.RCPProvider;
import com.g4vrk.text.LegacySerializers;
import net.kyori.adventure.audience.Audience;
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
    private final SnapArgument snap = new SnapArgument(detector);
    private final ActionParser<Audience> parser;

    public ExampleRCP(final @NotNull RCPEnvironment environment) {
        super(environment);
        final ActionRegistry<Audience> registry = new SimpleActionRegistry<>(true);
        DefaultActions.Adventure.registerDefaults(
                registry, LegacySerializers.INSTANCE.universalSerializer()::serialize, ";");
        this.parser = new SimpleActionParser<>(registry);
    }

    @Override
    public void onEnable() throws ConfigurateException {
        final RCPEnvironment environment = singletonEnvironment();
        environment.listen(new SnapListener(environment, detector));
        environment.registerArgument("snap", snap);
        applyActions(environment);
        environment.logger().info("Example RCP is watching for aim snaps");
    }

    @Override
    public void onReload() throws ConfigurateException {
        detector.clear();
        applyActions(singletonEnvironment());
    }

    private void applyActions(final RCPEnvironment environment) throws ConfigurateException {
        final ExampleActions actions = ExampleActions.load(environment.directory(), environment.logger());
        snap.reload(parser, actions);
        environment.status(actions.status().replace("{degrees}", Float.toString(SnapDetector.SNAP_DEGREES)));
    }

}
