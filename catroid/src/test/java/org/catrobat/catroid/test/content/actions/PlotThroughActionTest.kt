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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.PlotThroughAction
import org.catrobat.catroid.embroidery.DSTPatternManager
import org.catrobat.catroid.embroidery.ZigZagRunningStitch
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.math.abs
import kotlin.math.atan2

class PlotThroughActionTest {
    private lateinit var sprite: Sprite
    private lateinit var embroideryPatternManager: DSTPatternManager

    @Before
    fun setUp() {
        val parentGroup = Group()
        sprite = Sprite("sprite")
        parentGroup.addActor(sprite.look)

        embroideryPatternManager = DSTPatternManager()
        StageActivity.stageListener = Mockito.mock(StageListener::class.java)
        StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager

        sprite.look.setPositionInUserInterfaceDimensionUnit(START_X, START_Y)
        sprite.look.motionDirectionInUserInterfaceDimensionUnit = START_DIRECTION
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testGoThroughEndsFacingAlongBezierTangent() {
        executePlotThrough(THROUGH_X, THROUGH_Y, END_X, END_Y)

        assertEquals(END_X, sprite.look.xInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(END_Y, sprite.look.yInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(expectedEndDirection(), sprite.look.motionDirectionInUserInterfaceDimensionUnit, DIRECTION_DELTA)
    }

    @Test
    fun testGoThroughUpdatesDirectionForZigZagEmbroidery() {
        val addedPoints = mutableListOf<Pair<Float, Float>>()
        val zigZagRunningStitch = ZigZagRunningStitch(sprite, 1f, STITCH_WIDTH)
        zigZagRunningStitch.setListener { x, y -> addedPoints.add(x to y) }
        sprite.runningStitch.activateStitching(sprite, zigZagRunningStitch)

        executePlotThrough(THROUGH_X, THROUGH_Y, END_X, END_Y)

        assertTrue("Expected embroidery output for go through", embroideryPatternManager.validPatternExists())
        assertTrue("Expected zig-zag stitches to be emitted", addedPoints.isNotEmpty())

        val (_, lastY) = addedPoints.last()
        assertTrue(
            "Expected the last zig-zag stitch to use the local curve direction instead of the initial heading",
            abs(lastY - sprite.look.yInUserInterfaceDimensionUnit) > 5f
        )
    }

    private fun executePlotThrough(x1: Float, y1: Float, x2: Float, y2: Float) {
        val plotThroughAction = PlotThroughAction()
        plotThroughAction.setScope(Scope(Project(), sprite, SequenceAction()))
        plotThroughAction.setTargetCoordinates(Formula(x1), Formula(y1), Formula(x2), Formula(y2))
        plotThroughAction.duration = 0f
        plotThroughAction.act(1f)
    }

    private fun expectedEndDirection(): Float {
        val anchorX = (4 * THROUGH_X - START_X - END_X) / 2.0
        val anchorY = (4 * THROUGH_Y - START_Y - END_Y) / 2.0
        return Math.toDegrees(atan2(END_X - anchorX, END_Y - anchorY)).toFloat()
    }

    companion object {
        private const val START_X = 0f
        private const val START_Y = 0f
        private const val START_DIRECTION = 0f
        private const val THROUGH_X = 100f
        private const val THROUGH_Y = 100f
        private const val END_X = 0f
        private const val END_Y = 200f
        private const val STITCH_WIDTH = 20f
        private const val POSITION_DELTA = 0.5f
        private const val DIRECTION_DELTA = 1f
    }
}
