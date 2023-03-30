/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.formulaeditor.InternToken
import androidx.annotation.IdRes
import org.catrobat.catroid.formulaeditor.InternFormula
import org.junit.Before
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.Operators
import org.junit.Test
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class InternTokenInsertTokenTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null

    @Parameterized.Parameter(1)
     lateinit var initialTokens: Array<InternToken>
    @JvmField
    @Parameterized.Parameter(2)
    var externCursorPosition = 0
    @JvmField
    @Parameterized.Parameter(3)
    @IdRes
    var keyInputId = 0
    @JvmField
    @Parameterized.Parameter(4)
    var tokenPosition = 0
    @JvmField
    @Parameterized.Parameter(5)
    var expectedStringTokenValue: String? = null
    @JvmField
    @Parameterized.Parameter(6)
    var expectedFormulaString: String? = null
    private var internFormula: InternFormula? = null
    private var internTokens: ArrayList<InternToken>? = null
    @Before
    fun setUp() {
        internTokens = ArrayList(Arrays.asList(*initialTokens))
        internFormula = InternFormula(internTokens)
        internFormula!!.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula!!.setCursorAndSelection(externCursorPosition, false)
        internFormula!!.handleKeyInput(
            keyInputId,
            ApplicationProvider.getApplicationContext(),
            null
        )
    }

    @Test
    fun testTokenStringValueFormulaString() {
        Assert.assertEquals(
            expectedStringTokenValue,
            internTokens!![tokenPosition].tokenStringValue
        )
        Assert.assertEquals(expectedFormulaString, internFormula!!.externFormulaString)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "Formula: .+",
                        arrayOf(InternToken(InternTokenType.OPERATOR, Operators.PLUS.name)),
                        0,
                        R.id.formula_editor_keyboard_decimal_mark,
                        0,
                        "0.",
                        "0. + "
                    ), arrayOf(
                        "Formula: +.",
                        arrayOf(InternToken(InternTokenType.OPERATOR, Operators.PLUS.name)),
                        1,
                        R.id.formula_editor_keyboard_decimal_mark,
                        1,
                        "0.",
                        "+ 0. "
                    ), arrayOf(
                        "Formula: .",
                        arrayOf<InternToken>(),
                        0,
                        R.id.formula_editor_keyboard_decimal_mark,
                        0,
                        "0.",
                        "0. "
                    ), arrayOf(
                        "Formula: 42.42",
                        arrayOf(InternToken(InternTokenType.NUMBER, "42.42")),
                        0,
                        R.id.formula_editor_keyboard_decimal_mark,
                        0,
                        "42.42",
                        "42.42 "
                    ), arrayOf(
                        "Formula: 42.42 0",
                        arrayOf(InternToken(InternTokenType.NUMBER, "42.42")),
                        6,
                        R.id.formula_editor_keyboard_0,
                        0,
                        "42.420",
                        "42.420 "
                    ), arrayOf(
                        "Formula: 42.42 .",
                        arrayOf(InternToken(InternTokenType.NUMBER, "42.42")),
                        6,
                        R.id.formula_editor_keyboard_decimal_mark,
                        0,
                        "42.42",
                        "42.42 "
                    ), arrayOf(
                        "Formula: 4242 .",
                        arrayOf(InternToken(InternTokenType.NUMBER, "4242")),
                        5,
                        R.id.formula_editor_keyboard_decimal_mark,
                        0,
                        "4242.",
                        "4242. "
                    ), arrayOf(
                        "Formula Token(0): 12 MULT 34",
                        arrayOf(InternToken(InternTokenType.NUMBER, "1234")),
                        2,
                        R.id.formula_editor_keyboard_mult,
                        0,
                        "12",
                        "12 × 34 "
                    ), arrayOf(
                        "Formula Token(1): 34 MULT 56",
                        arrayOf(InternToken(InternTokenType.NUMBER, "3456")),
                        2,
                        R.id.formula_editor_keyboard_mult,
                        1,
                        "MULT",
                        "34 × 56 "
                    ), arrayOf(
                        "Formula Token(2): 78 MULT 90 .",
                        arrayOf(InternToken(InternTokenType.NUMBER, "7890")),
                        2,
                        R.id.formula_editor_keyboard_mult,
                        2,
                        "90",
                        "78 × 90 "
                    )
                )
            )
        }
    }
}
