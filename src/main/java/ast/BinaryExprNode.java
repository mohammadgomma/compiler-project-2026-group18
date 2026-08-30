package ast;

import java.util.List;
import java.util.Map;

public class BinaryExprNode extends ExpressionNode {
    private ASTNode left;
    private String operator;
    private ASTNode right;
    public BinaryExprNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    public ASTNode getLeft() { return left; }
    public String getOperator() { return operator; }
    public ASTNode getRight() { return right; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
