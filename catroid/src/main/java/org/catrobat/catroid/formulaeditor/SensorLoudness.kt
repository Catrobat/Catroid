/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.soundrecorder.SoundRecorder
import java.io.IOException
import java.util.ArrayList

class SensorLoudness(private var recorder: SoundRecorder = SoundRecorder()) {
    private val listenerList: MutableList<SensorCustomEventListener> = ArrayList()
    private var lastValue = 0.0
    private var coroutine: Job? = null

    @JvmOverloads
    fun runLoudnessSensorAsync(
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        dispatcherOnFinished: CoroutineDispatcher = Dispatchers.Main
    ) {
        coroutine = scope.launch {
            while (isActive) {
                val loudness = SCALE_RANGE / MAX_AMP_VALUE * recorder.maxAmplitude
                if (loudness != lastValue && loudness != 0.0) {
                    lastValue = loudness
                    val event = SensorCustomEvent(Sensors.LOUDNESS, loudness)
                    withContext(dispatcherOnFinished) {
                        for (listener in listenerList) {
                            listener.onCustomSensorChanged(event)
                        }
                    }
                    delay(UPDATE_INTERVAL.toLong())
                }
            }
        }
    }

    @Synchronized
    fun registerListener(listener: SensorCustomEventListener): Boolean {
        if (listenerList.contains(listener)) return true
        listenerList.add(listener)
        if (!recorder.isRecording) {
            try {
                recorder.start()
                runLoudnessSensorAsync()
            } catch (ioException: IOException) {
                Log.d(TAG, "Could not start recorder", ioException)
                listenerList.remove(listener)
                recorder = SoundRecorder()
                return false
            } catch (e: IllegalStateException) {
                Log.d(TAG, "Could not start recorder", e)
                listenerList.remove(listener)
                recorder = SoundRecorder()
                return false
            }
        }
        return true
    }

    @Synchronized
    fun unregisterListener(listener: SensorCustomEventListener?) {
        if (!listenerList.contains(listener)) return
        listenerList.remove(listener)
        coroutine?.cancel()
        if (listenerList.size == 0) {
            if (recorder.isRecording) {
                try {
                    recorder.stop()
                } catch (ioException: IOException) {
                    // ignored, nothing we can do
                    Log.d(TAG, "Could not stop recorder", ioException)
                }
                recorder = SoundRecorder()
            }
            lastValue = 0.0
        }
    }

    companion object {
        private const val UPDATE_INTERVAL: Int = 50
        private const val SCALE_RANGE: Double = 100.0
        private const val MAX_AMP_VALUE: Double = 32_767.0
        private val TAG = SensorLoudness.toString()
    }
}
