$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$version = "8.14.3"
$bootstrap = Join-Path $root ".gradle-bootstrap"
$zip = Join-Path $bootstrap "gradle-$version-bin.zip"
$checksumFile = "$zip.sha256"
$dist = Join-Path $bootstrap "gradle-$version"

New-Item -ItemType Directory -Force -Path $bootstrap | Out-Null

if (-not (Test-Path $dist)) {
    Write-Host "[OS Cobblemon Library] Downloading Gradle $version..."
    Invoke-WebRequest "https://services.gradle.org/distributions/gradle-$version-bin.zip" -OutFile $zip
    Invoke-WebRequest "https://services.gradle.org/distributions/gradle-$version-bin.zip.sha256" -OutFile $checksumFile

    $expected = (Get-Content $checksumFile -Raw).Trim().ToLowerInvariant()
    $actual = (Get-FileHash $zip -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($expected -ne $actual) {
        throw "Gradle archive checksum mismatch."
    }

    Expand-Archive -Path $zip -DestinationPath $bootstrap -Force
}

$gradle = Join-Path $dist "bin\gradle.bat"
if (-not (Test-Path $gradle)) {
    throw "Gradle bootstrap failed: $gradle was not found."
}

Push-Location $root
try {
    & $gradle wrapper --gradle-version $version --distribution-type bin
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
finally {
    Pop-Location
}

Write-Host "[OS Cobblemon Library] Gradle wrapper ready."
