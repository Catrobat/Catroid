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
package org.catrobat.catroid.content.bricks;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class TranslateTextFromToBrick extends UserVariableBrickWithFormula {

	private static final long serialVersionUID = 1L;

	public TranslateTextFromToBrick() {
		addAllowedBrickField(BrickField.TRANSLATE_TEXT, R.id.brick_translate_from_to_edit_text);
		addAllowedBrickField(BrickField.TRANSLATE_FROM_LANGUAGE, R.id.brick_translate_from_edit_text);
		addAllowedBrickField(BrickField.TRANSLATE_TO_LANGUAGE, R.id.brick_translate_to_edit_text);
	}

	public TranslateTextFromToBrick(String text, String from, String to) {
		this(new Formula(text), new Formula(from), new Formula(to));
	}

	public TranslateTextFromToBrick(Formula textToTranslate, Formula fromLanguage, Formula toLanguage) {
		this();
		setFormulaWithBrickField(BrickField.TRANSLATE_TEXT, textToTranslate);
		setFormulaWithBrickField(BrickField.TRANSLATE_FROM_LANGUAGE, fromLanguage);
		setFormulaWithBrickField(BrickField.TRANSLATE_TO_LANGUAGE, toLanguage);
	}

	public TranslateTextFromToBrick(Formula textToTranslate, Formula fromLanguage, Formula toLanguage, UserVariable userVariable) {
		this(textToTranslate, fromLanguage, toLanguage);
		this.userVariable = userVariable;
	}

	public TranslateTextFromToBrick(String value, Sensors defaultValueFrom, Sensors defaultValueTo, Context context) {
		this(new Formula(value),
				new Formula(SensorHandler.getInstance(context).getSensorValue(defaultValueFrom).toString()),
				new Formula(SensorHandler.getInstance(context).getSensorValue(defaultValueTo).toString()));
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_translate_text_from_to;
	}

	@Override
	public int getSpinnerId() {
		return R.id.brick_translate_text_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createTranslateTextFromToAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TRANSLATE_TEXT),
				getFormulaWithBrickField(BrickField.TRANSLATE_FROM_LANGUAGE),
				getFormulaWithBrickField(BrickField.TRANSLATE_TO_LANGUAGE),
				userVariable));
	}
}
