# تقرير الاختبارات النهائي

## بيئة الاختبار

- Java: OpenJDK 21
- ANTLR: 4.13.2
- Python: 3.13.5
- JUnit Platform Console
- Pytest 9.0.2

## دورة البناء

تم تنفيذ `./build.sh` من مشروع نظيف بعد حذف جميع ملفات `.class`. سكربت Windows `build.ps1` ينفذ الدورة نفسها باستخدام classpath المناسب لنظام Windows.

## نتيجة JUnit

```text
[         8 containers found      ]
[         8 containers successful ]
[         0 containers failed     ]
[        26 tests found           ]
[        26 tests started         ]
[        26 tests successful      ]
[         0 tests failed          ]
```

### التغطية

- AST builders لـPython وTemplate.
- decorators وargs/kwargs.
- HTML/Jinja وfor/if/elif/else.
- Semantic diagnostics لـPython وTemplate.
- CSS المستقل الصحيح وغير الصحيح.
- ProjectGenerator وtemplate round-trip.
- PrintVisitor بما فيه kwargs وelif/else ومحتوى CSS.

## نتيجة اختبارات القبول Python

```text
8 passed
```

وتشمل:

1. دورة CRUD كاملة على التطبيق المولد.
2. HTTP 400 للحقول الإلزامية الناقصة من دون استهلاك ID.
3. HTTP 404 للمنتج غير الموجود.
4. فشل القالب المفقود مع `PY_RENDER_MISSING_TEMPLATE` ومنع التوليد.
5. قبول CSS مستقل صحيح وتوليده تحت `static/css`.
6. رفض CSS غير صحيح.
7. نجاح `test1_app.py` مع قوالبه المعزولة.
8. نجاح `test2_app.py` و`test3_app.py` مع مجلداتهما المعزولة (ضمن اختبار parameterized).

## فحص Python

```text
python -m py_compile generated/crud_flask_app/app.py
PASS
```

## ملاحظات إعادة الإنتاج

- يجب تثبيت حزم `requirements.txt` قبل Pytest.
- الملف الكامل لمخرجات آخر تشغيل موجود في `docs/test-results-latest.txt`.
