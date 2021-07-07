/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.ui.fragment

import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper.onActionMode
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@Category(AppUi::class, Smoke::class)
@RunWith(AndroidJUnit4::class)
class RenameProjectTest {

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )
    private val oldProjectName = "oldProjectName"
    private val newProjectName = "newProjectName"
    private val secondProjectName = "secondProjectName"

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(oldProjectName)
        createProject(secondProjectName)
        baseActivityTestRule.launchActivity(null)
    }

    @Test
    fun renameProjectTest() {
        UiTestUtils.openActionBar()
        onView(withText(R.string.rename))
            .perform(click())
        onRecyclerView()
            .atPosition(0)
            .perform(click())
        onView(withText(R.string.rename_project))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(allOf(withText(oldProjectName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(newProjectName))
        closeSoftKeyboard()
        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(click())
        onView(withText(newProjectName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun cancelRenameProjectTest() {
        UiTestUtils.openActionBar()
        onView(withText(R.string.rename))
            .perform(click())
        onRecyclerView()
            .atPosition(0)
            .perform(click())
        onView(withText(R.string.rename_project))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        closeSoftKeyboard()
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .perform(click())
        onView(withText(oldProjectName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invalidInputRenameProjectTest() {
        UiTestUtils.openActionBar()
        onView(withText(R.string.rename))
            .perform(click())
        onRecyclerView()
            .atPosition(0)
            .perform(click())
        onView(withText(R.string.rename_project))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        val emptyInput = ""
        val spacesOnlyInput = "   "
        onView(allOf(withText(oldProjectName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(emptyInput))
        closeSoftKeyboard()
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
        onView(allOf(withText(emptyInput), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(spacesOnlyInput))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
        onView(allOf(withText(spacesOnlyInput), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(secondProjectName))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
        onView(allOf(withText(secondProjectName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(newProjectName))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), isEnabled())))
    }

    @Test
    fun renameSingleProjectTest() {
        UiTestUtils.openActionBar()
        onView(withText(R.string.delete))
            .perform(click())
        onRecyclerView()
            .atPosition(1)
            .performCheckItem()
        onActionMode()
            .performConfirm()
        onView(withText(R.string.delete))
            .perform(click())
        UiTestUtils.openActionBar()
        onView(withText(R.string.rename))
            .perform(click())
        onView(withText(R.string.rename_project))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    private fun createProject(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("firstSprite")
        val script: Script = StartScript()
        script.addBrick(SetXBrick(Formula(BrickValues.X_POSITION)))
        script.addBrick(SetXBrick(Formula(BrickValues.X_POSITION)))
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        XstreamSerializer.getInstance().saveProject(project)
    }
}
