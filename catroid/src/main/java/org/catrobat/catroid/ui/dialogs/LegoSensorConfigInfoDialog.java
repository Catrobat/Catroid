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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Constants.LegoSensorType;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED;

public class LegoSensorConfigInfoDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = LegoSensorConfigInfoDialog.class.getSimpleName();
	private static final String BUNDLE_KEY_SENSOR_TYPE = "legoSensorType";

	public static LegoSensorConfigInfoDialog newInstance(@LegoSensorType int legoSensorType) {
		LegoSensorConfigInfoDialog dialog = new LegoSensorConfigInfoDialog();

		Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_KEY_SENSOR_TYPE, legoSensorType);
		dialog.setArguments(bundle);

		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int titleStringResId;
		int infoStringResId;

		Enum[] sensorMapping;
		String[] sensorMappingStrings;

		if (getArguments() == null) {
			dismiss();
		}

		final int legoSensorType = getArguments().getInt(BUNDLE_KEY_SENSOR_TYPE, Constants.NXT);

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

		final View dialogView = View.inflate(getActivity(), R.layout.dialog_lego_sensor_config_info, null);
		final CheckBox disableShowInfoDialog = dialogView.findViewById(R.id.lego_sensor_config_info_disable_show_dialog);

		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_text))
				.setText(infoStringResId);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_1_mapping))
				.setText(sensorMappingStrings[sensorMapping[0].ordinal()]);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_2_mapping))
				.setText(sensorMappingStrings[sensorMapping[1].ordinal()]);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_3_mapping))
				.setText(sensorMappingStrings[sensorMapping[2].ordinal()]);
		((TextView) dialogView.findViewById(R.id.lego_sensor_config_info_port_4_mapping))
				.setText(sensorMappingStrings[sensorMapping[3].ordinal()]);

		return new AlertDialog.Builder(getActivity())
				.setTitle(titleStringResId)
				.setView(dialogView)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						if (disableShowInfoDialog.isChecked()) {
							String preferenceKey = legoSensorType == Constants.NXT
									? SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED
									: SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED;

							PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
									.putBoolean(preferenceKey, true)
									.apply();
						}
					}
				})
				.create();
	}
}
