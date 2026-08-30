package ast;

import java.util.List;
import java.util.Map;

public class AssignNode extends ASTNode {
    private ASTNode target;
    private ASTNode value;
    public AssignNode(ASTNode target, ASTNode value) {
        this.target = target;
        this.value = value;
    }
    public ASTNode getTarget() { return target; }
    public ASTNode getValue() { return value; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
