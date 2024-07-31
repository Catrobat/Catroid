// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/FormulaParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link FormulaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface FormulaParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula(FormulaParser.FormulaContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(FormulaParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(FormulaParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(FormulaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#comparisonExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExpression(FormulaParser.ComparisonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#additiveOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveOperator(FormulaParser.AdditiveOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#multiplicativeOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeOperator(FormulaParser.MultiplicativeOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(FormulaParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#simpleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleExpression(FormulaParser.SimpleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#sensorPropertyOrMethodInvocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSensorPropertyOrMethodInvocation(FormulaParser.SensorPropertyOrMethodInvocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#methodParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodParameters(FormulaParser.MethodParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(FormulaParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(FormulaParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FormulaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(FormulaParser.LiteralContext ctx);
}