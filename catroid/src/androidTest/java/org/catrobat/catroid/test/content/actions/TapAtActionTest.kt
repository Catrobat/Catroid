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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.TapAtAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.utils.TouchUtil
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TapAtActionTest {
    private lateinit var sprite: Sprite
    private var project: Project? = null
    private lateinit var action: TapAtAction

    private val xTapPosition = 111f
    private val yTapPosition = 333f
    private var xPosition: Formula = Formula(xTapPosition)
    private var yPosition: Formula = Formula(yTapPosition)
    private var notNumericalString: Formula = Formula("NOT_NUMERICAL_STRING")

    @Before
    fun setUp() {
        TestUtils.deleteProjects()

        project = Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME)
        sprite = Sprite("TestSprite1")
        project!!.defaultScene.addSprite(sprite)

        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testCreateAction() {
        val factory = ActionFactory()
        action = factory.createTapAtAction(
            sprite, SequenceAction(), xPosition, yPosition) as TapAtAction

        Assert.assertThat(action, CoreMatchers.instanceOf(TapAtAction::class.java))
    }

    @Test
    fun testActionCallsStage() {
        val factory = ActionFactory()
        action = factory.createTapAtAction(
            sprite, SequenceAction(), xPosition, yPosition) as TapAtAction

        TouchUtil.reset()
        action.act(1.0f)

        Assert.assertEquals(1, TouchUtil.getLastTouchIndex())
        Assert.assertEquals(xTapPosition, TouchUtil.getX(1))
        Assert.assertEquals(yTapPosition, TouchUtil.getY(1))
    }

    @Test
    fun testXNotValid() {
        val factory = ActionFactory()
        val action = factory.createTapAtAction(Sprite(), SequenceAction(), notNumericalString, yPosition)

        TouchUtil.reset()
        action.act(1.0f)

        Assert.assertEquals(0, TouchUtil.getLastTouchIndex())
    }

    @Test
    fun testYNotValid() {
        val factory = ActionFactory()
        val action = factory.createTapAtAction(Sprite(), SequenceAction(), xPosition, notNumericalString)

        TouchUtil.reset()
        action.act(1.0f)

        Assert.assertEquals(0, TouchUtil.getLastTouchIndex())
    }
}
