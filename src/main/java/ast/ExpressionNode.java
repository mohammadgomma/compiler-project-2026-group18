package ast;

public abstract class ExpressionNode extends PythonNode {
    private int line;
    private int column;
    private String file;

    public void setLocation(int line, int column, String file) {
        this.line = line;
        this.column = column;
        this.file = file;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getFile() { return file; }

    public abstract <T> T accept(visitor.ASTVisitor<T> visitor);
}
