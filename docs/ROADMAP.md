# Roadmap

## 1.0 foundation

- Multi-loader project structure
- Java 21 and Minecraft 1.21.1
- Fabric and NeoForge builds
- Cobblemon 1.8.1+ compatibility baseline
- Automated build and release workflows
- Clear project scope and versioning model

## Initial library modules

### Pokémon

- Normalized species identifiers
- Form identifiers
- Shiny state
- Alpha detection
- Aspects and marks
- Reusable display-name helpers

### Storage

- Player party access
- PC access
- Pokémon UUID lookup
- Party slot lookup

### Events

- Capture events
- Battle victory and faint events
- Hatch events
- Trade and release events where reusable handling is justified

### Battles

- Wild, trainer and PvP classification
- Participant inspection
- Reusable defeated-Pokémon extraction

### Pokédex

- Seen status
- Caught status

## Later

Additional modules should be added only when they represent repeated Cobblemon-specific integration or a clear compatibility boundary.
