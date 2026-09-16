package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Reusable Pokémon matcher built on top of Cobblemon's native
 * {@link PokemonProperties} matching.
 *
 * <p>Use the native property string for species, form, level, shiny, gender,
 * nature, ability, type, IVs, EVs and registered custom properties. This class
 * only adds recurring constraints that are not already convenient in
 * {@code PokemonProperties}: Alpha state, aspects, marks and data labels.</p>
 *
 * <p>Instances are immutable and safe to reuse.</p>
 */
public final class PokemonMatcher implements Predicate<Pokemon> {
    private final String propertyQuery;
    private final PokemonProperties properties;
    private final Boolean alpha;
    private final Set<String> requiredAspects;
    private final Set<String> anyAspects;
    private final Set<ResourceLocation> requiredMarks;
    private final Set<String> requiredLabels;
    private final Set<String> anyLabels;

    private PokemonMatcher(
            String propertyQuery,
            PokemonProperties properties,
            Boolean alpha,
            Set<String> requiredAspects,
            Set<String> anyAspects,
            Set<ResourceLocation> requiredMarks,
            Set<String> requiredLabels,
            Set<String> anyLabels
    ) {
        this.propertyQuery = propertyQuery;
        this.properties = properties;
        this.alpha = alpha;
        this.requiredAspects = Set.copyOf(requiredAspects);
        this.anyAspects = Set.copyOf(anyAspects);
        this.requiredMarks = Set.copyOf(requiredMarks);
        this.requiredLabels = Set.copyOf(requiredLabels);
        this.anyLabels = Set.copyOf(anyLabels);
    }

    /**
     * Creates a matcher with no constraints.
     */
    public static PokemonMatcher any() {
        return fromProperties("");
    }

    /**
     * Creates a matcher using Cobblemon's native Pokémon property syntax.
     *
     * <p>Examples include {@code pikachu}, {@code species=pikachu shiny=true},
     * {@code level=50} and registered custom properties.</p>
     */
    public static PokemonMatcher fromProperties(String query) {
        Objects.requireNonNull(query, "query");
        String normalized = query.trim();
        PokemonProperties properties = PokemonProperties.Companion.parse(normalized);
        return new PokemonMatcher(
                normalized,
                properties,
                null,
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of()
        );
    }

    /**
     * Requires the Pokémon to match or not match Alpha state.
     */
    public PokemonMatcher alpha(boolean required) {
        return new PokemonMatcher(
                propertyQuery,
                properties,
                required,
                requiredAspects,
                anyAspects,
                requiredMarks,
                requiredLabels,
                anyLabels
        );
    }

    /**
     * Requires a single aspect.
     */
    public PokemonMatcher aspect(String aspect) {
        Objects.requireNonNull(aspect, "aspect");
        LinkedHashSet<String> copy = new LinkedHashSet<>(requiredAspects);
        String normalized = normalize(aspect);
        if (!normalized.isEmpty()) {
            copy.add(normalized);
        }
        return new PokemonMatcher(
                propertyQuery, properties, alpha, copy, anyAspects,
                requiredMarks, requiredLabels, anyLabels
        );
    }

    /**
     * Requires all supplied aspects.
     */
    public PokemonMatcher aspects(Collection<String> aspects) {
        Objects.requireNonNull(aspects, "aspects");
        LinkedHashSet<String> copy = normalizedCopy(requiredAspects, aspects);
        return new PokemonMatcher(
                propertyQuery, properties, alpha, copy, anyAspects,
                requiredMarks, requiredLabels, anyLabels
        );
    }

    /**
     * Requires at least one of the supplied aspects.
     */
    public PokemonMatcher anyAspect(Collection<String> aspects) {
        Objects.requireNonNull(aspects, "aspects");
        LinkedHashSet<String> copy = normalizedCopy(anyAspects, aspects);
        return new PokemonMatcher(
                propertyQuery, properties, alpha, requiredAspects, copy,
                requiredMarks, requiredLabels, anyLabels
        );
    }

    /**
     * Requires a namespaced mark.
     */
    public PokemonMatcher mark(ResourceLocation markId) {
        Objects.requireNonNull(markId, "markId");
        LinkedHashSet<ResourceLocation> copy = new LinkedHashSet<>(requiredMarks);
        copy.add(markId);
        return new PokemonMatcher(
                propertyQuery, properties, alpha, requiredAspects, anyAspects,
                copy, requiredLabels, anyLabels
        );
    }

    /**
     * Requires all supplied marks.
     */
    public PokemonMatcher marks(Collection<ResourceLocation> markIds) {
        Objects.requireNonNull(markIds, "markIds");
        LinkedHashSet<ResourceLocation> copy = new LinkedHashSet<>(requiredMarks);
        for (ResourceLocation markId : markIds) {
            if (markId != null) {
                copy.add(markId);
            }
        }
        return new PokemonMatcher(
                propertyQuery, properties, alpha, requiredAspects, anyAspects,
                copy, requiredLabels, anyLabels
        );
    }

    /**
     * Requires one data label on the Pokémon's current form.
     */
    public PokemonMatcher label(String label) {
        Objects.requireNonNull(label, "label");
        LinkedHashSet<String> copy = new LinkedHashSet<>(requiredLabels);
        String normalized = normalize(label);
        if (!normalized.isEmpty()) {
            copy.add(normalized);
        }
        return new PokemonMatcher(
                propertyQuery, properties, alpha, requiredAspects, anyAspects,
                requiredMarks, copy, anyLabels
        );
    }

    /**
     * Requires all supplied data labels.
     */
    public PokemonMatcher labels(Collection<String> labels) {
        Objects.requireNonNull(labels, "labels");
        LinkedHashSet<String> copy = normalizedCopy(requiredLabels, labels);
        return new PokemonMatcher(
                propertyQuery, properties, alpha, requiredAspects, anyAspects,
                requiredMarks, copy, anyLabels
        );
    }

    /**
     * Requires at least one of the supplied data labels.
     */
    public PokemonMatcher anyLabel(Collection<String> labels) {
        Objects.requireNonNull(labels, "labels");
        LinkedHashSet<String> copy = normalizedCopy(anyLabels, labels);
        return new PokemonMatcher(
                propertyQuery, properties, alpha, requiredAspects, anyAspects,
                requiredMarks, requiredLabels, copy
        );
    }

    /**
     * Returns the original native Cobblemon property query.
     */
    public String propertyQuery() {
        return propertyQuery;
    }

    /**
     * Matches a Pokémon against all configured constraints.
     */
    public boolean matches(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        if (!properties.matches(pokemon)) {
            return false;
        }

        if (alpha != null && PokemonIdentity.isAlpha(pokemon) != alpha) {
            return false;
        }

        for (String aspect : requiredAspects) {
            if (!PokemonIdentity.hasAspect(pokemon, aspect)) {
                return false;
            }
        }

        if (!anyAspects.isEmpty() && !matchesAnyAspect(pokemon)) {
            return false;
        }

        for (ResourceLocation markId : requiredMarks) {
            if (!PokemonIdentity.hasMark(pokemon, markId)) {
                return false;
            }
        }

        for (String label : requiredLabels) {
            if (!PokemonLabels.hasLabel(pokemon, label)) {
                return false;
            }
        }

        if (!anyLabels.isEmpty() && !PokemonLabels.hasAnyLabel(pokemon, anyLabels)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean test(Pokemon pokemon) {
        return matches(pokemon);
    }

    private boolean matchesAnyAspect(Pokemon pokemon) {
        for (String aspect : anyAspects) {
            if (PokemonIdentity.hasAspect(pokemon, aspect)) {
                return true;
            }
        }
        return false;
    }

    private static LinkedHashSet<String> normalizedCopy(
            Collection<String> existing,
            Collection<String> additions
    ) {
        LinkedHashSet<String> result = new LinkedHashSet<>(existing);
        for (String value : additions) {
            String normalized = normalize(value);
            if (!normalized.isEmpty()) {
                result.add(normalized);
            }
        }
        return result;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
