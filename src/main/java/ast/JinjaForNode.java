package ast;

import java.util.List;
import java.util.Map;

public class JinjaForNode extends TemplateNode {
    private String loopVar;
    private ASTNode iterable;
    private List<ASTNode> body;
    public JinjaForNode(String loopVar, ASTNode iterable, List<ASTNode> body) {
        this.loopVar = loopVar;
        this.iterable = iterable;
        this.body = body;
    }
    public String getLoopvar() { return loopVar; }
    public ASTNode getIterable() { return iterable; }
    public List<ASTNode> getBody() { return body; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
