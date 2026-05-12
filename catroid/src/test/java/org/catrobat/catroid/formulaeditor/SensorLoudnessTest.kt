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
package org.catrobat.catroid.formulaeditor

import android.os.Build
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.soundrecorder.SoundRecorder
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SensorLoudnessTest {

    @Test
    fun defaultRecorderPathUsesAppCacheFileInsteadOfDevNull() {
        val createdPath = AtomicReference<String>()
        val soundRecorder = mock(SoundRecorder::class.java)

        SensorLoudness(SensorLoudness.SoundRecorderFactory { path ->
            createdPath.set(path)
            soundRecorder
        })

        assertNotNull("Recorder path should be captured when SensorLoudness creates its recorder", createdPath.get())
        assertNotEquals("/dev/null should no longer be used for loudness recording", "/dev/null", createdPath.get())
        assertTrue(
            "Recorder path should live in the sound recorder cache directory",
            createdPath.get().startsWith(Constants.SOUND_RECORDER_CACHE_DIRECTORY.absolutePath + File.separator)
        )
    }

    @Test
    @Throws(IOException::class)
    fun registerListenerDeletesRecorderFileAfterStartFailure() {
        val recorderFile = File.createTempFile("loudness-start-failure", ".m4a")
        val soundRecorder = mock(SoundRecorder::class.java)
        `when`(soundRecorder.isRecording).thenReturn(false)
        doThrow(IOException("Could not start recorder")).`when`(soundRecorder).start()

        val sensorLoudness = SensorLoudness(
            SensorLoudness.SoundRecorderFactory { soundRecorder },
            recorderFile.absolutePath
        )

        val registered = sensorLoudness.registerListener(SensorCustomEventListener { })

        assertFalse("Listener registration should fail when recorder start throws", registered)
        assertFalse("Recorder file should be deleted after a failed start", recorderFile.exists())
    }

    @Test
    @Throws(IOException::class)
    fun unregisterListenerDeletesRecorderFileAfterStop() {
        val recorderFile = File.createTempFile("loudness-stop", ".m4a")
        val soundRecorder = mock(SoundRecorder::class.java)
        `when`(soundRecorder.isRecording).thenReturn(true)

        val sensorLoudness = SensorLoudness(
            SensorLoudness.SoundRecorderFactory { soundRecorder },
            recorderFile.absolutePath
        )
        val listener = SensorCustomEventListener { }

        sensorLoudness.registerListener(listener)
        sensorLoudness.unregisterListener(listener)

        verify(soundRecorder).stop()
        assertFalse("Recorder file should be deleted after unregistering the last listener", recorderFile.exists())
    }
}
