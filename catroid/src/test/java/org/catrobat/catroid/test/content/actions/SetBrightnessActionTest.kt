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
import org.junit.runners.JUnit4
import org.junit.rules.ExpectedException
import org.catrobat.catroid.formulaeditor.Formula
import org.junit.Before
import org.catrobat.catroid.ProjectManager
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import kotlin.NullPointerException

@RunWith(JUnit4::class)
class SetBrightnessActionTest {

    @Rule
    @JvmField
    val exception = ExpectedException.none()
    private val brightness = Formula(BRIGHTNESS)
    private var sprite: Sprite? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val project = Project(MockUtil.mockContextForProject(), "Project")
        sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testBrightnessEffect() {
        Assert.assertEquals(100f, sprite?.look?.brightnessInUserInterfaceDimensionUnit)
        sprite?.actionFactory?.createSetBrightnessAction(sprite, SequenceAction(), brightness)?.act(1.0f)
        Assert.assertEquals(BRIGHTNESS, sprite?.look?.brightnessInUserInterfaceDimensionUnit)
    }

    @Test(expected = NullPointerException::class)
    fun testNullSprite() {
        val action = sprite?.actionFactory?.createSetBrightnessAction(
            null,
            null, brightness
        )
        action?.act(1.0f)
    }

    @Test
    fun testNegativeBrightnessValue() {
        sprite?.actionFactory?.createSetBrightnessAction(
            sprite, SequenceAction(),
            Formula(-BRIGHTNESS)
        )?.act(1.0f)
        Assert.assertEquals(0f, sprite?.look?.brightnessInUserInterfaceDimensionUnit)
    }

    @Test
    fun testBrickWithStringFormula() {
        sprite?.actionFactory?.createSetBrightnessAction(sprite, SequenceAction(), Formula(BRIGHTNESS.toString()))?.act(1.0f)
        Assert.assertEquals(BRIGHTNESS, sprite?.look?.brightnessInUserInterfaceDimensionUnit)
        sprite?.actionFactory?.createSetBrightnessAction(sprite, SequenceAction(), Formula(NOT_NUMERICAL_STRING))?.act(1.0f)
        Assert.assertEquals(BRIGHTNESS, sprite?.look?.brightnessInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNullFormula() {
        sprite?.actionFactory?.createSetBrightnessAction(sprite, SequenceAction(), null)?.act(1.0f)
        Assert.assertEquals(0f, sprite?.look?.brightnessInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNotANumberFormula() {
        sprite?.actionFactory?.createSetBrightnessAction(
            sprite, SequenceAction(),
            Formula(Double.NaN)
        )?.act(1.0f)
        Assert.assertEquals(100f, sprite?.look?.brightnessInUserInterfaceDimensionUnit)
    }

    companion object {
        private const val BRIGHTNESS = 91f
        private const val NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING"
    }
}
