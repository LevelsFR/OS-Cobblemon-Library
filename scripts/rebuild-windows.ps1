$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$gradlew = Join-Path $root "gradlew.bat"
$commonBuild = Join-Path $root "common\build"

if (-not (Test-Path $gradlew)) {
    throw "Gradle wrapper not found. Run scripts\bootstrap-gradle.ps1 first."
}

Push-Location $root
try {
    Write-Host "[OS Cobblemon Library] Stopping Gradle daemons..."
    & $gradlew --stop | Out-Host

    if (Test-Path $commonBuild) {
        Write-Host "[OS Cobblemon Library] Clearing common build output..."
        Remove-Item -Recurse -Force $commonBuild
    }

    Write-Host "[OS Cobblemon Library] Building without parallel project execution..."
    & $gradlew build --no-parallel
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
finally {
    Pop-Location
}

Write-Host "[OS Cobblemon Library] Build successful."
