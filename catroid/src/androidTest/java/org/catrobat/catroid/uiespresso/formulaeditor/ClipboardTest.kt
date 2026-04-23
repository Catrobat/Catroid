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
package org.catrobat.catroid.uiespresso.formulaeditor

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class ClipboardTest {

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @JvmField
    @Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun copyAndPasteTest() {
        onView(withId(R.id.brick_set_variable_edit_text)).perform(click())
        FormulaEditorWrapper.onFormulaEditor().performEnterNumber(12_345)
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.formula_editor_keyboard_copy)).perform(click())
        FormulaEditorWrapper.onFormulaEditor().performBackspace()
        onView(withId(R.id.formula_editor_edit_field)).perform(longClick())
        onView(withId(R.id.formula_editor_keyboard_paste)).perform(click())
        FormulaEditorWrapper.onFormulaEditor().checkShows("12345")
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(PROJECT_NAME)
    }

    private fun createProject(): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        val setVariableBrick = SetVariableBrick()
        val userVariable = UserVariable("testVariable")
        project.addUserVariable(userVariable)
        setVariableBrick.userVariable = userVariable
        script.addBrick(setVariableBrick)
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
        return project
    }

    companion object {
        private const val PROJECT_NAME = "ClipboardTest"
    }
}
