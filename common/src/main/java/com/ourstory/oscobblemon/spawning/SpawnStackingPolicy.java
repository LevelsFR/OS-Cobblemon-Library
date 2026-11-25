package com.ourstory.oscobblemon.spawning;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Deterministic combination rule for modifiers that share a group. */
public final class SpawnStackingPolicy {
    public static final double MAX_TECHNICAL_CAP = SpawnModifier.MAX_MULTIPLIER;

    public enum Mode {
        MAX,
        MULTIPLICATIVE_CAPPED
    }

    private final ResourceLocation group;
    private final Mode mode;
    private final double maximumMultiplier;

    private SpawnStackingPolicy(ResourceLocation group, Mode mode, double maximumMultiplier) {
        this.group = Objects.requireNonNull(group, "group");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.maximumMultiplier = maximumMultiplier;
    }

    /** Uses the largest boost and strongest reduction in this group, then combines them. */
    public static SpawnStackingPolicy max(ResourceLocation group) {
        return new SpawnStackingPolicy(group, Mode.MAX, 0.0);
    }

    /** Multiplies all matching values in this group and caps only the positive result. */
    public static SpawnStackingPolicy multiplicativeCapped(ResourceLocation group, double maximumMultiplier) {
        if (!Double.isFinite(maximumMultiplier) || maximumMultiplier < 1.0
                || maximumMultiplier > MAX_TECHNICAL_CAP) {
            throw new IllegalArgumentException("maximumMultiplier must be finite and in [1.0, 1e6]");
        }
        return new SpawnStackingPolicy(group, Mode.MULTIPLICATIVE_CAPPED, maximumMultiplier);
    }

    public ResourceLocation group() {
        return group;
    }

    public Mode mode() {
        return mode;
    }

    /**
     * Returns the positive-result cap for {@link Mode#MULTIPLICATIVE_CAPPED},
     * or zero when this policy has no cap.
     */
    public double maximumMultiplier() {
        return maximumMultiplier;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SpawnStackingPolicy that)) return false;
        return Double.compare(maximumMultiplier, that.maximumMultiplier) == 0
                && group.equals(that.group) && mode == that.mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, mode, maximumMultiplier);
    }
}
