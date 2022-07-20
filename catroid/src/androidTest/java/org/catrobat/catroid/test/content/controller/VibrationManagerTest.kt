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

package org.catrobat.catroid.test.content.controller

import android.os.Vibrator
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.catrobat.catroid.utils.VibrationManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class VibrationManagerTest {
    private lateinit var vibrator: Vibrator
    private lateinit var vibrationManager: VibrationManager
    private val duration: Long = 1000

    @Before
    fun init() {
        vibrator = Mockito.mock(Vibrator::class.java)
        doNothing().`when`(vibrator).vibrate(anyLong())
        doNothing().`when`(vibrator).cancel()
        vibrationManager = VibrationManager()
        vibrationManager.vibration = vibrator
    }

    @Test
    fun testVibrateForDuration() {
        vibrationManager.vibrateFor(duration)
        assert(vibrationManager.hasActiveVibration())

        vibrationManager.vibrateFor(duration + 1)
        verify(vibrator, times(1)).cancel()
        verify(vibrator, times(2)).vibrate(anyLong())
    }

    @Test
    fun testPauseVibration() {
        vibrationManager.vibrateFor(duration)
        assert(vibrationManager.hasActiveVibration())

        vibrationManager.pause()
        assertFalse(vibrationManager.hasActiveVibration())

        vibrationManager.pause()
        assertEquals(0, vibrationManager.startTime)
        assertEquals(0, vibrationManager.timeToVibrate)
        verify(vibrator, times(1)).cancel()
    }

    @Test
    fun testResumeVibration() {
        vibrationManager.vibrateFor(duration)
        assert(vibrationManager.hasActiveVibration())

        vibrationManager.pause()
        assertFalse(vibrationManager.hasActiveVibration())

        vibrationManager.resume()
        assert(vibrationManager.hasActiveVibration())
        verify(vibrator, times(2)).vibrate(anyLong())
    }
}
