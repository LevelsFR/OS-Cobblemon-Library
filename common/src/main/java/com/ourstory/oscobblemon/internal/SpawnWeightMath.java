package com.ourstory.oscobblemon.internal;

import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnStackingPolicy;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/** Deterministic modifier aggregation and finite Float weight conversion. */
final class SpawnWeightMath {
    private SpawnWeightMath() {
    }

    static double logMultiplier(List<SpawnModifier> matching) {
        if (matching.isEmpty()) return 0.0;

        TreeMap<ResourceLocation, GroupValues> groups = new TreeMap<>(Comparator.comparing(ResourceLocation::toString));
        matching.stream()
                .sorted(Comparator.comparing(modifier -> modifier.id().toString()))
                .forEach(modifier -> {
                    SpawnStackingPolicy policy = modifier.stackingPolicy();
                    groups.computeIfAbsent(policy.group(), ignored -> new GroupValues(policy))
                            .add(modifier.multiplier());
                });

        double result = 0.0;
        for (GroupValues group : groups.values()) result += group.logMultiplier();
        return result;
    }

    static float apply(float weight, double logMultiplier) {
        if (!(weight > 0.0F) || !Float.isFinite(weight) || logMultiplier == 0.0) return weight;
        double result = Math.log(weight) + logMultiplier;
        if (result >= Math.log(Float.MAX_VALUE)) return weight;
        if (result <= Math.log(Float.MIN_VALUE)) return Float.MIN_VALUE;
        return (float) Math.exp(result);
    }

    private static final class GroupValues {
        private final SpawnStackingPolicy policy;
        private double maximumBoost = 1.0;
        private double strongestReduction = 1.0;
        private double logProduct;

        private GroupValues(SpawnStackingPolicy policy) {
            this.policy = policy;
        }

        private void add(double multiplier) {
            if (policy.mode() == SpawnStackingPolicy.Mode.MAX) {
                if (multiplier > 1.0) maximumBoost = Math.max(maximumBoost, multiplier);
                else strongestReduction = Math.min(strongestReduction, multiplier);
            } else {
                logProduct += Math.log(multiplier);
            }
        }

        private double logMultiplier() {
            if (policy.mode() == SpawnStackingPolicy.Mode.MAX) {
                return Math.log(maximumBoost) + Math.log(strongestReduction);
            }
            return Math.min(logProduct, Math.log(policy.maximumMultiplier()));
        }
    }
}
