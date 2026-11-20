package com.ourstory.oscobblemon.pokemon;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokemonDataTest {
    private static final String KEY =
            ResourceLocation.fromNamespaceAndPath("test_addon", "state").toString();

    @Test
    void readsAndUpdatesDefensiveNamespacedSections() {
        CompoundTag data = new CompoundTag();

        assertTrue(PokemonData.read(data, KEY).isEmpty());
        PokemonData.update(data, KEY, section -> section.putString("status", "ready"));

        CompoundTag read = PokemonData.read(data, KEY).orElseThrow();
        read.putString("status", "changed");

        assertEquals("ready", PokemonData.read(data, KEY).orElseThrow().getString("status"));
    }

    @Test
    void decodesTypedValuesAndUsesDefaultOnlyWhenAbsent() {
        CompoundTag data = new CompoundTag();

        assertEquals(Integer.valueOf(7), PokemonData.readOrDefault(data, KEY, Codec.INT, 7, NbtOps.INSTANCE).result().orElseThrow());
        assertEquals(Integer.valueOf(42), PokemonData.write(data, KEY, Codec.INT, 42, NbtOps.INSTANCE).result().orElseThrow());
        assertEquals(Integer.valueOf(42), PokemonData.readOrDefault(data, KEY, Codec.INT, 7, NbtOps.INSTANCE).result().orElseThrow());
    }

    @Test
    void rejectsMalformedStoredSectionsInsteadOfReturningDefault() {
        CompoundTag data = new CompoundTag();

        data.putString(KEY, "not a compound");
        assertTrue(PokemonData.readOrDefault(data, KEY, Codec.INT, 7, NbtOps.INSTANCE).error().isPresent());

        data.put(KEY, new CompoundTag());
        assertTrue(PokemonData.readOrDefault(data, KEY, Codec.INT, 7, NbtOps.INSTANCE).error().isPresent());
    }

    @Test
    void failedUpdaterDoesNotReplaceStoredSectionAndRemoveIsIdempotent() {
        CompoundTag data = new CompoundTag();
        PokemonData.update(data, KEY, section -> section.putString("status", "ready"));

        assertThrows(IllegalStateException.class, () -> PokemonData.update(data, KEY, section -> {
            section.putString("status", "partial");
            throw new IllegalStateException("abort update");
        }));
        assertEquals("ready", PokemonData.read(data, KEY).orElseThrow().getString("status"));

        PokemonData.remove(data, KEY);
        PokemonData.remove(data, KEY);
        assertFalse(PokemonData.contains(data, KEY));
    }
}
