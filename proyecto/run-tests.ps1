param(
    [string]$JavaHome = $env:JAVA_HOME,
    [ValidateSet('chrome', 'firefox', 'edge')][string]$Browser = 'chrome',
    [switch]$ShowBrowser,
    [switch]$UnitOnly
)
$ErrorActionPreference = 'Stop'
if (-not $JavaHome) { throw 'Indica -JavaHome con la carpeta de un JDK 11.' }
$javaExecutable = Join-Path $JavaHome 'bin/java.exe'
$compilerExecutable = Join-Path $JavaHome 'bin/javac.exe'
if (-not (Test-Path -LiteralPath $javaExecutable) -or -not (Test-Path -LiteralPath $compilerExecutable)) { throw 'La carpeta indicada no contiene un JDK.' }
$version = (& $javaExecutable -version 2>&1 | Out-String)
if ($LASTEXITCODE -ne 0 -or $version -notmatch 'version "11[.]') { throw 'Este proyecto requiere JDK 11. No se modificó el Java del equipo.' }
if (-not $UnitOnly) {
    $manifest = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'export-manifest.json') -Raw | ConvertFrom-Json
    foreach ($name in $manifest.secretEnvironmentVariables) {
        if ([string]::IsNullOrEmpty([Environment]::GetEnvironmentVariable($name, 'Process'))) { throw "Falta la variable de entorno $name. Consulta README; no escribas contraseñas en el código." }
    }
}
$previousJava = $env:JAVA_HOME
$previousPath = $env:Path
Push-Location -LiteralPath $PSScriptRoot
try {
    $env:JAVA_HOME = $JavaHome
    $env:Path = "$(Join-Path $JavaHome 'bin');$previousPath"
    $arguments = @('test', '--no-daemon', '--console=plain', "-Dbrowser=$Browser", "-Dheadless=$(-not $ShowBrowser)")
    if ($UnitOnly) { $arguments += @('--tests', 'com.ultimate.qa.pages.RecordedPageTest', '--tests', 'com.ultimate.qa.support.TestDataTest', '--tests', 'com.ultimate.qa.support.DataValuesTest') }
    & (Join-Path $PSScriptRoot 'gradlew.bat') @arguments
    if ($LASTEXITCODE -ne 0) { throw "Las pruebas fallaron (código $LASTEXITCODE). Consulta build/reports/tests/test/index.html." }
} finally {
    $env:JAVA_HOME = $previousJava
    $env:Path = $previousPath
    Pop-Location
}
