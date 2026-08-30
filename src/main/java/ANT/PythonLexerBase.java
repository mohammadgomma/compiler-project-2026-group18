package ANT;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CommonToken;

public abstract class PythonLexerBase extends Lexer {
    private java.util.Stack<Integer> indents = new java.util.Stack<>();
    private java.util.Queue<Token> tokens = new java.util.LinkedList<>();
    private int openedBrackets = 0;

    public PythonLexerBase(CharStream input) {
        super(input);
    }

    @Override
    public void emit(Token t) {
        super.setToken(t);
        tokens.offer(t);
    }

    @Override
    public Token nextToken() {
        if (_input.LA(1) == CharStream.EOF && !this.indents.isEmpty()) {
            for (int i = indents.size() - 1; i >= 0; i--) {
                emitToken(PythonLexer.DEDENT);
            }
            indents.clear();
            emitToken(PythonLexer.EOF);
        }
        Token next = super.nextToken();

        if (tokens.isEmpty()) {
            return next;
        }
        return tokens.poll();
    }

    protected void emitToken(int type) {
        CommonToken token = new CommonToken(type, "");
        token.setLine(getLine());
        token.setCharPositionInLine(getCharPositionInLine());
        emit(token);
    }

    public void incBrackets() {
        openedBrackets++;
    }

    public void decBrackets() {
        if (openedBrackets > 0) openedBrackets--;
    }

    protected void handleNewLine() {
        // If we're inside brackets, ignore newlines and indentation entirely.
        if (openedBrackets > 0) {
            // Do not emit NEWLINE, INDENT, or DEDENT. We skip it completely.
            skip();
            return;
        }

        String text = getText();

        // Only emit NEWLINE if the previous token wasn't also a NEWLINE
        // But for simplicity in a basic lexer, we can just emit it.
        emit(new CommonToken(PythonLexer.NEWLINE, text));
        
        int indent = getIndentationLength(text);
        int previous = indents.isEmpty() ? 0 : indents.peek();
        
        if (indent > previous) {
            indents.push(indent);
            emitToken(PythonLexer.INDENT);
        } else {
            while (!indents.isEmpty() && indents.peek() > indent) {
                emitToken(PythonLexer.DEDENT);
                indents.pop();
            }
        }
    }

    private int getIndentationLength(String c) {
        int length = 0;
        // The text might be \n followed by spaces. Find the last newline and count spaces after it.
        int lastNewline = c.lastIndexOf('\n');
        int start = lastNewline != -1 ? lastNewline + 1 : 0;
        for (int i = start; i < c.length(); i++) {
            if (c.charAt(i) == ' ') length++;
            else if (c.charAt(i) == '\t') length += 8;
        }
        return length;
    }
}
