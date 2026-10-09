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

package org.catrobat.catroid.test.content.bricks

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

@RunWith(JUnit4::class)
class GraphicEffectBricksTest {

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var actionFactory: ActionFactory
    private lateinit var scriptSequenceAction: ScriptSequenceAction
    private lateinit var scope: Scope

    @Before
    fun setUp() {
        project = Project(MockUtil.mockContextForProject(), "Project")
        val scene = Scene("Scene", project)
        sprite = Sprite("Sprite")
        scene.addSprite(sprite)
        project.addScene(scene)

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = Scene()
        ProjectManager.getInstance().currentlyPlayingScene = scene

        actionFactory = Mockito.mock(ActionFactory::class.java)
        sprite.actionFactory = actionFactory
        scriptSequenceAction = ScriptSequenceAction(Mockito.mock(Script::class.java))
        scope = Scope(project, sprite, scriptSequenceAction)
    }

    @Test
    fun testChangeBrightnessByNBrickResourceAndField() {
        val brick = ChangeBrightnessByNBrick(25.0)
        assertEquals(R.layout.brick_change_brightness, brick.viewResource)
        assertEquals("25", brick.getFormulaWithBrickField(Brick.BrickField.BRIGHTNESS_CHANGE).interpretString(scope))
    }

    @Test
    fun testChangeBrightnessByNBrickAddsActionToSequence() {
        val brick = ChangeBrightnessByNBrick(10.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createChangeBrightnessByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createChangeBrightnessByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testSetBrightnessBrickResourceAndField() {
        val brick = SetBrightnessBrick(50.0)
        assertEquals(R.layout.brick_set_brightness, brick.viewResource)
        assertEquals("50", brick.getFormulaWithBrickField(Brick.BrickField.BRIGHTNESS).interpretString(scope))
    }

    @Test
    fun testSetBrightnessBrickAddsActionToSequence() {
        val brick = SetBrightnessBrick(50.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createSetBrightnessAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetBrightnessAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testChangeColorByNBrickResourceAndField() {
        val brick = ChangeColorByNBrick(15.0)
        assertEquals(R.layout.brick_change_color_by, brick.viewResource)
        assertEquals("15", brick.getFormulaWithBrickField(Brick.BrickField.COLOR_CHANGE).interpretString(scope))
    }

    @Test
    fun testChangeColorByNBrickAddsActionToSequence() {
        val brick = ChangeColorByNBrick(15.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createChangeColorByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createChangeColorByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testSetColorBrickResourceAndField() {
        val brick = SetColorBrick(80.0)
        assertEquals(R.layout.brick_set_color_to, brick.viewResource)
        assertEquals("80", brick.getFormulaWithBrickField(Brick.BrickField.COLOR).interpretString(scope))
    }

    @Test
    fun testSetColorBrickAddsActionToSequence() {
        val brick = SetColorBrick(80.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createSetColorAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetColorAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testChangeSizeByNBrickResourceAndField() {
        val brick = ChangeSizeByNBrick(10.0)
        assertEquals(R.layout.brick_change_size_by_n, brick.viewResource)
        assertEquals("10", brick.getFormulaWithBrickField(Brick.BrickField.SIZE_CHANGE).interpretString(scope))
    }

    @Test
    fun testChangeSizeByNBrickAddsActionToSequence() {
        val brick = ChangeSizeByNBrick(10.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createChangeSizeByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createChangeSizeByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testSetSizeToBrickResourceAndField() {
        val brick = SetSizeToBrick(100.0)
        assertEquals(R.layout.brick_set_size_to, brick.viewResource)
        assertEquals("100", brick.getFormulaWithBrickField(Brick.BrickField.SIZE).interpretString(scope))
    }

    @Test
    fun testSetSizeToBrickAddsActionToSequence() {
        val brick = SetSizeToBrick(100.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createSetSizeToAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetSizeToAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testChangeTransparencyByNBrickResourceAndField() {
        val brick = ChangeTransparencyByNBrick(20.0)
        assertEquals(R.layout.brick_change_transparency, brick.viewResource)
        assertEquals("20", brick.getFormulaWithBrickField(Brick.BrickField.TRANSPARENCY_CHANGE).interpretString(scope))
    }

    @Test
    fun testChangeTransparencyByNBrickAddsActionToSequence() {
        val brick = ChangeTransparencyByNBrick(20.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createChangeTransparencyByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createChangeTransparencyByNAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testSetTransparencyBrickResourceAndField() {
        val brick = SetTransparencyBrick(60.0)
        assertEquals(R.layout.brick_set_transparency, brick.viewResource)
        assertEquals("60", brick.getFormulaWithBrickField(Brick.BrickField.TRANSPARENCY).interpretString(scope))
    }

    @Test
    fun testSetTransparencyBrickAddsActionToSequence() {
        val brick = SetTransparencyBrick(60.0)
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createSetTransparencyAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetTransparencyAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testClearGraphicEffectBrickResource() {
        val brick = ClearGraphicEffectBrick()
        assertEquals(R.layout.brick_clear_graphic_effect, brick.viewResource)
    }

    @Test
    fun testClearGraphicEffectBrickAddsActionToSequence() {
        val brick = ClearGraphicEffectBrick()
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createClearGraphicEffectAction(eq(sprite))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createClearGraphicEffectAction(eq(sprite))
    }
}
