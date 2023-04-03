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

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.stage.CameraPositioner
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class PenDownActionTest {
    @get:Rule
    val exception = ExpectedException.none()
    private val xMovement = Formula(X_MOVEMENT)
    private val yMovement = Formula(Y_MOVEMENT)
    private lateinit var sprite: Sprite
    private val camera = Mockito.spy(OrthographicCamera())
    private val cameraPositioner = CameraPositioner(camera, 960.0f, 540.0f)
    private val projectName = "testProject"
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = Sprite("testSprite")
        Mockito.doNothing().`when`(camera).update()
        createTestProject()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun testNullSprite() {
        val factory = ActionFactory()
        val action = factory.createPenDownAction(null)
        exception.expect(NullPointerException::class.java)
        action.act(1.0f)
    }

    @Test
    fun testSaveOnePositionChange() {
        Assert.assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        sprite.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        val positions = sprite.penConfiguration.positions
        Assert.assertEquals(0f, positions.first().removeFirst().x)
        Assert.assertEquals(X_MOVEMENT, positions.first().removeFirst().x)
    }

    @Test
    fun testSimultaneousPositionChangeXY() {
        Assert.assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        sprite.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite.actionFactory.createPlaceAtAction(
            sprite, SequenceAction(), xMovement,
            yMovement
        ).act(1.0f)
        val positions = sprite.penConfiguration.positions
        Assert.assertEquals(0f, positions.first().removeFirst().x)
        Assert.assertEquals(X_MOVEMENT, positions.first().first().x)
        Assert.assertEquals(Y_MOVEMENT, positions.first().removeFirst().y)
    }

    @Test
    fun testPositionChangeX() {
        Assert.assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        sprite.actionFactory.createPlaceAtAction(
            sprite, SequenceAction(), Formula(0f),
            Formula(0f)
        ).act(1.0f)
        sprite.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite.actionFactory.createPlaceAtAction(
            sprite, SequenceAction(), xMovement,
            Formula(0f)
        ).act(1.0f)
        val positions = sprite.penConfiguration.positions
        Assert.assertEquals(0f, positions.first().removeFirst().x)
        Assert.assertEquals(X_MOVEMENT, positions.first().first().x)
        Assert.assertEquals(0f, positions.first().removeFirst().y)
    }

    @Test
    fun testAfterBecomeFocusPoint() {
        Assert.assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        cameraPositioner.horizontalFlex = 0f
        cameraPositioner.verticalFlex = 0f
        cameraPositioner.spriteToFocusOn = sprite
        sprite.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite.actionFactory.createPlaceAtAction(
            sprite, SequenceAction(), xMovement,
            yMovement
        ).act(1.0f)
        cameraPositioner.updateCameraPositionForFocusedSprite()
        val positions = sprite.penConfiguration.positions
        Assert.assertEquals(0f, positions.first().removeFirst().x)
        Assert.assertEquals(X_MOVEMENT, positions.first().first().x)
        Assert.assertEquals(Y_MOVEMENT, positions.first().removeFirst().y)
        cameraPositioner.reset()
    }

    @Test
    fun testMultiplePenDownActions() {
        Assert.assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        sprite.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite.actionFactory.createPenDownAction(sprite).act(1.0f)
        val positions = sprite.penConfiguration.positions
        Assert.assertEquals(0f, positions.first().removeFirst().x)
        Assert.assertTrue(positions.first().isEmpty)
    }

    private fun createTestProject() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
    }

    companion object {
        private const val X_MOVEMENT = 100.0f
        private const val Y_MOVEMENT = 50.0f
    }
}