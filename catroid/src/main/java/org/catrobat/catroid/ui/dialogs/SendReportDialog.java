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
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.CrashReporter;
import org.catrobat.catroid.utils.Utils;

public class SendReportDialog extends DialogFragment {
	public static final String TAG = "dialog_send_report";

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_send_report, null);

		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxSendReportAutomatically);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingsActivity.setAutoCrashReportingEnabled(getActivity(), isChecked);
			}
		});
		Dialog reportDialog = new AlertDialog.Builder(getActivity()).setView(view).setTitle(getString(R.string
				.pocket_code_has_crashed))
				.setPositiveButton(R.string.crash_dialog_send, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Utils.sendCaughtException(getActivity());
					}
				})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove(
								CrashReporter.EXCEPTION_FOR_REPORT).commit();
					}
				}).create();

		reportDialog.setCanceledOnTouchOutside(false);

		return reportDialog;
	}
}
