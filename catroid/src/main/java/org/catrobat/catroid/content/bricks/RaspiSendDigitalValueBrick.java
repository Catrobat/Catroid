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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class RaspiSendDigitalValueBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;
	private transient View prototypeView;

	public RaspiSendDigitalValueBrick() {
		addAllowedBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER);
		addAllowedBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE);
	}

	public RaspiSendDigitalValueBrick(int pinNumber, int pinValue) {
		initializeBrickFields(new Formula(pinNumber), new Formula(pinValue));
	}

	public RaspiSendDigitalValueBrick(Formula pinNumber, Formula pinValue) {
		initializeBrickFields(pinNumber, pinValue);
	}

	public RaspiSendDigitalValueBrick(int pinNumber, String pinValue) {
		initializeBrickFields(new Formula(pinNumber), new Formula(pinValue));
	}

	private void initializeBrickFields(Formula pinNumber, Formula pinValue) {
		addAllowedBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER);
		addAllowedBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE);
		setFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER, pinNumber);
		setFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE, pinValue);
	}

	@Override
	public int getRequiredResources() {
		return SOCKET_RASPI
				| getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).getRequiredResources()
				| getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_raspi_send_digital, null);

		TextView textSetPinNumber = (TextView) prototypeView.findViewById(R.id.brick_raspi_set_digital_pin_edit_text);
		textSetPinNumber.setText(String.valueOf(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER));
		TextView textSetPinValue = (TextView) prototypeView.findViewById(R.id.brick_raspi_set_digital_value_edit_text);
		textSetPinValue.setText(String.valueOf(BrickValues.RASPI_DIGITAL_INITIAL_PIN_VALUE));

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

		view = View.inflate(context, R.layout.brick_raspi_send_digital, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_raspi_send_digital_checkbox);
		TextView editPinNumber = (TextView) view.findViewById(R.id.brick_raspi_set_digital_pin_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).setTextFieldId(R.id.brick_raspi_set_digital_pin_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER).refreshTextField(view);

		editPinNumber.setOnClickListener(this);

		TextView editPinValue = (TextView) view.findViewById(R.id.brick_raspi_set_digital_value_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE).setTextFieldId(R.id.brick_raspi_set_digital_value_edit_text);
		getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE).refreshTextField(view);

		editPinValue.setOnClickListener(this);

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSendDigitalRaspiValueAction(sprite,
				getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER),
				getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE)));
		return null;
	}

	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_raspi_set_digital_pin_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_DIGITAL_PIN_NUMBER);
				break;

			case R.id.brick_raspi_set_digital_value_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.RASPI_DIGITAL_PIN_VALUE);
				break;
		}
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
