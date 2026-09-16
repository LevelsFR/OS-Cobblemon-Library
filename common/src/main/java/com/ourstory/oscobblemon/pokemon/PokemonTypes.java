package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Reusable helpers for a Pokémon's current elemental typing.
 *
 * <p>The current form is always used, so form-specific type changes and custom
 * registered elemental types are respected.</p>
 */
public final class PokemonTypes {
    private PokemonTypes() {
    }

    /**
     * Returns the current form's types in primary/secondary order.
     */
    public static List<ElementalType> types(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        List<ElementalType> result = new ArrayList<>(2);
        for (ElementalType type : pokemon.getForm().getTypes()) {
            result.add(type);
        }
        return List.copyOf(result);
    }

    /**
     * Returns the current primary type.
     */
    public static ElementalType primary(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return pokemon.getForm().getPrimaryType();
    }

    /**
     * Returns the current secondary type when present.
     */
    public static Optional<ElementalType> secondary(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return Optional.ofNullable(pokemon.getForm().getSecondaryType());
    }

    /**
     * Returns normalized type names such as {@code fire} and {@code flying}.
     */
    public static Set<String> typeNames(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (ElementalType type : types(pokemon)) {
            result.add(type.getName().toLowerCase(Locale.ROOT));
        }
        return Set.copyOf(result);
    }

    /**
     * Resolves an elemental type by name.
     */
    public static Optional<ElementalType> resolve(String typeName) {
        if (typeName == null) {
            return Optional.empty();
        }

        String normalized = typeName.trim();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(ElementalTypes.INSTANCE.get(normalized));
    }

    /**
     * Returns whether the Pokémon currently has the supplied type.
     */
    public static boolean hasType(Pokemon pokemon, ElementalType expected) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(expected, "expected");

        for (ElementalType type : pokemon.getForm().getTypes()) {
            if (type == expected || type.getName().equalsIgnoreCase(expected.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the Pokémon currently has the supplied type name.
     */
    public static boolean hasType(Pokemon pokemon, String typeName) {
        Objects.requireNonNull(pokemon, "pokemon");

        Optional<ElementalType> resolved = resolve(typeName);
        return resolved.isPresent() && hasType(pokemon, resolved.get());
    }

    /**
     * Returns whether the Pokémon has at least one of the supplied type names.
     */
    public static boolean hasAnyType(Pokemon pokemon, Collection<String> typeNames) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(typeNames, "typeNames");

        for (String typeName : typeNames) {
            if (hasType(pokemon, typeName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the Pokémon has every supplied type name.
     */
    public static boolean hasAllTypes(Pokemon pokemon, Collection<String> typeNames) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(typeNames, "typeNames");

        for (String typeName : typeNames) {
            if (!hasType(pokemon, typeName)) {
                return false;
            }
        }

        return true;
    }
}
