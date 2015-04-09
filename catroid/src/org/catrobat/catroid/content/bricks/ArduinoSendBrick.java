/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class ArduinoSendBrick extends BrickBaseType implements OnItemSelectedListener {

	private static final long serialVersionUID = 1l;
	private transient View prototypeView;
	private transient AdapterView<?> adapterView;

	private int pinNumberLowerByte = 0;
	private int pinNumberHigherByte = 0;
	private int pinValue = 0;
	private int pinSpinnerPosition = 0;
	private int valueSpinnerPosition = 0;
	private String pinNumberString = "";

	public ArduinoSendBrick() {
	}

	/*
	public ArduinoSendBrick(Sprite sprite) {
		this.sprite = sprite;
	}
    */

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_SENSORS_ARDUINO;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		ArduinoSendBrick copyBrick = (ArduinoSendBrick) clone();
		//copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_arduino_send, null);

		Spinner arduinoPinSpinner = (Spinner) prototypeView.findViewById(R.id.brick_arduino_send_pin_spinner);
		arduinoPinSpinner.setFocusableInTouchMode(false);
		arduinoPinSpinner.setFocusable(false);

		Spinner arduinoValueSpinner = (Spinner) prototypeView.findViewById(R.id.brick_arduino_send_value_spinner);
		arduinoValueSpinner.setFocusableInTouchMode(false);
		arduinoValueSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> pinSpinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.arduino_pin_chooser, android.R.layout.simple_spinner_item);
		pinSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> valueSpinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.arduino_value_chooser, android.R.layout.simple_spinner_item);
		valueSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		arduinoPinSpinner.setAdapter(pinSpinnerAdapter);
		arduinoPinSpinner.setSelection(pinSpinnerPosition);
		arduinoValueSpinner.setAdapter(valueSpinnerAdapter);
		arduinoValueSpinner.setSelection(valueSpinnerPosition);

		return prototypeView;

	}

	@Override
	public Brick clone() {
		//return new ArduinoSendBrick(getSprite());
		return new ArduinoSendBrick();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_arduino_send, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_arduino_send_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner arduinoPinSpinner = (Spinner) view.findViewById(R.id.brick_arduino_send_pin_spinner);
		Spinner arduinoValueSpinner = (Spinner) view.findViewById(R.id.brick_arduino_send_value_spinner);

		ArrayAdapter<CharSequence> arduinoPinAdapter = ArrayAdapter.createFromResource(context,
				R.array.arduino_pin_chooser, android.R.layout.simple_spinner_item);
		arduinoPinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> arduinoValueAdapter = ArrayAdapter.createFromResource(context,
				R.array.arduino_value_chooser, android.R.layout.simple_spinner_item);
		arduinoValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (checkbox.getVisibility() == View.VISIBLE) {
			arduinoPinSpinner.setClickable(false);
			arduinoPinSpinner.setEnabled(false);
			arduinoValueSpinner.setClickable(false);
			arduinoValueSpinner.setEnabled(false);
		} else {
			arduinoPinSpinner.setClickable(true);
			arduinoPinSpinner.setEnabled(true);
			arduinoPinSpinner.setOnItemSelectedListener(this);
			arduinoValueSpinner.setClickable(true);
			arduinoValueSpinner.setEnabled(true);
			arduinoValueSpinner.setOnItemSelectedListener(this);
		}

		arduinoPinSpinner.setAdapter(arduinoPinAdapter);
		arduinoPinSpinner.setSelection(pinSpinnerPosition);
		arduinoValueSpinner.setAdapter(arduinoValueAdapter);
		arduinoValueSpinner.setSelection(valueSpinnerPosition);

		arduinoPinSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String tempSavingString = "00";
				tempSavingString = parent.getItemAtPosition(position).toString();

				if(tempSavingString != "")
					pinNumberString = tempSavingString;

				if (tempSavingString.length() < 2) {
					pinNumberLowerByte = '0';
					pinNumberHigherByte = tempSavingString.charAt(tempSavingString.length() - 1);
				} else {
					pinNumberLowerByte = tempSavingString.charAt(tempSavingString.length() - 2);
					pinNumberHigherByte = tempSavingString.charAt(tempSavingString.length() - 1);
				}
				pinSpinnerPosition = position;
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		arduinoValueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					pinValue = 'L';
				} else {
					pinValue = 'H';
				}
				valueSpinnerPosition = position;
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_arduino_send_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner pinSpinner = (Spinner) view.findViewById(R.id.brick_arduino_send_pin_spinner);
			pinSpinner.getBackground().setAlpha(alphaValue);

			Spinner valueSpinner = (Spinner) view.findViewById(R.id.brick_arduino_send_value_spinner);
			valueSpinner.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);
		}
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		adapterView = parent;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.sendArduinoValues(sprite, pinNumberString, pinValue));
		return null;
	}
}