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
 * adds a small set of recurring constraints that are otherwise commonly
 * repeated by consuming mods: Alpha state, aspects and marks.</p>
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

    private PokemonMatcher(
            String propertyQuery,
            PokemonProperties properties,
            Boolean alpha,
            Set<String> requiredAspects,
            Set<String> anyAspects,
            Set<ResourceLocation> requiredMarks
    ) {
        this.propertyQuery = propertyQuery;
        this.properties = properties;
        this.alpha = alpha;
        this.requiredAspects = Set.copyOf(requiredAspects);
        this.anyAspects = Set.copyOf(anyAspects);
        this.requiredMarks = Set.copyOf(requiredMarks);
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
                requiredMarks
        );
    }

    /**
     * Requires a single aspect.
     */
    public PokemonMatcher aspect(String aspect) {
        Objects.requireNonNull(aspect, "aspect");
        LinkedHashSet<String> copy = new LinkedHashSet<>(requiredAspects);
        String normalized = normalizeAspect(aspect);
        if (!normalized.isEmpty()) {
            copy.add(normalized);
        }
        return new PokemonMatcher(propertyQuery, properties, alpha, copy, anyAspects, requiredMarks);
    }

    /**
     * Requires all supplied aspects.
     */
    public PokemonMatcher aspects(Collection<String> aspects) {
        Objects.requireNonNull(aspects, "aspects");
        LinkedHashSet<String> copy = new LinkedHashSet<>(requiredAspects);
        for (String aspect : aspects) {
            if (aspect == null) {
                continue;
            }
            String normalized = normalizeAspect(aspect);
            if (!normalized.isEmpty()) {
                copy.add(normalized);
            }
        }
        return new PokemonMatcher(propertyQuery, properties, alpha, copy, anyAspects, requiredMarks);
    }

    /**
     * Requires at least one of the supplied aspects.
     */
    public PokemonMatcher anyAspect(Collection<String> aspects) {
        Objects.requireNonNull(aspects, "aspects");
        LinkedHashSet<String> copy = new LinkedHashSet<>(anyAspects);
        for (String aspect : aspects) {
            if (aspect == null) {
                continue;
            }
            String normalized = normalizeAspect(aspect);
            if (!normalized.isEmpty()) {
                copy.add(normalized);
            }
        }
        return new PokemonMatcher(propertyQuery, properties, alpha, requiredAspects, copy, requiredMarks);
    }

    /**
     * Requires a namespaced mark.
     */
    public PokemonMatcher mark(ResourceLocation markId) {
        Objects.requireNonNull(markId, "markId");
        LinkedHashSet<ResourceLocation> copy = new LinkedHashSet<>(requiredMarks);
        copy.add(markId);
        return new PokemonMatcher(propertyQuery, properties, alpha, requiredAspects, anyAspects, copy);
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
        return new PokemonMatcher(propertyQuery, properties, alpha, requiredAspects, anyAspects, copy);
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

        if (!anyAspects.isEmpty()) {
            boolean matched = false;
            for (String aspect : anyAspects) {
                if (PokemonIdentity.hasAspect(pokemon, aspect)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        for (ResourceLocation markId : requiredMarks) {
            if (!PokemonIdentity.hasMark(pokemon, markId)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean test(Pokemon pokemon) {
        return matches(pokemon);
    }

    private static String normalizeAspect(String aspect) {
        return aspect.trim().toLowerCase(Locale.ROOT);
    }
}
