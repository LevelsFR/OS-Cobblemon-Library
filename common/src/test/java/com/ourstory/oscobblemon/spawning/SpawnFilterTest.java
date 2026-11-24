package com.ourstory.oscobblemon.spawning;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnFilterTest {
    private static final String VULPIX = "cobblemon:vulpix";

    @Test
    void typeAndFormFiltersRequireEveryPossibleFormToMatch() {
        SpawnCandidate unresolvedVulpix = new SpawnCandidate(VULPIX, List.of(
                new SpawnCandidate.Form("", Set.of("fire"), Set.of("base")),
                new SpawnCandidate.Form("alola", Set.of("ice"), Set.of("regional"))
        ), Set.of(), "common");

        assertFalse(SpawnFilter.builder().type("ice").build().matches(unresolvedVulpix));
        assertFalse(SpawnFilter.builder().form(VULPIX, "alola").build().matches(unresolvedVulpix));

        SpawnCandidate alolanVulpix = new SpawnCandidate(VULPIX, List.of(
                new SpawnCandidate.Form("alola", Set.of("ice"), Set.of("regional"))
        ), Set.of("snowy"), "common");
        assertTrue(SpawnFilter.builder().type("ice").form(VULPIX, "alola").build().matches(alolanVulpix));
        assertFalse(SpawnFilter.builder().type("fire").build().matches(alolanVulpix));
    }

    @Test
    void typeLabelsAndAspectsAreMatchedWithoutDoubleCountingDualTypes() {
        SpawnCandidate dualType = new SpawnCandidate("addon:glacier_bird", List.of(
                new SpawnCandidate.Form("", Set.of("ice", "flying"), Set.of("legendary", "regional"))
        ), Set.of("winter", "rare"), "rare");

        SpawnFilter filter = SpawnFilter.builder()
                .types(Set.of("ICE", "flying"))
                .label("legendary")
                .aspect("winter")
                .bucket("rare")
                .excludeLabel("mythical")
                .build();

        assertTrue(filter.matches(dualType));
        assertFalse(SpawnFilter.builder().excludeLabel("legendary").build().matches(dualType));
        assertFalse(SpawnFilter.builder().aspect("alpha").build().matches(dualType));
        assertFalse(SpawnFilter.builder().bucket("common").build().matches(dualType));
    }

    @Test
    void speciesAndFormExclusionsRejectPossibleMatchesConservatively() {
        SpawnCandidate forms = new SpawnCandidate(VULPIX, List.of(
                new SpawnCandidate.Form("", Set.of("fire"), Set.of()),
                new SpawnCandidate.Form("alola", Set.of("ice"), Set.of())
        ), Set.of(), "common");

        assertFalse(SpawnFilter.builder().excludeSpecies(VULPIX).build().matches(forms));
        assertFalse(SpawnFilter.builder().excludeForm(VULPIX, "alola").build().matches(forms));
        assertTrue(SpawnFilter.builder().excludeForm(VULPIX, "galar").build().matches(forms));
    }
}
