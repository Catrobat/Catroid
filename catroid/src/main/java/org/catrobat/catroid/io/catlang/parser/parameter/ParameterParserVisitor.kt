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

internal class ParameterParserVisitor(
    private val context: Context,
    private val variables: List<String>,
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
    private val externToInternValues = InternToExternGenerator.getExternToInternValueMapping(CatrobatLanguageUtils.getEnglishContextForFormulas(context))

    private val parentFormulaStack = Stack<FormulaElement>()
    private fun tryGetParentFormulaElement(): FormulaElement? {
        return if (parentFormulaStack.empty()) {
            null
        } else parentFormulaStack.peek()
    }

    override fun visit(tree: ParseTree?): ArgumentBaseVisitResult {
        throw NotImplementedError("visit() method not implemented, and should not be used.")
    }

    override fun visitChildren(node: RuleNode?): ArgumentBaseVisitResult {
        throw NotImplementedError("visitChildren() method not implemented, and should not be used.")
    }

    override fun visitTerminal(node: TerminalNode?): ArgumentBaseVisitResult {
        throw NotImplementedError("visitTerminal() method not implemented, and should not be used.")
    }

    override fun visitErrorNode(node: ErrorNode?): ArgumentBaseVisitResult {
        throw NotImplementedError("visitErrorNode() method not implemented, and should not be used.")
    }

    override fun visitArgument(context: CatrobatParameterParser.ArgumentContext?): ArgumentBaseVisitResult {
        val response = visitExpression(context?.expression())
        val formulaElement = (response as FormulaElementVisitResult).formulaElement
        return FormulaVisitResult(Formula(formulaElement))
    }

    override fun visitExpression(context: CatrobatParameterParser.ExpressionContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("Cannot parse empty expression")
        }

        if (context.additiveExpression() != null) {
            return visitAdditiveExpression(context.additiveExpression())
        }

        var operator: Operators? = null
        if (context.OPERATOR_LOGIC_EQUAL() != null) {
            operator = Operators.EQUAL
        } else if (context.OPERATOR_LOGIC_NOT_EQUAL() != null) {
            operator = Operators.NOT_EQUAL
        } else if (context.OPERATOR_LOGIC_LOWER() != null) {
            operator = Operators.SMALLER_THAN
        } else if (context.OPERATOR_LOGIC_GREATER() != null) {
            operator = Operators.GREATER_THAN
        } else if (context.OPERATOR_LOGIC_LOWER_EQUAL() != null) {
            operator = Operators.SMALLER_OR_EQUAL
        } else if (context.OPERATOR_LOGIC_GREATER_EQUAL() != null) {
            operator = Operators.GREATER_OR_EQUAL
        }

        if (operator != null) {
            val formulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, operator.name, tryGetParentFormulaElement())
            parentFormulaStack.push(formulaElement)
            val leftResult = visitExpression(context.expression(0))
            val rightResult = visitExpression(context.expression(1))
            formulaElement.setLeftChild((leftResult as FormulaElementVisitResult).formulaElement)
            formulaElement.setRightChild((rightResult as FormulaElementVisitResult).formulaElement)
            parentFormulaStack.pop()
            return FormulaElementVisitResult(formulaElement)
        }

        throw ArgumentParsingException("No comparison operator found")
    }

    override fun visitAdditiveExpression(context: CatrobatParameterParser.AdditiveExpressionContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No additive expression found")
        }
        if (context.multiplicativeExpression().count() == 1) {
            return visitMultiplicativeExpression(context.multiplicativeExpression(0))
        }
        val additiveExpressionStack = Stack<FormulaElement>()
        for (i in context.additiveOperator().indices.reversed()) {
            val operator = (visitAdditiveOperator(context.additiveOperator(i)) as OperatorVisitResult).operator
            val formulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, operator.name, tryGetParentFormulaElement())
            parentFormulaStack.push(formulaElement)
            if (additiveExpressionStack.isNotEmpty()) {
                additiveExpressionStack.peek().setLeftChild(formulaElement)
            }
            val rightMultiplicativeExpression = (visitMultiplicativeExpression(context.multiplicativeExpression(i+1)) as FormulaElementVisitResult).formulaElement
            formulaElement.setRightChild(rightMultiplicativeExpression)
            if (i == 0) {
                val firstMultiplicativeExpression = (visitMultiplicativeExpression(context.multiplicativeExpression(i)) as FormulaElementVisitResult).formulaElement
                formulaElement.setLeftChild(firstMultiplicativeExpression)
            }
            additiveExpressionStack.push(formulaElement)
            parentFormulaStack.pop()
        }
        return FormulaElementVisitResult(additiveExpressionStack.elementAt(0))
    }

    override fun visitMultiplicativeExpression(context: CatrobatParameterParser.MultiplicativeExpressionContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No multiplicative expression found")
        }
        if (context.simpleExpression().count() == 1) {
            return visitSimpleExpression(context.simpleExpression(0))
        }
        val multiplicativeExpressionStack = Stack<FormulaElement>()
        for (i in context.multiplicativeOperator().indices.reversed()) {
            val operator = (visitMultiplicativeOperator(context.multiplicativeOperator(i)) as OperatorVisitResult).operator
            val formulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, operator.name, tryGetParentFormulaElement())
            parentFormulaStack.push(formulaElement)
            if (multiplicativeExpressionStack.isNotEmpty()) {
                multiplicativeExpressionStack.peek().setLeftChild(formulaElement)
            }
            val rightSimpleExpression = (visitSimpleExpression(context.simpleExpression(i+1)) as FormulaElementVisitResult).formulaElement
            formulaElement.setRightChild(rightSimpleExpression)
            if (i == 0) {
                val firstSimpleExpression = (visitSimpleExpression(context.simpleExpression(i)) as FormulaElementVisitResult).formulaElement
                formulaElement.setLeftChild(firstSimpleExpression)
            }
            multiplicativeExpressionStack.push(formulaElement)
            parentFormulaStack.pop()
        }
        return FormulaElementVisitResult(multiplicativeExpressionStack.elementAt(0))
    }

    override fun visitAdditiveOperator(context: CatrobatParameterParser.AdditiveOperatorContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No additive operator found")
        }
        if (context.OPERATOR_NUMERIC_ADD() != null) {
            return OperatorVisitResult(Operators.PLUS)
        }
        if (context.OPERATOR_NUMERIC_MINUS() != null) {
            return OperatorVisitResult(Operators.MINUS)
        }
        if (context.OPERATOR_LOGIC_OR() != null) {
            return OperatorVisitResult(Operators.LOGICAL_OR)
        }
        throw ArgumentParsingException("No known additive operator found")
    }

    override fun visitMultiplicativeOperator(context: CatrobatParameterParser.MultiplicativeOperatorContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No multiplicative operator found")
        }
        if (context.OPERATOR_NUMERIC_MULTIPLY() != null) {
            return OperatorVisitResult(Operators.MULT)
        }
        if (context.OPERATOR_NUMERIC_DIVIDE() != null) {
            return OperatorVisitResult(Operators.DIVIDE)
        }
        if (context.OPERATOR_LOGIC_AND() != null) {
            return OperatorVisitResult(Operators.LOGICAL_AND)
        }
        throw ArgumentParsingException("No known multiplicative operator found")
    }

    override fun visitSimpleExpression(context: CatrobatParameterParser.SimpleExpressionContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No simple expression found")
        }
        if (context.literal() != null) {
            return visitLiteral(context.literal())
        }
        if (context.unaryExpression() != null) {
            return visitUnaryExpression(context.unaryExpression())
        }
        if (context.sensorPropertyOrMethodInvocation() != null) {
            return visitSensorPropertyOrMethodInvocation(context.sensorPropertyOrMethodInvocation())
        }
        if (context.expression() != null) {
            val braceElement = FormulaElement(FormulaElement.ElementType.BRACKET, "", tryGetParentFormulaElement())
            parentFormulaStack.push(braceElement)
            val bracedExpression = visitExpression(context.expression())
            braceElement.setRightChild((bracedExpression as FormulaElementVisitResult).formulaElement)
            parentFormulaStack.pop()
            return FormulaElementVisitResult(braceElement)
        }
        throw ArgumentParsingException("No known simple expression found.")
    }

    override fun visitSensorPropertyOrMethodInvocation(context: CatrobatParameterParser.SensorPropertyOrMethodInvocationContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No sensor property or method invocation found")
        }

        val sensorPropertyOrMethod = context.SENSOR_OR_PROPERTY_OR_METHOD().text.trim()

        if (!externToInternValues.containsKey(sensorPropertyOrMethod)) {
            throw UnkownSensorException("Unknown sensor, property or method: $sensorPropertyOrMethod")
        }

        val sensorPropertyOrMethodIntern = externToInternValues[sensorPropertyOrMethod] ?: throw UnkownSensorException("Unknown sensor, property or method: $sensorPropertyOrMethod")

        val sensor = tryParseSensor(sensorPropertyOrMethodIntern)
        if (sensor != null) {
            if (context.methodParameters() != null) {
                throw UnkownSensorException("Referencing sensor $sensorPropertyOrMethod with parameters is not possible.")
            }
            val sensorFormulaElement = FormulaElement(FormulaElement.ElementType.SENSOR, sensor.name, tryGetParentFormulaElement())
            return FormulaElementVisitResult(sensorFormulaElement)
        }
        val function = tryParseFunction(sensorPropertyOrMethodIntern)
        if (function != null) {
            val functionFormulaElement = parseFunction(sensorPropertyOrMethod, function, context)
            return FormulaElementVisitResult(functionFormulaElement)
        }
        throw UnkownSensorException("Unknown sensor, property or method: $sensorPropertyOrMethod")
    }

    private fun parseFunction(functionText: String, function: Functions, context: CatrobatParameterParser.SensorPropertyOrMethodInvocationContext): FormulaElement {
        val functionFormulaElement = FormulaElement(FormulaElement.ElementType.FUNCTION, function.name, tryGetParentFormulaElement())
        parentFormulaStack.push(functionFormulaElement)

        if (!FUNCTION_TO_NUMBER_OF_PARAMETER_MAP.containsKey(function)) {
            throw UnkownFunctionException("Unknown number of parameters for function: $functionText")
        }
        val numberOfParametersExpected = FUNCTION_TO_NUMBER_OF_PARAMETER_MAP[function] ?: throw UnkownFunctionException("Unknown function: $function")

        if (numberOfParametersExpected == 0 && context.methodParameters() != null) {
            throw ArgumentParsingException("No parameters allowed for function $functionText.")
        }
        val parameters = visitMethodParameters(context.methodParameters()) as ParameterVisitResult
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

        parentFormulaStack.pop()
        return functionFormulaElement
    }

    private fun tryParseSensor(sensor: String): Sensors? {
        return try {
            Sensors.valueOf(sensor)
        } catch (exception: IllegalArgumentException) {
            null
        }
    }

    private fun tryParseFunction(function: String): Functions? {
        return try {
            Functions.valueOf(function)
        } catch (exception: IllegalArgumentException) {
            null
        }
    }

    override fun visitMethodParameters(context: CatrobatParameterParser.MethodParametersContext?): ArgumentBaseVisitResult {
        return visitParameterList(context?.parameterList())
    }

    override fun visitParameterList(context: CatrobatParameterParser.ParameterListContext?): ArgumentBaseVisitResult {
        if (context?.expression() == null) {
            return ParameterVisitResult(null, null, arrayListOf(), 0)
        }
        val parameters = arrayListOf<FormulaElement>()
        for (expression in context.expression()) {
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

    override fun visitUnaryExpression(context: CatrobatParameterParser.UnaryExpressionContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No unary expression found")
        }
        var unaryFormulaElement: FormulaElement? = null

        if (context.OPERATOR_NUMERIC_ADD() != null) {
            unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.PLUS.name, tryGetParentFormulaElement())
        } else if (context.OPERATOR_NUMERIC_MINUS() != null) {
            unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.MINUS.name, tryGetParentFormulaElement())
        } else if (context.OPERATOR_LOGIC_NOT() != null) {
            unaryFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.LOGICAL_NOT.name, tryGetParentFormulaElement())
        }

        if (unaryFormulaElement != null) {
            parentFormulaStack.push(unaryFormulaElement)
            val result = visitExpression(context.expression())
            unaryFormulaElement.setRightChild((result as FormulaElementVisitResult).formulaElement)
            parentFormulaStack.pop()
            return FormulaElementVisitResult(unaryFormulaElement)
        }
        throw ArgumentParsingException("Cannot parse unary expression. No valid operator found.")
    }

    override fun visitLiteral(context: CatrobatParameterParser.LiteralContext?): ArgumentBaseVisitResult {
        if (context == null) {
            throw ArgumentParsingException("No literal found")
        }
        if (context.NUMBER() != null) {
            return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.NUMBER, context.NUMBER().text, tryGetParentFormulaElement()))
        }
        if (context.STRING() != null) {
            val trimmedString = trimFirstAndLastCharacter(context.STRING().text).replace("\\'", "'")
            return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.STRING, trimmedString, tryGetParentFormulaElement()))
        }
        if (context.UDB_PARAMETER() != null) {
            val trimmedUDBParameter = trimFirstAndLastCharacter(context.UDB_PARAMETER().text).replace("\\[", "[").replace("\\]", "]")
            if (!userDefinedBrickParameters.contains(trimmedUDBParameter)) {
                throw ArgumentParsingException("Unknown user defined brick parameter: $trimmedUDBParameter")
            }
            return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, trimmedUDBParameter, tryGetParentFormulaElement()))
        }
        if (context.LIST() != null) {
            val trimmedListParameter = trimFirstAndLastCharacter(context.LIST().text).replace("\\*", "*")
            if (!userLists.contains(trimmedListParameter)) {
                throw ArgumentParsingException("Unknown list: $trimmedListParameter")
            }
            return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_LIST, trimmedListParameter, tryGetParentFormulaElement()))
        }
        if (context.VARIABLE() != null) {
            val trimmedVariableParameter = trimFirstAndLastCharacter(context.VARIABLE().text).replace("\\\"", "\"")
            if (!variables.contains(trimmedVariableParameter)) {
                throw ArgumentParsingException("Unknown variable: $trimmedVariableParameter")
            }
            return FormulaElementVisitResult(FormulaElement(FormulaElement.ElementType.USER_VARIABLE, trimmedVariableParameter, tryGetParentFormulaElement()))
        }
        throw ArgumentParsingException("No literal found")
    }

    private fun trimFirstAndLastCharacter(string: String): String {
        return string.substring(1, string.length - 1)
    }
}