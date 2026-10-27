# Pokémon EV helpers

`PokemonEVs` provides Java-friendly EV inspection and source-aware additions.
It uses Cobblemon's registered permanent stats rather than assuming only the
six vanilla stats.

## Inspect EVs

```java
int attackEvs = PokemonEVs.value(pokemon, Stats.ATTACK);
int totalEvs = PokemonEVs.total(pokemon);
Map<Stat, Integer> evs = PokemonEVs.snapshot(pokemon);
```

The snapshot is immutable and contains one entry for each registered permanent
stat. Missing values are reported as zero.

## Add EVs through Cobblemon

```java
int applied = PokemonEVs.add(pokemon, stat, amount, source);
```

Pass Cobblemon's `EvSource` for the action, and make sure it belongs to the
same Pokémon passed to `add`. Cobblemon applies its per-stat and total limits,
posts the EV pre/post events, and returns the amount actually applied. A
canceled or capped change can return zero; a negative amount can reduce an
existing value. The library does not define a separate EV formula or limit.
