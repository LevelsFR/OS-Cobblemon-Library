package com.ourstory.oscobblemon.spawning;

import net.minecraft.resources.ResourceLocation;

/** Idempotent, generation-safe reference to one server registration. */
public interface SpawnModifierHandle {
    ResourceLocation id();

    boolean isActive();

    /** Removes this exact registration. A stale handle cannot remove its replacement. */
    boolean remove();
}
