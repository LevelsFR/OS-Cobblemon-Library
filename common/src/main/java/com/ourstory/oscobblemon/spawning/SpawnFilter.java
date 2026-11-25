package com.ourstory.oscobblemon.spawning;

import com.ourstory.oscobblemon.pokemon.PokemonSpeciesResolver;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Immutable declarative filter over Cobblemon spawn definitions. */
public final class SpawnFilter {
    private static final SpawnFilter ANY = new Builder().build();

    private final Set<String> types;
    private final Set<ResourceLocation> species;
    private final Map<ResourceLocation, Set<String>> forms;
    private final Set<String> aspects;
    private final Set<String> anyAspects;
    private final Set<String> excludedAspects;
    private final Set<String> labels;
    private final Set<String> anyLabels;
    private final Set<String> excludedLabels;
    private final Set<ResourceLocation> excludedSpecies;
    private final Map<ResourceLocation, Set<String>> excludedForms;
    private final Set<String> buckets;

    private SpawnFilter(Builder builder) {
        types = Set.copyOf(builder.types);
        species = Set.copyOf(builder.species);
        forms = immutableFormMap(builder.forms);
        aspects = Set.copyOf(builder.aspects);
        anyAspects = Set.copyOf(builder.anyAspects);
        excludedAspects = Set.copyOf(builder.excludedAspects);
        labels = Set.copyOf(builder.labels);
        anyLabels = Set.copyOf(builder.anyLabels);
        excludedLabels = Set.copyOf(builder.excludedLabels);
        excludedSpecies = Set.copyOf(builder.excludedSpecies);
        excludedForms = immutableFormMap(builder.excludedForms);
        buckets = Set.copyOf(builder.buckets);
    }

    public static SpawnFilter any() {
        return ANY;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Returns whether every possible Pokémon represented by the candidate matches. */
    public boolean matches(SpawnCandidate candidate) {
        Objects.requireNonNull(candidate, "candidate");
        ResourceLocation candidateSpecies = PokemonSpeciesResolver.parseId(candidate.speciesId()).orElse(null);
        if (candidateSpecies == null) return false;
        if (!buckets.isEmpty() && !buckets.contains(candidate.bucket())) return false;
        if (!species.isEmpty() && !species.contains(candidateSpecies)) return false;
        if (excludedSpecies.contains(candidateSpecies)) return false;
        if (!candidate.aspects().containsAll(aspects)) return false;
        if (!anyAspects.isEmpty() && candidate.aspects().stream().noneMatch(anyAspects::contains)) return false;
        if (candidate.aspects().stream().anyMatch(excludedAspects::contains)) return false;

        if (!forms.isEmpty() && !forms.containsKey(candidateSpecies)) return false;
        Set<String> allowedForms = forms.get(candidateSpecies);
        Set<String> deniedForms = excludedForms.getOrDefault(candidateSpecies, Set.of());
        for (SpawnCandidate.Form form : candidate.possibleForms()) {
            if (!types.isEmpty() && form.types().stream().noneMatch(types::contains)) return false;
            if (!labels.isEmpty() && !form.labels().containsAll(labels)) return false;
            if (!anyLabels.isEmpty() && form.labels().stream().noneMatch(anyLabels::contains)) return false;
            if (form.labels().stream().anyMatch(excludedLabels::contains)) return false;
            if (!deniedForms.isEmpty() && deniedForms.contains(form.id())) return false;
            if (allowedForms != null && !allowedForms.contains(form.id())) return false;
        }
        return true;
    }

    private static Map<ResourceLocation, Set<String>> immutableFormMap(Map<ResourceLocation, Set<String>> source) {
        Map<ResourceLocation, Set<String>> result = new LinkedHashMap<>();
        source.forEach((species, forms) -> result.put(species, Set.copyOf(forms)));
        return Map.copyOf(result);
    }

    public static final class Builder {
        private final Set<String> types = new LinkedHashSet<>();
        private final Set<ResourceLocation> species = new LinkedHashSet<>();
        private final Map<ResourceLocation, Set<String>> forms = new LinkedHashMap<>();
        private final Set<String> aspects = new LinkedHashSet<>();
        private final Set<String> anyAspects = new LinkedHashSet<>();
        private final Set<String> excludedAspects = new LinkedHashSet<>();
        private final Set<String> labels = new LinkedHashSet<>();
        private final Set<String> anyLabels = new LinkedHashSet<>();
        private final Set<String> excludedLabels = new LinkedHashSet<>();
        private final Set<ResourceLocation> excludedSpecies = new LinkedHashSet<>();
        private final Map<ResourceLocation, Set<String>> excludedForms = new LinkedHashMap<>();
        private final Set<String> buckets = new LinkedHashSet<>();

        /** Matches any possible primary or secondary type; each modifier is applied once. */
        public Builder type(String type) {
            types.add(normalized(type, "type"));
            return this;
        }

        public Builder types(Collection<String> values) {
            Objects.requireNonNull(values, "types").forEach(this::type);
            return this;
        }

        /** Matches one of the namespaced species IDs; bare IDs default to Cobblemon. */
        public Builder species(String speciesId) {
            species.add(parseSpecies(speciesId));
            return this;
        }

        public Builder species(Collection<String> speciesIds) {
            Objects.requireNonNull(speciesIds, "speciesIds").forEach(this::species);
            return this;
        }

        /** Requires the exact form-only Showdown ID for the named species. Use an empty ID for its standard form. */
        public Builder form(String speciesId, String formId) {
            addForm(forms, speciesId, formId);
            return this;
        }

        public Builder aspect(String aspect) {
            aspects.add(normalized(aspect, "aspect"));
            return this;
        }

        public Builder aspects(Collection<String> values) {
            Objects.requireNonNull(values, "aspects").forEach(this::aspect);
            return this;
        }

        public Builder anyAspect(Collection<String> values) {
            addNormalized(anyAspects, values, "aspects");
            return this;
        }

        public Builder excludeAspect(String aspect) {
            excludedAspects.add(normalized(aspect, "aspect"));
            return this;
        }

        public Builder label(String label) {
            labels.add(normalized(label, "label"));
            return this;
        }

        public Builder labels(Collection<String> values) {
            Objects.requireNonNull(values, "labels").forEach(this::label);
            return this;
        }

        public Builder anyLabel(Collection<String> values) {
            addNormalized(anyLabels, values, "labels");
            return this;
        }

        public Builder excludeLabel(String label) {
            excludedLabels.add(normalized(label, "label"));
            return this;
        }

        public Builder excludeLabels(Collection<String> values) {
            addNormalized(excludedLabels, values, "labels");
            return this;
        }

        public Builder excludeSpecies(String speciesId) {
            excludedSpecies.add(parseSpecies(speciesId));
            return this;
        }

        public Builder excludeForm(String speciesId, String formId) {
            addForm(excludedForms, speciesId, formId);
            return this;
        }

        public Builder bucket(String bucket) {
            buckets.add(normalized(bucket, "bucket"));
            return this;
        }

        public Builder buckets(Collection<String> values) {
            Objects.requireNonNull(values, "buckets").forEach(this::bucket);
            return this;
        }

        public SpawnFilter build() {
            return new SpawnFilter(this);
        }

        private static void addForm(Map<ResourceLocation, Set<String>> forms, String speciesId, String formId) {
            ResourceLocation species = parseSpecies(speciesId);
            String normalizedForm = Objects.requireNonNull(formId, "formId").trim().toLowerCase(Locale.ROOT);
            forms.computeIfAbsent(species, ignored -> new LinkedHashSet<>()).add(normalizedForm);
        }

        private static void addNormalized(Set<String> target, Collection<String> values, String name) {
            Objects.requireNonNull(values, name).forEach(value -> target.add(normalized(value, name)));
        }
    }

    private static ResourceLocation parseSpecies(String rawId) {
        return PokemonSpeciesResolver.parseId(rawId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid species ID: " + rawId));
    }

    private static String normalized(String value, String name) {
        Objects.requireNonNull(value, name);
        String result = value.trim().toLowerCase(Locale.ROOT);
        if (result.isEmpty()) throw new IllegalArgumentException(name + " must not be blank");
        return result;
    }
}
