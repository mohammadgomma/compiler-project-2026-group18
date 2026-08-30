package ast;

import java.util.List;
import java.util.Map;

public class ReturnNode extends PythonNode {
    private ASTNode value;
    public ReturnNode(ASTNode value) {
        this.value = value;
    }
    public ASTNode getValue() { return value; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
