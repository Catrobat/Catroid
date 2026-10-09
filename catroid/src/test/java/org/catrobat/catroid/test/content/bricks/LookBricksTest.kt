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
import org.catrobat.catroid.content.actions.SetNextLookAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CopyLookBrick
import org.catrobat.catroid.content.bricks.DeleteLookBrick
import org.catrobat.catroid.content.bricks.EditLookBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.PaintNewLookBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

@RunWith(JUnit4::class)
class LookBricksTest {

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
    fun testNextLookBrickResource() {
        val brick = NextLookBrick()
        assertEquals(R.layout.brick_next_look, brick.viewResource)
    }

    @Test
    fun testNextLookBrickAddsActionToSequence() {
        val brick = NextLookBrick()
        val mockAction = Mockito.mock(SetNextLookAction::class.java)
        Mockito.`when`(actionFactory.createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))
    }

    @Test
    fun testPreviousLookBrickResource() {
        val brick = PreviousLookBrick()
        assertEquals(R.layout.brick_previous_look, brick.viewResource)
    }

    @Test
    fun testPreviousLookBrickAddsActionToSequence() {
        val brick = PreviousLookBrick()
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createSetPreviousLookAction(eq(sprite), eq(scriptSequenceAction))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetPreviousLookAction(eq(sprite), eq(scriptSequenceAction))
    }

    @Test
    fun testDeleteLookBrickResource() {
        val brick = DeleteLookBrick()
        assertEquals(R.layout.brick_delete_look, brick.viewResource)
    }

    @Test
    fun testDeleteLookBrickAddsActionToSequence() {
        val brick = DeleteLookBrick()
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createDeleteLookAction(eq(sprite))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createDeleteLookAction(eq(sprite))
    }

    @Test
    fun testCopyLookBrickResourceAndField() {
        val brick = CopyLookBrick("look_name")
        assertEquals(R.layout.brick_copy_look, brick.viewResource)
        assertEquals("look_name", brick.getFormulaWithBrickField(Brick.BrickField.LOOK_COPY).interpretString(scope))
    }

    @Test
    fun testCopyLookBrickAddsActionToSequence() {
        val brick = CopyLookBrick("look_copy")
        val mockNextLookAction = Mockito.mock(SetNextLookAction::class.java)
        val mockCopyAction = Mockito.mock(Action::class.java)

        Mockito.`when`(actionFactory.createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))).thenReturn(mockNextLookAction)
        Mockito.`when`(actionFactory.createCopyLookAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java), eq(mockNextLookAction))).thenReturn(mockCopyAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))
        Mockito.verify(actionFactory).createCopyLookAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java), eq(mockNextLookAction))
    }

    @Test
    fun testEditLookBrickResource() {
        val brick = EditLookBrick()
        assertEquals(R.layout.brick_edit_look, brick.viewResource)
    }

    @Test
    fun testEditLookBrickAddsActionToSequence() {
        val brick = EditLookBrick()
        val mockNextLookAction = Mockito.mock(SetNextLookAction::class.java)
        val mockEditAction = Mockito.mock(Action::class.java)

        Mockito.`when`(actionFactory.createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))).thenReturn(mockNextLookAction)
        Mockito.`when`(actionFactory.createEditLookAction(eq(sprite), eq(scriptSequenceAction), eq(mockNextLookAction))).thenReturn(mockEditAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))
        Mockito.verify(actionFactory).createEditLookAction(eq(sprite), eq(scriptSequenceAction), eq(mockNextLookAction))
    }

    @Test
    fun testLookRequestBrickResourceAndField() {
        val brick = LookRequestBrick("url")
        assertEquals(R.layout.brick_look_request, brick.viewResource)
        assertEquals("url", brick.getFormulaWithBrickField(Brick.BrickField.LOOK_REQUEST).interpretString(scope))

        val resourcesSet = Brick.ResourcesSet()
        brick.addRequiredResources(resourcesSet)
        assertTrue(resourcesSet.contains(Brick.NETWORK_CONNECTION))
    }

    @Test
    fun testLookRequestBrickAddsActionToSequence() {
        val brick = LookRequestBrick(Formula("https://example.com"))
        val mockAction = Mockito.mock(Action::class.java)
        Mockito.`when`(actionFactory.createLookRequestAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))).thenReturn(mockAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createLookRequestAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java))
    }

    @Test
    fun testPaintNewLookBrickResourceAndField() {
        val brick = PaintNewLookBrick("new_look")
        assertEquals(R.layout.brick_paint_new_look, brick.viewResource)
        assertEquals("new_look", brick.getFormulaWithBrickField(Brick.BrickField.LOOK_NEW).interpretString(scope))
    }

    @Test
    fun testPaintNewLookBrickAddsActionToSequence() {
        val brick = PaintNewLookBrick("paint_look")
        val mockNextLookAction = Mockito.mock(SetNextLookAction::class.java)
        val mockPaintAction = Mockito.mock(Action::class.java)

        Mockito.`when`(actionFactory.createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))).thenReturn(mockNextLookAction)
        Mockito.`when`(actionFactory.createPaintNewLookAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java), eq(mockNextLookAction))).thenReturn(mockPaintAction)

        brick.addActionToSequence(sprite, scriptSequenceAction)

        Mockito.verify(actionFactory).createSetNextLookAction(eq(sprite), eq(scriptSequenceAction))
        Mockito.verify(actionFactory).createPaintNewLookAction(eq(sprite), eq(scriptSequenceAction), any(Formula::class.java), eq(mockNextLookAction))
    }
}
