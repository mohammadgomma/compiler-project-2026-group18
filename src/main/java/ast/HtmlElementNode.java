package ast;

import java.util.List;
import java.util.Map;

public class HtmlElementNode extends TemplateNode {
    private String tagName;
    private Map<String, String> attributes;
    private List<ASTNode> children;
    private boolean isSelfClosing;

    public HtmlElementNode(String tagName, Map<String, String> attributes, List<ASTNode> children, boolean isSelfClosing) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.children = children;
        this.isSelfClosing = isSelfClosing;
    }

    public String getTagName() { return tagName; }
    public Map<String, String> getAttributes() { return attributes; }
    public List<ASTNode> getChildren() { return children; }
    public boolean getIsSelfClosing() { return isSelfClosing; }

    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
