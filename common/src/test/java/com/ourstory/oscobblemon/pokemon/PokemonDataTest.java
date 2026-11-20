package com.ourstory.oscobblemon.pokemon;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokemonDataTest {
    private static final ResourceLocation KEY =
            ResourceLocation.fromNamespaceAndPath("test_addon", "state");

    @Test
    void readsAndUpdatesDefensiveNamespacedSections() {
        var pokemon = PokemonTestFixture.newPokemon();

        assertTrue(PokemonData.read(pokemon, KEY).isEmpty());
        PokemonData.update(pokemon, KEY, section -> section.putString("status", "ready"));

        CompoundTag read = PokemonData.read(pokemon, KEY).orElseThrow();
        read.putString("status", "changed");

        assertEquals("ready", PokemonData.read(pokemon, KEY).orElseThrow().getString("status"));
    }

    @Test
    void decodesTypedValuesAndUsesDefaultOnlyWhenAbsent() {
        var pokemon = PokemonTestFixture.newPokemon();

        assertEquals(Integer.valueOf(7), PokemonData.readOrDefault(pokemon, KEY, Codec.INT, 7).result().orElseThrow());
        assertEquals(Integer.valueOf(42), PokemonData.write(pokemon, KEY, Codec.INT, 42).result().orElseThrow());
        assertEquals(Integer.valueOf(42), PokemonData.readOrDefault(pokemon, KEY, Codec.INT, 7).result().orElseThrow());
    }

    @Test
    void rejectsMalformedStoredSectionsInsteadOfReturningDefault() {
        var pokemon = PokemonTestFixture.newPokemon();

        pokemon.getPersistentData().putString(KEY.toString(), "not a compound");
        assertTrue(PokemonData.readOrDefault(pokemon, KEY, Codec.INT, 7).error().isPresent());

        pokemon.getPersistentData().put(KEY.toString(), new CompoundTag());
        assertTrue(PokemonData.readOrDefault(pokemon, KEY, Codec.INT, 7).error().isPresent());
    }

    @Test
    void failedUpdaterDoesNotReplaceStoredSectionAndRemoveIsIdempotent() {
        var pokemon = PokemonTestFixture.newPokemon();
        PokemonData.update(pokemon, KEY, section -> section.putString("status", "ready"));

        assertThrows(IllegalStateException.class, () -> PokemonData.update(pokemon, KEY, section -> {
            section.putString("status", "partial");
            throw new IllegalStateException("abort update");
        }));
        assertEquals("ready", PokemonData.read(pokemon, KEY).orElseThrow().getString("status"));

        PokemonData.remove(pokemon, KEY);
        PokemonData.remove(pokemon, KEY);
        assertFalse(PokemonData.contains(pokemon, KEY));
    }
}
