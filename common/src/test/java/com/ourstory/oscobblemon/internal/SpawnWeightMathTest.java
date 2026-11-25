package com.ourstory.oscobblemon.internal;

import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnModifierScope;
import com.ourstory.oscobblemon.spawning.SpawnStackingPolicy;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnWeightMathTest {
    private static final ResourceLocation SOURCE = ResourceLocation.parse("example:source");

    @Test
    void maxUsesStrongestBoostAndReductionAndGroupsMultiply() {
        SpawnModifier boost = modifier("example:boost", 1.75, SpawnStackingPolicy.max(id("example:season")));
        SpawnModifier smallerBoost = modifier("other:boost", 1.25, SpawnStackingPolicy.max(id("example:season")));
        SpawnModifier reduction = modifier("example:reduction", 0.5, SpawnStackingPolicy.max(id("example:season")));
        SpawnModifier otherGroup = modifier("other:group", 1.25, SpawnStackingPolicy.max(id("example:other")));

        double log = SpawnWeightMath.logMultiplier(List.of(boost, smallerBoost, reduction, otherGroup));
        assertEquals(10.0 * 1.75 * 0.5 * 1.25, SpawnWeightMath.apply(10.0F, log), 0.0001);
    }

    @Test
    void multiplicativePolicyCapsOnlyPositiveStackAndIsOrderIndependent() {
        SpawnStackingPolicy policy = SpawnStackingPolicy.multiplicativeCapped(id("example:stack"), 2.0);
        List<SpawnModifier> modifiers = List.of(
                modifier("example:a", 1.75, policy),
                modifier("example:b", 1.25, policy),
                modifier("example:c", 0.5, policy)
        );
        List<SpawnModifier> reversed = new ArrayList<>(modifiers);
        java.util.Collections.reverse(reversed);

        assertEquals(10.0 * 1.75 * 1.25 * 0.5,
                SpawnWeightMath.apply(10.0F, SpawnWeightMath.logMultiplier(modifiers)), 0.0001);
        assertEquals(Double.doubleToLongBits(SpawnWeightMath.logMultiplier(modifiers)),
                Double.doubleToLongBits(SpawnWeightMath.logMultiplier(reversed)));
    }

    @Test
    void weightConversionNeverCreatesZeroOrNonFiniteResults() {
        assertEquals(1.0F, SpawnWeightMath.apply(1.0F, 2_000.0));
        assertEquals(Float.MIN_VALUE, SpawnWeightMath.apply(1.0F, -2_000.0));
        assertEquals(0.0F, SpawnWeightMath.apply(0.0F, 100.0));
        assertEquals(-2.0F, SpawnWeightMath.apply(-2.0F, 100.0));
    }

    @Test
    void seededWeightedSelectionTracksExpectedDistribution() {
        float[] baseline = {10.0F, 10.0F};
        float[] boosted = {SpawnWeightMath.apply(10.0F, SpawnWeightMath.logMultiplier(List.of(
                modifier("example:ice", 1.75, SpawnStackingPolicy.max(id("example:default")))
        ))), 10.0F};
        double expectedBaseline = baseline[0] / (baseline[0] + baseline[1]);
        double expectedBoosted = boosted[0] / (boosted[0] + boosted[1]);
        SplittableRandom random = new SplittableRandom(0x5A17_1CE);
        int baselineFirst = sampleFirst(baseline, random, 100_000);
        int boostedFirst = sampleFirst(boosted, random, 100_000);

        assertTrue(Math.abs(baselineFirst / 100_000.0 - expectedBaseline) < 0.006);
        assertTrue(Math.abs(boostedFirst / 100_000.0 - expectedBoosted) < 0.006);
        assertEquals(17.5F, boosted[0], 0.0001F);
    }

    private static int sampleFirst(float[] weights, SplittableRandom random, int samples) {
        double total = 0;
        for (float weight : weights) total += weight;
        int first = 0;
        for (int i = 0; i < samples; i++) {
            if (random.nextDouble(total) < weights[0]) first++;
        }
        return first;
    }

    private static SpawnModifier modifier(String modifierId, double multiplier, SpawnStackingPolicy policy) {
        return SpawnModifier.builder(id(modifierId), SOURCE)
                .multiplier(multiplier)
                .scope(SpawnModifierScope.global())
                .stackingPolicy(policy)
                .build();
    }

    private static ResourceLocation id(String value) {
        return ResourceLocation.parse(value);
    }
}
