package cli;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class BailErrorListener extends BaseErrorListener {
    public static final BailErrorListener INSTANCE = new BailErrorListener();
    private boolean hasErrors = false;
    private int errorCount = 0;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
        hasErrors = true;
        errorCount++;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public int getErrorCount() {
        return errorCount;
    }
}
