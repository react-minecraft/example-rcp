package com.example.rcp;

import com.g4vrk.functionalActions.list.ExecutableActionList;
import com.g4vrk.functionalActions.parser.ActionParser;
import com.g4vrk.react.api.extension.command.CommandRegistrar;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /react snap [clear]} requires {@code react.command.snap}.
 *
 * <p>The command is a plain Cloud command; the plugin removes it when the RCP is disabled.</p>
 */
final class SnapCommand {

    private static final String ROOT = "react";

    private final SnapDetector detector;

    private ExecutableActionList<? super Audience> tracking;
    private ExecutableActionList<? super Audience> cleared;

    SnapCommand(final @NotNull SnapDetector detector) {

        this.detector = detector;

    }

    void reload(final @NotNull ActionParser<Audience> parser, final @NotNull ExampleActions actions) {

        this.tracking = parser.parseAll(actions.tracking());
        this.cleared = parser.parseAll(actions.cleared());

    }

    void register(final @NotNull CommandRegistrar commands) {

        final Command.Builder<CommandSender> snap = commands.command(ROOT).literal("snap").permission("react.command.snap");
        commands.register(ROOT, snap.handler(context -> showTracking(context.sender())));
        commands.register(ROOT, snap.literal("clear").handler(context -> clear(context.sender())));

    }

    private void clear(final @NotNull CommandSender sender) {

        detector.clear();
        cleared.run(sender);

    }

    private void showTracking(final @NotNull CommandSender sender) {

        final String players = Integer.toString(detector.tracked());
        tracking.run(sender, text -> text.replace("{players}", players));

    }

}
