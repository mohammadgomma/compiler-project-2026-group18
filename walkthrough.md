# دليل الإنجاز الشامل

## 1. نقطة البداية

كان المشروع يحتوي على قواعد ومكونات أولية، لكن دورة المترجم لم تكن تعمل كاملة: ظهرت أخطاء في INDENT/DEDENT، وضاعت معاملات decorators وkeyword arguments، ولم يكن الربط بين Python وJinja مكتملًا، وكان تطبيق المنتجات موزعًا على أمثلة منفصلة.

## 2. إعادة تنظيم المشروع

تم اعتماد الهيكل القياسي:

- القواعد: `src/main/antlr4/ANT`
- Java: `src/main/java`
- الاختبارات: `src/test/java`
- الموارد: `src/test/resources`
- التطبيق المولد: `generated/crud_flask_app`

وأضيف سكربتا `build.ps1` و`build.sh` لتنفيذ دورة بناء قابلة للتكرار من الصفر.

## 3. إصلاح Lexer وParser

### Python

- `PythonLexerBase` يتابع مستويات الإزاحة ويصدر `INDENT` و`DEDENT`.
- لا تُنشأ إزاحة جديدة داخل الأقواس المفتوحة.
- تم دعم decorators مع arguments وkwargs.
- تم دعم attribute access، function calls، index access/assignment، lists، dictionaries، والشروط.

### HTML/Jinja

- تم الفصل بين نص HTML وتعبيرات `{{ ... }}` وكتل `{% ... %}`.
- تم دعم `for`, `if`, `elif`, `else`, و`set`.
- تم دعم filters داخل `JinjaExpressionNode`.
- تم الحفاظ على `<textarea></textarea>` والعناصر الفارغة المغلقة ذاتيًا.

### CSS

- CSS داخل `<style>` يُحفظ ضمن `CssStyleNode`.
- ملفات `.css` المستقلة تُحلل بواسطة `CssLexer` و`CssParser`، ثم تُولد داخل `static/css`.
- تم توحيد رمز النسبة `%` وإزالة تحذير token غير المعرّف.

## 4. بناء AST

تم إنشاء عائلة عقد تعتمد على OOP:

- قاعدة مشتركة `ASTNode`.
- فرع `PythonNode` وفرع `TemplateNode`.
- عقد للدوال، decorators، الاستدعاءات، القوائم، القواميس، الفهرسة، والشروط.
- عقد لـHTML وJinja وCSS.
- كل عقدة تحفظ موقع المصدر: الملف والسطر والعمود.

## 5. جدول الرموز والتحليل الدلالي

يدعم `SymbolTable` نطاقًا عالميًا ونطاقات أبناء للدوال والشروط وحلقات Jinja والقوالب. يقوم `SemanticAnalyzer` بزيارة الشجرتين وجمع الأخطاء في `DiagnosticCollector` بدل إيقاف البرنامج عند أول خطأ دلالي.

تم تنفيذ خمسة أخطاء Python وأكثر من خمسة أخطاء Template، موثقة في `docs/semantic-errors.md`.

## 6. الربط بين الشجرتين

1. يكتشف `CompilerCli` أسماء القوالب أولًا.
2. يبني Python AST ويحلل استدعاءات `render_template`.
3. يخزن kwargs لكل قالب في `templateContexts`.
4. يبني Template AST لكل ملف.
5. يدخل نطاقًا مستقلًا للقالب ويعرّف المتغيرات المرسلة إليه.
6. يفحص متغيرات Jinja، الحلقات، والفلاتر.

بهذا تصل بيانات مثل `products=products.values()` من Python إلى سياق `products.html` دلاليًا.

## 7. توليد الكود

- `ProjectGenerator.generateProject` يولد `app.py` من Python AST.
- القوالب لا تُنسخ مباشرة؛ كل Template AST يزور `ProjectGenerator` لتوليد HTML/Jinja.
- CSS المستقل يُسمح بتوليده فقط بعد اجتياز Parser الخاص به.
- يتم إنشاء `requirements.txt` داخل التطبيق الناتج.

## 8. تطبيق المنتجات

يستخدم التطبيق قاموسًا:

```python
products = {1: {...}, 2: {...}}
counters = {"next_id": 3}
```

وذلك لفصل هوية المنتج عن موقعه داخل مجموعة البيانات. يدعم التطبيق:

- `GET /products`
- `GET|POST /products/add`
- `GET /products/<int:id>`
- `POST /products/<int:id>/delete`

المنتج غير الموجود يعيد HTTP 404، والبيانات الإلزامية الناقصة تعيد HTTP 400 دون استهلاك ID جديد.

## 9. الطباعة والتشخيص

يدعم `PrintVisitor` الآن:

- decorators وargs وkwargs.
- جميع فروع `if/elif/else` في Python وJinja.
- القواميس والفهرسة.
- محتوى CSS.
- اسم الملف والسطر والعمود لكل عقدة.

## 10. التحقق النهائي

آخر بناء نظيف حقق:

- 26/26 JUnit.
- 8/8 Pytest acceptance tests.
- نجاح `py_compile`.
- نجاح تطبيق Flask المولد في دورة العرض والإضافة والتفاصيل والحذف.
- نجاح اكتشاف القالب المفقود.
- قبول CSS الصحيح ورفض CSS غير الصحيح.
- نجاح التطبيقات التجريبية الثلاثة باستخدام مجلدات قوالب مستقلة.
