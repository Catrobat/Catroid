/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.stage;

import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ContextThemeWrapper;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.bricks.Brick;

import java.io.File;
import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;

public final class TextToSpeechHolder {

	private static final String TAG = TextToSpeechHolder.class.getSimpleName();

	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

	private static TextToSpeechHolder instance;

	private TextToSpeechHolder() {
	}

	public static TextToSpeechHolder getInstance() {
		if (instance == null) {
			instance = new TextToSpeechHolder();
		}
		return instance;
	}

	public void initTextToSpeech(final StageActivity stageActivity, final StageResourceHolder stageResourceHolder) {
		textToSpeech = new TextToSpeech(stageActivity, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					onUtteranceCompletedListenerContainer = new OnUtteranceCompletedListenerContainer();
					textToSpeech.setOnUtteranceCompletedListener(onUtteranceCompletedListenerContainer);
					stageResourceHolder.resourceInitialized();
				} else {
					AlertDialog.Builder builder =
							new AlertDialog.Builder(new ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog));
					builder.setMessage(R.string.prestage_text_to_speech_engine_not_installed).setCancelable(false)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									Intent installIntent = new Intent();
									installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
									stageActivity.startActivity(installIntent);
									stageResourceHolder.resourceFailed(Brick.TEXT_TO_SPEECH);
								}
							})
							.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									stageResourceHolder.resourceFailed(Brick.TEXT_TO_SPEECH);
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});
	}

	public void shutDownTextToSpeech() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
	}

	public void textToSpeech(String text, File speechFile, TextToSpeech.OnUtteranceCompletedListener listener,
									HashMap<String, String> speakParameter) {
		if (text == null) {
			text = "";
		}

		if (onUtteranceCompletedListenerContainer.addOnUtteranceCompletedListener(speechFile, listener,
				speakParameter.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))) {
			int status = textToSpeech.synthesizeToFile(text, speakParameter, speechFile.getAbsolutePath());
			if (status == TextToSpeech.ERROR) {
				Log.e(TAG, "File synthesizing failed");
			}
		}
	}

	public void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		if (pathToSpeechFiles.isDirectory()) {
			for (File file : pathToSpeechFiles.listFiles()) {
				file.delete();
			}
		}
	}
}
