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
class SelectInternTokenFunctionWithOneParameterTest {
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
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        internFormula = InternFormula(internTokens)
        internFormula!!.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        functionName = ApplicationProvider.getApplicationContext<Context>().resources
            .getString(InternToExternGenerator.getMappedString(functionToken!!.tokenStringValue))
    }

    @Test
    fun testSelectFunctionNameBegin() {
        internFormula!!.setCursorAndSelection(0, true)
        assertInternFormulaSelectionIndices(0, 3, internFormula)
    }

    @Test
    fun testSelectFunctionNameMiddle() {
        internFormula!!.setCursorAndSelection(functionName!!.length / 2, true)
        assertInternFormulaSelectionIndices(0, 3, internFormula)
    }

    @Test
    fun testSelectFunctionNameEnd() {
        internFormula!!.setCursorAndSelection(functionName!!.length, true)
        assertInternFormulaSelectionIndices(0, 3, internFormula)
    }

    @Test
    fun testSelectBracketOpen() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 1, true)
        assertInternFormulaSelectionIndices(0, 3, internFormula)
    }

    @Test
    fun testSelectNumberParameter() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 2, true)
        assertInternFormulaSelectionIndices(2, 2, internFormula)
        internFormula!!.setCursorAndSelection(functionName!!.length + 3, true)
        assertInternFormulaSelectionIndices(2, 2, internFormula)
    }

    @Test
    fun testSelectStringParameterEnd() {
        internFormula!!.setCursorAndSelection(functionName!!.length + 4, true)
        assertInternFormulaSelectionIndices(0, 3, internFormula)
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
                        Functions.SIN.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name)
                    ), arrayOf(
                        Functions.COS.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name)
                    ), arrayOf(
                        Functions.TAN.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.TAN.name)
                    ), arrayOf(
                        Functions.LOG.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.LOG.name)
                    ), arrayOf(
                        Functions.SQRT.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.SQRT.name)
                    ), arrayOf(
                        Functions.ABS.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.ABS.name)
                    ), arrayOf(
                        Functions.ROUND.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name)
                    ), arrayOf(
                        Functions.ARCSIN.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.ARCSIN.name)
                    ), arrayOf(
                        Functions.ARCCOS.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.ARCCOS.name)
                    ), arrayOf(
                        Functions.ARCTAN.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.ARCTAN.name)
                    ), arrayOf(
                        Functions.LENGTH.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.LENGTH.name)
                    ), arrayOf(
                        Functions.LETTER.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.LETTER.name)
                    ), arrayOf(
                        Functions.NUMBER_OF_ITEMS.name,
                        InternToken(InternTokenType.FUNCTION_NAME, Functions.NUMBER_OF_ITEMS.name)
                    )
                )
            )
        }
    }
}