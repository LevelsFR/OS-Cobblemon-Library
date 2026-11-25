package com.ourstory.oscobblemon.internal;

import com.ourstory.oscobblemon.spawning.SpawnCandidate;
import com.ourstory.oscobblemon.spawning.SpawnModifier;

import java.util.ArrayList;
import java.util.List;

/** Pure filter-to-weight evaluation shared by the Cobblemon bridge and unit tests. */
final class SpawnModifierEvaluator {
    private SpawnModifierEvaluator() {
    }

    static double logMultiplier(List<SpawnModifier> modifiers, List<SpawnCandidate> candidates) {
        if (modifiers.isEmpty() || candidates.isEmpty()) return 0.0;

        List<SpawnModifier> matching = new ArrayList<>();
        for (SpawnModifier modifier : modifiers) {
            boolean matchesEveryCandidate = true;
            for (SpawnCandidate candidate : candidates) {
                if (!modifier.filter().matches(candidate)) {
                    matchesEveryCandidate = false;
                    break;
                }
            }
            if (matchesEveryCandidate) matching.add(modifier);
        }
        return SpawnWeightMath.logMultiplier(matching);
    }

    static float apply(float weight, float percentage, double logMultiplier) {
        if (!(weight > 0.0F) || !Float.isFinite(weight) || percentage > 0.0F) return weight;
        return SpawnWeightMath.apply(weight, logMultiplier);
    }
}
