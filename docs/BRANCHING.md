# Branching Model

OS Cobblemon Library follows a Minecraft compatibility-line model inspired by long-lived library projects such as FTB Library.

## Current line

- `1.21.1/dev`: active development
- `1.21.1/main`: stable code for the Minecraft 1.21.1 line

Feature branches should normally start from `1.21.1/dev` and merge back into it after CI passes.

## Future Minecraft versions

If Cobblemon moves to another Minecraft version, create a new compatibility line rather than filling one branch with version conditionals.

Example:

```text
1.21.1/dev
1.21.1/main
1.22/dev
1.22/main
```

Cobblemon patch/minor updates on the same Minecraft version do not automatically require a new Git branch. Keep the same Minecraft branch while compatibility remains maintainable.

## Releases

Library releases use their own semantic version such as `1.0.0`, `1.1.0` or `2.0.0`. The library version is independent of the Minecraft version.
