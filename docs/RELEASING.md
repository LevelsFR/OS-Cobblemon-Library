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

Update `CHANGELOG.md` so the release contents are final. Keep every previous
release entry from 1.0.0 onward, newest first. Prepare the version-specific
release notes separately if needed. Do not replace historical entries with a
summary of the newest release.

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
Verify the version embedded in both JARs matches the tag, and do not create
another release for an already published tag.

Pushing a v* tag triggers this workflow and creates a public GitHub binary release. Do not push a tag solely to mark an artifact already published on CurseForge or Modrinth; create a GitHub release only when a new binary publication is intended.

## 5. Synchronize public documentation and distribution

- Verify the release download links and the declared compatibility range in `README.md`, `docs/GETTING_STARTED.md`, `docs/PROJECT_GUIDE.md` and the GitHub Pages wiki under `site/`.
- Keep `CHANGELOG.md` cumulative, add a short release-specific summary if useful, and ensure the wiki's historical release list does not skip versions.
- Check CurseForge and Modrinth separately: the GitHub release workflow does not automatically prove that either platform has received the new JARs. Do not advertise a platform version until it is present there.
- Changes under `site/` on `1.21.1/main` trigger the Pages deployment workflow. Verify the deployed site after that workflow completes.
- Keep any known runtime-verification limitations in the documentation even when the CI build passes.

## 6. Resume development

After releasing library version `1.6.0`, return `1.21.1/dev` to the next snapshot version:

```properties
mod_version=1.7.0-SNAPSHOT
```

Do not overwrite an already published stable version with new development
artifacts.
