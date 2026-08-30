#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

ANTLR_JAR="$ROOT_DIR/dependencies/antlr-4.13.2-complete.jar"
JUNIT_JAR="$ROOT_DIR/dependencies/junit-platform-console-standalone.jar"
MAIN_SRC="$ROOT_DIR/src/main/java"
TEST_SRC="$ROOT_DIR/src/test/java"

if [[ ! -f "$ANTLR_JAR" || ! -f "$JUNIT_JAR" ]]; then
  echo "Required ANTLR/JUnit JAR files are missing under dependencies/." >&2
  exit 1
fi

echo "Cleaning compiled files..."
find "$MAIN_SRC" "$TEST_SRC" -name '*.class' -delete

echo "Generating ANTLR lexers and parsers..."
pushd src/main/antlr4/ANT >/dev/null
java -jar "$ANTLR_JAR" -no-listener -visitor -o "$MAIN_SRC/ANT" -package ANT \
  PythonLexer.g4 TemplateLexer.g4 CssLexer.g4
java -jar "$ANTLR_JAR" -no-listener -visitor -o "$MAIN_SRC/ANT" -lib "$MAIN_SRC/ANT" -package ANT \
  PythonParser.g4 TemplateParser.g4 CssParser.g4
popd >/dev/null

echo "Compiling Java main sources..."
find "$MAIN_SRC" -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -cp "$ANTLR_JAR"

echo "Compiling Java tests..."
find "$TEST_SRC" -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 \
  -cp "$ANTLR_JAR:$JUNIT_JAR:$MAIN_SRC"

echo "Running JUnit tests..."
java -jar "$JUNIT_JAR" execute \
  -cp "$ANTLR_JAR:$MAIN_SRC:$TEST_SRC" --scan-class-path --details=summary

echo "Generating Flask CRUD application..."
rm -rf generated/crud_flask_app
java -cp "$ANTLR_JAR:$MAIN_SRC" cli.CompilerCli \
  --python src/test/resources/crud_app.py \
  --templates src/test/resources/crud_templates \
  --output generated/crud_flask_app

echo "Checking generated Python syntax..."
python -m py_compile generated/crud_flask_app/app.py

echo "Installing Python test dependencies..."
python -m pip install -r requirements.txt -q

echo "Running Flask and CLI acceptance tests..."
python -m pytest test_flask_app.py test_cli_acceptance.py -v

echo "Build and all tests passed successfully."
