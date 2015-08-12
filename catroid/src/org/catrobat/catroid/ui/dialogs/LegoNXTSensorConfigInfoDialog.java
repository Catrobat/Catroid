/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.ui.SettingsActivity;

public class LegoNXTSensorConfigInfoDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_lego_nxt_sensor_config_info";

	private CheckBox disableShowInfoDialog;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_lego_nxt_sensor_config_info, null);

		disableShowInfoDialog = (CheckBox) dialogView.findViewById(R.id.lego_nxt_sensor_config_info_disable_show_dialog);

		NXTSensor.Sensor[] sensorMapping = SettingsActivity.getLegoMindstormsNXTSensorMapping(this.getActivity());
		String[] sensorMappingStrings = getResources().getStringArray(R.array.nxt_sensor_chooser);

		TextView mapping1 = (TextView) dialogView.findViewById(R.id.lego_nxt_sensor_config_info_port_1_mapping);
		TextView mapping2 = (TextView) dialogView.findViewById(R.id.lego_nxt_sensor_config_info_port_2_mapping);
		TextView mapping3 = (TextView) dialogView.findViewById(R.id.lego_nxt_sensor_config_info_port_3_mapping);
		TextView mapping4 = (TextView) dialogView.findViewById(R.id.lego_nxt_sensor_config_info_port_4_mapping);

		mapping1.setText(sensorMappingStrings[sensorMapping[0].ordinal()]);
		mapping2.setText(sensorMappingStrings[sensorMapping[1].ordinal()]);
		mapping3.setText(sensorMappingStrings[sensorMapping[2].ordinal()]);
		mapping4.setText(sensorMappingStrings[sensorMapping[3].ordinal()]);

		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setView(dialogView)
				.setTitle(R.string.lego_nxt_sensor_config_info_title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (disableShowInfoDialog.isChecked()) {
							SettingsActivity.disableLegoMindstormsSensorInfoDialog(LegoNXTSensorConfigInfoDialog.this.getActivity());
						}
					}
				}).create();

		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}
}
