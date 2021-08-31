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
package org.catrobat.catroid.uiespresso.content.messagecontainer

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Functional
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.checkBroadcastMessageDoesNotExist
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.createNewBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.editBroadcastMessageOnBrick
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class BroadcastReceiveBrickMessageContainerTest {
    private val defaultMessage = "defaultMessage"
    private var project: Project? = null
    private var sprite: Sprite? = null
    private var broadcastMessageBrick: BroadcastMessageBrick? = null

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @kotlin.jvm.Throws(Exception::class)
    fun setUp() {
        createProject(this.javaClass.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @After
    @kotlin.jvm.Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(this.javaClass.simpleName)
    }

    @Category(AppUi::class, Functional::class)
    @Test
    fun testBroadcastReceiveBrickOmitSaveUnusedMessages() {
        val uselessMessage = "useless"
        val broadcastReceivePosition = 1
        val activity = baseActivityTestRule.activity

        createNewBroadcastMessageOnBrick(uselessMessage, broadcastReceivePosition, activity)

        onBrickAtPosition(broadcastReceivePosition).checkShowsText(uselessMessage)

        checkBroadcastMessageDoesNotExist(defaultMessage, broadcastReceivePosition, activity)

        editBroadcastMessageOnBrick(
            uselessMessage, defaultMessage, broadcastReceivePosition, activity)

        saveProjectSerial(project, ApplicationProvider.getApplicationContext())

        baseActivityTestRule.finishActivity()

        assertTrue(loadProject(project?.directory, ApplicationProvider.getApplicationContext()))

        projectManager.currentSprite = sprite

        baseActivityTestRule.launchActivity()

        onBrickAtPosition(broadcastReceivePosition).checkShowsText(defaultMessage)

        checkBroadcastMessageDoesNotExist(uselessMessage, broadcastReceivePosition, activity)
    }

    private fun createProject(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        sprite = Sprite("testSprite")
        val script: Script = StartScript()
        sprite?.addScript(script)
        broadcastMessageBrick = BroadcastReceiverBrick(BroadcastScript(defaultMessage))
        script.addBrick(broadcastMessageBrick)
        project?.defaultScene?.addSprite(sprite)

        projectManager.currentProject = project
        projectManager.currentSprite = sprite
    }
}
