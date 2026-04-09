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

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.PlotArcAction
import org.catrobat.catroid.content.bricks.ArcBrick
import org.catrobat.catroid.embroidery.DSTPatternManager
import org.catrobat.catroid.embroidery.SimpleRunningStitch
import org.catrobat.catroid.embroidery.StitchPoint
import org.catrobat.catroid.embroidery.TripleRunningStitch
import org.catrobat.catroid.embroidery.ZigZagRunningStitch
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class EmbroideryArcActionTest {
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
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testHalfArcProducesStitchesOffHorizontalAxis() {
        val stitchPoints = executeArcWithSimpleStitch(180f)

        assertTrue("Expected embroidery output for a 180 degree arc", embroideryPatternManager.validPatternExists())
        assertTrue("Expected multiple stitches for a 180 degree arc", stitchPoints.size > 2)
        assertTrue("Expected at least one stitch with a vertical offset", containsPointOffHorizontalAxis(stitchPoints))
    }

    @Test
    fun testFullArcProducesEmbroideryOutput() {
        val stitchPoints = executeArcWithSimpleStitch(360f)

        assertTrue("Expected embroidery output for a 360 degree arc", embroideryPatternManager.validPatternExists())
        assertFalse("Expected a non-empty stitch list for a 360 degree arc", stitchPoints.isEmpty())
        assertTrue(
            "Expected the full arc to contain stitches away from the horizontal axis",
            containsPointOffHorizontalAxis(stitchPoints)
        )
    }

    @Test
    fun testTripleStitchArcProducesStitchesOffHorizontalAxis() {
        val stitchPoints = executeArcWithTripleStitch(180f)

        assertTrue("Expected embroidery output for a triple-stitch arc", embroideryPatternManager.validPatternExists())
        assertTrue("Expected multiple triple-stitch points for an arc", stitchPoints.size > 4)
        assertTrue("Expected triple stitch points away from the horizontal axis", containsPointOffHorizontalAxis(stitchPoints))
    }

    @Test
    fun testZigZagStitchArcProducesStitchesOffHorizontalAxis() {
        val stitchPoints = executeArcWithZigZagStitch(180f)

        assertTrue("Expected embroidery output for a zig-zag arc", embroideryPatternManager.validPatternExists())
        assertTrue("Expected multiple zig-zag stitch points for an arc", stitchPoints.size > 2)
        assertTrue("Expected zig-zag stitch points away from the horizontal axis", containsPointOffHorizontalAxis(stitchPoints))
    }

    @Test
    fun testArcUpdatesMotionDirectionForCurvedMovement() {
        executePlotArc(ArcBrick.Directions.RIGHT, RADIUS.toFloat(), 180f)

        assertEquals(
            "Expected the sprite to end a right 180 degree arc facing left",
            -90f,
            sprite.look.motionDirectionInUserInterfaceDimensionUnit,
            DELTA
        )
    }

    @Test
    fun testLeftArcRespectsInitialMotionDirection() {
        sprite.look.motionDirectionInUserInterfaceDimensionUnit = 90f

        executePlotArc(ArcBrick.Directions.LEFT, 200f, 30f)

        assertEquals(100f, sprite.look.xInUserInterfaceDimensionUnit, DELTA)
        assertEquals(26.8f, sprite.look.yInUserInterfaceDimensionUnit, DELTA)
        assertEquals(60f, sprite.look.motionDirectionInUserInterfaceDimensionUnit, DELTA)
    }

    @Test
    fun testRightArcRespectsInitialMotionDirection() {
        sprite.look.motionDirectionInUserInterfaceDimensionUnit = 90f

        executePlotArc(ArcBrick.Directions.RIGHT, 200f, 30f)

        assertEquals(100f, sprite.look.xInUserInterfaceDimensionUnit, DELTA)
        assertEquals(-26.8f, sprite.look.yInUserInterfaceDimensionUnit, DELTA)
        assertEquals(120f, sprite.look.motionDirectionInUserInterfaceDimensionUnit, DELTA)
    }

    @Test
    fun testArcRespectsArbitraryInitialMotionDirection() {
        sprite.look.motionDirectionInUserInterfaceDimensionUnit = 10f

        executePlotArc(ArcBrick.Directions.LEFT, 200f, 30f)

        assertEquals(
            "Expected the sprite to keep turning relative to its initial motion direction",
            -20f,
            sprite.look.motionDirectionInUserInterfaceDimensionUnit,
            DELTA
        )
    }

    @Test
    fun testNegativeLeftAngleBehavesLikePositiveRightAngle() {
        sprite.look.motionDirectionInUserInterfaceDimensionUnit = 90f

        executePlotArc(ArcBrick.Directions.LEFT, 200f, -30f)

        assertEquals(100f, sprite.look.xInUserInterfaceDimensionUnit, DELTA)
        assertEquals(-26.8f, sprite.look.yInUserInterfaceDimensionUnit, DELTA)
        assertEquals(120f, sprite.look.motionDirectionInUserInterfaceDimensionUnit, DELTA)
    }

    @Test
    fun testNegativeRightAngleBehavesLikePositiveLeftAngle() {
        sprite.look.motionDirectionInUserInterfaceDimensionUnit = 90f

        executePlotArc(ArcBrick.Directions.RIGHT, 200f, -30f)

        assertEquals(100f, sprite.look.xInUserInterfaceDimensionUnit, DELTA)
        assertEquals(26.8f, sprite.look.yInUserInterfaceDimensionUnit, DELTA)
        assertEquals(60f, sprite.look.motionDirectionInUserInterfaceDimensionUnit, DELTA)
    }

    private fun executeArcWithSimpleStitch(degrees: Float): ArrayList<StitchPoint> {
        sprite.runningStitch.activateStitching(sprite, SimpleRunningStitch(sprite, STITCH_LENGTH))
        executePlotArc(ArcBrick.Directions.RIGHT, RADIUS.toFloat(), degrees)
        return embroideryPatternManager.embroideryPatternList
    }

    private fun executeArcWithTripleStitch(degrees: Float): ArrayList<StitchPoint> {
        sprite.runningStitch.activateStitching(sprite, TripleRunningStitch(sprite, STITCH_LENGTH))
        executePlotArc(ArcBrick.Directions.RIGHT, RADIUS.toFloat(), degrees)
        return embroideryPatternManager.embroideryPatternList
    }

    private fun executeArcWithZigZagStitch(degrees: Float): ArrayList<StitchPoint> {
        sprite.runningStitch.activateStitching(sprite, ZigZagRunningStitch(sprite, STITCH_LENGTH.toFloat(), 20f))
        executePlotArc(ArcBrick.Directions.RIGHT, RADIUS.toFloat(), degrees)
        return embroideryPatternManager.embroideryPatternList
    }

    private fun executePlotArc(direction: ArcBrick.Directions, radius: Float, degrees: Float) {
        val plotArcAction = PlotArcAction()
        plotArcAction.setScope(Scope(Project(), sprite, SequenceAction()))
        plotArcAction.setDirection(direction)
        plotArcAction.radius = Formula(radius)
        plotArcAction.degrees = Formula(degrees)
        plotArcAction.duration = 0f
        plotArcAction.act(1f)
    }

    private fun containsPointOffHorizontalAxis(stitchPoints: ArrayList<StitchPoint>): Boolean =
        stitchPoints.any { kotlin.math.abs(it.y) > 0.1f }

    companion object {
        private const val STITCH_LENGTH = 10
        private const val RADIUS = 100
        private const val DELTA = 2f
    }
}
