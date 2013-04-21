/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.web;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

public class ServerCalls {
	private final static String TAG = "ServerCalls";

	private static final String REG_USER_NAME = "registrationUsername";
	private static final String REG_USER_PASSWORD = "registrationPassword";
	private static final String REG_USER_COUNTRY = "registrationCountry";
	private static final String REG_USER_LANGUAGE = "registrationLanguage";
	private static final String REG_USER_EMAIL = "registrationEmail";
	private static final String LOGIN_USERNAME = "username";

	private static final String FILE_UPLOAD_TAG = "upload";
	private static final String PROJECT_NAME_TAG = "projectTitle";
	private static final String PROJECT_DESCRIPTION_TAG = "projectDescription";
	private static final String PROJECT_CHECKSUM_TAG = "fileChecksum";
	private static final String USER_EMAIL = "userEmail";
	private static final String USER_LANGUAGE = "userLanguage";
	private static final String CATROID_FILE_NAME = "catroidFileName";

	private static final int SERVER_RESPONSE_TOKEN_OK = 200;
	private static final int SERVER_RESPONSE_REGISTER_OK = 201;

	public static final String BASE_URL_HTTP = "http://www.catroid.org/";
	public static final String BASE_URL_FTP = "catroid.org";
	public static final int FTP_PORT = 8080;

	private static final String FILE_UPLOAD_URL = BASE_URL_FTP;
	private static final String CHECK_TOKEN_URL = BASE_URL_HTTP + "api/checkToken/check.json";
	public static final String REGISTRATION_URL = BASE_URL_HTTP + "api/loginOrRegister/loginOrRegister.json";

	public static final String BASE_URL_TEST_HTTP = "http://catroidtest.ist.tugraz.at/";
	public static final String BASE_URL_TEST_FTP = "catroidtest.ist.tugraz.at";

	public static final String TEST_FILE_UPLOAD_URL_HTTP = BASE_URL_TEST_HTTP + "api/upload/upload.json";
	public static final String FILE_UPLOAD_URL_HTTP = BASE_URL_HTTP + "api/upload/upload.json";

	public static final String TEST_FILE_UPLOAD_URL = BASE_URL_TEST_FTP;
	private static final String TEST_CHECK_TOKEN_URL = BASE_URL_TEST_HTTP + "api/checkToken/check.json";
	private static final String TEST_REGISTRATION_URL = BASE_URL_TEST_HTTP + "api/loginOrRegister/loginOrRegister.json";

	public static final int TOKEN_LENGTH = 32;
	public static final String TOKEN_CODE_INVALID = "-1";

	private static ServerCalls instance;
	public static boolean useTestUrl = false;
	private String resultString;
	private ConnectionWrapper connection;
	private String emailForUiTests;
	private int uploadStatusCode;

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

	public void uploadProject(String projectName, String projectDescription, String zipFileString, String userEmail,
			String language, String token, String username, ResultReceiver receiver, Integer notificationId)
			throws WebconnectionException {
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
			postValues.put(Constants.TOKEN, token);
			postValues.put(Constants.USERNAME, username);
			postValues.put(CATROID_FILE_NAME, projectName + ".catrobat");

			if (language != null) {
				postValues.put(USER_LANGUAGE, language);
			}

			String serverUrl = useTestUrl ? TEST_FILE_UPLOAD_URL : FILE_UPLOAD_URL;
			String httpPostUrl = useTestUrl ? TEST_FILE_UPLOAD_URL_HTTP : FILE_UPLOAD_URL_HTTP;

			Log.v(TAG, "url to upload: " + serverUrl);
			String answer = connection.doFtpPostFileUpload(serverUrl, postValues, FILE_UPLOAD_TAG, zipFileString,
					receiver, httpPostUrl, notificationId);

			// check statusCode from Webserver
			JSONObject jsonObject = null;
			Log.v(TAG, "result string: " + answer);
			jsonObject = new JSONObject(answer);
			uploadStatusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.optString("answer");

			if (uploadStatusCode != 200) {
				throw new WebconnectionException(uploadStatusCode, serverAnswer);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK);
		}
	}

	public void downloadProject(String downloadUrl, String zipFileString, ResultReceiver receiver,
			Integer notificationId, String projectName) throws WebconnectionException {
		try {
			connection.doHttpPostFileDownload(downloadUrl, null, zipFileString, receiver, notificationId, projectName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new WebconnectionException(0);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(0);
		}

	}

	public boolean checkToken(String token, String username) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(Constants.TOKEN, token);
			postValues.put(LOGIN_USERNAME, username);

			String serverUrl = useTestUrl ? TEST_CHECK_TOKEN_URL : CHECK_TOKEN_URL;

			Log.v(TAG, "post values - token:" + token + "user: " + username);
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
		}

		catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK);
		}
	}

	public boolean registerOrCheckToken(String username, String password, String userEmail, String language,
			String country, String token, Context context) throws WebconnectionException {
		if (emailForUiTests != null) {
			userEmail = emailForUiTests;
		}
		try {
			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(REG_USER_NAME, username);
			postValues.put(REG_USER_PASSWORD, password);
			postValues.put(REG_USER_EMAIL, userEmail);
			if (token != Constants.NO_TOKEN) {
				postValues.put(Constants.TOKEN, token);
			}

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
			String tokenReceived = "";

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.optString("answer");

			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				tokenReceived = jsonObject.getString("token");
				if (tokenReceived.length() != TOKEN_LENGTH || tokenReceived.isEmpty()
						|| tokenReceived.equals(TOKEN_CODE_INVALID)) {
					throw new WebconnectionException(statusCode, serverAnswer);
				}
				if (context != null) {
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
					sharedPreferences.edit().putString(Constants.TOKEN, tokenReceived).commit();
					sharedPreferences.edit().putString(Constants.USERNAME, username).commit();
				}
			}

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
