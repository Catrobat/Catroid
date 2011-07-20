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
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.Utils;

public class ServerCalls {
	private final static String TAG = "ServerCalls";

	private static ServerCalls instance;
	public static boolean useTestUrl = false;
	protected String resultString;
	private ConnectionWrapper connection;

	// protected constructor to prevent direct instancing
	protected ServerCalls() {
		connection = new ConnectionWrapper();
	}

	public static ServerCalls getInstance() {
		if (instance == null) {
			instance = new ServerCalls();
		}
		return instance;
	}

	// used for mock object testing
	public void setConnectionToUse(ConnectionWrapper connection) {
		this.connection = connection;
	}

	public String uploadProject(String projectName, String projectDescription, String zipFileString, String userEmail,
			String language, String token) throws WebconnectionException {
		try {
			String md5Checksum = Utils.md5Checksum(new File(zipFileString));

			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(Consts.PROJECT_NAME_TAG, projectName);
			postValues.put(Consts.PROJECT_DESCRIPTION_TAG, projectDescription);
			postValues.put(Consts.PROJECT_CHECKSUM_TAG, md5Checksum.toLowerCase());
			postValues.put(Consts.TOKEN, token);

			if (userEmail != null) {
				postValues.put(Consts.USER_EMAIL, userEmail);
			}
			if (language != null) {
				postValues.put(Consts.USER_LANGUAGE, language);
			}
			String serverUrl = useTestUrl ? Consts.TEST_FILE_UPLOAD_URL : Consts.FILE_UPLOAD_URL;

			Log.v(TAG, "url to upload: " + serverUrl);
			resultString = connection
					.doHttpPostFileUpload(serverUrl, postValues, Consts.FILE_UPLOAD_TAG, zipFileString);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.getString("answer");

			if (statusCode == 200) {
				return serverAnswer;
			} else {
				throw new WebconnectionException(0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new WebconnectionException(0);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(0);
		}
	}

	public void downloadProject(String downloadUrl, String zipFileString) throws WebconnectionException {
		try {
			connection.doHttpPostFileDownload(downloadUrl, null, zipFileString);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(0);
		}
	}

}
