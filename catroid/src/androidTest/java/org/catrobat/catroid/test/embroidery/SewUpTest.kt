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
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class SewUpTest {
    private var sprite: Sprite? = null
    private var spriteLook: Look? = null
    private var embroideryPatternManager: EmbroideryPatternManager? = null
    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        spriteLook = Mockito.mock(Look::class.java)
        sprite!!.look = spriteLook
        embroideryPatternManager = DSTPatternManager()
        StageActivity.stageListener = StageListener()
        StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testVerticalSewUp() {
        sprite!!.actionFactory.createSewUpAction(sprite).act(1f)
        val stitches = StageActivity.stageListener.embroideryPatternManager.embroideryPatternList
        val expectedStitchesX = ArrayList<Float>()
        val expectedStitchesY = ArrayList<Float>()
        val x = sprite!!.look.xInUserInterfaceDimensionUnit
        val y = sprite!!.look.yInUserInterfaceDimensionUnit
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
            Assert.assertEquals(stitches[i].x.toDouble(), expectedStitchesX[i].toDouble(), 0.01)
            Assert.assertEquals(stitches[i].y.toDouble(), expectedStitchesY[i].toDouble(), 0.01)
        }
    }

    @Test
    fun testAngledSewUp() {
        sprite!!.look.motionDirectionInUserInterfaceDimensionUnit = 137f
        sprite!!.actionFactory.createSewUpAction(sprite).act(1f)
        val stitches = StageActivity.stageListener.embroideryPatternManager.embroideryPatternList
        val expectedStitchesX = ArrayList<Float>()
        val expectedStitchesY = ArrayList<Float>()
        val x = sprite!!.look.xInUserInterfaceDimensionUnit
        val y = sprite!!.look.yInUserInterfaceDimensionUnit
        val radians =
            Math.toRadians(sprite!!.look.motionDirectionInUserInterfaceDimensionUnit.toDouble())
        expectedStitchesX.add(x)
        expectedStitchesY.add(y)
        expectedStitchesX.add(x + SewUpAction.STEPS * Math.sin(radians).toFloat())
        expectedStitchesY.add(y + SewUpAction.STEPS * Math.cos(radians).toFloat())
        expectedStitchesX.add(x)
        expectedStitchesY.add(y)
        expectedStitchesX.add(x - SewUpAction.STEPS * Math.sin(radians).toFloat())
        expectedStitchesY.add(y - SewUpAction.STEPS * Math.cos(radians).toFloat())
        expectedStitchesX.add(x)
        expectedStitchesY.add(y)
        for (i in expectedStitchesX.indices) {
            Assert.assertEquals(stitches[i].x.toDouble(), expectedStitchesX[i].toDouble(), 0.01)
            Assert.assertEquals(stitches[i].y.toDouble(), expectedStitchesY[i].toDouble(), 0.01)
        }
    }
}
