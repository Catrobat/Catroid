/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.ui.actionbar

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.NfcTagData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.nfc.NfcHandler
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.content.brick.utils.UiNFCTestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.IOException
import kotlin.getValue

@RunWith(Parameterized::class)
class ActionBarUndoSpinnerTest(
    private val brickName: String,
    private val brickPosition: Int,
    private val brickSpinnerViewId: Int
) {
    private val waitThreshold: Long = 5000

    @get:Rule
    var baseActivityTestRule: FragmentActivityTestRule<SpriteActivity?> = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    private val firstItem = "abc"
    private val secondItem = "def"

    private val projectManager by inject(ProjectManager::class.java)

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(ActionBarUndoSpinnerTest::class.java.simpleName)
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testUndoSpinnerActionVisible() {
        onView(withId(R.id.menu_undo)).check(doesNotExist())
        BrickDataInteractionWrapper.onBrickAtPosition(brickPosition).onSpinner(brickSpinnerViewId)
            .performSelectNameable(secondItem)

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isDisplayed(), waitThreshold))

        BrickDataInteractionWrapper.onBrickAtPosition(brickPosition).onSpinner(brickSpinnerViewId)
            .performSelectNameable(firstItem)

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isDisplayed(), waitThreshold))
    }

    @Test
    fun testUndoSpinnerAction() {
        BrickDataInteractionWrapper.onBrickAtPosition(brickPosition).onSpinner(brickSpinnerViewId)
            .performSelectNameable(secondItem)
        onView(withId(R.id.menu_undo))
            .perform(waitFor(isDisplayed(), waitThreshold))
        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_undo)).check(doesNotExist())
    }

    @Test
    fun testUndoSpinnerNotVisibleAfterNewOptionSelected() {
        if (brickPosition >= 4) {
            val newItem = "new"
            BrickDataInteractionWrapper.onBrickAtPosition(brickPosition)
                .onVariableSpinner(brickSpinnerViewId).performNewVariable(newItem)
            onView(withId(R.id.menu_undo))
                .check(doesNotExist())
        }
    }

    private fun createProject() {
        val script =
            createProjectAndGetStartScript(ActionBarUndoSpinnerTest::class.java.simpleName)
        val currentProject = projectManager.currentProject
        val currentSprite = projectManager.currentSprite

        currentProject.addUserVariable(UserVariable(firstItem))
        currentProject.addUserVariable(UserVariable(secondItem))

        val soundDirectory =
            File(currentProject.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME)
        val imageDirectory =
            File(currentProject.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME)

        val firstSoundFile = File(soundDirectory, "abc.mp3")
        val secondSoundFile = File(soundDirectory, "def.mp3")
        val firstLookFile = File(imageDirectory, "abc.png")
        val secondLookFile = File(imageDirectory, "def.png")

        val soundInfoList = currentSprite.soundList
        soundInfoList.add(SoundInfo(firstItem, firstSoundFile))
        soundInfoList.add(SoundInfo(secondItem, secondSoundFile))

        val lookDataList = currentSprite.lookList
        lookDataList.add(LookData(firstItem, firstLookFile))
        lookDataList.add(LookData(secondItem, secondLookFile))

        val nfcTagDataList = currentSprite.nfcTagList
        val firstTagData = NfcTagData()
        firstTagData.name = firstItem
        firstTagData.nfcTagUid = NfcHandler.byteArrayToHex(UiNFCTestUtils.FIRST_TEST_TAG_ID.toByteArray())
        val secondTagData = NfcTagData()
        secondTagData.name = secondItem
        secondTagData.nfcTagUid = NfcHandler.byteArrayToHex(UiNFCTestUtils.SECOND_TEST_TAG_ID.toByteArray())
        nfcTagDataList.add(firstTagData)
        nfcTagDataList.add(secondTagData)
        val whenNfcBrick = WhenNfcBrick()
        whenNfcBrick.onItemSelected(R.id.brick_when_nfc_spinner, firstTagData)

        script.addBrick(PlaySoundBrick())
        script.addBrick(SetLookBrick())
        script.addBrick(whenNfcBrick)
        script.addBrick(SetVariableBrick(Formula(1), UserVariable(firstItem)))
        script.addBrick(BroadcastBrick(firstItem))
        script.addBrick(BroadcastBrick(secondItem))

        XstreamSerializer.getInstance().saveProject(currentProject)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf(
                PlaySoundBrick::class.java.name, 1, R.id.brick_play_sound_spinner
            ), arrayOf<Any?>(
                SetLookBrick::class.java.name, 2, R.id.brick_set_look_spinner
            ), arrayOf<Any?>(
                WhenNfcBrick::class.java.name, 3, R.id.brick_when_nfc_spinner
            ), arrayOf<Any?>(
                SetVariableBrick::class.java.name, 4, R.id.set_variable_spinner
            ), arrayOf<Any?>(
                BroadcastBrick::class.java.name, 5, R.id.brick_broadcast_spinner
            )
        )
    }
}
