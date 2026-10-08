package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable UI-oriented values copied from a Pokémon at one point in time.
 */
public record PokemonSnapshot(
        UUID uuid,
        ResourceLocation speciesId,
        String formId,
        Set<String> aspects,
        MutableComponent displayName,
        ResourceLocation caughtBallId,
        boolean shiny,
        boolean alpha,
        int level,
        int currentHealth,
        int maxHealth,
        ResourceLocation persistentStatusId
) {
    public PokemonSnapshot {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(speciesId, "speciesId");
        formId = formId == null ? PokemonIdentity.STANDARD_FORM_ID : formId;
        aspects = Set.copyOf(Objects.requireNonNull(aspects, "aspects"));
        displayName = Objects.requireNonNull(displayName, "displayName").copy();
        Objects.requireNonNull(caughtBallId, "caughtBallId");
    }

    /**
     * Captures the current display-facing values without retaining the live Pokémon.
     */
    public static PokemonSnapshot from(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return new PokemonSnapshot(
                pokemon.getUuid(),
                pokemon.getSpecies().getResourceIdentifier(),
                PokemonIdentity.formId(pokemon),
                PokemonIdentity.aspects(pokemon),
                pokemon.getDisplayName(false),
                PokemonBalls.idOf(pokemon.getCaughtBall()),
                pokemon.getShiny(),
                PokemonIdentity.isAlpha(pokemon),
                pokemon.getLevel(),
                pokemon.getCurrentHealth(),
                pokemon.getMaxHealth(),
                pokemon.getStatus() == null ? null : pokemon.getStatus().getStatus().getName()
        );
    }

    @Override
    public MutableComponent displayName() {
        return displayName.copy();
    }
}
