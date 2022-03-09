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

package org.catrobat.catroid.uiespresso.ui.dialog

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.koin.myModules
import org.catrobat.catroid.koin.startWithContext
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.actions.selectTabAtPosition
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class AddNewActorOrLookDialogTest {
    private var currentProject: Project? = null

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        startWithContext(baseActivityTestRule.activity.applicationContext, myModules)
        val projectName = "newProject"
        currentProject = createProject(projectName)
        currentProject!!.defaultScene.addSprite(Sprite("Sprite1"))
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(currentProject!!.name)
    }

    @Test
    fun addActorOrObjectDialogTest() {
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.new_sprite_dialog_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_paintroid))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_camera))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_gallery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_media_library))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_object_library))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_backpack))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_from_local))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun addLookDialogTest() {
        Espresso.onView(ViewMatchers.withText(currentProject!!.defaultScene.spriteList[0].toString()))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.new_look_dialog_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_paintroid))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_camera))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_gallery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_media_library))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun createProject(projectName: String?): Project? {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val project = Project(context, projectName)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        val setVariableBrick = SetVariableBrick()
        val userVariable = UserVariable("Global1")
        project.addUserVariable(userVariable)
        setVariableBrick.userVariable = userVariable
        script.addBrick(setVariableBrick)
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        val projectManager = inject(ProjectManager::class.java)
        projectManager.value.currentProject = project
        projectManager.value.currentSprite = sprite
        return project
    }
}
