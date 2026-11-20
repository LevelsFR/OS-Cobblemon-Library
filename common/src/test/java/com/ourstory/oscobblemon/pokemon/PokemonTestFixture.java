package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

final class PokemonTestFixture {
    private static final ResourceLocation SPECIES_ID =
            ResourceLocation.fromNamespaceAndPath("oscl_test", "fixture");

    private PokemonTestFixture() {
    }

    static Pokemon newPokemon() {
        return withRegisteredSpecies(SPECIES_ID, new Species(), Pokemon::new);
    }

    static <T> T withRegisteredSpecies(
            ResourceLocation id,
            Species species,
            Supplier<T> action
    ) {
        Map<ResourceLocation, Species> original = new LinkedHashMap<>();
        PokemonSpecies.INSTANCE.getSpecies().forEach(
                registered -> original.put(registered.getResourceIdentifier(), registered)
        );

        species.setResourceIdentifier(id);
        try {
            PokemonSpecies.INSTANCE.reload(Map.of(id, species));
            return action.get();
        } finally {
            PokemonSpecies.INSTANCE.reload(original);
        }
    }
}
