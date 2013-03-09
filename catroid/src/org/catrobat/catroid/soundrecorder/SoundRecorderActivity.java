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

import org.catrobat.catroid.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SoundRecorderActivity extends SherlockFragmentActivity implements OnClickListener {

	//	private SoundRecorder soundRecorder;
	//	private ImageView recordButton;
	//	private TextView recordText;
	//	private LinearLayout recordLayout;
	//	private Chronometer timeRecorderChronometer;
	//	private TextView recordingIndicationText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_soundrecorder);

		//		recordLayout = (LinearLayout) findViewById(R.id.soundrecorder_linearlayout_record);
		//		recordButton = (ImageView) findViewById(R.id.soundrecorder_imageview_record);
		//		recordText = (TextView) findViewById(R.id.soundrecorder_textview_record_start_stop);
		//		recordingIndicationText = (TextView) findViewById(R.id.soundrecorder_textview_recording_hint);
		//		timeRecorderChronometer = (Chronometer) findViewById(R.id.soundrecorder_chronometer_time_recorded);
		//		recordLayout.setOnClickListener(this);
		//		Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this);
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
	public void onClick(View v) {
		//		if (v.getId() == R.id.soundrecorder_linearlayout_record) {
		//			if (soundRecorder != null && soundRecorder.isRecording()) {
		//				stopRecording();
		//				timeRecorderChronometer.stop();
		//				finish();
		//			} else {
		//				startRecording();
		//				long currentPlayingBase = SystemClock.elapsedRealtime();
		//				timeRecorderChronometer.setBase(currentPlayingBase);
		//				timeRecorderChronometer.start();
		//			}
		//		}
	}

	@Override
	public void onBackPressed() {
		//		stopRecording();
		super.onBackPressed();
	}

	//	private synchronized void startRecording() {
	//		if (soundRecorder != null && soundRecorder.isRecording()) {
	//			return;
	//		}
	//		try {
	//			String recordPath = Utils.buildPath(Constants.TMP_PATH, getString(R.string.soundrecorder_recorded_filename)
	//					+ Constants.RECORDING_EXTENTION);
	//			soundRecorder = new SoundRecorder(recordPath);
	//			soundRecorder.start();
	//			setViewsToRecordingState();
	//		} catch (IOException e) {
	//			Log.e("CATROID", "Error recording sound.", e);
	//			Toast.makeText(this, R.string.soundrecorder_error, Toast.LENGTH_SHORT).show();
	//		}
	//	}
	//
	//	private void setViewsToRecordingState() {
	//		recordButton.setImageResource(R.drawable.ic_record);
	//		recordText.setText(R.string.soundrecorder_record_stop);
	//		recordingIndicationText.setVisibility(View.VISIBLE);
	//	}
	//
	//	private synchronized void stopRecording() {
	//		if (soundRecorder == null || !soundRecorder.isRecording()) {
	//			return;
	//		}
	//		setViewsToNotRecordingState();
	//		try {
	//			soundRecorder.stop();
	//			Uri uri = soundRecorder.getPath();
	//			setResult(Activity.RESULT_OK, new Intent(Intent.ACTION_PICK, uri));
	//		} catch (IOException e) {
	//			Log.e("CATROID", "Error recording sound.", e);
	//			Toast.makeText(this, R.string.soundrecorder_error, Toast.LENGTH_SHORT).show();
	//			setResult(Activity.RESULT_CANCELED);
	//		}
	//	}
	//
	//	private void setViewsToNotRecordingState() {
	//		recordButton.setImageResource(R.drawable.ic_record_inactive);
	//		recordText.setText(R.string.soundrecorder_record_start);
	//		recordingIndicationText.setVisibility(View.INVISIBLE);
	//	}

}
