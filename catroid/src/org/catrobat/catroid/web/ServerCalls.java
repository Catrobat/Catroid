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

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.ProjectUploadService;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;

//web status codes are on: https://github.com/Catrobat/Catroweb/blob/master/statusCodes.php

public final class ServerCalls {

	public static final String BASE_URL_TEST_HTTPS = "https://web-test.catrob.at/pocketcode/";
	public static final String TEST_FILE_UPLOAD_URL_HTTP = BASE_URL_TEST_HTTPS + "api/upload/upload.json";
	public static final int TOKEN_LENGTH = 32;
	public static final String TOKEN_CODE_INVALID = "-1";
	private static final String TAG = ServerCalls.class.getSimpleName();
	private static final String REGISTRATION_USERNAME_KEY = "registrationUsername";
	private static final String REGISTRATION_PASSWORD_KEY = "registrationPassword";
	private static final String REGISTRATION_COUNTRY_KEY = "registrationCountry";
	private static final String REGISTRATION_EMAIL_KEY = "registrationEmail";
	private static final String SIGNIN_USERNAME_KEY = "username";
	private static final String SIGNIN_OAUTH_ID_KEY = "id";
	private static final String SIGNIN_EMAIL_KEY = "email";
	private static final String SIGNIN_LOCALE_KEY = "locale";
	private static final String SIGNIN_FACEBOOK_CLIENT_TOKEN_KEY = "client_token";
	private static final String SIGNIN_GOOGLE_CODE_KEY = "code";
	private static final String SIGNIN_STATE = "state"; //not supported yet, but necessary argument for server call
	private static final String OAUTH_TOKEN_AVAILABLE = "token_available";
	private static final String EMAIL_AVAILABLE = "email_available";
	private static final String USERNAME_AVAILABLE = "username_available";
	private static final String FILE_UPLOAD_TAG = "upload";
	private static final String PROJECT_NAME_TAG = "projectTitle";
	private static final String PROJECT_DESCRIPTION_TAG = "projectDescription";
	private static final String PROJECT_CHECKSUM_TAG = "fileChecksum";
	private static final String USER_EMAIL = "userEmail";
	private static final String DEVICE_LANGUAGE = "deviceLanguage";
	private static final MediaType MEDIA_TYPE_ZIPFILE = MediaType.parse("application/zip");
	private static final int SERVER_RESPONSE_TOKEN_OK = 200;
	private static final int SERVER_RESPONSE_REGISTER_OK = 201;
	private static final String FILE_UPLOAD_URL = Constants.BASE_URL_HTTPS + "api/upload/upload.json";
	private static final String CHECK_TOKEN_URL = Constants.BASE_URL_HTTPS + "api/checkToken/check.json";
	private static final String LOGIN_URL = Constants.BASE_URL_HTTPS + "api/login/Login.json";
	private static final String REGISTRATION_URL = Constants.BASE_URL_HTTPS + "api/register/Register.json";
	private static final String CHECK_GOOGLE_TOKEN_URL = Constants.BASE_URL_HTTPS +
			"api/GoogleServerTokenAvailable/GoogleServerTokenAvailable.json";
	private static final String CHECK_FACEBOOK_TOKEN_URL = Constants.BASE_URL_HTTPS +
			"api/FacebookServerTokenAvailable/FacebookServerTokenAvailable.json";
	private static final String CHECK_EMAIL_AVAILABLE_URL = Constants.BASE_URL_HTTPS +
			"api/EMailAvailable/EMailnAvailable.json";
	private static final String CHECK_USERNAME_AVAILABLE_URL = Constants.BASE_URL_HTTPS +
			"api/UsernameAvailable/UsernameAvailable.json";
	private static final String EXCHANGE_GOOGLE_CODE_URL = Constants.BASE_URL_HTTPS +
			"api/exchangeGoogleCode/exchangeGoogleCode.json";
	private static final String EXCHANGE_FACEBOOK_TOKEN_URL = Constants.BASE_URL_HTTPS +
			"api/exchangeFacebookToken/exchangeFacebookToken.json";
	private static final String GOOGLE_LOGIN_URL = Constants.BASE_URL_HTTPS +
			"api/loginWithGoogle/loginWithGoogle.json";
	private static final String FACEBOOK_LOGIN_URL = Constants.BASE_URL_HTTPS +
			"api/loginWithFacebook/loginWithFacebook.json";
	private static final String TEST_CHECK_TOKEN_URL = BASE_URL_TEST_HTTPS + "api/checkToken/check.json";
	private static final String TEST_LOGIN_URL = BASE_URL_TEST_HTTPS + "api/login/Login.json";
	private static final String TEST_REGISTRATION_URL = BASE_URL_TEST_HTTPS + "api/register/Register.json";
	private static final String TEST_CHECK_GOOGLE_TOKEN_URL = BASE_URL_TEST_HTTPS +
			"api/GoogleServerTokenAvailable/GoogleServerTokenAvailable.json";
	private static final String TEST_CHECK_FACEBOOK_TOKEN_URL = BASE_URL_TEST_HTTPS +
			"api/FacebookServerTokenAvailable/FacebookServerTokenAvailable.json";
	private static final String TEST_CHECK_EMAIL_AVAILABLE_URL = BASE_URL_TEST_HTTPS +
			"api/EMailAvailable/EMailnAvailable.json";
	private static final String TEST_CHECK_USERNAME_AVAILABLE_URL = BASE_URL_TEST_HTTPS +
			"api/UsernameAvailable/UsernameAvailable.json";
	private static final String TEST_EXCHANGE_GOOGLE_CODE_URL = BASE_URL_TEST_HTTPS +
			"api/exchangeGoogleCode/exchangeGoogleCode.json";
	private static final String TEST_EXCHANGE_FACEBOOK_TOKEN_URL = BASE_URL_TEST_HTTPS +
			"api/exchangeFacebookToken/exchangeFacebookToken.json";
	private static final String TEST_GOOGLE_LOGIN_URL = BASE_URL_TEST_HTTPS +
			"api/loginWithGoogle/loginWithGoogle.json";
	private static final String TEST_FACEBOOK_LOGIN_URL = BASE_URL_TEST_HTTPS +
			"api/loginWithFacebook/loginWithFacebook.json";
	private static final String TEST_DELETE_TEST_USERS = BASE_URL_TEST_HTTPS +
			"api/deleteOAuthUserAccounts/deleteOAuthUserAccounts.json";
	private static final String JSON_STATUS_CODE = "statusCode";
	private static final String JSON_ANSWER = "answer";
	private static final String JSON_TOKEN = "token";

	private static final ServerCalls INSTANCE = new ServerCalls();

	public static boolean useTestUrl = true;
	private final OkHttpClient okHttpClient;
	private final Gson gson;
	public int oldNotificationId = 0;
	private String resultString;
	private String emailForUiTests;

	private ServerCalls() {
		okHttpClient = new OkHttpClient();
		okHttpClient.setConnectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS));
		gson = new Gson();
	}

	public static ServerCalls getInstance() {
		return INSTANCE;
	}

	public void uploadProject(String projectName, String projectDescription, String zipFileString, String userEmail,
			String language, String token, String username, ResultReceiver receiver, Integer notificationId,
			Context context) throws WebconnectionException {

		Preconditions.checkNotNull(context, "Context cannot be null!");

		userEmail = emailForUiTests == null ? userEmail : emailForUiTests;
		userEmail = userEmail == null ? "" : userEmail;

		try {
			String md5Checksum = Utils.md5Checksum(new File(zipFileString));

			final String serverUrl = useTestUrl ? TEST_FILE_UPLOAD_URL_HTTP : FILE_UPLOAD_URL;

			Log.v(TAG, "Url to upload: " + serverUrl);

			File file = new File(zipFileString);
			RequestBody requestBody = new MultipartBuilder()
					.type(MultipartBuilder.FORM)
					.addFormDataPart(
							FILE_UPLOAD_TAG,
							ProjectUploadService.UPLOAD_FILE_NAME,
							RequestBody.create(MEDIA_TYPE_ZIPFILE, file))
					.addFormDataPart(
							PROJECT_NAME_TAG,
							projectName)
					.addFormDataPart(
							PROJECT_DESCRIPTION_TAG,
							projectDescription)
					.addFormDataPart(
							USER_EMAIL,
							userEmail)
					.addFormDataPart(
							PROJECT_CHECKSUM_TAG,
							md5Checksum)
					.addFormDataPart(
							Constants.TOKEN,
							token)
					.addFormDataPart(
							Constants.USERNAME,
							username)
					.addFormDataPart(
							DEVICE_LANGUAGE,
							language)
					.build();

			Request request = new Request.Builder()
					.url(serverUrl)
					.post(requestBody)
					.build();

			Response response = okHttpClient.newCall(request).execute();

			if (response.isSuccessful()) {
				Log.v(TAG, "Upload successful");
				StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationId, 100);
			} else {
				Log.v(TAG, "Upload not successful");
				throw new WebconnectionException(response.code(), "Upload failed! HTTP Status code was " + response.code());
			}

			UploadResponse uploadResponse = gson.fromJson(response.body().string(), UploadResponse.class);

			String newToken = uploadResponse.token;
			String answer = uploadResponse.answer;
			int status = uploadResponse.statusCode;

			if (status != SERVER_RESPONSE_TOKEN_OK) {
				throw new WebconnectionException(status, "Upload failed! JSON Response was " + status);
			}

			if (token.length() != TOKEN_LENGTH || token.isEmpty() || token.equals(TOKEN_CODE_INVALID)) {
				throw new WebconnectionException(status, answer);
			}
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			sharedPreferences.edit().putString(Constants.TOKEN, newToken).commit();
			sharedPreferences.edit().putString(Constants.USERNAME, username).commit();
		} catch (JsonSyntaxException jsonSyntaxException) {
			Log.e(TAG, Log.getStackTraceString(jsonSyntaxException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JsonSyntaxException");
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, "I/O Exception");
		}
	}

	public void downloadProject(String url, String filePath, final ResultReceiver receiver,
			final int notificationId) throws IOException, WebconnectionException {

		File file = new File(filePath);
		if (!(file.getParentFile().mkdirs() || file.getParentFile().isDirectory())) {
			throw new IOException("Directory not created");
		}

		Request request = new Request.Builder()
				.url(url)
				.build();

		okHttpClient.networkInterceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Response originalResponse = chain.proceed(chain.request());

				if (notificationId >= oldNotificationId) {
					oldNotificationId = notificationId;
					return originalResponse.newBuilder()
							.body(new ProgressResponseBody(
									originalResponse.body(),
									receiver,
									notificationId))
							.build();
				} else {
					return originalResponse;
				}
			}
		});

		try {
			Response response = okHttpClient.newCall(request).execute();
			BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
			bufferedSink.writeAll(response.body().source());
			bufferedSink.close();
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
					"Connection could not be established!");
		}
	}

	public void downloadMedia(String url, String filePath, final ResultReceiver receiver)
			throws IOException, WebconnectionException {
		File file = new File(filePath);
		if (!(file.getParentFile().mkdirs() || file.getParentFile().isDirectory())) {
			throw new IOException("Directory not created");
		}

		Request request = new Request.Builder()
				.url(url)
				.build();

		okHttpClient.networkInterceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Response originalResponse = chain.proceed(chain.request());
				return originalResponse.newBuilder()
						.body(new ProgressResponseBody(
								originalResponse.body(),
								receiver,
								0))
						.build();
			}
		});

		try {
			Response response = okHttpClient.newCall(request).execute();
			BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
			bufferedSink.writeAll(response.body().source());
			bufferedSink.close();
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
					"Connection could not be established!");
		}
	}

    public boolean checkToken(String token, String username) throws WebconnectionException {
        try {
            HashMap<String, String> postValues = new HashMap<>();
            postValues.put(Constants.TOKEN, token);
            postValues.put(SIGNIN_USERNAME_KEY, username);

            String serverUrl = useTestUrl ? TEST_CHECK_TOKEN_URL : CHECK_TOKEN_URL;

            Log.v(TAG, "post values - token:" + token + "user: " + username);
            Log.v(TAG, "url to upload: " + serverUrl);
            resultString = httpFormUpload(serverUrl, postValues);

            Log.v(TAG, "result string: " + resultString);

            JSONObject jsonObject = new JSONObject(resultString);
            int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
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

	public String httpFormUpload(String url, Map<String, String> postValues) throws WebconnectionException {
		FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

		if (postValues != null) {
			for (Map.Entry<String, String> entry : postValues.entrySet()) {
				formEncodingBuilder.add(entry.getKey(), entry.getValue());
			}
		}

		Request request = new Request.Builder()
				.url(url)
				.post(formEncodingBuilder.build())
				.build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException ioException) {
            Log.e(TAG, Log.getStackTraceString(ioException));
            throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
                    "Connection could not be established!");
        }
    }

	public String getRequest(String url) throws WebconnectionException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		try {
			Response response = okHttpClient.newCall(request).execute();
			return response.body().string();
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
					"Connection could not be established!");
		}
	}

	public boolean register(String username, String password, String userEmail, String language,
			String country, String token, Context context) throws WebconnectionException {

		Preconditions.checkNotNull(context, "Context cannot be null!");

		if (emailForUiTests != null) {
			userEmail = emailForUiTests;
		}

        if (userEmail == null) {
            userEmail = Constants.RESTRICTED_USER;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().putBoolean(Constants.RESTRICTED_USER, true).commit();
        }

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(REGISTRATION_USERNAME_KEY, username);
			postValues.put(REGISTRATION_PASSWORD_KEY, password);
			postValues.put(REGISTRATION_EMAIL_KEY, userEmail);
			if (!token.equals(Constants.NO_TOKEN)) {
			    postValues.put(Constants.TOKEN, token);
			}

			if (country != null) {
				postValues.put(REGISTRATION_COUNTRY_KEY, country);
			}
			if (language != null) {
				postValues.put(DEVICE_LANGUAGE, language);
			}
			String serverUrl = useTestUrl ? TEST_REGISTRATION_URL : REGISTRATION_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				String tokenReceived = jsonObject.getString(JSON_TOKEN);
				if (tokenReceived.length() != TOKEN_LENGTH || tokenReceived.equals("")
						|| tokenReceived.equals(TOKEN_CODE_INVALID)) {
					throw new WebconnectionException(statusCode, serverAnswer);
				}
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				sharedPreferences.edit().putString(Constants.TOKEN, tokenReceived).commit();
				sharedPreferences.edit().putString(Constants.USERNAME, username).commit();
				sharedPreferences.edit().putString(Constants.EMAIL, userEmail).commit();
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

	public boolean login(String username, String password, String token, Context context) throws WebconnectionException {

		Preconditions.checkNotNull(context, "Context cannot be null!");

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(REGISTRATION_USERNAME_KEY, username);
			postValues.put(REGISTRATION_PASSWORD_KEY, password);
            if (!token.equals(Constants.NO_TOKEN)) {
                postValues.put(Constants.TOKEN, token);
            }
			Log.d(TAG, "token:" + token);

			String serverUrl = useTestUrl ? TEST_LOGIN_URL : LOGIN_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				String tokenReceived = jsonObject.getString(JSON_TOKEN);
				if (tokenReceived.length() != TOKEN_LENGTH || tokenReceived.equals("")
						|| tokenReceived.equals(TOKEN_CODE_INVALID)) {
					throw new WebconnectionException(statusCode, serverAnswer);
				}
				sharedPreferences.edit().putString(Constants.TOKEN, tokenReceived).commit();
				sharedPreferences.edit().putString(Constants.USERNAME, username).commit();
			}

			String eMail = jsonObject.optString(Constants.EMAIL);
			if (!eMail.isEmpty()) {
				sharedPreferences.edit().putString(Constants.EMAIL, eMail).commit();
			}

			boolean loggedIn;
			if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
				loggedIn = false;
			} else if (statusCode == SERVER_RESPONSE_REGISTER_OK) {
				loggedIn = true;
			} else {
				throw new WebconnectionException(statusCode, serverAnswer);
			}
			return loggedIn;
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	public Boolean checkOAuthToken(String id, String oauthProvider, Context context) throws WebconnectionException {

		Preconditions.checkNotNull(context, "Context cannot be null!");

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);

			String serverUrl = "";
			if (oauthProvider.equals(Constants.FACEBOOK)) {
				serverUrl = useTestUrl ? TEST_CHECK_FACEBOOK_TOKEN_URL : CHECK_FACEBOOK_TOKEN_URL;
			} else if (oauthProvider.equals(Constants.GOOGLE_PLUS)) {
				serverUrl = useTestUrl ? TEST_CHECK_GOOGLE_TOKEN_URL : CHECK_GOOGLE_TOKEN_URL;
			} else {
				throw new WebconnectionException(-1, "OAuth provider not supported!");
			}

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			String serverEmail = jsonObject.optString(SIGNIN_EMAIL_KEY);
			String serverUsername = jsonObject.optString(SIGNIN_USERNAME_KEY);
			boolean tokenAvailable = jsonObject.getBoolean(OAUTH_TOKEN_AVAILABLE);

			if (tokenAvailable) {
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				if (oauthProvider.equals(Constants.FACEBOOK)) {
					sharedPreferences.edit().putString(Constants.FACEBOOK_USERNAME, serverUsername).commit();
					sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, serverEmail).commit();
				} else if (oauthProvider.equals(Constants.GOOGLE_PLUS)) {
					sharedPreferences.edit().putString(Constants.GOOGLE_USERNAME, serverUsername).commit();
					sharedPreferences.edit().putString(Constants.GOOGLE_EMAIL, serverEmail).commit();
				}
			}

			return tokenAvailable;
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	public Boolean checkEMailAvailable(String email) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_EMAIL_KEY, email);

			String serverUrl = useTestUrl ? TEST_CHECK_EMAIL_AVAILABLE_URL : CHECK_EMAIL_AVAILABLE_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			return jsonObject.getBoolean(EMAIL_AVAILABLE);
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	public Boolean checkUserNameAvailable(String username) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_USERNAME_KEY, username);

			String serverUrl = useTestUrl ? TEST_CHECK_USERNAME_AVAILABLE_URL : CHECK_USERNAME_AVAILABLE_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			return jsonObject.getBoolean(USERNAME_AVAILABLE);
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	public boolean facebookLogin(String mail, String username, String id, String locale, Context context) throws
			WebconnectionException {

		Preconditions.checkNotNull(context, "Context cannot be null!");

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_USERNAME_KEY, username);
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);
			postValues.put(SIGNIN_EMAIL_KEY, mail);
			postValues.put(SIGNIN_LOCALE_KEY, locale);
			/*
			if (mail.equals(Constants.NO_TOKEN)) {
                postValues.put(Constants.TOKEN, mail);
            }
            */

			String serverUrl = useTestUrl ? TEST_FACEBOOK_LOGIN_URL : FACEBOOK_LOGIN_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				String tokenReceived = jsonObject.getString(JSON_TOKEN);
				if (tokenReceived.length() != TOKEN_LENGTH || tokenReceived.equals("")
						|| tokenReceived.equals(TOKEN_CODE_INVALID)) {
					throw new WebconnectionException(statusCode, serverAnswer);
				}
				refreshUploadTokenAndUsername(tokenReceived, username, context);
			}

			return true;
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	public boolean facebookExchangeToken(String accessToken, String id, String username,
			String mail, String locale) throws WebconnectionException {

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_FACEBOOK_CLIENT_TOKEN_KEY, accessToken);
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);
			postValues.put(SIGNIN_USERNAME_KEY, username);
			postValues.put(SIGNIN_EMAIL_KEY, mail);
			postValues.put(SIGNIN_LOCALE_KEY, locale);
			postValues.put(SIGNIN_STATE, "");
			postValues.put(Constants.REQUEST_MOBILE, "Android");

			String serverUrl = useTestUrl ? TEST_EXCHANGE_FACEBOOK_TOKEN_URL : EXCHANGE_FACEBOOK_TOKEN_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			if (!(statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK)) {
				throw new WebconnectionException(statusCode, resultString);
			}

			return true;
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	public boolean googleLogin(String mail, String username, String id, String locale, Context context) throws
			WebconnectionException {

		Preconditions.checkNotNull(context, "Context cannot be null!");

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_EMAIL_KEY, mail);
			postValues.put(SIGNIN_USERNAME_KEY, username);
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);
			postValues.put(SIGNIN_LOCALE_KEY, locale);

			String serverUrl = useTestUrl ? TEST_GOOGLE_LOGIN_URL : GOOGLE_LOGIN_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));
			refreshUploadTokenAndUsername(jsonObject.getString(Constants.TOKEN), username, context);

			return true;
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	public boolean googleExchangeCode(String code, String id, String username,
			String mail, String locale) throws WebconnectionException {

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_GOOGLE_CODE_KEY, code);
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);
			postValues.put(SIGNIN_USERNAME_KEY, username);
			postValues.put(SIGNIN_EMAIL_KEY, mail);
			postValues.put(SIGNIN_LOCALE_KEY, locale);
			postValues.put(SIGNIN_STATE, "");
			postValues.put(Constants.REQUEST_MOBILE, "Android");

			String serverUrl = useTestUrl ? TEST_EXCHANGE_GOOGLE_CODE_URL : EXCHANGE_GOOGLE_CODE_URL;

			Log.v(TAG, "URL to use: " + serverUrl);
			resultString = httpFormUpload(serverUrl, postValues);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			if (!(statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK)) {
				throw new WebconnectionException(statusCode, resultString);
			}

			return true;
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	private void checkStatusCode200(int statusCode) throws WebconnectionException {
		if (statusCode != SERVER_RESPONSE_TOKEN_OK) {
			throw new WebconnectionException(statusCode, resultString);
		}
	}

	private void refreshUploadTokenAndUsername(String newToken, String username, Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putString(Constants.TOKEN, newToken).commit();
		sharedPreferences.edit().putString(Constants.USERNAME, username).commit();
	}

	public boolean deleteTestUserAccountsOnServer() throws WebconnectionException {
		try {
			String serverUrl = TEST_DELETE_TEST_USERS;
			Log.v(TAG, "URL to use: " + serverUrl);

			resultString = getRequest(serverUrl);

			Log.v(TAG, "Result string: " + resultString);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			return true;
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "JSON-Error");
		}
	}

	static class UploadResponse {
		//		int projectId;
		int statusCode;
		String answer;
		String token;
		//		String preHeaderMessages;
	}
}
