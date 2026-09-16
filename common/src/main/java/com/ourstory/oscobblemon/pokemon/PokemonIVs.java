package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Reusable IV inspection and mutation helpers.
 *
 * <p>Cobblemon 1.8 stores natural IVs and Hyper Trained battle IVs separately.
 * This class keeps those two concepts explicit so consuming mods do not
 * accidentally treat Hyper Training as a natural perfect IV.</p>
 */
public final class PokemonIVs {
    private PokemonIVs() {
    }

    /**
     * Returns the currently registered permanent stats.
     *
     * <p>The Cobblemon stat provider is used instead of hard-coding the six
     * vanilla stats so addons can participate when they extend the provider.</p>
     */
    public static List<Stat> permanentStats() {
        return List.copyOf(
                Cobblemon.INSTANCE.getStatProvider().ofType(Stat.Type.PERMANENT)
        );
    }

    /**
     * Returns the natural IV for a stat.
     */
    public static int natural(Pokemon pokemon, Stat stat) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(stat, "stat");
        return pokemon.getIvs().getOrDefault(stat);
    }

    /**
     * Returns the IV used for battle calculations, including Hyper Training.
     */
    public static int effective(Pokemon pokemon, Stat stat) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(stat, "stat");
        return pokemon.getIvs().getEffectiveBattleIV(stat);
    }

    /**
     * Returns whether the supplied stat is Hyper Trained.
     */
    public static boolean isHyperTrained(Pokemon pokemon, Stat stat) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(stat, "stat");
        return pokemon.getIvs().isHyperTrained(stat);
    }

    /**
     * Returns an immutable snapshot of natural IVs.
     */
    public static Map<Stat, Integer> naturalSnapshot(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        LinkedHashMap<Stat, Integer> result = new LinkedHashMap<>();
        for (Stat stat : permanentStats()) {
            result.put(stat, natural(pokemon, stat));
        }
        return Map.copyOf(result);
    }

    /**
     * Returns an immutable snapshot of effective battle IVs.
     */
    public static Map<Stat, Integer> effectiveSnapshot(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        LinkedHashMap<Stat, Integer> result = new LinkedHashMap<>();
        for (Stat stat : permanentStats()) {
            result.put(stat, effective(pokemon, stat));
        }
        return Map.copyOf(result);
    }

    /**
     * Counts naturally perfect IVs.
     */
    public static int perfectNaturalCount(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        int count = 0;
        for (Stat stat : permanentStats()) {
            if (natural(pokemon, stat) == IVs.MAX_VALUE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts perfect effective battle IVs, including Hyper Training.
     */
    public static int perfectEffectiveCount(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        int count = 0;
        for (Stat stat : permanentStats()) {
            if (effective(pokemon, stat) == IVs.MAX_VALUE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns whether the Pokémon has at least the requested number of naturally
     * perfect IVs.
     */
    public static boolean hasAtLeastPerfectNatural(Pokemon pokemon, int minimum) {
        validateMinimum(minimum);
        return perfectNaturalCount(pokemon) >= minimum;
    }

    /**
     * Returns whether the Pokémon has at least the requested number of perfect
     * effective battle IVs, including Hyper Training.
     */
    public static boolean hasAtLeastPerfectEffective(Pokemon pokemon, int minimum) {
        validateMinimum(minimum);
        return perfectEffectiveCount(pokemon) >= minimum;
    }

    /**
     * Returns the sum of all natural permanent IVs.
     */
    public static int naturalTotal(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        int total = 0;
        for (Stat stat : permanentStats()) {
            total += natural(pokemon, stat);
        }
        return total;
    }

    /**
     * Returns the sum of all effective permanent IVs, including Hyper Training.
     */
    public static int effectiveTotal(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        int total = 0;
        for (Stat stat : permanentStats()) {
            total += effective(pokemon, stat);
        }
        return total;
    }

    /**
     * Ensures that a Pokémon has at least the requested number of naturally
     * perfect IVs.
     *
     * <p>Only currently non-perfect permanent stats are eligible. Existing
     * perfect IVs are preserved. Stats are selected uniformly without
     * replacement using the supplied random source.</p>
     *
     * @return the number of IVs changed to perfect
     */
    public static int ensureMinimumPerfectNatural(
            Pokemon pokemon,
            int minimum,
            RandomSource random
    ) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(random, "random");
        validateMinimum(minimum);

        List<Stat> stats = permanentStats();
        if (minimum > stats.size()) {
            throw new IllegalArgumentException(
                    "minimum perfect IVs cannot exceed registered permanent stat count: "
                            + stats.size()
            );
        }

        int current = perfectNaturalCount(pokemon);
        int needed = minimum - current;
        if (needed <= 0) {
            return 0;
        }

        List<Stat> candidates = new ArrayList<>();
        for (Stat stat : stats) {
            if (natural(pokemon, stat) != IVs.MAX_VALUE) {
                candidates.add(stat);
            }
        }

        shuffle(candidates, random);

        int changed = Math.min(needed, candidates.size());
        for (int i = 0; i < changed; i++) {
            pokemon.setIV(candidates.get(i), IVs.MAX_VALUE);
        }

        return changed;
    }

    /**
     * Sets every registered permanent natural IV to perfect.
     *
     * @return the number of IVs that were changed
     */
    public static int setAllPerfectNatural(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        int changed = 0;
        for (Stat stat : permanentStats()) {
            if (natural(pokemon, stat) != IVs.MAX_VALUE) {
                pokemon.setIV(stat, IVs.MAX_VALUE);
                changed++;
            }
        }
        return changed;
    }

    private static void validateMinimum(int minimum) {
        if (minimum < 0) {
            throw new IllegalArgumentException("minimum must be >= 0");
        }
    }

    private static <T> void shuffle(List<T> values, RandomSource random) {
        for (int i = values.size() - 1; i > 0; i--) {
            int swapWith = random.nextInt(i + 1);
            T value = values.get(i);
            values.set(i, values.get(swapWith));
            values.set(swapWith, value);
        }
    }
}
