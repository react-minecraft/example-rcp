package com.example.rcp;

import com.g4vrk.react.api.rcp.environment.RCPEnvironment;
import com.g4vrk.react.api.rcp.event.RCPPlayerListener;
import com.g4vrk.react.api.rcp.player.RCPPlayer;
import com.g4vrk.react.api.rcp.player.RotationSample;
import com.g4vrk.react.api.rcp.verdict.InferenceResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Feeds rotations of players in combat to the detector and hands every finished window to
 * the plugin's check.
 *
 * <p>{@link #onRotation} runs on a netty thread: it only does cheap, non-blocking work and
 * never touches the world.</p>
 */
final class SnapListener implements RCPPlayerListener {

    private final RCPEnvironment environment;
    private final SnapDetector detector;

    SnapListener(final @NotNull RCPEnvironment environment, final @NotNull SnapDetector detector) {
        this.environment = environment;
        this.detector = detector;
    }

    @Override
    public void onRotation(final @NotNull RCPPlayer player, final float @NotNull [] sample) {
        if (!player.inCombat()) {
            return;
        }
        final double probability = detector.record(player.uniqueId(), sample);
        if (probability < 0.0D) {
            return;
        }
        // enforcement=false keeps the example observe-only: results are shown and stored, never punished.
        environment.fire(player, new InferenceResult("example/snap", "Example Snap", probability, 0.8D, false));
    }

    @Override
    public void onReset(final @NotNull UUID player) {
        detector.forget(player);
    }

}
