/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context;
import android.content.DialogInterface;

import com.google.common.collect.ImmutableMap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

public final class LegoSensorPortConfigDialog extends AlertDialog {

	private LegoSensorPortConfigDialog(@NonNull Context context) {
		super(context);
	}

	public static class Builder extends AlertDialog.Builder {

		private class SensorInfo {
			int titleResId;
			Enum sensor;

			SensorInfo(int titleResId, Enum sensor) {
				this.titleResId = titleResId;
				this.sensor = sensor;
			}
		}

		private SensorInfo sensorInfo;

		private Map<Integer, SensorInfo> sensorInfoMap = ImmutableMap.<Integer, SensorInfo>builder()
				.put(R.string.formula_editor_sensor_lego_nxt_touch,
						new SensorInfo(R.string.nxt_sensor_touch, NXTSensor.Sensor.TOUCH))
				.put(R.string.formula_editor_sensor_lego_nxt_sound,
						new SensorInfo(R.string.nxt_sensor_sound, NXTSensor.Sensor.SOUND))
				.put(R.string.formula_editor_sensor_lego_nxt_light,
						new SensorInfo(R.string.nxt_sensor_light, NXTSensor.Sensor.LIGHT_INACTIVE))
				.put(R.string.formula_editor_sensor_lego_nxt_light_active,
						new SensorInfo(R.string.nxt_sensor_light_active, NXTSensor.Sensor.LIGHT_ACTIVE))
				.put(R.string.formula_editor_sensor_lego_nxt_ultrasonic,
						new SensorInfo(R.string.nxt_sensor_ultrasonic, NXTSensor.Sensor.ULTRASONIC))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_touch,
						new SensorInfo(R.string.ev3_sensor_touch, EV3Sensor.Sensor.TOUCH))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_infrared,
						new SensorInfo(R.string.ev3_sensor_infrared, EV3Sensor.Sensor.INFRARED))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_color,
						new SensorInfo(R.string.ev3_sensor_color, EV3Sensor.Sensor.COLOR))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient,
						new SensorInfo(R.string.ev3_sensor_color_ambient, EV3Sensor.Sensor.COLOR_AMBIENT))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected,
						new SensorInfo(R.string.ev3_sensor_color_reflected, EV3Sensor.Sensor.COLOR_REFLECT))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color,
						new SensorInfo(R.string.ev3_sensor_hitechnic_color, EV3Sensor.Sensor.HT_NXT_COLOR))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c,
						new SensorInfo(R.string.ev3_sensor_nxt_temperature_c, EV3Sensor.Sensor.NXT_TEMPERATURE_C))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f,
						new SensorInfo(R.string.ev3_sensor_nxt_temperature_f, EV3Sensor.Sensor.NXT_TEMPERATURE_F))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light,
					new SensorInfo(R.string.ev3_sensor_nxt_light, EV3Sensor.Sensor.NXT_LIGHT))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light_active,
					new SensorInfo(R.string.ev3_sensor_nxt_light_active, EV3Sensor.Sensor.NXT_LIGHT_ACTIVE))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_sound,
					new SensorInfo(R.string.ev3_sensor_nxt_sound, EV3Sensor.Sensor.NXT_SOUND))
				.put(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_ultrasonic,
					new SensorInfo(R.string.ev3_sensor_nxt_ultrasonic, EV3Sensor.Sensor.NXT_ULTRASONIC))
				.build();

		SensorInfo getSensorInfo(int selectedItem, @Constants.LegoSensorType int type) {
			SensorInfo info = sensorInfoMap.get(selectedItem);
			Enum sensor = type == Constants.NXT ? NXTSensor.Sensor.NO_SENSOR : EV3Sensor.Sensor.NO_SENSOR;
			return info != null ? info : new SensorInfo(R.string.nxt_sensor_not_found, sensor);
		}

		public Builder(@NonNull Context context, @Constants.LegoSensorType int legoType, @StringRes int selectedItem) {
			super(context);

			sensorInfo = getSensorInfo(selectedItem, legoType);

			final Enum[] sensorsByPort = legoType == Constants.NXT
					? SettingsFragment.getLegoNXTSensorMapping(context)
					: SettingsFragment.getLegoEV3SensorMapping(context);

			String[] portNames = context.getResources().getStringArray(R.array.port_chooser);
			String[] dialogItems = new String[portNames.length];

			final String[] sensorNames = context.getResources()
					.getStringArray(legoType == Constants.NXT ? R.array.nxt_sensor_chooser : R.array.ev3_sensor_chooser);

			for (int portNumber = 0; portNumber < portNames.length; portNumber++) {
				int sensorNameIndex = sensorsByPort[portNumber].ordinal();
				dialogItems[portNumber] = portNames[portNumber] + ": " + sensorNames[sensorNameIndex];
			}

			setTitle(context.getString(R.string.lego_sensor_port_config_dialog_title,
					context.getString(sensorInfo.titleResId)));
			setSingleChoiceItems(dialogItems, -1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int selectedPort) {
					((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
				}
			});
		}

		public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
			setPositiveButton(text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog alertDialog = (AlertDialog) dialog;
					int selectedPort = alertDialog.getListView().getCheckedItemPosition();
					Enum selectedSensor = sensorInfo.sensor;
					listener.onPositiveButtonClick(dialog, selectedPort, selectedSensor);
				}
			});
			return this;
		}

		@Override
		public AlertDialog create() {
			final AlertDialog alertDialog = super.create();
			alertDialog.setOnShowListener(new OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				}
			});
			return alertDialog;
		}
	}

	public interface OnClickListener {

		void onPositiveButtonClick(DialogInterface dialog, int selectedPort, Enum selectedSensor);
	}
}
