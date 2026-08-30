package ast;

import java.util.List;
import java.util.Map;

public class AttributeExprNode extends ExpressionNode {
    private ASTNode object;
    private String attribute;
    public AttributeExprNode(ASTNode object, String attribute) {
        this.object = object;
        this.attribute = attribute;
    }
    public ASTNode getObject() { return object; }
    public String getAttribute() { return attribute; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
