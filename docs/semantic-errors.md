# الأخطاء الدلالية المدعومة

يستخدم `SemanticAnalyzer` مع `DiagnosticCollector` رموزًا ثابتة للأخطاء، وتحتوي كل رسالة على اسم الملف ورقم السطر والعمود.

## أخطاء Python/Flask

| الرمز | الحالة المكتشفة | مثال مختصر |
|---|---|---|
| `PY_DUPLICATE_SYMBOL` | تعريف دالة مكررة أو تكرار معامل داخل النطاق نفسه | تعريف `def save()` مرتين |
| `PY_RETURN_OUTSIDE_FUNCTION` | استخدام `return` خارج جسم دالة | `return 5` في المستوى العام |
| `PY_DUPLICATE_ROUTE` | تكرار مسار Flask نفسه مع HTTP methods نفسها | مساران بـ`@app.route('/products')` |
| `PY_RENDER_MISSING_TEMPLATE` | استدعاء قالب غير موجود في مجلد القوالب | `render_template('missing.html')` |
| `PY_TYPE_MISMATCH` | عملية حسابية بسيطة بين أنواع غير متوافقة | `5 + 'text'` |

## أخطاء Jinja/HTML/CSS

| الرمز | الحالة المكتشفة | مثال مختصر |
|---|---|---|
| `TPL_DUPLICATE_ID` | تكرار `id` داخل ملف HTML نفسه | عنصران يحملان `id="main"` |
| `TPL_MISSING_HREF` | عنصر `<a>` من دون `href` | `<a>Open</a>` |
| `TPL_MISSING_SRC` | عنصر `<img />` من دون `src` | `<img />` |
| `TPL_EMPTY_STYLE` | كتلة `<style>` فارغة | `<style></style>` |
| `TPL_UNSUPPORTED_FILTER` | فلتر Jinja غير موجود في القائمة المدعومة | `{{ name | fake }}` |
| `TPL_UNDEFINED_VARIABLE` | متغير قالب لم يُمرر من Python ولم يُعرّف في نطاق Jinja | `{{ title }}` من دون `title=...` |
| `TPL_UNDEFINED_ITERABLE` | iterable غير معرف داخل حلقة `for` | `{% for x in missing %}` |
| أخطاء CSS النحوية | ملف CSS مستقل غير مطابق لقواعد `CssParser` | قوس `}` مفقود |

## كيف يتم ربط Python بالقالب؟

عند زيارة استدعاء مثل:

```python
return render_template('products.html', products=products, title='Products')
```

يسجل المحلل السياق التالي:

```text
products.html -> {products, title}
```

وعند تحليل `products.html` ينشئ نطاقًا خاصًا بالقالب ويعرّف هذه الأسماء قبل زيارة تعبيرات Jinja. المتغيرات الخاصة بحلقات `for` وعبارات `set` تُعرّف داخل نطاقات متداخلة حتى لا تتسرب إلى القوالب الأخرى.
