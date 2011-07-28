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

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import at.abraxas.amarino.Amarino;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.DevicesDialog;
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

	private Sprite sprite;
	private int type;
	private int pin;
	private double value;
	private double time;
	private final int DIGITAL = 1;
	private final int ANALOG = 0;
	private BluetoothAdapter bluetoothAdapter;
	private Context context;

	private DevicesDialog bluetoothDeviceDialog;
	private String selectedAddress;

	private static final long serialVersionUID = 1L;
	protected static final int REQUEST_CONNECT_DEVICE = 1;

	ImageButton digitalButton;
	ImageButton analogButton;

	public SensorBrick(Sprite sprite, int type, int pin, double value, double time, String deviceAddress) {
		this.sprite = sprite;
		this.type = type;
		this.pin = pin;
		this.value = value;
		this.time = time;

	}

	public void execute() {

		double[] arduinoPackage = new double[3];

		arduinoPackage[0] = (this.pin);
		arduinoPackage[1] = (this.value);
		arduinoPackage[2] = (this.time);

		for (int i = 0; i < arduinoPackage.length; i++) {
			Log.d("Packet", Double.toString(arduinoPackage[i]));
		}

		bluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter.isEnabled() && bluetoothAdapter.getBondedDevices() != null) {
			Amarino.connect(this.context, selectedAddress);
			if (type == DIGITAL) {
				Amarino.sendDataToArduino(this.context, selectedAddress, 'd', arduinoPackage);
			} else if (type == ANALOG) {
				Amarino.sendDataToArduino(this.context, selectedAddress, 'a', arduinoPackage);
			}
		}

		Amarino.disconnect(this.context, selectedAddress);

	}

	public Sprite getSprite() {
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
		final View view = inflater.inflate(R.layout.construction_brick_sensor, null);

		this.context = context;

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

		digitalButton = (ImageButton) view.findViewById(R.id.construction_brick_sensor_digital_button);
		analogButton = (ImageButton) view.findViewById(R.id.construction_brick_sensor_analog_button);

		digitalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("TAG", "DIGITAL BUTTON");
				type = DIGITAL;
				digitalButton.setImageDrawable(view.getResources().getDrawable(R.drawable.digital_active));
				analogButton.setImageDrawable(view.getResources().getDrawable(R.drawable.analog_inactive));
			}
		});

		analogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("TAG", "ANALOG BUTTON");
				type = ANALOG;
				analogButton.setImageDrawable(view.getResources().getDrawable(R.drawable.analog_active));
				digitalButton.setImageDrawable(view.getResources().getDrawable(R.drawable.digital_inactive));
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

	/**
	 * @param selectedAddress
	 */
	public void setSelectedAddress(String selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	public void connectArduino() {
		Intent serverIntent = new Intent(this.activity, DeviceListActivity.class);
		activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}
}
