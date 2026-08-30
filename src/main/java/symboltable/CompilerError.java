package symboltable;

public class CompilerError {
    public enum Severity { ERROR, WARNING }
    
    private String code;
    private Severity severity;
    private String message;
    private String file;
    private int line;
    private int column;

    public CompilerError(String code, Severity severity, String message, String file, int line, int column) {
        this.code = code;
        this.severity = severity;
        this.message = message;
        this.file = file;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.ROOT, "[%s] %s at %s:%d:%d - %s", severity, code, file != null ? file : "unknown", line, column, message);
    }

    public String getCode() { return code; }
    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public String getFile() { return file; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}
