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
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.soundrecorder.SoundRecorder
import java.io.File
import java.io.IOException

class SensorLoudness {
    private val listenerList = mutableListOf<SensorCustomEventListener>()
    private val handler = Handler()
    private val soundRecorderFactory: SoundRecorderFactory
    private val recorderPath: String
    private var lastValue = 0.0
    private var recorder: SoundRecorder

    @VisibleForTesting
    fun interface SoundRecorderFactory {
        fun create(path: String): SoundRecorder
    }

    constructor() : this(SoundRecorderFactory(::defaultSoundRecorderFactory), defaultRecorderPath())

    @VisibleForTesting
    internal constructor(soundRecorderFactory: SoundRecorderFactory) : this(soundRecorderFactory, defaultRecorderPath())

    @VisibleForTesting
    internal constructor(soundRecorderFactory: SoundRecorderFactory, recorderPath: String) {
        this.soundRecorderFactory = soundRecorderFactory
        this.recorderPath = recorderPath
        recorder = createSoundRecorder()
    }

    @VisibleForTesting
    var statusChecker: Runnable = Runnable {
        val loudness = (SCALE_RANGE / MAX_AMP_VALUE) * recorder.maxAmplitude
        if (loudness != lastValue && loudness != 0.0) {
            lastValue = loudness
            val event = SensorCustomEvent(Sensors.LOUDNESS, loudness)
            listenerList.forEach { it.onCustomSensorChanged(event) }
        }
        handler.postDelayed(statusChecker, UPDATE_INTERVAL.toLong())
    }

    @Synchronized
    @Suppress("TooGenericExceptionCaught")
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
                cleanupRecorderFile()
                recorder = createSoundRecorder()
                return false
            } catch (runtimeException: RuntimeException) {
                Log.d(TAG, "Could not start recorder", runtimeException)
                listenerList.remove(listener)
                cleanupRecorderFile()
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
                cleanupRecorderFile()
                recorder = createSoundRecorder()
            }
            lastValue = 0.0
        }
    }

    private fun cleanupRecorderFile() {
        val recorderFile = File(recorderPath)
        if (recorderFile.exists() && !recorderFile.delete()) {
            Log.d(TAG, "Could not delete recorder file $recorderPath")
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
        private const val MAX_AMP_VALUE = 32_767.0
        private const val TAG = "SensorLoudness"

        private fun defaultSoundRecorderFactory(path: String): SoundRecorder = SoundRecorder(path)

        @VisibleForTesting
        @JvmStatic
        internal fun defaultRecorderPath(): String =
            File(Constants.SOUND_RECORDER_CACHE_DIRECTORY, LOUDNESS_SENSOR_RECORDING_FILE_NAME).absolutePath

        private const val LOUDNESS_SENSOR_RECORDING_FILE_NAME = "loudness_sensor.m4a"
    }
}
