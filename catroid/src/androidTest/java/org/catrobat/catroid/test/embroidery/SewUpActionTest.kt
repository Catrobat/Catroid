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

package org.catrobat.catroid.test.embroidery

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SewUpAction
import org.catrobat.catroid.embroidery.DSTPatternManager
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import kotlin.math.cos
import kotlin.math.sin

@RunWith(AndroidJUnit4::class)
class SewUpActionTest {
    private lateinit var sprite: Sprite
    private lateinit var spriteLook: Look
    private lateinit var embroideryPatternManager: EmbroideryPatternManager

    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        spriteLook = mock(Look::class.java)
        sprite.look = spriteLook
        embroideryPatternManager = DSTPatternManager()
        StageActivity.stageListener = StageListener().apply {
            embroideryPatternManager = this@SewUpActionTest.embroideryPatternManager
        }
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testVerticalSewUp() {
        sprite.actionFactory.createSewUpAction(sprite).act(1f)

        val stitches = StageActivity.stageListener?.embroideryPatternManager?.embroideryPatternList
            ?: throw RuntimeException("StageActivity.stageListener or embroideryPatternManager is null")

        val expectedStitchesX = mutableListOf<Float>()
        val expectedStitchesY = mutableListOf<Float>()
        val x = sprite.look?.xInUserInterfaceDimensionUnit ?: 0f
        val y = sprite.look?.yInUserInterfaceDimensionUnit ?: 0f

        expectedStitchesX.add(x)
        expectedStitchesY.add(y)
        expectedStitchesX.add(x)
        expectedStitchesY.add(y + SewUpAction.STEPS)
        expectedStitchesX.add(x)
        expectedStitchesY.add(y)
        expectedStitchesX.add(x)
        expectedStitchesY.add(y - SewUpAction.STEPS)
        expectedStitchesX.add(x)
        expectedStitchesY.add(y)

        for (i in expectedStitchesX.indices) {
            assertEquals(expectedStitchesX[i], stitches[i].x, 0.01f)
            assertEquals(expectedStitchesY[i], stitches[i].y, 0.01f)
        }
    }

    @Test
    fun testAngledSewUp() {
        val look = sprite.look ?: throw RuntimeException("Sprite look is null")
        look.setMotionDirectionInUserInterfaceDimensionUnit(137f)
        sprite.actionFactory.createSewUpAction(sprite).act(1f)

        val stitches = StageActivity.stageListener?.embroideryPatternManager?.embroideryPatternList
            ?: throw RuntimeException("StageActivity.stageListener or embroideryPatternManager is null")

        val expectedStitchesX = mutableListOf<Float>()
        val expectedStitchesY = mutableListOf<Float>()
        val x = look.xInUserInterfaceDimensionUnit
        val y = look.yInUserInterfaceDimensionUnit
        val radians = Math.toRadians(look.motionDirectionInUserInterfaceDimensionUnit.toDouble())

        expectedStitchesX.add(x)
        expectedStitchesY.add(y)
        expectedStitchesX.add(x + SewUpAction.STEPS * sin(radians).toFloat())
        expectedStitchesY.add(y + SewUpAction.STEPS * cos(radians).toFloat())
        expectedStitchesX.add(x)
        expectedStitchesY.add(y)
        expectedStitchesX.add(x - (SewUpAction.STEPS * sin(radians)).toFloat())
        expectedStitchesY.add(y - (SewUpAction.STEPS * cos(radians)).toFloat())
        expectedStitchesX.add(x)
        expectedStitchesY.add(y)

        for (i in expectedStitchesX.indices) {
            assertEquals(expectedStitchesX[i], stitches[i].x, 0.01f)
            assertEquals(expectedStitchesY[i], stitches[i].y, 0.01f)
        }
    }
}
