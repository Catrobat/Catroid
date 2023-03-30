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
import org.junit.runner.RunWith
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.RunningStitch
import org.catrobat.catroid.embroidery.RunningStitchType
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class RunningStitchTest {
    private var sprite: Sprite? = null
    private var spriteLook: Look? = null
    private var runningStitch: RunningStitch? = null
    private var runningStitchType: RunningStitchType? = null
    @Before
    fun setUp() {
        spriteLook = Mockito.mock(Look::class.java)
        sprite = Mockito.mock(Sprite::class.java)
        sprite?.look = spriteLook
        runningStitchType = Mockito.mock(RunningStitchType::class.java)
        runningStitch = RunningStitch()
    }

    @Test
    fun testActivateRunningStitch() {
        runningStitch!!.activateStitching(sprite, runningStitchType)
        runningStitch!!.update()
        Mockito.verify(spriteLook, Mockito.times(1))?.xInUserInterfaceDimensionUnit
        Mockito.verify(spriteLook, Mockito.times(1))?.yInUserInterfaceDimensionUnit
        Mockito.verify(runningStitchType, Mockito.times(1))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testInvalidRunningTypeActivateRunningStitch() {
        runningStitch!!.activateStitching(sprite, null)
        runningStitch!!.update()
        Mockito.verify(spriteLook, Mockito.times(0))?.xInUserInterfaceDimensionUnit
        Mockito.verify(spriteLook, Mockito.times(0))?.yInUserInterfaceDimensionUnit
    }

    @Test
    fun testInvalidSpriteActivateRunningStitch() {
        runningStitch!!.activateStitching(null, runningStitchType)
        runningStitch!!.update()
        Mockito.verify(runningStitchType, Mockito.times(0))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testPauseRunningStitch() {
        runningStitch!!.activateStitching(sprite, runningStitchType)
        runningStitch!!.pause()
        runningStitch!!.update()
        Mockito.verify(spriteLook, Mockito.times(0))?.xInUserInterfaceDimensionUnit
        Mockito.verify(spriteLook, Mockito.times(0))?.yInUserInterfaceDimensionUnit
        Mockito.verify(runningStitchType, Mockito.times(0))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testResumeRunningStitch() {
        runningStitch!!.activateStitching(sprite, runningStitchType)
        runningStitch!!.pause()
        runningStitch!!.resume()
        runningStitch!!.update()
        Mockito.verify(spriteLook, Mockito.times(1))?.xInUserInterfaceDimensionUnit
        Mockito.verify(spriteLook, Mockito.times(1))?.yInUserInterfaceDimensionUnit
        Mockito.verify(runningStitchType, Mockito.times(1))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testInvalidResumeRunningStitch() {
        runningStitch!!.resume()
        runningStitch!!.update()
        Mockito.verify(spriteLook, Mockito.times(0))?.xInUserInterfaceDimensionUnit
        Mockito.verify(spriteLook, Mockito.times(0))?.yInUserInterfaceDimensionUnit
        Mockito.verify(runningStitchType, Mockito.times(0))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testSetStartCoordinates() {
        val xCoord = 1f
        val yCoord = 2f
        runningStitch!!.activateStitching(sprite, runningStitchType)
        runningStitch!!.setStartCoordinates(xCoord, yCoord)
        Mockito.verify(runningStitchType, Mockito.times(1))?.setStartCoordinates(xCoord, yCoord)
    }

    @Test
    fun testInvalidSetStartCoordinates() {
        val xCoord = 1f
        val yCoord = 2f
        runningStitch!!.setStartCoordinates(xCoord, yCoord)
        Mockito.verify(runningStitchType, Mockito.times(0))
            ?.setStartCoordinates(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testDeactivateRunningStitch() {
        runningStitch!!.activateStitching(sprite, runningStitchType)
        runningStitch!!.deactivate()
        val xCoord = 1f
        val yCoord = 2f
        runningStitch!!.setStartCoordinates(xCoord, yCoord)
        runningStitch!!.update()
        Mockito.verify(runningStitchType, Mockito.times(0))?.setStartCoordinates(xCoord, yCoord)
        Mockito.verify(spriteLook, Mockito.times(0))?.xInUserInterfaceDimensionUnit
        Mockito.verify(spriteLook, Mockito.times(0))?.yInUserInterfaceDimensionUnit
        Mockito.verify(runningStitchType, Mockito.times(0))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }
}
