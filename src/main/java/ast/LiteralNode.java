package ast;

import java.util.List;
import java.util.Map;

public class LiteralNode extends ExpressionNode {
    private String value;
    private String type;
    public LiteralNode(String value, String type) {
        this.value = value;
        this.type = type;
    }
    public String getValue() { return value; }
    public String getType() { return type; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
