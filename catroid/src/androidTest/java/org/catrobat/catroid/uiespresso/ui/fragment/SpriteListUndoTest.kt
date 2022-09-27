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

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter.createImageFileFromResourcesInDirectory
import org.catrobat.catroid.io.ResourceImporter.createSoundFileFromResourcesInDirectory
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewItemCountAssertion.Companion.withItemCount
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import java.io.File

@RunWith(AndroidJUnit4::class)
class SpriteListUndoTest {

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    private val amountOfSprites = 3

    private val looks = listOf(R.drawable.default_project_clouds_portrait,
        R.drawable.default_project_bird_wing_up,
        R.drawable.default_project_background_portrait)

    private val sounds = listOf(R.raw.default_project_tweet_1,
        R.raw.default_project_tweet_2)

    private lateinit var imageDirectory: File
    private lateinit var soundDirectory: File

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity(null)

        onView(withText("testProject"))
            .perform(click())
    }

    @Test
    fun deleteAndUndoSingleSpriteTest() {
        UiTestUtils.openSpriteActionMenu("testSprite_0", false)
        onView(withText(R.string.delete))
            .perform(click())

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText("testSprite_0"))
            .check(matches(isDisplayed()))

        val imageFile = imageDirectory.listFiles()?.firstOrNull { file -> file.name == "image_0" }
        Assert.assertNotNull(imageFile)

        val soundFile = soundDirectory.listFiles()?.firstOrNull { file -> file.name == "sound_0" }
        Assert.assertNotNull(soundFile)
    }

    @Test
    fun deleteSingleSpriteAndCheckIfLookAndSoundDataIsCleanedUpTest() {
        UiTestUtils.openSpriteActionMenu("testSprite_0", false)
        onView(withText(R.string.delete))
            .perform(click())

        pressBack()

        onView(withText("testProject"))
            .perform(click())

        val imageFile = imageDirectory.listFiles()?.firstOrNull { file -> file.name == "image_0" }
        Assert.assertNull(imageFile)

        val soundFile = soundDirectory.listFiles()?.firstOrNull { file -> file.name == "sound_0" }
        Assert.assertNull(soundFile)
    }

    @Test
    fun deleteAndUndoMultipleSpritesAtOnceTest() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)
        onView(withText(R.string.delete))
            .perform(click())

        onRecyclerView().atPosition(1)
            .performCheckItem()

        onRecyclerView().atPosition(2)
            .performCheckItem()

        onRecyclerView().atPosition(3)
            .performCheckItem()

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withId(R.id.recycler_view)).check(withItemCount(1))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withId(R.id.recycler_view)).check(withItemCount(4))

        // need to assert on 4 elements because there is a ".nomedia" file in the directory
        Assert.assertEquals(4, imageDirectory.listFiles()?.size)
        Assert.assertEquals(4, soundDirectory.listFiles()?.size)
    }

    @Test
    fun deleteMultipleSpritesAndCheckIfLookAndSoundDataIsCleanedUpTest() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)
        onView(withText(R.string.delete))
            .perform(click())

        onRecyclerView().atPosition(1)
            .performCheckItem()

        onRecyclerView().atPosition(2)
            .performCheckItem()

        onView(withId(R.id.confirm))
            .perform(click())

        pressBack()

        onView(withText("testProject"))
            .perform(click())

        val imageFile0 = imageDirectory.listFiles()?.firstOrNull { file -> file.name == "image_0" }
        Assert.assertNull(imageFile0)
        val imageFile1 = imageDirectory.listFiles()?.firstOrNull { file -> file.name == "image_1" }
        Assert.assertNull(imageFile1)
        val imageFile2 = imageDirectory.listFiles()?.firstOrNull { file -> file.name == "image_2" }
        Assert.assertNotNull(imageFile2)

        val soundFile0 = soundDirectory.listFiles()?.firstOrNull { file -> file.name == "sound_0" }
        Assert.assertNull(soundFile0)
        val soundFile1 = soundDirectory.listFiles()?.firstOrNull { file -> file.name == "sound_1" }
        Assert.assertNull(soundFile1)
        val soundFile2 = soundDirectory.listFiles()?.firstOrNull { file -> file.name == "sound_2" }
        Assert.assertNotNull(soundFile2)
    }

    @Test
    fun renameAndUndoSingleSpriteTest() {
        UiTestUtils.openSpriteActionMenu("testSprite_0", false)
        onView(withText(R.string.rename))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(clearText(), replaceText("renamedSprite"), closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())

        onView(withText("renamedSprite"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText("testSprite_0"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun copyAndUndoSingleSpriteTest() {
        UiTestUtils.openSpriteActionMenu("testSprite_0", false)
        onView(withText(R.string.rename))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(clearText(), replaceText("renamedSprite"), closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())

        onView(withText("renamedSprite"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText("testSprite_0"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun copyAndUndoMultipleSpritesAtOnceTest() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)
        onView(withText(R.string.copy))
            .perform(click())

        onRecyclerView().atPosition(1)
            .performCheckItem()

        onRecyclerView().atPosition(2)
            .performCheckItem()

        onRecyclerView().atPosition(3)
            .performCheckItem()

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withId(R.id.recycler_view)).check(withItemCount(7))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withId(R.id.recycler_view)).check(withItemCount(4))
    }

    @Test
    fun createGroupAndUndoTest() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.new_group))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(clearText(), replaceText("newGroup"), closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())

        onView(withText("newGroup"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withText("newGroup"))
            .check(doesNotExist())
    }

    private fun createProject(): Project {
        val project = Project(getApplicationContext(), "testProject")
        XstreamSerializer.getInstance().saveProject(project)

        val scene = project.defaultScene
        imageDirectory = File(scene.directory, Constants.IMAGE_DIRECTORY_NAME)
        soundDirectory = File(scene.directory, Constants.SOUND_DIRECTORY_NAME)

        projectManager.currentProject = project

        for (i in 0 until amountOfSprites) {
            projectManager.currentProject.defaultScene.addSprite(createSpriteWithLookAndSound(i))
        }

        XstreamSerializer.getInstance().saveProject(project)
        return project
    }

    private fun createSpriteWithLookAndSound(number: Int): Sprite {
        val sprite = Sprite("testSprite_$number")

        val lookFile = createImageFileFromResourcesInDirectory(
            (getApplicationContext() as Context).resources,
            looks[number % looks.size],
            imageDirectory,
            "image_$number",
            1.0
        )

        val soundFile = createSoundFileFromResourcesInDirectory(
            (getApplicationContext() as Context).resources,
            sounds[number % sounds.size],
            soundDirectory,
            "sound_$number"
        )

        sprite.lookList.add(LookData("Look", lookFile))
        sprite.soundList.add(SoundInfo("Sound", soundFile))
        return sprite
    }
}
