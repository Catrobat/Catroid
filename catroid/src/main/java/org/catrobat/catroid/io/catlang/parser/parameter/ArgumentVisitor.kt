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
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.CatrobatParameterParser
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.CatrobatParameterParserVisitor
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils
import java.util.Stack

class ArgumentVisitor(
    private val context: Context,
    private val userVariables: List<String>,
    private val userLists: List<String>,
    private val userDefinedBrickParameters: List<String>
    ) : CatrobatParameterParserVisitor<ArgumentBaseVisitResult> {

    companion object {
        val FUNCTION_TO_NUMBER_OF_PARAMETER_MAP = mapOf(
            Functions.SIN to 1,
            Functions.COS to 1,
            Functions.TAN to 1,
            Functions.LN to 1,
            Functions.LOG to 1,
            Functions.PI to 0,
            Functions.SQRT to 1,
            Functions.RAND to 2,
            Functions.ABS to 1,
            Functions.ROUND to 1,
            Functions.MOD to 2,
            Functions.ARCSIN to 1,
            Functions.ARCCOS to 1,
            Functions.ARCTAN to 1,
            Functions.ARCTAN2 to 2,
            Functions.EXP to 1,
            Functions.POWER to 2,
            Functions.FLOOR to 1,
            Functions.CEIL to 1,
            Functions.MAX to 2,
            Functions.MIN to 2,
            Functions.IF_THEN_ELSE to 3,
            Functions.TRUE to 0,
            Functions.FALSE to 0,
            Functions.LENGTH to 1,
            Functions.NUMBER_OF_ITEMS to 1,
            Functions.LETTER to 2,
            Functions.JOIN to 2,
            Functions.JOIN3 to 3,
            Functions.REGEX to 2,
            Functions.FLATTEN to 1,
            Functions.ARDUINODIGITAL to 1,
            Functions.ARDUINOANALOG to 1,
            Functions.RASPIDIGITAL to 1,
            Functions.MULTI_FINGER_X to 1,
            Functions.MULTI_FINGER_Y to 1,
            Functions.MULTI_FINGER_TOUCHED to 0,
            Functions.INDEX_CURRENT_TOUCH to 1,
            Functions.LIST_ITEM to 2,
            Functions.CONTAINS to 2,
            Functions.INDEX_OF_ITEM to 2,
            Functions.COLOR_AT_XY to 2,
            Functions.COLOR_EQUALS_COLOR to 3,
            Functions.COLLIDES_WITH_COLOR to 1,
            Functions.COLOR_TOUCHES_COLOR to 2,
            Functions.OBJECT_WITH_ID_VISIBLE to 1,
            Functions.ID_OF_DETECTED_OBJECT to 1,
            Functions.TEXT_BLOCK_X to 1,
            Functions.TEXT_BLOCK_Y to 1,
            Functions.TEXT_BLOCK_SIZE to 1,
            Functions.TEXT_BLOCK_FROM_CAMERA to 1,
            Functions.TEXT_BLOCK_LANGUAGE_FROM_CAMERA to 1
        )
    }

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
        if (ctx.simpleExpression().count() == 1) {
            return visitSimpleExpression(ctx.simpleExpression(0))
        }

        var multiplicativeExpressionStack = Stack<FormulaElement>()

        for (i in ctx.multiplicativeOperator().indices.reversed()) {
            val operator = (visitMultiplicativeOperator(ctx.multiplicativeOperator(i)) as OperatorVisitResult).operator
            val formulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, operator.name, tryGetParentFormulaElement())
            formulaElementStack.push(formulaElement)
            if (multiplicativeExpressionStack.isNotEmpty()) {
                multiplicativeExpressionStack.peek().setLeftChild(formulaElement)
            }
            val rightSimpleExpression = (visitSimpleExpression(ctx.simpleExpression(i+1)) as FormulaElementVisitResult).formulaElement
            formulaElement.setRightChild(rightSimpleExpression)
            if (i == 0) {
                val firstSimpleExpression = (visitSimpleExpression(ctx.simpleExpression(i)) as FormulaElementVisitResult).formulaElement
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

    override fun visitSimpleExpression(ctx: CatrobatParameterParser.SimpleExpressionContext?): ArgumentBaseVisitResult {
        if (ctx != null) {
            if (ctx.literal() != null) {
                return visitLiteral(ctx.literal())
            }
            if (ctx.unaryExpression() != null) {
                return visitUnaryExpression(ctx.unaryExpression())
            }
            if (ctx.sensorPropertyOrMethodInvocation() != null) {
                return visitSensorPropertyOrMethodInvocation(ctx.sensorPropertyOrMethodInvocation())
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

    override fun visitSensorPropertyOrMethodInvocation(ctx: CatrobatParameterParser.SensorPropertyOrMethodInvocationContext?): ArgumentBaseVisitResult {
        if (ctx == null) {
            throw ArgumentParsingException("No sensor property or method invocation found")
        }

        val sensorPropertyOrMethod = ctx.SENSOR_OR_PROPERTY_OR_METHOD().text.trim()

        if (!externToInternValues.containsKey(sensorPropertyOrMethod)) {
            throw UnkownSensorException("Unknown sensor, property or method: $sensorPropertyOrMethod")
        }

        val sensorPropertyOrMethodIntern = externToInternValues[sensorPropertyOrMethod] ?: throw UnkownSensorException("Unknown sensor, property or method: $sensorPropertyOrMethod")

        val sensor = tryParseSensor(sensorPropertyOrMethodIntern)
        if (sensor != null) {
            if (ctx.methodParameters() != null) {
                throw UnkownSensorException("Referencing sensor $sensorPropertyOrMethod with parameters is not possible.")
            }
            val sensorFormulaElement = FormulaElement(FormulaElement.ElementType.SENSOR, sensor.name, tryGetParentFormulaElement())
            return FormulaElementVisitResult(sensorFormulaElement)
        }

        val function = tryParseFunction(sensorPropertyOrMethodIntern)
        if (function != null) {
            val functionFormulaElement = parseFunction(sensorPropertyOrMethod, function, ctx)
            return FormulaElementVisitResult(functionFormulaElement)
        }
        throw UnkownSensorException("Unknown sensor, property or method: $sensorPropertyOrMethod")
    }

    private fun parseFunction(functionText: String, function: Functions, ctx: CatrobatParameterParser.SensorPropertyOrMethodInvocationContext): FormulaElement {
        val functionFormulaElement = FormulaElement(FormulaElement.ElementType.FUNCTION, function.name, tryGetParentFormulaElement())
        formulaElementStack.push(functionFormulaElement)

        if (!FUNCTION_TO_NUMBER_OF_PARAMETER_MAP.containsKey(function)) {
            throw UnkownFunctionException("Unknown number of parameters for function: $functionText")
        }
        val numberOfParametersExpected = FUNCTION_TO_NUMBER_OF_PARAMETER_MAP[function] ?: throw UnkownFunctionException("Unknown function: $function")

        if (numberOfParametersExpected == 0 && ctx.methodParameters() != null) {
            throw ArgumentParsingException("No parameters allowed for function $functionText.")
        }

        val parameters = visitMethodParameters(ctx.methodParameters()) as ParameterVisitResult

        if (numberOfParametersExpected != parameters.numberOfParameters) {
            if (function == Functions.JOIN || function == Functions.JOIN3) {
                if (parameters.numberOfParameters == FUNCTION_TO_NUMBER_OF_PARAMETER_MAP[Functions.JOIN]) {
                    functionFormulaElement.value = Functions.JOIN.name
                } else if (parameters.numberOfParameters == FUNCTION_TO_NUMBER_OF_PARAMETER_MAP[Functions.JOIN3]) {
                    functionFormulaElement.value = Functions.JOIN3.name
                } else {
                    throw ArgumentParsingException("Wrong number of parameters for function $functionText. Expected ${FUNCTION_TO_NUMBER_OF_PARAMETER_MAP[Functions.JOIN]} or ${FUNCTION_TO_NUMBER_OF_PARAMETER_MAP[Functions.JOIN3]}, but got ${parameters.numberOfParameters}")
                }
            } else {
                throw ArgumentParsingException("Wrong number of parameters for function $functionText. Expected $numberOfParametersExpected, but got ${parameters.numberOfParameters}")
            }
        }

        val leftChild = parameters.leftChild
        val rightChild = parameters.rightChild
        val additionalChildren = parameters.additionalChildren

        if (leftChild != null) {
            functionFormulaElement.setLeftChild(leftChild)
        }
        if (rightChild != null) {
            functionFormulaElement.setRightChild(rightChild)
        }
        if (!additionalChildren.isNullOrEmpty()) {
            functionFormulaElement.additionalChildren = additionalChildren
        }

        formulaElementStack.pop()
        return functionFormulaElement
    }

    private fun tryParseSensor(sensor: String): Sensors? {
        return try {
            Sensors.valueOf(sensor)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun tryParseFunction(function: String): Functions? {
        return try {
            Functions.valueOf(function)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun visitMethodParameters(ctx: CatrobatParameterParser.MethodParametersContext?): ArgumentBaseVisitResult {
        return visitParameterList(ctx?.parameterList())
    }

    override fun visitParameterList(ctx: CatrobatParameterParser.ParameterListContext?): ArgumentBaseVisitResult {
        if (ctx?.expression() == null) {
            return ParameterVisitResult(null, null, arrayListOf(), 0)
        }
        val parameters = arrayListOf<FormulaElement>()
        for (expression in ctx.expression()) {
            val result = visitExpression(expression)
            parameters.add((result as FormulaElementVisitResult).formulaElement)
        }
        val numberOfParameters = parameters.size

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

        return ParameterVisitResult(leftParameter, rightParameter, parameters, numberOfParameters)
    }

    override fun visitUnaryExpression(ctx: CatrobatParameterParser.UnaryExpressionContext?): ArgumentBaseVisitResult {
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
        if (ctx == null) {
            throw ArgumentParsingException("No literal found")
        }
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

        throw ArgumentParsingException("No literal found")
    }
}