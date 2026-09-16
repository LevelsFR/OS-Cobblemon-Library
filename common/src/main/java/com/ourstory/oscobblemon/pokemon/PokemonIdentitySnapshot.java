package com.ourstory.oscobblemon.pokemon;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable normalized identity data for a Cobblemon Pokémon.
 *
 * <p>The species ID is namespace-aware, for example {@code cobblemon:pikachu}.
 * The standard form is represented by an empty {@code formId}.</p>
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
        formId = formId == null ? PokemonIdentity.STANDARD_FORM_ID : formId;
    }

    /**
     * Returns whether this snapshot represents the species' standard form.
     */
    public boolean standardForm() {
        return formId.isEmpty();
    }
}
