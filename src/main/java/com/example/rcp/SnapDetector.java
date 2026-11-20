package com.example.rcp;

import com.g4vrk.react.api.rcp.environment.RCPEnvironment;
import com.g4vrk.react.api.rcp.player.RCPPlayer;
import com.g4vrk.react.api.rcp.player.RotationSample;
import com.g4vrk.react.api.rcp.verdict.InferenceResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A deliberately simple model: counts fast yaw snaps in a row and turns the count into a
 * probability. Real RCPs replace this with their own model or a call to their backend.
 */
public final class SnapDetector {

    public static final float SNAP_DEGREES = 40.0F;
    private static final int WINDOW = 20;

    private final Map<UUID, Window> windows = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock resetLock = new ReentrantReadWriteLock();

    /**
     * Records a rotation and publishes a completed window before any concurrent reset.
     */
    public void record(
            final @NotNull RCPEnvironment environment,
            final @NotNull RCPPlayer player,
            final float @NotNull [] sample
    ) {

        resetLock.readLock().lock();
        try {
            final Window window = windows.computeIfAbsent(player.uniqueId(), ignored -> new Window());
            synchronized (window) {
                if (Math.abs(RotationSample.deltaYaw(sample, 0)) >= SNAP_DEGREES) {
                    window.snaps++;
                }
                if (++window.seen < WINDOW) {
                    return;
                }
                final double probability = Math.min(1.0D, window.snaps / 5.0D);
                window.seen = 0;
                window.snaps = 0;
                environment.fire(player, new InferenceResult("example/snap", "Example Snap", probability, 0.8D, false));
            }
        } finally {
            resetLock.readLock().unlock();
        }

    }

    public int tracked() {

        return windows.size();

    }

    public void forget(final @NotNull UUID player) {

        resetLock.writeLock().lock();
        try {
            windows.remove(player);
        } finally {
            resetLock.writeLock().unlock();
        }

    }

    public void clear() {

        resetLock.writeLock().lock();
        try {
            windows.clear();
        } finally {
            resetLock.writeLock().unlock();
        }

    }

    /**
     * Mutable counters guarded by this window's monitor.
     */
    private static final class Window {

        private int seen;
        private int snaps;

    }

}
