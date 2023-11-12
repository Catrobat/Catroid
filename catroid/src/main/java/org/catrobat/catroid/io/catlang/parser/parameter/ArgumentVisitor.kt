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
import org.catrobat.catroid.io.catlang.parser.parameter.context.ParameterVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterParser
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterParserVisitor
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils
import java.util.Stack

class ArgumentVisitor(context: Context) : CatrobatParameterParserVisitor<ArgumentBaseVisitResult> {

    private val _formulaElementStack = Stack<FormulaElement>()

    private var externToInternValues = InternToExternGenerator.getExternToInternValueMapping(CatrobatLanguageUtils.getEnglishContextForFormulas(context))
//    private var externToInternValues = mapOf<String, String>()

    override fun visit(tree: ParseTree?): ArgumentBaseVisitResult {
        return ArgumentBaseVisitResult()
    }

    override fun visitChildren(node: RuleNode?): ArgumentBaseVisitResult {
        return ArgumentBaseVisitResult()
    }

    override fun visitTerminal(node: TerminalNode?): ArgumentBaseVisitResult {
        return ArgumentBaseVisitResult()
    }

    override fun visitErrorNode(node: ErrorNode?): ArgumentBaseVisitResult {
        return ArgumentBaseVisitResult()
    }

    override fun visitArgument(ctx: CatrobatParameterParser.ArgumentContext?): ArgumentBaseVisitResult {

        var result = visitExpression(ctx?.expression())
        if (result is FormulaElementVisitResult) {
            return FormulaVisitResult(Formula(result.formulaElement))
        }
        return ArgumentBaseVisitResult()
    }

    override fun visitExpression(ctx: CatrobatParameterParser.ExpressionContext?): ArgumentBaseVisitResult {
        if (ctx != null) {
            var operator: Operators? = null
            if (ctx.OPERATOR_ADD() != null) {
                if (ctx.OPERATOR_ADD().text == "+") {
                    operator = Operators.PLUS
                } else if (ctx.OPERATOR_ADD().text == "-") {
                    operator = Operators.MINUS
                }
            } else if (ctx.OPERATOR_NUMERIC_DIVIDE() != null) {
                operator = Operators.DIVIDE
            } else if (ctx.OPERATOR_NUMERIC_MULTIPLY() != null) {
                operator = Operators.MULT
            } else if (ctx.OPERATOR_LOGIC_AND() != null) {
                operator = Operators.LOGICAL_AND
            } else if (ctx.OPERATOR_LOGIC_OR() != null) {
                operator = Operators.LOGICAL_OR
            } else if (ctx.OPERATOR_LOGIC_EQUAL() != null) {
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
                _formulaElementStack.push(formulaElement)
                val leftResult = visitExpression(ctx.expression(0))
                val rightResult = visitExpression(ctx.expression(1))
                formulaElement.setLeftChild((leftResult as FormulaElementVisitResult).formulaElement)
                formulaElement.setRightChild((rightResult as FormulaElementVisitResult).formulaElement)
                _formulaElementStack.pop()
                return FormulaElementVisitResult(formulaElement)
            } else if (ctx.simple_expression() != null) {
                return visitSimple_expression(ctx.simple_expression())
            }
        }
        return ArgumentBaseVisitResult()
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
                _formulaElementStack.push(braceElement)
                val bracedExpression = visitExpression(ctx.expression())
                braceElement.setRightChild((bracedExpression as FormulaElementVisitResult).formulaElement)
                _formulaElementStack.pop()
                return FormulaElementVisitResult(braceElement)
            }
        }

        return ArgumentBaseVisitResult()
    }

    override fun visitSensor_reference(ctx: CatrobatParameterParser.Sensor_referenceContext?): ArgumentBaseVisitResult {
        if (ctx?.FUNCTION_OR_SENSOR() != null) {
            if (externToInternValues.containsKey(ctx.FUNCTION_OR_SENSOR().text)) {
                val sensorName = externToInternValues[ctx.FUNCTION_OR_SENSOR().text]

                val allSensors = enumValues<Sensors>()
                for (sensor in allSensors) {
                    if (sensor.name == sensorName) {
                        return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.SENSOR, sensorName, tryGetParentFormulaElement()))
                    }
                }
            }
        }
        return ArgumentBaseVisitResult()
    }

    override fun visitMethod_invoaction(ctx: CatrobatParameterParser.Method_invoactionContext?): ArgumentBaseVisitResult {
        if (ctx?.FUNCTION_OR_SENSOR() != null) {
            if (externToInternValues.containsKey(ctx.FUNCTION_OR_SENSOR().text)) {
                val sensorName = externToInternValues[ctx.FUNCTION_OR_SENSOR().text]

                var functionElement: FormulaElement? = null
                val allFunctions = enumValues<Functions>()
                for (sensor in allFunctions) {
                    if (sensor.name == sensorName) {
                        functionElement = FormulaElement(FormulaElement.ElementType.FUNCTION, sensorName, tryGetParentFormulaElement())
                    }
                }
                if (functionElement != null) {
                    _formulaElementStack.push(functionElement)
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
                    _formulaElementStack.pop()
                    return FormulaElementVisitResult(functionElement)
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

            if (ctx.OPERATOR_ADD() != null) {
                val operatorAdd = ctx.OPERATOR_ADD().text
                if (operatorAdd == "+") {
                    unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.PLUS.name, parentElement)
                } else if (operatorAdd == "-") {
                    unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.MINUS.name, parentElement)
                }
            } else if (ctx.OPERATOR_LOGIC_NOT() != null) {
                unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.LOGICAL_NOT.name, parentElement)
            }

            if (unaryFormulaElement != null) {
                _formulaElementStack.push(unaryFormulaElement)
                val result = visitExpression(ctx.expression())
                unaryFormulaElement.setRightChild((result as FormulaElementVisitResult).formulaElement)
                _formulaElementStack.pop()
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
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, trimmedUDBParameter, parentElement))
            }
            if (ctx.LIST() != null) {
                val trimmedListParameter = ctx.LIST().text.trim('*')
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_LIST, trimmedListParameter, parentElement))
            }
            if (ctx.VARIABLE() != null) {
                val trimmedVariableParameter = ctx.VARIABLE().text.trim('"')
                return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_VARIABLE, trimmedVariableParameter, parentElement))
            }
        }
        return ArgumentBaseVisitResult()
    }

    private fun tryGetParentFormulaElement(): FormulaElement? {
        return if (_formulaElementStack.empty()) {
            null
        } else _formulaElementStack.peek()
    }
}