package visitor;

import ast.*;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ProjectGenerator implements ASTVisitor<String> {
    private int indentLevel = 0;
    
    private String indent() {
        return "    ".repeat(indentLevel);
    }
    
    public void generateProject(ProgramNode pythonAst, String outputDir) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();
        
        String pyCode = pythonAst.accept(this);
        Files.writeString(new File(dir, "app.py").toPath(), pyCode, StandardCharsets.UTF_8);
        Files.writeString(
            new File(dir, "requirements.txt").toPath(),
            "Flask>=3.0,<4.0\n",
            StandardCharsets.UTF_8
        );
    }

    @Override
    public String visit(ProgramNode node) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode stmt : node.getStatements()) {
            if (stmt != null) {
                sb.append(stmt.accept(this));
                if (!(stmt instanceof HtmlElementNode || stmt instanceof JinjaIfNode || stmt instanceof JinjaForNode || stmt instanceof JinjaSetNode || stmt instanceof JinjaExpressionNode || stmt instanceof TextNode)) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String visit(FunctionDefNode node) {
        StringBuilder sb = new StringBuilder();
        for (DecoratorNode dec : node.getDecorators()) {
            sb.append(indent()).append("@").append(dec.accept(this)).append("\n");
        }
        sb.append(indent()).append("def ").append(node.getName()).append("(");
        sb.append(String.join(", ", node.getParams())).append("):\n");
        
        indentLevel++;
        if (node.getBody().isEmpty()) {
            sb.append(indent()).append("pass\n");
        } else {
            for (ASTNode stmt : node.getBody()) {
                if (stmt != null) sb.append(indent()).append(stmt.accept(this)).append("\n");
            }
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(DecoratorNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getName());
        if (!node.getArgs().isEmpty() || !node.getKwargs().isEmpty()) {
            sb.append("(");
            List<String> argsList = new ArrayList<>();
            for (ASTNode arg : node.getArgs()) {
                argsList.add(arg.accept(this));
            }
            for (java.util.Map.Entry<String, ASTNode> entry : node.getKwargs().entrySet()) {
                argsList.add(entry.getKey() + "=" + entry.getValue().accept(this));
            }
            sb.append(String.join(", ", argsList));
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public String visit(IfNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("if ");
        if (node.getCondition() != null) sb.append(node.getCondition().accept(this));
        sb.append(":\n");
        
        indentLevel++;
        if (node.getIfbody().isEmpty()) sb.append(indent()).append("pass\n");
        else {
            for (ASTNode stmt : node.getIfbody()) {
                if (stmt != null) sb.append(indent()).append(stmt.accept(this)).append("\n");
            }
        }
        indentLevel--;
        
        for (int i=0; i < node.getElifconditions().size(); i++) {
            sb.append(indent()).append("elif ");
            if (node.getElifconditions().get(i) != null) sb.append(node.getElifconditions().get(i).accept(this));
            sb.append(":\n");
            indentLevel++;
            if (node.getElifbodies().get(i).isEmpty()) sb.append(indent()).append("pass\n");
            else {
                for (ASTNode stmt : node.getElifbodies().get(i)) {
                    if (stmt != null) sb.append(indent()).append(stmt.accept(this)).append("\n");
                }
            }
            indentLevel--;
        }
        
        if (!node.getElsebody().isEmpty()) {
            sb.append(indent()).append("else:\n");
            indentLevel++;
            for (ASTNode stmt : node.getElsebody()) {
                if (stmt != null) sb.append(indent()).append(stmt.accept(this)).append("\n");
            }
            indentLevel--;
        }
        
        return sb.toString().trim();
    }

    @Override
    public String visit(AssignNode node) {
        String target = "";
        if (node.getTarget() != null) {
            target = node.getTarget().accept(this);
        }
        String val = (node.getValue() != null) ? node.getValue().accept(this) : "None";
        return target + " = " + val;
    }

    @Override
    public String visit(ReturnNode node) {
        String val = node.getValue() != null ? node.getValue().accept(this) : "";
        return "return " + val;
    }

    @Override
    public String visit(ImportNode node) {
        if (!node.getModulename().isEmpty()) {
            return "from " + node.getModulename() + " import " + String.join(", ", node.getNames());
        } else {
            return "import " + String.join(", ", node.getNames());
        }
    }

    @Override
    public String visit(BinaryExprNode node) {
        String l = node.getLeft() != null ? node.getLeft().accept(this) : "";
        String r = node.getRight() != null ? node.getRight().accept(this) : "";
        return l + " " + node.getOperator() + " " + r;
    }

    @Override
    public String visit(UnaryExprNode node) {
        String e = node.getExpr() != null ? node.getExpr().accept(this) : "";
        if (node.getOperator().equals("not")) return "not " + e;
        return node.getOperator() + e;
    }

    @Override
    public String visit(CallExprNode node) {
        String func = node.getFunction() != null ? node.getFunction().accept(this) : "";
        List<String> argsList = new ArrayList<>();
        for (ASTNode a : node.getArgs()) {
            if (a != null) argsList.add(a.accept(this));
        }
        if (node.getKwargs() != null) {
            for (java.util.Map.Entry<String, ASTNode> entry : node.getKwargs().entrySet()) {
                argsList.add(entry.getKey() + "=" + entry.getValue().accept(this));
            }
        }
        return func + "(" + String.join(", ", argsList) + ")";
    }

    @Override
    public String visit(AttributeExprNode node) {
        String obj = node.getObject() != null ? node.getObject().accept(this) : "";
        return obj + "." + node.getAttribute();
    }

    @Override
    public String visit(IndexExprNode node) {
        String obj = node.getObject() != null ? node.getObject().accept(this) : "";
        String idx = node.getIndex() != null ? node.getIndex().accept(this) : "";
        return obj + "[" + idx + "]";
    }

    @Override
    public String visit(LiteralNode node) {
        return node.getValue();
    }

    @Override
    public String visit(IdentifierNode node) {
        return node.getName();
    }

    @Override
    public String visit(ListNode node) {
        List<String> els = new ArrayList<>();
        for (ASTNode e : node.getElements()) {
            if (e != null) els.add(e.accept(this));
        }
        return "[" + String.join(", ", els) + "]";
    }

    @Override
    public String visit(DictNode node) {
        List<String> pairs = new ArrayList<>();
        for (int i=0; i<node.getKeys().size(); i++) {
            String k = node.getKeys().get(i) != null ? node.getKeys().get(i).accept(this) : "";
            String v = node.getValues().get(i) != null ? node.getValues().get(i).accept(this) : "";
            pairs.add(k + ": " + v);
        }
        return "{\n" + indent() + "    " + String.join(",\n" + indent() + "    ", pairs) + "\n" + indent() + "}";
    }

    @Override
    public String visit(HtmlElementNode node) {
        StringBuilder sb = new StringBuilder();
        if (node.getTagName().equals("!DOCTYPE")) {
            sb.append("<!DOCTYPE html>\n");
            return sb.toString();
        }
        sb.append("<").append(node.getTagName());
        for (java.util.Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            sb.append(" ").append(entry.getKey());
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append("=\"").append(entry.getValue()).append("\"");
            }
        }
        
        boolean isVoidElement = java.util.Arrays.asList("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr").contains(node.getTagName().toLowerCase());
        
        if (isVoidElement) {
            if (node.getIsSelfClosing()) sb.append("/>");
            else sb.append(">");
        } else {
            sb.append(">");
            for (ASTNode child : node.getChildren()) {
                if (child != null) sb.append(child.accept(this));
            }
            sb.append("</").append(node.getTagName()).append(">");
        }
        return sb.toString();
    }

    @Override
    public String visit(JinjaExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{{ ");
        if (node.getExpr() != null) sb.append(node.getExpr().accept(this));
        for (String filter : node.getFilters()) {
            sb.append(" | ").append(filter);
        }
        sb.append(" }}");
        return sb.toString();
    }

    @Override
    public String visit(JinjaIfNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{% if ");
        if (node.getCondition() != null) {
            sb.append(node.getCondition().accept(this));
        }
        sb.append(" %}\n");
        for (ASTNode child : node.getIfbody()) {
            if (child != null) sb.append(child.accept(this));
        }
        
        for (int i = 0; i < node.getElifConditions().size(); i++) {
            sb.append("{% elif ");
            if (node.getElifConditions().get(i) != null) {
                sb.append(node.getElifConditions().get(i).accept(this));
            }
            sb.append(" %}\n");
            for (ASTNode child : node.getElifBodies().get(i)) {
                if (child != null) sb.append(child.accept(this));
            }
        }
        
        if (!node.getElsebody().isEmpty()) {
            sb.append("{% else %}\n");
            for (ASTNode child : node.getElsebody()) {
                if (child != null) sb.append(child.accept(this));
            }
        }
        sb.append("{% endif %}\n");
        return sb.toString();
    }

    @Override
    public String visit(JinjaForNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{% for ").append(node.getLoopvar()).append(" in ");
        if (node.getIterable() != null) sb.append(node.getIterable().accept(this));
        sb.append(" %}");
        for (ASTNode child : node.getBody()) {
            if (child != null) sb.append(child.accept(this));
        }
        sb.append("{% endfor %}");
        return sb.toString();
    }

    @Override
    public String visit(JinjaSetNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{% set ").append(node.getTarget()).append(" = ");
        if (node.getValue() != null) sb.append(node.getValue().accept(this));
        sb.append(" %}");
        return sb.toString();
    }

    @Override
    public String visit(TextNode node) {
        return node.getText();
    }

    @Override
    public String visit(CssStyleNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("<style");
        for (java.util.Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            sb.append(" ").append(entry.getKey());
            if (entry.getValue() != null) {
                sb.append("=").append(entry.getValue());
            }
        }
        sb.append(">");
        for (ASTNode child : node.getContent()) {
            if (child != null) sb.append(child.accept(this));
        }
        sb.append("</style>");
        return sb.toString();
    }
}
