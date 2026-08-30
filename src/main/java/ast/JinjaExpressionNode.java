package ast;

import java.util.List;
import java.util.Map;

public class JinjaExpressionNode extends ExpressionNode {
    private ASTNode expr;
    private List<String> filters;
    public JinjaExpressionNode(ASTNode expr, List<String> filters) {
        this.expr = expr;
        this.filters = filters;
    }
    public ASTNode getExpr() { return expr; }
    public List<String> getFilters() { return filters; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
