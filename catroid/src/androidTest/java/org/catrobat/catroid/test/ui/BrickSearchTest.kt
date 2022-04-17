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

package org.catrobat.catroid.test.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.runner.Flaky
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class BrickSearchTest {
    var projectName = "searchTestProject"

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject(projectName)
        baseActivityTestRule.launchActivity(Intent())
    }

    @Test
    fun testSearchBrickParams() {
        val arguments = arrayOf("When scene starts", "move", "BROADCAST")
        val bricks = arrayOf(
            WhenStartedBrick::class.java,
            MoveNStepsBrick::class.java,
            BroadcastBrick::class.java
        )
        ensureKeyboardIsClosed()
        Espresso.onView(withId(R.id.button_add)).perform(click())
        Espresso.onView(withId(R.id.search)).perform(click())
        for (index in arguments.indices) {
            Espresso.onView(withId(R.id.search_src_text)).perform(click())
            val viewMatcher = Espresso.onView(withId(R.id.search_src_text)).perform(
                replaceText(arguments[index])
            )
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            viewMatcher.perform(pressKey(KeyEvent.KEYCODE_ENTER))
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            Espresso.onData(Matchers.allOf(Matchers.`is`(Matchers.instanceOf
            (bricks[index] as Class<*>?))))
                .inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
                .atPosition(0)
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testSearchIfBrick() {
        ensureKeyboardIsClosed()
        Espresso.onView(withId(R.id.button_add)).perform(click())
        Espresso.onView(withId(R.id.search)).perform(click())
        Espresso.onView(withId(R.id.search_src_text)).perform(
            replaceText("if")
        ).perform(pressKey(KeyEvent.KEYCODE_ENTER))
        val searchAutoComplete = Espresso.onView(
            Matchers.allOf(
                withId(R.id.search_src_text),
                ViewMatchers.withText("if"), childAtPosition(
                    Matchers.allOf(
                        withId(R.id.search_plate),
                        childAtPosition(withId(R.id.search_edit_frame), 1)
                    ), 0
                ), isDisplayed()
            )
        )
        searchAutoComplete.perform(ViewActions.pressImeActionButton())
        val linearLayout = Espresso.onData(Matchers.anything()).inAdapterView(
            Matchers.allOf(
                withId(android.R.id.list),
                childAtPosition(withId(R.id.fragment_brick_search), 0)
            )
        ).atPosition(0)
        linearLayout.perform(click())
        Assert.assertFalse(isKeyboardVisible())
    }

    @Test
    fun testCloseKeyboardAfterSearching() {
        ensureKeyboardIsClosed()
        Espresso.onView(withId(R.id.button_add)).perform(click())
        Espresso.onView(withId(R.id.search)).perform(click())
        Espresso.onView(ViewMatchers.isRoot()).perform(CustomActions.wait(2000))
        Assert.assertTrue(isKeyboardVisible())
        Espresso.onView(withId(R.id.search_src_text)).perform(
            replaceText("if")
        ).perform(pressKey(KeyEvent.KEYCODE_ENTER))
        Espresso.onView(ViewMatchers.isRoot()).perform(CustomActions.wait(2000))
        Assert.assertFalse(isKeyboardVisible())
    }

    @Test
    fun testCategorySearch() {
        ensureKeyboardIsClosed()
        Espresso.onView(withId(R.id.button_add)).perform(click())
        Espresso.onView(withId(R.id.search)).perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withId(R.id.search_src_text)).perform(
            replaceText("When")
        ).perform(pressKey(KeyEvent.KEYCODE_ENTER))
        InstrumentationRegistry.getInstrumentation().waitForIdleSync() // previous action takes
        // long to execute
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withText("When scene starts"),
                isDisplayed()
            )
        ).check(matches(isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withText("When background changes to"),
                isDisplayed()
            )
        ).check(matches(isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withContentDescription(Matchers.containsString("Navigate up")),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.toolbar),
                        childAtPosition(withId(R.id.activity_sprite), 0)
                    ), 1
                ),
                isDisplayed()
            )
        ).perform(click())
        Espresso.onData(Matchers.anything())
            .inAdapterView(
                Matchers.allOf(
                    withId(R.id.brick_category_list),
                    childAtPosition(withId(R.id.fragment_container), 1)
                )
            ).atPosition(5).perform(click())
        Espresso.onView(withId(R.id.search)).perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withId(R.id.search_src_text))
            .perform(replaceText("When"))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withText("When scene starts")
            )
        ).check(doesNotExist())
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withText("When background changes to"),
                isDisplayed()
            )
        )
    }

    fun ensureKeyboardIsClosed() {
        if (isKeyboardVisible()) {
            hideKeyboard()
        }
    }

    fun hideKeyboard() {
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
    }
    @Test
    @Flaky
    fun testProgressiveSearch() {
        val arguments = arrayOf("W", "h", "e", "n")
        val brick = WhenStartedBrick::class.java
        Espresso.onView(withId(R.id.button_add)).perform(click())
        Espresso.onView(withId(R.id.search)).perform(click())
        for (index in arguments.indices) {
            Espresso.onView(withId(R.id.search_src_text)).perform(ViewActions.typeText(arguments[index]))
            Espresso.onView(ViewMatchers.isRoot()).perform(CustomActions.wait(2000))
            Espresso.onData(Matchers.allOf(Matchers.`is`(Matchers.instanceOf(brick))))
                .inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
                .atPosition(0)
                .check(matches(isDisplayed()))
        }
    }

    private fun isKeyboardVisible(): Boolean {
        return try {
            val manager = ApplicationProvider.getApplicationContext<Context>()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val windowHeightMethod =
                InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
            val height = windowHeightMethod.invoke(manager) as Int
            height > 0
        } catch (e: Exception) {
            Log.d(e.toString(), "keyboard is not visible")
            false
        }
    }

    fun createProject(projectName: String?) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.sceneList.clear()
        val sceneName1 = "scene1"
        val scene1 = Scene("scene1", project)
        val sprite1 = Sprite("testSprite1")
        project.addScene(scene1)
        project.getSceneByName(sceneName1).addSprite(sprite1)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().setCurrentSceneAndSprite(sceneName1, sprite1.name)
        ProjectManager.getInstance().currentlyEditedScene = scene1
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        try {
            TestUtils.deleteProjects(projectName)
        } catch (e: IOException) {
            Log.d(javaClass.simpleName, Log.getStackTraceString(e))
        }
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent) &&
                    view == parent.getChildAt(position)
            }
        }
    }
}
