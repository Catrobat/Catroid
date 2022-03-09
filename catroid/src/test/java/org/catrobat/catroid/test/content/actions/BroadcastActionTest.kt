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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.utils.GdxNativesLoader
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.Collections

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class)
class BroadcastActionTest {
    private lateinit var sprite: Sprite
    private lateinit var startScript: Script
    private lateinit var broadcastScript: Script

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    @Before
    fun setUp() {
        PowerMockito.mockStatic(GdxNativesLoader::class.java)

        startScript = StartScript()
        broadcastScript = BroadcastScript(MESSAGE_1)
        sprite = Sprite("testSprite").apply {
            addScript(startScript)
            addScript(broadcastScript)
        }

        val context = MockUtil.mockContextForProject(dependencyModules)
        Project(context, "testProject").also { project ->
            projectManager.currentProject = project
            project.defaultScene.addSprite(sprite)
        }
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testBroadcast() {
        val expectedXPosition = 123
        assertNotEquals(expectedXPosition, sprite.look.xInUserInterfaceDimensionUnit.toInt())

        startScript.addBrick(BroadcastBrick(MESSAGE_1))
        broadcastScript.addBrick(SetXBrick(Formula(expectedXPosition)))

        executeAllActions()
        assertEquals(expectedXPosition, sprite.look.xInUserInterfaceDimensionUnit.toInt())
    }

    @Test
    fun testBroadcastWait() {
        val expectedXPosition = 123
        assertNotEquals(expectedXPosition, sprite.look.xInUserInterfaceDimensionUnit.toInt())

        startScript.apply {
            addBrick(BroadcastWaitBrick(MESSAGE_1))
            addBrick(SetXBrick(Formula(expectedXPosition)))
        }
        broadcastScript.apply {
            addBrick(WaitBrick(500))
            addBrick(SetXBrick(Formula(20)))
        }

        executeAllActions()
        assertEquals(
            expectedXPosition,
            sprite.look.xInUserInterfaceDimensionUnit.toInt()
        )
    }

    @Test
    fun testScriptRestart() {
        val xMovement = 1

        startScript.addBrick(BroadcastBrick(MESSAGE_1))
        broadcastScript.apply {
            addBrick(ChangeXByNBrick(xMovement))
            addBrick(BroadcastBrick(MESSAGE_1))
            addBrick(WaitBrick(5))
        }

        executeAllActions()
        assertThat(
            sprite.look.xInUserInterfaceDimensionUnit.toInt(),
            Matchers.greaterThan(5 * xMovement)
        )
    }

    @Test
    fun testScriptRestartWithBroadcastWaitBrick() {
        val xMovement = 1

        startScript.addBrick(BroadcastBrick(MESSAGE_1))
        broadcastScript.apply {
            addBrick(ChangeXByNBrick(xMovement))
            addBrick(BroadcastWaitBrick(MESSAGE_2))
        }
        BroadcastScript(MESSAGE_2).apply {
            addBrick(ChangeXByNBrick(xMovement))
            addBrick(BroadcastWaitBrick(MESSAGE_1))
            sprite.addScript(this)
        }

        executeAllActions()
        assertThat(
            sprite.look.xInUserInterfaceDimensionUnit.toInt(),
            Matchers.greaterThan(5 * xMovement)
        )
    }

    @Test
    fun testRestartBroadcastWait() {
        val expectedXPosition = 123
        assertNotEquals(expectedXPosition, sprite.look.xInUserInterfaceDimensionUnit.toInt())

        startScript.apply {
            addBrick(BroadcastWaitBrick(MESSAGE_1))
            addBrick(SetXBrick(Formula(expectedXPosition)))
            addBrick(WaitBrick(1))
            addBrick(BroadcastBrick(MESSAGE_1))
        }
        broadcastScript.addBrick(WaitBrick(50))

        executeAllActions()
        assertEquals(expectedXPosition, sprite.look.xInUserInterfaceDimensionUnit.toInt())
    }

    private fun executeAllActions() {
        sprite.initializeEventThreads(EventId.START)
        repeat(20) {
            projectManager.currentlyEditedScene.spriteList.forEach { sprite ->
                sprite.look.act(1f)
            }
            if (sprite.look.haveAllThreadsFinished()) {
                return
            }
        }
    }

    companion object {
        const val MESSAGE_1 = "message1"
        const val MESSAGE_2 = "message2"
    }
}
