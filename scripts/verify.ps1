#Requires -Version 5.1
$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$out = Join-Path $root "build\verify-classes"
if (Test-Path $out) {
    Remove-Item -Recurse -Force $out
}
New-Item -ItemType Directory -Force -Path $out | Out-Null

$mainSources = @(
    (Join-Path $root "game-logic\src\main\java\io\github\zh667\bouncetales\logic\GameLogic.java"),
    (Join-Path $root "game-logic\src\main\java\io\github\zh667\bouncetales\logic\HostTarget.java"),
    (Join-Path $root "runtime-pc\src\main\java\io\github\zh667\bouncetales\pc\DesktopRuntime.java"),
    (Join-Path $root "runtime-android\src\main\java\io\github\zh667\bouncetales\android\AndroidRuntime.java")
)

Write-Host "javac --release 17"
& javac --release 17 -encoding UTF-8 -d $out $mainSources
if ($LASTEXITCODE -ne 0) {
    throw "javac failed"
}

Write-Host "run desktop stub"
& java -cp $out io.github.zh667.bouncetales.pc.DesktopRuntime
if ($LASTEXITCODE -ne 0) {
    throw "desktop stub failed"
}

Write-Host "verify.ps1 ok"
