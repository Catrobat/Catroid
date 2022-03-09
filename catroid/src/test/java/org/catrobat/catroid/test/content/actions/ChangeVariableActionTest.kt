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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import java.util.Collections

@RunWith(Parameterized::class)
class ChangeVariableActionTest(
    private val name: String,
    private val formula: Formula?,
    private val userVariable: UserVariable?,
    private val expectedValue: Double?
) {
    private lateinit var testSprite: Sprite
    private lateinit var testSequence: SequenceAction

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("INVALID_USER_VARIABLE", Formula(CHANGE_VARIABLE_VALUE), null, null),
            arrayOf("NUMBER_NEGATIVE", Formula(CHANGE_VARIABLE_VALUE), UserVariable(TEST_USERVARIABLE, -1.0), -1.0 + CHANGE_VARIABLE_VALUE),
            arrayOf("NUMBER_ZERO", Formula(CHANGE_VARIABLE_VALUE), UserVariable(TEST_USERVARIABLE), CHANGE_VARIABLE_VALUE),
            arrayOf("NUMBER_POSITIVE", Formula(CHANGE_VARIABLE_VALUE), UserVariable(TEST_USERVARIABLE, 1.0), 1.0 + CHANGE_VARIABLE_VALUE),
            arrayOf("NUMERICAL_STRING", Formula(CHANGE_VARIABLE_VALUE.toString()), UserVariable(TEST_USERVARIABLE), CHANGE_VARIABLE_VALUE),
            arrayOf("NON_NUMERICAL_STRING", Formula("changeVariable"), UserVariable(TEST_USERVARIABLE), 0.0),
            arrayOf("NULL", null, UserVariable(TEST_USERVARIABLE), 0.0),
            arrayOf("NAN", Formula(Double.NaN), UserVariable(TEST_USERVARIABLE), 0.0)
        )

        private const val TEST_USERVARIABLE = "testUservariable"
        private const val CHANGE_VARIABLE_VALUE = 3.14
    }

    @Before
    fun setUp() {
        testSprite = Sprite("testSprite")
        testSequence = SequenceAction()
        val context = MockUtil.mockContextForProject(dependencyModules)
        Project(context, "testProject").apply {
            addUserVariable(userVariable)
            projectManager.currentProject = this
        }
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testChangeUserVariableAndCheckResult() {
        Assert.assertTrue(testSprite.actionFactory.createChangeVariableAction(testSprite, testSequence,
            formula,
            userVariable).act(1f))
        Assert.assertEquals(expectedValue, userVariable?.value)
    }
}
