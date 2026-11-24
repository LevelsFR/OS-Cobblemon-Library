package com.ourstory.oscobblemon.internal;

import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnModifierSnapshot;
import com.ourstory.oscobblemon.spawning.SpawnStackingPolicy;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnModifierRegistryTest {
    @Test
    void duplicateIdsRequireExplicitReplaceAndStaleGenerationCannotRemoveReplacement() {
        SpawnModifierRegistry registry = new SpawnModifierRegistry();
        SpawnModifier first = modifier("example:event", "example:source", 2.0, null, 0);
        long firstGeneration = registry.register(10, first, false);

        assertThrows(IllegalStateException.class, () -> registry.register(10, first, false));
        assertThrows(IllegalStateException.class,
                () -> registry.register(10, modifier("example:missing", "example:source", 1.0, null, 0), true));

        SpawnModifier replacement = modifier("example:event", "example:source", 1.5, null, 0);
        long replacementGeneration = registry.register(10, replacement, true);
        assertFalse(registry.isActive(10, first.id(), firstGeneration));
        assertFalse(registry.remove(10, first.id(), firstGeneration));
        assertTrue(registry.isActive(10, replacement.id(), replacementGeneration));
        assertEquals(1.5, registry.find(10, replacement.id()).orElseThrow().multiplier());
    }

    @Test
    void expirationUsesElapsedServerTicksAndHandlesCounterWrap() {
        SpawnModifierRegistry registry = new SpawnModifierRegistry();
        SpawnModifier timed = modifier("example:timed", "example:source", 1.25, null, 50);
        long generation = registry.register(100, timed, false);

        assertTrue(registry.isActive(149, timed.id(), generation));
        assertFalse(registry.isActive(150, timed.id(), generation));
        assertTrue(registry.snapshot(150).modifiers().isEmpty());

        SpawnModifierRegistry wrapping = new SpawnModifierRegistry();
        SpawnModifier shortLived = modifier("example:wrap", "example:source", 1.25, null, 2);
        long wrapGeneration = wrapping.register(Integer.MAX_VALUE, shortLived, false);
        assertTrue(wrapping.isActive(Integer.MIN_VALUE, shortLived.id(), wrapGeneration));
        assertFalse(wrapping.isActive(Integer.MIN_VALUE + 1, shortLived.id(), wrapGeneration));
    }

    @Test
    void snapshotsAreImmutableAndSourceRemovalIsScopedToOneRegistry() {
        SpawnModifierRegistry firstServer = new SpawnModifierRegistry();
        SpawnModifierRegistry secondServer = new SpawnModifierRegistry();
        SpawnModifier one = modifier("example:one", "example:source", 1.5, null, 0);
        SpawnModifier two = modifier("example:two", "other:source", 2.0, null, 0);
        firstServer.register(0, one, false);
        firstServer.register(0, two, false);
        secondServer.register(0, one, false);

        SpawnModifierSnapshot snapshot = firstServer.snapshot(0);
        assertEquals(List.of(one, two), snapshot.modifiers());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.modifiers().clear());
        assertEquals(1, firstServer.removeSource(0, id("example:source")));
        assertEquals(List.of(two), firstServer.snapshot(0).modifiers());
        assertEquals(List.of(one), secondServer.snapshot(0).modifiers());
    }

    @Test
    void oneStackingGroupCannotMixPolicies() {
        SpawnModifierRegistry registry = new SpawnModifierRegistry();
        SpawnModifier max = modifier("example:max", "example:source", 1.5,
                SpawnStackingPolicy.max(id("example:group")), 0);
        SpawnModifier capped = modifier("example:capped", "example:source", 1.5,
                SpawnStackingPolicy.multiplicativeCapped(id("example:group"), 3.0), 0);

        registry.register(0, max, false);
        assertThrows(IllegalStateException.class, () -> registry.register(0, capped, false));
        assertEquals(1, registry.snapshot(0).modifiers().size());
    }

    private static SpawnModifier modifier(
            String modifierId,
            String source,
            double multiplier,
            SpawnStackingPolicy policy,
            long durationTicks
    ) {
        SpawnModifier.Builder builder = SpawnModifier.builder(id(modifierId), id(source)).multiplier(multiplier);
        if (policy != null) builder.stackingPolicy(policy);
        if (durationTicks > 0) builder.expiresAfterTicks(durationTicks);
        return builder.build();
    }

    private static ResourceLocation id(String value) {
        return ResourceLocation.parse(value);
    }
}
