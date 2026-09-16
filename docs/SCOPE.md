# Scope

OS Cobblemon Library is focused on reusable Cobblemon integration shared across multiple mods.

## In scope

- Pokémon identity and reusable property access
- Species, forms, aspects and marks
- Native Pokémon types, size categories and scale inspection
- Read-only `PokemonEntity` to `Pokemon` bridge helpers
- Party and PC access
- Pokémon lookup helpers
- Cobblemon event integration
- Battle inspection and classification
- Pokédex queries
- Small compatibility adapters that isolate Cobblemon API changes

## Out of scope

- Gameplay rules specific to a single mod
- Progression systems
- Custom balancing formulas
- Mod-specific user interfaces
- Mod-specific spawning systems
- Entity AI, movement, navigation or despawn systems
- Leaderboard scoring
- Generic Minecraft configuration frameworks
- Generic networking frameworks
- Generic permission systems
- Generic registry utilities

The goal is to keep the library small, predictable and useful without turning it into a general-purpose dependency bundle.
