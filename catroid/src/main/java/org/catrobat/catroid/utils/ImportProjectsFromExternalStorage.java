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

package org.catrobat.catroid.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import org.catrobat.catroid.R;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG;

public class ImportProjectsFromExternalStorage implements DialogInterface.OnClickListener {

	public static final String TAG = ImportProjectsFromExternalStorage.class.getSimpleName();
	private Activity activity;

	public ImportProjectsFromExternalStorage(Activity mActivity) {
		activity = mActivity;
	}

	private AlertDialog.Builder createImportProjectsDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		alertDialog.setTitle(R.string.import_dialog_title)
				.setCancelable(false)
				.setMessage(R.string.import_dialog_message)
				.setPositiveButton(R.string.import_dialog_move_btn, this)
				.setNeutralButton(R.string.import_dialog_copy_btn, this);
		return alertDialog;
	}

	public void showImportProjectsDialog() {
		AlertDialog.Builder alertDialog = createImportProjectsDialog();
		boolean showDialog = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, true);
		if (showDialog) {
			alertDialog.show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				CopyProjectsTask moveProjectsTask = new CopyProjectsTask(activity, true);
				moveProjectsTask.execute();
				doNotShowDialogAgain();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				CopyProjectsTask copyProjectsTask = new CopyProjectsTask(activity, false);
				copyProjectsTask.execute();
				doNotShowDialogAgain();
				break;
		}
	}

	private void doNotShowDialogAgain() {
		PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, false).apply();
	}
}
