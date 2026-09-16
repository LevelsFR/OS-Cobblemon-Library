package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.CommonAbilityType;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType;

import java.util.List;
import java.util.Objects;

/**
 * Reusable ability classification helpers for Cobblemon Pokémon.
 *
 * <p>Cobblemon 1.8 stores coordinates for non-forced abilities using their
 * priority and index inside the current form's ability pool. Those coordinates
 * are preferred over name-only matching so an ability template that appears in
 * more than one category is not accidentally misclassified.</p>
 */
public final class PokemonAbilities {
    private PokemonAbilities() {
    }

    /**
     * Classifies the currently assigned ability.
     */
    public static PokemonAbilityKind kind(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        Ability ability = Objects.requireNonNull(pokemon.getAbility(), "pokemon.ability");
        if (ability.getForced()) {
            return PokemonAbilityKind.FORCED;
        }

        PotentialAbility exact = resolveExactPotential(pokemon, ability);
        if (exact != null) {
            return kind(exact);
        }

        return resolveFallbackKind(pokemon, ability);
    }

    /**
     * Returns whether the Pokémon currently has a hidden ability.
     */
    public static boolean hasHiddenAbility(Pokemon pokemon) {
        return kind(pokemon) == PokemonAbilityKind.HIDDEN;
    }

    /**
     * Returns whether the Pokémon currently has a normal/common ability.
     */
    public static boolean hasCommonAbility(Pokemon pokemon) {
        return kind(pokemon) == PokemonAbilityKind.COMMON;
    }

    /**
     * Returns whether the active ability was explicitly forced rather than
     * selected from the current form's normal ability pool.
     */
    public static boolean hasForcedAbility(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return pokemon.getAbility().getForced();
    }

    /**
     * Returns whether the Pokémon's current form can provide at least one hidden
     * ability for its current aspects.
     */
    public static boolean canHaveHiddenAbility(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        for (PotentialAbility potential : pokemon.getForm().getAbilities()) {
            if (potential.getType() == HiddenAbilityType.INSTANCE
                    && potential.isSatisfiedBy(pokemon.getAspects())) {
                return true;
            }
        }

        return false;
    }

    private static PotentialAbility resolveExactPotential(Pokemon pokemon, Ability ability) {
        List<PotentialAbility> atPriority = pokemon.getForm()
                .getAbilities()
                .getMapping()
                .get(ability.getPriority());

        if (atPriority == null) {
            return null;
        }

        int index = ability.getIndex();
        if (index < 0 || index >= atPriority.size()) {
            return null;
        }

        PotentialAbility potential = atPriority.get(index);
        if (potential.getTemplate() != ability.getTemplate()) {
            return null;
        }

        return potential;
    }

    private static PokemonAbilityKind resolveFallbackKind(Pokemon pokemon, Ability ability) {
        PokemonAbilityKind found = PokemonAbilityKind.UNKNOWN;

        for (PotentialAbility potential : pokemon.getForm().getAbilities()) {
            if (potential.getTemplate() != ability.getTemplate()) {
                continue;
            }

            PokemonAbilityKind candidate = kind(potential);
            if (candidate == PokemonAbilityKind.UNKNOWN) {
                continue;
            }

            if (found != PokemonAbilityKind.UNKNOWN && found != candidate) {
                return PokemonAbilityKind.UNKNOWN;
            }

            found = candidate;
        }

        return found;
    }

    private static PokemonAbilityKind kind(PotentialAbility potential) {
        if (potential.getType() == HiddenAbilityType.INSTANCE) {
            return PokemonAbilityKind.HIDDEN;
        }
        if (potential.getType() == CommonAbilityType.INSTANCE) {
            return PokemonAbilityKind.COMMON;
        }
        return PokemonAbilityKind.UNKNOWN;
    }
}
