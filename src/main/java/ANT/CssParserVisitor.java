// Generated from CssParser.g4 by ANTLR 4.13.2
package ANT;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CssParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CssParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CssParser#stylesheet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStylesheet(CssParser.StylesheetContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#charsetRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharsetRule(CssParser.CharsetRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#importRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportRule(CssParser.ImportRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#mediaQueryRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMediaQueryRule(CssParser.MediaQueryRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#mediaQueryList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMediaQueryList(CssParser.MediaQueryListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#mediaQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMediaQuery(CssParser.MediaQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#mediaType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMediaType(CssParser.MediaTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#mediaExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMediaExpression(CssParser.MediaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#mediaFeature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMediaFeature(CssParser.MediaFeatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#keyframesRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyframesRule(CssParser.KeyframesRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#keyframeBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyframeBlock(CssParser.KeyframeBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#keyframeSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyframeSelector(CssParser.KeyframeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#fontFaceRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFontFaceRule(CssParser.FontFaceRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#supportsRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSupportsRule(CssParser.SupportsRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#supportsCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSupportsCondition(CssParser.SupportsConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#supportsInParens}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSupportsInParens(CssParser.SupportsInParensContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#atRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtRule(CssParser.AtRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(CssParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#cssRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssRule(CssParser.CssRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#selectorGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectorGroup(CssParser.SelectorGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelector(CssParser.SelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#combinator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombinator(CssParser.CombinatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#simpleSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleSelector(CssParser.SimpleSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#elementName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementName(CssParser.ElementNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#idSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdSelector(CssParser.IdSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#classSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassSelector(CssParser.ClassSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#attributeSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeSelector(CssParser.AttributeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#attributeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeName(CssParser.AttributeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#attributeMatcher}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeMatcher(CssParser.AttributeMatcherContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#attributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeValue(CssParser.AttributeValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#pseudoSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPseudoSelector(CssParser.PseudoSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(CssParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty(CssParser.PropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#important}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportant(CssParser.ImportantContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(CssParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(CssParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(CssParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#literalValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralValue(CssParser.LiteralValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit(CssParser.UnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#keywordValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeywordValue(CssParser.KeywordValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#color_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColor_name(CssParser.Color_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator(CssParser.OperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#functionValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionValue(CssParser.FunctionValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#funcContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncContent(CssParser.FuncContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#calcFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalcFunction(CssParser.CalcFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CssParser#varFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarFunction(CssParser.VarFunctionContext ctx);
}