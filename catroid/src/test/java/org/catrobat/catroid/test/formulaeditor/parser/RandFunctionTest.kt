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

package org.catrobat.catroid.test.formulaeditor.parser

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil
import org.junit.Before
import org.junit.Test

class RandFunctionTest {
    private var sprite: Sprite? = null
    private var scope: Scope? = null
    private val firstParameterValue: String = "0"
    private val secondParameterValue: String = "0"
    private val nonNumericalString: String = "nonNumericalString"

    @Before
    fun setUp() {
        val project = Project(
            MockUtil.mockContextForProject(),
            "Project"
        )
        sprite = Sprite("sprite")
        scope = Scope(project, sprite!!, SequenceAction())
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testNumberParameters() {
        val firstParameter =
            listOf(InternToken(InternTokenType.NUMBER, firstParameterValue))
        val secondParameter =
            listOf(InternToken(InternTokenType.NUMBER, secondParameterValue))

        FormulaEditorTestUtil.testDoubleParameterFunction(
            Functions.RAND,
            firstParameter,
            secondParameter,
            0.0,
            scope
        )
    }

    @Test
    fun testNotNumericalStringParameters() {
        val firstParameter = listOf(
            InternToken(
                InternTokenType.STRING,
                this.nonNumericalString
            )
        )
        val secondParameter = listOf(
            InternToken(
                InternTokenType.STRING,
                this.nonNumericalString

            )
        )
        FormulaEditorTestUtil.testDoubleParameterFunction(
            Functions.RAND,
            firstParameter,
            secondParameter,
            0.0,
            scope
        )
    }
}
