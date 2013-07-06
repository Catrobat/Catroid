/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.soundrecorder;

import java.io.IOException;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SoundRecorderActivity extends SherlockFragmentActivity implements OnClickListener {

	/**
	 * 
	 */
	private static final String TAG = SoundRecorderActivity.class.getSimpleName();
	private SoundRecorder soundRecorder;
	private Chronometer timeRecorderChronometer;
	private ImageButton recordButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_soundrecorder);

		recordButton = (ImageButton) findViewById(R.id.soundrecorder_record_button);
		timeRecorderChronometer = (Chronometer) findViewById(R.id.soundrecorder_chronometer_time_recorded);
		recordButton.setOnClickListener(this);
		Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this);
	}

	// Code from Stackoverflow to reduce memory problems
	// onDestroy() and unbindDrawables() methods taken from
	// http://stackoverflow.com/a/6779067
	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindDrawables(findViewById(R.id.soundrecorder));
		System.gc();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	public void onClick(View view) {
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
			String recordPath = Utils.buildPath(Constants.TMP_PATH, getString(R.string.soundrecorder_recorded_filename)
					+ Constants.RECORDING_EXTENTION);
			soundRecorder = new SoundRecorder(recordPath);
			soundRecorder.start();
			setViewsToRecordingState();
		} catch (IOException e) {
			Log.e(TAG, "Error recording sound.", e);
			Toast.makeText(this, R.string.soundrecorder_error, Toast.LENGTH_SHORT).show();
		} catch (IllegalStateException e) {
			// app would crash if other app uses mic, catch IllegalStateException and display Toast
			Log.e(TAG, "Error recording sound (Other recorder running?).", e);
			Toast.makeText(this, R.string.soundrecorder_error, Toast.LENGTH_SHORT).show();
		}
	}

	private void setViewsToRecordingState() {
		recordButton.setImageResource(R.drawable.microphone_icon_active);
	}

	private synchronized void stopRecording() {
		if (soundRecorder == null || !soundRecorder.isRecording()) {
			return;
		}
		setViewsToNotRecordingState();
		try {
			soundRecorder.stop();
			Uri uri = soundRecorder.getPath();
			setResult(Activity.RESULT_OK, new Intent(Intent.ACTION_PICK, uri));
		} catch (IOException e) {
			Log.e("CATROID", "Error recording sound.", e);
			Toast.makeText(this, R.string.soundrecorder_error, Toast.LENGTH_SHORT).show();
			setResult(Activity.RESULT_CANCELED);
		}
	}

	private void setViewsToNotRecordingState() {
		recordButton.setImageResource(R.drawable.microphone_icon);
	}

}
