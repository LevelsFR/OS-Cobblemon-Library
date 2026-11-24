package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.FormData;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Reusable access to the current form's Cobblemon labels.
 *
 * <p>Labels are data-driven classifications supplied by Cobblemon species/form
 * data and addons. This class deliberately does not hard-code meanings such as
 * legendary, mythical, fossil or starter. Consuming mods decide which labels
 * matter to their own gameplay.</p>
 */
public final class PokemonLabels {
    private PokemonLabels() {
    }

    /**
     * Returns an immutable normalized snapshot of the current form's labels.
     */
    public static Set<String> labels(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return labels(pokemon.getForm());
    }

    /**
     * Returns an immutable normalized snapshot of a form's native labels,
     * without constructing a Pokémon.
     */
    public static Set<String> labels(FormData form) {
        Objects.requireNonNull(form, "form");

        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String label : form.getLabels()) {
            String normalized = normalize(label);
            if (!normalized.isEmpty()) {
                result.add(normalized);
            }
        }
        return Set.copyOf(result);
    }

    /**
     * Returns whether the current form has the supplied label.
     */
    public static boolean hasLabel(Pokemon pokemon, String label) {
        Objects.requireNonNull(pokemon, "pokemon");
        return hasLabel(pokemon.getForm(), label);
    }

    /** Returns whether the supplied form has the given native label. */
    public static boolean hasLabel(FormData form, String label) {
        Objects.requireNonNull(form, "form");
        String expected = normalize(label);
        if (expected.isEmpty()) {
            return false;
        }

        for (String current : form.getLabels()) {
            if (expected.equals(normalize(current))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the current form has at least one supplied label.
     */
    public static boolean hasAnyLabel(Pokemon pokemon, Collection<String> labels) {
        Objects.requireNonNull(pokemon, "pokemon");
        return hasAnyLabel(pokemon.getForm(), labels);
    }

    /** Returns whether the supplied form has at least one supplied label. */
    public static boolean hasAnyLabel(FormData form, Collection<String> labels) {
        Objects.requireNonNull(form, "form");
        Objects.requireNonNull(labels, "labels");

        for (String label : labels) {
            if (hasLabel(form, label)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the current form has every supplied label.
     */
    public static boolean hasAllLabels(Pokemon pokemon, Collection<String> labels) {
        Objects.requireNonNull(pokemon, "pokemon");
        return hasAllLabels(pokemon.getForm(), labels);
    }

    /** Returns whether the supplied form has every supplied label. */
    public static boolean hasAllLabels(FormData form, Collection<String> labels) {
        Objects.requireNonNull(form, "form");
        Objects.requireNonNull(labels, "labels");

        for (String label : labels) {
            if (!hasLabel(form, label)) {
                return false;
            }
        }
        return true;
    }

    private static String normalize(String label) {
        if (label == null) {
            return "";
        }
        return label.trim().toLowerCase(Locale.ROOT);
    }
}
