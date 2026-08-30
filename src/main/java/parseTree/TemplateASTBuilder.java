package parseTree;

import ANT.*;
import ast.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateASTBuilder extends TemplateParserBaseVisitor<ASTNode> {

    private String fileName;

    public TemplateASTBuilder(String fileName) {
        this.fileName = fileName;
    }

    private void setLocation(ASTNode node, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (node != null && ctx != null && ctx.getStart() != null) {
            node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), fileName);
        }
    }

    @Override
    public ASTNode visitTemplate(TemplateParser.TemplateContext ctx) {
        List<ASTNode> statements = new ArrayList<>();
        for (TemplateParser.ElementContext eCtx : ctx.element()) {
            ASTNode stmt = visit(eCtx);
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        ProgramNode node = new ProgramNode(statements);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitElement(TemplateParser.ElementContext ctx) {
        if (ctx.htmlElement() != null) return visit(ctx.htmlElement());
        if (ctx.jinjaExpression() != null) return visit(ctx.jinjaExpression());
        if (ctx.jinjaBlock() != null) return visit(ctx.jinjaBlock());
        if (ctx.styleElement() != null) return visit(ctx.styleElement());
        if (ctx.TEXT() != null) {
            TextNode node = new TextNode(ctx.TEXT().getText());
            setLocation(node, ctx);
            return node;
        }
        return null;
    }

    @Override
    public ASTNode visitHtmlElement(TemplateParser.HtmlElementContext ctx) {
        if (ctx.OPEN_DOCTYPE() != null) {
            Map<String, String> attrs = new HashMap<>();
            HtmlElementNode node = new HtmlElementNode("!DOCTYPE", attrs, new ArrayList<>(), true);
            setLocation(node, ctx);
            return node;
        }
        
        String tagName = ctx.htmlParts().HTML_NAME().getText();
        Map<String, String> attributes = new HashMap<>();
        for (TemplateParser.HtmlAttributeContext attrCtx : ctx.htmlParts().htmlAttribute()) {
            String name = attrCtx.HTML_NAME().getText();
            String value = attrCtx.HTML_STRING() != null ? attrCtx.HTML_STRING().getText() : "";
            if (value.startsWith("\"") || value.startsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }
            attributes.put(name, value);
        }
        
        List<ASTNode> children = new ArrayList<>();
        for (TemplateParser.ElementContext eCtx : ctx.element()) {
            ASTNode child = visit(eCtx);
            if (child != null) children.add(child);
        }
        
        boolean isSelfClosing = ctx.HTML_SLASH_CLOSE() != null;
        HtmlElementNode node = new HtmlElementNode(tagName, attributes, children, isSelfClosing);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitStyleElement(TemplateParser.StyleElementContext ctx) {
        Map<String, String> attributes = new HashMap<>();
        for (TemplateParser.StyleAttributeContext attrCtx : ctx.styleAttribute()) {
            String name = attrCtx.STYLE_ATTR_NAME().getText();
            String value = attrCtx.STYLE_ATTR_VALUE() != null ? attrCtx.STYLE_ATTR_VALUE().getText() : "";
            attributes.put(name, value);
        }
        
        List<ASTNode> content = new ArrayList<>();
        for (TemplateParser.CssContentContext cCtx : ctx.cssContent()) {
            if (cCtx.CSS_CONTENT() != null) {
                TextNode node = new TextNode(cCtx.CSS_CONTENT().getText());
                setLocation(node, cCtx);
                content.add(node);
            } else if (cCtx.CSS_LBRACE() != null) {
                TextNode node = new TextNode("{");
                setLocation(node, cCtx);
                content.add(node);
            } else if (cCtx.CSS_RBRACE() != null) {
                TextNode node = new TextNode("}");
                setLocation(node, cCtx);
                content.add(node);
            } else if (cCtx.jinjaExpression() != null) {
                content.add(visit(cCtx.jinjaExpression()));
            } else if (cCtx.jinjaBlock() != null) {
                content.add(visit(cCtx.jinjaBlock()));
            }
        }
        CssStyleNode node = new CssStyleNode(attributes, content);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitJinjaExpression(TemplateParser.JinjaExpressionContext ctx) {
        ASTNode expr = visit(ctx.jExpr());
        List<String> filters = new ArrayList<>();
        for (TemplateParser.JFilterContext fCtx : ctx.jExpr().jFilter()) {
            if (fCtx.J_ID() != null) {
                filters.add(fCtx.J_ID().getText());
            }
        }
        JinjaExpressionNode node = new JinjaExpressionNode(expr, filters);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitJExpr(TemplateParser.JExprContext ctx) {
        ASTNode left = visit(ctx.jAtom());
        int childCount = ctx.getChildCount();
        for (int i = 1; i < childCount; i++) {
            org.antlr.v4.runtime.tree.ParseTree c = ctx.getChild(i);
            if (c instanceof TemplateParser.JFilterContext) {
                // Skip filter here, handled in jinjaExpression
                continue;
            } else if (c instanceof TerminalNode) {
                TerminalNode tn = (TerminalNode) c;
                if (tn.getSymbol().getType() == TemplateLexer.J_DOT) {
                    String attr = ctx.getChild(i+1).getText();
                    left = new AttributeExprNode(left, attr);
                    setLocation(left, ctx);
                    i++;
                } else if (tn.getSymbol().getType() == TemplateLexer.J_LBRACKET) {
                    ASTNode index = visit(ctx.getChild(i+1));
                    left = new IndexExprNode(left, index);
                    setLocation(left, ctx);
                    i += 2; // skip index and RBRACKET
                }
            }
        }
        return left;
    }

    @Override
    public ASTNode visitJAtom(TemplateParser.JAtomContext ctx) {
        ASTNode node;
        if (ctx.J_ID() != null) node = new IdentifierNode(ctx.J_ID().getText());
        else if (ctx.J_NUMBER() != null) node = new LiteralNode(ctx.J_NUMBER().getText(), "number");
        else if (ctx.J_STRING() != null) node = new LiteralNode(ctx.J_STRING().getText(), "string");
        else node = new IdentifierNode(ctx.getText());
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitIfBlock(TemplateParser.IfBlockContext ctx) {
        ASTNode condition = visit(ctx.jbExpr(0));
        
        List<ASTNode> ifBody = new ArrayList<>();
        List<ASTNode> elifConditions = new ArrayList<>();
        List<List<ASTNode>> elifBodies = new ArrayList<>();
        List<ASTNode> elseBody = new ArrayList<>();
        
        int nextExprIdx = 1;
        List<ASTNode> currentBody = ifBody;
        
        for (int i = 0; i < ctx.getChildCount(); i++) {
            org.antlr.v4.runtime.tree.ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                if (child.getText().equals("elif")) {
                    elifConditions.add(visit(ctx.jbExpr(nextExprIdx++)));
                    currentBody = new ArrayList<>();
                    elifBodies.add(currentBody);
                } else if (child.getText().equals("else")) {
                    currentBody = elseBody;
                }
            } else if (child instanceof TemplateParser.ElementContext || child instanceof TemplateParser.CssContentContext) {
                ASTNode stmt = visit(child);
                if (stmt != null) {
                    currentBody.add(stmt);
                }
            }
        }
        
        JinjaIfNode node = new JinjaIfNode(condition, ifBody, elifConditions, elifBodies, elseBody);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitForBlock(TemplateParser.ForBlockContext ctx) {
        String loopVar = ctx.JB_ID().getText();
        ASTNode iterable = visit(ctx.jbExpr());
        List<ASTNode> body = new ArrayList<>();
        
        for (TemplateParser.ElementContext eCtx : ctx.element()) {
            ASTNode stmt = visit(eCtx);
            if (stmt != null) body.add(stmt);
        }
        
        JinjaForNode node = new JinjaForNode(loopVar, iterable, body);
        setLocation(node, ctx);
        return node;
    }
    
    @Override
    public ASTNode visitJbExpr(TemplateParser.JbExprContext ctx) {
        ASTNode left = visit(ctx.jbAtom());
        int childCount = ctx.getChildCount();
        for (int i = 1; i < childCount; i++) {
            org.antlr.v4.runtime.tree.ParseTree c = ctx.getChild(i);
            if (c instanceof TerminalNode) {
                TerminalNode tn = (TerminalNode) c;
                if (tn.getSymbol().getType() == TemplateLexer.JB_DOT) {
                    String attr = ctx.getChild(i+1).getText();
                    left = new AttributeExprNode(left, attr);
                    setLocation(left, ctx);
                    i++;
                } else if (tn.getSymbol().getType() == TemplateLexer.JB_LBRACKET) {
                    ASTNode index = visit(ctx.getChild(i+1));
                    left = new IndexExprNode(left, index);
                    setLocation(left, ctx);
                    i += 2;
                }
            }
        }
        return left;
    }

    @Override
    public ASTNode visitJbAtom(TemplateParser.JbAtomContext ctx) {
        ASTNode node;
        if (ctx.JB_ID() != null) node = new IdentifierNode(ctx.JB_ID().getText());
        else if (ctx.JB_NUMBER() != null) node = new LiteralNode(ctx.JB_NUMBER().getText(), "number");
        else if (ctx.JB_STRING() != null) node = new LiteralNode(ctx.JB_STRING().getText(), "string");
        else node = new IdentifierNode(ctx.getText());
        setLocation(node, ctx);
        return node;
    }

}
