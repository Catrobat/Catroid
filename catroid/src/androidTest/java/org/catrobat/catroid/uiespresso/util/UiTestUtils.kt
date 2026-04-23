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
package org.catrobat.catroid.uiespresso.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.uiespresso.util.matchers.SuperToastMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.koin.java.KoinJavaComponent.inject

class UiTestUtils private constructor() {
    companion object {
        private val projectManager by inject(ProjectManager::class.java)

        @JvmStatic
        val resources: Resources
            get() = ApplicationProvider.getApplicationContext<Context>().resources

        @JvmStatic
        fun getResourcesString(stringId: Int): String =
            ApplicationProvider.getApplicationContext<Context>().resources.getString(stringId)

        @JvmStatic
        fun getResourcesStringWithArgs(stringId: Int, vararg formatArgs: Any?): String {
            return ApplicationProvider.getApplicationContext<Context>().resources.getString(
                stringId,
                *formatArgs
            )
        }

        @JvmStatic
        fun getDefaultTestSprite(project: Project): Sprite =
            project.defaultScene.getSprite(TestUtils.DEFAULT_TEST_SPRITE_NAME)

        @JvmStatic
        fun getDefaultTestScript(project: Project): Script =
            project.defaultScene.getSprite(TestUtils.DEFAULT_TEST_SPRITE_NAME).getScript(TestUtils.DEFAULT_TEST_SCRIPT_INDEX)

        @JvmStatic
        fun assertCurrentActivityIsInstanceOf(activityClass: Class<*>?) {
            val currentActivity = arrayOf<Activity?>(null)
            getInstrumentation().runOnMainSync {
                val resumedActivities =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED
                    )
                if (resumedActivities.iterator().hasNext()) {
                    currentActivity[0] = resumedActivities.iterator().next()
                }
            }
            assertThat(currentActivity[0], CoreMatchers.instanceOf(activityClass))
        }

        @JvmStatic
        fun createDefaultTestProject(projectName: String?): Project {
            val project = Project(ApplicationProvider.getApplicationContext(), projectName)
            val sprite = Sprite(TestUtils.DEFAULT_TEST_SPRITE_NAME)
            val script: Script = StartScript()
            sprite.addScript(script)
            project.defaultScene.addSprite(sprite)
            projectManager.currentProject = project
            projectManager.currentSprite = sprite
            return project
        }

        @JvmStatic
        fun createProjectAndGetStartScript(projectName: String?): Script {
            val project = Project(ApplicationProvider.getApplicationContext(), projectName)
            val sprite = Sprite(TestUtils.DEFAULT_TEST_SPRITE_NAME)
            val script: Script = StartScript()
            sprite.addScript(script)
            project.defaultScene.addSprite(sprite)
            projectManager.currentProject = project
            projectManager.currentSprite = sprite
            projectManager.currentlyEditedScene = project.defaultScene
            return script
        }

        @JvmStatic
        fun createProjectWithCustomScript(projectName: String?, script: Script): Project {
            val project = Project(ApplicationProvider.getApplicationContext(), projectName)
            val sprite = Sprite(TestUtils.DEFAULT_TEST_SPRITE_NAME)
            sprite.addScript(script)
            project.defaultScene.addSprite(sprite)
            projectManager.currentProject = project
            projectManager.currentSprite = sprite
            projectManager.currentlyEditedScene = project.defaultScene
            return project
        }

        @JvmStatic
        fun createProjectWithOutDefaultScript(projectName: String?): Project {
            val project = Project(ApplicationProvider.getApplicationContext(), projectName)
            val sprite = Sprite(TestUtils.DEFAULT_TEST_SPRITE_NAME)
            project.defaultScene.addSprite(sprite)
            UiTestCatroidApplication.projectManager.currentProject = project
            UiTestCatroidApplication.projectManager.currentSprite = sprite
            return project
        }

        @JvmStatic
        fun onToast(viewMatcher: Matcher<View?>?): ViewInteraction =
            onView(viewMatcher).inRoot(SuperToastMatchers.isToast())

        @JvmStatic
        fun openActionBarMenu() {
            try {
                Thread.sleep(100)
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                Log.e(UiTestUtils::class.java.name, e.message!!)
            }
        }

        @JvmStatic
        fun openSpriteActionMenu(spriteName: String, isGroupSprite: Boolean) {
            if (isGroupSprite) {
                onView(
                    allOf(
                        withId(R.id.settings_button),
                        isDescendantOfA(withId(R.id.view_holder_sprite_group)),
                        hasSibling(withText(spriteName))
                    )
                ).perform(click())
            } else {
                onView(
                    allOf(
                        withId(R.id.settings_button),
                        isDescendantOfA(withId(R.id.view_holder_with_checkbox)),
                        hasSibling(withChild(withText(spriteName)))
                    )
                ).perform(click())
            }
        }

        @JvmStatic
        fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
            return object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description) {
                    description.appendText("Child at position $position in parent ")
                    parentMatcher.describeTo(description)
                }

                override fun matchesSafely(view: View): Boolean {
                    val parent = view.parent
                    return parent is ViewGroup && parentMatcher.matches(parent) &&
                        view == parent.getChildAt(position)
                }
            }
        }

        @JvmStatic
        fun uncheckPlaceVisually(activity: Activity) {
            val switchCompat = onView(
                allOf(
                    withId(R.id.place_visually_sprite_switch), withText("Place visually"),
                    childAtPosition(
                        childAtPosition(
                            ViewMatchers.withClassName(Matchers.`is`("android.widget.ScrollView")),
                            0
                        ),
                        1
                    )
                )
            )

            if (activity.window.currentFocus?.findViewById<SwitchCompat>(R.id.place_visually_sprite_switch)?.isChecked == true) {
                switchCompat.perform(ViewActions.scrollTo(), click())
            }
        }
    }
}
