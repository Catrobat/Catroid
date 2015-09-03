/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class SpeakAction extends TemporalAction {

	private Formula text;
	private Object interpretedText;
	private String hashText;
	private Sprite sprite;

	private File speechFile;
	private OnUtteranceCompletedListener listener;

	@Override
	protected void begin() {
		try {
			interpretedText = text == null ? "" : text.interpretString(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			interpretedText = "";
		}

		boolean isFirstLevelStringTree = false;
		if (text != null && text.getRoot().getElementType() == FormulaElement.ElementType.STRING) {
			isFirstLevelStringTree = true;
		}

		if (!isFirstLevelStringTree) {
			try {
				if (interpretedText instanceof String) {
					Double doubleValue = Double.valueOf((String) interpretedText);
					if (doubleValue.isNaN()) {
						interpretedText = "";
					}
				}
			} catch (NumberFormatException numberFormatException) {
				Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatException);
			}
		}

		hashText = Utils.md5Checksum(String.valueOf(interpretedText));
		String fileName = hashText;
		File pathToSpeechFile = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		pathToSpeechFile.mkdirs();
		speechFile = new File(pathToSpeechFile, fileName + Constants.SOUND_STANDARD_EXTENSION);
		listener = new OnUtteranceCompletedListener() {
			@Override
			public void onUtteranceCompleted(String utteranceId) {
				SoundManager.getInstance().playSoundFile(speechFile.getAbsolutePath());
			}
		};
		super.begin();
	}

	@Override
	protected void update(float delta) {
		HashMap<String, String> speakParameter = new HashMap<String, String>();
		speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, hashText);
		PreStageActivity.textToSpeech(String.valueOf(interpretedText), speechFile, listener, speakParameter);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setText(Formula text) {
		this.text = text;
	}
}
