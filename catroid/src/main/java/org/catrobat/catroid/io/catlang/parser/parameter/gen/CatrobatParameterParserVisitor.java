// Generated from /home/bprattes/catrobat/Catroid/catroid/src/main/java/org/catrobat/catroid/io/catlang/parser/parameter/antlr/CatrobatParameterParser.g4 by ANTLR 4.13.1
package org.catrobat.catroid.io.catlang.parser.parameter.gen;
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