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
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.rules.FlakyTestRule
import org.catrobat.catroid.runner.Flaky
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.content.messagecontainer.TestUtilsBroadcastMessageBrick.Companion.checkBroadcastMessageDoesNotExist
import org.catrobat.catroid.uiespresso.content.messagecontainer.TestUtilsBroadcastMessageBrick.Companion.createNewBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.content.messagecontainer.TestUtilsBroadcastMessageBrick.Companion.editBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.content.messagecontainer.TestUtilsBroadcastMessageBrick.Companion.selectBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException

class BroadcastBrickMessageUpdateTest {
    private val standardMessage = "standardMessage"
    private val editedMessage = "editedMessage"
    private val addedMessage = "addedMessage"
    private val addedMessage2 = "addedMessage2"
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
    var flakyTestRule: FlakyTestRule = FlakyTestRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createTestProjectWithBricks(BroadcastBrickMessageUpdateTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(BroadcastBrickMessageUpdateTest::class.java.simpleName)
    }

    @Test
    @Flaky
    fun testAllBroadcastBricksShowTheEditedMessage() {
        editBroadcastMessageOnBrick(
            standardMessage,
            editedMessage,
            1,
            baseActivityTestRule.activity
        )
        checkShowsSameBroadcastMessage(editedMessage)
    }

    @Test
    @Flaky
    fun testEditingOccursOnlyInCurrentScene() {
        editBroadcastMessageOnBrick(
            standardMessage,
            editedMessage,
            1,
            baseActivityTestRule.activity
        )
        switchScene()
        checkShowsSameBroadcastMessage(standardMessage)
    }

    @Test
    @Flaky
    fun testMultipleDifferentMessages() {
        checkShowsSameBroadcastMessage(standardMessage)
        createNewBroadcastMessageOnBrick(addedMessage, 2, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(standardMessage)
        onBrickAtPosition(2).checkShowsText(addedMessage)
        onBrickAtPosition(3).checkShowsText(standardMessage)
        onBrickAtPosition(4).checkShowsText(standardMessage)
        onBrickAtPosition(5).checkShowsText(standardMessage)

        createNewBroadcastMessageOnBrick(addedMessage2, 5, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(standardMessage)
        onBrickAtPosition(2).checkShowsText(addedMessage)
        onBrickAtPosition(3).checkShowsText(standardMessage)
        onBrickAtPosition(4).checkShowsText(standardMessage)
        onBrickAtPosition(5).checkShowsText(addedMessage2)

        selectBroadcastMessageOnBrick(
            addedMessage2, 2, baseActivityTestRule.activity
        )

        onBrickAtPosition(1).checkShowsText(standardMessage)
        onBrickAtPosition(2).checkShowsText(addedMessage2)
        onBrickAtPosition(3).checkShowsText(standardMessage)
        onBrickAtPosition(4).checkShowsText(standardMessage)
        onBrickAtPosition(5).checkShowsText(addedMessage2)

        editBroadcastMessageOnBrick(
            standardMessage,
            editedMessage,
            1,
            baseActivityTestRule.activity
        )

        onBrickAtPosition(1).checkShowsText(editedMessage)
        onBrickAtPosition(2).checkShowsText(addedMessage2)
        onBrickAtPosition(3).checkShowsText(editedMessage)
        onBrickAtPosition(4).checkShowsText(editedMessage)
        onBrickAtPosition(5).checkShowsText(addedMessage2)

        checkBroadcastMessageDoesNotExist(addedMessage, 1, baseActivityTestRule.activity)
    }

    @Test
    @Flaky
    fun testCreateNewMessageAndSwitchScene() {
        createNewBroadcastMessageOnBrick(addedMessage, 1, baseActivityTestRule.activity)

        onBrickAtPosition(1).checkShowsText(addedMessage)
        onBrickAtPosition(2).checkShowsText(standardMessage)
        onBrickAtPosition(3).checkShowsText(standardMessage)
        onBrickAtPosition(4).checkShowsText(standardMessage)
        onBrickAtPosition(5).checkShowsText(standardMessage)

        switchScene()

        checkShowsSameBroadcastMessage(standardMessage)

        checkBroadcastMessageDoesNotExist(addedMessage, 1, baseActivityTestRule.activity)
    }

    private fun checkShowsSameBroadcastMessage(message: String) {
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

        val script: Script = BroadcastScript(standardMessage)
        firstBroadcastBrick = script.scriptBrick as BroadcastReceiverBrick

        script.addBrick(BroadcastBrick(standardMessage))
        script.addBrick(BroadcastWaitBrick(standardMessage))

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
