/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteProjectTest {
    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )

    private val projectToDelete = "firstProject"
    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(projectToDelete)
        createProject("secondProject")
        baseActivityTestRule.launchActivity(null)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteProjectTest() {
        openActionBarOverflowOrOptionsMenu(targetContext)
        onView(withText(R.string.delete))
            .perform(click())
        onRecyclerView()
            .atPosition(0)
            .performCheckItem()
        onView(withId(R.id.confirm))
            .perform(click())
        onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_projects, 1)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withText(R.string.dialog_confirm_delete))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .perform(click())
        onView(withText(projectToDelete))
            .check(doesNotExist())
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun cancelDeleteProjectTest() {
        openActionBarOverflowOrOptionsMenu(targetContext)
        onView(withText(R.string.delete))
            .perform(click())
        onRecyclerView()
            .atPosition(1)
            .performCheckItem()
        onView(withId(R.id.confirm))
            .perform(click())
        onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_projects, 1)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withText(R.string.dialog_confirm_delete))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .perform(click())
        onView(withText(projectToDelete))
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
