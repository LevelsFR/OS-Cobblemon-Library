package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.PokemonSizeCategory;

import java.util.Collection;
import java.util.Objects;

/**
 * Reusable helpers around Cobblemon's native Pokémon size model.
 *
 * <p>The scale helpers return a safe positive value. Invalid, non-finite or
 * non-positive scale values fall back to {@code 1.0F}, matching the defensive
 * handling commonly needed by UI and statistics integrations.</p>
 */
public final class PokemonSize {
    private static final float DEFAULT_SCALE = 1.0F;

    private PokemonSize() {
    }

    /**
     * Returns the native Cobblemon size category.
     */
    public static PokemonSizeCategory category(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return Objects.requireNonNull(pokemon.getSizeCategory(), "pokemon.sizeCategory");
    }

    /**
     * Returns the native Cobblemon size category for a Pokémon entity.
     */
    public static PokemonSizeCategory category(PokemonEntity entity) {
        return category(requirePokemon(entity));
    }

    /**
     * Returns whether the Pokémon belongs to the requested native size category.
     */
    public static boolean isCategory(Pokemon pokemon, PokemonSizeCategory expected) {
        Objects.requireNonNull(expected, "expected");
        return category(pokemon) == expected;
    }

    /**
     * Returns whether the Pokémon belongs to at least one requested native size
     * category.
     */
    public static boolean isAnyCategory(
            Pokemon pokemon,
            Collection<PokemonSizeCategory> categories
    ) {
        Objects.requireNonNull(categories, "categories");
        PokemonSizeCategory current = category(pokemon);
        for (PokemonSizeCategory category : categories) {
            if (category != null && current == category) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns Cobblemon's translation key for the native size category.
     */
    public static String translationKey(Pokemon pokemon) {
        return PokemonSizeCategory.Companion.translationKey(category(pokemon));
    }

    /**
     * Returns Cobblemon's translation key for a Pokémon entity's native size
     * category.
     */
    public static String translationKey(PokemonEntity entity) {
        return translationKey(requirePokemon(entity));
    }

    /**
     * Returns a safe positive form scale modifier.
     */
    public static float scaleModifier(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return sanitizeScale(pokemon.getScaleModifier());
    }

    /**
     * Returns a safe positive form scale modifier for a Pokémon entity.
     */
    public static float scaleModifier(PokemonEntity entity) {
        return scaleModifier(requirePokemon(entity));
    }

    /**
     * Returns a safe positive effective scale, including Cobblemon modifiers
     * such as Alpha scaling.
     */
    public static float effectiveScale(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return sanitizeScale(pokemon.getEffectiveScale());
    }

    /**
     * Returns a safe positive effective scale for a Pokémon entity.
     */
    public static float effectiveScale(PokemonEntity entity) {
        return effectiveScale(requirePokemon(entity));
    }

    private static Pokemon requirePokemon(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return Objects.requireNonNull(entity.getPokemon(), "entity.pokemon");
    }

    private static float sanitizeScale(float scale) {
        return Float.isFinite(scale) && scale > 0.0F ? scale : DEFAULT_SCALE;
    }
}
