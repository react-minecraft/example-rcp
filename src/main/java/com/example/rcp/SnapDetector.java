package com.example.rcp;

import com.g4vrk.react.api.rcp.player.RotationSample;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A deliberately simple model: counts fast yaw snaps in a row and turns the count into a
 * probability. Real RCPs replace this with their own model or a call to their backend.
 */
public final class SnapDetector {

    public static final float SNAP_DEGREES = 40.0F;
    private static final int WINDOW = 20;

    private final Map<UUID, Window> windows = new ConcurrentHashMap<>();

    /**
     * Records one rotation of the player.
     *
     * @return the probability of cheating once a full window was seen, or -1 before that
     */
    public double record(final @NotNull UUID player, final float @NotNull [] sample) {
        final Window window = windows.computeIfAbsent(player, ignored -> new Window());
        synchronized (window) {
            if (Math.abs(RotationSample.deltaYaw(sample, 0)) >= SNAP_DEGREES) {
                window.snaps++;
            }
            if (++window.seen < WINDOW) {
                return -1.0D;
            }
            final double probability = Math.min(1.0D, window.snaps / 5.0D);
            window.seen = 0;
            window.snaps = 0;
            return probability;
        }
    }

    public int tracked() {
        return windows.size();
    }

    public void forget(final @NotNull UUID player) {
        windows.remove(player);
    }

    public void clear() {
        windows.clear();
    }

    private static final class Window {
        private int seen;
        private int snaps;
    }

}
