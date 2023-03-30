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
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Operators
import org.junit.Assert
import org.junit.Test
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class NoSelectionInternTokenTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null

    @Parameterized.Parameter(1)
    lateinit var initialTokens: Array<InternToken>
    @JvmField
    @Parameterized.Parameter(2)
    var externCursorPosition = 0
    private var internFormula: InternFormula? = null
    @Before
    fun setUp() {
        val internTokens = ArrayList(Arrays.asList(*initialTokens))
        internFormula = InternFormula(internTokens)
        internFormula!!.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula!!.setCursorAndSelection(externCursorPosition, true)
    }

    @Test
    fun testExternFormulaString() {
        Assert.assertNull(internFormula!!.internFormulaTokenSelection)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "Select Bracket OPEN", arrayOf(
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        0
                    ), arrayOf(
                        "Select Bracket CLOSE", arrayOf(
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        0
                    ), arrayOf(
                        "Select Bracket CLOSE SIN",
                        arrayOf(
                            InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name),
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE),
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        ApplicationProvider.getApplicationContext<Context>().resources.getString(R.string.formula_editor_function_sin)
                            .length + 4
                    ), arrayOf(
                        "Select SIN Bracket OPEN", arrayOf(
                            InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name),
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
                            InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        ApplicationProvider.getApplicationContext<Context>().resources.getString(R.string.formula_editor_function_sin)
                            .length + 2
                    ), arrayOf(
                        "Select SIN name end", arrayOf(
                            InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name),
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        ApplicationProvider.getApplicationContext<Context>().resources.getString(R.string.formula_editor_function_sin)
                            .length
                    ), arrayOf(
                        "Select SIN name begin", arrayOf(
                            InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name),
                            InternToken(
                                InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN,
                                Operators.PLUS.name
                            ),
                            InternToken(InternTokenType.NUMBER, "42.42")
                        ),
                        1
                    )
                )
            )
        }
    }
}
