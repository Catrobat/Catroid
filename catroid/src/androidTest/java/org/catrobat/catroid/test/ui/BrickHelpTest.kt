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

package org.catrobat.catroid.test.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.childAtPosition
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException

class BrickHelpTest {
    var projectName = "helpTestProject"
    private lateinit var project: Project
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject(projectName)
        Intents.init()
        baseActivityTestRule.launchActivity(Intent())
    }

    @Test
    fun testHelpBrick() {
        val expectedIntent = allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(
                "https://wiki.catrobat" +
                    ".org/bin/view/Documentation/BrickDocumentation/WhenStartedBrick"
            )
        )
        intending(
            expectedIntent
        ).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        onView(withId(R.id.button_add)).perform(click())

        onView(
            withSubstring(
                projectManager.applicationContext.getString(
                    R.string.category_event
                )
            )
        ).perform(click())

        onView(withId(R.id.help)).perform(click())

        onView(withId(R.id.help_brick_fragment_list)).check(matches(isDisplayed()))

        val linearLayout2 = onData(Matchers.anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(
                        withId(R.id.help_brick_fragment_list),
                        0
                    )
                )
            )
            .atPosition(0)
        linearLayout2.perform(click())
        intended(expectedIntent)
        onView(withId(R.id.help_brick_fragment_list)).check(matches(isDisplayed()))

        onView(
            withSubstring(
                projectManager.applicationContext.getString(
                    R.string.help
                )
            )
        ).check(matches(isDisplayed()))
    }

    fun createProject(projectName: String?) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.sceneList.clear()
        val sceneName1 = "scene1"
        val scene1 = Scene("scene1", project)
        val sprite1 = Sprite("testSprite1")
        project.addScene(scene1)
        project.getSceneByName(sceneName1).addSprite(sprite1)
        projectManager.currentProject = project
        projectManager.setCurrentSceneAndSprite(sceneName1, sprite1.name)
        projectManager.currentlyEditedScene = scene1
    }

    @After
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.finishActivity()
        try {
            TestUtils.deleteProjects(projectName)
        } catch (e: IOException) {
            Log.d(javaClass.simpleName, Log.getStackTraceString(e))
        }
    }
}
