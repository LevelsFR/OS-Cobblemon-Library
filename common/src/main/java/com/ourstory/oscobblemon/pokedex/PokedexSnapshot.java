package com.ourstory.oscobblemon.pokedex;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Immutable copy of the species and currently registered form data in a Pokédex manager.
 */
public record PokedexSnapshot(Map<ResourceLocation, SpeciesEntry> entries) {
    public PokedexSnapshot {
        entries = Map.copyOf(Objects.requireNonNull(entries, "entries"));
    }

    public Optional<SpeciesEntry> findSpecies(ResourceLocation speciesId) {
        Objects.requireNonNull(speciesId, "speciesId");
        return Optional.ofNullable(entries.get(speciesId));
    }

    /** Immutable progress captured for one registered species. */
    public record SpeciesEntry(
            PokedexEntryProgress progress,
            Set<String> aspects,
            Map<String, FormEntry> forms
    ) {
        public SpeciesEntry {
            Objects.requireNonNull(progress, "progress");
            aspects = Set.copyOf(Objects.requireNonNull(aspects, "aspects"));
            forms = Map.copyOf(Objects.requireNonNull(forms, "forms"));
        }
    }

    /** Immutable progress captured for one registered form. */
    public record FormEntry(PokedexEntryProgress progress, Set<String> seenShinyStates) {
        public FormEntry {
            Objects.requireNonNull(progress, "progress");
            seenShinyStates = Set.copyOf(Objects.requireNonNull(seenShinyStates, "seenShinyStates"));
        }
    }
}
