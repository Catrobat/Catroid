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
	 * Visit a parse tree produced by {@link CatrobatParameterParser#simple_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_expression(CatrobatParameterParser.Simple_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#sensor_reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSensor_reference(CatrobatParameterParser.Sensor_referenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#method_invoaction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod_invoaction(CatrobatParameterParser.Method_invoactionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(CatrobatParameterParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#param_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list(CatrobatParameterParser.Param_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#unary_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_expression(CatrobatParameterParser.Unary_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatrobatParameterParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(CatrobatParameterParser.LiteralContext ctx);
}