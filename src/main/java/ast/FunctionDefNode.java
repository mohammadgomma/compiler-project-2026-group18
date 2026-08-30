package ast;

import java.util.List;
import java.util.Map;

public class FunctionDefNode extends PythonNode {
    private String name;
    private List<String> params;
    private List<ASTNode> body;
    private List<DecoratorNode> decorators;
    public FunctionDefNode(String name, List<String> params, List<ASTNode> body, List<DecoratorNode> decorators) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.decorators = decorators;
    }
    public String getName() { return name; }
    public List<String> getParams() { return params; }
    public List<ASTNode> getBody() { return body; }
    public List<DecoratorNode> getDecorators() { return decorators; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
