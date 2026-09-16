package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.mark.Mark;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Canonical identity and lightweight property helpers for Cobblemon Pokémon.
 *
 * <p>The methods in this class deliberately expose stable, namespace-aware values
 * instead of requiring every consuming mod to repeat direct Cobblemon lookups.</p>
 *
 * <p>A standard form is represented by an empty form ID. Non-standard form IDs
 * use Cobblemon's form-only Showdown identifier.</p>
 */
public final class PokemonIdentity {
    public static final String STANDARD_FORM_ID = "";

    private static final String COBBLEMON_NAMESPACE = "cobblemon";
    private static final String ALPHA_ASPECT = "alpha";
    private static final String ALPHA_MARK_PATH = "mark_alpha";

    private PokemonIdentity() {
    }

    /**
     * Creates a small immutable identity snapshot suitable for comparisons,
     * caching and persistence by consuming mods.
     */
    public static PokemonIdentitySnapshot snapshot(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        return new PokemonIdentitySnapshot(
                pokemon.getUuid(),
                speciesId(pokemon),
                formId(pokemon),
                pokemon.getShiny(),
                isAlpha(pokemon)
        );
    }

    /**
     * Returns the canonical namespaced species identifier, for example
     * {@code cobblemon:pikachu}.
     */
    public static String speciesId(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return speciesId(pokemon.getSpecies());
    }

    /**
     * Returns the canonical namespaced identifier of a registered species.
     */
    public static String speciesId(Species species) {
        Objects.requireNonNull(species, "species");
        return Objects.requireNonNull(species.getResourceIdentifier(), "species.resourceIdentifier").toString();
    }

    /**
     * Returns an empty string for the standard form, otherwise the normalized
     * form-only Showdown identifier exposed by Cobblemon.
     */
    public static String formId(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        Species species = Objects.requireNonNull(pokemon.getSpecies(), "pokemon.species");
        FormData form = Objects.requireNonNull(pokemon.getForm(), "pokemon.form");

        if (form == species.getStandardForm()) {
            return STANDARD_FORM_ID;
        }

        String formId = form.formOnlyShowdownId();
        if (formId == null || formId.isBlank()) {
            return STANDARD_FORM_ID;
        }

        return formId.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Returns whether the Pokémon currently uses its species' standard form.
     */
    public static boolean isStandardForm(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        Species species = Objects.requireNonNull(pokemon.getSpecies(), "pokemon.species");
        return pokemon.getForm() == species.getStandardForm();
    }

    /**
     * Returns whether the Pokémon currently uses a non-standard form.
     */
    public static boolean isNonStandardForm(Pokemon pokemon) {
        return !isStandardForm(pokemon);
    }

    /**
     * Returns an immutable copy of the Pokémon's current aspects.
     */
    public static Set<String> aspects(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return Set.copyOf(pokemon.getAspects());
    }

    /**
     * Performs a case-insensitive aspect lookup.
     */
    public static boolean hasAspect(Pokemon pokemon, String aspect) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(aspect, "aspect");

        String expected = aspect.trim();
        if (expected.isEmpty()) {
            return false;
        }

        for (String current : pokemon.getAspects()) {
            if (current != null && current.equalsIgnoreCase(expected)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all mark identifiers currently carried by the Pokémon.
     *
     * <p>The active mark is included defensively even if it is not present in
     * the marks collection.</p>
     */
    public static Set<ResourceLocation> markIds(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        LinkedHashSet<ResourceLocation> result = new LinkedHashSet<>();
        addMarkIdentifier(result, pokemon.getActiveMark());

        for (Mark mark : pokemon.getMarks()) {
            addMarkIdentifier(result, mark);
        }

        return Collections.unmodifiableSet(result);
    }

    /**
     * Checks a mark using its full namespaced identifier.
     */
    public static boolean hasMark(Pokemon pokemon, ResourceLocation markId) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(markId, "markId");

        if (matchesMark(pokemon.getActiveMark(), markId)) {
            return true;
        }

        for (Mark mark : pokemon.getMarks()) {
            if (matchesMark(mark, markId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience lookup for a mark from the {@code cobblemon} namespace.
     */
    public static boolean hasCobblemonMark(Pokemon pokemon, String path) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(path, "path");

        String expectedPath = path.trim().toLowerCase(Locale.ROOT);
        if (expectedPath.isEmpty()) {
            return false;
        }

        if (matchesCobblemonMark(pokemon.getActiveMark(), expectedPath)) {
            return true;
        }

        for (Mark mark : pokemon.getMarks()) {
            if (matchesCobblemonMark(mark, expectedPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether a Pokémon should be treated as Alpha.
     *
     * <p>Cobblemon 1.8 exposes native Alpha state. The aspect and mark checks are
     * retained as compatibility fallbacks for Pokémon data originating from
     * older integrations.</p>
     */
    public static boolean isAlpha(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return pokemon.isAlpha()
                || hasAspect(pokemon, ALPHA_ASPECT)
                || hasCobblemonMark(pokemon, ALPHA_MARK_PATH);
    }

    private static void addMarkIdentifier(Set<ResourceLocation> result, Mark mark) {
        if (mark != null && mark.getIdentifier() != null) {
            result.add(mark.getIdentifier());
        }
    }

    private static boolean matchesMark(Mark mark, ResourceLocation markId) {
        return mark != null && markId.equals(mark.getIdentifier());
    }

    private static boolean matchesCobblemonMark(Mark mark, String expectedPath) {
        if (mark == null || mark.getIdentifier() == null) {
            return false;
        }

        ResourceLocation id = mark.getIdentifier();
        return COBBLEMON_NAMESPACE.equals(id.getNamespace()) && expectedPath.equals(id.getPath());
    }
}
