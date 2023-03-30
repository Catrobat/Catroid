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
package org.catrobat.catroid.test.content.script

import org.junit.runner.RunWith
import com.badlogic.gdx.graphics.OrthographicCamera
import org.junit.Before
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.utils.TouchUtil
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.test.content.script.WhenScriptTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.Color
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.bricks.WaitBrick
import org.junit.Test
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class WhenScriptTest {
    private var sprite: Sprite? = null
    private var whenScript: Script? = null
    private val camera = Mockito.spy(OrthographicCamera())
    @Before
    fun setUp() {
        sprite = createSprite()
        whenScript = WhenScript()
        sprite!!.addScript(whenScript)
        Mockito.doNothing().`when`(camera).update()
        createProjectWithSprite(sprite)
        TouchUtil.reset()
    }

    private fun createSprite(): Sprite {
        val sprite = Sprite("testSprite")
        sprite.look = object : Look(sprite) {
            init {
                pixmap = TestUtils.createRectanglePixmap(WIDTH, HEIGHT, Color.RED)
            }
        }
        sprite.look.setSize(WIDTH.toFloat(), HEIGHT.toFloat())
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f
        return sprite
    }

    private fun createProjectWithSprite(sprite: Sprite?): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), "testProject")
        ProjectManager.getInstance().currentProject = project
        project.defaultScene.addSprite(sprite)
        return project
    }

    @Test
    fun basicWhenScriptTest() {
        whenScript!!.addBrick(ChangeXByNBrick(10))
        sprite!!.initializeEventThreads(EventId.START)
        tapSprite()
        while (!sprite!!.look.haveAllThreadsFinished()) {
            sprite!!.look.act(1.0f)
        }
        Assert.assertEquals(10f, sprite!!.look.xInUserInterfaceDimensionUnit)
    }

    private fun tapSprite() {
        sprite!!.look.doTouchDown(0f, 0f, 0)
    }

    @Test
    fun whenScriptRestartTest() {
        whenScript!!.addBrick(WaitBrick(50))
        whenScript!!.addBrick(ChangeXByNBrick(10))
        sprite!!.initializeEventThreads(EventId.START)
        tapSprite()
        tapSprite()
        while (!sprite!!.look.haveAllThreadsFinished()) {
            sprite!!.look.act(1.0f)
        }
        Assert.assertEquals(10f, sprite!!.look.xInUserInterfaceDimensionUnit)
    }

    @Test
    fun movedCameraPosition() {
        camera.position[1000.0f, 600.0f] = 0.0f
        sprite!!.look.setPositionInUserInterfaceDimensionUnit(1000.0f, 600.0f)
        whenScript!!.addBrick(ChangeXByNBrick(10))
        sprite!!.initializeEventThreads(EventId.START)
        tapSprite()
        while (!sprite!!.look.haveAllThreadsFinished()) {
            sprite!!.look.act(1.0f)
        }
        Assert.assertEquals(1010f, sprite!!.look.xInUserInterfaceDimensionUnit)
    }

    companion object {
        private const val WIDTH = 100
        private const val HEIGHT = 100
    }
}