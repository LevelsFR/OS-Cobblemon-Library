package com.ourstory.oscobblemon.pokemon;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PokemonSnapshotTest {
    @Test
    void copiesMutableDisplayNameAndAspects() {
        MutableComponent sourceName = Component.literal("Pikachu");
        Set<String> sourceAspects = new HashSet<>(Set.of("shiny"));
        PokemonSnapshot snapshot = new PokemonSnapshot(
                UUID.randomUUID(),
                ResourceLocation.fromNamespaceAndPath("cobblemon", "pikachu"),
                "normal",
                sourceAspects,
                sourceName,
                ResourceLocation.fromNamespaceAndPath("cobblemon", "poke_ball"),
                true,
                false,
                25,
                50,
                50,
                null
        );

        sourceName.append(" changed");
        sourceAspects.add("altered");

        assertEquals("Pikachu", snapshot.displayName().getString());
        assertEquals(Set.of("shiny"), snapshot.aspects());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.aspects().add("altered"));
    }

    @Test
    void displayNameAccessorReturnsANewCopyEachTime() {
        PokemonSnapshot snapshot = new PokemonSnapshot(
                UUID.randomUUID(),
                ResourceLocation.fromNamespaceAndPath("cobblemon", "pikachu"),
                null,
                Set.of(),
                Component.literal("Pikachu"),
                ResourceLocation.fromNamespaceAndPath("cobblemon", "poke_ball"),
                false,
                false,
                25,
                50,
                50,
                null
        );

        snapshot.displayName().append(" changed");

        assertEquals("Pikachu", snapshot.displayName().getString());
    }
}
