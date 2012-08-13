/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.InstalledAppAdapter;
import at.tugraz.ist.catroid.utils.InstalledApplicationInfo;
import at.tugraz.ist.catroid.utils.Utils;

public class AddCostumeDialog extends Dialog {
	private ScriptTabActivity scriptTabActivity;

	public AddCostumeDialog(ScriptTabActivity scriptTabActivity) {
		super(scriptTabActivity);
		this.scriptTabActivity = scriptTabActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_costume_list_view);
		setTitle(R.string.add_costume_dialog_title);
		setCanceledOnTouchOutside(true);

		PackageManager packageManager = scriptTabActivity.getPackageManager();
		ArrayList<InstalledApplicationInfo> installedAppInfo = Utils.createApplicationsInfoList(packageManager);

		ListView listView = (ListView) findViewById(R.id.listViewInstalledApps);
		listView.setAdapter(new InstalledAppAdapter(scriptTabActivity, R.layout.add_costume_applicationlist_item,
				installedAppInfo));
	}
}