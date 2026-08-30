package semantic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import visitor.SemanticAnalyzer;
import symboltable.SymbolTable;
import symboltable.DiagnosticCollector;
import parseTree.*;
import ast.*;
import org.antlr.v4.runtime.*;
import ANT.*;

import java.util.List;
import java.util.HashMap;

public class SemanticAnalyzerTest {

    private ProgramNode parsePython(String code) {
        PythonLexer lexer = new PythonLexer(CharStreams.fromString(code));
        PythonParser parser = new PythonParser(new CommonTokenStream(lexer));
        PythonASTBuilder builder = new PythonASTBuilder("test.py");
        return (ProgramNode) builder.visit(parser.program());
    }
    
    private ProgramNode parseTemplate(String code, String fileName) {
        org.antlr.v4.runtime.CharStream input = org.antlr.v4.runtime.CharStreams.fromString(code);
        ANT.TemplateLexer lexer = new ANT.TemplateLexer(input);
        org.antlr.v4.runtime.CommonTokenStream tokens = new org.antlr.v4.runtime.CommonTokenStream(lexer);
        ANT.TemplateParser parser = new ANT.TemplateParser(tokens);
        
        parseTree.TemplateASTBuilder builder = new parseTree.TemplateASTBuilder(fileName);
        return (ProgramNode) builder.visit(parser.template());
    }

    private ProgramNode parseTemplate(String code) {
        return parseTemplate(code, "test.html");
    }

    private SemanticAnalyzer createAnalyzer() {
        SymbolTable sym = new SymbolTable();
        DiagnosticCollector diag = new DiagnosticCollector();
        return new SemanticAnalyzer(sym, diag);
    }

    @Test
    public void testDuplicateRoute() {
        String code = "@app.route('/test')\ndef f1(): pass\n@app.route('/test')\ndef f2(): pass\n";
        ProgramNode ast = parsePython(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("PY_DUPLICATE_ROUTE")));
    }

    @Test
    public void testDuplicateSymbol() {
        String code = "def f(): pass\ndef f(): pass\n";
        ProgramNode ast = parsePython(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("PY_DUPLICATE_SYMBOL")));
    }

    @Test
    public void testReturnOutsideFunction() {
        String code = "return 5\n";
        ProgramNode ast = parsePython(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("PY_RETURN_OUTSIDE_FUNCTION")));
    }

    @Test
    public void testTypeMismatch() {
        String code = "def f():\n    x = 5 + 'hello'\n";
        ProgramNode ast = parsePython(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("PY_TYPE_MISMATCH")));
    }

    @Test
    public void testRenderMissingTemplate() {
        String code = "def f():\n    return render_template('not_found.html')\n";
        ProgramNode ast = parsePython(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        analyzer.addKnownTemplate("exists.html");
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("PY_RENDER_MISSING_TEMPLATE")));
    }

    @Test
    public void testTemplateDuplicateId() {
        String code = "<div id='abc'></div><span id='abc'></span>";
        ProgramNode ast = parseTemplate(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_DUPLICATE_ID")));
    }

    @Test
    public void testTemplateDuplicateIdAcrossFiles() {
        String code1 = "<div id='abc'></div>";
        String code2 = "<span id='abc'></span>";
        
        ProgramNode ast1 = parseTemplate(code1, "t1.html");
        ProgramNode ast2 = parseTemplate(code2, "t2.html");
        
        SemanticAnalyzer analyzer = createAnalyzer();
        ast1.accept(analyzer);
        ast2.accept(analyzer);
        
        // No duplicate ID error should occur because they are in different files!
        assertFalse(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_DUPLICATE_ID")));
    }

    @Test
    public void testTemplateUnsupportedFilter() {
        String code = "{{ var | unknown_filter }}";
        ProgramNode ast = parseTemplate(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_UNSUPPORTED_FILTER")));
    }

    @Test
    public void testTemplateMissingHref() {
        String code = "<a>Link</a>";
        ProgramNode ast = parseTemplate(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_MISSING_HREF")));
    }

    @Test
    public void testTemplateMissingSrc() {
        String code = "<img />";
        ProgramNode ast = parseTemplate(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_MISSING_SRC")));
    }

    @Test
    public void testTemplateEmptyStyle() {
        String code = "<style></style>";
        ProgramNode ast = parseTemplate(code);
        SemanticAnalyzer analyzer = createAnalyzer();
        ast.accept(analyzer);
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_EMPTY_STYLE")));
    }

    @Test
    public void testTemplateUndefinedVariable() {
        String pythonCode = "def f():\n    return render_template('test.html')\n";
        String templateCode = "{{ missing_var }}";
        
        SemanticAnalyzer analyzer = createAnalyzer();
        analyzer.addKnownTemplate("test.html");
        
        ProgramNode pyAst = parsePython(pythonCode);
        pyAst.accept(analyzer); // populates context
        
        ProgramNode tplAst = parseTemplate(templateCode);
        tplAst.accept(analyzer); // analyzes template
        
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_UNDEFINED_VARIABLE")));
    }

    @Test
    public void testTemplateDefinedVariable() {
        String pythonCode = "def f():\n    return render_template('test.html', existing_var='val')\n";
        String templateCode = "{{ existing_var }}";
        
        SemanticAnalyzer analyzer = createAnalyzer();
        analyzer.addKnownTemplate("test.html");
        
        ProgramNode pyAst = parsePython(pythonCode);
        pyAst.accept(analyzer);
        
        ProgramNode tplAst = parseTemplate(templateCode);
        tplAst.accept(analyzer);
        
        assertFalse(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_UNDEFINED_VARIABLE")));
    }

    @Test
    public void testTemplateUndefinedIterable() {
        String pythonCode = "def f():\n    return render_template('test.html')\n";
        String templateCode = "{% for item in missing_list %}{{ item }}{% endfor %}";
        
        SemanticAnalyzer analyzer = createAnalyzer();
        analyzer.addKnownTemplate("test.html");
        
        ProgramNode pyAst = parsePython(pythonCode);
        pyAst.accept(analyzer);
        
        ProgramNode tplAst = parseTemplate(templateCode);
        tplAst.accept(analyzer);
        
        assertTrue(analyzer.getDiagnostics().getErrors().stream().anyMatch(e -> e.getCode().equals("TPL_UNDEFINED_ITERABLE")));
    }
}
