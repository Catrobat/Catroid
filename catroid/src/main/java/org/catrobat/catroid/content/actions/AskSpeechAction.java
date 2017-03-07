/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;

import java.util.ArrayList;

public class AskSpeechAction extends Action implements StageActivity.IntentListener {

	private static final String TAG = "AskSpeechAction";
	private Sprite sprite;
	private Formula questionFormula;
	private UserVariable answerVariable;

	private boolean questionAsked = false;
	private boolean answerReceived = false;

	private void askQuestion() {
		if (StageActivity.messageHandler == null) {
			return;
		}
		ArrayList<Object> params = new ArrayList<>();
		params.add(this);
		Message message = StageActivity.messageHandler.obtainMessage(StageActivity.REGISTER_INTENT, params);
		message.sendToTarget();

		questionAsked = true;
	}

	private Intent createRecognitionIntent(String question) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		if (question != null && question.length() != 0) {
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, question);
		}
		return intent;
	}

	public void setAnswerText(String answer) {
		if (answerVariable == null) {
			return;
		}
		answerVariable.setValue(answer);

		answerReceived = true;
	}

	public void setAnswerVariable(UserVariable answerVariable) {
		if (answerVariable == null) {
			return;
		}
		this.answerVariable = answerVariable;
	}

	public void setQuestionFormula(Formula questionFormula) {
		this.questionFormula = questionFormula;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public boolean act(float delta) {
		if (!questionAsked) {
			askQuestion();
		}

		return answerReceived;
	}

	@Override
	public void restart() {
		questionAsked = false;
		answerReceived = false;
		super.restart();
	}

	@Override
	public Intent getTargetIntent() {
		String question = "";
		try {
			if (questionFormula != null) {
				question = questionFormula.interpretString(sprite);
			}
		} catch (InterpretationException e) {
			Log.e(getClass().getSimpleName(), "formula interpretation in ask brick failed");
		}
		return createRecognitionIntent(question);
	}

	@Override
	public void onIntentResult(int resultCode, Intent data) {
		ArrayList<String> matches;
		switch (resultCode) {
			case Activity.RESULT_OK:
				matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				Log.d(TAG, "Results Speechrecognition: " + matches.toString());
				if (matches != null && matches.size() > 0) {
					setAnswerText(matches.get(0));
				} else {
					setAnswerText("");
				}
				break;
			case Activity.RESULT_CANCELED:
			case Activity.RESULT_FIRST_USER:
				setAnswerText(""); //User canceled action
				break;
			default:
				Log.e(TAG, "unhandeld speech recognizer resultCode " + resultCode);
		}
	}
}
