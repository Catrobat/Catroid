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

package org.catrobat.catroid.ui

import android.os.Build
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.FinishStageBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@LargeTest
@RunWith(AndroidJUnit4::class)
class AssertEqualsBrickTest {
    companion object {
        private const val PROJECT_NAME = "project"
        private const val SPRITE_NAME = "sprite"
    }

    val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private lateinit var script: Script
    private lateinit var project: Project

    @get: Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity(null)
    }

    @Test
    fun assertEqualsBrickTest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            onView(withId(R.id.brick_assert_actual))
                .perform(click())

            onView(withText(R.string.formula_editor_logic))
                .perform(click())

            onView(withText("false"))
                .perform(click())

            onView(withId(R.id.brick_assert_expected))
                .perform(handleConstraints(click(), isDisplayingAtLeast(50)))

            onView(withText(R.string.formula_editor_logic))
                .perform(click())

            onView(withText("true"))
                .perform(click())

            pressBack()

            onView(withId(R.id.button_play))
                .perform(click())

            UiTestUtils.onToast(withText(containsString("on sprite")))
                .check(matches(isDisplayed()))
        }
    }

    private fun handleConstraints(action: ViewAction, constraints: Matcher<View>): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return constraints
            }

            override fun getDescription(): String {
                return action.description
            }

            override fun perform(uiController: UiController?, view: View?) {
                action.perform(uiController, view)
            }
        }
    }

    fun createProject() {
        val sprite = Sprite(SPRITE_NAME)
        script = StartScript()
        script.addBrick(AssertEqualsBrick())
        script.addBrick(FinishStageBrick())

        sprite.addScript(script)
        project = Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        XstreamSerializer.getInstance().saveProject(project)
    }
}
