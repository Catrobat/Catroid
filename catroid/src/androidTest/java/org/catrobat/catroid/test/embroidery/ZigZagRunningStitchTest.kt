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
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.embroidery.ZigZagRunningStitch
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class ZigZagRunningStitchTest {
    private var sprite: Sprite? = null
    private var spriteLook: Look? = null
    private var zigZagRunningStitch: ZigZagRunningStitch? = null
    private var embroideryPatternManager: EmbroideryPatternManager? = null
    @Before
    fun setUp() {
        sprite = Mockito.mock(Sprite::class.java)
        spriteLook = Mockito.mock(Look::class.java)
        sprite?.look = spriteLook
        embroideryPatternManager = Mockito.mock(
            EmbroideryPatternManager::class.java
        )
        StageActivity.stageListener = Mockito.mock(StageListener::class.java)
        StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager
        val length = 5
        val width = 10
        zigZagRunningStitch = ZigZagRunningStitch(sprite, length.toFloat(), width.toFloat())
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testNoMoveOfRunningStitch() {
        zigZagRunningStitch!!.update(0f, 0f)
        Mockito.verify(embroideryPatternManager, Mockito.times(0))
            ?.addStitchCommand(ArgumentMatchers.any())
    }

    @Test
    fun testSimpleMoveOfRunningStitch() {
        zigZagRunningStitch!!.update(10f, 10f)
        Mockito.verify(embroideryPatternManager, Mockito.times(3))
            ?.addStitchCommand(ArgumentMatchers.any())
    }

    @Test
    fun testSetStartCoordinates() {
        zigZagRunningStitch!!.setStartCoordinates(10f, 10f)
        zigZagRunningStitch!!.update(0f, 0f)
        Mockito.verify(embroideryPatternManager, Mockito.times(3))
            ?.addStitchCommand(ArgumentMatchers.any())
    }
}
