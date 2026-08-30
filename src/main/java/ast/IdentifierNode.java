package ast;

import java.util.List;
import java.util.Map;

public class IdentifierNode extends ExpressionNode {
    private String name;
    public IdentifierNode(String name) {
        this.name = name;
    }
    public String getName() { return name; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
