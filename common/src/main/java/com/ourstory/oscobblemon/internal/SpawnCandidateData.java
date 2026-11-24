package com.ourstory.oscobblemon.internal;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.ourstory.oscobblemon.pokemon.PokemonIdentity;
import com.ourstory.oscobblemon.pokemon.PokemonLabels;
import com.ourstory.oscobblemon.pokemon.PokemonSpeciesResolver;
import com.ourstory.oscobblemon.pokemon.PokemonTypes;
import com.ourstory.oscobblemon.spawning.SpawnCandidate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Converts Cobblemon's spawn definitions into short-lived filter data. */
public final class SpawnCandidateData {
    private SpawnCandidateData() {
    }

    public static List<SpawnCandidate> from(SpawnDetail detail) {
        if (detail instanceof PokemonSpawnDetail pokemon) {
            return profile(pokemon.getPokemon(), detail.getBucket()).map(List::of).orElseGet(List::of);
        }
        if (detail instanceof PokemonHerdSpawnDetail herd) {
            List<SpawnCandidate> candidates = new ArrayList<>();
            for (PokemonHerdSpawnDetail.Herdable member : herd.getHerdablePokemon()) {
                Optional<SpawnCandidate> candidate = profile(member.getPokemon(), detail.getBucket());
                if (candidate.isEmpty()) return List.of();
                candidates.add(candidate.get());
            }
            return List.copyOf(candidates);
        }
        return List.of();
    }

    private static Optional<SpawnCandidate> profile(PokemonProperties properties, String bucket) {
        if (properties.getSpecies() == null || properties.getSpecies().equalsIgnoreCase("random")) return Optional.empty();
        Optional<Species> resolved = PokemonSpeciesResolver.resolve(properties.getSpecies());
        if (resolved.isEmpty()) return Optional.empty();
        Species species = resolved.get();

        LinkedHashSet<FormData> forms = new LinkedHashSet<>();
        String explicitForm = properties.getForm();
        if (explicitForm != null) {
            String normalized = explicitForm.trim().toLowerCase(Locale.ROOT);
            species.getForms().stream()
                    .filter(form -> PokemonIdentity.formId(species, form).equals(normalized))
                    .findFirst()
                    .ifPresent(forms::add);
        }
        if (forms.isEmpty()) {
            // An absent or unresolved form is ambiguous; never assume it is the standard form.
            forms.addAll(species.getForms());
            forms.add(species.getStandardForm());
        }

        List<SpawnCandidate.Form> possibleForms = forms.stream()
                .map(form -> new SpawnCandidate.Form(
                        PokemonIdentity.formId(species, form),
                        PokemonTypes.typeNames(form),
                        PokemonLabels.labels(form)
                ))
                .toList();

        return Optional.of(new SpawnCandidate(
                PokemonIdentity.speciesId(species),
                possibleForms,
                PokemonIdentity.aspects(properties),
                bucket
        ));
    }
}
