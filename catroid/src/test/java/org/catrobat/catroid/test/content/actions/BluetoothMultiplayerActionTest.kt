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
import org.catrobat.catroid.content.actions.ChangeVariableAction
import org.catrobat.catroid.content.actions.SetVariableAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.Collections

@RunWith(Parameterized::class)
class BluetoothMultiplayerActionTest(
    private val name: String,
    private val userVariable: UserVariable,
    private val expectedValue: Int
) {

    private val formula: Formula = Formula(INITIAL_VALUE)
    private val testSprite: Sprite = Sprite("testSprite")
    private val testSequence: SequenceAction = SequenceAction()

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf(USERVARIABLE_NAME, UserVariable(USERVARIABLE_NAME, INITIAL_VALUE), 0),
            arrayOf(MULTIVARIABLE_NAME, UserVariable(MULTIVARIABLE_NAME, INITIAL_VALUE), 1)
        )

        private const val USERVARIABLE_NAME = "UserVariable"
        private const val MULTIVARIABLE_NAME = "MultiVariable"
        private const val INITIAL_VALUE = 0.0
        private const val VALUE = 2.0f
    }

    @Before
    fun setUp() {
        val context = MockUtil.mockContextForProject(dependencyModules)
        Project(context, "testProject").apply {
            if (expectedValue == 0) {
                addUserVariable(userVariable)
            } else {
                addMultiplayerVariable(userVariable)
            }
            projectManager.currentProject = this
        }
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testChangeVariable() {
        val action = testSprite.actionFactory.createChangeVariableAction(testSprite, testSequence, formula, userVariable)
        val actionSpy = spy(action as? ChangeVariableAction)
        actionSpy?.act(VALUE)
        verify(actionSpy, times(expectedValue))?.getMultiplayerDevice()
    }

    @Test
    fun testSetVariable() {
        val action = testSprite.actionFactory.createSetVariableAction(testSprite, testSequence, formula, userVariable)
        val actionSpy = spy(action as? SetVariableAction)
        actionSpy?.act(VALUE)
        verify(actionSpy, times(expectedValue))?.multiplayerDevice
    }
}
