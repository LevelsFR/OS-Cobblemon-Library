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
- Native size translation keys and safe scale access
- Reusable Pokémon matcher built on `PokemonProperties`
- Command-free Pokémon creation and spawning
- Natural and Hyper Trained IV helpers
- Common, Hidden and forced ability classification

### Entities

- Read-only `PokemonEntity` to `Pokemon` bridge
- Stable Pokémon UUID and identity access from entities
- Owner UUID inspection
- Battle, busy and evolution state inspection
- Entity matching through `Predicate<Pokemon>`

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

## 1.1 integration boundaries

- Namespaced persistent Pokémon data with defensive reads and change
  notification
- Capture-rate and calculated-capture hooks
- Accepted and completed evolution hooks
- Battle start and battle gimmick hooks
- Pokémon send, recall, healing and faint hooks
- Pokémon aspect and Pokédex pre/post hooks
- Pokémon entity save/load/save-to-world hooks
- Cobblemon Pokémon spawn hook

The additions remain Java-friendly subscriptions around Cobblemon's public
events. They do not introduce a second persistence format, spawn system or
battle abstraction.

## Later

Additional helpers should be added only when they represent repeated
Cobblemon-specific integration or a clear compatibility boundary.

The library should not grow by wrapping simple Cobblemon getters that are
already stable and convenient to use directly.
