package ast;

import java.util.List;
import java.util.Map;

public class UnaryExprNode extends ExpressionNode {
    private String operator;
    private ASTNode expr;
    public UnaryExprNode(String operator, ASTNode expr) {
        this.operator = operator;
        this.expr = expr;
    }
    public String getOperator() { return operator; }
    public ASTNode getExpr() { return expr; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
