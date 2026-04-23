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
package org.catrobat.catroid.uiespresso.ui.fragment

import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.setLanguageSharedPreference
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@Category(AppUi::class, Smoke::class)
@RunWith(AndroidJUnit4::class)
class RenameSpriteTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    private val firstSpriteName = "firstSprite"
    private val secondSpriteName = "secondSprite"
    private val groupSpriteName = "groupSprite"

    private val projectManager by inject(ProjectManager::class.java)

    @Before
    fun setUp() {
        setLanguageSharedPreference(ApplicationProvider.getApplicationContext(), "en")
        createProject(RenameSpriteTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        setLanguageSharedPreference(ApplicationProvider.getApplicationContext(), "en")
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(RenameSpriteTest::class.java.simpleName)
    }

    @Test
    fun renameSpriteSwitchCaseDialogTest() {
        val newSpriteName = "SeConDspRite"
        renameViaActionBarMenu(secondSpriteName, newSpriteName, 2)
    }

    @Test
    fun renameSpriteDialogTest() {
        val modifiedFirstSprite = "modifiedFirstSprite"
        val modifiedGroupSprite = "modifiedGroupSprite"
        renameViaSpriteActionMenu(firstSpriteName, modifiedFirstSprite, isGroupSprite = false)
        renameViaActionBarMenu(modifiedFirstSprite, firstSpriteName, 1)
        renameViaSpriteActionMenu(groupSpriteName, modifiedGroupSprite, isGroupSprite = true)
    }

    @Test
    fun cancelRenameSpriteDialogTest() {
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.rename))
            .perform(click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .check(matches(not(isDisplayed())))
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(2)
            .perform(click())
        onView(withText(R.string.rename_sprite_dialog))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        closeSoftKeyboard()
        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .perform(click())
        onView(withText(secondSpriteName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invalidInputRenameSoundTest() {
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.rename))
            .perform(click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .check(matches(not(isDisplayed())))
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1)
            .perform(click())
        onView(withText(R.string.rename_sprite_dialog))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        val emptyInput = ""
        val spacesOnlyInput = "   "
        val newSpriteName = "newSpriteName"
        onView(allOf(withText(firstSpriteName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(emptyInput))
        closeSoftKeyboard()
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
        onView(allOf(withText(emptyInput), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(spacesOnlyInput))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
        onView(allOf(withText(spacesOnlyInput), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(secondSpriteName))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
        onView(allOf(withText(secondSpriteName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(newSpriteName))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .check(matches(allOf(isDisplayed(), isEnabled())))
    }

    @Test
    fun renameSingleSpriteTest() {
        UiTestUtils.openSpriteActionMenu(groupSpriteName, isGroupSprite = true)
        onView(withText(R.string.delete))
            .perform(click())
        UiTestUtils.openSpriteActionMenu(firstSpriteName, isGroupSprite = false)
        onView(withText(R.string.delete))
            .perform(click())
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.rename))
            .perform(click())
        onView(withText(R.string.rename_sprite_dialog))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    private fun renameViaActionBarMenu(spriteToRename: String, newName: String, position: Int) {
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.rename))
            .perform(click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .check(matches(not(isDisplayed())))
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(position)
            .perform(click())
        onView(withText(R.string.rename_sprite_dialog))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(allOf(withText(spriteToRename), isDisplayed()))
            .perform(replaceText(newName))
        closeSoftKeyboard()
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(click())
        onView(withText(newName))
            .check(matches(isDisplayed()))
        onView(withText(spriteToRename))
            .check(doesNotExist())
    }

    private fun renameViaSpriteActionMenu(
        spriteToRename: String,
        newName: String,
        isGroupSprite: Boolean
    ) {
        UiTestUtils.openSpriteActionMenu(spriteToRename, isGroupSprite)
        onView(withText(R.string.rename))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(allOf(withText(spriteToRename), isDisplayed()))
            .perform(replaceText(newName))
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(click())
        onView(withText(newName))
            .check(matches(isDisplayed()))
        onView(withText(spriteToRename))
            .check(doesNotExist())
    }

    private fun createProject(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.defaultScene.addSprite(Sprite(firstSpriteName))
        project.defaultScene.addSprite(Sprite(secondSpriteName))
        project.defaultScene.addSprite(GroupSprite(groupSpriteName))
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
    }
}
