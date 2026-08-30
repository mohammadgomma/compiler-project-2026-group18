package visitor;

import ast.*;

public interface ASTVisitor<T> {
    T visit(ProgramNode node);
    T visit(FunctionDefNode node);
    T visit(IfNode node);
    T visit(AssignNode node);
    T visit(ReturnNode node);
    T visit(ImportNode node);
    T visit(BinaryExprNode node);
    T visit(UnaryExprNode node);
    T visit(CallExprNode node);
    T visit(AttributeExprNode node);
    T visit(IndexExprNode node);
    T visit(LiteralNode node);
    T visit(IdentifierNode node);
    T visit(ListNode node);
    T visit(DictNode node);
    T visit(HtmlElementNode node);
    T visit(JinjaExpressionNode node);
    T visit(JinjaIfNode node);
    T visit(JinjaForNode node);
    T visit(JinjaSetNode node);
    T visit(TextNode node);
    T visit(CssStyleNode node);
    T visit(DecoratorNode node);
}
