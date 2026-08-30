package ast;

import java.util.List;
import java.util.Map;

public class DecoratorNode extends PythonNode {
    private String name;
    private List<ASTNode> args;
    private Map<String, ASTNode> kwargs;

    public DecoratorNode(String name, List<ASTNode> args, Map<String, ASTNode> kwargs) {
        this.name = name;
        this.args = args;
        this.kwargs = kwargs;
    }

    public String getName() { return name; }
    public List<ASTNode> getArgs() { return args; }
    public Map<String, ASTNode> getKwargs() { return kwargs; }

    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
