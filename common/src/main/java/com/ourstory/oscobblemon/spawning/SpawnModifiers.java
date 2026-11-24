package com.ourstory.oscobblemon.spawning;

import com.ourstory.oscobblemon.internal.SpawnModifierRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.Optional;

/** Server-local entry point for Cobblemon natural spawn-weight modifiers. */
public final class SpawnModifiers {
    private SpawnModifiers() {
    }

    public static SpawnModifierCapabilities capabilities() {
        return SpawnModifierRuntime.capabilities();
    }

    /** Registers a new modifier. An existing ID is an error; use {@link #replace} deliberately. */
    public static SpawnModifierHandle register(MinecraftServer server, SpawnModifier modifier) {
        return SpawnModifierRuntime.register(Objects.requireNonNull(server, "server"),
                Objects.requireNonNull(modifier, "modifier"), false);
    }

    /** Replaces the definition with the same ID and returns a handle for the new generation. */
    public static SpawnModifierHandle replace(MinecraftServer server, SpawnModifier modifier) {
        return SpawnModifierRuntime.register(Objects.requireNonNull(server, "server"),
                Objects.requireNonNull(modifier, "modifier"), true);
    }

    public static Optional<SpawnModifier> find(MinecraftServer server, ResourceLocation id) {
        return SpawnModifierRuntime.find(Objects.requireNonNull(server, "server"), Objects.requireNonNull(id, "id"));
    }

    public static SpawnModifierSnapshot snapshot(MinecraftServer server) {
        return SpawnModifierRuntime.snapshot(Objects.requireNonNull(server, "server"));
    }

    public static boolean remove(MinecraftServer server, ResourceLocation id) {
        return SpawnModifierRuntime.remove(Objects.requireNonNull(server, "server"), Objects.requireNonNull(id, "id"));
    }

    public static int removeSource(MinecraftServer server, ResourceLocation source) {
        return SpawnModifierRuntime.removeSource(Objects.requireNonNull(server, "server"),
                Objects.requireNonNull(source, "source"));
    }
}
