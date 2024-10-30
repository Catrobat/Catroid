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

package org.catrobat.catroid.uiespresso.content.brick.app

import org.catrobat.catroid.R
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.pocketmusic.PocketMusicActivityTest
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createDefaultTestProject
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.getDefaultTestScript
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.getResourcesString
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.openActionBarMenu
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.actions.selectTabAtPosition
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.IOException

class PlaySoundBrickTest {

    private val soundName = "testSound1"
    private val soundName2 = "testSound2"
    private var soundFile: File? = null
    private var soundFile2: File? = null

    @Rule
    @JvmField
    var baseActivityTestRule = BaseActivityTestRule(
        SpriteActivity::class.java, true, false
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity(null)
    }

    private fun renameSound(position: Int, newSoundName: String) {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SOUNDS))
        openActionBarMenu()
        onView(withText(R.string.rename)).perform(click())
        onRecyclerView().atPosition(position).perform(click())
        onView(
            allOf(
                withText(soundName), isDisplayed(), Matchers.instanceOf(
                    EditText::class.java
                )
            )
        ).perform(ViewActions.replaceText(newSoundName))
        onView(
            allOf(
                withId(android.R.id.button1), withText(R.string.ok), isDisplayed()
            )
        ).perform(closeSoftKeyboard()).perform(click())
    }

    private fun deleteSound(position: Int) {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SOUNDS))
        openActionBarMenu()
        onView(withText(R.string.delete)).perform(click())
        onRecyclerView().atPosition(position).performCheckItemClick()
        onView(withId(R.id.confirm)).perform(click())
        onView(allOf(withId(android.R.id.button1), withText(R.string.delete))).check(
            matches(
                isDisplayed()
            )
        )
        onView(allOf(withId(android.R.id.button1), withText(R.string.delete))).perform(click())
    }

    @Test
    fun testRecordNewSound() {
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).perform(click())
        onView(withText(R.string.new_option)).perform(click())
        onView(withText(R.string.add_sound_from_recorder)).perform(click())
        onView(withId(R.id.soundrecorder_record_button)).perform(click())
        onView(isRoot()).perform(CustomActions.wait(1000))
        onView(withId(R.id.soundrecorder_record_button)).perform(click())
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
            .checkShowsText(getResourcesString(R.string.soundrecorder_recorded_filename))
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
    }

    @Test
    fun testAddNewSoundMenu() {
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).perform(click())
        onView(withText(R.string.new_option)).perform(click())
        onView(withText(R.string.add_sound_from_recorder)).check(matches(isDisplayed()))
        onView(withText(R.string.add_sound_choose_file)).check(matches(isDisplayed()))
        onView(withText(R.string.add_sound_choose_sound)).check(matches(isDisplayed()))
        onView(withText(R.string.add_sound_music)).check(matches(isDisplayed()))
    }

    @Test
    fun testCreateNewPocketMusicSong() {
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).perform(click())
        onView(withText(R.string.new_option)).perform(click())
        onView(withId(R.id.dialog_new_sound_pocketmusic)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_sound_pocketmusic)).perform(click())
        onView(isRoot()).perform(CustomActions.wait(500))
        onView(withId(R.id.tact_scroller)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1, PocketMusicActivityTest.toggleNoteViewAtPositionInTact(1)
            )
        )
        onView(isRoot()).perform(CustomActions.wait(1000))
        pressBack()
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
            .checkShowsText(getResourcesString(R.string.pocket_music_default_project_name))
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
    }

    @Test
    fun testDeleteSound() {
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        deleteSound(0)
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS))
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName2)
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName2)
    }

    @Test
    fun testRenameSound() {
        val newSoundName = "newName"
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(soundName)
        renameSound(0, newSoundName)
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS))
        onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(newSoundName)
        onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner).checkShowsText(newSoundName)
    }

    @Throws(IOException::class)
    private fun createProject() {
        val project = createDefaultTestProject("PlaySoundBrickTest")
        val startScript = getDefaultTestScript(project)
        XstreamSerializer.getInstance().saveProject(project)
        startScript.addBrick(PlaySoundBrick())
        startScript.addBrick(PlaySoundBrick())
        soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.longsound,
            File(project.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "longsound.mp3"
        )
        val soundInfo = SoundInfo()
        soundInfo.file = soundFile
        soundInfo.name = soundName
        soundFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.testsoundui,
            File(project.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "testsoundui.mp3"
        )
        val soundInfo2 = SoundInfo()
        soundInfo2.file = soundFile2
        soundInfo2.name = soundName2
        val soundInfoList = project.defaultScene.spriteList[1].soundList
        soundInfoList.add(soundInfo)
        soundInfoList.add(soundInfo2)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        if (soundFile != null && soundFile!!.exists()) {
            soundFile!!.delete()
        }
        if (soundFile2 != null && soundFile2!!.exists()) {
            soundFile2!!.delete()
        }
    }
}
