# Pokémon IV helpers

The `PokemonIVs` helper keeps natural IVs and Hyper Trained battle IVs
explicitly separate.

Cobblemon 1.8 stores Hyper Trained values independently from the Pokémon's
natural IVs, so feature code should choose the correct interpretation instead
of treating both as the same value.

## Natural and effective IVs

```java
int natural = PokemonIVs.natural(pokemon, stat);
int effective = PokemonIVs.effective(pokemon, stat);

boolean hyperTrained =
        PokemonIVs.isHyperTrained(pokemon, stat);
```

- `natural`: the Pokémon's real stored IV
- `effective`: the value used for battle calculations

## Perfect IV counts

```java
int naturalPerfect =
        PokemonIVs.perfectNaturalCount(pokemon);

int effectivePerfect =
        PokemonIVs.perfectEffectiveCount(pokemon);
```

For mechanics based on breeding, capture chains or generated Pokémon, natural
IVs are usually the appropriate value.

For battle-facing displays or calculations, effective IVs may be more useful.

## Guarantee a minimum number of natural perfect IVs

```java
PokemonIVs.ensureMinimumPerfectNatural(
        pokemon,
        5,
        level.getRandom()
);
```

Existing perfect IVs are preserved. Only non-perfect permanent stats are
eligible, and the remaining stats are selected randomly without replacement.

This is useful for mechanics such as progressive capture rewards or special
encounters without duplicating the same IV-selection logic in each mod.

## Set every natural IV to perfect

```java
PokemonIVs.setAllPerfectNatural(pokemon);
```

The helper writes through `Pokemon#setIV`, preserving Cobblemon's normal update
behavior.

## Addon compatibility

Permanent stats are obtained from Cobblemon's active `StatProvider` instead of
hard-coding HP, Attack, Defense, Special Attack, Special Defense and Speed.
