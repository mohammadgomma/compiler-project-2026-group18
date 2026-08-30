package ast;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.antlr.v4.runtime.*;
import ANT.*;
import parseTree.*;

public class ASTBuilderTest {

    private ProgramNode parsePython(String code) {
        PythonLexer lexer = new PythonLexer(CharStreams.fromString(code));
        PythonParser parser = new PythonParser(new CommonTokenStream(lexer));
        PythonASTBuilder builder = new PythonASTBuilder("test.py");
        return (ProgramNode) builder.visit(parser.program());
    }

    private ProgramNode parseTemplate(String code) {
        TemplateLexer lexer = new TemplateLexer(CharStreams.fromString(code));
        TemplateParser parser = new TemplateParser(new CommonTokenStream(lexer));
        TemplateASTBuilder builder = new TemplateASTBuilder("test.html");
        return (ProgramNode) builder.visit(parser.template());
    }

    @Test
    public void testPythonFunctionParsing() {
        String code = "def my_func(a, b):\n    return a + b\n";
        ProgramNode ast = parsePython(code);
        assertEquals(1, ast.getStatements().size());
        assertTrue(ast.getStatements().get(0) instanceof FunctionDefNode);
        
        FunctionDefNode func = (FunctionDefNode) ast.getStatements().get(0);
        assertEquals("my_func", func.getName());
        assertEquals(2, func.getParams().size());
    }

    @Test
    public void testPythonDecoratorParsing() {
        String code = "@app.route('/home', methods=['GET'])\ndef home(): pass\n";
        ProgramNode ast = parsePython(code);
        FunctionDefNode func = (FunctionDefNode) ast.getStatements().get(0);
        assertEquals(1, func.getDecorators().size());
        assertEquals("app.route", func.getDecorators().get(0).getName());
        assertEquals(1, func.getDecorators().get(0).getArgs().size()); // '/home'
        assertEquals(1, func.getDecorators().get(0).getKwargs().size()); // methods=['GET']
    }

    @Test
    public void testHtmlTagParsing() {
        String code = "<div class=\"container\">Hello</div>";
        ProgramNode ast = parseTemplate(code);
        HtmlElementNode div = (HtmlElementNode) ast.getStatements().get(0);
        assertEquals("div", div.getTagName());
        assertEquals("container", div.getAttributes().get("class"));
        assertTrue(div.getChildren().get(0) instanceof TextNode);
    }

    @Test
    public void testJinjaForLoopParsing() {
        String code = "{% for item in items %}<div>{{ item }}</div>{% endfor %}";
        ProgramNode ast = parseTemplate(code);
        JinjaForNode forNode = (JinjaForNode) ast.getStatements().get(0);
        assertEquals("item", forNode.getLoopvar());
        assertTrue(forNode.getIterable() instanceof IdentifierNode);
        assertEquals("items", ((IdentifierNode) forNode.getIterable()).getName());
    }
}
