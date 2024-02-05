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

package org.catrobat.catroid.uiespresso.ui.fragment

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.GroupItemSprite
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter.createImageFileFromResourcesInDirectory
import org.catrobat.catroid.io.ResourceImporter.createSoundFileFromResourcesInDirectory
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.openActionBarMenu
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.openSpriteActionMenu
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import java.io.File

@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(AndroidJUnit4::class)
class SpriteListUndoTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    private val firstSpriteName = "firstSprite"
    private val secondSpriteName = "secondSprite"
    private val groupSpriteName = "groupSprite"
    private val groupItemSpriteName = "groupItemSprite"

    private lateinit var lookDirectory : File
    private lateinit var soundDirectory : File

    private val projectManager by inject(ProjectManager::class.java)

    @Before
    fun setUp() {
        createProject(SpriteListUndoTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun deleteAndUndoMultipleSpritesAtOnceTest() {
        openActionBarMenu()
        onView(withText(R.string.delete))
            .perform(click())

        onRecyclerView().atPosition(1)
            .performCheckItemClick()

        onRecyclerView().atPosition(2)
            .performCheckItemClick()

        onView(withId(R.id.confirm))
            .perform(click())

        onRecyclerView().checkHasNumberOfItems(3)

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isClickable(), 3000))
            .perform(click())

        onRecyclerView().checkHasNumberOfItems(5)
    }

    @Test
    fun copyAndUndoMultipleSpritesAtOnceTest() {
        openActionBarMenu()
        onView(withText(R.string.copy))
            .perform(click())

        onRecyclerView().atPosition(1)
            .performCheckItemClick()

        onRecyclerView().atPosition(2)
            .performCheckItemClick()

        onView(withId(R.id.confirm))
            .perform(click())

        onRecyclerView().checkHasNumberOfItems(7)

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isClickable(), 3000))
            .perform(click())

        onRecyclerView().checkHasNumberOfItems(5)
    }

    @Test
    fun undoOfConsecutiveActions() {
        onView(withText(firstSpriteName)).check(matches(isDisplayed()))

        openSpriteActionMenu(firstSpriteName, isGroupSprite = false)

        onView(withText(R.string.delete))
            .perform(click())

        onView(withText(firstSpriteName)).check(doesNotExist())

        onView(withText(secondSpriteName)).check(matches(isDisplayed()))

        openSpriteActionMenu(secondSpriteName, isGroupSprite = false)

        onView(withText(R.string.delete))
            .perform(click())

        onView(withText(secondSpriteName)).check(doesNotExist())

        onView(withId(R.id.menu_undo)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.menu_undo)).perform(click())

        onView(withText(firstSpriteName)).check(doesNotExist())

        onView(withText(secondSpriteName)).check(matches(isDisplayed()))
    }

    @Test
    fun noUndoOptionAfterOpenScript() {
        onView(withText(firstSpriteName)).check(matches(isDisplayed()))

        openSpriteActionMenu(firstSpriteName, isGroupSprite = false)

        onView(withText(R.string.delete))
            .perform(click())

        onView(withText(firstSpriteName)).check(doesNotExist())

        onView(withId(R.id.menu_undo)).check(matches(isCompletelyDisplayed()))

        onView(withText(secondSpriteName)).perform(click())

        pressBack()

        onView(withText(firstSpriteName)).check(doesNotExist())

        onView(withId(R.id.menu_undo)).check(doesNotExist())
    }

    @Test
    fun deleteAndUndoSingleSpriteTest() {
        onView(withText(firstSpriteName)).check(matches(isDisplayed()))

        openSpriteActionMenu(firstSpriteName, isGroupSprite = false)

        onView(withText(R.string.delete))
            .perform(click())

        onRecyclerView().checkHasNumberOfItems(4)

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText(firstSpriteName)).check(matches(isDisplayed()))

        Assert.assertNotNull(lookDirectory.listFiles()?.firstOrNull { file -> file.name == firstSpriteName })
    }

    @Test
    fun copyAndUndoSingleSpriteTest() {
        onView(withText(firstSpriteName)).check(matches(isDisplayed()))

        onRecyclerView().checkHasNumberOfItems(5)

        openSpriteActionMenu(firstSpriteName, isGroupSprite = false)

        onView(withText(R.string.copy))
            .perform(click())

        onRecyclerView().checkHasNumberOfItems(6)

        onView(withId(R.id.menu_undo))
            .perform(click())

        onRecyclerView().checkHasNumberOfItems(5)

        onView(withText(firstSpriteName)).check(matches(isDisplayed()))
    }

    @Test
    fun renameAndUndoSingleSpriteTest() {
        onView(withText(firstSpriteName)).check(matches(isDisplayed()))

        openSpriteActionMenu(firstSpriteName, isGroupSprite = false)
        onView(withText(R.string.rename))
            .perform(click())

        onView(withText(R.string.rename_sprite_dialog))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.input_edit_text))
            .perform(clearText())
            .perform(replaceText("renamedSprite"))
            .perform(closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())

        onView(withText("renamedSprite"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText(firstSpriteName)).check(matches(isDisplayed()))
    }

    @Test
    fun createGroupAndUndoTest() {
        openActionBarMenu()

        onView(withText(R.string.new_group))
            .perform(click())

        onView(withText(R.string.new_group)).inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.input_edit_text))
            .perform(clearText())
            .perform(replaceText("newGroup"))
            .perform(closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())

        onView(withText("newGroup"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText("newGroup"))
            .check(doesNotExist())
    }

    @Test
    fun deleteAndUndoGroupSpriteTest() {
        onView(withText(groupSpriteName)).check(matches(isDisplayed()))

        openSpriteActionMenu(groupSpriteName, isGroupSprite = true)

        onView(withText(R.string.delete))
            .perform(click())

        onView(withText(groupSpriteName)).check(doesNotExist())
        onView(withText(groupItemSpriteName)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText(groupSpriteName)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfDataIsCleanedUpAfterUndoTest() {
        Assert.assertEquals(lookDirectory.listFiles()?.size, 3)
        Assert.assertEquals(soundDirectory.listFiles()?.size, 3)

        onView(withText(firstSpriteName)).check(matches(isDisplayed()))

        openSpriteActionMenu(firstSpriteName, isGroupSprite = false)

        onView(withText(R.string.copy))
            .perform(click())

        Assert.assertEquals(lookDirectory.listFiles()?.size, 4)
        Assert.assertEquals(soundDirectory.listFiles()?.size, 4)

        onView(withId(R.id.menu_undo)).check(matches(isDisplayed()))

        onView(withId(R.id.menu_undo))
            .perform(click())

        Assert.assertEquals(lookDirectory.listFiles()?.size, 3)
        Assert.assertEquals(soundDirectory.listFiles()?.size, 3)
    }
    private fun createProject(projectName: String) {
        val project = UiTestUtils.createProjectWithoutSprite(projectName)
        XstreamSerializer.getInstance().saveProject(project)
        lookDirectory = File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME)
        soundDirectory = File(project.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME)
        val firstSprite = createSprite(firstSpriteName)
        project.defaultScene.addSprite(firstSprite)
        project.defaultScene.addSprite(createSprite(secondSpriteName))
        project.defaultScene.addSprite(GroupSprite(groupSpriteName))
        project.defaultScene.addSprite(GroupItemSprite(groupItemSpriteName))
        projectManager.currentSprite = firstSprite
        XstreamSerializer.getInstance().saveProject(project)
    }

    private fun createSprite(name : String) : Sprite {
        val sprite = Sprite(name)
        addLookFileToSprite(sprite)
        addSoundFileToSprite(sprite)
        return sprite
    }

    private fun addLookFileToSprite(sprite : Sprite) {
        val lookFile = createImageFileFromResourcesInDirectory(
            (getApplicationContext() as Context).resources,
            R.drawable.default_project_bird_wing_up,
            lookDirectory,
            sprite.name,
            1.0
        )
        sprite.lookList.add(LookData(sprite.name, lookFile))
    }

    private fun addSoundFileToSprite(sprite : Sprite) {
        val soundFile = createSoundFileFromResourcesInDirectory(
            (getApplicationContext() as Context).resources,
            R.raw.default_project_tweet_1,
            soundDirectory,
            sprite.name
        )
        sprite.soundList.add(SoundInfo(sprite.name, soundFile))
    }
}


