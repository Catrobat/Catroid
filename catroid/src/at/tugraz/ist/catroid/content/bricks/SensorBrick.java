/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.abraxas.amarino.Amarino;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;

/**
 * @author manuelzoderer
 * 
 */
public class SensorBrick implements Brick, OnDismissListener {

	private Sprite sprite;
	private int type;
	private double pin;
	private double value;
	private double time;
	private final int DIGITAL = 1;
	private final int ANALOG = 0;

	private static final long serialVersionUID = 1L;

	//ToDo change this to your Bluetooth device address 
	private static final String deviceAddress;

	public SensorBrick(Sprite sprite, int type, double pin, double value, double time, String deviceAddress) {
		this.sprite = sprite;
		this.type = type;
		this.pin = pin;
		this.value = value;
		this.time = time;
		this.deviceAddress = deviceAddress; // for example "00:06:66:03:73:7B";
	}

	public void execute() {
		// TODO Auto-generated method stub

	}

	public Sprite getSprite() {
		// TODO Auto-generated method stub
		return sprite;
	}

	public int getType() {
		return type;
	}

	public double getPin() {
		return pin;
	}

	public double getValue() {
		return value;
	}

	public double getTime() {
		return time;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_sensor, null);

		//Turn Bluetooth on an scan for devices

		//Connect to the Arduino via Bluetooth
		Amarino.connect(context, deviceAddress);
		//Type 1...Digital
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.brick_sensor_digital_button:
						type = DIGITAL;
						break;
					case R.id.brick_sensor_analog_button:
						type = ANALOG;
						break;
					default:
						break;
				}
			}
		};

		//Pin TODO: CHECK FOR WRONG INPUT AND MAKE NEW DIALOG FOR EACH ONE? OR CHECK IT SOMEHOW....
		EditText editPin = (EditText) view.findViewById(R.id.construction_brick_sensor_pin);
		editPin.setText(String.valueOf(pin));
		EditDoubleDialog dialogPin;
		switch (type) {
			case DIGITAL:
				dialogPin = new EditDoubleDialog(context, editPin, pin);
				dialogPin.setOnDismissListener(this);
				dialogPin.setOnCancelListener((OnCancelListener) context);
				editPin.setOnClickListener(dialogPin);
				break;
			case ANALOG:
				dialogPin = new EditDoubleDialog(context, editPin, pin);
				dialogPin.setOnDismissListener(this);
				dialogPin.setOnCancelListener((OnCancelListener) context);
				editPin.setOnClickListener(dialogPin);
				break;
			default:
				break;
		}
		//Value TODO: CHECK FOR WRONG INPUT AND MAKE NEW DIALOG FOR EACH ONE? OR CHECK IT SOMEHOW....
		EditText editValue = (EditText) view.findViewById(R.id.construction_brick_sensor_value);
		editValue.setText(String.valueOf(value));
		EditDoubleDialog dialogValue;
		switch (type) {
			case DIGITAL:
				dialogValue = new EditDoubleDialog(context, editValue, value);
				dialogValue.setOnDismissListener(this);
				dialogValue.setOnCancelListener((OnCancelListener) context);
				editValue.setOnClickListener(dialogValue);
				break;
			case ANALOG:
				dialogValue = new EditDoubleDialog(context, editValue, value);
				dialogValue.setOnDismissListener(this);
				dialogValue.setOnCancelListener((OnCancelListener) context);
				editValue.setOnClickListener(dialogValue);
				break;
			default:
				break;
		}

		//Time
		EditText editTime = (EditText) view.findViewById(R.id.construction_brick_sensor_time);
		editTime.setText(String.valueOf(time));

		EditDoubleDialog dialogTime = new EditDoubleDialog(context, editTime, time);
		dialogTime.setOnDismissListener(this);
		dialogTime.setOnCancelListener((OnCancelListener) context);

		editTime.setOnClickListener(dialogTime);

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_sensor, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new SensorBrick(getSprite(), getType(), getPin(), getValue(), getTime());
	}

	public void onDismiss(DialogInterface dialog) {
		EditDoubleDialog inputDialog = (EditDoubleDialog) dialog;

		if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_sensor_pin) {
			pin = inputDialog.getValue();
		} else if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_sensor_time) {
			time = inputDialog.getValue();
		} else if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_sensor_value) {
			value = inputDialog.getValue();
		} else {
			throw new RuntimeException("Received illegal id from EditText: " + inputDialog.getRefernecedEditTextId());
		}

		dialog.cancel();
	}

}
