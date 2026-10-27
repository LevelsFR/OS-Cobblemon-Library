package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.stats.EvSource;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Java-friendly inspection and source-aware EV changes for Cobblemon Pokémon. */
public final class PokemonEVs {
    private PokemonEVs() {
    }

    /** Returns the currently registered permanent stats. */
    public static List<Stat> permanentStats() {
        return List.copyOf(Cobblemon.INSTANCE.getStatProvider().ofType(Stat.Type.PERMANENT));
    }

    /** Returns the current EV value for a stat. */
    public static int value(Pokemon pokemon, Stat stat) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(stat, "stat");
        return pokemon.getEvs().getOrDefault(stat);
    }

    /** Returns an immutable snapshot of current EV values. */
    public static Map<Stat, Integer> snapshot(Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        LinkedHashMap<Stat, Integer> result = new LinkedHashMap<>();
        for (Stat stat : permanentStats()) {
            result.put(stat, value(pokemon, stat));
        }
        return Map.copyOf(result);
    }

    /** Returns the sum of the Pokémon's current permanent-stat EVs. */
    public static int total(Pokemon pokemon) {
        int total = 0;
        for (int value : snapshot(pokemon).values()) {
            total += value;
        }
        return total;
    }

    /**
     * Applies an EV change through Cobblemon's source-aware API.
     *
     * @return the change Cobblemon applied after its limits and EV events
     */
    public static int add(Pokemon pokemon, Stat stat, int amount, EvSource source) {
        Objects.requireNonNull(pokemon, "pokemon");
        Objects.requireNonNull(stat, "stat");
        Objects.requireNonNull(source, "source");
        if (source.getPokemon() != pokemon) {
            throw new IllegalArgumentException("source belongs to a different Pokémon");
        }
        return pokemon.getEvs().add(stat, amount, source);
    }
}
