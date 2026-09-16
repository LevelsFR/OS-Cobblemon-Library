# Initial Roadmap

## 1.0 foundation

- Multi-loader repository structure
- Java 21 / Minecraft 1.21.1
- Fabric and NeoForge builds
- Cobblemon 1.8.1 development dependency
- CI builds and GitHub release workflow
- Scope and agent rules

## Next implementation slice

Implement only helpers proven by existing OS mods:

1. Pokémon identity
   - normalized species ID
   - form ID
   - shiny
   - alpha
   - aspects / marks
2. Party and storage
   - player party access
   - PC access
   - UUID lookup
   - party slot lookup
3. Event bridge
   - capture
   - battle victory / faint
   - hatch
   - trade / release where multiple consumers exist
4. Battle helpers
   - wild / trainer / PvP classification
   - participants
   - defeated Pokémon extraction
5. Pokédex
   - seen / caught status

Do not migrate existing released mods just to consume the library. Use the library first in a new Cobblemon project, then migrate older mods during meaningful major updates when there is a real maintenance benefit.
