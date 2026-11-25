package com.ourstory.oscobblemon.spawning;

import java.util.List;

/** Immutable view of modifiers currently registered on one server. */
public record SpawnModifierSnapshot(List<SpawnModifier> modifiers) {
    public SpawnModifierSnapshot {
        modifiers = List.copyOf(modifiers);
    }
}
