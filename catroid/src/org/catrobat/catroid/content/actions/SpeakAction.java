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
package org.catrobat.catroid.content.actions;

import java.util.HashMap;

import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.stage.PreStageActivity;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class SpeakAction extends TemporalAction {

	private static final String LOG_TAG = SpeakBrick.class.getSimpleName();
	private static HashMap<String, SpeakBrick> activeSpeakBricks = new HashMap<String, SpeakBrick>();
	private String text;
	private SpeakBrick speakBrick;

	@Override
	protected void update(float percent) {
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
		activeSpeakBricks.put(utteranceId, speakBrick);

		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);

		long time = System.currentTimeMillis();
		PreStageActivity.textToSpeech(text, listener, speakParameter);
		try {
			this.wait();
		} catch (InterruptedException e) {
			// nothing to do
		}
		Log.i(LOG_TAG, "speak Time: " + (System.currentTimeMillis() - time));
		activeSpeakBricks.remove(utteranceId);

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public SpeakBrick getSpeakBrick() {
		return speakBrick;
	}

	public void setSpeakBrick(SpeakBrick speakBrick) {
		this.speakBrick = speakBrick;
	}

}
