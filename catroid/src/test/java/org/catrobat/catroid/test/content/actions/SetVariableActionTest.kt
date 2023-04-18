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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetVariableActionTest {
    private val TEST_USERVARIABLE = "testUservariable"
    private val SET_VARIABLE_VALUE = 17.0
    private val INITIALIZED_VALUE = 0.0
    private lateinit var testSprite: Sprite
    private lateinit var project: Project
    private lateinit var userVariable: UserVariable

    @Before
    @Throws(Exception::class)
    fun setUp() {
        testSprite = Sprite("testSprite")
        project = Project(MockUtil.mockContextForProject(), "testProject")
        ProjectManager.getInstance().currentProject = project
        userVariable = UserVariable(TEST_USERVARIABLE)
        project.addUserVariable(userVariable)
    }
    @Test
    fun testSetVariableWithInvalidUserVariable() {
        testSprite.actionFactory.createSetVariableAction(
            testSprite,
            SequenceAction(),
            Formula(SET_VARIABLE_VALUE),
            null
        ).act(1f)
        assertEquals(INITIALIZED_VALUE, userVariable.value)
    }

    @Test
    fun testSetVariableWithStringFormula() {
        val myString = "myString"
        testSprite.actionFactory.createSetVariableAction(
            testSprite,
            SequenceAction(),
            Formula(myString),
            userVariable
        ).act(1f)
        assertEquals(myString, userVariable.value as String)
    }

    @Test
    fun testNullFormula() {
        testSprite.actionFactory.createSetVariableAction(
            testSprite,
            SequenceAction(),
            null,
            userVariable
        ).act(1f)
        assertEquals(0.0, userVariable.value)
    }
}
