/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import org.catrobat.catroid.content.actions.GlideToOtherSpritePositionAction
import org.catrobat.catroid.formulaeditor.Formula
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GlideToOtherSpritePositionActionTest {
    private lateinit var sprite: Sprite
    private lateinit var destinationSprite: Sprite
    private lateinit var action: GlideToOtherSpritePositionAction

    companion object {
        private const val DESTINATION_X_POSITION = 150f
        private const val DESTINATION_Y_POSITION = 300f
    }

    private lateinit var projectManager: ProjectManager

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val mockContext = org.mockito.Mockito.mock(android.content.Context::class.java)
        projectManager = ProjectManager(mockContext)
        val project = Project()
        projectManager.currentProject = project
        val scene = Scene()
        project.addScene(scene)
        projectManager.currentlyPlayingScene = scene
        sprite = Sprite("testSprite")
        destinationSprite = Sprite("destinationSprite")
        scene.addSprite(sprite)
        scene.addSprite(destinationSprite)
        ScreenValues.setToDefaultScreenSize()
        action = sprite.actionFactory.createGlideToPositionAction(
            sprite, destinationSprite,
            SequenceAction(), Formula(2.0F), BrickValues.GLIDE_TO_OTHER_SPRITE_POSITION
        ) as GlideToOtherSpritePositionAction
    }

    @Test
    fun testGlideToOtherSpritePositionAction() {
        destinationSprite.look.xInUserInterfaceDimensionUnit = DESTINATION_X_POSITION
        destinationSprite.look.yInUserInterfaceDimensionUnit = DESTINATION_Y_POSITION
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f
        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        action.act(2f)
        assertEquals(DESTINATION_X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(DESTINATION_Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testGlideToBehavior() {
        destinationSprite.look.xInUserInterfaceDimensionUnit = DESTINATION_X_POSITION
        destinationSprite.look.yInUserInterfaceDimensionUnit = DESTINATION_Y_POSITION
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f
        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        action.act(1f)
        assertNotEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertNotEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        assertNotEquals(DESTINATION_X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
        assertNotEquals(DESTINATION_Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)
        assertEquals(action.currentXPosition, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(action.currentYPosition, sprite.look.yInUserInterfaceDimensionUnit)
    }
}
