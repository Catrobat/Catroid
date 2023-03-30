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

import android.content.Context
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternFormula
import org.junit.Before
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.Operators
import org.junit.Test
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class DeleteInternTokenTest {
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
    var expectedFormulaString: String? = null
    private var internFormula: InternFormula? = null
    @Before
    fun setUp() {
        val internTokens = ArrayList(Arrays.asList(*initialTokens))
        internFormula = InternFormula(internTokens)
        internFormula!!.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula!!.setCursorAndSelection(0, false)
    }

    @Test
    fun testExternFormulaString() {
        internFormula!!.externCursorPosition = externCursorPosition
        internFormula!!.handleKeyInput(
            R.id.formula_editor_keyboard_delete,
            ApplicationProvider.getApplicationContext(),
            null
        )
        internFormula!!.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        Assert.assertEquals(expectedFormulaString, internFormula!!.externFormulaString)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "Invalid CursorPosition", arrayOf(
                            InternToken(InternTokenType.NUMBER, "42.42"),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            ),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        -1, "42.42( 42.42 "
                    ), arrayOf(
                        "Begin CursorPosition", arrayOf(
                            InternToken(InternTokenType.NUMBER, "42.42"),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            ),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        0, "42.42( 42.42 "
                    ), arrayOf(
                        "CursorPosition 1",
                        arrayOf(
                            InternToken(InternTokenType.NUMBER, "42.42"),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            ),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        1,
                        "2.42( 42.42 "
                    ), arrayOf(
                        "CursorPosition 8",
                        arrayOf(
                            InternToken(InternTokenType.NUMBER, "42.42"),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            ),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        8,
                        "42.42( 2.42 "
                    ), arrayOf(
                        "Invalid Deletion of Brace",
                        arrayOf(
                            InternToken(InternTokenType.NUMBER, "42.42"),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            ),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        6,
                        "42.42( 42.42 "
                    ), arrayOf(
                        "Invalid Sin Deletion Begin",
                        arrayOf(
                            InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            )
                        ),
                        1,
                        "sine( "
                    ), arrayOf(
                        "Invalid Sin Deletion End",
                        arrayOf(
                            InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            ),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        ApplicationProvider.getApplicationContext<Context>().resources.getString(R.string.formula_editor_function_sin)
                            .length,
                        "sine( 42.42 "
                    )
                )
            )
        }
    }
}