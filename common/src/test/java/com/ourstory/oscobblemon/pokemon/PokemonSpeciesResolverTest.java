package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    void resolvesRegisteredAddonSpeciesAndItsForm() {
        Species species = new Species();

        PokemonTestFixture.withRegisteredSpecies(ADDON_SPECIES, species, () -> {
            FormData form = new Gson().fromJson("{\"name\":\"Alternate Form\"}", FormData.class);
            species.getForms().add(form);

            assertSame(species, PokemonSpeciesResolver.resolve("example_addon:custom_species").orElseThrow());
            assertEquals(Optional.of(ADDON_SPECIES), PokemonSpeciesResolver.canonicalId("example_addon:custom_species"));
            assertEquals("example_addon:custom_species", PokemonIdentity.speciesId(species));
            assertSame(form, species.getFormByShowdownId("alternateform"));
            assertEquals("alternateform", form.formOnlyShowdownId());
            return null;
        });
    }
}
