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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class RaspiPwmBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;
	private transient View prototypeView;

	public RaspiPwmBrick(int pinNumber, double pwmFrequency, double pwmPercentage) {
		initializeBrickFields(new Formula(pinNumber), new Formula(pwmFrequency), new Formula(pwmPercentage));
	}

	private void initializeBrickFields(Formula pinNumber, Formula pwmFrequency, Formula pwmPercentage) {
		addAllowedBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER);
		addAllowedBrickField(BrickField.RASPI_PWM_FREQUENCY);
		addAllowedBrickField(BrickField.RASPI_PWM_PERCENTAGE);

		setFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER, pinNumber);
		setFormulaWithBrickField(BrickField.RASPI_PWM_FREQUENCY, pwmFrequency);
		setFormulaWithBrickField(BrickField.RASPI_PWM_PERCENTAGE, pwmPercentage);
	}

	@Override
	public int getRequiredResources() {
		return SOCKET_RASPI
				| getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).getRequiredResources()
				| getFormulaWithBrickField(BrickField.RASPI_PWM_FREQUENCY).getRequiredResources()
				| getFormulaWithBrickField(BrickField.RASPI_PWM_PERCENTAGE).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_raspi_pwm, null);

		TextView textPinNumber = (TextView) prototypeView.findViewById(R.id.brick_raspi_pwm_pin_prototype_text_view);
		textPinNumber.setText(String.valueOf(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER));

		TextView textPwmPercentage = (TextView) prototypeView.findViewById(R.id
				.brick_raspi_pwm_percentage_prototype_text_view);
		textPwmPercentage.setText(String.valueOf(BrickValues.RASPI_PWM_INITIAL_PERCENTAGE));

		TextView textPwmFrequency = (TextView) prototypeView.findViewById(R.id
				.brick_raspi_pwm_frequency_prototype_text_view);
		textPwmFrequency.setText(String.valueOf(BrickValues.RASPI_PWM_INITIAL_FREQUENCY));

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

		view = View.inflate(context, R.layout.brick_raspi_pwm, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_raspi_pwm_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textPinNumber = (TextView) view.findViewById(R.id.brick_raspi_pwm_pin_prototype_text_view);
		TextView editPinNumber = (TextView) view.findViewById(R.id.brick_raspi_pwm_pin_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).setTextFieldId(R.id.brick_raspi_pwm_pin_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).refreshTextField(view);

		textPinNumber.setVisibility(View.GONE);
		editPinNumber.setVisibility(View.VISIBLE);
		editPinNumber.setOnClickListener(this);

		TextView textPwmFrequency = (TextView) view.findViewById(R.id.brick_raspi_pwm_frequency_prototype_text_view);
		TextView editPwmFrequency = (TextView) view.findViewById(R.id.brick_raspi_pwm_frequency_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_FREQUENCY).setTextFieldId(R.id.brick_raspi_pwm_frequency_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_FREQUENCY).refreshTextField(view);

		textPwmFrequency.setVisibility(View.GONE);
		editPwmFrequency.setVisibility(View.VISIBLE);
		editPwmFrequency.setOnClickListener(this);

		TextView textPwmPercentage = (TextView) view.findViewById(R.id.brick_raspi_pwm_percentage_prototype_text_view);
		TextView editPwmPercentage = (TextView) view.findViewById(R.id.brick_raspi_pwm_percentage_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_PERCENTAGE).setTextFieldId(R.id.brick_raspi_pwm_percentage_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_PERCENTAGE).refreshTextField(view);

		textPwmPercentage.setVisibility(View.GONE);
		editPwmPercentage.setVisibility(View.VISIBLE);
		editPwmPercentage.setOnClickListener(this);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_raspi_pwm_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textPinNumber = (TextView) view.findViewById(R.id.brick_raspi_pwm_pin_prototype_text_view);
			TextView textPwmFrequency = (TextView) view.findViewById(R.id.brick_raspi_pwm_frequency_prototype_text_view);
			TextView textPwmPercentage = (TextView) view.findViewById(R.id.brick_raspi_pwm_percentage_prototype_text_view);

			TextView editPinNumber = (TextView) view.findViewById(R.id.brick_raspi_pwm_pin_edit_text);
			TextView editPwmFrequency = (TextView) view.findViewById(R.id.brick_raspi_pwm_frequency_edit_text);
			TextView editPwmPercentage = (TextView) view.findViewById(R.id.brick_raspi_pwm_percentage_edit_text);

			textPinNumber.setTextColor(textPinNumber.getTextColors().withAlpha(alphaValue));
			textPwmFrequency.setTextColor(textPwmFrequency.getTextColors().withAlpha(alphaValue));
			textPwmPercentage.setTextColor(textPwmPercentage.getTextColors().withAlpha(alphaValue));

			editPinNumber.setTextColor(editPinNumber.getTextColors().withAlpha(alphaValue));
			editPinNumber.getBackground().setAlpha(alphaValue);
			editPwmFrequency.setTextColor(editPwmFrequency.getTextColors().withAlpha(alphaValue));
			editPwmFrequency.getBackground().setAlpha(alphaValue);
			editPwmPercentage.setTextColor(editPwmPercentage.getTextColors().withAlpha(alphaValue));
			editPwmPercentage.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		switch (view.getId()) {
			case R.id.brick_raspi_pwm_pin_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_DIGITAL_PIN_NUMBER);
				break;

			case R.id.brick_raspi_pwm_frequency_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_PWM_FREQUENCY);
				break;

			case R.id.brick_raspi_pwm_percentage_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_PWM_PERCENTAGE);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSendRaspiPwmValueAction(sprite,
				getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER),
				getFormulaWithBrickField(BrickField.RASPI_PWM_FREQUENCY),
				getFormulaWithBrickField(BrickField.RASPI_PWM_PERCENTAGE)));
		return null;
	}

	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_DIGITAL_PIN_NUMBER);
	}

	@Override
	public void updateReferenceAfterMerge(Project into, Project from) {
	}
}
