/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.soundrecorder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import static android.Manifest.permission.RECORD_AUDIO;

import static org.catrobat.catroid.common.Constants.SOUND_RECORDER_CACHE_DIR;

public class SoundRecorderActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = SoundRecorderActivity.class.getSimpleName();
	private SoundRecorder soundRecorder;
	private Chronometer timeRecorderChronometer;
	private RecordButton recordButton;
	private static final int REQUEST_PERMISSIONS_RECORD_AUDIO = 401;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_soundrecorder);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.soundrecorder_name);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		recordButton = findViewById(R.id.soundrecorder_record_button);
		timeRecorderChronometer = findViewById(R.id.soundrecorder_chronometer_time_recorded);
		recordButton.setOnClickListener(this);
	}

	@Override
	public void onClick(final View view) {
		new RequiresPermissionTask(REQUEST_PERMISSIONS_RECORD_AUDIO,
				Arrays.asList(RECORD_AUDIO),
				R.string.runtime_permission_general) {
			public void task() {
				if (view.getId() == R.id.soundrecorder_record_button) {
					if (soundRecorder != null && soundRecorder.isRecording()) {
						stopRecording();
						timeRecorderChronometer.stop();
						finish();
					} else {
						startRecording();
						long currentPlayingBase = SystemClock.elapsedRealtime();
						timeRecorderChronometer.setBase(currentPlayingBase);
						timeRecorderChronometer.start();
					}
				}
			}
		}.execute(this);
	}

	@Override
	public void onBackPressed() {
		stopRecording();
		super.onBackPressed();
	}

	private synchronized void startRecording() {
		if (soundRecorder != null && soundRecorder.isRecording()) {
			return;
		}
		try {
			if (soundRecorder != null) {
				soundRecorder.stop();
			}

			SOUND_RECORDER_CACHE_DIR.mkdirs();
			if (!SOUND_RECORDER_CACHE_DIR.isDirectory()) {
				throw new IOException("Cannot create " + SOUND_RECORDER_CACHE_DIR);
			}
			File soundFile = new File(SOUND_RECORDER_CACHE_DIR, getString(R.string.soundrecorder_recorded_filename));
			soundRecorder = new SoundRecorder(soundFile.getAbsolutePath());
			soundRecorder.start();
			setViewsToRecordingState();
		} catch (IOException e) {
			Log.e(TAG, "Error recording sound.", e);
			ToastUtil.showError(this, R.string.soundrecorder_error);
		} catch (IllegalStateException e) {
			Log.e(TAG, "Error recording sound (Other recorder running?).", e);
			ToastUtil.showError(this, R.string.soundrecorder_error);
		} catch (RuntimeException e) {
			Log.e(TAG, "Device does not support audio or video format.", e);
			ToastUtil.showError(this, R.string.soundrecorder_error);
		}
	}

	private void setViewsToRecordingState() {
		recordButton.setState(RecordButton.RecordState.RECORD);
		recordButton.setImageResource(R.drawable.ic_microphone_active);
	}

	private synchronized void stopRecording() {
		if (soundRecorder == null || !soundRecorder.isRecording()) {
			return;
		}
		setViewsToNotRecordingState();
		try {
			soundRecorder.stop();

			Uri uri = FileProvider.getUriForFile(this,
					getApplicationContext().getPackageName() + ".fileProvider",
					new File(soundRecorder.getPath()));
			setResult(AppCompatActivity.RESULT_OK, new Intent(Intent.ACTION_PICK, uri));
		} catch (IOException e) {
			Log.e(TAG, "Error recording sound.", e);
			ToastUtil.showError(this, R.string.soundrecorder_error);
			setResult(AppCompatActivity.RESULT_CANCELED);
		}
	}

	private void setViewsToNotRecordingState() {
		recordButton.setState(RecordButton.RecordState.STOP);
		recordButton.setImageResource(R.drawable.ic_microphone);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
