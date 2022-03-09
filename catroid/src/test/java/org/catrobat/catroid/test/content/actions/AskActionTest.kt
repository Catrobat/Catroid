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
import com.badlogic.gdx.utils.GdxNativesLoader
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.AskAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.Collections

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class)
class AskActionTest {
    private lateinit var testSprite: Sprite
    private lateinit var testSequence: SequenceAction
    private lateinit var userVariableForAnswer: UserVariable

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        testSprite = Sprite("testSprite")
        testSequence = SequenceAction()
        userVariableForAnswer = UserVariable(TEST_USERVARIABLE)
        PowerMockito.mockStatic(GdxNativesLoader::class.java)

        val context = MockUtil.mockContextForProject(dependencyModules)
        val project = Project(context, "Project")
        projectManager.currentProject = project
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testAskAndCheckAnswer() {
        val action = testSprite.actionFactory.createAskAction(
            testSprite,
            testSequence,
            Formula(ASK_QUESTION),
            userVariableForAnswer
        ) as AskAction
        action.act(1f)
        action.setAnswerText(ASK_ANSWER)
        Assert.assertEquals(
            ASK_ANSWER,
            userVariableForAnswer.value.toString()
        )
    }

    companion object {
        private const val TEST_USERVARIABLE = "testUservariable"
        private const val ASK_QUESTION = "What's your name"
        private const val ASK_ANSWER = "Catrobat Pocket Cat"
    }
}
