package ast;

import java.util.List;
import java.util.Map;

public class ProgramNode extends ASTNode {
    private List<ASTNode> statements;
    public ProgramNode(List<ASTNode> statements) {
        this.statements = statements;
    }
    public List<ASTNode> getStatements() { return statements; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
