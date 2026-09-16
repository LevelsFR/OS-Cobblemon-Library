$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$gradlew = Join-Path $root "gradlew.bat"
$propertiesFile = Join-Path $root "gradle.properties"

if (-not (Test-Path $gradlew)) {
    throw "Gradle wrapper not found. Run scripts\bootstrap-gradle.ps1 first."
}

$version = Get-Content $propertiesFile |
    Where-Object { $_ -match '^mod_version=' } |
    ForEach-Object { ($_ -split '=', 2)[1].Trim() } |
    Select-Object -First 1

if (-not $version) {
    throw "mod_version was not found in gradle.properties."
}

Push-Location $root
try {
    Write-Host "[OS Cobblemon Library] Publishing $version to Maven Local..."
    & $gradlew publishAllToMavenLocal
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
finally {
    Pop-Location
}

Write-Host ""
Write-Host "[OS Cobblemon Library] Published:"
Write-Host "  com.ourstory:os-cobblemon-library-common:$version"
Write-Host "  com.ourstory:os-cobblemon-library-fabric:$version"
Write-Host "  com.ourstory:os-cobblemon-library-neoforge:$version"
