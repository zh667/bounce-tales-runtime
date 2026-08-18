#Requires -Version 5.1
$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$out = Join-Path $root "build\verify-classes"
if (Test-Path $out) {
    Remove-Item -Recurse -Force $out
}
New-Item -ItemType Directory -Force -Path $out | Out-Null

$mainSources = Get-ChildItem -Path @(
    (Join-Path $root "game-logic\src\main\java"),
    (Join-Path $root "runtime-pc\src\main\java"),
    (Join-Path $root "runtime-android\src\main\java")
) -Recurse -Filter *.java | ForEach-Object { $_.FullName }

Write-Host "javac --release 17"
& javac --release 17 -encoding UTF-8 -d $out @mainSources
if ($LASTEXITCODE -ne 0) {
    throw "javac failed"
}

Write-Host "run desktop host headless"
& java -cp $out io.github.zh667.bouncetales.pc.DesktopRuntime --headless
if ($LASTEXITCODE -ne 0) {
    throw "desktop host failed"
}

Write-Host "verify.ps1 ok"
