# API stability

OS Cobblemon Library follows semantic versioning for its public API.

## Public API

The supported public API consists of documented classes outside the
`com.ourstory.oscobblemon.internal` package.

Public packages currently include:

- `com.ourstory.oscobblemon.pokemon`
- `com.ourstory.oscobblemon.storage`
- `com.ourstory.oscobblemon.event`
- `com.ourstory.oscobblemon.battle`
- `com.ourstory.oscobblemon.pokedex`

## Internal API

Anything under:

```text
com.ourstory.oscobblemon.internal
```

may change or disappear without notice.

Consuming mods should never depend on internal classes.

## Versioning rules

### Patch releases

Patch releases may contain:

- bug fixes
- documentation changes
- compatibility fixes
- implementation changes that preserve the public API

### Minor releases

Minor releases may add:

- new helpers
- new overloads
- new supported Cobblemon integration areas

Existing public API should remain source-compatible.

### Major releases

Breaking changes to the supported public API require a major version.

Minecraft or Cobblemon compatibility changes do not automatically require a
major library version unless they also require a breaking API change.

## Development snapshots

Development branches use versions such as:

```text
1.0.0-SNAPSHOT
```

Snapshot artifacts are intended for development and may change before the
corresponding stable release.
