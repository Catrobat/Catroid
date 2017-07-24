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

package org.catrobat.catroid.createatschool.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.Utils;

import static org.catrobat.catroid.utils.IconsUtil.isLargeSize;

public class CreateAtSchoolMainMenuActivity extends MainMenuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isLargeSize()) {
			IconsUtil.setLeftDrawableSize(getApplicationContext(), this.findViewById(android.R.id.content),
					IconsUtil.getLargeIconSizeMainMenu(), R.id.main_menu_button_templates, R.drawable.ic_main_menu_templates);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem settingsMenuItem = menu.findItem(R.id.settings);
		if (settingsMenuItem != null) {
			settingsMenuItem.setVisible(true);
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Utils.isUserLoggedIn(this) || !Utils.isCreateAtSchoolUser(this)) {
			if (!Utils.isNetworkAvailable(this)) {
				AlertDialog noInternetDialog = new CustomAlertDialogBuilder(this)
						.setTitle(R.string.no_internet)
						.setMessage(R.string.error_no_internet)
						.setPositiveButton(R.string.ok, null)
						.setCancelable(false)
						.show();

				noInternetDialog.setCanceledOnTouchOutside(false);

				Button okButton = noInternetDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				okButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						finish();
					}
				});
			} else {
				ProjectManager.getInstance().showLogInDialog(this, false);
			}
		}
	}

	public void handleTemplatesButton(View view) {
		findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Intent intent = new Intent(this, TemplatesActivity.class);
		startActivity(intent);
	}
}
