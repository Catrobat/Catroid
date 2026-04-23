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
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class LegoNxtPlayToneBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public LegoNxtPlayToneBrick() {
		addAllowedBrickField(BrickField.LEGO_NXT_FREQUENCY, R.id.nxt_tone_freq_edit_text);
		addAllowedBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS, R.id.nxt_tone_duration_edit_text);
	}

	public LegoNxtPlayToneBrick(double frequencyValue, double durationValue) {
		this(new Formula(frequencyValue), new Formula(durationValue));
	}

	public LegoNxtPlayToneBrick(Formula frequencyFormula, Formula durationFormula) {
		this();
		setFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY, frequencyFormula);
		setFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS, durationFormula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_nxt_play_tone;
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.LEGO_NXT_DURATION_IN_SECONDS;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		setSecondsLabel(view, BrickField.LEGO_NXT_DURATION_IN_SECONDS);
		return view;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_LEGO_NXT);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoNxtPlayToneAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY),
				getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS)));
	}
}
