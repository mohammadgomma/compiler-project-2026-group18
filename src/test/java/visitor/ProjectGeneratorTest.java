package visitor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import parseTree.*;
import ast.*;
import org.antlr.v4.runtime.*;
import ANT.*;

import java.util.HashMap;

public class ProjectGeneratorTest {

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
    public void testGenerateFunction() {
        String code = "def test_func(a, b):\n    return a\n";
        ProgramNode ast = parsePython(code);
        ProjectGenerator generator = new ProjectGenerator();
        String result = generator.visit(ast);
        assertTrue(result.contains("def test_func(a, b):"));
        assertTrue(result.contains("    return a"));
    }

    @Test
    public void testGenerateHtmlTag() {
        String code = "<div class=\"container\">Hello World</div>";
        ProgramNode ast = parseTemplate(code);
        ProjectGenerator generator = new ProjectGenerator();
        String result = generator.visit(ast);
        assertEquals("<div class=\"container\">Hello World</div>", result);
    }
    
    @Test
    public void testGenerateJinjaIf() {
        String code = "{% if is_admin %}<p>Admin</p>{% endif %}";
        ProgramNode ast = parseTemplate(code);
        ProjectGenerator generator = new ProjectGenerator();
        String result = generator.visit(ast);
        assertTrue(result.contains("{% if is_admin %}"));
        assertTrue(result.contains("<p>Admin</p>"));
        assertTrue(result.contains("{% endif %}"));
    }

    @Test
    public void testComplexTemplateRoundTrip() {
        String code = "<html><head><style>.box { width: 50%; }</style></head>"
            + "<body>{% if user %}<p>{{ user.name | upper }}</p>"
            + "{% elif guest %}<p>Guest</p>{% else %}<p>Unknown</p>{% endif %}"
            + "{% for item in items %}<a href=\"/items/{{ item.id }}\">{{ item.name }}</a>{% endfor %}"
            + "</body></html>";
        ProgramNode firstAst = parseTemplate(code);
        ProjectGenerator generator = new ProjectGenerator();
        String generated = generator.visit(firstAst);

        TemplateLexer secondLexer = new TemplateLexer(CharStreams.fromString(generated));
        TemplateParser secondParser = new TemplateParser(new CommonTokenStream(secondLexer));
        ProgramNode secondAst = (ProgramNode) new TemplateASTBuilder("roundtrip.html").visit(secondParser.template());
        String regenerated = new ProjectGenerator().visit(secondAst);

        assertEquals(0, secondParser.getNumberOfSyntaxErrors());
        assertTrue(regenerated.contains(".box { width: 50%; }"));
        assertTrue(regenerated.contains("{% elif guest %}"));
        assertTrue(regenerated.contains("{% else %}"));
        assertTrue(regenerated.contains("user.name | upper"));
        assertTrue(regenerated.contains("{% for item in items %}"));
        assertTrue(regenerated.contains("href=\"/items/{{ item.id }}\""));
    }
}
