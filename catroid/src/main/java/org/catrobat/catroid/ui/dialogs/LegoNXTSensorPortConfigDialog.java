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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.TextSizeUtil;

public class LegoNXTSensorPortConfigDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_lego_port_config";
	public static final String TAG = LegoNXTSensorPortConfigDialog.class.getSimpleName();
	private int clickedItem = 0;
	private String title;
	private NXTSensor.Sensor nxtSensor;
	private String[] sensorMappingStrings;
	private String[] sensorMappings;
	private NXTSensor.Sensor[] sensorMapping;

	public LegoNXTSensorPortConfigDialog(int clickedResItem) {
		clickedItem = clickedResItem;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		sensorMapping = SettingsActivity.getLegoMindstormsNXTSensorMapping(this.getActivity());
		sensorMappingStrings = getResources().getStringArray(R.array.nxt_sensor_chooser);
		String[] portStrings = getResources().getStringArray(R.array.nxt_port_chooser);

		sensorMappings = new String[portStrings.length];

		for (int index = 0; index < sensorMappings.length; index++) {
			sensorMappings[index] = portStrings[index] + ": " + sensorMappingStrings[sensorMapping[index].ordinal()];
		}

		getAttributes(clickedItem);

		final Dialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setItems(sensorMappings, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						checkIfOverwrite(which);
					}
				}).create();

		dialog.setCanceledOnTouchOutside(true);

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				if (getActivity() == null) {
					Log.e(DIALOG_FRAGMENT_TAG, "onShow() Activity was null!");
					return;
				}

				TextSizeUtil.enlargeViewGroup((ViewGroup) dialog.getWindow().getDecorView().getRootView());
			}
		});

		return dialog;
	}

	private void getAttributes(int clickedItem) {
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
	}

	private void checkIfOverwrite(final int selectedPort) {
		String selected = sensorMappingStrings[sensorMapping[selectedPort].ordinal()];
		final Context dialogueContext = getActivity();
		if (selected.equals(getString(R.string.nxt_no_sensor))) {
			overwrite(dialogueContext, selectedPort);
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
							String sensor = SettingsActivity.NXT_SENSORS[selectedPort];
							if (dialogueContext != null) {
								SettingsActivity.setLegoMindstormsNXTSensorMapping(dialogueContext, nxtSensor, sensor);
							} else {
								Log.e(TAG, "Context == null :(");
							}
						}
					}).create().show();
		}
		getTargetFragment().onActivityResult(getTargetRequestCode(), selectedPort, getActivity().getIntent());
	}

	private void overwrite(Context context, int selectedPort) {
		String sensor = SettingsActivity.NXT_SENSORS[selectedPort];
		if (getActivity() != null) {
			SettingsActivity.setLegoMindstormsNXTSensorMapping(context, nxtSensor, sensor);
		} else {
			Log.e(TAG, "Context == null :(");
		}
	}
}
