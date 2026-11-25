package com.ourstory.oscobblemon.internal;

import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnModifierSnapshot;
import com.ourstory.oscobblemon.spawning.SpawnStackingPolicy;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** One server's mutable registration state. Methods accept the server's tick counter for deterministic expiry. */
final class SpawnModifierRegistry {
    private static final SpawnModifierSnapshot EMPTY = new SpawnModifierSnapshot(List.of());

    private final Map<ResourceLocation, ActiveRegistration> entries = new LinkedHashMap<>();
    private long nextGeneration;
    private int lastRawTick;
    private long logicalTick;
    private boolean clockInitialized;
    private SpawnModifierSnapshot snapshot = EMPTY;

    synchronized long register(int rawTick, SpawnModifier modifier, boolean replace) {
        if (modifier == null) throw new NullPointerException("modifier");
        advance(rawTick);
        ActiveRegistration old = entries.get(modifier.id());
        if (old != null && !replace) {
            throw new IllegalStateException("Spawn modifier ID is already registered: " + modifier.id());
        }
        if (old == null && replace) {
            throw new IllegalStateException("Cannot replace an unregistered spawn modifier ID: " + modifier.id());
        }
        validateGroupPolicies(modifier, old);
        long generation = ++nextGeneration;
        long expiresAtTick = modifier.durationTicks() == 0
                ? Long.MAX_VALUE
                : saturatingAdd(logicalTick, modifier.durationTicks());
        entries.put(modifier.id(), new ActiveRegistration(modifier, generation, expiresAtTick));
        rebuildSnapshot();
        return generation;
    }

    synchronized boolean remove(int rawTick, ResourceLocation id) {
        advance(rawTick);
        boolean removed = entries.remove(id) != null;
        if (removed) rebuildSnapshot();
        return removed;
    }

    synchronized int removeSource(int rawTick, ResourceLocation source) {
        advance(rawTick);
        int before = entries.size();
        entries.values().removeIf(entry -> entry.modifier.source().equals(source));
        int removed = before - entries.size();
        if (removed > 0) rebuildSnapshot();
        return removed;
    }

    synchronized Optional<SpawnModifier> find(int rawTick, ResourceLocation id) {
        advance(rawTick);
        ActiveRegistration entry = entries.get(id);
        return entry == null ? Optional.empty() : Optional.of(entry.modifier);
    }

    synchronized SpawnModifierSnapshot snapshot(int rawTick) {
        advance(rawTick);
        return snapshot;
    }

    synchronized boolean isActive(int rawTick, ResourceLocation id, long generation) {
        advance(rawTick);
        ActiveRegistration entry = entries.get(id);
        return entry != null && entry.generation == generation;
    }

    synchronized boolean remove(int rawTick, ResourceLocation id, long generation) {
        advance(rawTick);
        ActiveRegistration entry = entries.get(id);
        if (entry == null || entry.generation != generation) return false;
        entries.remove(id);
        rebuildSnapshot();
        return true;
    }

    synchronized void clear() {
        entries.clear();
        snapshot = EMPTY;
    }

    private void advance(int rawTick) {
        if (!clockInitialized) {
            clockInitialized = true;
            lastRawTick = rawTick;
            return;
        }
        logicalTick += Integer.toUnsignedLong(rawTick - lastRawTick);
        lastRawTick = rawTick;
        if (entries.values().removeIf(entry -> entry.expiresAtTick <= logicalTick)) rebuildSnapshot();
    }

    private void validateGroupPolicies(SpawnModifier modifier, ActiveRegistration replaced) {
        SpawnStackingPolicy policy = modifier.stackingPolicy();
        for (ActiveRegistration entry : entries.values()) {
            if (entry == replaced) continue;
            SpawnStackingPolicy existing = entry.modifier.stackingPolicy();
            if (existing.group().equals(policy.group()) && !existing.equals(policy)) {
                throw new IllegalStateException("Stacking group " + policy.group()
                        + " already uses a different policy; remove its modifiers before changing policy");
            }
        }
    }

    private void rebuildSnapshot() {
        List<SpawnModifier> modifiers = entries.values().stream()
                .map(entry -> entry.modifier)
                .sorted(Comparator.comparing(modifier -> modifier.id().toString()))
                .toList();
        snapshot = new SpawnModifierSnapshot(modifiers);
    }

    private static long saturatingAdd(long left, long right) {
        if (Long.MAX_VALUE - left < right) return Long.MAX_VALUE;
        return left + right;
    }

    private static final class ActiveRegistration {
        private final SpawnModifier modifier;
        private final long generation;
        private final long expiresAtTick;

        private ActiveRegistration(SpawnModifier modifier, long generation, long expiresAtTick) {
            this.modifier = modifier;
            this.generation = generation;
            this.expiresAtTick = expiresAtTick;
        }
    }
}
