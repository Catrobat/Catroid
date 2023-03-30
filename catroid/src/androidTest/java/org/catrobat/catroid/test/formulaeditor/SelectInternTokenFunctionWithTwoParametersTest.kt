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
import org.catrobat.catroid.formulaeditor.InternTokenType
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternToExternGenerator
import org.junit.Test
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class SelectInternTokenFunctionWithTwoParametersTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var functionToken: InternToken? = null
    private var internFormula: InternFormula? = null
    private var functionName: String? = null
    @Before
    fun setUp() {
        val internTokens = ArrayList<InternToken?>()
        internTokens.add(functionToken)
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.NUMBER, "0"))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER))
        internTokens.add(InternToken(InternTokenType.STRING, "A"))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        internFormula = InternFormula(internTokens)
        internFormula!!.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        functionName = ApplicationProvider.getApplicationContext<Context>().resources
            .getString(InternToExternGenerator.getMappedString(functionToken!!.tokenStringValue))
    }

    @Test
    fun testSelectFunctionNameBegin() {
        internFormula!!.setCursorAndSelection(0, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
    }

    @Test
    fun testSelectFunctionNameMiddle() {
        internFormula!!.setCursorAndSelection(functionName!!.length / 2, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
    }

    @Test
    fun testSelectFunctionNameEnd() {
        internFormula!!.setCursorAndSelection(functionName!!.length, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
    }

    @Test
    fun testSelectBracketOpen() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 1, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
    }

    @Test
    fun testSelectNumberParameter() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 2, true)
        assertInternFormulaSelectionIndices(2, 2, internFormula)
        internFormula!!.setCursorAndSelection(functionName!!.length + 3, true)
        assertInternFormulaSelectionIndices(2, 2, internFormula)
    }

    @Test
    fun testSelectDelimiter() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 4, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
        internFormula!!.setCursorAndSelection(functionName!!.length + 5, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
    }

    @Test
    fun testSelectStringParameterBegin() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 6, true)
        assertInternFormulaSelectionIndices(4, 4, internFormula)
    }

    @Test
    fun testSelectStringParameterMiddle() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 8, true)
        assertInternFormulaSelectionIndices(4, 4, internFormula)
    }

    @Test
    fun testSelectStringParameterEnd() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 9, true)
        assertInternFormulaSelectionIndices(4, 4, internFormula)
    }

    @Test
    fun testSelectBracketClose() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 10, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
        internFormula!!.setCursorAndSelection(functionName!!.length + 12, true)
        assertInternFormulaSelectionIndices(0, 5, internFormula)
    }

    private fun assertInternFormulaSelectionIndices(
        expectedStartIndex: Int,
        expectedEndIndex: Int,
        internFormula: InternFormula?
    ) {
        Assert.assertEquals(expectedStartIndex, internFormula!!.selection.startIndex)
        Assert.assertEquals(expectedEndIndex, internFormula.selection.endIndex)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        Functions.RAND.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name)
                    ), arrayOf(
                        Functions.MOD.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name)
                    ), arrayOf(
                        Functions.POWER.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.POWER.name)
                    ), arrayOf(
                        Functions.MAX.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.MAX.name)
                    ), arrayOf(
                        Functions.MIN.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.MIN.name)
                    ), arrayOf(
                        Functions.LETTER.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.LETTER.name)
                    ), arrayOf(
                        Functions.JOIN.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name)
                    ), arrayOf(
                        Functions.REGEX.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name)
                    ), arrayOf(
                        Functions.LIST_ITEM.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.LIST_ITEM.name)
                    ), arrayOf(
                        Functions.CONTAINS.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.CONTAINS.name)
                    ), arrayOf(
                        Functions.INDEX_OF_ITEM.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.CONTAINS.name)
                    )
                )
            )
        }
    }
}
