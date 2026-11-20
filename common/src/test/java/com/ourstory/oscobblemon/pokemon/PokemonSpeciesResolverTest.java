package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.pokemon.FormData;
import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokemonSpeciesResolverTest {
    private static final ResourceLocation ADDON_SPECIES =
            ResourceLocation.fromNamespaceAndPath("example_addon", "custom_species");

    @Test
    void parsesBareAndExplicitAddonIdentifiers() {
        assertEquals(
                Optional.of(ResourceLocation.fromNamespaceAndPath("cobblemon", "pikachu")),
                PokemonSpeciesResolver.parseId(" Pikachu ")
        );
        assertEquals(
                Optional.of(ADDON_SPECIES),
                PokemonSpeciesResolver.parseId(" Example_Addon:Custom_Species ")
        );
        assertTrue(PokemonSpeciesResolver.parseId(null).isEmpty());
        assertTrue(PokemonSpeciesResolver.parseId(" ").isEmpty());
        assertFalse(PokemonSpeciesResolver.parseId("example_addon:bad id").isPresent());
    }

    @Test
    void resolvesAddonIdentifiersAndFormNamesWithoutSpeciesHardcoding() {
        assertTrue(PokemonSpeciesResolver.resolve(ADDON_SPECIES).isEmpty());

        FormData form = new Gson().fromJson("{\"name\":\"Alternate Form\"}", FormData.class);

        assertEquals("alternateform", form.formOnlyShowdownId());
        assertTrue(PokemonSpeciesResolver.parseId("other_addon:custom_species").isPresent());
    }
}
