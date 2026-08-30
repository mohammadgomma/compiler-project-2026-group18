package symboltable;

public class Symbol {
    public enum Kind { VARIABLE, FUNCTION, PARAMETER, IMPORT }

    private String name;
    private Kind kind;
    private String type; // Optional: "int", "float", "list", etc.
    private int line;
    private int column;

    public Symbol(String name, Kind kind, String type, int line, int column) {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public Kind getKind() { return kind; }
    public String getType() { return type; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return String.format(java.util.Locale.ROOT, "%s (Kind: %s, Type: %s)", name, kind, type != null ? type : "any");
    }
}
