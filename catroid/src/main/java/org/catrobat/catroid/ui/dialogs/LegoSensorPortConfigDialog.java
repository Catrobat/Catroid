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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.ui.SettingsActivity;

public class LegoSensorPortConfigDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_lego_sensor_port_config";
	public static final String TAG = LegoSensorPortConfigDialog.class.getSimpleName();
	public enum Lego { NXT, EV3 }
	private int clickedItem = 0;
	private Lego legoType;
	private String legoTypeString;
	private String title;
	private NXTSensor.Sensor nxtSensor;
	private EV3Sensor.Sensor ev3Sensor;

	public LegoSensorPortConfigDialog(int clickedResItem, Lego type) {
		clickedItem = clickedResItem;
		legoType = type;
		legoTypeString = type == Lego.NXT ? "NXT" : "EV3";
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String[] portStrings;
		final String[] sensorMappingStrings;
		String[] sensorMappings;
		Dialog dialog;
		if (legoType == Lego.NXT) {
			final NXTSensor.Sensor[] sensorMapping = SettingsActivity.getLegoMindstormsNXTSensorMapping(this.getActivity());
			sensorMappingStrings = getResources().getStringArray(R.array.nxt_sensor_chooser);
			portStrings = getResources().getStringArray(R.array.port_chooser);

			sensorMappings = new String[portStrings.length];

			for (int index = 0; index < sensorMappings.length; index++) {
				sensorMappings[index] = portStrings[index] + ": " + sensorMappingStrings[sensorMapping[index].ordinal()];
			}
			getAttributes(clickedItem, legoType);

			dialog = new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setItems(sensorMappings, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							checkIfOverwrite(which, sensorMappingStrings[sensorMapping[which].ordinal()], legoType);
						}
					}).create();

			dialog.setCanceledOnTouchOutside(true);
		} else {
			final EV3Sensor.Sensor[] sensorMapping = SettingsActivity.getLegoMindstormsEV3SensorMapping(this.getActivity());
			sensorMappingStrings = getResources().getStringArray(R.array.ev3_sensor_chooser);
			portStrings = getResources().getStringArray(R.array.port_chooser);

			sensorMappings = new String[portStrings.length];

			for (int index = 0; index < sensorMappings.length; index++) {
				sensorMappings[index] = portStrings[index] + ": " + sensorMappingStrings[sensorMapping[index].ordinal()];
			}
			getAttributes(clickedItem, legoType);

			dialog = new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setItems(sensorMappings, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							checkIfOverwrite(which, sensorMappingStrings[sensorMapping[which].ordinal()], legoType);
						}
					}).create();

			dialog.setCanceledOnTouchOutside(true);
		}

		return dialog;
	}

	private void getAttributes(int clickedItem, Lego type) {
		if (type == Lego.NXT) {
			switch (clickedItem) {
				case R.string.formula_editor_sensor_lego_nxt_touch:
					title = getString(R.string.nxt_sensor_touch);
					nxtSensor = NXTSensor.Sensor.TOUCH;
					break;
				case R.string.formula_editor_sensor_lego_nxt_sound:
					title = getString(R.string.nxt_sensor_sound);
					nxtSensor = NXTSensor.Sensor.SOUND;
					break;
				case R.string.formula_editor_sensor_lego_nxt_light:
					title = getString(R.string.nxt_sensor_light);
					nxtSensor = NXTSensor.Sensor.LIGHT_INACTIVE;
					break;
				case R.string.formula_editor_sensor_lego_nxt_light_active:
					title = getString(R.string.nxt_sensor_light_active);
					nxtSensor = NXTSensor.Sensor.LIGHT_ACTIVE;
					break;
				case R.string.formula_editor_sensor_lego_nxt_ultrasonic:
					title = getString(R.string.nxt_sensor_ultrasonic);
					nxtSensor = NXTSensor.Sensor.ULTRASONIC;
					break;
				default:
					title = getString(R.string.nxt_sensor_not_found);
					nxtSensor = NXTSensor.Sensor.NO_SENSOR;
			}
		} else {
			switch (clickedItem) {
				case R.string.formula_editor_sensor_lego_ev3_sensor_touch:
					title = getString(R.string.ev3_sensor_touch);
					ev3Sensor = EV3Sensor.Sensor.TOUCH;
					break;
				case R.string.formula_editor_sensor_lego_ev3_sensor_infrared:
					title = getString(R.string.ev3_sensor_infrared);
					ev3Sensor = EV3Sensor.Sensor.INFRARED;
					break;
				case R.string.formula_editor_sensor_lego_ev3_sensor_color:
					title = getString(R.string.ev3_sensor_color);
					ev3Sensor = EV3Sensor.Sensor.COLOR;
					break;
				case R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient:
					title = getString(R.string.ev3_sensor_color_ambient);
					ev3Sensor = EV3Sensor.Sensor.COLOR_AMBIENT;
					break;
				case R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected:
					title = getString(R.string.ev3_sensor_color_reflected);
					ev3Sensor = EV3Sensor.Sensor.COLOR_REFLECT;
					break;
				default:
					title = getString(R.string.nxt_sensor_not_found);
					ev3Sensor = EV3Sensor.Sensor.NO_SENSOR;
			}
		}
	}

	private void checkIfOverwrite(final int selectedPort, String selected, final Lego legoType) {
		final Context dialogueContext = getActivity();
		if (selected.equals(getString(R.string.nxt_no_sensor))) { // nxt_no_sensor equals ev3_no_sensor
			overwrite(dialogueContext, selectedPort, legoType);
		} else if (!selected.equals(title)) {
			AlertDialog.Builder builder = new CustomAlertDialogBuilder(dialogueContext);
			builder.setTitle(R.string.lego_nxt_overwrite_sensor_port_dialog_title)
					.setMessage(R.string.lego_nxt_overwrite_sensor_port_dialog_message)
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String sensor = legoType == Lego.NXT ? SettingsActivity.NXT_SENSORS[selectedPort]
									: SettingsActivity.EV3_SENSORS[selectedPort];
							if (dialogueContext != null) {
								if (legoType == Lego.NXT) {
									SettingsActivity.setLegoMindstormsNXTSensorMapping(dialogueContext, nxtSensor, sensor);
								} else {
									SettingsActivity.setLegoMindstormsEV3SensorMapping(dialogueContext, ev3Sensor, sensor);
								}
							} else {
								Log.e(TAG, "Context == null :(");
							}
						}
					}).create().show();
		}
		getTargetFragment().onActivityResult(getTargetRequestCode(), selectedPort, getActivity().getIntent().setType(legoTypeString));
	}

	private void overwrite(Context context, int selectedPort, Lego legoType) {
		String sensor = legoType == Lego.NXT ? SettingsActivity.NXT_SENSORS[selectedPort]
				: SettingsActivity.EV3_SENSORS[selectedPort];
		if (getActivity() != null) {
			if (legoType == Lego.NXT) {
				SettingsActivity.setLegoMindstormsNXTSensorMapping(context, nxtSensor, sensor);
			} else {
				SettingsActivity.setLegoMindstormsEV3SensorMapping(context, ev3Sensor, sensor);
			}
		} else {
			Log.e(TAG, "Context == null :(");
		}
	}
}
