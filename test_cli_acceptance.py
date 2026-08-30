import os
import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parent
JAVA_CP = os.pathsep.join([
    str(ROOT / 'dependencies' / 'antlr-4.13.2-complete.jar'),
    str(ROOT / 'src' / 'main' / 'java'),
])


def run_cli(python_file: Path, template_dir: Path, output_dir: Path, *extra_args: str):
    command = [
        'java', '-cp', JAVA_CP, 'cli.CompilerCli',
        '--python', str(python_file),
        '--templates', str(template_dir),
        '--output', str(output_dir),
        *extra_args,
    ]
    return subprocess.run(command, cwd=ROOT, text=True, capture_output=True, check=False)


def test_missing_template_fails_with_location_and_no_output(tmp_path):
    python_file = tmp_path / 'missing.py'
    templates = tmp_path / 'templates'
    output = tmp_path / 'generated'
    templates.mkdir()
    python_file.write_text(
        "from flask import Flask, render_template\n"
        "app = Flask(__name__)\n"
        "@app.route('/x')\n"
        "def x():\n"
        "    return render_template('missing.html')\n",
        encoding='utf-8',
    )

    result = run_cli(python_file, templates, output, '--diagnostics')
    combined = result.stdout + result.stderr

    assert result.returncode != 0
    assert 'PY_RENDER_MISSING_TEMPLATE' in combined
    assert 'missing.py:5:' in combined
    assert not output.exists()


def test_valid_standalone_css_is_validated_and_generated_under_static(tmp_path):
    python_file = tmp_path / 'app.py'
    templates = tmp_path / 'assets'
    output = tmp_path / 'generated'
    templates.mkdir()
    python_file.write_text('from flask import Flask\napp = Flask(__name__)\n', encoding='utf-8')
    css = (
        'body { color: red; }\n'
        'a:hover { color: #fff; }\n'
        '.box { width: 50%; margin: 10px; }\n'
        '@media screen and (max-width: 600px) { body { margin: 0; } }\n'
    )
    (templates / 'site.css').write_text(css, encoding='utf-8')

    result = run_cli(python_file, templates, output)

    assert result.returncode == 0, result.stdout + result.stderr
    generated_css = output / 'static' / 'css' / 'site.css'
    assert generated_css.exists()
    assert generated_css.read_text(encoding='utf-8') == css
    assert not (output / 'templates' / 'site.css').exists()


def test_invalid_standalone_css_is_rejected(tmp_path):
    python_file = tmp_path / 'app.py'
    templates = tmp_path / 'assets'
    output = tmp_path / 'generated'
    templates.mkdir()
    python_file.write_text('from flask import Flask\napp = Flask(__name__)\n', encoding='utf-8')
    (templates / 'bad.css').write_text('body { color: red;', encoding='utf-8')

    result = run_cli(python_file, templates, output)

    assert result.returncode != 0
    assert 'Syntax errors found in CSS file: bad.css' in (result.stdout + result.stderr)
    assert not output.exists()


import pytest


@pytest.mark.parametrize(
    ("python_name", "templates_name"),
    [
        ("test1_app.py", "test1_templates"),
        ("test2_app.py", "test2_templates"),
        ("test3_app.py", "test3_templates"),
    ],
)
def test_bundled_sample_apps_compile_with_isolated_template_directories(tmp_path, python_name, templates_name):
    python_file = ROOT / "src" / "test" / "resources" / python_name
    templates = ROOT / "src" / "test" / "resources" / templates_name
    output = tmp_path / python_name.replace(".py", "_generated")

    result = run_cli(python_file, templates, output)

    assert result.returncode == 0, result.stdout + result.stderr
    assert (output / "app.py").exists()
    assert (output / "requirements.txt").exists()
    subprocess.run([sys.executable, '-m', 'py_compile', str(output / 'app.py')], check=True)
