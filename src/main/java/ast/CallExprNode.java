package ast;

import java.util.List;
import java.util.Map;

public class CallExprNode extends ExpressionNode {
    private ASTNode function;
    private List<ASTNode> args;
    private Map<String, ASTNode> kwargs;
    public CallExprNode(ASTNode function, List<ASTNode> args, Map<String, ASTNode> kwargs) {
        this.function = function;
        this.args = args;
        this.kwargs = kwargs;
    }
    public ASTNode getFunction() { return function; }
    public List<ASTNode> getArgs() { return args; }
    public Map<String, ASTNode> getKwargs() { return kwargs; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
