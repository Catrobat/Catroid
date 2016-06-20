/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.ScratchSearchProjectsListFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScratchConverterActivity extends BaseActivity {

    private static final String TAG = ScratchConverterActivity.class.getSimpleName();

    private ScratchSearchProjectsListFragment scratchSearchProjectsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_converter);
        setUpActionBar();
        scratchSearchProjectsListFragment = (ScratchSearchProjectsListFragment)getFragmentManager().findFragmentById(
                R.id.fragment_scratch_search_projects_list);
        Log.i(TAG, "Scratch Converter Activity created");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_scratch_projects, menu);
        return super.onCreateOptionsMenu(menu);
    }

   	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		handleShowDetails(scratchSearchProjectsListFragment.getShowDetails(),
                menu.findItem(R.id.menu_scratch_projects_show_details));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.menu_scratch_projects_convert:
                Log.d(TAG, "Selected menu item 'convert'");
                scratchSearchProjectsListFragment.startConvertActionMode();
                break;
			case R.id.menu_scratch_projects_show_details:
                Log.d(TAG, "Selected menu item 'Show/Hide details'");
				handleShowDetails(!scratchSearchProjectsListFragment.getShowDetails(), item);
				break;
		}
        return super.onOptionsItemSelected(item);
    }

	private boolean started = false;

	@Override
	protected void onResume() {
		super.onResume();
		/*
		AsyncHttpClient.getDefaultInstance().websocket("ws://scratch2.catrob.at/convertersocket", null, new
				WebSocketConnectCallback() {
			@Override
			public void onCompleted(Exception ex, final WebSocket webSocket) {
				if (ex != null) {
					ex.printStackTrace();
					return;
				}
				Log.d(TAG, "Sending set_client_ID request!");

				final int clientID = 7;
				Map<String, Object> map = new HashMap<String, Object>() {{
					put("cmd", "set_client_ID");
					put("args", new HashMap<String, String>() {{ put("clientID", String.valueOf(clientID)); }});
				}};
				JSONObject jsonObject = new JSONObject(map);
				Log.d(TAG, "Sending: " + jsonObject.toString());
				webSocket.send(jsonObject.toString());
				webSocket.setStringCallback(new WebSocket.StringCallback() {
					public void onStringAvailable(String s) {
						if (! started) {
							Log.d(TAG, "Sending start job!");
							Map<String, Object> map = new HashMap<String, Object>() {{
								put("cmd", "schedule_job");
								put("args", new HashMap<String, String>() {{
									put("clientID", String.valueOf(clientID));
									put("url", "https://scratch.mit.edu/projects/82443924");
									put("force", "1");
								}});
							}};
							JSONObject jsonObject = new JSONObject(map);
							String message = jsonObject.toString().replace("\\", "");
							Log.d(TAG, "Sending: " + message);
							webSocket.send(message);
							started = true;
						}
						Log.d(TAG, s);
					}
				});
		*/
				/*
				webSocket.setDataCallback(new DataCallback() {
					public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
						if (! started) {
							Log.d(TAG, "Sending start job!");
							webSocket.send("{'cmd':'schedule_job','args':{'clientID':'7','url':" +
									"'https://scratch.mit.edu/projects/82443924/','force':True}}");
							started = true;
						}
						Log.d(TAG, "I got some bytes!");
						// note that this data has been read
						byteBufferList.recycle();
					}
				});
				*/
		/*	}
		}); */
	}

	private void setUpActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.title_activity_scratch_converter);
        actionBar.setHomeButtonEnabled(true);
    }

    private void handleShowDetails(boolean showDetails, MenuItem item) {
        scratchSearchProjectsListFragment.setShowDetails(showDetails);
        item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
    }

	public void displaySpeechRecognizer() {
		// Create an intent that can start the Speech Recognizer activity
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// Start the activity, the intent will be populated with the speech text
		startActivityForResult(intent, SPEECH_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		// This callback is invoked when the Speech Recognizer returns.
		// This is where you process the intent and extract the speech text from the intent.
		if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
			List<String> results = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			scratchSearchProjectsListFragment.searchAndUpdateText(spokenText);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
