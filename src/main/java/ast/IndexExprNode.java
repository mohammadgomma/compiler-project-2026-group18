package ast;

import java.util.List;
import java.util.Map;

public class IndexExprNode extends ExpressionNode {
    private ASTNode object;
    private ASTNode index;
    public IndexExprNode(ASTNode object, ASTNode index) {
        this.object = object;
        this.index = index;
    }
    public ASTNode getObject() { return object; }
    public ASTNode getIndex() { return index; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
