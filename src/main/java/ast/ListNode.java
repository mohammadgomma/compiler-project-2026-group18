package ast;

import java.util.List;
import java.util.Map;

public class ListNode extends ExpressionNode {
    private List<ASTNode> elements;
    public ListNode(List<ASTNode> elements) {
        this.elements = elements;
    }
    public List<ASTNode> getElements() { return elements; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
