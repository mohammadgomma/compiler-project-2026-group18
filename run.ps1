$ErrorActionPreference = "Stop"
$antlrJar = "dependencies\antlr-4.13.2-complete.jar"
$cp = "$antlrJar;src\main\java"

java -cp $cp cli.CompilerCli `
  --python src\test\resources\crud_app.py `
  --templates src\test\resources\crud_templates `
  --output generated\crud_flask_app `
  --print-ast --print-symbols --diagnostics
