package ast;

import java.util.List;
import java.util.Map;

public class DictNode extends ExpressionNode {
    private List<ASTNode> keys;
    private List<ASTNode> values;
    public DictNode(List<ASTNode> keys, List<ASTNode> values) {
        this.keys = keys;
        this.values = values;
    }
    public List<ASTNode> getKeys() { return keys; }
    public List<ASTNode> getValues() { return values; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
