# Roadmap

## 1.0 foundation

- Multi-loader project structure
- Java 21 and Minecraft 1.21.1
- Fabric and NeoForge builds
- Cobblemon 1.8.x compatibility line, starting at 1.8.1
- Automated build, Maven publication verification and release workflows
- Clear public API scope and semantic versioning model

## 1.0 public API

### Pokémon

- Canonical namespaced species identifiers
- Species resolution
- Standard and alternate form identity
- Shiny and Alpha state
- Aspects and marks
- Data-driven labels
- Current-form elemental types
- Native size-category matching
- Reusable Pokémon matcher built on `PokemonProperties`
- Command-free Pokémon creation and spawning
- Natural and Hyper Trained IV helpers
- Common, Hidden and forced ability classification

### Storage

- Player Party access
- PC access
- Pokémon UUID lookup
- Party slot lookup
- Immutable storage snapshots
- Predicate and matcher searches
- Predicate and matcher counts

### Events

- Capture events
- Battle victory, faint and flee events
- Pokédex updates
- Hatch events
- Trade and release events
- Nickname, level-up and experience events
- Grouped subscription lifecycle management

### Battles

- Wild, trainer and PvP classification
- Actor and player inspection
- Normal `Pokemon` extraction from battle wrappers
- Fainted Pokémon extraction
- Victory winner and loser inspection

### Pokédex

- Species seen and caught status
- Form progress
- Known aspects and shiny states
- Global and per-dex counts and percentages

## Later

Additional helpers should be added only when they represent repeated
Cobblemon-specific integration or a clear compatibility boundary.

The library should not grow by wrapping simple Cobblemon getters that are
already stable and convenient to use directly.
