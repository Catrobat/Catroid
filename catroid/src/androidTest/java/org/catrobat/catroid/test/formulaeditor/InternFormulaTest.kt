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
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.InternFormula
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection
import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType
import org.catrobat.catroid.formulaeditor.ExternInternRepresentationMapping
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Operators
import org.junit.Test
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class InternFormulaTest {
    @Test
    fun testReplaceFunctionByToken() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        val internFormula = InternFormula(internTokens)
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula.setCursorAndSelection(internFormula.externFormulaString.length, true)
        internFormula.handleKeyInput(
            R.id.formula_editor_keyboard_4,
            ApplicationProvider.getApplicationContext(),
            null
        )
        internFormula.handleKeyInput(
            R.id.formula_editor_keyboard_2,
            ApplicationProvider.getApplicationContext(),
            null
        )
        Assert.assertNull(internFormula.selection)
        internFormula.setCursorAndSelection(internFormula.externFormulaString.length, true)
        assertInternFormulaSelectionIndices(0, 0, internFormula)
    }

    @Test
    fun testReplaceFunctionButKeepParameters() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        internTokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        val internFormula = InternFormula(internTokens)
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula.setCursorAndSelection(internFormula.externFormulaString.length, true)
        assertInternFormulaSelectionIndices(0, 9, internFormula)
        internFormula.handleKeyInput(
            R.string.formula_editor_function_rand,
            ApplicationProvider.getApplicationContext(),
            null
        )
        assertInternFormulaSelectionIndices(2, 8, internFormula)
        internFormula.setCursorAndSelection(internFormula.externFormulaString.length, true)
        assertInternFormulaSelectionIndices(0, 11, internFormula)
        internFormula.handleKeyInput(
            R.string.formula_editor_function_sqrt,
            ApplicationProvider.getApplicationContext(),
            null
        )
        assertInternFormulaSelectionIndices(2, 8, internFormula)
        internFormula.setCursorAndSelection(internFormula.externFormulaString.length, true)
        assertInternFormulaSelectionIndices(0, 9, internFormula)
    }

    @Test
    fun testSelectBrackets() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.NUMBER, "0"))
        internTokens.add(InternToken(InternTokenType.BRACKET_CLOSE))
        val internFormula = InternFormula(internTokens)
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        val externFormulaString = internFormula.externFormulaString
        internFormula.setCursorAndSelection(externFormulaString.length, true)
        assertInternFormulaSelectionIndices(0, 2, internFormula)
        internFormula.setCursorAndSelection(externFormulaString.length, true)
        assertInternFormulaSelectionIndices(0, 2, internFormula)
    }

    @Test
    fun testReplaceSelection() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        val internFormula = InternFormula(internTokens)
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula.setCursorAndSelection(1, true)
        val externFormulaString = internFormula.externFormulaString
        val tokenSelectionStartIndex = -1
        val tokenSelectionEndIndex = 3
        internFormula.internFormulaTokenSelection = InternFormulaTokenSelection(
            TokenSelectionType.USER_SELECTION, tokenSelectionStartIndex, tokenSelectionEndIndex
        )
        internFormula.handleKeyInput(
            R.id.formula_editor_keyboard_0,
            ApplicationProvider.getApplicationContext(),
            null
        )
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        Assert.assertEquals(externFormulaString, internFormula.externFormulaString)
    }

    @Test
    fun testSetExternCursorPositionLeftTo() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        internTokens.add(InternToken(InternTokenType.OPERATOR, Operators.PLUS.name))
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        val internFormula = InternFormula(internTokens)
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula.setCursorAndSelection(1, false)
        val externInternRepresentationMapping = ExternInternRepresentationMapping()
        internFormula.externInternRepresentationMapping = externInternRepresentationMapping
        val externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition()
        internFormula.setExternCursorPositionLeftTo(1)
        Assert.assertEquals(
            externCursorPositionBeforeMethodCall,
            internFormula.getExternCursorPosition()
        )
    }

    @Test
    fun testSetExternCursorPositionRightToEmptyFormula() {
        val internTokens = ArrayList<InternToken>()
        val internFormula = InternFormula(internTokens)
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula.setCursorAndSelection(1, false)
        val externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition()
        internFormula.setExternCursorPositionRightTo(1)
        Assert.assertEquals(
            externCursorPositionBeforeMethodCall,
            internFormula.getExternCursorPosition()
        )
    }

    @Test
    fun testSetExternCursorPositionRightTo() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        internTokens.add(InternToken(InternTokenType.OPERATOR, Operators.PLUS.name))
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        val internFormula = InternFormula(internTokens)
        internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula.setCursorAndSelection(1, false)
        internFormula.setExternCursorPositionRightTo(3)
        Assert.assertEquals(13, internFormula.getExternCursorPosition())
        internFormula.externInternRepresentationMapping = ExternInternRepresentationMapping()
        val externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition()
        internFormula.setExternCursorPositionRightTo(1)
        Assert.assertEquals(
            externCursorPositionBeforeMethodCall,
            internFormula.getExternCursorPosition()
        )
    }

    @Test
    fun testSelectCursorPositionInternTokenOnError() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.BRACKET_OPEN))
        internTokens.add(InternToken(InternTokenType.NUMBER, "42.42"))
        val internFormula = InternFormula(internTokens)
        internFormula.cursorPositionInternToken = null
        internFormula.selectCursorPositionInternToken(TokenSelectionType.USER_SELECTION)
        Assert.assertNull(internFormula.internFormulaTokenSelection)
    }

    @Test
    fun testTokenTrailingWhiteSpace() {
        val internTokens = ArrayList<InternToken>()
        internTokens.add(InternToken(InternTokenType.NUMBER, "0 "))
        val internFormula = InternFormula(internTokens)
        internFormula.trimExternFormulaString(ApplicationProvider.getApplicationContext())
    }

    private fun assertInternFormulaSelectionIndices(
        expectedStartIndex: Int,
        expectedEndIndex: Int,
        internFormula: InternFormula
    ) {
        Assert.assertEquals(expectedStartIndex, internFormula.selection.startIndex)
        Assert.assertEquals(expectedEndIndex, internFormula.selection.endIndex)
    }
}