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

package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.GlideToTouchPositionAction
import org.catrobat.catroid.formulaeditor.Formula
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.catrobat.catroid.utils.TouchUtil
import org.koin.java.KoinJavaComponent

@RunWith(AndroidJUnit4::class)
class GlideToTouchPositionActionTest {

    private lateinit var sprite: Sprite
    private lateinit var action: GlideToTouchPositionAction
    private  val EXPECTED_X_POSITION =20f;
    private  val EXPECTED_Y_POSITION =40f;
    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)

    @Before
    @Throws(Exception::class)
    fun SetUp() {


        val project = Project()
        projectManager.currentProject = project
        val scene = Scene()
        project.addScene(scene)
        projectManager.currentlyPlayingScene = scene
        sprite = Sprite("testSprite")
        scene.addSprite(sprite)
        ScreenValues.setToDefaultScreenSize()
        action = sprite.actionFactory.createGlideToPositionAction(
            sprite, null, SequenceAction(), Formula(2.0),
            BrickValues.GLIDE_TO_TOUCH_POSITION
        )
            as GlideToTouchPositionAction
    }

    @Test
    public fun testGlideToTouchPositionDestination() {

        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f

        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)

        TouchUtil.touchDown(EXPECTED_X_POSITION, EXPECTED_Y_POSITION, 0)

        action.act(2.0F)

        assertEquals(EXPECTED_Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)
        assertEquals(EXPECTED_X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
    }

    @Test
    public fun testGlideToBehavior(){
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f

        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)

        TouchUtil.touchDown(EXPECTED_X_POSITION, EXPECTED_Y_POSITION, 0)
        action.act(1.0f)

        assertNotEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertNotEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        assertNotEquals(EXPECTED_X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
        assertNotEquals(EXPECTED_Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)
        assertEquals(action.currentXPosition, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(action.currentYPosition, sprite.look.yInUserInterfaceDimensionUnit)
    }
}
