/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;

import com.google.common.collect.ImmutableMap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public class LegoSensorPortConfigDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_lego_sensor_port_config";
	public static final String TAG = LegoSensorPortConfigDialog.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NXT, EV3})
	public @interface LegoType {}
	public static final int NXT = 0;
	public static final int EV3 = 1;

	private OnSetSensorListener listener;
	private @LegoType int legoType;
	private SensorInfo sensorInfo;

	private class SensorInfo {
		String title;
		Enum sensor;

		SensorInfo(String title, Enum sensor) {
			this.title = title;
			this.sensor = sensor;
		}
	}

	private Map<Integer, SensorInfo> sensorInfoMap = ImmutableMap.<Integer, SensorInfo>builder()
			.put(R.string.formula_editor_sensor_lego_nxt_touch,
					new SensorInfo(getString(R.string.nxt_sensor_touch), NXTSensor.Sensor.TOUCH))
			.put(R.string.formula_editor_sensor_lego_nxt_sound,
					new SensorInfo(getString(R.string.nxt_sensor_sound), NXTSensor.Sensor.SOUND))
			.put(R.string.formula_editor_sensor_lego_nxt_light,
					new SensorInfo(getString(R.string.nxt_sensor_light), NXTSensor.Sensor.LIGHT_INACTIVE))
			.put(R.string.formula_editor_sensor_lego_nxt_light_active,
					new SensorInfo(getString(R.string.nxt_sensor_light_active), NXTSensor.Sensor.LIGHT_ACTIVE))
			.put(R.string.formula_editor_sensor_lego_nxt_ultrasonic,
					new SensorInfo(getString(R.string.nxt_sensor_ultrasonic), NXTSensor.Sensor.ULTRASONIC))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_touch,
					new SensorInfo(getString(R.string.ev3_sensor_touch), EV3Sensor.Sensor.TOUCH))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_infrared,
					new SensorInfo(getString(R.string.ev3_sensor_infrared), EV3Sensor.Sensor.INFRARED))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_color,
					new SensorInfo(getString(R.string.ev3_sensor_color), EV3Sensor.Sensor.COLOR))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient,
					new SensorInfo(getString(R.string.ev3_sensor_color_ambient), EV3Sensor.Sensor.COLOR_AMBIENT))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected,
					new SensorInfo(getString(R.string.ev3_sensor_color_reflected), EV3Sensor.Sensor.COLOR_REFLECT))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color,
					new SensorInfo(getString(R.string.ev3_sensor_hitechnic_color), EV3Sensor.Sensor.HT_NXT_COLOR))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c,
					new SensorInfo(getString(R.string.ev3_sensor_nxt_temperature_c), EV3Sensor.Sensor.NXT_TEMPERATURE_C))
			.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f,
					new SensorInfo(getString(R.string.ev3_sensor_nxt_temperature_f), EV3Sensor.Sensor.NXT_TEMPERATURE_F))
			.build();

	public LegoSensorPortConfigDialog(OnSetSensorListener listener, int clickedResItem, @LegoType int type) {
		this.listener = listener;
		sensorInfo = getSensorInfo(clickedResItem, type);
		legoType = type;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Enum[] sensorMapping = legoType == NXT
				? SettingsFragment.getLegoMindstormsNXTSensorMapping(this.getActivity())
				: SettingsFragment.getLegoMindstormsEV3SensorMapping(this.getActivity());

		String[] portStrings = getResources().getStringArray(R.array.port_chooser);
		String[] sensorMappings = new String[portStrings.length];
		int mappingStringsResId = legoType == NXT ? R.array.nxt_sensor_chooser : R.array.ev3_sensor_chooser;
		final String[] sensorMappingStrings = getResources().getStringArray(mappingStringsResId);

		for (int index = 0; index < sensorMappings.length; index++) {
			sensorMappings[index] = portStrings[index] + ": " + sensorMappingStrings[sensorMapping[index].ordinal()];
		}

		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(sensorInfo.title)
				.setItems(sensorMappings, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						checkIfOverwrite(which, sensorMappingStrings[sensorMapping[which].ordinal()], legoType);
					}
				}).create();

		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}

	private SensorInfo getSensorInfo(int clickedItem, @LegoType int type) {
		SensorInfo info = sensorInfoMap.get(clickedItem);
		Enum sensor = type == NXT ? NXTSensor.Sensor.NO_SENSOR : EV3Sensor.Sensor.NO_SENSOR;
		return info != null ? info : new SensorInfo(getString(R.string.nxt_sensor_not_found), sensor);
	}

	private void checkIfOverwrite(final int selectedPort, String selected, final @LegoType int legoType) {
		if (selected.equals(getString(R.string.nxt_no_sensor))) { // nxt_no_sensor equals ev3_no_sensor
			overwriteSensorPortConfig(selectedPort, legoType);
		} else if (!selected.equals(sensorInfo.title)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.lego_nxt_overwrite_sensor_port_dialog_title)
					.setMessage(R.string.lego_nxt_overwrite_sensor_port_dialog_message)
					.setNegativeButton(R.string.no, null)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LegoSensorPortConfigDialog.this.overwriteSensorPortConfig(selectedPort, legoType);
						}
					}).create().show();
		}
		listener.onSetSensor(selectedPort, legoType);
	}

	private void overwriteSensorPortConfig(int selectedPort, @LegoType int legoType) {
		if (legoType == NXT) {
			SettingsFragment.setLegoMindstormsNXTSensorMapping(getActivity(), (NXTSensor.Sensor) sensorInfo.sensor,
					SettingsFragment.NXT_SENSORS[selectedPort]);
		} else if (legoType == EV3) {
			SettingsFragment.setLegoMindstormsEV3SensorMapping(getActivity(), (EV3Sensor.Sensor) sensorInfo.sensor,
					SettingsFragment.EV3_SENSORS[selectedPort]);
		} else {
			throw new IllegalArgumentException("LegoSensorPortConfigDialog.overwriteSensorPortConfig: Unknown LegoType");
		}
	}

	public interface OnSetSensorListener {
		void onSetSensor(int setPort, @LegoType int type);
	}
}
