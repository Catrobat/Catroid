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
import org.junit.Before
import org.catrobat.catroid.content.WhenTouchDownScript
import org.catrobat.catroid.utils.TouchUtil
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.bricks.WaitBrick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class WhenTouchDownScriptTest {
    private var sprite: Sprite? = null
    private var touchDownScript: Script? = null
    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        sprite!!.look.xInUserInterfaceDimensionUnit = 0f
        touchDownScript = WhenTouchDownScript()
        sprite!!.addScript(touchDownScript)
        createProjectWithSprite(sprite!!)
        TouchUtil.reset()
    }

    @Test
    fun basicTouchDownScriptTest() {
        touchDownScript!!.addBrick(ChangeXByNBrick(10))
        sprite!!.initializeEventThreads(EventId.START)
        TouchUtil.touchDown(0f, 0f, 1)
        while (!sprite!!.look.haveAllThreadsFinished()) {
            sprite!!.look.act(1.0f)
        }
        Assert.assertEquals(10f, sprite!!.look.xInUserInterfaceDimensionUnit)
    }

    @Test
    fun touchDownScriptRestartTest() {
        touchDownScript!!.addBrick(WaitBrick(50))
        touchDownScript!!.addBrick(ChangeXByNBrick(10))
        sprite!!.initializeEventThreads(EventId.START)
        TouchUtil.touchDown(0f, 0f, 1)
        TouchUtil.touchUp(1)
        TouchUtil.touchDown(10f, 10f, 1)
        while (!sprite!!.look.haveAllThreadsFinished()) {
            sprite!!.look.act(1.0f)
        }
        Assert.assertEquals(10f, sprite!!.look.xInUserInterfaceDimensionUnit)
    }

    private fun createProjectWithSprite(sprite: Sprite): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), "testProject")
        ProjectManager.getInstance().currentProject = project
        project.defaultScene.addSprite(sprite)
        return project
    }
}