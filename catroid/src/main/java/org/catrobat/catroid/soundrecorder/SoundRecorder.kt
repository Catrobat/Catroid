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
package org.catrobat.catroid.soundrecorder

import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.IOException

class SoundRecorder @JvmOverloads constructor(
    val path: String,
    private val recorder: MediaRecorder = MediaRecorder()
) {
    var isRecording: Boolean = false
        private set

    val maxAmplitude: Int
        get() = recorder.maxAmplitude

    companion object {
        private val TAG: String = SoundRecorder::class.java.simpleName
    }

    @Throws(IOException::class, RuntimeException::class)
    fun start() {
        val soundFile = File(path)
        if (soundFile.exists() && !soundFile.delete()) {
            throw IOException("Could not delete existing file at $path")
        }
        val directory = soundFile.parentFile
        if (directory == null || (!directory.exists() && !directory.mkdirs())) {
            throw IOException("Path to file could not be created.")
        }

        try {
            recorder.reset()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setOutputFile(path)
            recorder.prepare()
            recorder.start()
            isRecording = true
        } catch (e: IllegalStateException) {
            throw e
        } catch (e: RuntimeException) {
            throw e
        }
    }

    @Throws(IOException::class)
    fun stop() {
        try {
            recorder.stop()
        } catch (_: RuntimeException) {
            Log.d(
                TAG, ("Note that a RuntimeException is intentionally "
                    + "thrown to the application, if no valid audio/video data "
                    + "has been received when stop() is called. This happens if stop() "
                    + "is called immediately after start(). The failure lets the application "
                    + "take action accordingly to clean up the output file "
                    + "(delete the output file, for instance), since the output file "
                    + "is not properly constructed when this happens.")
            )
        }
        recorder.reset()
        recorder.release()
        isRecording = false
    }
}
