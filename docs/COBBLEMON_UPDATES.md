# Updating for a new Cobblemon version

OS Cobblemon Library is intended to absorb repeated Cobblemon API migrations in
one place.

When Cobblemon updates, validate the library before migrating consuming mods.

## 1. Update dependency versions

Start with `gradle.properties`:

- Cobblemon
- Fabric loader
- Fabric API
- Fabric Language Kotlin
- NeoForge
- Kotlin for Forge when needed

Keep Java and Minecraft versions aligned with the targeted Cobblemon release.

## 2. Build the full multi-loader project

```powershell
.\gradlew.bat clean build
```

On Windows, if an Architectury transform JAR becomes locked:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\rebuild-windows.ps1
```

Both Fabric and NeoForge must compile before a compatibility line is considered
valid.

## 3. Verify compatibility boundaries

Review these areas first because they intentionally sit close to Cobblemon API
surfaces.

### Pokémon properties and spawning

Check:

- `PokemonProperties`
- property parsing
- `PokemonEntity` creation
- direct world spawning
- spawn bounds and initialization behavior

Relevant library class:

```text
PokemonCreation
```

### Species and forms

Check:

- species registry lookup
- resource identifiers
- standard form behavior
- form-only identifiers
- labels

Relevant classes:

```text
PokemonIdentity
PokemonSpeciesResolver
PokemonLabels
```

### Storage

Check:

- `PlayerPartyStore`
- `PCStore`
- UUID lookup
- party slot access
- storage manager entry points

Relevant class:

```text
PokemonStorage
```

### Events

Compare `CobblemonEvents` names and event payload types.

Relevant classes:

```text
CobblemonEventHooks
EventSubscriptionGroup
```

Do not automatically wrap every newly added Cobblemon event. Add convenience
hooks only when they are reused or provide a useful compatibility boundary.

### Battles

Check:

- `PokemonBattle`
- PvW / PvN / PvP flags
- actor types
- `BattlePokemon`
- faint and victory event payloads

Relevant classes:

```text
BattleInspector
BattleKind
```

### Pokédex

Check:

- `PokedexManager`
- `PokedexEntryProgress`
- species/form records
- Pokédex value calculators

Relevant class:

```text
PokedexQueries
```

Pay particular attention to enum renames. For example, Cobblemon 1.8 uses
`UNREGISTERED`, `SEEN` and `OWNED` for Pokédex progress.

Client Pokédex and party data are separate read paths from the server stores.
The library's client helpers target `ClientPokedexManager` and `ClientParty`;
keep those classes in client-only call paths. In the 1.8.1 source, incremental
Pokédex packets update the client manager directly and do not expose a public
client update observable. Server `PokemonStore` exposes
`getAnyChangeObservable()`, which signals save-worthy store and Pokémon
changes; it does not include the changed Pokémon UUID. `ClientParty` has no
matching observable, so do not infer client invalidation from server gain or
release events.

Poké Ball IDs come from Cobblemon's `PokeBall.name`; item display names come
from the ball item's translated name component. Keep rendering in consumers.

### IVs and abilities

Check:

- IV and Hyper Training storage
- permanent stat provider
- ability pool priority/index behavior
- hidden ability type representation

Relevant classes:

```text
PokemonIVs
PokemonAbilities
```

### Persistent Pokémon data

When changing the Cobblemon version, re-check `Pokemon.persistentData` and
`Pokemon.onChange(...)`. Codec-backed values use Minecraft `DynamicOps<Tag>`;
registry-aware codecs need registry-aware operations rather than the default
`NbtOps`.

## 4. Re-run local Maven publishing

After the library builds:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\publish-local.ps1
```

Then validate one or two consuming mods against the updated snapshot before
migrating the rest.

## 5. Create a new Minecraft compatibility line when needed

A Cobblemon patch or minor update on the same Minecraft version does not
automatically require a new branch.

Create a new compatibility line when the Minecraft version changes or when
maintaining both targets in one branch would require excessive version-specific
conditionals.
