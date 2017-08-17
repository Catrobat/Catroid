/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.gui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.View

import org.catrobat.catroid.R
import org.catrobat.catroid.formula.Formula
import org.catrobat.catroid.formula.FormulaInterpreter
import org.catrobat.catroid.formula.Token
import org.catrobat.catroid.formula.operator.BinaryOperatorToken
import org.catrobat.catroid.formula.operator.OperatorToken
import org.catrobat.catroid.formula.value.ValueToken

class FormulaEditorActivity : AppCompatActivity() {

    val formula = Formula()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formula_editor)
    }

    fun handleEditorButton(view: View) {

        val inputField = (findViewById(R.id.input) as TextInputLayout).editText

        when (view.id) {
            R.id.btn0 -> inputField?.setText(inputField.text.append("0"))
            R.id.btn1 -> inputField?.setText(inputField.text.append("1"))
            R.id.btn2 -> inputField?.setText(inputField.text.append("2"))
            R.id.btn3 -> inputField?.setText(inputField.text.append("3"))
            R.id.btn4 -> inputField?.setText(inputField.text.append("4"))
            R.id.btn5 -> inputField?.setText(inputField.text.append("5"))
            R.id.btn6 -> inputField?.setText(inputField.text.append("6"))
            R.id.btn7 -> inputField?.setText(inputField.text.append("7"))
            R.id.btn8 -> inputField?.setText(inputField.text.append("8"))
            R.id.btn9 -> inputField?.setText(inputField.text.append("9"))

            R.id.btnDecimal -> inputField?.setText(inputField.text.append(getString(R.string
                    .formula_editor_decimal_mark)))

            R.id.btnAdd -> inputField?.setText(inputField.text
                    .append(" " + getString(R.string.formula_editor_operator_plus) + " "))
            R.id.btnSub -> inputField?.setText(inputField.text
                    .append(" " + getString(R.string.formula_editor_operator_minus) + " "))
            R.id.btnMult -> inputField?.setText(inputField.text
                    .append(" " + getString(R.string.formula_editor_operator_mult) + " "))
            R.id.btnDiv -> inputField?.setText(inputField.text
                    .append(" " + getString(R.string.formula_editor_operator_divide) + " "))

            R.id.btnBracketOpen -> inputField?.setText(inputField.text
                    .append(" " + getString(R.string.formula_editor_bracket_open) + " "))
            R.id.btnBracketClose -> inputField?.setText(inputField.text
                    .append(" " + getString(R.string.formula_editor_bracket_close) + " "))

            R.id.btnEquals -> inputField?.setText(inputField.text
                    .append(" " + getString(R.string.formula_editor_logic_equal) + " "))

            R.id.btnBack -> inputField?.setText(inputField.text?.dropLast(1))

            R.id.btnCompute -> {
                try {
                    formula.tokens.clear()
                    parseInput(inputField?.text.toString())
                    inputField?.setText(FormulaInterpreter().eval(formula.tokens).value.toString())
                } catch (exception: Exception) {
                    //TODO: handle Parsing/Evaluation Errors.
                }
            }
        }

        inputField?.setSelection(inputField.text.length)
    }

    private fun parseInput(text: String) {

        val tokens = text.split(" ")

        for (token in tokens) {
            when (token) {
                getString(R.string.formula_editor_operator_plus) -> formula.tokens
                        .add(BinaryOperatorToken.AddOperatorToken())
                getString(R.string.formula_editor_operator_minus) -> formula.tokens
                        .add(BinaryOperatorToken.SubOperatorToken())
                getString(R.string.formula_editor_operator_mult) -> formula.tokens
                        .add(BinaryOperatorToken.MultOperatorToken())
                getString(R.string.formula_editor_operator_divide) -> formula.tokens
                        .add(BinaryOperatorToken.DivOperatorToken())

                getString(R.string.formula_editor_bracket_open) -> formula.tokens
                        .add(OperatorToken.BracketOperator(Token.Type.LEFT_BRACKET))
                getString(R.string.formula_editor_bracket_close) -> formula.tokens
                        .add(OperatorToken.BracketOperator(Token.Type.RIGHT_BRACKET))

                getString(R.string.formula_editor_logic_equal) -> formula.tokens
                        .add(BinaryOperatorToken.EqualsOperatorToken())

                else -> formula.tokens.add(ValueToken(token.toDouble()))
            }
        }
    }
}
