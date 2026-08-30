package visitor;

import ast.*;

public class PrintVisitor implements ASTVisitor<String> {
    private int indentLevel = 0;

    private String indent() {
        return "  ".repeat(indentLevel);
    }

    private String formatNode(ASTNode node, String name, String details) {
        String fileStr = (node.getFile() != null) ? "File: " + node.getFile() + ", " : "";
        String loc = (node.getLine() > 0) ? String.format(java.util.Locale.ROOT, " [%sLine: %d, Col: %d]", fileStr, node.getLine(), node.getColumn()) : "";
        return indent() + name + loc + (details.isEmpty() ? "" : " (" + details + ")") + "\n";
    }

    @Override
    public String visit(ProgramNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "ProgramNode", ""));
        indentLevel++;
        for (ASTNode stmt : node.getStatements()) {
            if (stmt != null) sb.append(stmt.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(FunctionDefNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "FunctionDefNode", node.getName() + "(" + String.join(", ", node.getParams()) + ")"));
        indentLevel++;
        if (node.getDecorators() != null && !node.getDecorators().isEmpty()) {
            sb.append(indent()).append("Decorators:\n");
            for (DecoratorNode dec : node.getDecorators()) {
                if (dec != null) sb.append(dec.accept(this));
            }
        }
        sb.append(indent()).append("Body:\n");
        for (ASTNode stmt : node.getBody()) {
            if (stmt != null) sb.append(stmt.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(IfNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "IfNode", ""));
        indentLevel++;
        sb.append(indent()).append("Condition:\n");
        indentLevel++;
        if (node.getCondition() != null) sb.append(node.getCondition().accept(this));
        indentLevel--;
        sb.append(indent()).append("IfBody:\n");
        indentLevel++;
        for (ASTNode stmt : node.getIfbody()) {
            if (stmt != null) sb.append(stmt.accept(this));
        }
        indentLevel--;
        for (int i = 0; i < node.getElifconditions().size(); i++) {
            sb.append(indent()).append("ElifBranch ").append(i + 1).append(":\n");
            indentLevel++;
            sb.append(indent()).append("Condition:\n");
            indentLevel++;
            ASTNode elifCondition = node.getElifconditions().get(i);
            if (elifCondition != null) sb.append(elifCondition.accept(this));
            indentLevel--;
            sb.append(indent()).append("Body:\n");
            indentLevel++;
            for (ASTNode stmt : node.getElifbodies().get(i)) {
                if (stmt != null) sb.append(stmt.accept(this));
            }
            indentLevel--;
            indentLevel--;
        }
        if (!node.getElsebody().isEmpty()) {
            sb.append(indent()).append("ElseBody:\n");
            indentLevel++;
            for (ASTNode stmt : node.getElsebody()) {
                if (stmt != null) sb.append(stmt.accept(this));
            }
            indentLevel--;
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(AssignNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "AssignNode", ""));
        indentLevel++;
        if (node.getTarget() != null) sb.append(node.getTarget().accept(this));
        if (node.getValue() != null) sb.append(node.getValue().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(ReturnNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "ReturnNode", ""));
        indentLevel++;
        if (node.getValue() != null) sb.append(node.getValue().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(ImportNode node) {
        return formatNode(node, "ImportNode", node.getModulename() + " " + node.getNames());
    }

    @Override
    public String visit(BinaryExprNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "BinaryExprNode", node.getOperator()));
        indentLevel++;
        if (node.getLeft() != null) sb.append(node.getLeft().accept(this));
        if (node.getRight() != null) sb.append(node.getRight().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(UnaryExprNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "UnaryExprNode", node.getOperator()));
        indentLevel++;
        if (node.getExpr() != null) sb.append(node.getExpr().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(CallExprNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "CallExprNode", ""));
        indentLevel++;
        if (node.getFunction() != null) sb.append(node.getFunction().accept(this));
        if (!node.getArgs().isEmpty()) {
            sb.append(indent()).append("Args:\n");
            for (ASTNode arg : node.getArgs()) {
                if (arg != null) sb.append(arg.accept(this));
            }
        }
        if (node.getKwargs() != null && !node.getKwargs().isEmpty()) {
            sb.append(indent()).append("Kwargs:\n");
            for (java.util.Map.Entry<String, ASTNode> entry : node.getKwargs().entrySet()) {
                sb.append(indent()).append(entry.getKey()).append("=\n");
                if (entry.getValue() != null) sb.append(entry.getValue().accept(this));
            }
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(AttributeExprNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "AttributeExprNode", "." + node.getAttribute()));
        indentLevel++;
        if (node.getObject() != null) sb.append(node.getObject().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(IndexExprNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "IndexExprNode", ""));
        indentLevel++;
        if (node.getObject() != null) sb.append(node.getObject().accept(this));
        if (node.getIndex() != null) sb.append(node.getIndex().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(LiteralNode node) {
        return formatNode(node, "LiteralNode", node.getValue() + " [" + node.getType() + "]");
    }

    @Override
    public String visit(IdentifierNode node) {
        return formatNode(node, "IdentifierNode", node.getName());
    }

    @Override
    public String visit(ListNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "ListNode", ""));
        indentLevel++;
        for (ASTNode el : node.getElements()) {
            if (el != null) sb.append(el.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(DictNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "DictNode", ""));
        indentLevel++;
        for (int i=0; i<node.getKeys().size(); i++) {
            if (node.getKeys().get(i) != null) sb.append(node.getKeys().get(i).accept(this));
            if (node.getValues().get(i) != null) sb.append(node.getValues().get(i).accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(HtmlElementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "HtmlElementNode", "<" + node.getTagName() + "> attrs=" + node.getAttributes()));
        indentLevel++;
        for (ASTNode child : node.getChildren()) {
            if (child != null) sb.append(child.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(JinjaExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "JinjaExpressionNode", "filters=" + node.getFilters()));
        indentLevel++;
        if (node.getExpr() != null) sb.append(node.getExpr().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(JinjaIfNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "JinjaIfNode", ""));
        indentLevel++;
        if (node.getCondition() != null) sb.append(node.getCondition().accept(this));
        sb.append(indent()).append("IfBody:\n");
        for (ASTNode child : node.getIfbody()) {
            if (child != null) sb.append(child.accept(this));
        }
        
        for (int i = 0; i < node.getElifConditions().size(); i++) {
            sb.append(indent()).append("ElifCondition:\n");
            if (node.getElifConditions().get(i) != null) sb.append(node.getElifConditions().get(i).accept(this));
            sb.append(indent()).append("ElifBody:\n");
            for (ASTNode child : node.getElifBodies().get(i)) {
                if (child != null) sb.append(child.accept(this));
            }
        }
        
        if (!node.getElsebody().isEmpty()) {
            sb.append(indent()).append("ElseBody:\n");
            for (ASTNode child : node.getElsebody()) {
                if (child != null) sb.append(child.accept(this));
            }
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(JinjaForNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "JinjaForNode", node.getLoopvar()));
        indentLevel++;
        if (node.getIterable() != null) sb.append(node.getIterable().accept(this));
        for (ASTNode child : node.getBody()) {
            if (child != null) sb.append(child.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(JinjaSetNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "JinjaSetNode", node.getTarget()));
        indentLevel++;
        if (node.getValue() != null) sb.append(node.getValue().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(TextNode node) {
        String text = node.getText().trim().replace("\n", " ");
        if (text.length() > 30) text = text.substring(0, 30) + "...";
        if (text.isEmpty()) return "";
        return formatNode(node, "TextNode", "\"" + text + "\"");
    }

    @Override
    public String visit(CssStyleNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "CssStyleNode", "attrs=" + node.getAttributes()));
        indentLevel++;
        sb.append(indent()).append("Content:\n");
        indentLevel++;
        for (ASTNode child : node.getContent()) {
            if (child != null) sb.append(child.accept(this));
        }
        indentLevel -= 2;
        return sb.toString();
    }

    @Override
    public String visit(DecoratorNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatNode(node, "DecoratorNode", node.getName()));
        indentLevel++;
        if (node.getArgs() != null && !node.getArgs().isEmpty()) {
            sb.append(indent()).append("Args:\n");
            for (ASTNode arg : node.getArgs()) {
                if (arg != null) sb.append(arg.accept(this));
            }
        }
        if (node.getKwargs() != null && !node.getKwargs().isEmpty()) {
            sb.append(indent()).append("Kwargs:\n");
            for (java.util.Map.Entry<String, ASTNode> entry : node.getKwargs().entrySet()) {
                sb.append(indent()).append(entry.getKey()).append("=\n");
                if (entry.getValue() != null) sb.append(entry.getValue().accept(this));
            }
        }
        indentLevel--;
        return sb.toString();
    }
}
