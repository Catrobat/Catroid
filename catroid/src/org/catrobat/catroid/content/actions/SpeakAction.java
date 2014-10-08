/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class SpeakAction extends TemporalAction {

	private String text;
	private String hashText;

	private File speechFile;
	private final OnUtteranceCompletedListener listener = new OnUtteranceCompletedListener() {

		@Override
		public void onUtteranceCompleted(String utteranceId) {
			SoundManager.getInstance().playSoundFile(speechFile.getAbsolutePath());
		}
	};

	@Override
	protected void update(float delta) {
		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, hashText);
		PreStageActivity.textToSpeech(text, speechFile, listener, speakParameter);
	}

	public void setText(String text) {
		if (text == null) {
			text = "";
		}
		this.text = text;

		hashText = Utils.md5Checksum(text);
		String fileName = hashText;
		File pathToSpeechFile = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		pathToSpeechFile.mkdirs();
		speechFile = new File(pathToSpeechFile, fileName + Constants.TEXT_TO_SPEECH_EXTENSION);
	}
}
