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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;

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

		TextView textPinNumber = (TextView) prototypeView.findViewById(R.id.brick_raspi_pwm_pin_edit_text);
		textPinNumber.setText(String.valueOf(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER));

		TextView textPwmPercentage = (TextView) prototypeView.findViewById(R.id
				.brick_raspi_pwm_percentage_edit_text);
		textPwmPercentage.setText(String.valueOf(BrickValues.RASPI_PWM_INITIAL_PERCENTAGE));

		TextView textPwmFrequency = (TextView) prototypeView.findViewById(R.id
				.brick_raspi_pwm_frequency_edit_text);
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
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_raspi_pwm_pin_text_view),
				context.getString(R.string.category_raspi));

		setCheckboxView(R.id.brick_raspi_pwm_checkbox);
		TextView editPinNumber = (TextView) view.findViewById(R.id.brick_raspi_pwm_pin_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).setTextFieldId(R.id.brick_raspi_pwm_pin_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).refreshTextField(view);

		editPinNumber.setOnClickListener(this);

		TextView editPwmFrequency = (TextView) view.findViewById(R.id.brick_raspi_pwm_frequency_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_FREQUENCY).setTextFieldId(R.id.brick_raspi_pwm_frequency_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_FREQUENCY).refreshTextField(view);

		editPwmFrequency.setOnClickListener(this);

		TextView editPwmPercentage = (TextView) view.findViewById(R.id.brick_raspi_pwm_percentage_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_PERCENTAGE).setTextFieldId(R.id.brick_raspi_pwm_percentage_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_PWM_PERCENTAGE).refreshTextField(view);

		editPwmPercentage.setOnClickListener(this);

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
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
		switch (view.getId()) {
			case R.id.brick_raspi_pwm_frequency_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_PWM_FREQUENCY);
				break;

			case R.id.brick_raspi_pwm_percentage_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_PWM_PERCENTAGE);
				break;

			case R.id.brick_raspi_pwm_pin_edit_text:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_DIGITAL_PIN_NUMBER);
				break;
		}
	}
}
