package ast;

import java.util.List;
import java.util.Map;

public class IfNode extends PythonNode {
    private ASTNode condition;
    private List<ASTNode> ifBody;
    private List<ASTNode> elifConditions;
    private List<List<ASTNode>> elifBodies;
    private List<ASTNode> elseBody;
    public IfNode(ASTNode condition, List<ASTNode> ifBody, List<ASTNode> elifConditions, List<List<ASTNode>> elifBodies, List<ASTNode> elseBody) {
        this.condition = condition;
        this.ifBody = ifBody;
        this.elifConditions = elifConditions;
        this.elifBodies = elifBodies;
        this.elseBody = elseBody;
    }
    public ASTNode getCondition() { return condition; }
    public List<ASTNode> getIfbody() { return ifBody; }
    public List<ASTNode> getElifconditions() { return elifConditions; }
    public List<List<ASTNode>> getElifbodies() { return elifBodies; }
    public List<ASTNode> getElsebody() { return elseBody; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
