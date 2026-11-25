package com.ourstory.oscobblemon.spawning;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.UUID;

/** Defines which natural spawning attempts may use a modifier. */
public final class SpawnModifierScope {
    public enum Kind {
        GLOBAL,
        DIMENSION,
        PLAYER
    }

    private static final SpawnModifierScope GLOBAL = new SpawnModifierScope(Kind.GLOBAL, null, null);

    private final Kind kind;
    private final ResourceLocation dimension;
    private final UUID playerId;

    private SpawnModifierScope(Kind kind, ResourceLocation dimension, UUID playerId) {
        this.kind = kind;
        this.dimension = dimension;
        this.playerId = playerId;
    }

    public static SpawnModifierScope global() {
        return GLOBAL;
    }

    public static SpawnModifierScope dimension(ResourceLocation dimension) {
        return new SpawnModifierScope(Kind.DIMENSION, Objects.requireNonNull(dimension, "dimension"), null);
    }

    public static SpawnModifierScope player(UUID playerId) {
        return new SpawnModifierScope(Kind.PLAYER, null, Objects.requireNonNull(playerId, "playerId"));
    }

    public Kind kind() {
        return kind;
    }

    public ResourceLocation dimension() {
        return dimension;
    }

    public UUID playerId() {
        return playerId;
    }

    /** Returns whether this scope accepts a context with the supplied dimension and player UUID. */
    public boolean matches(ResourceLocation attemptDimension, UUID attemptPlayer) {
        return switch (kind) {
            case GLOBAL -> true;
            case DIMENSION -> dimension.equals(attemptDimension);
            case PLAYER -> playerId.equals(attemptPlayer);
        };
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SpawnModifierScope that)) return false;
        return kind == that.kind && Objects.equals(dimension, that.dimension) && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, dimension, playerId);
    }
}
