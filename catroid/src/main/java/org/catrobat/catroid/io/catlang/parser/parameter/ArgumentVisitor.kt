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

package org.catrobat.catroid.io.catlang.parser.parameter

import android.content.Context
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternToExternGenerator
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.io.catlang.parser.parameter.context.FormulaElementVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.context.FormulaVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.context.ArgumentBaseVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.context.OperatorVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.context.ParameterVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.error.ArgumentParsingException
import org.catrobat.catroid.io.catlang.parser.parameter.error.UnkownFunctionException
import org.catrobat.catroid.io.catlang.parser.parameter.error.UnkownSensorException
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterParser
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterParserVisitor
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils
import java.util.Stack

class ArgumentVisitor(
    private val context: Context,
    private val userVariables: List<String>,
    private val userLists: List<String>,
    private val userDefinedBrickParameters: List<String>
    ) : CatrobatParameterParserVisitor<ArgumentBaseVisitResult> {

    private val formulaElementStack = Stack<FormulaElement>()

    private val externToInternValues = InternToExternGenerator.getExternToInternValueMapping(CatrobatLanguageUtils.getEnglishContextForFormulas(context))

    private fun tryGetParentFormulaElement(): FormulaElement? {
        return if (formulaElementStack.empty()) {
            null
        } else formulaElementStack.peek()
    }

    override fun visit(tree: ParseTree?): ArgumentBaseVisitResult {
        TODO("Not yet implemented")
    }

    override fun visitChildren(node: RuleNode?): ArgumentBaseVisitResult {
        TODO("Not yet implemented")
    }

    override fun visitTerminal(node: TerminalNode?): ArgumentBaseVisitResult {
        TODO("Not yet implemented")
    }

    override fun visitErrorNode(node: ErrorNode?): ArgumentBaseVisitResult {
        TODO("Not yet implemented")
    }

    override fun visitArgument(ctx: CatrobatParameterParser.ArgumentContext?): ArgumentBaseVisitResult {
        val response = visitExpression(ctx?.expression())
        val formulaElement = (response as FormulaElementVisitResult).formulaElement
        return FormulaVisitResult(Formula(formulaElement))
    }

    override fun visitExpression(ctx: CatrobatParameterParser.ExpressionContext?): ArgumentBaseVisitResult {
        if (ctx == null) {
            throw ArgumentParsingException("Cannot parse empty expression")
        }

        if (ctx.additiveExpression() != null) {
            return visitAdditiveExpression(ctx.additiveExpression())
        }

        var operator: Operators? = null
        if (ctx.OPERATOR_LOGIC_EQUAL() != null) {
            operator = Operators.EQUAL
        } else if (ctx.OPERATOR_LOGIC_NOT_EQUAL() != null) {
            operator = Operators.NOT_EQUAL
        } else if (ctx.OPERATOR_LOGIC_LOWER() != null) {
            operator = Operators.SMALLER_THAN
        } else if (ctx.OPERATOR_LOGIC_GREATER() != null) {
            operator = Operators.GREATER_THAN
        } else if (ctx.OPERATOR_LOGIC_LOWER_EQUAL() != null) {
            operator = Operators.SMALLER_OR_EQUAL
        } else if (ctx.OPERATOR_LOGIC_GREATER_EQUAL() != null) {
            operator = Operators.GREATER_OR_EQUAL
        }

        if (operator != null) {
            val parentElement = tryGetParentFormulaElement()
            val formulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, operator.name, parentElement)
            formulaElementStack.push(formulaElement)
            val leftResult = visitExpression(ctx.expression(0))
            val rightResult = visitExpression(ctx.expression(1))
            formulaElement.setLeftChild((leftResult as FormulaElementVisitResult).formulaElement)
            formulaElement.setRightChild((rightResult as FormulaElementVisitResult).formulaElement)
            formulaElementStack.pop()
            return FormulaElementVisitResult(formulaElement)
        }

        throw ArgumentParsingException("No comparison operator found")
    }

    override fun visitAdditiveExpression(ctx: CatrobatParameterParser.AdditiveExpressionContext?): ArgumentBaseVisitResult {
        if (ctx == null) {
            throw ArgumentParsingException("No additive expression found")
        }
        if (ctx.multiplicativeExpression().count() == 1) {
            return visitMultiplicativeExpression(ctx.multiplicativeExpression(0))
        }

        var additiveExpressionStack = Stack<FormulaElement>()

        for (i in ctx.additiveOperator().indices.reversed()) {
            val operator = (visitAdditiveOperator(ctx.additiveOperator(i)) as OperatorVisitResult).operator
            val formulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, operator.name, tryGetParentFormulaElement())
            formulaElementStack.push(formulaElement)
            if (additiveExpressionStack.isNotEmpty()) {
                additiveExpressionStack.peek().setLeftChild(formulaElement)
            }
            val rightMultiplicativeExpression = (visitMultiplicativeExpression(ctx.multiplicativeExpression(i+1)) as FormulaElementVisitResult).formulaElement
            formulaElement.setRightChild(rightMultiplicativeExpression)
            if (i == 0) {
                val firstMultiplicativeExpression = (visitMultiplicativeExpression(ctx.multiplicativeExpression(i)) as FormulaElementVisitResult).formulaElement
                formulaElement.setLeftChild(firstMultiplicativeExpression)
            }
            additiveExpressionStack.push(formulaElement)
            formulaElementStack.pop()
        }
        return FormulaElementVisitResult(additiveExpressionStack.elementAt(0))
    }

    override fun visitMultiplicativeExpression(ctx: CatrobatParameterParser.MultiplicativeExpressionContext?): ArgumentBaseVisitResult {
        if (ctx == null) {
            throw ArgumentParsingException("No multiplicative expression found")
        }
        if (ctx.simple_expression().count() == 1) {
            return visitSimple_expression(ctx.simple_expression(0))
        }

        var multiplicativeExpressionStack = Stack<FormulaElement>()

        for (i in ctx.multiplicativeOperator().indices.reversed()) {
            val operator = (visitMultiplicativeOperator(ctx.multiplicativeOperator(i)) as OperatorVisitResult).operator
            val formulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, operator.name, tryGetParentFormulaElement())
            formulaElementStack.push(formulaElement)
            if (multiplicativeExpressionStack.isNotEmpty()) {
                multiplicativeExpressionStack.peek().setLeftChild(formulaElement)
            }
            val rightSimpleExpression = (visitSimple_expression(ctx.simple_expression(i+1)) as FormulaElementVisitResult).formulaElement
            formulaElement.setRightChild(rightSimpleExpression)
            if (i == 0) {
                val firstSimpleExpression = (visitSimple_expression(ctx.simple_expression(i)) as FormulaElementVisitResult).formulaElement
                formulaElement.setLeftChild(firstSimpleExpression)
            }
            multiplicativeExpressionStack.push(formulaElement)
            formulaElementStack.pop()
        }
        return FormulaElementVisitResult(multiplicativeExpressionStack.elementAt(0))
    }

    override fun visitAdditiveOperator(ctx: CatrobatParameterParser.AdditiveOperatorContext?): ArgumentBaseVisitResult {
        if (ctx == null) {
            throw ArgumentParsingException("No additive operator found")
        }
        if (ctx.OPERATOR_NUMERIC_ADD() != null) {
            return OperatorVisitResult(Operators.PLUS)
        }
        if (ctx.OPERATOR_NUMERIC_MINUS() != null) {
            return OperatorVisitResult(Operators.MINUS)
        }
        if (ctx.OPERATOR_LOGIC_OR() != null) {
            return OperatorVisitResult(Operators.LOGICAL_OR)
        }
        throw ArgumentParsingException("No additive operator found")
    }

    override fun visitMultiplicativeOperator(ctx: CatrobatParameterParser.MultiplicativeOperatorContext?): ArgumentBaseVisitResult {
        if (ctx == null) {
            throw ArgumentParsingException("No multiplicative operator found")
        }
        if (ctx.OPERATOR_NUMERIC_MULTIPLY() != null) {
            return OperatorVisitResult(Operators.MULT)
        }
        if (ctx.OPERATOR_NUMERIC_DIVIDE() != null) {
            return OperatorVisitResult(Operators.DIVIDE)
        }
        if (ctx.OPERATOR_LOGIC_AND() != null) {
            return OperatorVisitResult(Operators.LOGICAL_AND)
        }
        throw ArgumentParsingException("No multiplicative operator found")
    }

    override fun visitSimple_expression(ctx: CatrobatParameterParser.Simple_expressionContext?): ArgumentBaseVisitResult {
        if (ctx != null) {
            if (ctx.literal() != null) {
                return visitLiteral(ctx.literal())
            }
            if (ctx.unary_expression() != null) {
                return visitUnary_expression(ctx.unary_expression())
            }
            if (ctx.sensor_reference() != null) {
                return visitSensor_reference(ctx.sensor_reference())
            }
            if (ctx.method_invoaction() != null) {
                return visitMethod_invoaction(ctx.method_invoaction())
            }
            if (ctx.expression() != null) {
                val parentElement = tryGetParentFormulaElement()
                val braceElement = FormulaElement(FormulaElement.ElementType.BRACKET, "", parentElement)
                formulaElementStack.push(braceElement)
                val bracedExpression = visitExpression(ctx.expression())
                braceElement.setRightChild((bracedExpression as FormulaElementVisitResult).formulaElement)
                formulaElementStack.pop()
                return FormulaElementVisitResult(braceElement)
            }
        }
        return ArgumentBaseVisitResult()
    }

    override fun visitSensor_reference(ctx: CatrobatParameterParser.Sensor_referenceContext?): ArgumentBaseVisitResult {
        if (ctx?.FUNCTION_OR_SENSOR() != null) {
            val sensorName = ctx.FUNCTION_OR_SENSOR().text
            if (externToInternValues.containsKey(sensorName)) {
                val sensorID = externToInternValues[sensorName]

                val allSensors = enumValues<Sensors>()
                for (sensor in allSensors) {
                    if (sensor.name == sensorID) {
                        return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.SENSOR, sensorID, tryGetParentFormulaElement()))
                    }
                }
            }
            throw UnkownSensorException(sensorName!!)
        }
        return ArgumentBaseVisitResult()
    }

    override fun visitMethod_invoaction(ctx: CatrobatParameterParser.Method_invoactionContext?): ArgumentBaseVisitResult {
        if (ctx?.FUNCTION_OR_SENSOR() != null) {
            if (externToInternValues.containsKey(ctx.FUNCTION_OR_SENSOR().text)) {
                val functionName = externToInternValues[ctx.FUNCTION_OR_SENSOR().text]

                var functionElement: FormulaElement? = null
                val allFunctions = enumValues<Functions>()
                for (sensor in allFunctions) {
                    if (sensor.name == functionName) {
                        functionElement = FormulaElement(FormulaElement.ElementType.FUNCTION, functionName, tryGetParentFormulaElement())
                    }
                }
                if (functionElement != null) {
                    formulaElementStack.push(functionElement)
                    val paramReply = visitParameters(ctx.parameters())
                    if (paramReply is ParameterVisitResult) {
                        val parameters = paramReply
                        if (parameters.leftChild != null) {
                            functionElement.setLeftChild(parameters.leftChild)
                        }
                        if (parameters.rightChild != null) {
                            functionElement.setRightChild(parameters.rightChild)
                        }
                        if (parameters.additionalChildren != null) {
                            functionElement.additionalChildren = parameters.additionalChildren
                        }
                    }
                    formulaElementStack.pop()
                    return FormulaElementVisitResult(functionElement)
                } else {
                    throw UnkownFunctionException(functionName!!)
                }
            }
        }
        return ArgumentBaseVisitResult()
    }

    override fun visitParameters(ctx: CatrobatParameterParser.ParametersContext?): ArgumentBaseVisitResult {
        return visitParam_list(ctx?.param_list())
    }

    override fun visitParam_list(ctx: CatrobatParameterParser.Param_listContext?): ArgumentBaseVisitResult {
        if (ctx?.expression() != null) {
            val parameters = arrayListOf<FormulaElement>()
            for (expression in ctx.expression()) {
                val result = visitExpression(expression)
                parameters.add((result as FormulaElementVisitResult).formulaElement)
            }
            var leftParameter: FormulaElement? = null
            var rightParameter: FormulaElement? = null

            if (parameters.isNotEmpty()) {
                leftParameter = parameters.first()
                parameters.remove(leftParameter)
            }
            if (parameters.isNotEmpty()) {
                rightParameter = parameters.first()
                parameters.remove(rightParameter)
            }
            return ParameterVisitResult(leftParameter, rightParameter, parameters)
        }
        return ArgumentBaseVisitResult()
    }

    override fun visitUnary_expression(ctx: CatrobatParameterParser.Unary_expressionContext?): ArgumentBaseVisitResult {
        val parentElement = tryGetParentFormulaElement()
        if (ctx != null) {
            var unaryFormulaElement: FormulaElement? = null

            if (ctx.OPERATOR_NUMERIC_ADD() != null) {
                unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.PLUS.name, parentElement)
            } else if (ctx.OPERATOR_NUMERIC_MINUS() != null) {
                unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.MINUS.name, parentElement)
            } else if (ctx.OPERATOR_LOGIC_NOT() != null) {
                unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.LOGICAL_NOT.name, parentElement)
            }

            if (unaryFormulaElement != null) {
                formulaElementStack.push(unaryFormulaElement)
                val result = visitExpression(ctx.expression())
                unaryFormulaElement.setRightChild((result as FormulaElementVisitResult).formulaElement)
                formulaElementStack.pop()
                return FormulaElementVisitResult(unaryFormulaElement)
            }
        }
        return ArgumentBaseVisitResult()
    }

    override fun visitLiteral(ctx: CatrobatParameterParser.LiteralContext?): ArgumentBaseVisitResult {
        if (ctx != null) {
            var parentElement = tryGetParentFormulaElement()

            if (ctx.NUMBER() != null) {
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.NUMBER, ctx.NUMBER().text, parentElement))
            }
            if (ctx.STRING() != null) {
                val trimmedString = ctx.STRING().text.trim('\'');
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.STRING, trimmedString, parentElement))
            }
            if (ctx.UDB_PARAMETER() != null) {
                val trimmedUDBParameter = ctx.UDB_PARAMETER().text.trimStart('[').trimEnd(']')
                if (!userDefinedBrickParameters.contains(trimmedUDBParameter)) {
                    throw ArgumentParsingException("Unknown user defined brick parameter: $trimmedUDBParameter")
                }
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, trimmedUDBParameter, parentElement))
            }
            if (ctx.LIST() != null) {
                val trimmedListParameter = ctx.LIST().text.trim('*')
                if (!userLists.contains(trimmedListParameter)) {
                    throw ArgumentParsingException("Unknown list: $trimmedListParameter")
                }
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_LIST, trimmedListParameter, parentElement))
            }
            if (ctx.VARIABLE() != null) {
                val trimmedVariableParameter = ctx.VARIABLE().text.trim('"')
                if (!userVariables.contains(trimmedVariableParameter)) {
                    throw ArgumentParsingException("Unknown variable: $trimmedVariableParameter")
                }
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_VARIABLE, trimmedVariableParameter, parentElement))
            }
        }
        return ArgumentBaseVisitResult()
    }
}