package ast;

import java.util.List;
import java.util.Map;

public class CssStyleNode extends TemplateNode {
    private Map<String, String> attributes;
    private List<ASTNode> content;

    public CssStyleNode(Map<String, String> attributes, List<ASTNode> content) {
        this.attributes = attributes;
        this.content = content;
    }

    public Map<String, String> getAttributes() { return attributes; }
    public List<ASTNode> getContent() { return content; }

    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
