package visitor;

import ast.*;
import symboltable.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class SemanticAnalyzer implements ASTVisitor<Void> {
    private SymbolTable symbolTable;
    private DiagnosticCollector diagnostics;
    
    // Semantic Context
    private Stack<Boolean> insideFunction = new Stack<>();
    private Set<String> flaskRoutes = new HashSet<>();
    private Set<String> htmlIds = new HashSet<>();
    private Set<String> knownTemplates = new HashSet<>(); // templates available
    private Map<String, Set<String>> templateContexts = new java.util.HashMap<>();
    private String currentTemplate = null;

    public SemanticAnalyzer(SymbolTable symbolTable, DiagnosticCollector diagnostics) {
        this.symbolTable = symbolTable;
        this.diagnostics = diagnostics;
        this.insideFunction.push(false);
    }
    
    public DiagnosticCollector getDiagnostics() {
        return diagnostics;
    }
    
    public void addKnownTemplate(String name) {
        knownTemplates.add(name);
    }

    private void error(ASTNode node, String code, String message) {
        diagnostics.addError(new CompilerError(
            code, CompilerError.Severity.ERROR, message, 
            node.getFile(), node.getLine(), node.getColumn()
        ));
    }

    @Override
    public Void visit(ProgramNode node) {
        boolean isTemplate = node.getFile() != null && (node.getFile().endsWith(".html") || node.getFile().endsWith(".css"));
        if (isTemplate) {
            currentTemplate = node.getFile();
            symbolTable.enterScope("template_" + currentTemplate);
            htmlIds.clear(); // Reset HTML ids for each template
            if (templateContexts.containsKey(currentTemplate)) {
                for (String var : templateContexts.get(currentTemplate)) {
                    symbolTable.define(new Symbol(var, Symbol.Kind.VARIABLE, "any", node.getLine(), node.getColumn()));
                }
            }
        }
        for (ASTNode stmt : node.getStatements()) {
            if (stmt != null) stmt.accept(this);
        }
        if (isTemplate) {
            symbolTable.exitScope();
            currentTemplate = null;
        }
        return null;
    }

    @Override
    public Void visit(FunctionDefNode node) {
        // 1. Duplicate Function
        if (symbolTable.resolveLocal(node.getName()) != null) {
            error(node, "PY_DUPLICATE_SYMBOL", "Duplicate function definition: " + node.getName());
        }
        symbolTable.define(new Symbol(node.getName(), Symbol.Kind.FUNCTION, "function", node.getLine(), node.getColumn()));
        
        for (DecoratorNode dec : node.getDecorators()) {
            if (dec.getName().equals("app.route")) {
                if (dec.getArgs().size() > 0 && dec.getArgs().get(0) instanceof LiteralNode) {
                    String routePath = ((LiteralNode) dec.getArgs().get(0)).getValue();
                    if (routePath != null) {
                        String routeKey = routePath;
                        if (dec.getKwargs().containsKey("methods")) {
                            ASTNode methodsNode = dec.getKwargs().get("methods");
                            if (methodsNode instanceof ListNode) {
                                routeKey += " methods: [";
                                for (ASTNode el : ((ListNode) methodsNode).getElements()) {
                                    if (el instanceof LiteralNode) routeKey += ((LiteralNode) el).getValue() + ",";
                                }
                                routeKey += "]";
                            }
                        }
                        if (flaskRoutes.contains(routeKey)) {
                            diagnostics.addError(new CompilerError("PY_DUPLICATE_ROUTE", CompilerError.Severity.ERROR, "Duplicate route path: " + routeKey, node.getFile(), node.getLine(), node.getColumn()));
                        } else {
                            flaskRoutes.add(routeKey);
                        }
                    }
                }
            }
        }
        
        symbolTable.enterScope(node.getName());
        insideFunction.push(true);
        
        for (String param : node.getParams()) {
            if (symbolTable.resolveLocal(param) != null) {
                error(node, "PY_DUPLICATE_SYMBOL", "Duplicate parameter: " + param);
            }
            symbolTable.define(new Symbol(param, Symbol.Kind.PARAMETER, "any", node.getLine(), node.getColumn()));
        }
        
        for (ASTNode stmt : node.getBody()) {
            if (stmt != null) stmt.accept(this);
        }
        
        insideFunction.pop();
        symbolTable.exitScope();
        return null;
    }

    @Override
    public Void visit(DecoratorNode node) {
        for (ASTNode arg : node.getArgs()) {
            if (arg != null) arg.accept(this);
        }
        for (ASTNode arg : node.getKwargs().values()) {
            if (arg != null) arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        if (node.getCondition() != null) node.getCondition().accept(this);
        symbolTable.enterScope("if");
        for (ASTNode stmt : node.getIfbody()) {
            if (stmt != null) stmt.accept(this);
        }
        symbolTable.exitScope();
        
        for (int i=0; i < node.getElifconditions().size(); i++) {
            if (node.getElifconditions().get(i) != null) node.getElifconditions().get(i).accept(this);
            symbolTable.enterScope("elif");
            for (ASTNode stmt : node.getElifbodies().get(i)) {
                if (stmt != null) stmt.accept(this);
            }
            symbolTable.exitScope();
        }
        
        if (!node.getElsebody().isEmpty()) {
            symbolTable.enterScope("else");
            for (ASTNode stmt : node.getElsebody()) {
                if (stmt != null) stmt.accept(this);
            }
            symbolTable.exitScope();
        }
        return null;
    }

    @Override
    public Void visit(AssignNode node) {
        if (node.getValue() != null) node.getValue().accept(this);
        
        String targetName = "";
        if (node.getTarget() instanceof IdentifierNode) {
            targetName = ((IdentifierNode)node.getTarget()).getName();
        } else if (node.getTarget() != null) {
            node.getTarget().accept(this);
            return null;
        }

        if (!targetName.isEmpty() && symbolTable.resolveLocal(targetName) == null) {
            String type = "any";
            if (node.getValue() instanceof LiteralNode) type = ((LiteralNode) node.getValue()).getType();
            if (node.getValue() instanceof ListNode) type = "list";
            if (node.getValue() instanceof DictNode) type = "dict";
            symbolTable.define(new Symbol(targetName, Symbol.Kind.VARIABLE, type, node.getLine(), node.getColumn()));
        }
        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        if (!insideFunction.peek()) {
            error(node, "PY_RETURN_OUTSIDE_FUNCTION", "'return' outside function");
        }
        if (node.getValue() != null) node.getValue().accept(this);
        return null;
    }

    @Override
    public Void visit(ImportNode node) {
        for (String name : node.getNames()) {
            if (symbolTable.resolveLocal(name) == null) {
                symbolTable.define(new Symbol(name, Symbol.Kind.IMPORT, "module", node.getLine(), node.getColumn()));
            }
        }
        return null;
    }

    @Override
    public Void visit(BinaryExprNode node) {
        if (node.getLeft() != null) node.getLeft().accept(this);
        if (node.getRight() != null) node.getRight().accept(this);
        
        // basic type mismatch check
        if (node.getOperator().equals("+") || node.getOperator().equals("-")) {
            if (node.getLeft() instanceof LiteralNode && node.getRight() instanceof LiteralNode) {
                String t1 = ((LiteralNode)node.getLeft()).getType();
                String t2 = ((LiteralNode)node.getRight()).getType();
                if ((t1.equals("string") && !t2.equals("string")) || (!t1.equals("string") && t2.equals("string"))) {
                    error(node, "PY_TYPE_MISMATCH", "Type mismatch in binary operation between " + t1 + " and " + t2);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(UnaryExprNode node) {
        if (node.getExpr() != null) node.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(CallExprNode node) {
        if (node.getFunction() instanceof IdentifierNode) {
            String funcName = ((IdentifierNode)node.getFunction()).getName();
            Symbol s = symbolTable.resolve(funcName);
            if (s == null) {
                // Warning rather than error because of built-ins (print, len, etc)
                // diagnostics.addError(new CompilerError("PY_UNDEFINED_FUNCTION", CompilerError.Severity.WARNING, "Call to undefined function: " + funcName, node.getFile(), node.getLine(), node.getColumn()));
            }
        }
        if (node.getFunction() instanceof AttributeExprNode) {
            AttributeExprNode attr = (AttributeExprNode) node.getFunction();
            if (attr.getAttribute().equals("render_template") || (attr.getObject() instanceof IdentifierNode && ((IdentifierNode)attr.getObject()).getName().equals("render_template"))) {
                // If it's a direct call to render_template, check first arg
                // We'll approximate this by checking args
            }
        }
        // Direct render_template check
        if (node.getFunction() instanceof IdentifierNode && ((IdentifierNode)node.getFunction()).getName().equals("render_template")) {
            if (!node.getArgs().isEmpty() && node.getArgs().get(0) instanceof LiteralNode) {
                String tmplName = ((LiteralNode)node.getArgs().get(0)).getValue();
                tmplName = tmplName.replace("\"", "").replace("'", "");
                if (!knownTemplates.contains(tmplName)) {
                    error(node, "PY_RENDER_MISSING_TEMPLATE", "render_template references a missing template: " + tmplName);
                }
                
                // Track context variables passed to the template
                templateContexts.putIfAbsent(tmplName, new HashSet<>());
                if (node.getKwargs() != null) {
                    for (String kwarg : node.getKwargs().keySet()) {
                        templateContexts.get(tmplName).add(kwarg);
                    }
                }
            }
        }
        
        for (ASTNode arg : node.getArgs()) {
            if (arg != null) arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(AttributeExprNode node) {
        if (node.getObject() != null) node.getObject().accept(this);
        return null;
    }

    @Override
    public Void visit(IndexExprNode node) {
        if (node.getObject() != null) node.getObject().accept(this);
        if (node.getIndex() != null) node.getIndex().accept(this);
        return null;
    }

    @Override
    public Void visit(LiteralNode node) {
        return null;
    }

    @Override
    public Void visit(IdentifierNode node) {
        if (currentTemplate != null) {
            Symbol s = symbolTable.resolve(node.getName());
            if (s == null) {
                error(node, "TPL_UNDEFINED_VARIABLE", "Undefined variable in template: " + node.getName());
            }
        } else {
            // Python AST check
            if (!node.getName().equals("__name__") && !node.getName().equals("request") && !node.getName().equals("app") && !node.getName().equals("render_template") && !node.getName().equals("jsonify")) {
                Symbol s = symbolTable.resolve(node.getName());
                if (s == null) {
                    // Suppressed for python to avoid false positives on imports etc.
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(ListNode node) {
        for (ASTNode e : node.getElements()) {
            if (e != null) e.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DictNode node) {
        for (ASTNode e : node.getKeys()) {
            if (e != null) e.accept(this);
        }
        for (ASTNode e : node.getValues()) {
            if (e != null) e.accept(this);
        }
        return null;
    }

    // -----------------------------------------
    // Template Visitors
    // -----------------------------------------

    @Override
    public Void visit(HtmlElementNode node) {
        String id = node.getAttributes().get("id");
        if (id != null && !id.isEmpty()) {
            if (htmlIds.contains(id)) {
                error(node, "TPL_DUPLICATE_ID", "Duplicate HTML id: " + id);
            }
            htmlIds.add(id);
        }
        
        if (node.getTagName().equalsIgnoreCase("a") && !node.getAttributes().containsKey("href")) {
            error(node, "TPL_MISSING_HREF", "Anchor <a> tag is missing href attribute");
        }
        
        if (node.getTagName().equalsIgnoreCase("img") && !node.getAttributes().containsKey("src")) {
            error(node, "TPL_MISSING_SRC", "Image <img> tag is missing src attribute");
        }
        
        for (ASTNode child : node.getChildren()) {
            if (child != null) child.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(JinjaExpressionNode node) {
        if (node.getExpr() != null) node.getExpr().accept(this);
        for (String filter : node.getFilters()) {
            if (!filter.equals("length") && !filter.equals("upper") && !filter.equals("lower") && !filter.equals("default") && !filter.equals("safe")) {
                error(node, "TPL_UNSUPPORTED_FILTER", "Unsupported Jinja filter: " + filter);
            }
        }
        return null;
    }

    @Override
    public Void visit(JinjaIfNode node) {
        if (node.getCondition() != null) node.getCondition().accept(this);
        
        symbolTable.enterScope("if");
        for (ASTNode child : node.getIfbody()) {
            if (child != null) child.accept(this);
        }
        symbolTable.exitScope();
        
        for (int i = 0; i < node.getElifConditions().size(); i++) {
            if (node.getElifConditions().get(i) != null) node.getElifConditions().get(i).accept(this);
            symbolTable.enterScope("elif");
            for (ASTNode child : node.getElifBodies().get(i)) {
                if (child != null) child.accept(this);
            }
            symbolTable.exitScope();
        }
        
        symbolTable.enterScope("else");
        for (ASTNode child : node.getElsebody()) {
            if (child != null) child.accept(this);
        }
        symbolTable.exitScope();
        return null;
    }

    @Override
    public Void visit(JinjaForNode node) {
        // define loop variable
        symbolTable.enterScope("for");
        symbolTable.define(new Symbol(node.getLoopvar(), Symbol.Kind.VARIABLE, "any", node.getLine(), node.getColumn()));
        
        if (node.getIterable() != null) {
            // Capture errors from iterable evaluation to convert them to TPL_UNDEFINED_ITERABLE
            int errorsBefore = diagnostics.getErrors().size();
            node.getIterable().accept(this);
            if (diagnostics.getErrors().size() > errorsBefore) {
                // If the error was TPL_UNDEFINED_VARIABLE, we might want to change it to TPL_UNDEFINED_ITERABLE
                CompilerError lastError = diagnostics.getErrors().get(diagnostics.getErrors().size() - 1);
                if (lastError.getCode().equals("TPL_UNDEFINED_VARIABLE")) {
                    diagnostics.getErrors().remove(diagnostics.getErrors().size() - 1);
                    error(node, "TPL_UNDEFINED_ITERABLE", "Undefined iterable in template for-loop");
                }
            }
        }
        
        for (ASTNode child : node.getBody()) {
            if (child != null) child.accept(this);
        }
        symbolTable.exitScope();
        return null;
    }

    @Override
    public Void visit(JinjaSetNode node) {
        if (node.getValue() != null) node.getValue().accept(this);
        symbolTable.define(new Symbol(node.getTarget(), Symbol.Kind.VARIABLE, "any", node.getLine(), node.getColumn()));
        return null;
    }

    @Override
    public Void visit(TextNode node) {
        return null;
    }

    @Override
    public Void visit(CssStyleNode node) {
        if (node.getContent().isEmpty()) {
            error(node, "TPL_EMPTY_STYLE", "CSS style block is empty");
        }
        for (ASTNode child : node.getContent()) {
            if (child != null) child.accept(this);
        }
        return null;
    }
}
