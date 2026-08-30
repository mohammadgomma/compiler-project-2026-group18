# مخطط شجرتي AST

ينتج المشروع شجرتين مستقلتين: Python AST وTemplate AST. جميع العقد ترث من `ASTNode` وتحفظ `fileName`, `line`, و`column`، وتنفذ التابع `accept` لتطبيق Visitor Pattern.

## البنية العامة

```mermaid
classDiagram
    class ASTNode {
      <<abstract>>
      -String file
      -int line
      -int column
      +accept(ASTVisitor)
      +setLocation(line, column, file)
    }
    class PythonNode { <<abstract>> }
    class TemplateNode { <<abstract>> }
    ASTNode <|-- PythonNode
    ASTNode <|-- TemplateNode
```

## Python AST

```mermaid
classDiagram
    PythonNode <|-- ProgramNode
    PythonNode <|-- FunctionDefNode
    PythonNode <|-- DecoratorNode
    PythonNode <|-- AssignNode
    PythonNode <|-- IfNode
    PythonNode <|-- ReturnNode
    PythonNode <|-- ImportNode
    PythonNode <|-- CallExprNode
    PythonNode <|-- AttributeExprNode
    PythonNode <|-- IndexExprNode
    PythonNode <|-- BinaryExprNode
    PythonNode <|-- UnaryExprNode
    PythonNode <|-- IdentifierNode
    PythonNode <|-- LiteralNode
    PythonNode <|-- ListNode
    PythonNode <|-- DictNode

    FunctionDefNode --> DecoratorNode : decorators
    FunctionDefNode --> ASTNode : body
    CallExprNode --> ASTNode : args/kwargs
    IfNode --> ASTNode : conditions/bodies
    DictNode --> ASTNode : keys/values
```

## Template AST

```mermaid
classDiagram
    TemplateNode <|-- ProgramNode
    TemplateNode <|-- HtmlElementNode
    TemplateNode <|-- TextNode
    TemplateNode <|-- JinjaExpressionNode
    TemplateNode <|-- JinjaIfNode
    TemplateNode <|-- JinjaForNode
    TemplateNode <|-- JinjaSetNode
    TemplateNode <|-- CssStyleNode

    HtmlElementNode --> ASTNode : children
    JinjaExpressionNode --> ASTNode : expression
    JinjaIfNode --> ASTNode : conditions/bodies
    JinjaForNode --> ASTNode : iterable/body
    CssStyleNode --> ASTNode : content
```

## الزوار

```mermaid
flowchart LR
    AST[Python AST / Template AST] --> PV[PrintVisitor]
    AST --> SA[SemanticAnalyzer]
    AST --> PG[ProjectGenerator]
    SA --> ST[SymbolTable]
    SA --> DC[DiagnosticCollector]
    PG --> OUT[Flask Project]
```

- `PrintVisitor`: يطبع كل عقدة وأبناءها والموقع.
- `SemanticAnalyzer`: يبني جدول الرموز ويكتشف الأخطاء.
- `ProjectGenerator`: يعيد توليد Python وHTML/Jinja من الشجرتين.
