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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
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
		TextView textDuration = (TextView) prototypeView.findViewById(R.id.nxt_tone_duration_text_view);

		NumberFormat nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
		nf.setMinimumFractionDigits(1);
		textDuration.setText(nf.format(BrickValues.LEGO_DURATION));
		TextView textFreq = (TextView) prototypeView.findViewById(R.id.nxt_tone_freq_text_view);

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
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_nxt_play_tone_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textDuration = (TextView) view.findViewById(R.id.nxt_tone_duration_text_view);
		TextView editDuration = (TextView) view.findViewById(R.id.nxt_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS)
				.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS).refreshTextField(view);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		TextView textFreq = (TextView) view.findViewById(R.id.nxt_tone_freq_text_view);
		editFreq = (TextView) view.findViewById(R.id.nxt_tone_freq_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).setTextFieldId(R.id.nxt_tone_freq_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).refreshTextField(view);

		textFreq.setVisibility(View.GONE);
		editFreq.setVisibility(View.VISIBLE);

		editFreq.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		switch (view.getId()) {
			case R.id.nxt_tone_freq_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_NXT_FREQUENCY);
				break;
			case R.id.nxt_tone_duration_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_NXT_DURATION_IN_SECONDS);
				break;
		}
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_NXT_FREQUENCY);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_nxt_play_tone_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textLegoPlayToneLabel = (TextView) view.findViewById(R.id.brick_nxt_play_tone_label);
			TextView textLegoPlayToneDuration = (TextView) view.findViewById(R.id.brick_nxt_play_tone_duration);
			TextView textLegoPlayToneDurationTextView = (TextView) view.findViewById(R.id.nxt_tone_duration_text_view);
			TextView textLegoPlayToneSeconds = (TextView) view.findViewById(R.id.brick_nxt_play_tone_seconds);
			TextView textLegoPlayToneFrequency = (TextView) view.findViewById(R.id.brick_nxt_play_tone_frequency);
			TextView textLegoPlayToneOz = (TextView) view.findViewById(R.id.brick_nxt_play_tone_hundred_hz);

			TextView editLegoDuration = (TextView) view.findViewById(R.id.nxt_tone_duration_edit_text);
			TextView editLegoFrequency = (TextView) view.findViewById(R.id.nxt_tone_freq_edit_text);
			textLegoPlayToneLabel.setTextColor(textLegoPlayToneLabel.getTextColors().withAlpha(alphaValue));
			textLegoPlayToneDuration.setTextColor(textLegoPlayToneDuration.getTextColors().withAlpha(alphaValue));
			textLegoPlayToneDurationTextView.setTextColor(textLegoPlayToneDurationTextView.getTextColors().withAlpha(
					alphaValue));
			textLegoPlayToneSeconds.setTextColor(textLegoPlayToneSeconds.getTextColors().withAlpha(alphaValue));
			textLegoPlayToneFrequency.setTextColor(textLegoPlayToneFrequency.getTextColors().withAlpha(alphaValue));
			textLegoPlayToneOz.setTextColor(textLegoPlayToneOz.getTextColors().withAlpha(alphaValue));

			editLegoFrequency.setTextColor(editLegoFrequency.getTextColors().withAlpha(alphaValue));
			editLegoFrequency.getBackground().setAlpha(alphaValue);
			editLegoDuration.setTextColor(editLegoDuration.getTextColors().withAlpha(alphaValue));
			editLegoDuration.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoNxtPlayTone(sprite,
				getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY),
				getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS)));
		return null;
	}
}
