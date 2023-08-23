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

package org.catrobat.catroid.content.actions;

import android.util.Log;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.languagetranslator.LanguageTranslator;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.DownloadLanguageModelDialog;

public class TranslateTextFromToAction extends AsynchronousAction {

	public static final String TAG = "TranslateTextAction";
	private Scope scope;
	private Formula text;
	private Formula fromLanguage;
	private Formula toLanguage;
	private Object translation;
	private UserVariable userVariable;
	private LanguageTranslator languageTranslator;
	private boolean translationDone = false;

	@Override
	public void initialize() {
		if (userVariable == null) {
			return;
		}

		Object textValue = text == null ? "" : text.interpretObject(scope);
		Object fromValue = fromLanguage == null ? "" : fromLanguage.interpretObject(scope);
		Object toValue = toLanguage == null ? "" : toLanguage.interpretObject(scope);

		if (textValue.toString().isEmpty()) {
			translation = "The 'text' field is empty.";
			translationDone = true;
		}

		if (toValue.toString().isEmpty()) {
			translation = "No target language was given in 'to' field.";
			translationDone = true;
		}

		if (!textValue.toString().isEmpty() && !fromValue.toString().isEmpty() && !toValue.toString().isEmpty()) {
			if (languageTranslator == null) {
				languageTranslator = new LanguageTranslator(textValue.toString(), fromValue.toString(),
						toValue.toString(), StageActivity.activeStageActivity.get());
			}

			StageActivity stageActivity = languageTranslator.getStageActivity();
			DownloadLanguageModelDialog downloadLanguageModelDialog = stageActivity.getDownloadLanguageModelDialog();
			downloadLanguageModelDialog.setLanguageTranslator(languageTranslator);

			TranslationResult translationResult = new TranslationResult();
			languageTranslator.registerTranslationListener(translationResult);
			languageTranslator.translate();
		}
	}

	@Override
	public boolean isFinished() {
		return translationDone;
	}

	public void setText(Formula text) {
		this.text = text;
	}

	public void setLanguageFrom(Formula fromLanguage) {
		this.fromLanguage = fromLanguage;
	}

	public void setLanguageTo(Formula toLanguage) {
		this.toLanguage = toLanguage;
	}

	public void setUserVariable(UserVariable userVariable) {
		if (userVariable == null) {
			return;
		}
		this.userVariable = userVariable;
	}

	public void setLanguageTranslator(LanguageTranslator languageTranslator) {
		this.languageTranslator = languageTranslator;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public interface Result {
		void onComplete(String result);
	}

	public class TranslationResult implements Result {
		@Override
		public void onComplete(String result) {
			Log.i(TAG, "Setting translation result in user variable.");
			translation = result;
			userVariable.setValue(translation);
			translationDone = true;
			Log.i(TAG, "Translate action done.");
		}
	}
}
