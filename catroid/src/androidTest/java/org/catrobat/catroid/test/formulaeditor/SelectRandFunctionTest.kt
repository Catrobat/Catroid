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

package org.catrobat.catroid.test.formulaeditor

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.R

import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternFormula
import org.catrobat.catroid.formulaeditor.InternToExternGenerator
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SelectRandFunctionTest {

    private var internFormula: InternFormula? = null
    private var functionName: String = ""

    @Before
    fun setUp() {
        val internTokens = ArrayList<InternToken>()
        val from: String = ApplicationProvider.getApplicationContext<Context>().resources
            .getString(R.string.formula_editor_from)
        val to: String = ApplicationProvider.getApplicationContext<Context>().resources
            .getString(R.string.formula_editor_to)
        internTokens.add(InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name))
        internTokens.add(InternToken(InternTokenType.STRING, from))
        internTokens.add(InternToken(InternTokenType.NUMBER, "0"))
        internTokens.add(InternToken(InternTokenType.STRING, to))
        internTokens.add(InternToken(InternTokenType.STRING, "5"))
        this.internFormula = InternFormula(internTokens)
        this.internFormula?.generateExternFormulaStringAndInternExternMapping(
            ApplicationProvider
                .getApplicationContext()
        )
        this.functionName = ApplicationProvider.getApplicationContext<Context>().resources
            .getString(InternToExternGenerator.getMappedString(Functions.RAND.name))
    }

    @Test
    fun testSelectFunctionNameBegin() {
        internFormula?.setCursorAndSelection(0, true)
        assertInternFormulaSelectionIndices(internFormula)
    }

    private fun assertInternFormulaSelectionIndices(
        internFormula: InternFormula?
    ) {
        Assert.assertEquals(0, internFormula?.selection?.startIndex)
        Assert.assertEquals(0, internFormula?.selection?.endIndex)
    }
}
