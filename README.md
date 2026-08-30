# مشروع المترجمات 2026 - المجموعة 18

مترجم تعليمي مبني بلغة Java وANTLR4 لتحليل جزء عملي من Python/Flask وقوالب Jinja2/HTML وCSS، ثم بناء أشجار AST، وإجراء التحليل الدلالي، وتوليد تطبيق Flask متكامل لإدارة المنتجات.

## أعضاء المجموعة

- فردوس محمد هديه
- محمد فهد جمعه
- حسن منذر يوسف
- احمد عبد المجيد الفروان
- شاها اسماعيل جاويش

## النتائج النهائية المثبتة

- بناء نظيف من دون الاعتماد على ملفات `.class` قديمة.
- **26/26** اختبار JUnit ناجح.
- **8/8** اختبارات قبول Python ناجحة، وتشمل Flask CRUD والـCLI والقوالب وCSS المستقل.
- فحص `python -m py_compile` للتطبيق المولد ناجح.
- تطبيق المنتجات المولد يدعم العرض والإضافة والتفاصيل والحذف، ويعالج المعرفات بقاموس وعداد مستقل.

## بنية المشروع

- `src/main/antlr4/ANT/`: قواعد Python وTemplate وCSS.
- `src/main/java/ast/`: عقد شجرتي Python وTemplate وفق OOP والوراثة وVisitor Pattern.
- `src/main/java/parseTree/`: تحويل Parse Trees إلى AST.
- `src/main/java/symboltable/`: جدول الرموز والنطاقات المتداخلة.
- `src/main/java/visitor/`: الطباعة والتحليل الدلالي وتوليد الكود.
- `src/main/java/cli/CompilerCli.java`: دورة المترجم من الإدخال حتى التوليد.
- `src/test/`: اختبارات JUnit وملفات التطبيقات والقوالب التجريبية.
- `generated/crud_flask_app/`: تطبيق Flask الناتج عن المترجم.

## البناء والاختبار على Windows

المتطلبات: Java 17 أو أحدث، Python 3، واتصال بالإنترنت عند أول تثبيت لحزم Python.

```powershell
.\build.ps1
```

يقوم السكربت بالتسلسل التالي:

1. حذف ملفات `.class` القديمة.
2. توليد Lexers وParsers بواسطة ANTLR.
3. تجميع كود Java الرئيسي والاختبارات.
4. تشغيل اختبارات JUnit.
5. توليد تطبيق Flask.
6. فحص صياغة `app.py`.
7. تثبيت `requirements.txt` وتشغيل اختبارات Flask والـCLI.

## البناء والاختبار على Linux/macOS

```bash
./build.sh
```

## تشغيل المترجم يدويًا

### Windows

```powershell
java -cp "dependencies\antlr-4.13.2-complete.jar;src\main\java" cli.CompilerCli `
  --python src\test\resources\crud_app.py `
  --templates src\test\resources\crud_templates `
  --output generated\crud_flask_app `
  --print-ast --print-symbols --diagnostics
```

### Linux/macOS

```bash
java -cp "dependencies/antlr-4.13.2-complete.jar:src/main/java" cli.CompilerCli \
  --python src/test/resources/crud_app.py \
  --templates src/test/resources/crud_templates \
  --output generated/crud_flask_app \
  --print-ast --print-symbols --diagnostics
```

## تشغيل التطبيق المولد

```bash
cd generated/crud_flask_app
python -m pip install -r requirements.txt
python app.py
```

ثم افتح: `http://127.0.0.1:5000/products`

## خيارات الـCLI

- `--python <file>`: ملف Python المطلوب تحليله، وهو إلزامي.
- `--templates <directory>`: مجلد HTML/Jinja وCSS.
- `--output <directory>`: مجلد المشروع الناتج.
- `--print-ast`: طباعة Python AST وTemplate AST.
- `--print-symbols`: طباعة جدول الرموز والنطاقات.
- `--diagnostics`: طباعة الأخطاء الدلالية.

## وثائق المشروع

- [التقرير النهائي العربي](docs/final-report-ar.md)
- [مخطط AST](docs/ast-diagram.md)
- [الأخطاء الدلالية](docs/semantic-errors.md)
- [تقرير الاختبارات](docs/testing-report.md)
- [نتيجة آخر بناء](docs/test-results-latest.txt)
- [دليل الإنجاز](walkthrough.md)
- [قائمة المهام](task.md)

## حدود اللغة المدعومة

المشروع مترجم تعليمي لجزء محدد من اللغات وليس تنفيذًا كاملًا لكل مواصفات Python أو HTML أو CSS:

- عناصر HTML الفارغة في ملفات المصدر تُكتب بصيغة مغلقة مثل `<input />` و`<img />`.
- تعبيرات Jinja داخل قيم سمات HTML تُحفظ وتُولد كما هي، لكنها لا تدخل حاليًا في فحص المتغير غير المعرف داخل السمة.
- دعم Python موجّه للتراكيب المطلوبة في تطبيقات Flask التجريبية: الاستيراد، التعريفات، الدوال، decorators، القوائم والقواميس، الاستدعاءات، الفهرسة، والشروط.
