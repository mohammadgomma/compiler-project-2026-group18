package ast;

import java.util.List;
import java.util.Map;

public class TextNode extends TemplateNode {
    private String text;
    public TextNode(String text) {
        this.text = text;
    }
    public String getText() { return text; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
