package com.example.rcp;

import com.g4vrk.react.api.rcp.command.RCPCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@code /react snap}: needs the {@code react.command.snap} permission, which the plugin checks.
 */
final class SnapCommand implements RCPCommand {

    private final SnapDetector detector;

    SnapCommand(final @NotNull SnapDetector detector) {
        this.detector = detector;
    }

    @Override
    public @NotNull String name() {
        return "snap";
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull List<String> args) {
        if (!args.isEmpty() && args.get(0).equalsIgnoreCase("clear")) {
            detector.clear();
            sender.sendMessage("Example RCP: windows cleared");
            return;
        }
        sender.sendMessage("Example RCP: tracking " + detector.tracked() + " players");
    }

    @Override
    public @NotNull List<String> suggest(final @NotNull CommandSender sender, final @NotNull List<String> args) {
        return args.size() <= 1 ? List.of("clear") : List.of();
    }

}
