# Pokémon ability helpers

`PokemonAbilities` classifies the active ability without relying on display
names or a simple template-name lookup.

## Current ability kind

```java
PokemonAbilityKind kind =
        PokemonAbilities.kind(pokemon);
```

Possible values:

- `COMMON`
- `HIDDEN`
- `FORCED`
- `UNKNOWN`

Convenience checks are also available:

```java
boolean hidden =
        PokemonAbilities.hasHiddenAbility(pokemon);

boolean common =
        PokemonAbilities.hasCommonAbility(pokemon);

boolean forced =
        PokemonAbilities.hasForcedAbility(pokemon);
```

## Why priority and index matter

Cobblemon 1.8 stores the active non-forced ability's last known position inside
the form's `AbilityPool`.

The helper first resolves that exact `priority + index` coordinate and verifies
the selected template. This is safer than treating every matching ability name
as hidden.

If the stored coordinates cannot be resolved, the helper performs a conservative
fallback. If the same template maps to conflicting ability types, the result is
`UNKNOWN` instead of guessing.

## Can the current form have a Hidden Ability?

```java
boolean supportsHidden =
        PokemonAbilities.canHaveHiddenAbility(pokemon);
```

This checks the current form and current aspects, including conditional
`PotentialAbility` entries.

The library does not force or reroll abilities here. Ability mutation remains an
explicit gameplay decision for the consuming mod.
