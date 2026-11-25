package com.ourstory.oscobblemon.internal;

import com.ourstory.oscobblemon.spawning.SpawnCandidate;
import com.ourstory.oscobblemon.spawning.SpawnFilter;
import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnStackingPolicy;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpawnModifierEvaluatorTest {
    private static final ResourceLocation SOURCE = id("example:source");

    @Test
    void appliesOnlyToEligibleCandidatesWithoutChangingBucketsOrExplicitPercentages() {
        SpawnModifier iceBoost = modifier("example:ice", SpawnFilter.builder().type("ice").build(), 1.75);
        List<SpawnCandidate> ice = List.of(candidate("ice", Set.of("ice"), "common"));
        List<SpawnCandidate> fire = List.of(candidate("fire", Set.of("fire"), "common"));
        List<SpawnCandidate> rareIce = List.of(candidate("ice", Set.of("ice"), "rare"));

        assertEquals(17.5F, SpawnWeightMath.apply(10.0F,
                SpawnModifierEvaluator.logMultiplier(List.of(iceBoost), ice)), 0.0001F);
        assertEquals(10.0F, SpawnWeightMath.apply(10.0F,
                SpawnModifierEvaluator.logMultiplier(List.of(iceBoost), fire)), 0.0001F);
        double iceMultiplier = SpawnModifierEvaluator.logMultiplier(List.of(iceBoost), ice);
        assertEquals(10.0F, SpawnModifierEvaluator.apply(10.0F, 100.0F, iceMultiplier), 0.0001F);

        SpawnModifier commonIceBoost = modifier("example:common_ice",
                SpawnFilter.builder().type("ice").bucket("common").build(), 2.0);
        assertEquals(10.0F, SpawnModifierEvaluator.apply(10.0F, 0.0F,
                SpawnModifierEvaluator.logMultiplier(List.of(commonIceBoost), rareIce)), 0.0001F);
    }

    @Test
    void herdWeightChangesOnlyWhenEveryPossibleMemberMatches() {
        SpawnModifier iceBoost = modifier("example:ice", SpawnFilter.builder().type("ice").build(), 1.5);
        List<SpawnCandidate> allIce = List.of(
                candidate("ice_one", Set.of("ice"), "common"),
                candidate("ice_two", Set.of("ice", "flying"), "common")
        );
        List<SpawnCandidate> mixed = List.of(allIce.getFirst(), candidate("fire", Set.of("fire"), "common"));

        assertEquals(15.0F, SpawnModifierEvaluator.apply(10.0F, 0.0F,
                SpawnModifierEvaluator.logMultiplier(List.of(iceBoost), allIce)), 0.0001F);
        assertEquals(10.0F, SpawnModifierEvaluator.apply(10.0F, 0.0F,
                SpawnModifierEvaluator.logMultiplier(List.of(iceBoost), mixed)), 0.0001F);
        assertEquals(10.0F, SpawnModifierEvaluator.apply(10.0F, 0.0F,
                SpawnModifierEvaluator.logMultiplier(List.of(iceBoost), List.of())), 0.0001F);
    }

    private static SpawnCandidate candidate(String species, Set<String> types, String bucket) {
        return new SpawnCandidate("example:" + species, List.of(new SpawnCandidate.Form("", types, Set.of())),
                Set.of(), bucket);
    }

    private static SpawnModifier modifier(String id, SpawnFilter filter, double multiplier) {
        return SpawnModifier.builder(id(id), SOURCE)
                .filter(filter)
                .multiplier(multiplier)
                .stackingPolicy(SpawnStackingPolicy.max(id("example:default")))
                .build();
    }

    private static ResourceLocation id(String value) {
        return ResourceLocation.parse(value);
    }
}
