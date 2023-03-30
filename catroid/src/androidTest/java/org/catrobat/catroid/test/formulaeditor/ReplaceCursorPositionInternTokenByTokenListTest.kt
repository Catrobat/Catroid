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
import org.catrobat.catroid.formulaeditor.InternFormula.CursorTokenPropertiesAfterModification
import org.catrobat.catroid.formulaeditor.InternFormula
import org.junit.Before
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Test
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class ReplaceCursorPositionInternTokenByTokenListTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null

    @Parameterized.Parameter(1)
    lateinit var initialTokens: Array<InternToken>

    @Parameterized.Parameter(2)
    lateinit var tokensToReplaceWith: Array<InternToken>
    @JvmField
    @Parameterized.Parameter(3)
    var expectedModification: CursorTokenPropertiesAfterModification? = null
    private var internFormula: InternFormula? = null
    @Before
    fun setUp() {
        val internTokens = ArrayList(Arrays.asList(*initialTokens))
        internFormula = InternFormula(internTokens)
        internFormula!!.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext())
        internFormula!!.setCursorAndSelection(1, true)
    }

    @Test
    fun testReplaceInternTokenModification() {
        Assert.assertEquals(
            expectedModification, internFormula!!.replaceCursorPositionInternTokenByTokenList(
                Arrays.asList(*tokensToReplaceWith)
            )
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "Replace number with the same",
                        arrayOf(InternToken(InternTokenType.NUMBER, "42.42")),
                        arrayOf(InternToken(InternTokenType.NUMBER, "42.42")),
                        CursorTokenPropertiesAfterModification.DO_NOT_MODIFY
                    ), arrayOf(
                        "Replace Float number with period",
                        arrayOf(InternToken(InternTokenType.NUMBER, "42.42")),
                        arrayOf(InternToken(InternTokenType.PERIOD)),
                        CursorTokenPropertiesAfterModification.DO_NOT_MODIFY
                    ), arrayOf(
                        "Replace Integer number with period",
                        arrayOf(InternToken(InternTokenType.NUMBER, "4242")),
                        arrayOf(InternToken(InternTokenType.PERIOD)),
                        CursorTokenPropertiesAfterModification.DO_NOT_MODIFY
                    ), arrayOf(
                        "Replace function SIN with period",
                        arrayOf(
                            InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name),
                            InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN)
                        ),
                        arrayOf(InternToken(InternTokenType.PERIOD)),
                        CursorTokenPropertiesAfterModification.DO_NOT_MODIFY
                    ), arrayOf(
                        "Replace sensor OBJECT_COLOR with period",
                        arrayOf(InternToken(InternTokenType.SENSOR, Sensors.OBJECT_COLOR.name)),
                        arrayOf(InternToken(InternTokenType.PERIOD)),
                        CursorTokenPropertiesAfterModification.RIGHT
                    ), arrayOf(
                        "Replace sensor OBJECT_BRIGHTNESS with period",
                        arrayOf(
                            InternToken(
                                InternTokenType.SENSOR,
                                Sensors.OBJECT_BRIGHTNESS.name
                            )
                        ),
                        arrayOf(InternToken(InternTokenType.PERIOD)),
                        CursorTokenPropertiesAfterModification.RIGHT
                    ), arrayOf(
                        "Replace sensor OBJECT_BRIGHTNESS with function",
                        arrayOf(
                            InternToken(
                                InternTokenType.SENSOR,
                                Sensors.OBJECT_BRIGHTNESS.name
                            )
                        ),
                        arrayOf(InternToken(InternTokenType.FUNCTION_NAME)),
                        CursorTokenPropertiesAfterModification.RIGHT
                    )
                )
            )
        }
    }
}