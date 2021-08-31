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
package org.catrobat.catroid.uiespresso.content.messagecontainer

import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.junit.runner.RunWith
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.junit.Before
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Functional
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import org.catrobat.catroid.io.asynctask.ProjectLoadTask
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastMessageBrickTestUtils.Companion.createNewBroadcastMessageOnBrick
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.java.KoinJavaComponent.inject
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class BroadcastAndWaitBrickMessageContainerTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    private val defaultMessage = "defaultMessage"
    private var project: Project? = null
    private var sprite: Sprite? = null
    private val broadcastAndWaitPosition = 1
    private var broadcastMessageBrick: BroadcastMessageBrick? = null

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

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
    fun testBroadcastAndWaitBrickOmitSaveUnusedMessages() {
        val uselessMessage = "useless"
        createNewBroadcastMessageOnBrick(
            uselessMessage, broadcastAndWaitPosition,
            baseActivityTestRule.activity
        )

        onBrickAtPosition(broadcastAndWaitPosition)
            .checkShowsText(uselessMessage)

        onBrickAtPosition(broadcastAndWaitPosition)
            .performEditBroadcastMessage()

        onView(withText(defaultMessage)).check(doesNotExist())

        pressBack()

        createNewBroadcastMessageOnBrick(
            defaultMessage, broadcastAndWaitPosition,
            baseActivityTestRule.activity
        )

        onBrickAtPosition(broadcastAndWaitPosition)
            .checkShowsText(defaultMessage)

        saveProjectSerial(project, ApplicationProvider.getApplicationContext())

        baseActivityTestRule.finishActivity()

        assertTrue(
            ProjectLoadTask
                .task(project?.directory, ApplicationProvider.getApplicationContext())
        )

        projectManager.currentSprite = sprite

        baseActivityTestRule.launchActivity()

        onBrickAtPosition(broadcastAndWaitPosition)
            .checkShowsText(defaultMessage)

        onBrickAtPosition(broadcastAndWaitPosition)
            .performEditBroadcastMessage()

        onView(withText(uselessMessage)).check(doesNotExist())
    }

    private fun createProject(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        sprite = Sprite("testSprite")
        val script: Script = StartScript()
        sprite?.addScript(script)
        broadcastMessageBrick = BroadcastWaitBrick(defaultMessage)
        script.addBrick(broadcastMessageBrick)
        project?.defaultScene?.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
    }
}
