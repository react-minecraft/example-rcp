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

    private volatile Actions actions;

    SnapCommand(final @NotNull SnapDetector detector) {

        this.detector = detector;

    }

    void reload(final @NotNull ActionParser<Audience> parser, final @NotNull ExampleActions actions) {

        this.actions = new Actions(parser.parseAll(actions.tracking()), parser.parseAll(actions.cleared()));

    }

    void register(final @NotNull CommandRegistrar commands) {

        final Command.Builder<CommandSender> snap = commands.command(ROOT).literal("snap").permission("react.command.snap");
        commands.register(ROOT, snap.handler(context -> showTracking(context.sender())));
        commands.register(ROOT, snap.literal("clear").handler(context -> clear(context.sender())));

    }

    private void clear(final @NotNull CommandSender sender) {

        detector.clear();
        actions.cleared().run(sender);

    }

    private void showTracking(final @NotNull CommandSender sender) {

        final String players = Integer.toString(detector.tracked());
        actions.tracking().run(sender, text -> text.replace("{players}", players));

    }

    /**
     * A complete, atomically published set of command actions.
     */
    private record Actions(
            ExecutableActionList<? super Audience> tracking,
            ExecutableActionList<? super Audience> cleared
    ) {
    }

}
