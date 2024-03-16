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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.GlideToTouchPositionAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.utils.TouchUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GlideToTouchPositionActionTest {
    private lateinit var sprite: Sprite
    private lateinit var action: GlideToTouchPositionAction

    private val EXPECTED_X_POSITION = 20f
    private val EXPECTED_Y_POSITION = 25f

    private val _duration = 225f
    var duration = Formula(_duration)

    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        action = sprite.actionFactory.createGlideToTouchAction(
            sprite, SequenceAction(), duration) as GlideToTouchPositionAction
    }

    @Test
    fun testGlideToTouchPositionAction() {
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f

        TouchUtil.setDummyTouchForTest(EXPECTED_X_POSITION, EXPECTED_Y_POSITION)

        Assert.assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)

        var currentTimeDelta = System.currentTimeMillis()
        var step = 0
        do {
            currentTimeDelta = System.currentTimeMillis() - currentTimeDelta
            Assert.assertNotSame(
                action.getXPosition(), sprite.look
                    .xInUserInterfaceDimensionUnit
            )
            Assert.assertNotSame(
                action.getYPosition(), sprite.look
                    .yInUserInterfaceDimensionUnit
            )
            step++
        } while (!action.act(currentTimeDelta.toFloat()))

        Assert.assertEquals(action.getXPosition(), sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(action.getYPosition(), sprite.look.yInUserInterfaceDimensionUnit)
        Assert.assertEquals(2, step)
    }

    @Test(expected = NullPointerException::class)
    fun testNullActor() {
        sprite.actionFactory.createGlideToTouchAction(
            null, SequenceAction(), duration
        ).act(1.0f)
    }
}
