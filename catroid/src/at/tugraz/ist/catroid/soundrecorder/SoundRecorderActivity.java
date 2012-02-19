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
import at.tugraz.ist.catroid.R;

public class SoundRecorderActivity extends Activity implements OnClickListener {
	private static final String TAG = SoundRecorderActivity.class.getSimpleName();

	private SoundRecorder soundRecorder;
	private ImageView recordButton;
	private TextView recordText;
	private LinearLayout recordLayout;

	private TextView recordingIndicationText;

	private boolean isRecording = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_soundrecorder);

		recordLayout = (LinearLayout) findViewById(R.id.recordLayout);
		recordButton = (ImageView) findViewById(R.id.recordButton);
		recordText = (TextView) findViewById(R.id.recordText);
		recordingIndicationText = (TextView) findViewById(R.id.recording);

		recordLayout.setOnClickListener(this);
	}

	public synchronized void onClick(View v) {
		switch (v.getId()) {
			case R.id.recordLayout:
				if (isRecording) {
					stopRecording();
					isRecording = false;
				} else {
					startRecording();
					isRecording = true;
				}
				break;
		}
	}

	private void startRecording() {
		recordButton.setImageResource(R.drawable.ic_record);
		recordText.setText(R.string.soundrecorder_record_stop);
		recordingIndicationText.setVisibility(View.VISIBLE);
		try {
			soundRecorder = new SoundRecorder("catroid/soundrecorder/mytestfile");
			soundRecorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		if (soundRecorder == null) {
			return;
		}
		recordButton.setImageResource(R.drawable.ic_record_inactive);
		recordText.setText(R.string.soundrecorder_record_start);
		recordingIndicationText.setVisibility(View.INVISIBLE);
		try {
			soundRecorder.stop();
			Uri uri = soundRecorder.getPath();
			Log.i(TAG, "uri from record file:" + uri);
			setResult(Activity.RESULT_OK, new Intent(Intent.ACTION_PICK, uri));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setResult(Activity.RESULT_CANCELED);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//stopRecording();
	}

}
