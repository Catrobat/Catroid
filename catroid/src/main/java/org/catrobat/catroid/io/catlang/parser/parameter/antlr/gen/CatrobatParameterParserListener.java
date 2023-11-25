/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	 * Enter a parse tree produced by {@link CatrobatParameterParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void enterSimple_expression(CatrobatParameterParser.Simple_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void exitSimple_expression(CatrobatParameterParser.Simple_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#sensor_reference}.
	 * @param ctx the parse tree
	 */
	void enterSensor_reference(CatrobatParameterParser.Sensor_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#sensor_reference}.
	 * @param ctx the parse tree
	 */
	void exitSensor_reference(CatrobatParameterParser.Sensor_referenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#method_invoaction}.
	 * @param ctx the parse tree
	 */
	void enterMethod_invoaction(CatrobatParameterParser.Method_invoactionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#method_invoaction}.
	 * @param ctx the parse tree
	 */
	void exitMethod_invoaction(CatrobatParameterParser.Method_invoactionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(CatrobatParameterParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(CatrobatParameterParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#param_list}.
	 * @param ctx the parse tree
	 */
	void enterParam_list(CatrobatParameterParser.Param_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#param_list}.
	 * @param ctx the parse tree
	 */
	void exitParam_list(CatrobatParameterParser.Param_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link CatrobatParameterParser#unary_expression}.
	 * @param ctx the parse tree
	 */
	void enterUnary_expression(CatrobatParameterParser.Unary_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CatrobatParameterParser#unary_expression}.
	 * @param ctx the parse tree
	 */
	void exitUnary_expression(CatrobatParameterParser.Unary_expressionContext ctx);
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