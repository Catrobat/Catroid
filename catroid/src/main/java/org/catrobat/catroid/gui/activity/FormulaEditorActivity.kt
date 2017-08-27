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

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import org.catrobat.catroid.R
import org.catrobat.catroid.formula.FormulaInterpreter
import org.catrobat.catroid.formula.Token
import org.catrobat.catroid.formula.operator.BinaryOperatorToken
import org.catrobat.catroid.formula.operator.OperatorToken
import org.catrobat.catroid.formula.stringprovider.FormulaTextProvider
import org.catrobat.catroid.formula.value.ValueToken

class FormulaEditorActivity : AppCompatActivity() {

    private val tokens = ArrayList<Token>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formula_editor)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar?)
        supportActionBar?.setTitle(R.string.formula_editor_title)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.formula_editor_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.btnSave -> {
            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    fun handleEditorButton(view: View) {

        (findViewById(R.id.input) as TextInputLayout).error = null
        (findViewById(R.id.input) as TextInputLayout).hint = null

        val inputField = (findViewById(R.id.input) as TextInputLayout).editText

        when (view.id) {
            R.id.btn0 -> appendDigit('0')
            R.id.btn1 -> appendDigit('1')
            R.id.btn2 -> appendDigit('2')
            R.id.btn3 -> appendDigit('3')
            R.id.btn4 -> appendDigit('4')
            R.id.btn5 -> appendDigit('5')
            R.id.btn6 -> appendDigit('6')
            R.id.btn7 -> appendDigit('7')
            R.id.btn8 -> appendDigit('8')
            R.id.btn9 -> appendDigit('9')

            R.id.btnDecimal -> appendDigit('.')

            R.id.btnAdd -> tokens.add(BinaryOperatorToken.AddOperatorToken())
            R.id.btnSub -> tokens.add(BinaryOperatorToken.SubOperatorToken())
            R.id.btnMult -> tokens.add(BinaryOperatorToken.MultOperatorToken())
            R.id.btnDiv -> tokens.add(BinaryOperatorToken.DivOperatorToken())

            R.id.btnBracketOpen -> tokens.add(OperatorToken.BracketOperator(Token.Type.LEFT_BRACKET))
            R.id.btnBracketClose -> tokens.add(OperatorToken.BracketOperator(Token.Type.RIGHT_BRACKET))

            R.id.btnEquals -> tokens.add(BinaryOperatorToken.EqualsOperatorToken())

            R.id.btnBack -> if (tokens.isNotEmpty()) removeToken(tokens.last())

            R.id.btnCompute -> {
                try {
                    (findViewById(R.id.input) as TextInputLayout).hint =
                            FormulaInterpreter().eval(tokens).getString()
                } catch (exception: Exception) {
                    (findViewById(R.id.input) as TextInputLayout).error = getString(R.string.fe_parser_error)
                }
            }
        }

        inputField?.setText(FormulaTextProvider(resources).getText(tokens))
    }

    private fun appendDigit(digit: Char) {
        if (tokens.isEmpty() || tokens.last() !is ValueToken) tokens.add(ValueToken(0.0))

        (tokens.last() as ValueToken).appendDigit(digit)
    }

    private fun removeToken(token: Token) {
        when (token) {
            is ValueToken -> if (token.removeDigit()) tokens.remove(token)
            else -> tokens.remove(token)
        }
    }
}
