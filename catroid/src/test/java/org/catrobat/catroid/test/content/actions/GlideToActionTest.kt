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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.conditional.GlideToAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Before
import org.junit.Test

class GlideToActionTest {
    private lateinit var sprite: Sprite

    @Before
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
    }

    @Test
    fun testNormalBehavior() {
        val xPosition = Formula(X_POSITION)
        val yPosition = Formula(Y_POSITION)
        val duration = Formula(DURATION)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        sprite.look.width = 100.0f
        sprite.look.height = 50.0f

        val action = sprite.actionFactory.createGlideToAction(sprite, SequenceAction(), xPosition, yPosition, duration)

        runActionUntilFinished(action)
        assertEquals(X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testGlideToBoundaryPositionsImmediate() {
        val glideToMax = sprite.actionFactory.createGlideToAction(
            sprite,
            SequenceAction(),
            Formula(Int.MAX_VALUE),
            Formula(Int.MAX_VALUE),
            Formula(0f)
        )
        runActionUntilFinished(glideToMax)

        assertEquals(Int.MAX_VALUE.toFloat(), sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(Int.MAX_VALUE.toFloat(), sprite.look.yInUserInterfaceDimensionUnit)

        val glideToMin = sprite.actionFactory.createGlideToAction(
            sprite,
            SequenceAction(),
            Formula(Int.MIN_VALUE),
            Formula(Int.MIN_VALUE),
            Formula(0f)
        )
        runActionUntilFinished(glideToMin)

        assertEquals(Int.MIN_VALUE.toFloat(), sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(Int.MIN_VALUE.toFloat(), sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test(expected = NullPointerException::class)
    fun testNullActor() {
        val xPosition = Formula(X_POSITION)
        val yPosition = Formula(Y_POSITION)
        val duration = Formula(DURATION)
        val factory = ActionFactory()
        val action = factory.createGlideToAction(null, SequenceAction(), xPosition, yPosition, duration)
        action.act(1.0f)
    }

    @Test
    fun testBoundaryPositions() {
        val sprite = Sprite("testSprite")
        sprite.actionFactory.createPlaceAtAction(sprite, SequenceAction(), Formula(Int.MAX_VALUE), Formula(
            Int.MAX_VALUE)).act(1.0f)
        assertEquals(Int.MAX_VALUE.toFloat(), sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(Int.MAX_VALUE.toFloat(), sprite.look.yInUserInterfaceDimensionUnit)

        sprite.actionFactory.createPlaceAtAction(sprite, SequenceAction(), Formula(Int.MIN_VALUE), Formula(
            Int.MIN_VALUE)).act(1.0f)
        assertEquals(Int.MIN_VALUE.toFloat(), sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(Int.MIN_VALUE.toFloat(), sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testBrickWithStringFormula() {
        var action = sprite.actionFactory.createGlideToAction(
            sprite, SequenceAction(), Formula(X_POSITION.toString()),
            Formula(Y_POSITION.toString()), Formula(DURATION.toString()))

        runActionUntilFinished(action)
        assertEquals(X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)

        action = sprite.actionFactory.createGlideToAction(sprite, SequenceAction(), Formula(
            NOT_NUMERICAL_STRING), Formula(NOT_NUMERICAL_STRING2), Formula(NOT_NUMERICAL_STRING3))

        runActionUntilFinished(action)
        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNullFormula() {
        val action =
            sprite.actionFactory.createGlideToAction(sprite, SequenceAction(), null, null, null)

        runActionUntilFinished(action)
        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNotANumberFormula() {
        val action = sprite.actionFactory.createGlideToAction(sprite, SequenceAction(), Formula(
            Double.NaN), Formula(Double.NaN), Formula(Double.NaN))

        runActionUntilFinished(action)
        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testDurationChangesAfterRestart() {
        val xPosition = Formula(X_POSITION)
        val yPosition = Formula(Y_POSITION)
        val duration = Formula(DURATION)

        sprite.look.setPositionInUserInterfaceDimensionUnit(0f, 0f)

        val action = sprite.actionFactory.createGlideToAction(sprite, SequenceAction(), xPosition, yPosition, duration
        ) as GlideToAction

        action.act(0f)
        val durationBefore = action.durationValue

        sprite.look.setPositionInUserInterfaceDimensionUnit(10f, 10f)

        action.act(0.5f)
        val durationAfter = action.durationValue

        assertTrue(durationAfter < durationBefore)
    }

    @Test
    fun testInvalidEndFormulaResetsDuration() {
        val action = sprite.actionFactory.createGlideToAction(
            sprite,
            SequenceAction(),
            Formula(NOT_NUMERICAL_STRING),
            Formula(NOT_NUMERICAL_STRING2),
            Formula(DURATION)
        ) as GlideToAction

        action.act(0f)

        assertEquals(0f, action.durationValue)
    }

    private fun runActionUntilFinished(action: com.badlogic.gdx.scenes.scene2d.Action) {
        while (!action.act(1.0f)) {
            // does nothing
        }
    }

    companion object {
        private const val X_POSITION = 12f
        private const val Y_POSITION = 150f
        private const val DURATION = 225f
        private const val NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING"
        private const val NOT_NUMERICAL_STRING2 = "NOT_NUMERICAL_STRING2"
        private const val NOT_NUMERICAL_STRING3 = "NOT_NUMERICAL_STRING3"
    }
}
