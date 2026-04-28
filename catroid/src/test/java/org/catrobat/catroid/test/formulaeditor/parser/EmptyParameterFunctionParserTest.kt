/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import org.catrobat.catroid.formulaeditor.Functions.INDEX_CURRENT_TOUCH
import org.catrobat.catroid.formulaeditor.Functions.MULTI_FINGER_X
import org.catrobat.catroid.formulaeditor.Functions.MULTI_FINGER_Y
import org.catrobat.catroid.formulaeditor.Functions.RASPIDIGITAL
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_SIZE
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_X
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_Y
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.testSingleParameterFunction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EmptyParameterFunctionParserTest(
    val name: String,
    private val function: Functions,
    private val expectedValue: Any
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf("RASPIDIGITAL", RASPIDIGITAL, 0.0),
                arrayOf("MULTI_FINGER_X", MULTI_FINGER_X, 0.0),
                arrayOf("MULTI_FINGER_Y", MULTI_FINGER_Y, 0.0),
                arrayOf("INDEX_CURRENT_TOUCH", INDEX_CURRENT_TOUCH, 0.0),
                arrayOf("TEXT_BLOCK_X", TEXT_BLOCK_X, 0.0),
                arrayOf("TEXT_BLOCK_Y", TEXT_BLOCK_Y, 0.0),
                arrayOf("TEXT_BLOCK_SIZE", TEXT_BLOCK_SIZE, 0.0)
            )
        }
    }

    private var sprite: Sprite? = null
    private var scope: Scope? = null

    @Before
    fun setUp() {
        val project = Project(
            MockUtil.mockContextForProject(), "Project"
        )
        sprite = Sprite("testSprite")
        scope = Scope(project, sprite!!, SequenceAction())
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testEmptyParameter() {
        val internTokenList = listOf(InternToken(InternTokenType.STRING, ""))
        testSingleParameterFunction(function, internTokenList, expectedValue, null)
    }
}
