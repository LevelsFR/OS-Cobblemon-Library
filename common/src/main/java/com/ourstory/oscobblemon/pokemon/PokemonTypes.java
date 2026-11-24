package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.FormData;
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
        return types(pokemon.getForm());
    }

    /**
     * Returns the supplied form's types without constructing a Pokémon.
     */
    public static List<ElementalType> types(FormData form) {
        Objects.requireNonNull(form, "form");

        List<ElementalType> result = new ArrayList<>(2);
        for (ElementalType type : form.getTypes()) {
            result.add(type);
        }
        return List.copyOf(result);
    }

    /**
     * Returns the current primary type.
     */
    public static ElementalType primary(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return primary(pokemon.getForm());
    }

    /** Returns the supplied form's primary type. */
    public static ElementalType primary(FormData form) {
        Objects.requireNonNull(form, "form");
        return form.getPrimaryType();
    }

    /**
     * Returns the current secondary type when present.
     */
    public static Optional<ElementalType> secondary(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return secondary(pokemon.getForm());
    }

    /** Returns the supplied form's secondary type when present. */
    public static Optional<ElementalType> secondary(FormData form) {
        Objects.requireNonNull(form, "form");
        return Optional.ofNullable(form.getSecondaryType());
    }

    /**
     * Returns normalized type names such as {@code fire} and {@code flying}.
     */
    public static Set<String> typeNames(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return typeNames(pokemon.getForm());
    }

    /** Returns normalized type names for the supplied form. */
    public static Set<String> typeNames(FormData form) {
        Objects.requireNonNull(form, "form");

        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (ElementalType type : types(form)) {
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
        return hasType(pokemon.getForm(), expected);
    }

    /** Returns whether the supplied form has the expected type. */
    public static boolean hasType(FormData form, ElementalType expected) {
        Objects.requireNonNull(form, "form");
        Objects.requireNonNull(expected, "expected");

        for (ElementalType type : form.getTypes()) {
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
        return hasType(pokemon.getForm(), typeName);
    }

    /** Returns whether the supplied form has the supplied type name. */
    public static boolean hasType(FormData form, String typeName) {
        Objects.requireNonNull(form, "form");

        Optional<ElementalType> resolved = resolve(typeName);
        return resolved.isPresent() && hasType(form, resolved.get());
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
