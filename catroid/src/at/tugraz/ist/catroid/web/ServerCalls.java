/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.UtilDeviceInfo;
import at.tugraz.ist.catroid.utils.Utils;

public class ServerCalls {
	private final static String TAG = "ServerCalls";

	public static boolean useTestUrl = false;
	protected static String resultString;

	// mock object testing
	protected static ConnectionWrapper createConnection() {
		return new ConnectionWrapper();
	}

	public static boolean uploadProject(String projectName, String projectDescription, String zipFileString)
			throws WebconnectionException {
		try {
			HashMap<String, String> postValues = buildPostValues(projectName, projectDescription, new File(
					zipFileString));

			String serverUrl = useTestUrl ? Consts.TEST_FILE_UPLOAD_URL : Consts.FILE_UPLOAD_URL;

			resultString = createConnection().doHttpPostFileUpload(serverUrl, postValues, Consts.FILE_UPLOAD_TAG,
					zipFileString);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "out: " + resultString);
			try {
				jsonObject = new JSONObject(resultString);
				statusCode = jsonObject.getInt("statusCode");
				String serverAnswer = jsonObject.getString("answer");
				int serverProjectId = jsonObject.getInt("projectId");
				ProjectManager.getInstance().setServerProjectId(serverProjectId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (statusCode == 200) {
				return true;
			} else if (statusCode >= 500) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(0);
		}
		return false;
	}

	private static HashMap<String, String> buildPostValues(String projectName, String projectDescription, File zipFile)
			throws IOException {
		String md5Checksum = Utils.md5Checksum(zipFile);

		HashMap<String, String> postValues = new HashMap<String, String>();
		postValues.put(Consts.PROJECT_NAME_TAG, projectName);
		postValues.put(Consts.PROJECT_DESCRIPTION_TAG, projectDescription);
		postValues.put(Consts.PROJECT_CHECKSUM_TAG, md5Checksum.toLowerCase());
		postValues.put(Consts.TOKEN, token); //anonymous

		String deviceIMEI = UtilDeviceInfo.getDeviceIMEI(context);
		if (deviceIMEI != null) {
			postValues.put(Consts.DEVICE_IMEI, deviceIMEI);
		}

		String userEmail = UtilDeviceInfo.getUserEmail(context);
		if (userEmail != null) {
			postValues.put(Consts.USER_EMAIL, userEmail);
		}

		String language = UtilDeviceInfo.getUserLanguageCode(context);
		if (language != null) {
			postValues.put(Consts.USER_LANGUAGE, language);
		}

		return postValues;
	}
}
