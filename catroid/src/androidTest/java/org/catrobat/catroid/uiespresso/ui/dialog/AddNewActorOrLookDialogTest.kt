/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.selectTabAtPosition
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
        val projectName = "newProject"
        currentProject = UiTestUtils.createDefaultTestProject(projectName)
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
        onView(withId(R.id.button_add)).perform(ViewActions.click())
        onView(withText(R.string.new_sprite_dialog_title)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_paintroid)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_camera)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_gallery)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_media_library)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_object_media_library)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_backpack)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_import_sprite_from_local)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_empty_object)).check(matches(isDisplayed()))
    }

    @Test
    fun addLookDialogTest() {
        onView(withText(currentProject!!.defaultScene.spriteList[0].toString()))
            .perform(ViewActions.click())
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))
        onView(withId(R.id.button_add)).perform(ViewActions.click())
        onView(withText(R.string.new_look_dialog_title)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_paintroid)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_camera)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_gallery)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_media_library)).check(matches(isDisplayed()))
    }
}
