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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.connections.Bluetooth;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditDialog;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;
import at.tugraz.ist.catroid.ui.dialogs.SensorPinAnalogDialog;
import at.tugraz.ist.catroid.ui.dialogs.SensorPinDigitalDialog;
import at.tugraz.ist.catroid.ui.dialogs.SensorValueAnalogDialog;
import at.tugraz.ist.catroid.ui.dialogs.SensorValueDigitalDialog;

/**
 * @author manuelzoderer
 * 
 */
public class SensorBrick implements Brick, OnDismissListener {

	//TODO INVALIDATE THE SENSOR BRICK !!!!
	// SO THAT CHANGES LIKE BUTTON CLICKS ARE REGISTERD PROPERLY!!!!!

	private Sprite sprite;
	private int type;
	private int pin;
	private double value;
	private double time;
	private final int DIGITAL = 1;
	private final int ANALOG = 0;

	private static final long serialVersionUID = 1L;
	protected static final int REQUEST_CONNECT_DEVICE = 3;

	public SensorBrick(Sprite sprite, int type, double pin, double value, double time, String deviceAddress) {
		this.sprite = sprite;
		this.type = type;
		this.pin = pin;
		this.value = value;
		this.time = time;

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

	public int getPin() {
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

		Bluetooth bluetooth = new Bluetooth(context);
		if (!bluetooth.getBluetoothAdapter().isEnabled()) {
			bluetooth.start();
		}

		bluetooth.checkForDevices();

		//Pin TODO: CHECK FOR WRONG INPUT AND MAKE NEW DIALOG FOR EACH ONE? OR CHECK IT SOMEHOW....
		EditText editPin = (EditText) view.findViewById(R.id.construction_brick_sensor_pin);
		editPin.setText(String.valueOf(pin));
		SensorPinAnalogDialog dialogPinAnalog;
		SensorPinDigitalDialog dialogPinDigital;
		switch (type) {
			case DIGITAL:
				dialogPinDigital = new SensorPinDigitalDialog(context, editPin, pin, true);
				dialogPinDigital.setOnDismissListener(this);
				dialogPinDigital.setOnCancelListener((OnCancelListener) context);
				editPin.setOnClickListener(dialogPinDigital);
				break;
			case ANALOG:
				dialogPinAnalog = new SensorPinAnalogDialog(context, editPin, pin, true);
				dialogPinAnalog.setOnDismissListener(this);
				dialogPinAnalog.setOnCancelListener((OnCancelListener) context);
				editPin.setOnClickListener(dialogPinAnalog);
				break;
			default:
				break;
		}

		Button digitalButton = (Button) view.findViewById(R.id.construction_brick_sensor_digital_button);
		Button analogButton = (Button) view.findViewById(R.id.construction_brick_sensor_analog_button);

		digitalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("TAG", "DIGITAL BUTTON");
				type = DIGITAL;
			}
		});

		analogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("TAG", "ANALOG BUTTON");
				type = ANALOG;
			}
		});

		//Value TODO: CHECK FOR WRONG INPUT AND MAKE NEW DIALOG FOR EACH ONE? OR CHECK IT SOMEHOW....
		EditText editValue = (EditText) view.findViewById(R.id.construction_brick_sensor_value);
		editValue.setText(String.valueOf(value));
		SensorValueAnalogDialog dialogValueAnalog;
		SensorValueDigitalDialog dialogValueDigital;

		switch (type) {
			case DIGITAL:
				dialogValueDigital = new SensorValueDigitalDialog(context, editValue, (int) value, true);
				dialogValueDigital.setOnDismissListener(this);
				dialogValueDigital.setOnCancelListener((OnCancelListener) context);
				editValue.setOnClickListener(dialogValueDigital);
				break;
			case ANALOG:
				dialogValueAnalog = new SensorValueAnalogDialog(context, editValue, value);
				dialogValueAnalog.setOnDismissListener(this);
				dialogValueAnalog.setOnCancelListener((OnCancelListener) context);
				editValue.setOnClickListener(dialogValueAnalog);
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
		return new SensorBrick(getSprite(), getType(), getPin(), getValue(), getTime(), null);
	}

	public void onDismiss(DialogInterface dialog) {
		EditDialog inputDialog = (EditDialog) dialog;

		if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_sensor_pin) {
			if (type == ANALOG) {
				SensorPinAnalogDialog pindialog = (SensorPinAnalogDialog) inputDialog;
				pin = pindialog.getValue();
			} else {
				SensorPinDigitalDialog pindialog = (SensorPinDigitalDialog) inputDialog;
				pin = pindialog.getValue();
			}
		} else if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_sensor_time) {
			EditDoubleDialog timeDialog = (EditDoubleDialog) inputDialog;
			time = timeDialog.getValue();
		} else if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_sensor_value) {
			if (type == ANALOG) {
				SensorValueAnalogDialog valuedialog = (SensorValueAnalogDialog) inputDialog;
				value = valuedialog.getValue();
			} else {
				SensorValueDigitalDialog valuedialog = (SensorValueDigitalDialog) inputDialog;
				value = valuedialog.getValue();
			}

		} else {
			throw new RuntimeException("Received illegal id from EditText: " + inputDialog.getRefernecedEditTextId());
		}

		dialog.cancel();
	}
}
