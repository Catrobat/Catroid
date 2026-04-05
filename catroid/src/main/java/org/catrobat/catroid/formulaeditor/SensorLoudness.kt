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

import android.os.Handler
import android.util.Log
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.soundrecorder.SoundRecorder
import java.io.IOException

class SensorLoudness @JvmOverloads @VisibleForTesting internal constructor(
    private val soundRecorderFactory: SoundRecorderFactory = SoundRecorderFactory(::defaultSoundRecorderFactory),
    private val recorderPath: String = defaultRecorderPath()
) {
    private val listenerList = mutableListOf<SensorCustomEventListener>()
    private val handler = Handler()
    private var lastValue = 0.0
    private var recorder: SoundRecorder = createSoundRecorder()

    @VisibleForTesting
    fun interface SoundRecorderFactory {
        fun create(path: String): SoundRecorder
    }

    @VisibleForTesting
    var statusChecker: Runnable = object : Runnable {
        override fun run() {
            val loudness = (SCALE_RANGE / MAX_AMP_VALUE) * recorder.maxAmplitude
            if (loudness != lastValue && loudness != 0.0) {
                lastValue = loudness
                val event = SensorCustomEvent(Sensors.LOUDNESS, loudness)
                listenerList.forEach { it.onCustomSensorChanged(event) }
            }
            handler.postDelayed(this, UPDATE_INTERVAL.toLong())
        }
    }

    @Synchronized
    fun registerListener(listener: SensorCustomEventListener): Boolean {
        if (listenerList.contains(listener)) {
            return true
        }

        listenerList.add(listener)
        if (!recorder.isRecording) {
            try {
                recorder.start()
                statusChecker.run()
            } catch (ioException: IOException) {
                Log.d(TAG, "Could not start recorder", ioException)
                listenerList.remove(listener)
                recorder = createSoundRecorder()
                return false
            } catch (runtimeException: RuntimeException) {
                Log.d(TAG, "Could not start recorder", runtimeException)
                listenerList.remove(listener)
                recorder = createSoundRecorder()
                return false
            }
        }
        return true
    }

    @Synchronized
    fun unregisterListener(listener: SensorCustomEventListener) {
        if (!listenerList.contains(listener)) {
            return
        }

        listenerList.remove(listener)
        if (listenerList.isEmpty()) {
            handler.removeCallbacks(statusChecker)
            if (recorder.isRecording) {
                try {
                    recorder.stop()
                } catch (ioException: IOException) {
                    Log.d(TAG, "Could not stop recorder", ioException)
                }
                recorder = createSoundRecorder()
            }
            lastValue = 0.0
        }
    }

    private fun createSoundRecorder(): SoundRecorder = soundRecorderFactory.create(recorderPath)

    @VisibleForTesting
    fun setSoundRecorder(soundRecorder: SoundRecorder) {
        recorder = soundRecorder
    }

    @VisibleForTesting
    fun getSoundRecorder(): SoundRecorder = recorder

    companion object {
        private const val UPDATE_INTERVAL = 50
        private const val SCALE_RANGE = 100.0
        private const val MAX_AMP_VALUE = 32767.0
        private const val TAG = "SensorLoudness"

        private fun defaultSoundRecorderFactory(path: String): SoundRecorder = SoundRecorder(path)

        @VisibleForTesting
        @JvmStatic
        internal fun defaultRecorderPath(): String = "/dev/null"
    }
}
