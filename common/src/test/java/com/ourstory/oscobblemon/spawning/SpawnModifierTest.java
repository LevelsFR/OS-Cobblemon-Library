package com.ourstory.oscobblemon.spawning;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnModifierTest {
    private static ResourceLocation id(String value) {
        return ResourceLocation.parse(value);
    }

    @Test
    void defaultsAndServerTickDurationAreExplicit() {
        SpawnModifier modifier = SpawnModifier.builder(id("example:event"), id("example"))
                .multiplier(1.75)
                .expiresAfterTicks(36_000)
                .scope(SpawnModifierScope.player(UUID.fromString("f96d13bc-61d6-4f57-bdb6-bc2254de5215")))
                .build();

        assertEquals(1.75, modifier.multiplier());
        assertEquals(36_000, modifier.durationTicks());
        assertEquals(SpawnModifierScope.Kind.PLAYER, modifier.scope().kind());
        assertEquals(SpawnStackingPolicy.Mode.MAX, modifier.stackingPolicy().mode());
        assertEquals(0.0, modifier.stackingPolicy().maximumMultiplier());
    }

    @Test
    void invalidMultipliersDurationsAndCapsAreRejected() {
        SpawnModifier.Builder builder = SpawnModifier.builder(id("example:event"), id("example"));
        assertThrows(IllegalArgumentException.class, () -> builder.multiplier(0.0));
        assertThrows(IllegalArgumentException.class, () -> builder.multiplier(-1.0));
        assertThrows(IllegalArgumentException.class, () -> builder.multiplier(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> builder.multiplier(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> builder.multiplier(0.0000001));
        assertThrows(IllegalArgumentException.class, () -> builder.multiplier(1_000_001));
        assertThrows(IllegalArgumentException.class, () -> builder.expiresAfterTicks(0));
        assertThrows(IllegalArgumentException.class,
                () -> SpawnStackingPolicy.multiplicativeCapped(id("example:default"), 0.99));
        assertThrows(IllegalArgumentException.class,
                () -> SpawnStackingPolicy.multiplicativeCapped(id("example:default"), Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> SpawnStackingPolicy.multiplicativeCapped(id("example:default"), 1_000_001));
    }

    @Test
    void scopesRequireTheirExactDimensionOrPlayerContext() {
        ResourceLocation overworld = id("minecraft:overworld");
        UUID player = UUID.fromString("f96d13bc-61d6-4f57-bdb6-bc2254de5215");

        assertTrue(SpawnModifierScope.global().matches(null, null));
        assertTrue(SpawnModifierScope.dimension(overworld).matches(overworld, null));
        assertFalse(SpawnModifierScope.dimension(overworld).matches(id("minecraft:the_nether"), player));
        assertFalse(SpawnModifierScope.dimension(overworld).matches(null, player));
        assertTrue(SpawnModifierScope.player(player).matches(id("minecraft:the_nether"), player));
        assertFalse(SpawnModifierScope.player(player).matches(overworld, null));
        assertFalse(SpawnModifierScope.player(player).matches(overworld, UUID.randomUUID()));
    }

    @Test
    void capabilitiesExposeTheLoadedCobblemonVersionAndPlayerScope() {
        SpawnModifierCapabilities capabilities = new SpawnModifierCapabilities(true, "1.8.4+1.21.1",
                java.util.Set.of(SpawnModifierScope.Kind.GLOBAL, SpawnModifierScope.Kind.DIMENSION,
                        SpawnModifierScope.Kind.PLAYER), true, "");

        assertEquals("1.8.4+1.21.1", capabilities.cobblemonVersion());
        assertTrue(capabilities.available());
        assertTrue(capabilities.formResolution());
        assertTrue(capabilities.supportsPlayerContext());
    }
}
