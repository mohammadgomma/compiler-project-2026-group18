// Generated from TemplateParser.g4 by ANTLR 4.13.2
package ANT;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TemplateParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TemplateParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TemplateParser#template}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplate(TemplateParser.TemplateContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(TemplateParser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlElement(TemplateParser.HtmlElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#htmlParts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlParts(TemplateParser.HtmlPartsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#htmlAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlAttribute(TemplateParser.HtmlAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#closeTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCloseTag(TemplateParser.CloseTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#styleElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleElement(TemplateParser.StyleElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#styleAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleAttribute(TemplateParser.StyleAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssContent(TemplateParser.CssContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExpression(TemplateParser.JinjaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJExpr(TemplateParser.JExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJFilter(TemplateParser.JFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jFilterArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJFilterArg(TemplateParser.JFilterArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJAtom(TemplateParser.JAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJArgs(TemplateParser.JArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlock(TemplateParser.JinjaBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#ifBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfBlock(TemplateParser.IfBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#forBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForBlock(TemplateParser.ForBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#setBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetBlock(TemplateParser.SetBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jbExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJbExpr(TemplateParser.JbExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jbFilter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJbFilter(TemplateParser.JbFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jbFilterArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJbFilterArg(TemplateParser.JbFilterArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jbAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJbAtom(TemplateParser.JbAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jbArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJbArgs(TemplateParser.JbArgsContext ctx);
}