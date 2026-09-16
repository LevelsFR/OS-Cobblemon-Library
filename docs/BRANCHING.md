# Branching Model

OS Cobblemon Library uses separate compatibility lines for Minecraft versions.

## Current line

- `1.21.1/dev`: active development
- `1.21.1/main`: stable code for Minecraft 1.21.1

Feature work should normally start from `1.21.1/dev` and return to that branch after validation.

## Future Minecraft versions

When Cobblemon moves to a new Minecraft version, a new compatibility line can be created instead of filling one branch with version-specific conditionals.

Example:

```text
1.21.1/dev
1.21.1/main
1.22/dev
1.22/main
```

Cobblemon updates on the same Minecraft version do not automatically require a new branch. The existing compatibility line can continue while support remains maintainable.

## Releases

Library releases use semantic versioning such as `1.0.0`, `1.1.0` and `2.0.0`.

The library version is independent of the Minecraft version.
