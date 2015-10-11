/**
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2013 The Catrobat Team
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
package org.catrobat.catroid.content.actions;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;

import java.util.ArrayList;
import java.util.Locale;

public class AskAction extends Action implements StageActivity.IntentListener {

	private Formula question;
	private Sprite sprite;
	private boolean pendingResult = false;
	private String answer = null;
	private boolean gotAnswer = false;
	private UserVariable userVariable;

	private static String TAG = "SPEECH_TO_TEXT_ACTION";

	public void setQuestion(Sprite sprite, Formula question) {
		this.question = question;
		this.sprite = sprite;
	}

	public void setVariable(UserVariable uv) {
		userVariable = uv;
	}

	@Override
	public boolean act(float delta) {
		if (!pendingResult) {
			StageActivity.queueIntent(this);

			pendingResult = true;
		}
		if (gotAnswer) {
			Log.d("AskAction", "Anser will be set to " + answer);
			if (answer != null) {
				userVariable.setValue(answer);
			} else {
				userVariable.setValue("");
			}

			pendingResult = false;
			gotAnswer = false;
			return true;
		}
		return false;
	}

	private Intent createRecognitionIntent(String question) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale
				.getDefault());
		if (question != null && question.length() != 0) {
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, question);
		}
		return intent;
	}

	@Override
	public void onIntentResult(int resultCode, Intent data) {
		ArrayList<String> matches;
		switch (resultCode) {
			case Activity.RESULT_OK:
				matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				Log.d(TAG, "Results Speechrecognition: " + matches.toString());
				if (matches != null && matches.size() > 0) {
					answer = matches.get(0);
				} else {
					answer = null;
				}
				break;
			case Activity.RESULT_CANCELED:
			case Activity.RESULT_FIRST_USER:
				answer = null; //User canceled action
				break;
			default:
				Log.e(TAG, "unhandeld speech recognizer resultCode " + resultCode);
		}
		gotAnswer = true;
	}

	@Override
	public Intent getTargetIntent() {

		String interpretedText = "";
		try {
			interpretedText = question == null ? "" : question.interpretString(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			interpretedText = "";
		}

		return createRecognitionIntent(interpretedText);
	}
}