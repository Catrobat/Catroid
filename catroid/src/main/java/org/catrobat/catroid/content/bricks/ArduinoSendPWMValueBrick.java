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
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class ArduinoSendPWMValueBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;
	private transient View prototypeView;

	public ArduinoSendPWMValueBrick() {
		addAllowedBrickField(BrickField.ARDUINO_ANALOG_PIN_NUMBER);
		addAllowedBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE);
	}

	public ArduinoSendPWMValueBrick(int pinNumber, int pinValue) {
		initializeBrickFields(new Formula(pinNumber), new Formula(pinValue));
	}

	public ArduinoSendPWMValueBrick(Formula pinNumber, Formula pinValue) {
		initializeBrickFields(pinNumber, pinValue);
	}

	private void initializeBrickFields(Formula pinNumber, Formula pinValue) {
		addAllowedBrickField(BrickField.ARDUINO_ANALOG_PIN_NUMBER);
		addAllowedBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE);
		setFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_NUMBER, pinNumber);
		setFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE, pinValue);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_SENSORS_ARDUINO
				| getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_NUMBER).getRequiredResources()
				| getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_arduino_send_analog, null);

		TextView textSetPinNumber = (TextView) prototypeView.findViewById(R.id.brick_arduino_set_analog_pin_edit_text);
		textSetPinNumber.setText(String.valueOf(BrickValues.ARDUINO_PWM_INITIAL_PIN_NUMBER));
		TextView textSetPinValue = (TextView) prototypeView.findViewById(R.id.brick_arduino_set_analog_value_edit_text);
		textSetPinValue.setText(String.valueOf(BrickValues.ARDUINO_PWM_INITIAL_PIN_VALUE));

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

		view = View.inflate(context, R.layout.brick_arduino_send_analog, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_arduino_send_analog_checkbox);
		TextView editPinNumber = (TextView) view.findViewById(R.id.brick_arduino_set_analog_pin_edit_text);
		getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_NUMBER).setTextFieldId(R.id.brick_arduino_set_analog_pin_edit_text);
		getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_NUMBER).refreshTextField(view);

		editPinNumber.setOnClickListener(this);

		TextView editPinValue = (TextView) view.findViewById(R.id.brick_arduino_set_analog_value_edit_text);
		getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE).setTextFieldId(R.id.brick_arduino_set_analog_value_edit_text);
		getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE).refreshTextField(view);

		editPinValue.setOnClickListener(this);

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSendPWMArduinoValueAction(sprite,
				getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_NUMBER),
				getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE)));
		return null;
	}

	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_arduino_set_analog_value_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ARDUINO_ANALOG_PIN_VALUE);
				break;

			case R.id.brick_arduino_set_analog_pin_edit_text:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.ARDUINO_ANALOG_PIN_NUMBER);
				break;
		}
	}

	public void updateArduinoValues994to995() {
		Formula formula = getFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE);
		FormulaElement oldFormulaElement = formula.getFormulaTree();

		FormulaElement multiplication =
				new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.MULT.toString(), null);
		FormulaElement twoPointFiveFive = new FormulaElement(FormulaElement.ElementType.NUMBER, "2.55", null);

		multiplication.setLeftChild(twoPointFiveFive);
		multiplication.setRightChild(oldFormulaElement);

		setFormulaWithBrickField(BrickField.ARDUINO_ANALOG_PIN_VALUE, new Formula(multiplication));
	}
}
