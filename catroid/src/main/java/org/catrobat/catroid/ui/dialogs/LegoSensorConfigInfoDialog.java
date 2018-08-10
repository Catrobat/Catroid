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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Constants.LegoSensorType;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED;

public class LegoSensorConfigInfoDialog extends DialogFragment {
	public static final String DIALOG_FRAGMENT_TAG = "dialog_lego_sensor_config_info";

	private @LegoSensorType int legoSensorType;

	public LegoSensorConfigInfoDialog() {
	}

	public LegoSensorConfigInfoDialog(@LegoSensorType int legoSensorType) {
		this.legoSensorType = legoSensorType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isRestoringPreviouslyDestroyedActivity = savedInstanceState != null;
		if (isRestoringPreviouslyDestroyedActivity) {
			dismiss();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int titleStringResId;
		int infoStringResId;
		Enum[] sensorMapping;
		String[] sensorMappingStrings;
		switch (legoSensorType) {
			case Constants.NXT:
				titleStringResId = R.string.lego_nxt_sensor_config_info_title;
				infoStringResId = R.string.lego_nxt_sensor_config_info_text;
				sensorMapping = SettingsFragment.getLegoNXTSensorMapping(getActivity());
				sensorMappingStrings = getResources().getStringArray(R.array.nxt_sensor_chooser);
				break;

			case Constants.EV3:
				titleStringResId = R.string.lego_ev3_sensor_config_info_title;
				infoStringResId = R.string.lego_ev3_sensor_config_info_text;
				sensorMapping = SettingsFragment.getLegoEV3SensorMapping(getActivity());
				sensorMappingStrings = getResources().getStringArray(R.array.ev3_sensor_chooser);
				break;

			default:
				throw new IllegalArgumentException("LegoSensorConfigInfoDialog: Constructor called with invalid sensor type");
		}

		View dialogView = View.inflate(getActivity(), R.layout.dialog_lego_sensor_config_info, null);

		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_text)).setText(infoStringResId);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_1_mapping))
				.setText(sensorMappingStrings[sensorMapping[0].ordinal()]);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_2_mapping))
				.setText(sensorMappingStrings[sensorMapping[1].ordinal()]);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_3_mapping))
				.setText(sensorMappingStrings[sensorMapping[2].ordinal()]);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_4_mapping))
				.setText(sensorMappingStrings[sensorMapping[3].ordinal()]);

		final CheckBox disableShowInfoDialog = dialogView.findViewById(R.id.lego_sensor_config_info_disable_show_dialog);
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setView(dialogView)
				.setTitle(titleStringResId)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (disableShowInfoDialog.isChecked()) {
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
									LegoSensorConfigInfoDialog.this.getActivity()).edit();
							if (LegoSensorConfigInfoDialog.this.legoSensorType == Constants.NXT) {
								editor.putBoolean(SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, true);
							} else if (LegoSensorConfigInfoDialog.this.legoSensorType == Constants.EV3) {
								editor.putBoolean(SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED, true);
							}
							editor.commit();
						}
					}
				}).create();

		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
}
