/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class PhiroPlayToneBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	private String tone;
	private transient Tone toneEnum;
	private transient TextView editDuration;

	public static enum Tone {
		DO, RE, MI, FA, SO, LA, TI
	}

	public PhiroPlayToneBrick() {
		addAllowedBrickField(BrickField.PHIRO_DURATION_IN_SECONDS);
	}

	public PhiroPlayToneBrick(Tone tone, int durationValue) {
		this.toneEnum = tone;
		this.tone = toneEnum.name();
		initializeBrickFields(new Formula(durationValue));
	}

	public PhiroPlayToneBrick(Tone tone, Formula durationFormula) {
		this.toneEnum = tone;
		this.tone = toneEnum.name();
		initializeBrickFields(durationFormula);
	}

	protected Object readResolve() {
		if (tone != null) {
			toneEnum = Tone.valueOf(tone);
		}
		return this;
	}

	private void initializeBrickFields(Formula duration) {
		addAllowedBrickField(BrickField.PHIRO_DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS, duration);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_PHIRO | getFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_phiro_play_tone, null);
		TextView textDuration = (TextView) prototypeView.findViewById(R.id.brick_phiro_play_tone_duration_text_view);
		textDuration.setText(String.valueOf(BrickValues.PHIRO_DURATION));

		Spinner phiroProToneSpinner = (Spinner) prototypeView.findViewById(R.id.brick_phiro_select_tone_spinner);
		phiroProToneSpinner.setFocusableInTouchMode(false);
		phiroProToneSpinner.setFocusable(false);
		phiroProToneSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> toneAdapter = ArrayAdapter.createFromResource(context, R.array.brick_phiro_select_tone_spinner,
				android.R.layout.simple_spinner_item);
		toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		phiroProToneSpinner.setAdapter(toneAdapter);
		phiroProToneSpinner.setSelection(toneEnum.ordinal());
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new PhiroPlayToneBrick(toneEnum,
				getFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS).clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_phiro_play_tone, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_phiro_play_tone_checkbox);
		TextView textDuration = (TextView) view.findViewById(R.id.brick_phiro_play_tone_duration_text_view);
		editDuration = (TextView) view.findViewById(R.id.brick_phiro_play_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS).setTextFieldId(R.id.brick_phiro_play_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS).refreshTextField(view);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		ArrayAdapter<CharSequence> toneAdapter = ArrayAdapter.createFromResource(context, R.array.brick_phiro_select_tone_spinner,
				android.R.layout.simple_spinner_item);
		toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner toneSpinner = (Spinner) view.findViewById(R.id.brick_phiro_select_tone_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			toneSpinner.setClickable(true);
			toneSpinner.setEnabled(true);
		} else {
			toneSpinner.setClickable(false);
			toneSpinner.setEnabled(false);
		}

		toneSpinner.setAdapter(toneAdapter);
		toneSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				toneEnum = Tone.values()[position];
				tone = toneEnum.name();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		toneSpinner.setSelection(toneEnum.ordinal());

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.PHIRO_DURATION_IN_SECONDS);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPhiroPlayToneActionAction(sprite, toneEnum,
				getFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS)));
		sequence.addAction(sprite.getActionFactory().createDelayAction(sprite, getFormulaWithBrickField(BrickField
				.PHIRO_DURATION_IN_SECONDS)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
