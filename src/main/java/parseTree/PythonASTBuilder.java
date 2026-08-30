package parseTree;

import ANT.*;
import ast.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class PythonASTBuilder extends PythonParserBaseVisitor<ASTNode> {
    
    private String fileName;

    public PythonASTBuilder(String fileName) {
        this.fileName = fileName;
    }

    private void setLocation(ASTNode node, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (node != null && ctx != null && ctx.getStart() != null) {
            node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), fileName);
        }
    }

    @Override
    public ASTNode visitProgram(PythonParser.ProgramContext ctx) {
        List<ASTNode> statements = new ArrayList<>();
        for (PythonParser.StatementContext stmtCtx : ctx.statement()) {
            ASTNode stmt = visit(stmtCtx);
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        ProgramNode node = new ProgramNode(statements);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitStatement(PythonParser.StatementContext ctx) {
        if (ctx.simple_stmt() != null) return visit(ctx.simple_stmt());
        if (ctx.compound_stmt() != null) return visit(ctx.compound_stmt());
        return null;
    }

    @Override
    public ASTNode visitSimple_stmt(PythonParser.Simple_stmtContext ctx) {
        return visit(ctx.small_stmt());
    }

    @Override
    public ASTNode visitSmall_stmt(PythonParser.Small_stmtContext ctx) {
        if (ctx.import_stmt() != null) return visit(ctx.import_stmt());
        if (ctx.assign_stmt() != null) return visit(ctx.assign_stmt());
        if (ctx.expr_stmt() != null) return visit(ctx.expr_stmt());
        if (ctx.return_stmt() != null) return visit(ctx.return_stmt());
        return null;
    }

    @Override
    public ASTNode visitCompound_stmt(PythonParser.Compound_stmtContext ctx) {
        if (ctx.func_def() != null) return visit(ctx.func_def());
        if (ctx.if_stmt() != null) return visit(ctx.if_stmt());
        return null;
    }

    @Override
    public ASTNode visitImport_stmt(PythonParser.Import_stmtContext ctx) {
        if (ctx.FROM() != null) {
            String module = ctx.module_name().getText();
            List<String> names = new ArrayList<>();
            for (PythonParser.Imported_nameContext nameCtx : ctx.imported_names().imported_name()) {
                names.add(nameCtx.getText());
            }
            ImportNode node = new ImportNode(module, names);
            setLocation(node, ctx);
            return node;
        } else {
            List<String> names = new ArrayList<>();
            for (PythonParser.Module_nameContext nameCtx : ctx.import_list().module_name()) {
                names.add(nameCtx.getText());
            }
            ImportNode node = new ImportNode("", names);
            setLocation(node, ctx);
            return node;
        }
    }

    @Override
    public ASTNode visitAssign_stmt(PythonParser.Assign_stmtContext ctx) {
        ASTNode target = visit(ctx.test(0));
        ASTNode value = visit(ctx.test(1));
        AssignNode node = new AssignNode(target, value);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitExpr_stmt(PythonParser.Expr_stmtContext ctx) {
        return visit(ctx.test());
    }

    @Override
    public ASTNode visitReturn_stmt(PythonParser.Return_stmtContext ctx) {
        ASTNode value = ctx.test() != null ? visit(ctx.test()) : null;
        ReturnNode node = new ReturnNode(value);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitFunc_def(PythonParser.Func_defContext ctx) {
        String name = ctx.NAME().getText();
        List<String> params = new ArrayList<>();
        if (ctx.parameters().param_list() != null) {
            for (PythonParser.ParamContext pCtx : ctx.parameters().param_list().param()) {
                params.add(pCtx.NAME().getText());
            }
        }
        
        List<DecoratorNode> decorators = new ArrayList<>();
        if (ctx.decorators() != null) {
            for (PythonParser.DecoratorContext dCtx : ctx.decorators().decorator()) {
                String dName = dCtx.dotted_name().getText();
                List<ASTNode> args = new ArrayList<>();
                Map<String, ASTNode> kwargs = new LinkedHashMap<>();
                if (dCtx.arglist() != null) {
                    for (PythonParser.ArgumentContext argCtx : dCtx.arglist().argument()) {
                        if (argCtx.ASSIGN() != null) {
                            kwargs.put(argCtx.NAME().getText(), visit(argCtx.test()));
                        } else {
                            args.add(visit(argCtx.test()));
                        }
                    }
                }
                DecoratorNode dNode = new DecoratorNode(dName, args, kwargs);
                setLocation(dNode, dCtx);
                decorators.add(dNode);
            }
        }
        
        List<ASTNode> body = new ArrayList<>();
        if (ctx.suite().simple_stmt() != null) {
            body.add(visit(ctx.suite().simple_stmt()));
        } else {
            for (PythonParser.StatementContext sCtx : ctx.suite().statement()) {
                ASTNode stmt = visit(sCtx);
                if (stmt != null) body.add(stmt);
            }
        }
        
        FunctionDefNode node = new FunctionDefNode(name, params, body, decorators);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitIf_stmt(PythonParser.If_stmtContext ctx) {
        ASTNode condition = visit(ctx.test(0));
        List<ASTNode> ifBody = new ArrayList<>();
        PythonParser.SuiteContext ifSuite = ctx.suite(0);
        if (ifSuite.simple_stmt() != null) {
            ifBody.add(visit(ifSuite.simple_stmt()));
        } else {
            for (PythonParser.StatementContext sCtx : ifSuite.statement()) {
                ASTNode stmt = visit(sCtx);
                if (stmt != null) ifBody.add(stmt);
            }
        }
        
        List<ASTNode> elifConditions = new ArrayList<>();
        List<List<ASTNode>> elifBodies = new ArrayList<>();
        List<ASTNode> elseBody = new ArrayList<>();
        
        int nextTestIdx = 1;
        int nextSuiteIdx = 1;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            org.antlr.v4.runtime.tree.ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                if (child.getText().equals("elif")) {
                    elifConditions.add(visit(ctx.test(nextTestIdx++)));
                    List<ASTNode> elifBody = new ArrayList<>();
                    PythonParser.SuiteContext eSuite = ctx.suite(nextSuiteIdx++);
                    if (eSuite.simple_stmt() != null) {
                        elifBody.add(visit(eSuite.simple_stmt()));
                    } else {
                        for (PythonParser.StatementContext sCtx : eSuite.statement()) {
                            ASTNode stmt = visit(sCtx);
                            if (stmt != null) elifBody.add(stmt);
                        }
                    }
                    elifBodies.add(elifBody);
                } else if (child.getText().equals("else")) {
                    PythonParser.SuiteContext eSuite = ctx.suite(nextSuiteIdx++);
                    if (eSuite.simple_stmt() != null) {
                        elseBody.add(visit(eSuite.simple_stmt()));
                    } else {
                        for (PythonParser.StatementContext sCtx : eSuite.statement()) {
                            ASTNode stmt = visit(sCtx);
                            if (stmt != null) elseBody.add(stmt);
                        }
                    }
                }
            }
        }
        
        IfNode node = new IfNode(condition, ifBody, elifConditions, elifBodies, elseBody);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitOr_test(PythonParser.Or_testContext ctx) {
        if (ctx.and_test().size() == 1) return visit(ctx.and_test(0));
        ASTNode left = visit(ctx.and_test(0));
        for (int i = 1; i < ctx.and_test().size(); i++) {
            ASTNode right = visit(ctx.and_test(i));
            left = new BinaryExprNode(left, "or", right);
            setLocation(left, ctx);
        }
        return left;
    }

    @Override
    public ASTNode visitAnd_test(PythonParser.And_testContext ctx) {
        if (ctx.not_test().size() == 1) return visit(ctx.not_test(0));
        ASTNode left = visit(ctx.not_test(0));
        for (int i = 1; i < ctx.not_test().size(); i++) {
            ASTNode right = visit(ctx.not_test(i));
            left = new BinaryExprNode(left, "and", right);
            setLocation(left, ctx);
        }
        return left;
    }

    @Override
    public ASTNode visitNot_test(PythonParser.Not_testContext ctx) {
        if (ctx.comparison() != null) return visit(ctx.comparison());
        ASTNode expr = visit(ctx.not_test());
        UnaryExprNode node = new UnaryExprNode("not", expr);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitComparison(PythonParser.ComparisonContext ctx) {
        if (ctx.expr().size() == 1) return visit(ctx.expr(0));
        ASTNode left = visit(ctx.expr(0));
        ASTNode right = visit(ctx.expr(1));
        String op = ctx.comp_op(0).getText();
        BinaryExprNode node = new BinaryExprNode(left, op, right);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitExpr(PythonParser.ExprContext ctx) {
        if (ctx.term().size() == 1) return visit(ctx.term(0));
        ASTNode left = visit(ctx.term(0));
        for (int i = 1; i < ctx.term().size(); i++) {
            ASTNode right = visit(ctx.term(i));
            String op = ctx.getChild(2*i - 1).getText();
            left = new BinaryExprNode(left, op, right);
            setLocation(left, ctx);
        }
        return left;
    }

    @Override
    public ASTNode visitTerm(PythonParser.TermContext ctx) {
        if (ctx.factor().size() == 1) return visit(ctx.factor(0));
        ASTNode left = visit(ctx.factor(0));
        for (int i = 1; i < ctx.factor().size(); i++) {
            ASTNode right = visit(ctx.factor(i));
            String op = ctx.getChild(2*i - 1).getText();
            left = new BinaryExprNode(left, op, right);
            setLocation(left, ctx);
        }
        return left;
    }

    @Override
    public ASTNode visitFactor(PythonParser.FactorContext ctx) {
        if (ctx.power() != null) return visit(ctx.power());
        String op = ctx.getChild(0).getText();
        ASTNode expr = visit(ctx.factor());
        UnaryExprNode node = new UnaryExprNode(op, expr);
        setLocation(node, ctx);
        return node;
    }

    @Override
    public ASTNode visitPower(PythonParser.PowerContext ctx) {
        return visit(ctx.atom_expr());
    }

    @Override
    public ASTNode visitAtom_expr(PythonParser.Atom_exprContext ctx) {
        ASTNode atom = visit(ctx.atom());
        for (PythonParser.TrailerContext tCtx : ctx.trailer()) {
            if (tCtx.LPAREN() != null) {
                List<ASTNode> args = new ArrayList<>();
                Map<String, ASTNode> kwargs = new LinkedHashMap<>();
                if (tCtx.arglist() != null) {
                    for (PythonParser.ArgumentContext argCtx : tCtx.arglist().argument()) {
                        if (argCtx.ASSIGN() != null) {
                            kwargs.put(argCtx.NAME().getText(), visit(argCtx.test()));
                        } else {
                            args.add(visit(argCtx.test()));
                        }
                    }
                }
                atom = new CallExprNode(atom, args, kwargs);
            } else if (tCtx.DOT() != null) {
                atom = new AttributeExprNode(atom, tCtx.NAME().getText());
            } else if (tCtx.LBRACK() != null) {
                atom = new IndexExprNode(atom, visit(tCtx.test()));
            }
            setLocation(atom, tCtx);
        }
        return atom;
    }

    @Override
    public ASTNode visitAtom(PythonParser.AtomContext ctx) {
        if (ctx.NAME() != null) {
            IdentifierNode node = new IdentifierNode(ctx.NAME().getText());
            setLocation(node, ctx);
            return node;
        }
        if (ctx.DNAME() != null) {
            IdentifierNode node = new IdentifierNode(ctx.DNAME().getText());
            setLocation(node, ctx);
            return node;
        }
        if (ctx.INTEGER() != null) {
            LiteralNode node = new LiteralNode(ctx.INTEGER().getText(), "int");
            setLocation(node, ctx);
            return node;
        }
        if (ctx.FLOAT() != null) {
            LiteralNode node = new LiteralNode(ctx.FLOAT().getText(), "float");
            setLocation(node, ctx);
            return node;
        }
        if (ctx.STRING() != null && !ctx.STRING().isEmpty()) {
            String combinedString = "";
            for (TerminalNode sNode : ctx.STRING()) {
                combinedString += sNode.getText();
            }
            LiteralNode node = new LiteralNode(combinedString, "string");
            setLocation(node, ctx);
            return node;
        }
        if (ctx.TRUE() != null) {
            LiteralNode node = new LiteralNode("True", "bool");
            setLocation(node, ctx);
            return node;
        }
        if (ctx.FALSE() != null) {
            LiteralNode node = new LiteralNode("False", "bool");
            setLocation(node, ctx);
            return node;
        }
        if (ctx.NONE() != null) {
            LiteralNode node = new LiteralNode("None", "none");
            setLocation(node, ctx);
            return node;
        }
        if (ctx.LBRACK() != null) {
            List<ASTNode> elements = new ArrayList<>();
            for (PythonParser.TestContext testCtx : ctx.test()) {
                elements.add(visit(testCtx));
            }
            ListNode node = new ListNode(elements);
            setLocation(node, ctx);
            return node;
        }
        if (ctx.LBRACE() != null) {
            List<ASTNode> keys = new ArrayList<>();
            List<ASTNode> values = new ArrayList<>();
            if (ctx.dictorsetmaker() != null) {
                for (int i = 0; i < ctx.dictorsetmaker().test().size(); i += 2) {
                    keys.add(visit(ctx.dictorsetmaker().test(i)));
                    values.add(visit(ctx.dictorsetmaker().test(i+1)));
                }
            }
            DictNode node = new DictNode(keys, values);
            setLocation(node, ctx);
            return node;
        }
        if (ctx.LPAREN() != null && !ctx.test().isEmpty()) {
            return visit(ctx.test(0));
        }
        return null;
    }
}
