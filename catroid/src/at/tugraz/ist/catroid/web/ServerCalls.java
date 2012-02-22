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

	private static final String REG_USER_NAME = "registrationUsername";
	private static final String REG_USER_PASSWORD = "registrationPassword";
	private static final String REG_USER_COUNTRY = "registrationCountry";
	private static final String REG_USER_LANGUAGE = "registrationLanguage";
	private static final String REG_USER_EMAIL = "registrationEmail";

	private static final String FILE_UPLOAD_TAG = "upload";
	private static final String PROJECT_NAME_TAG = "projectTitle";
	private static final String PROJECT_DESCRIPTION_TAG = "projectDescription";
	private static final String PROJECT_CHECKSUM_TAG = "fileChecksum";
	private static final String USER_EMAIL = "userEmail";
	private static final String USER_LANGUAGE = "userLanguage";

	private static final int SERVER_RESPONSE_TOKEN_OK = 200;
	private static final int SERVER_RESPONSE_REGISTER_OK = 201;

	public static final String BASE_URL = "http://www.catroid.org/";
	//public static final String BASE_URL = "http://catroidtest.ist.tugraz.at/";
	private static final String FILE_UPLOAD_URL = BASE_URL + "api/upload/upload.json";
	private static final String CHECK_TOKEN_URL = BASE_URL + "api/checkToken/check.json";
	public static final String REGISTRATION_URL = BASE_URL + "api/checkTokenOrRegister/check.json";

	public static final String BASE_URL_TEST = "http://catroidtest.ist.tugraz.at/";
	public static final String TEST_FILE_UPLOAD_URL = BASE_URL_TEST + "api/upload/upload.json";
	private static final String TEST_CHECK_TOKEN_URL = BASE_URL_TEST + "api/checkToken/check.json";
	private static final String TEST_REGISTRATION_URL = BASE_URL_TEST + "api/checkTokenOrRegister/check.json";

	private static ServerCalls instance;
	public static boolean useTestUrl = false;
	private String resultString;
	private ConnectionWrapper connection;
	private String emailForUiTests;

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
		if (emailForUiTests != null) {
			userEmail = emailForUiTests;
		}
		try {
			String md5Checksum = Utils.md5Checksum(new File(zipFileString));

			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(PROJECT_NAME_TAG, projectName);
			postValues.put(PROJECT_DESCRIPTION_TAG, projectDescription);
			postValues.put(USER_EMAIL, userEmail);
			postValues.put(PROJECT_CHECKSUM_TAG, md5Checksum.toLowerCase());
			postValues.put(Consts.TOKEN, token);

			if (language != null) {
				postValues.put(USER_LANGUAGE, language);
			}
			String serverUrl = useTestUrl ? TEST_FILE_UPLOAD_URL : FILE_UPLOAD_URL;

			Log.v(TAG, "url to upload: " + serverUrl);
			resultString = connection.doHttpPostFileUpload(serverUrl, postValues, FILE_UPLOAD_TAG, zipFileString);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.getString("answer");

			if (statusCode == 200) {
				return serverAnswer;
			} else {
				throw new WebconnectionException(statusCode, serverAnswer);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK);
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

	public boolean checkToken(String token) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(Consts.TOKEN, token);

			String serverUrl = useTestUrl ? TEST_CHECK_TOKEN_URL : CHECK_TOKEN_URL;

			Log.v(TAG, "url to upload: " + serverUrl);
			resultString = connection.doHttpPost(serverUrl, postValues);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.optString("answer");

			if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
				return true;
			} else {
				throw new WebconnectionException(statusCode, serverAnswer);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK);
		}
	}

	public boolean registerOrCheckToken(String username, String password, String userEmail, String language,
			String country, String token) throws WebconnectionException {
		if (emailForUiTests != null) {
			userEmail = emailForUiTests;
		}
		try {

			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(REG_USER_NAME, username);
			postValues.put(REG_USER_PASSWORD, password);
			postValues.put(REG_USER_EMAIL, userEmail);
			postValues.put(Consts.TOKEN, token);

			if (country != null) {
				postValues.put(REG_USER_COUNTRY, country);
			}
			if (language != null) {
				postValues.put(REG_USER_LANGUAGE, language);
			}
			String serverUrl = useTestUrl ? TEST_REGISTRATION_URL : REGISTRATION_URL;

			Log.v(TAG, "url to use: " + serverUrl);
			resultString = connection.doHttpPost(serverUrl, postValues);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.optString("answer");

			boolean registered;
			if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
				registered = false;
			} else if (statusCode == SERVER_RESPONSE_REGISTER_OK) {
				registered = true;
			} else {
				throw new WebconnectionException(statusCode, serverAnswer);
			}
			return registered;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK);
		}
	}

}
