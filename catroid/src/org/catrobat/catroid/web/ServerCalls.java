/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

//web status codes are on: https://github.com/Catrobat/Catroweb/blob/master/statusCodes.php

public final class ServerCalls {

	private static final String TAG = ServerCalls.class.getCanonicalName();

	private static final String REGISTRATION_USERNAME_KEY = "registrationUsername";
	private static final String REGISTRATION_PASSWORD_KEY = "registrationPassword";
	private static final String REGISTRATION_COUNTRY_KEY = "registrationCountry";
	private static final String REGISTRATION_EMAIL_KEY = "registrationEmail";
	private static final String LOGIN_USERNAME_KEY = "username";

	private static final String FILE_UPLOAD_TAG = "upload";
	private static final String PROJECT_NAME_TAG = "projectTitle";
	private static final String PROJECT_DESCRIPTION_TAG = "projectDescription";
	private static final String PROJECT_CHECKSUM_TAG = "fileChecksum";
	private static final String USER_EMAIL = "userEmail";
	private static final String DEVICE_LANGUAGE = "deviceLanguage";

	private static final int SERVER_RESPONSE_TOKEN_OK = 200;
	private static final int SERVER_RESPONSE_REGISTER_OK = 201;

	private static final String FILE_UPLOAD_URL = Constants.BASE_URL_HTTPS + "api/upload/upload.json";
	private static final String CHECK_TOKEN_URL = Constants.BASE_URL_HTTPS + "api/checkToken/check.json";
	private static final String REGISTRATION_URL = Constants.BASE_URL_HTTPS + "api/loginOrRegister/loginOrRegister.json";

	public static final String BASE_URL_TEST_HTTP = "https://catroid-test.catrob.at/";

	public static final String TEST_FILE_UPLOAD_URL_HTTP = BASE_URL_TEST_HTTP + "api/upload/upload.json";

	private static final String TEST_CHECK_TOKEN_URL = BASE_URL_TEST_HTTP + "api/checkToken/check.json";
	private static final String TEST_REGISTRATION_URL = BASE_URL_TEST_HTTP + "api/loginOrRegister/loginOrRegister.json";

	public static final int TOKEN_LENGTH = 32;
	public static final String TOKEN_CODE_INVALID = "-1";

	private static final String JSON_STATUS_CODE = "statusCode";
	private static final String JSON_ANSWER = "answer";
	private static final String JSON_TOKEN = "token";

	private static final ServerCalls INSTANCE = new ServerCalls();

	public static boolean useTestUrl = false;
	private String resultString;
	private ConnectionWrapper connection;
	private String emailForUiTests;
	private int uploadStatusCode;

	private ServerCalls() {
		connection = new ConnectionWrapper();
	}

	public static ServerCalls getInstance() {
		return INSTANCE;
	}

	// used for mock object testing
	public void setConnectionToUse(ConnectionWrapper connection) {
		this.connection = connection;
	}

	public void uploadProject(String projectName, String projectDescription, String zipFileString, String userEmail,
			String language, String token, String username, ResultReceiver receiver, Integer notificationId,
			Context context) throws WebconnectionException {
		if (emailForUiTests != null) {
			userEmail = emailForUiTests;
		}

		try {
			String md5Checksum = Utils.md5Checksum(new File(zipFileString));

			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(PROJECT_NAME_TAG, projectName);
			postValues.put(PROJECT_DESCRIPTION_TAG, projectDescription);
			postValues.put(USER_EMAIL, userEmail == null ? "" : userEmail);
			postValues.put(PROJECT_CHECKSUM_TAG, md5Checksum);
			postValues.put(Constants.TOKEN, token);
			postValues.put(Constants.USERNAME, username);

			if (language != null) {
				postValues.put(DEVICE_LANGUAGE, language);
			}

			String serverUrl = useTestUrl ? TEST_FILE_UPLOAD_URL_HTTP : FILE_UPLOAD_URL;

			Log.v(TAG, "url to upload: " + serverUrl);

			String answer = connection.doHttpsPostFileUpload(serverUrl, postValues, FILE_UPLOAD_TAG, zipFileString,
					receiver, notificationId);
			if (answer != null && !answer.isEmpty()) {
				// check statusCode from Webserver
				JSONObject jsonObject = null;
				Log.v(TAG, "result string: " + answer);

				// needed cause of a beautiful test case which gets resultString through reflection :)
				resultString = answer;

				jsonObject = new JSONObject(answer);
				uploadStatusCode = jsonObject.getInt(JSON_STATUS_CODE);
				String serverAnswer = jsonObject.optString(JSON_ANSWER);
				String tokenReceived = "";

				if (uploadStatusCode == SERVER_RESPONSE_TOKEN_OK) {
					tokenReceived = jsonObject.getString(JSON_TOKEN);
					if (tokenReceived.length() != TOKEN_LENGTH || tokenReceived.equals("")
							|| tokenReceived.equals(TOKEN_CODE_INVALID)) {
						throw new WebconnectionException(uploadStatusCode, serverAnswer);
					}
					if (context != null) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
						sharedPreferences.edit().putString(Constants.TOKEN, tokenReceived).commit();
						sharedPreferences.edit().putString(Constants.USERNAME, username).commit();
					}
				} else {
					throw new WebconnectionException(uploadStatusCode, serverAnswer);
				}
			}
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Exception");
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, "IO-Exception");
		}
	}

	public void downloadProject(String downloadUrl, String zipFileString, ResultReceiver receiver,
			Integer notificationId) throws WebconnectionException {

		try {
			connection.doHttpPostFileDownload(downloadUrl, new HashMap<String, String>(), zipFileString, receiver,
					notificationId);
		} catch (MalformedURLException malformedURLException) {
			Log.e(TAG, Log.getStackTraceString(malformedURLException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Malformed URL");
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, "IO-Exception");
		}

	}

	public boolean checkToken(String token, String username) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(Constants.TOKEN, token);
			postValues.put(LOGIN_USERNAME_KEY, username);

			String serverUrl = useTestUrl ? TEST_CHECK_TOKEN_URL : CHECK_TOKEN_URL;

			Log.v(TAG, "post values - token:" + token + "user: " + username);
			Log.v(TAG, "url to upload: " + serverUrl);
			resultString = connection.doHttpPost(serverUrl, postValues);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
				return true;
			} else {
				throw new WebconnectionException(statusCode, "server response token ok, but error: " + serverAnswer);
			}
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Exception");
		}
	}

	public boolean registerOrCheckToken(String username, String password, String userEmail, String language,
			String country, String token, Context context) throws WebconnectionException {
		if (emailForUiTests != null) {
			userEmail = emailForUiTests;
		}
		try {
			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(REGISTRATION_USERNAME_KEY, username);
			postValues.put(REGISTRATION_PASSWORD_KEY, password);
			postValues.put(REGISTRATION_EMAIL_KEY, userEmail);
			if (token != Constants.NO_TOKEN) {
				postValues.put(Constants.TOKEN, token);
			}

			if (country != null) {
				postValues.put(REGISTRATION_COUNTRY_KEY, country);
			}
			if (language != null) {
				postValues.put(DEVICE_LANGUAGE, language);
			}
			String serverUrl = useTestUrl ? TEST_REGISTRATION_URL : REGISTRATION_URL;

			Log.v(TAG, "url to use: " + serverUrl);
			resultString = connection.doHttpPost(serverUrl, postValues);

			JSONObject jsonObject = null;
			int statusCode = 0;
			String tokenReceived = "";

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				tokenReceived = jsonObject.getString(JSON_TOKEN);
				if (tokenReceived.length() != TOKEN_LENGTH || tokenReceived.equals("")
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
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}
}
