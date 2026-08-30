package ast;

import java.util.List;
import java.util.Map;

public class JinjaSetNode extends TemplateNode {
    private String target;
    private ASTNode value;
    public JinjaSetNode(String target, ASTNode value) {
        this.target = target;
        this.value = value;
    }
    public String getTarget() { return target; }
    public ASTNode getValue() { return value; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
