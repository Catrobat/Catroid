// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/CatrobatParameterParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CatrobatParameterParser}.
 */
public interface CatrobatParameterParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(CatrobatParameterParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(CatrobatParameterParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(CatrobatParameterParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(CatrobatParameterParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(CatrobatParameterParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(CatrobatParameterParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(CatrobatParameterParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(CatrobatParameterParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveOperator(CatrobatParameterParser.AdditiveOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveOperator(CatrobatParameterParser.AdditiveOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#multiplicativeOperator}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeOperator(CatrobatParameterParser.MultiplicativeOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#multiplicativeOperator}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeOperator(CatrobatParameterParser.MultiplicativeOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpression(CatrobatParameterParser.SimpleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpression(CatrobatParameterParser.SimpleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#sensorPropertyOrMethodInvocation}.
	 * @param ctx the parse tree
	 */
	void enterSensorPropertyOrMethodInvocation(CatrobatParameterParser.SensorPropertyOrMethodInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#sensorPropertyOrMethodInvocation}.
	 * @param ctx the parse tree
	 */
	void exitSensorPropertyOrMethodInvocation(CatrobatParameterParser.SensorPropertyOrMethodInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#methodParameters}.
	 * @param ctx the parse tree
	 */
	void enterMethodParameters(CatrobatParameterParser.MethodParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#methodParameters}.
	 * @param ctx the parse tree
	 */
	void exitMethodParameters(CatrobatParameterParser.MethodParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(CatrobatParameterParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(CatrobatParameterParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(CatrobatParameterParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(CatrobatParameterParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(CatrobatParameterParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(CatrobatParameterParser.LiteralContext ctx);
}