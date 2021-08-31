/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.app.Activity
import android.util.Log
import org.junit.runner.RunWith
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.Before
import org.catrobat.catroid.ProjectManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.checkBroadcastMessageDoesNotExist
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.createNewBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.editBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.selectBroadcastMessageOnBrick
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException
import java.lang.Exception
import org.catrobat.catroid.rules.FlakyTestRule
import org.catrobat.catroid.runner.Flaky

@RunWith(AndroidJUnit4::class)
class BroadcastBrickMessageUpdateTest {
    private val defaultMessage = "defaultMessage"
    private val editedMessage = "editedMessage"
    private val newAddedMessage = "newAddedMessage"
    private val otherNewAddedMessage = "otherNewAddedMessage"
    private var secondScene: Scene? = null
    private var secondSprite: Sprite? = null
    private var firstBroadcastBrick: BroadcastReceiverBrick? = null

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS
    )

    @get:Rule
    var flakyTestRule: FlakyTestRule<*> = FlakyTestRule<Activity>()

    @After
    @Throws(IOException::class)
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(BroadcastBrickMessageUpdateTest::class.java.simpleName)
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createTestProjectWithBricks(BroadcastBrickMessageUpdateTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @Test
    @Flaky
    fun testAllBroadcastBricksShowTheEditedMessage() {
        editBroadcastMessageOnBrick(defaultMessage, editedMessage, 1, baseActivityTestRule.activity)
        checkShowsCorrectMessage(editedMessage)
    }

    @Test
    @Flaky
    fun testEditingOccursOnlyInCurrentScene() {
        editBroadcastMessageOnBrick(defaultMessage, editedMessage, 1, baseActivityTestRule.activity)
        switchScene()
        checkShowsCorrectMessage(defaultMessage)
    }

    @Test
    @Flaky
    fun testMultipleDifferentMessages() {
        checkShowsCorrectMessage(defaultMessage)
        createNewBroadcastMessageOnBrick(newAddedMessage, 2, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(defaultMessage)
        onBrickAtPosition(2).checkShowsText(newAddedMessage)
        onBrickAtPosition(3).checkShowsText(defaultMessage)
        onBrickAtPosition(4).checkShowsText(defaultMessage)
        onBrickAtPosition(5).checkShowsText(defaultMessage)

        createNewBroadcastMessageOnBrick(otherNewAddedMessage, 5, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(defaultMessage)
        onBrickAtPosition(2).checkShowsText(newAddedMessage)
        onBrickAtPosition(3).checkShowsText(defaultMessage)
        onBrickAtPosition(4).checkShowsText(defaultMessage)
        onBrickAtPosition(5).checkShowsText(otherNewAddedMessage)

        selectBroadcastMessageOnBrick(
            otherNewAddedMessage, 2, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(defaultMessage)
        onBrickAtPosition(2).checkShowsText(otherNewAddedMessage)
        onBrickAtPosition(3).checkShowsText(defaultMessage)
        onBrickAtPosition(4).checkShowsText(defaultMessage)
        onBrickAtPosition(5).checkShowsText(otherNewAddedMessage)

        editBroadcastMessageOnBrick(defaultMessage, editedMessage, 1, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(editedMessage)
        onBrickAtPosition(2).checkShowsText(otherNewAddedMessage)
        onBrickAtPosition(3).checkShowsText(editedMessage)
        onBrickAtPosition(4).checkShowsText(editedMessage)
        onBrickAtPosition(5).checkShowsText(otherNewAddedMessage)

        checkBroadcastMessageDoesNotExist(newAddedMessage, 1, baseActivityTestRule.activity)
    }

    @Test
    @Flaky
    fun testCreateNewMessageAndSwitchScene() {
        createNewBroadcastMessageOnBrick(newAddedMessage, 1, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(newAddedMessage)
        onBrickAtPosition(2).checkShowsText(defaultMessage)
        onBrickAtPosition(3).checkShowsText(defaultMessage)
        onBrickAtPosition(4).checkShowsText(defaultMessage)
        onBrickAtPosition(5).checkShowsText(defaultMessage)

        switchScene()

        checkShowsCorrectMessage(defaultMessage)

        checkBroadcastMessageDoesNotExist(newAddedMessage, 1, baseActivityTestRule.activity)
    }

    private fun checkShowsCorrectMessage(message: String) {
        onBrickAtPosition(1).checkShowsText(message)
        onBrickAtPosition(2).checkShowsText(message)
        onBrickAtPosition(3).checkShowsText(message)
        onBrickAtPosition(4).checkShowsText(message)
        onBrickAtPosition(5).checkShowsText(message)
    }

    private fun switchScene() {
        baseActivityTestRule.finishActivity()
        projectManager.currentSprite = secondSprite
        projectManager.currentlyEditedScene = secondScene
        baseActivityTestRule.launchActivity()
    }

    private fun createTestProjectWithBricks(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val firstSprite = Sprite("spriteScene1")
        secondSprite = Sprite("spriteScene2")

        val script: Script = BroadcastScript(defaultMessage)
        firstBroadcastBrick = script.scriptBrick as BroadcastReceiverBrick

        script.addBrick(BroadcastBrick(defaultMessage))
        script.addBrick(BroadcastWaitBrick(defaultMessage))

        firstSprite.addScript(script)

        try {
            firstSprite.addScript(script.clone())
            secondSprite?.addScript(script.clone())
            secondSprite?.addScript(script.clone())
        } catch (e: CloneNotSupportedException) {
            e.message?.let { Log.e(BroadcastBrickMessageUpdateTest::class.java.simpleName, it) }
        }
        val firstScene = Scene("Scene1", project)
        secondScene = Scene("Scene2", project)
        firstScene.addSprite(firstSprite)
        secondScene?.addSprite(secondSprite)
        project.addScene(firstScene)
        project.addScene(secondScene)
        projectManager.currentProject = project
        projectManager.currentSprite = firstSprite
        projectManager.currentlyEditedScene = firstScene
    }
}
