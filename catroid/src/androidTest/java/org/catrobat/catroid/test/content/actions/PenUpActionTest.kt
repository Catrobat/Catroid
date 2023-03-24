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

import org.junit.runner.RunWith
import org.junit.rules.ExpectedException
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.content.actions.PenUpActionTest
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.content.ActionFactory
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import android.graphics.PointF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.lang.Exception
import java.lang.NullPointerException

@RunWith(AndroidJUnit4::class)
class PenUpActionTest {
    @get:Rule
    val exception = ExpectedException.none()
    private val xMovement = Formula(X_MOVEMENT)
    private var sprite: Sprite? = null
    private val projectName = "testProject"
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = Sprite("testSprite")
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
        val action = factory.createPenUpAction(null)
        exception.expect(NullPointerException::class.java)
        action.act(1.0f)
    }

    @Test
    fun testSaveMultiplePositionChangesWithPenUpActionBetween() {
        Assert.assertEquals(0f, sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite!!.look.yInUserInterfaceDimensionUnit)
        sprite!!.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        sprite!!.actionFactory.createPenUpAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        sprite!!.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        val positions = sprite!!.penConfiguration.positions
        Assert.assertEquals(0f, positions.first().removeFirst().x)
        Assert.assertEquals(X_MOVEMENT, positions.first().removeFirst().x)
        Assert.assertEquals(X_MOVEMENT, positions.first().removeFirst().x)
        positions.removeFirst()
        Assert.assertEquals(X_MOVEMENT * 2, positions.first().removeFirst().x)
        Assert.assertEquals(X_MOVEMENT * 3, positions.first().removeFirst().x)
    }

    @Test
    fun testMultiplePenUpActions() {
        Assert.assertEquals(0f, sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite!!.look.yInUserInterfaceDimensionUnit)
        sprite!!.actionFactory.createPenDownAction(sprite).act(1.0f)
        sprite!!.actionFactory.createPenUpAction(sprite).act(1.0f)
        sprite!!.actionFactory.createPenUpAction(sprite).act(1.0f)
        val positions = sprite!!.penConfiguration.positions
        Assert.assertEquals(0f, positions.first().removeFirst().x)
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
    }
}