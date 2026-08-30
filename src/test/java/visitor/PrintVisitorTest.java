package visitor;

import ANT.PythonLexer;
import ANT.PythonParser;
import ANT.TemplateLexer;
import ANT.TemplateParser;
import ast.ProgramNode;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import parseTree.PythonASTBuilder;
import parseTree.TemplateASTBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrintVisitorTest {

    private ProgramNode parsePython(String code) {
        PythonLexer lexer = new PythonLexer(CharStreams.fromString(code));
        PythonParser parser = new PythonParser(new CommonTokenStream(lexer));
        return (ProgramNode) new PythonASTBuilder("printer.py").visit(parser.program());
    }

    private ProgramNode parseTemplate(String code) {
        TemplateLexer lexer = new TemplateLexer(CharStreams.fromString(code));
        TemplateParser parser = new TemplateParser(new CommonTokenStream(lexer));
        return (ProgramNode) new TemplateASTBuilder("printer.html").visit(parser.template());
    }

    @Test
    public void printsDecoratorsKwargsAndAllPythonBranches() {
        String code = "@app.route('/x', methods=['GET'])\n"
            + "def f(value):\n"
            + "    if value == 1:\n"
            + "        return render_template('one.html', title='One')\n"
            + "    elif value == 2:\n"
            + "        return 'two'\n"
            + "    else:\n"
            + "        return 'other'\n";

        String output = parsePython(code).accept(new PrintVisitor());

        assertTrue(output.contains("DecoratorNode"));
        assertTrue(output.contains("methods="));
        assertTrue(output.contains("title="));
        assertTrue(output.contains("ElifBranch 1"));
        assertTrue(output.contains("ElseBody"));
        assertTrue(output.contains("File: printer.py"));
        assertFalse(output.contains("ast.DecoratorNode@"));
    }

    @Test
    public void printsEmbeddedCssContent() {
        String output = parseTemplate("<style>body { color: red; }</style>")
            .accept(new PrintVisitor());

        assertTrue(output.contains("CssStyleNode"));
        assertTrue(output.contains("Content:"));
        assertTrue(output.contains("color: red"));
        assertTrue(output.contains("File: printer.html"));
    }
}
