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

import org.catrobat.catroid.formulaeditor.Clipboard
import org.catrobat.catroid.formulaeditor.ClipboardSerializer
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType.NUMBER
import org.catrobat.catroid.formulaeditor.Operators.PLUS
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.buildBinaryOperator
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class ClipboardSerializerTest {

    private val clipboardSerializer = ClipboardSerializer(File(""))

    @Test
    fun testSerializeAndDeserialize() {
        val formula: List<InternToken> = buildBinaryOperator(NUMBER, "1", PLUS, NUMBER, "1")

        val jsonString = clipboardSerializer.clipboardGson.toJson(Clipboard(formula))
        val retrievedFormula: Clipboard = clipboardSerializer.clipboardGson.fromJson(jsonString, Clipboard::class.java)

        for (i in formula.indices) {
            Assert.assertEquals(formula[i].internTokenType, retrievedFormula.content[i].internTokenType)
            Assert.assertEquals(formula[i].tokenStringValue, retrievedFormula.content[i].tokenStringValue)
        }
    }
}
