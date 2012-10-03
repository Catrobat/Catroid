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
package at.tugraz.ist.catroid.content.bricks;

import java.util.HashMap;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dialogs.BrickTextDialog;

public class SpeakBrick implements Brick {
	private static final String LOG_TAG = SpeakBrick.class.getSimpleName();
	private static final long serialVersionUID = 1L;

	private static HashMap<String, SpeakBrick> activeSpeakBricks = new HashMap<String, SpeakBrick>();
	private Sprite sprite;
	private String text = "";

	private transient View view;

	public SpeakBrick(Sprite sprite, String text) {
		this.sprite = sprite;
		this.text = text;
	}

	public SpeakBrick() {

	}

	@Override
	public int getRequiredResources() {
		return TEXT_TO_SPEECH;
	}

	@Override
	public synchronized void execute() {

		OnUtteranceCompletedListener listener = new OnUtteranceCompletedListener() {
			@Override
			public void onUtteranceCompleted(String utteranceId) {
				SpeakBrick speakBrick = activeSpeakBricks.get(utteranceId);
				if (speakBrick == null) {
					return;
				}
				synchronized (speakBrick) {
					speakBrick.notifyAll();
				}
			}
		};

		String utteranceId = this.hashCode() + "";
		activeSpeakBricks.put(utteranceId, this);

		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);

		long time = System.currentTimeMillis();
		PreStageActivity.textToSpeech(getText(), listener, speakParameter);
		try {
			this.wait();
		} catch (InterruptedException e) {
			// nothing to do
		}
		Log.i(LOG_TAG, "speak Time: " + (System.currentTimeMillis() - time));
		activeSpeakBricks.remove(utteranceId);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public String getText() {
		return text;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_speak, null);

		TextView textHolder = (TextView) view.findViewById(R.id.brick_speak_text_view);
		EditText editText = (EditText) view.findViewById(R.id.brick_speak_edit_text);
		editText.setText(text);

		textHolder.setVisibility(View.GONE);
		editText.setVisibility(View.VISIBLE);

		editText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ScriptTabActivity activity = (ScriptTabActivity) context;

				BrickTextDialog editDialog = new BrickTextDialog() {
					@Override
					protected void initialize() {
						input.setText(text);
						input.setSelectAllOnFocus(true);
					}

					@Override
					protected boolean handleOkButton() {
						text = (input.getText().toString()).trim();
						return true;
					}
				};

				editDialog.show(activity.getSupportFragmentManager(), "dialog_speak_brick");
			}
		});
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_speak, null);
	}

	@Override
	public Brick clone() {
		return new SpeakBrick(this.sprite, this.text);
	}
}
