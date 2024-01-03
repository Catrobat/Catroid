// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/CatrobatParameterParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CatrobatParameterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CatrobatParameterParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(CatrobatParameterParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(CatrobatParameterParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(CatrobatParameterParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(CatrobatParameterParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#comparisonExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExpression(CatrobatParameterParser.ComparisonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#additiveOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveOperator(CatrobatParameterParser.AdditiveOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#multiplicativeOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeOperator(CatrobatParameterParser.MultiplicativeOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(CatrobatParameterParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#simpleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleExpression(CatrobatParameterParser.SimpleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#sensorPropertyOrMethodInvocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSensorPropertyOrMethodInvocation(CatrobatParameterParser.SensorPropertyOrMethodInvocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#methodParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodParameters(CatrobatParameterParser.MethodParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(CatrobatParameterParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(CatrobatParameterParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(CatrobatParameterParser.LiteralContext ctx);
}