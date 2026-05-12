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

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Chronometer
import androidx.core.content.FileProvider
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.SOUND_RECORDER_CACHE_DIRECTORY
import org.catrobat.catroid.ui.BaseActivity
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.catrobat.catroid.utils.ToastUtil
import java.io.File
import java.io.IOException

class SoundRecorderActivity : BaseActivity(), View.OnClickListener {

    private var soundRecorder: SoundRecorder? = null
    private lateinit var timeRecorderChronometer: Chronometer
    private lateinit var recordButton: RecordButton

    companion object {
        private val TAG: String = SoundRecorderActivity::class.java.simpleName
        private const val REQUEST_PERMISSIONS_RECORD_AUDIO = 401
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_soundrecorder)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle(R.string.soundrecorder_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recordButton = findViewById(R.id.soundrecorder_record_button)
        timeRecorderChronometer = findViewById(R.id.soundrecorder_chronometer_time_recorded)
        recordButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        object : RequiresPermissionTask(
            REQUEST_PERMISSIONS_RECORD_AUDIO,
            listOf(RECORD_AUDIO),
            R.string.runtime_permission_general
        ) {
            override fun task() {
                if (view.id == R.id.soundrecorder_record_button) {
                    if (soundRecorder?.isRecording == true) {
                        stopRecording()
                        timeRecorderChronometer.stop()
                        finish()
                    } else {
                        startRecording()
                        val currentPlayingBase = SystemClock.elapsedRealtime()
                        timeRecorderChronometer.base = currentPlayingBase
                        timeRecorderChronometer.start()
                    }
                }
            }
        }.execute(this)
    }

    override fun onBackPressed() {
        stopRecording()
        super.onBackPressed()
    }

    @Synchronized
    private fun startRecording() {
        if (soundRecorder?.isRecording == true) {
            return
        }

        try {
            soundRecorder?.stop()

            SOUND_RECORDER_CACHE_DIRECTORY.mkdirs()
            if (!SOUND_RECORDER_CACHE_DIRECTORY.isDirectory) {
                throw IOException("Cannot create $SOUND_RECORDER_CACHE_DIRECTORY")
            }

            val soundFile = File(
                SOUND_RECORDER_CACHE_DIRECTORY,
                getString(R.string.soundrecorder_recorded_filename)
            )
            soundRecorder = SoundRecorder(soundFile.absolutePath)
            soundRecorder?.start()
            setViewsToRecordingState()
        } catch (e: IOException) {
            Log.e(TAG, "Error recording sound.", e)
            ToastUtil.showError(this, R.string.soundrecorder_error)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error recording sound (Other recorder running?).", e)
            ToastUtil.showError(this, R.string.soundrecorder_error)
        } catch (e: RuntimeException) {
            Log.e(TAG, "Device does not support audio or video format.", e)
            ToastUtil.showError(this, R.string.soundrecorder_error)
        }
    }

    private fun setViewsToRecordingState() {
        recordButton.state = RecordButton.RecordState.RECORD
        recordButton.setImageResource(R.drawable.ic_microphone_active)
    }

    @Synchronized
    private fun stopRecording() {
        val recorderSnapshot = soundRecorder ?: return
        if (!recorderSnapshot.isRecording) {
            return
        }

        setViewsToNotRecordingState()
        try {
            recorderSnapshot.stop()

            val uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileProvider",
                File(recorderSnapshot.path)
            )
            setResult(RESULT_OK, Intent(Intent.ACTION_PICK, uri))
        } catch (e: IOException) {
            Log.e(TAG, "Error recording sound.", e)
            ToastUtil.showError(this, R.string.soundrecorder_error)
            setResult(RESULT_CANCELED)
        }
    }

    private fun setViewsToNotRecordingState() {
        recordButton.state = RecordButton.RecordState.STOP
        recordButton.setImageResource(R.drawable.ic_microphone)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
