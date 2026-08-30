package css;

import ANT.CssLexer;
import ANT.CssParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CssParserTest {

    private int countSyntaxErrors(String css) {
        CountingErrorListener errors = new CountingErrorListener();
        CssLexer lexer = new CssLexer(CharStreams.fromString(css));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errors);
        CssParser parser = new CssParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        parser.stylesheet();
        return errors.count;
    }

    @Test
    public void parsesStandaloneCssWithPercentagePseudoSelectorAndMediaQuery() {
        String css = "body { color: red; }\n"
            + "a:hover { color: #fff; }\n"
            + ".box { width: 50%; margin: 10px; }\n"
            + "@media screen and (max-width: 600px) { body { margin: 0; } }\n";
        assertEquals(0, countSyntaxErrors(css));
    }

    @Test
    public void rejectsUnclosedCssBlock() {
        assertTrue(countSyntaxErrors("body { color: red;") > 0);
    }

    private static class CountingErrorListener extends BaseErrorListener {
        private int count = 0;

        @Override
        public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e
        ) {
            count++;
        }
    }
}
