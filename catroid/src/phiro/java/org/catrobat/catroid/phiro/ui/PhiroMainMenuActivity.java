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

package org.catrobat.catroid.phiro.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;

import java.io.IOException;

public class PhiroMainMenuActivity extends MainMenuActivity {

	public static final String PHIRO_INITIALIZED = "phiro_initialized";

	private static final String TAG = PhiroMainMenuActivity.class.getSimpleName();

	public static String[] phiroProjects = {
			"BETT dice phiro program",
			"BETT DIRECTION DETECT",
			"BETT DRIVE APP",
			"BETT FACE DETECTION",
			"phiro draw sequential mode",
			"loudness control"
	};

	@Override
	protected void onResume() {
		super.onResume();

		enablePhiro();
		loadPhiroProjects();
	}

	private void loadPhiroProjects() {
		for (String project : phiroProjects) {
			if (StorageHandler.getInstance().projectExists(project)) {
				continue;
			}

			String zipFileName = project + ".zip";
			try {
				UtilFile.copyAssetProjectZipFile(this, zipFileName, Constants.TMP_PATH);
				UtilZip.unZipFile(Constants.TMP_PATH + "/" + zipFileName, Constants.DEFAULT_ROOT + "/" + project);
				UtilFile.deleteFile(Constants.TMP_PATH + "/" + zipFileName);
			} catch (IOException exception) {
				Log.e(TAG, "Could not load phiro project " + project);
				ToastUtil.showError(this, R.string.error_load_project);
			}
		}
	}

	private void enablePhiro() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (!sharedPreferences.getBoolean(PHIRO_INITIALIZED, false)) {
			SettingsActivity.setPhiroSharedPreferenceEnabled(this, true);
			setPhiroInitialized();
		}
	}

	private void setPhiroInitialized() {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean(PHIRO_INITIALIZED, true);
		editor.apply();
	}
}
