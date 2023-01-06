/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.languagetranslator.LanguageTranslator;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageLifeCycleController;

public class DownloadLanguageModelDialog extends Dialog implements View.OnClickListener {

	private static final String TAG = DownloadLanguageModelDialog.class.getSimpleName();
	private static final int SIZE_MB = 30;
	private final StageActivity stageActivity;
	private LanguageTranslator languageTranslator;

	public DownloadLanguageModelDialog(StageActivity stageActivity, int theme) {
		super(stageActivity, theme);
		this.stageActivity = stageActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_stage_download_language_model);
		getWindow().getAttributes();

		int width = WindowManager.LayoutParams.MATCH_PARENT;
		int height = WindowManager.LayoutParams.WRAP_CONTENT;

		getWindow().setLayout(width, height);
		getWindow().setBackgroundDrawableResource(R.color.transparent);

		findViewById(R.id.stage_dialog_download_language_model_button_maybe_later).setOnClickListener(this);
		findViewById(R.id.stage_dialog_download_language_model_button_download_now).setOnClickListener(this);
		setTextInDialog();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.stage_dialog_download_language_model_button_maybe_later:
				downloadLanguageModelsLatter();
				break;
			case R.id.stage_dialog_download_language_model_button_download_now:
				downloadLanguageModelsNow();
				break;
			default:
				Log.w(TAG, "Unimplemented button clicked! This shouldn't happen!");
				break;
		}
	}

	public void setTextInDialog() {
		String sourceLanguage = "'" + languageTranslator.getSourceLanguage() + "'";
		String targetLanguage = "'" + languageTranslator.getTargetLanguage() + "'";
		String size = String.valueOf(SIZE_MB * (languageTranslator.getModelsToDownload().size()));
		String message = "A translation brick from "
				+ sourceLanguage + " to language " + targetLanguage + " is being executed. "
				+ "To enable this feature, language models must be downloaded from the internet, "
				+ "which may incur additional costs. The size of the additional model or models is "
				+ size + " MB. Do you want to download it/them now?";

		((TextView) findViewById(R.id.stage_dialog_download_language_model)).setText(message);
	}

	public void downloadLanguageModelsNow() {
		Log.i(TAG, "User clicked download now button.");
		downloadLanguageModels(true);
	}

	public void downloadLanguageModelsLatter() {
		Log.i(TAG, "User clicked maybe latter button.");
		downloadLanguageModels(false);
	}

	public void downloadLanguageModels(boolean download) {
		languageTranslator.checkIfDownloadLanguageModels(download);
		dismiss();
		SensorHandler.timerReferenceValue += SystemClock.uptimeMillis() - SensorHandler.timerPauseValue;
		StageLifeCycleController.stageResume(stageActivity);
	}

	public void setLanguageTranslator(LanguageTranslator languageTranslator) {
		this.languageTranslator = languageTranslator;
	}
}
