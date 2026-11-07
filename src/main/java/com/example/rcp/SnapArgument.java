package com.example.rcp;

import com.g4vrk.functionalActions.list.ExecutableActionList;
import com.g4vrk.functionalActions.parser.ActionParser;
import com.g4vrk.react.api.rcp.argument.RCPArgument;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /react snap}: needs the {@code react.command.snap} permission, which the plugin checks.
 */
final class SnapArgument implements RCPArgument {

    private final SnapDetector detector;

    private ExecutableActionList<? super Audience> tracking;
    private ExecutableActionList<? super Audience> cleared;

    SnapArgument(final @NotNull SnapDetector detector) {
        this.detector = detector;
    }

    void reload(final @NotNull ActionParser<Audience> parser, final @NotNull ExampleActions actions) {
        this.tracking = parser.parseAll(actions.tracking());
        this.cleared = parser.parseAll(actions.cleared());
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull List<String> args) {
        if (!args.isEmpty() && args.get(0).equalsIgnoreCase("clear")) {
            detector.clear();
            cleared.run(sender);
            return;
        }
        final String players = Integer.toString(detector.tracked());
        tracking.run(sender, text -> text.replace("{players}", players));
    }

    @Override
    public @NotNull List<String> suggest(final @NotNull CommandSender sender, final @NotNull List<String> args) {
        return args.size() <= 1 ? List.of("clear") : List.of();
    }

}
