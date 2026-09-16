package com.ourstory.oscobblemon.pokemon;

import java.util.Objects;
import java.util.UUID;

/**
 * Loader-neutral normalized identity data for a Pokémon.
 *
 * <p>This record deliberately stores simple Java values so consuming mods can
 * compare or persist identity information without coupling that data model to
 * Cobblemon implementation classes.</p>
 */
public record PokemonIdentitySnapshot(
        UUID uuid,
        String speciesId,
        String formId,
        boolean shiny,
        boolean alpha
) {
    public PokemonIdentitySnapshot {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(speciesId, "speciesId");
        formId = formId == null ? "" : formId;
    }
}
