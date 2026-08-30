$ErrorActionPreference = "Continue"
$rootDir = (Get-Location).Path
$antlrJar = "$rootDir\dependencies\antlr-4.13.2-complete.jar"
$junitJar = "$rootDir\dependencies\junit-platform-console-standalone.jar"
$srcDir = "$rootDir\src\main\java"
$testDir = "$rootDir\src\test\java"

# 1. Download JUnit if missing
if (-not (Test-Path $junitJar)) {
    Write-Host "Downloading JUnit 5 Console Standalone..."
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar" -OutFile $junitJar -UseBasicParsing
    if (-not $?) {
        Write-Host "Failed to download JUnit."
        exit 1
    }
}

# Navigate to grammar folder so ANTLR doesn't recreate directory structure
Set-Location "src\main\antlr4\ANT"

Write-Host "Cleaning .class files..."
Get-ChildItem -Path "$rootDir\src\main\java", "$rootDir\src\test\java" -Filter "*.class" -Recurse | Remove-Item -Force

Write-Host "Generating Lexers..."
java -jar $antlrJar -no-listener -visitor -o "$srcDir\ANT" -package ANT PythonLexer.g4 TemplateLexer.g4 CssLexer.g4
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Generating Parsers..."
java -jar $antlrJar -no-listener -visitor -o "$srcDir\ANT" -lib "$srcDir\ANT" -package ANT PythonParser.g4 TemplateParser.g4 CssParser.g4
if ($LASTEXITCODE -ne 0) { exit 1 }

Set-Location $rootDir

# Compile a list of sources using a response file.
# Note: paths use forward slashes because javac's @file format treats '\' as an escape
# character, which breaks paths containing backslashes.
function Compile-Java([string]$label, [string]$classpath, [string]$out, [string[]]$javaFiles) {
    if (-not $javaFiles) {
        Write-Host "No java files found for $label."
        exit 1
    }
    $rsp = Join-Path $env:TEMP "compiler_$label.rsp"
    $lines = New-Object System.Collections.Generic.List[string]
    $lines.Add('-encoding'); $lines.Add('UTF-8')
    $lines.Add('-classpath'); $lines.Add('"' + $classpath.Replace('\', '/') + '"')
    $lines.Add('-d'); $lines.Add('"' + $out.Replace('\', '/') + '"')
    foreach ($f in $javaFiles) { $lines.Add('"' + $f.Replace('\', '/') + '"') }
    [System.IO.File]::WriteAllLines($rsp, $lines, (New-Object System.Text.UTF8Encoding($false)))
    Write-Host "Compiling $label..."
    cmd.exe /c "javac @$rsp"
    $code = $LASTEXITCODE
    Remove-Item $rsp -ErrorAction SilentlyContinue
    if ($code -ne 0) { exit 1 }
}

Write-Host "Compiling Java main sources..."
Get-ChildItem -Path $srcDir -Recurse -Filter *.class | Remove-Item -Force
$mainFiles = Get-ChildItem -Path $srcDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
Compile-Java "main" $antlrJar $srcDir $mainFiles
Remove-Item "$rootDir\sources.txt" -ErrorAction SilentlyContinue
Write-Host "Main Compilation successful."

Write-Host "Compiling Java test sources..."
Get-ChildItem -Path $testDir -Recurse -Filter *.class | Remove-Item -Force
$testFiles = Get-ChildItem -Path $testDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
Compile-Java "test" "$antlrJar;$junitJar;$srcDir" $testDir $testFiles
Remove-Item "$rootDir\test-sources.txt" -ErrorAction SilentlyContinue
Write-Host "Test Compilation successful."

Write-Host "Running JUnit Tests..."
java -jar $junitJar execute -cp "$antlrJar;$srcDir;$testDir" --scan-class-path
if ($LASTEXITCODE -ne 0) { 
    Write-Host "Tests failed!"
    exit 1 
}

Write-Host "Generating Flask App..."
java -cp "$antlrJar;$srcDir" cli.CompilerCli --python src/test/resources/crud_app.py --templates src/test/resources/crud_templates --output generated/crud_flask_app
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Checking python compilation..."
python -m py_compile generated/crud_flask_app/app.py
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Installing Python test dependencies..."
python -m pip install -r requirements.txt -q 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Running Flask and CLI acceptance tests..."
python -m pytest test_flask_app.py test_cli_acceptance.py -v
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Build and Tests passed successfully!"
