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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.text.NumberFormat;
import java.util.List;

public class LegoNxtPlayToneBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	private transient TextView editFreq;

	public LegoNxtPlayToneBrick() {
		addAllowedBrickField(BrickField.LEGO_NXT_FREQUENCY);
		addAllowedBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS);
	}

	public LegoNxtPlayToneBrick(int frequencyValue, float durationValue) {
		initializeBrickFields(new Formula(frequencyValue), new Formula(durationValue));
	}

	public LegoNxtPlayToneBrick(Formula frequencyFormula, Formula durationFormula) {
		initializeBrickFields(frequencyFormula, durationFormula);
	}

	private void initializeBrickFields(Formula frequencyFormula, Formula durationFormula) {
		addAllowedBrickField(BrickField.LEGO_NXT_FREQUENCY);
		addAllowedBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY, frequencyFormula);
		setFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS, durationFormula);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT | getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).getRequiredResources() | getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		TextView textDuration = (TextView) prototypeView.findViewById(R.id.nxt_tone_duration_edit_text);

		NumberFormat nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
		nf.setMinimumFractionDigits(1);
		textDuration.setText(nf.format(BrickValues.LEGO_DURATION));
		TextView textFreq = (TextView) prototypeView.findViewById(R.id.nxt_tone_freq_edit_text);

		textFreq.setText(String.valueOf(BrickValues.LEGO_FREQUENCY));
		return prototypeView;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_nxt_play_tone_checkbox);

		TextView editDuration = (TextView) view.findViewById(R.id.nxt_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS)
				.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS).refreshTextField(view);

		editDuration.setOnClickListener(this);

		editFreq = (TextView) view.findViewById(R.id.nxt_tone_freq_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).setTextFieldId(R.id.nxt_tone_freq_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).refreshTextField(view);

		editFreq.setOnClickListener(this);

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.nxt_tone_freq_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_NXT_FREQUENCY);
				break;
			case R.id.nxt_tone_duration_edit_text:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_NXT_DURATION_IN_SECONDS);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoNxtPlayToneAction(sprite,
				getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY),
				getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS)));
		return null;
	}
}
