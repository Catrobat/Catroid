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
package org.catrobat.catroid.uiespresso.content.brick.app

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.createNewBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.editBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.getResourcesString
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class BroadcastBrickMessageUpdateTest {
    private val defaultMessage = "defaultMessage"
    private val editedMessage = "editedMessage"
    private val message = "newAddedMessage"
    private var secondScene: Scene? = null
    private var secondSprite: Sprite? = null
    private var firstBroadcastBrick: BroadcastReceiverBrick? = null
    private val projectManager by inject(ProjectManager::class.java)

    @JvmField
    @Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @After
    fun tearDown() {
        TestUtils.deleteProjects(BroadcastBrickMessageUpdateTest::class.java.simpleName)
    }

    @Before
    fun setUp() {
        createTestProjectWithBricks(BroadcastBrickMessageUpdateTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testAllBroadcastBrickSpinnersContainTheNewAddedMessage() {
        createNewBroadcastMessageOnBrick(
            message, firstBroadcastBrick, baseActivityTestRule.activity
        )
        val spinnerValues = listOf(
            getResourcesString(R.string.new_option),
            getResourcesString(R.string.edit_option),
            defaultMessage,
            message
        )
        checkAllBrickSpinnerValues(spinnerValues)
    }

    @Test
    fun testAllBroadcastBrickSpinnersContainTheEditedMessage() {
        editBroadcastMessageOnBrick(
            defaultMessage, editedMessage, firstBroadcastBrick, baseActivityTestRule.activity
        )
        val spinnerValues = listOf(
            getResourcesString(R.string.new_option),
            getResourcesString(R.string.edit_option),
            editedMessage
        )
        checkAllBrickSpinnerValues(spinnerValues)
    }

    @Test
    fun testAllBroadcastBrickSpinnersShowTheEditedMessage() {
        editBroadcastMessageOnBrick(
            defaultMessage, editedMessage, firstBroadcastBrick, baseActivityTestRule.activity
        )
        checkShowsCorrectSpinnerMessage(editedMessage)
    }

    @Test
    fun testEditingOccursOnlyInCurrentScene() {
        editBroadcastMessageOnBrick(
            defaultMessage, editedMessage, firstBroadcastBrick, baseActivityTestRule.activity
        )
        switchScene()
        checkShowsCorrectSpinnerMessage(defaultMessage)
    }

    private fun checkAllBrickSpinnerValues(spinnerValues: List<String>) {
        onBrickAtPosition(1)
            .onSpinner(R.id.brick_broadcast_spinner)
            .checkNameableValuesAvailable(spinnerValues)
        onBrickAtPosition(2)
            .onSpinner(R.id.brick_broadcast_spinner)
            .checkNameableValuesAvailable(spinnerValues)
        onBrickAtPosition(3)
            .onSpinner(R.id.brick_broadcast_spinner)
            .checkNameableValuesAvailable(spinnerValues)
        onBrickAtPosition(4)
            .onSpinner(R.id.brick_broadcast_spinner)
            .checkNameableValuesAvailable(spinnerValues)
        onBrickAtPosition(5)
            .onSpinner(R.id.brick_broadcast_spinner)
            .checkNameableValuesAvailable(spinnerValues)
    }

    private fun checkShowsCorrectSpinnerMessage(message: String) {
        onBrickAtPosition(1).onSpinner(R.id.brick_broadcast_spinner).checkShowsText(message)
        onBrickAtPosition(2).onSpinner(R.id.brick_broadcast_spinner).checkShowsText(message)
        onBrickAtPosition(3).onSpinner(R.id.brick_broadcast_spinner).checkShowsText(message)
        onBrickAtPosition(4).onSpinner(R.id.brick_broadcast_spinner).checkShowsText(message)
        onBrickAtPosition(5).onSpinner(R.id.brick_broadcast_spinner).checkShowsText(message)
    }

    private fun switchScene() {
        baseActivityTestRule.finishActivity()
        projectManager.currentSprite = secondSprite
        projectManager.currentlyEditedScene = secondScene
        baseActivityTestRule.launchActivity()
    }

    private fun createTestProjectWithBricks(projectName: String) {
        val project = UiTestUtils.createDefaultTestProject(projectName)
        val firstSprite = UiTestUtils.getDefaultTestSprite(project)
        secondSprite = Sprite("spriteScene2")

        val script: Script = BroadcastScript(defaultMessage)
        firstBroadcastBrick = script.scriptBrick as BroadcastReceiverBrick

        script.addBrick(BroadcastBrick(defaultMessage))
        script.addBrick(BroadcastWaitBrick(defaultMessage))
        firstSprite.addScript(script)

        try {
            firstSprite.addScript(script.clone())
            secondSprite!!.addScript(script.clone())
            secondSprite!!.addScript(script.clone())
        } catch (e: CloneNotSupportedException) {
            Log.e(BroadcastBrickMessageUpdateTest::class.java.simpleName, e.message!!)
        }
        val firstScene = Scene("Scene1", project)
        secondScene = Scene("Scene2", project)
        secondScene!!.addSprite(secondSprite)

        project.addScene(firstScene)
        project.addScene(secondScene)
    }
}
