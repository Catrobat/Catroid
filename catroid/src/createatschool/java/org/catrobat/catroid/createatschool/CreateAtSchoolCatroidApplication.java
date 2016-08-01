/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.createatschool;

import android.util.Log;

import com.zed.bdsclient.controller.BDSClientController;
import com.zed.bdsclient.environments.BDSClientEnvironments;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.CatroidApplication;

import java.io.IOException;
import java.io.InputStream;

public class CreateAtSchoolCatroidApplication extends CatroidApplication {

	private static final String TAG = CreateAtSchoolCatroidApplication.class.getSimpleName();

	private static final String NOLB_APP_ID = "appId";
	private static final String NOLB_PASSWORD = "appPassword";
	private static final String NOLB_CONFIG_FILE = "nolb_config.xml";

	@Override
	public void onCreate() {
		super.onCreate();
		initBdsClientController();
	}

	private void initBdsClientController() {
		try {
			InputStream inputStream = context.getAssets().open(NOLB_CONFIG_FILE);
			int size = inputStream.available();
			byte[] buffer = new byte[size];
			inputStream.read(buffer);
			inputStream.close();
			String fileContent = new String(buffer);

			String appId = fileContent.substring(fileContent.indexOf(NOLB_APP_ID) + NOLB_APP_ID.length() + 1,
					fileContent.indexOf("/" + NOLB_APP_ID) - 1);
			String appPassword = fileContent.substring(fileContent.indexOf(NOLB_PASSWORD) + NOLB_PASSWORD.length() + 1,
					fileContent.indexOf("/" + NOLB_PASSWORD) - 1);

			BDSClientController.init(context, BDSClientEnvironments.PRO, appId, appPassword, 1, 0);
			if (BuildConfig.DEBUG) {
				BDSClientController.getInstance().setDebugMode(true);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
