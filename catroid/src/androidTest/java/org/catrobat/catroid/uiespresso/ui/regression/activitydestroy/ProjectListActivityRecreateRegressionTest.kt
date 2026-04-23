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
package org.catrobat.catroid.uiespresso.ui.regression.activitydestroy

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.BrickValues.X_POSITION
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.rules.FlakyTestRule
import org.catrobat.catroid.runner.Flaky
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Cat.Quarantine
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@Category(AppUi::class, Smoke::class, Quarantine::class)
@RunWith(AndroidJUnit4::class)
class ProjectListActivityRecreateRegressionTest {
    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )

    @get:Rule
    var flakyTestRule = FlakyTestRule()
    private val projectName = "testProject"

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity(null)
    }

    @Flaky
    @Test
    fun testActivityRecreateRenameProjectDialog() {
        openActionBarOverflowOrOptionsMenu(getApplicationContext())

        onView(withText(R.string.rename)).perform(click())

        onView(withText(R.string.rename_project))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        getInstrumentation().runOnMainSync { baseActivityTestRule.activity.recreate() }
        getInstrumentation().waitForIdleSync()
    }

    @Flaky
    @Test
    fun testActivityRecreateNewProjectDialog() {
        onRecyclerView().atPosition(0).onChildView(R.id.title_view)
            .check(matches(withText(projectName)))

        onView(withId(R.id.button_add))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(typeText("TestProject0815"), closeSoftKeyboard())

        onView(withId(R.id.confirm))
            .perform(click())

        getInstrumentation().runOnMainSync { baseActivityTestRule.activity.recreate() }
        getInstrumentation().waitForIdleSync()
    }

    private fun createProject() {
        val project = Project(getApplicationContext(), projectName)
        val sprite = Sprite("firstSprite")
        val script: Script = StartScript()

        script.addBrick(SetXBrick(Formula(X_POSITION)))
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene

        XstreamSerializer.getInstance().saveProject(project)
    }
}
