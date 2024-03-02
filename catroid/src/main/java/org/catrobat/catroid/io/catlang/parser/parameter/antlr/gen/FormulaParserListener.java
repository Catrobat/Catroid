// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/FormulaParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FormulaParser}.
 */
public interface FormulaParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(FormulaParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(FormulaParser.FormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(FormulaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(FormulaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(FormulaParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(FormulaParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(FormulaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(FormulaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#comparisonExpression}.
	 * @param ctx the parse tree
	 */
	void enterComparisonExpression(FormulaParser.ComparisonExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#comparisonExpression}.
	 * @param ctx the parse tree
	 */
	void exitComparisonExpression(FormulaParser.ComparisonExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveOperator(FormulaParser.AdditiveOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveOperator(FormulaParser.AdditiveOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#multiplicativeOperator}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeOperator(FormulaParser.MultiplicativeOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#multiplicativeOperator}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeOperator(FormulaParser.MultiplicativeOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(FormulaParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(FormulaParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpression(FormulaParser.SimpleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpression(FormulaParser.SimpleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#sensorPropertyOrMethodInvocation}.
	 * @param ctx the parse tree
	 */
	void enterSensorPropertyOrMethodInvocation(FormulaParser.SensorPropertyOrMethodInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#sensorPropertyOrMethodInvocation}.
	 * @param ctx the parse tree
	 */
	void exitSensorPropertyOrMethodInvocation(FormulaParser.SensorPropertyOrMethodInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#methodParameters}.
	 * @param ctx the parse tree
	 */
	void enterMethodParameters(FormulaParser.MethodParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#methodParameters}.
	 * @param ctx the parse tree
	 */
	void exitMethodParameters(FormulaParser.MethodParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(FormulaParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(FormulaParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(FormulaParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(FormulaParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link FormulaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(FormulaParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link FormulaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(FormulaParser.LiteralContext ctx);
}