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
package org.catrobat.catroid.test.io

import org.junit.runner.RunWith
import org.catrobat.catroid.io.StageAudioFocus
import org.junit.Before
import kotlin.Throws
import androidx.test.core.app.ApplicationProvider
import android.media.AudioManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.junit.After
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class StageAudioFocusTest {
    private var audioFocus: StageAudioFocus? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        audioFocus = StageAudioFocus(ApplicationProvider.getApplicationContext())
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        audioFocus = null
    }

    @Test
    fun testRequestAndReleaseAudioFocus() {
        Assert.assertFalse(audioFocus!!.isAudioFocusGranted)
        audioFocus!!.requestAudioFocus()
        Assert.assertTrue(audioFocus!!.isAudioFocusGranted)
        audioFocus!!.releaseAudioFocus()
        Assert.assertFalse(audioFocus!!.isAudioFocusGranted)
    }

    @Test
    fun testIfAudioFocusGetsAbandonedOnAudioFocusLossEvent() {
        audioFocus!!.requestAudioFocus()
        Assert.assertTrue(audioFocus!!.isAudioFocusGranted)
        audioFocus!!.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS)
        Assert.assertFalse(audioFocus!!.isAudioFocusGranted)
    }
}
