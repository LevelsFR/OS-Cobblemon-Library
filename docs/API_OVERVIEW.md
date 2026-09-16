# API overview

OS Cobblemon Library keeps its public surface intentionally small and focused on
reusable Cobblemon integration.

## Pokémon

### `PokemonIdentity`

Canonical species/form identity, aspects, marks and Alpha compatibility.

Typical uses:

- stable namespaced species IDs
- form detection
- aspect checks
- mark checks
- Alpha checks
- immutable identity snapshots

### `PokemonMatcher`

Immutable reusable matcher built on Cobblemon's native `PokemonProperties`.

Adds recurring constraints that are not already convenient in the native
property syntax:

- Alpha
- aspects
- marks
- labels
- native size categories

Implements `Predicate<Pokemon>`, so it can be passed directly to storage
queries and Java collection APIs.

### `PokemonSpeciesResolver`

Namespace-aware species lookup.

Bare IDs default to the Cobblemon namespace while addon namespaces are
preserved.

### `PokemonCreation`

Command-free creation and spawning through Cobblemon's native property API.

Use it when a mod needs to:

- parse Pokémon properties
- create a `Pokemon`
- create a `PokemonEntity`
- spawn a Pokémon directly into a server level

### `PokemonIVs`

Natural and effective IV inspection.

Includes:

- natural IV values
- Hyper Trained effective values
- perfect IV counts
- IV totals
- minimum guaranteed perfect natural IVs

### `PokemonAbilities`

Classifies the active ability as:

- common
- hidden
- forced
- unknown

It uses Cobblemon's ability-pool coordinates before falling back to template
matching.

### `PokemonTypes`

Current-form type inspection and matching.

### `PokemonLabels`

Generic access to data-driven Cobblemon form labels without hard-coding category
semantics.

### `PokemonSize`

Native size helpers for:

- `PokemonSizeCategory` access and matching
- Cobblemon size-category translation keys
- safe positive scale modifiers
- safe positive effective scale values
- `PokemonEntity` overloads

Invalid, non-finite or non-positive scale values fall back to `1.0F`.

## Entities

### `PokemonEntities`

Read-only bridge helpers for recurring `PokemonEntity` integrations.

Includes:

- underlying `Pokemon` access
- stable Pokémon UUID access
- immutable identity snapshots
- owner UUID inspection
- battle, busy and evolution state inspection
- alive/present checks
- direct `Predicate<Pokemon>` and `PokemonMatcher` matching

The entity API intentionally does not wrap movement, AI, navigation, despawning
or persistence behavior.

## Storage

### `PokemonStorage`

Shared Party and PC access.

Includes:

- direct Party/PC access
- UUID lookup
- party slot lookup
- immutable membership snapshots
- predicate queries
- predicate counts

Queries accept any `Predicate<Pokemon>`, including `PokemonMatcher`.

## Events

### `CobblemonEventHooks`

Java-friendly subscriptions for frequently reused Cobblemon events.

The class intentionally does not mirror every event in `CobblemonEvents`.

### `EventSubscriptionGroup`

Tracks multiple Cobblemon subscriptions and unsubscribes them together.

Useful for reloadable systems, optional features and lifecycle-safe event
registration.

## Battles

### `BattleInspector`

Read-only battle helpers for:

- wild / trainer / PvP classification
- actor filtering
- player UUIDs
- underlying Pokémon
- fainted Pokémon events
- victory winners and losers

The library hides `BattlePokemon` from common consumer use where practical.

### `BattleKind`

High-level battle classification:

- `WILD`
- `TRAINER`
- `PVP`
- `OTHER`

## Pokédex

### `PokedexQueries`

Read-only Pokédex helpers for:

- species seen/caught progress
- form progress
- aspects
- shiny-state knowledge
- global counts and percentages
- individual Pokédex counts and percentages

## Internal package

Anything under:

```text
com.ourstory.oscobblemon.internal
```

is not part of the supported public API and should not be used by consuming
mods.
