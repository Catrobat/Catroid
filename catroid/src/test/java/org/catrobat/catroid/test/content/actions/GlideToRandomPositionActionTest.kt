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

import android.widget.Spinner
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.GlideToRandomPositionAction
import org.catrobat.catroid.formulaeditor.Formula
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.scope.Scope
import org.koin.ext.scope

import kotlin.getValue
import kotlin.time.Duration
import org.koin.java.KoinJavaComponent


@RunWith(JUnit4::class)
class GlideToRandomPositionActionTest {

    private lateinit var sprite: Sprite
    private lateinit var dummySprite: Sprite
    private lateinit var action: GlideToRandomPositionAction
    private lateinit var  projectManager: ProjectManager

    @Before
    @Throws(Exception::class)
    fun SetUp() {
        val mockContext = org.mockito.Mockito.mock(android.content.Context::class.java)
        projectManager = ProjectManager(mockContext)
        val project = Project()
        projectManager.currentProject = project
        val scene = Scene()
        project.addScene(scene)
        projectManager.currentlyPlayingScene = scene
        sprite = Sprite("testSprite")
        dummySprite = Sprite("dummySprite")
        scene.addSprite(sprite)
        ScreenValues.setToDefaultScreenSize()
        action = sprite.actionFactory.createGlideToPositionAction(
            sprite, dummySprite, SequenceAction(), Formula(2.0),
            BrickValues.GLIDE_TO_RANDOM_POSITION
        )
            as GlideToRandomPositionAction
    }

    @Test
    public fun testGlideToRandomPositionDestination() {

        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f

        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)

        action.act(2.0F)

        assertEquals(action.randomYPosition, sprite.look.yInUserInterfaceDimensionUnit)
        assertEquals(action.randomXPosition, sprite.look.xInUserInterfaceDimensionUnit)
    }

    @Test
    public fun testGlideToBehavior(){
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f

        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)

        action.act(1.0f)

        assertNotEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertNotEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        assertNotEquals(action.randomXPosition, sprite.look.xInUserInterfaceDimensionUnit)
        assertNotEquals(action.randomYPosition, sprite.look.yInUserInterfaceDimensionUnit)
        assertEquals(action.currentXPosition, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(action.currentYPosition, sprite.look.yInUserInterfaceDimensionUnit)
    }
}
