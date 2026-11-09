package com.example.rcp;

import com.g4vrk.functionalActions.list.ExecutableActionList;
import com.g4vrk.functionalActions.parser.ActionParser;
import com.g4vrk.react.api.extension.command.CommandRegistrar;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /snap [clear]}: needs the {@code react.command.snap} permission, which the plugin sets.
 *
 * <p>The command is a plain Cloud command; the plugin removes it when the RCP is disabled.</p>
 */
final class SnapCommand {

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
        commands.register(
                "snap",
                commands.command("snap")
                        .optional("action", StringParser.stringParser(), SuggestionProvider.suggestingStrings("clear"))
                        .handler(context -> execute(context.sender(), context.<String>optional("action").orElse("")))
        );
    }

    private void execute(final @NotNull CommandSender sender, final @NotNull String action) {
        if (action.equalsIgnoreCase("clear")) {
            detector.clear();
            cleared.run(sender);
            return;
        }
        final String players = Integer.toString(detector.tracked());
        tracking.run(sender, text -> text.replace("{players}", players));
    }

}
