package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Namespace-aware species resolution for Cobblemon and addon species.
 *
 * <p>Bare IDs such as {@code pikachu} default to the {@code cobblemon}
 * namespace. Explicit IDs such as {@code addon:species_name} retain their
 * namespace.</p>
 */
public final class PokemonSpeciesResolver {
    private PokemonSpeciesResolver() {
    }

    /**
     * Resolves a species ID, defaulting bare IDs to the Cobblemon namespace.
     */
    public static Optional<Species> resolve(String rawId) {
        return parseId(rawId).flatMap(PokemonSpeciesResolver::resolve);
    }

    /**
     * Resolves a species by a canonical resource identifier.
     */
    public static Optional<Species> resolve(ResourceLocation id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(PokemonSpecies.INSTANCE.getByIdentifier(id));
    }

    /**
     * Resolves a species or throws an informative exception.
     */
    public static Species require(String rawId) {
        return resolve(rawId).orElseThrow(
                () -> new IllegalArgumentException("Unknown Cobblemon species: " + rawId)
        );
    }

    /**
     * Returns the canonical registered species identifier when the species
     * exists.
     */
    public static Optional<ResourceLocation> canonicalId(String rawId) {
        return resolve(rawId).map(Species::getResourceIdentifier);
    }

    /**
     * Returns whether the supplied species ID currently exists.
     */
    public static boolean exists(String rawId) {
        return resolve(rawId).isPresent();
    }

    /**
     * Parses a resource identifier without querying the species registry.
     */
    public static Optional<ResourceLocation> parseId(String rawId) {
        if (rawId == null) {
            return Optional.empty();
        }

        String normalized = rawId.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        String id = normalized.indexOf(':') >= 0
                ? normalized
                : Cobblemon.MODID + ":" + normalized;

        return Optional.ofNullable(ResourceLocation.tryParse(id));
    }
}
