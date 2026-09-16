# Releasing

Stable releases are cut from the current Minecraft compatibility line's
`main` branch.

For Minecraft 1.21.1:

```text
1.21.1/dev
1.21.1/main
```

## 1. Validate development

On `1.21.1/dev`:

```powershell
git pull
.\gradlew.bat clean build
powershell -ExecutionPolicy Bypass -File .\scripts\publish-local.ps1
```

Both loader builds and Maven publication must succeed.

## 2. Set the stable version

Change:

```properties
mod_version=1.0.0-SNAPSHOT
```

to the intended release version, for example:

```properties
mod_version=1.0.0
```

Update `CHANGELOG.md` so the release contents are final.

## 3. Promote to the stable branch

Move the validated release commit to `1.21.1/main` through the normal review
or merge workflow.

The stable branch should never carry a `-SNAPSHOT` version.

## 4. Tag the release

Create a tag matching `mod_version` exactly:

```text
v1.0.0
```

The release workflow validates that:

- the project version is not a snapshot
- the tag version matches `mod_version`
- the complete multi-loader build succeeds

Only the final Fabric and NeoForge JARs are attached to the GitHub release.

## 5. Resume development

After release, return `1.21.1/dev` to the next snapshot version, for example:

```properties
mod_version=1.1.0-SNAPSHOT
```

Do not overwrite an already published stable version with new development
artifacts.
