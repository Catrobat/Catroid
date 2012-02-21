/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.soundrecorder;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.Utils;

public class SoundRecorderActivity extends Activity implements OnClickListener {
	private static final String TAG = SoundRecorderActivity.class.getSimpleName();

	private SoundRecorder soundRecorder;
	private ImageView recordButton;
	private TextView recordText;
	private LinearLayout recordLayout;

	private TextView recordingIndicationText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_soundrecorder);

		recordLayout = (LinearLayout) findViewById(R.id.recordLayout);
		recordButton = (ImageView) findViewById(R.id.recordButton);
		recordText = (TextView) findViewById(R.id.recordText);
		recordingIndicationText = (TextView) findViewById(R.id.recording);

		recordLayout.setOnClickListener(this);

		soundRecorder = (SoundRecorder) getLastNonConfigurationInstance();
		if (soundRecorder != null && soundRecorder.isRecording()) {
			setViewsToRecordingState();
		}

		Utils.checkForSdCard(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.recordLayout) {
			if (soundRecorder != null && soundRecorder.isRecording()) {
				stopRecording();
				finish();
			} else {
				startRecording();
			}
		}
	}

	@Override
	public void onBackPressed() {
		stopRecording();
		super.onBackPressed();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return soundRecorder;
	}

	private synchronized void startRecording() {
		if (soundRecorder != null && soundRecorder.isRecording()) {
			return;
		}
		try {
			String recordPath = Utils.buildPath(Consts.TMP_PATH, getString(R.string.soundrecorder_recorded_filename)
					+ Consts.RECORDING_EXTENTION);
			soundRecorder = new SoundRecorder(recordPath);
			soundRecorder.start();
			setViewsToRecordingState();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.soundrecorder_error, Toast.LENGTH_SHORT).show();
		}
	}

	private void setViewsToRecordingState() {
		recordButton.setImageResource(R.drawable.ic_record);
		recordText.setText(R.string.soundrecorder_record_stop);
		recordingIndicationText.setVisibility(View.VISIBLE);
	}

	private synchronized void stopRecording() {
		if (soundRecorder == null || !soundRecorder.isRecording()) {
			return;
		}
		setViewsToNotRecordingState();
		try {
			soundRecorder.stop();
			Uri uri = soundRecorder.getPath();
			Log.i(TAG, "uri from record file:" + uri);
			setResult(Activity.RESULT_OK, new Intent(Intent.ACTION_PICK, uri));
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.soundrecorder_error, Toast.LENGTH_SHORT).show();
			setResult(Activity.RESULT_CANCELED);
		}
	}

	private void setViewsToNotRecordingState() {
		recordButton.setImageResource(R.drawable.ic_record_inactive);
		recordText.setText(R.string.soundrecorder_record_start);
		recordingIndicationText.setVisibility(View.INVISIBLE);
	}

}
