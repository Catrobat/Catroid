/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.net.Uri;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.images.WebImage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.common.ScratchVisibilityState;
import org.catrobat.catroid.web.requests.HttpRequestsKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;

import static org.catrobat.catroid.common.Constants.CATROBAT_TOKEN_LOGIN_AMP_TOKEN;
import static org.catrobat.catroid.common.Constants.CATROBAT_TOKEN_LOGIN_URL;
import static org.catrobat.catroid.common.Constants.NO_TOKEN;

public final class ServerCalls implements ScratchDataFetcher {

	public static final String BASE_URL_TEST_HTTPS = "https://catroid-test.catrob.at/pocketcode/";
	public static final String FILE_TAG_URL_HTTP = FlavoredConstants.BASE_URL_HTTPS + "api/tags/getTags.json";
	public static final int TOKEN_LENGTH = 32;
	public static final String TOKEN_CODE_INVALID = "-1";
	public static final String TAG = ServerCalls.class.getSimpleName();
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
	private static final String SIGNIN_ID_TOKEN = "id_token";
	private static final String SIGNIN_TOKEN = "token";
	private static final String OAUTH_TOKEN_AVAILABLE = "token_available";
	private static final String EMAIL_AVAILABLE = "email_available";
	private static final String USERNAME_AVAILABLE = "username_available";
	private static final String DEVICE_LANGUAGE = "deviceLanguage";
	private static final int SERVER_RESPONSE_TOKEN_OK = 200;
	private static final int SERVER_RESPONSE_REGISTER_OK = 201;
	private static final String CHECK_TOKEN_URL = FlavoredConstants.BASE_URL_HTTPS + "api/checkToken/check.json";
	private static final String LOGIN_URL = FlavoredConstants.BASE_URL_HTTPS + "api/login/Login.json";
	private static final String REGISTRATION_URL = FlavoredConstants.BASE_URL_HTTPS + "api/register/Register.json";
	private static final String CHECK_GOOGLE_TOKEN_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/GoogleServerTokenAvailable/GoogleServerTokenAvailable.json";
	private static final String CHECK_FACEBOOK_TOKEN_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/FacebookServerTokenAvailable/FacebookServerTokenAvailable.json";
	private static final String GET_FACEBOOK_USER_INFO_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/getFacebookUserInfo/getFacebookUserInfo.json";
	private static final String CHECK_EMAIL_AVAILABLE_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/EMailAvailable/EMailAvailable.json";
	private static final String CHECK_USERNAME_AVAILABLE_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/UsernameAvailable/UsernameAvailable.json";
	private static final String EXCHANGE_GOOGLE_CODE_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/exchangeGoogleCode/exchangeGoogleCode.json";
	private static final String EXCHANGE_FACEBOOK_TOKEN_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/exchangeFacebookToken/exchangeFacebookToken.json";
	private static final String GOOGLE_LOGIN_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/loginWithGoogle/loginWithGoogle.json";
	private static final String FACEBOOK_LOGIN_URL = FlavoredConstants.BASE_URL_HTTPS
			+ "api/loginWithFacebook/loginWithFacebook.json";
	private static final String FACEBOOK_CHECK_SERVER_TOKEN_VALIDITY = FlavoredConstants.BASE_URL_HTTPS
			+ "api/checkFacebookServerTokenValidity/checkFacebookServerTokenValidity.json";
	private static final String TEST_CHECK_TOKEN_URL = BASE_URL_TEST_HTTPS + "api/checkToken/check.json";
	private static final String TEST_LOGIN_URL = BASE_URL_TEST_HTTPS + "api/login/Login.json";
	private static final String TEST_REGISTRATION_URL = BASE_URL_TEST_HTTPS + "api/register/Register.json";
	private static final String TEST_CHECK_GOOGLE_TOKEN_URL = BASE_URL_TEST_HTTPS
			+ "api/GoogleServerTokenAvailable/GoogleServerTokenAvailable.json";
	private static final String TEST_CHECK_FACEBOOK_TOKEN_URL = BASE_URL_TEST_HTTPS
			+ "api/FacebookServerTokenAvailable/FacebookServerTokenAvailable.json";
	private static final String TEST_GET_FACEBOOK_USER_INFO_URL = BASE_URL_TEST_HTTPS
			+ "api/getFacebookUserInfo/getFacebookUserInfo.json";
	private static final String TEST_CHECK_EMAIL_AVAILABLE_URL = BASE_URL_TEST_HTTPS
			+ "api/EMailAvailable/EMailAvailable.json";
	private static final String TEST_CHECK_USERNAME_AVAILABLE_URL = BASE_URL_TEST_HTTPS
			+ "api/UsernameAvailable/UsernameAvailable.json";
	private static final String TEST_EXCHANGE_GOOGLE_CODE_URL = BASE_URL_TEST_HTTPS
			+ "api/exchangeGoogleCode/exchangeGoogleCode.json";
	private static final String TEST_EXCHANGE_FACEBOOK_TOKEN_URL = BASE_URL_TEST_HTTPS
			+ "api/exchangeFacebookToken/exchangeFacebookToken.json";
	private static final String TEST_GOOGLE_LOGIN_URL = BASE_URL_TEST_HTTPS
			+ "api/loginWithGoogle/loginWithGoogle.json";
	private static final String TEST_FACEBOOK_LOGIN_URL = BASE_URL_TEST_HTTPS
			+ "api/loginWithFacebook/loginWithFacebook.json";
	private static final String TEST_DELETE_TEST_USERS = BASE_URL_TEST_HTTPS
			+ "api/deleteOAuthUserAccounts/deleteOAuthUserAccounts.json";
	private static final String TEST_FACEBOOK_CHECK_SERVER_TOKEN_VALIDITY = BASE_URL_TEST_HTTPS
			+ "api/checkFacebookServerTokenValidity/checkFacebookServerTokenValidity.json";
	private static final String JSON_STATUS_CODE = "statusCode";
	private static final String JSON_ANSWER = "answer";
	private static final String JSON_TOKEN = "token";
	private static final String FACEBOOK_SERVER_TOKEN_INVALID = "token_invalid";

	private static final ServerCalls INSTANCE = new ServerCalls();

	public static boolean useTestUrl = false;
	private final OkHttpClient okHttpClient;
	private final Gson gson;
	public int oldNotificationId = 0;
	private String resultString;
	private int projectId;

	private ServerCalls() {
		okHttpClient = new OkHttpClient();
		okHttpClient.setConnectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS));
		gson = new Gson();
	}

	public static ServerCalls getInstance() {
		return INSTANCE;
	}

	public ScratchProgramData fetchScratchProgramDetails(final long programID) throws WebconnectionException,
			WebScratchProgramException, InterruptedIOException {

		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

		try {
			final String url = Constants.SCRATCH_CONVERTER_API_DEFAULT_PROJECTS_URL + programID;

			resultString = getRequestInterruptable(url);

			final JSONObject jsonObject = new JSONObject(resultString);
			if (jsonObject.length() == 0) {
				return null;
			}

			if (!jsonObject.getBoolean("accessible")) {
				throw new WebScratchProgramException(WebScratchProgramException.ERROR_PROGRAM_NOT_ACCESSIBLE,
						"Program not accessible!");
			}

			final JSONObject jsonData = jsonObject.getJSONObject("projectData");
			if (jsonData.length() == 0) {
				throw new WebconnectionException(WebconnectionException.ERROR_EMPTY_PROJECT_DATA, "Field projectData "
						+ "is empty.");
			}
			final String title = jsonData.getString("title");
			final String owner = jsonData.getString("owner");
			final String imageURL = jsonData.isNull("image_url") ? null : jsonData.getString("image_url");
			final String instructions = jsonData.isNull("instructions") ? null : jsonData.getString("instructions");
			final String notesAndCredits = jsonData.isNull("notes_and_credits") ? null : jsonData.getString("notes_and_credits");
			final String sharedDateString = jsonData.getString("shared_date");
			final String modifiedDateString = jsonData.getString("modified_date");
			final int views = jsonData.getInt("views");
			final int favorites = jsonData.getInt("favorites");
			final int loves = jsonData.getInt("loves");
			final ScratchVisibilityState visibilityState = ScratchVisibilityState.valueOf(jsonObject.getInt("visibility"));

			Date sharedDate;
			try {
				sharedDate = formatter.parse(sharedDateString);
			} catch (ParseException ex) {
				sharedDate = null;
			}

			Date modifiedDate;
			try {
				modifiedDate = formatter.parse(modifiedDateString);
			} catch (ParseException ex) {
				modifiedDate = null;
			}

			WebImage image = null;
			if (imageURL != null) {
				image = new WebImage(Uri.parse(imageURL), Constants.SCRATCH_IMAGE_DEFAULT_WIDTH,
						Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT);
			}

			final ScratchProgramData programData = new ScratchProgramData(programID, title, owner, image);
			programData.setInstructions(instructions);
			programData.setNotesAndCredits(notesAndCredits);
			programData.setModifiedDate(modifiedDate);
			programData.setSharedDate(sharedDate);
			programData.setViews(views);
			programData.setLoves(loves);
			programData.setFavorites(favorites);
			programData.setVisibilityState(visibilityState);

			JSONArray remixes = jsonData.getJSONArray("remixes");
			for (int i = 0; i < remixes.length(); ++i) {
				JSONObject remixJson = remixes.getJSONObject(i);
				long remixId = remixJson.getLong("id");
				String remixTitle = remixJson.getString("title");
				String remixOwner = remixJson.getString("owner");
				String remixImageURL = remixJson.isNull("image") ? null : remixJson.getString("image");

				WebImage remixImage = null;
				if (remixImageURL != null) {
					remixImage = new WebImage(Uri.parse(remixImageURL), Constants.SCRATCH_IMAGE_DEFAULT_WIDTH,
							Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT);
				}

				programData.addRemixProgram(new ScratchProgramData(remixId, remixTitle, remixOwner, remixImage));
			}
			return programData;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public ScratchSearchResult fetchDefaultScratchPrograms() throws WebconnectionException, InterruptedIOException {
		try {
			final String url = Constants.SCRATCH_CONVERTER_API_DEFAULT_PROJECTS_URL;
			resultString = getRequestInterruptable(url);

			final JSONObject jsonObject = new JSONObject(resultString);
			final JSONArray results = jsonObject.getJSONArray("results");
			final List<ScratchProgramData> programDataList = extractScratchProgramDataListFromJson(results);

			return new ScratchSearchResult(programDataList, null, 0);
		} catch (JSONException | ParseException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public ScratchSearchResult scratchSearch(final String query, final int numberOfItems, final int pageNumber)
			throws WebconnectionException, InterruptedIOException {

		if (query == null) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "Query is null.");
		}
		if (numberOfItems < 1) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "numberOfItems has to be positive and"
					+ " non-zero.");
		}
		if (pageNumber < 0) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "pageNumber has to be positive.");
		}

		try {
			final HashMap<String, String> httpGetParams = new HashMap<String, String>() {
				{
					put("limit", Integer.toString(numberOfItems));
					put("offset", Integer.toString(pageNumber * numberOfItems));
					put("language", Locale.getDefault().getLanguage());
					put("q", URLEncoder.encode(query, "UTF-8"));
				}
			};

			StringBuilder urlStringBuilder = new StringBuilder(Constants.SCRATCH_SEARCH_URL);
			urlStringBuilder.append('?');
			for (Map.Entry<String, String> entry : httpGetParams.entrySet()) {
				urlStringBuilder.append(entry.getKey());
				urlStringBuilder.append('=');
				urlStringBuilder.append(entry.getValue());
				urlStringBuilder.append('&');
			}
			urlStringBuilder.setLength(urlStringBuilder.length() - 1); // removes trailing "&" or "?" character

			final String url = urlStringBuilder.toString();
			resultString = getRequestInterruptable(url);

			final JSONArray results = new JSONArray(resultString);
			final List<ScratchProgramData> programDataList = extractScratchProgramDataListFromJson(results);

			return new ScratchSearchResult(programDataList, query, pageNumber);
		} catch (JSONException | ParseException | UnsupportedEncodingException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	private List<ScratchProgramData> extractScratchProgramDataListFromJson(final JSONArray jsonArray)
			throws JSONException, ParseException {

		final DateFormat iso8601LocalDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_ISO_8601, Locale.US);
		ArrayList<ScratchProgramData> programDataList = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject programJsonData = jsonArray.getJSONObject(i);
			final long id = programJsonData.getLong("id");
			final String title = programJsonData.getString("title");
			final String notesAndCredits = programJsonData.getString("description");
			final String instructions = programJsonData.getString("instructions");
			final String imageURL = programJsonData.isNull("image") ? null : programJsonData.getString("image");

			final JSONObject authorJsonData = programJsonData.getJSONObject("author");
			final String ownerUserName = authorJsonData.getString("username");

			final JSONObject historyJsonData = programJsonData.getJSONObject("history");
			final String createdDateString = historyJsonData.getString("created");
			final String modifiedDateString = historyJsonData.getString("modified");
			final String sharedDateString = historyJsonData.getString("shared");
			final Date createdDate = iso8601LocalDateFormat.parse(createdDateString);
			final Date modifiedDate = iso8601LocalDateFormat.parse(modifiedDateString);
			final Date sharedDate = iso8601LocalDateFormat.parse(sharedDateString);

			final JSONObject statisticsJsonData = programJsonData.getJSONObject("stats");
			final int views = statisticsJsonData.getInt("views");
			final int loves = statisticsJsonData.getInt("loves");
			final int favorites = statisticsJsonData.getInt("favorites");

			WebImage image = null;
			if (imageURL != null) {
				image = new WebImage(Uri.parse(imageURL), Constants.SCRATCH_IMAGE_DEFAULT_WIDTH,
						Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT);
			}

			final ScratchProgramData programData = new ScratchProgramData(id, title, ownerUserName, image);
			programData.setInstructions(instructions);
			programData.setNotesAndCredits(notesAndCredits);
			programData.setCreatedDate(createdDate);
			programData.setModifiedDate(modifiedDate);
			programData.setSharedDate(sharedDate);
			programData.setViews(views);
			programData.setLoves(loves);
			programData.setFavorites(favorites);
			programDataList.add(programData);
		}
		return programDataList;
	}

	public void uploadProject(String projectName, String projectDescription, String zipFilePath, String userEmail,
			String language, String token, String username, UploadSuccessCallback successCallback, UploadErrorCallback errorCallback) {
		userEmail = userEmail == null ? "" : userEmail;

		excecuteUploadCall(
				HttpRequestsKt.createUploadRequest(projectName, projectDescription, zipFilePath, userEmail, language, token, username),
				(uploadResponse) -> {
					String newToken = uploadResponse.token;
					projectId = uploadResponse.projectId;

					if (uploadResponse.statusCode != SERVER_RESPONSE_TOKEN_OK) {
						errorCallback.onError(uploadResponse.statusCode, "Upload failed! JSON Response was " + uploadResponse.statusCode);
					} else if (newToken.equals(TOKEN_CODE_INVALID) || newToken.length() != TOKEN_LENGTH) {
						errorCallback.onError(uploadResponse.statusCode, uploadResponse.answer);
					} else {
						successCallback.onSuccess(projectId, username, newToken);
					}
				},
				errorCallback
		);
	}

	private void excecuteUploadCall(Request request, UploadCallSuccessCallback successCallback, UploadErrorCallback errorCallback) {
		Response response;
		UploadResponse uploadResponse;
		try {
			response = okHttpClient.newCall(request).execute();
			if (!response.isSuccessful()) {
				Log.v(TAG, "Upload not successful");
				errorCallback.onError(response.code(), "Upload failed! HTTP Status code was " + response.code());
			}

			uploadResponse = gson.fromJson(response.body().string(), UploadResponse.class);
			successCallback.onSuccess(uploadResponse);
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			errorCallback.onError(WebconnectionException.ERROR_NETWORK, "I/O Exception");
		} catch (JsonSyntaxException jsonSyntaxException) {
			Log.e(TAG, Log.getStackTraceString(jsonSyntaxException));
			errorCallback.onError(WebconnectionException.ERROR_JSON, "JsonSyntaxException");
		}
	}

	public void downloadProject(final String url, final String filePath, final String programName,
			final ResultReceiver receiver, final int notificationId) throws IOException, WebconnectionException {

		File file = new File(filePath);
		if (!(file.getParentFile().mkdirs() || file.getParentFile().isDirectory())) {
			throw new IOException("Directory not created");
		}

		Request request = new Request.Builder()
				.url(url)
				.build();

		OkHttpClient httpClient = okHttpClient;
		if (url.startsWith("http://")) {
			httpClient = new OkHttpClient();
			httpClient.setConnectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT));
		}

		httpClient.networkInterceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Response originalResponse = chain.proceed(chain.request());

				if (notificationId >= oldNotificationId) {
					oldNotificationId = notificationId;
					return originalResponse.newBuilder()
							.body(new ProgressResponseBody(
									originalResponse.body(),
									receiver,
									notificationId,
									programName,
									url))
							.build();
				} else {
					return originalResponse;
				}
			}
		});

		try {
			Response response = httpClient.newCall(request).execute();
			BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
			bufferedSink.writeAll(response.body().source());
			bufferedSink.close();
		} catch (IOException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, Log.getStackTraceString(e));
		}
	}

	public void downloadMedia(final String url, final String filePath, final ResultReceiver receiver)
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
								0,
								null,
								url))
						.build();
			}
		});

		try {
			Response response = okHttpClient.newCall(request).execute();
			BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
			bufferedSink.writeAll(response.body().source());
			bufferedSink.close();
		} catch (IOException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, Log.getStackTraceString(e));
		}
	}

	public boolean checkToken(String token, String username) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(Constants.TOKEN, token);
			postValues.put(SIGNIN_USERNAME_KEY, username);

			String serverUrl = useTestUrl ? TEST_CHECK_TOKEN_URL : CHECK_TOKEN_URL;

			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
				return true;
			} else {
				throw new WebconnectionException(statusCode, "server response token ok, but error: " + serverAnswer);
			}
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
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
		} catch (IOException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, Log.getStackTraceString(e));
		}
	}

	public String getRequest(String url) throws WebconnectionException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		try {
			Response response = okHttpClient.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, Log.getStackTraceString(e));
		}
	}

	public String getRequestInterruptable(String url) throws InterruptedIOException, WebconnectionException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		try {
			OkHttpClient httpClient = okHttpClient;

			if (url.startsWith("http://")) {
				httpClient = new OkHttpClient();
				httpClient.setConnectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT));
			}

			Response response = httpClient.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, Log.getStackTraceString(e));
		}
	}

	public String getTags(String language) {
		try {
			String serverUrl = FILE_TAG_URL_HTTP;
			if (language != null) {
				serverUrl = serverUrl.concat("?language=" + language);
			}
			return getRequest(serverUrl);
		} catch (WebconnectionException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return "";
		}
	}

	public boolean register(String username, String password, String userEmail, String language,
			String country, String token, Context context) throws WebconnectionException {

		if (context == null) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "Context is null.");
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
			if (!token.equals(NO_TOKEN)) {
				postValues.put(Constants.TOKEN, token);
			}

			if (country != null) {
				postValues.put(REGISTRATION_COUNTRY_KEY, country);
			}
			if (language != null) {
				postValues.put(DEVICE_LANGUAGE, language);
			}
			String serverUrl = useTestUrl ? TEST_REGISTRATION_URL : REGISTRATION_URL;

			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				String tokenReceived = jsonObject.getString(JSON_TOKEN);
				if (isInvalidToken(tokenReceived)) {
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
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, resultString);
		}
	}

	public boolean login(String username, String password, String token, Context context) throws WebconnectionException {
		if (context == null) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "Context is null.");
		}

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(REGISTRATION_USERNAME_KEY, username);
			postValues.put(REGISTRATION_PASSWORD_KEY, password);
			if (!token.equals(NO_TOKEN)) {
				postValues.put(Constants.TOKEN, token);
			}

			String serverUrl = useTestUrl ? TEST_LOGIN_URL : LOGIN_URL;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				String tokenReceived = jsonObject.getString(JSON_TOKEN);
				if (isInvalidToken(tokenReceived)) {
					throw new WebconnectionException(statusCode, serverAnswer);
				}
				sharedPreferences.edit().putString(Constants.TOKEN, tokenReceived).commit();
				sharedPreferences.edit().putString(Constants.USERNAME, username).commit();
			}

			String eMail = jsonObject.optString(Constants.EMAIL);
			if (!eMail.isEmpty()) {
				sharedPreferences.edit().putString(Constants.EMAIL, eMail).commit();
			}

			if (statusCode != SERVER_RESPONSE_TOKEN_OK) {
				throw new WebconnectionException(statusCode, serverAnswer);
			}
			return true;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	private boolean isInvalidToken(String token) {
		return token.length() != TOKEN_LENGTH
				|| token.equals("")
				|| token.equals(TOKEN_CODE_INVALID);
	}

	public Boolean checkOAuthToken(String id, String oauthProvider, Context context) throws WebconnectionException {
		if (context == null) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "Context is null.");
		}

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);

			String serverUrl;
			switch (oauthProvider) {
				case Constants.FACEBOOK:
					serverUrl = useTestUrl ? TEST_CHECK_FACEBOOK_TOKEN_URL : CHECK_FACEBOOK_TOKEN_URL;
					break;
				case Constants.GOOGLE_PLUS:
					serverUrl = useTestUrl ? TEST_CHECK_GOOGLE_TOKEN_URL : CHECK_GOOGLE_TOKEN_URL;
					break;
				default:
					throw new WebconnectionException(-1, "OAuth provider not supported!");
			}

			resultString = httpFormUpload(serverUrl, postValues);

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
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public Boolean checkEMailAvailable(String email) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_EMAIL_KEY, email);

			String serverUrl = useTestUrl ? TEST_CHECK_EMAIL_AVAILABLE_URL : CHECK_EMAIL_AVAILABLE_URL;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			return jsonObject.getBoolean(EMAIL_AVAILABLE);
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public Boolean checkUserNameAvailable(String username) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_USERNAME_KEY, username);

			String serverUrl = useTestUrl ? TEST_CHECK_USERNAME_AVAILABLE_URL : CHECK_USERNAME_AVAILABLE_URL;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			return jsonObject.getBoolean(USERNAME_AVAILABLE);
		} catch (JSONException jsonException) {
			Log.e(TAG, Log.getStackTraceString(jsonException));
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, resultString);
		}
	}

	public JSONObject getFacebookUserInfo(String facebookId, String token) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_OAUTH_ID_KEY, facebookId);
			if (token != null) {
				postValues.put(SIGNIN_TOKEN, token);
			}

			String serverUrl = useTestUrl ? TEST_GET_FACEBOOK_USER_INFO_URL : GET_FACEBOOK_USER_INFO_URL;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			if (jsonObject.has(Constants.JSON_ERROR_CODE)) {
				return jsonObject;
			}
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			return jsonObject;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public boolean facebookLogin(String mail, String username, String id, String locale, Context context) throws
			WebconnectionException {

		if (context == null) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "Context is null.");
		}

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_USERNAME_KEY, username);
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);
			postValues.put(SIGNIN_EMAIL_KEY, mail);
			postValues.put(SIGNIN_LOCALE_KEY, locale);

			String serverUrl = useTestUrl ? TEST_FACEBOOK_LOGIN_URL : FACEBOOK_LOGIN_URL;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			String serverAnswer = jsonObject.optString(JSON_ANSWER);

			if (statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK) {
				String tokenReceived = jsonObject.getString(JSON_TOKEN);
				if (tokenReceived.length() != TOKEN_LENGTH
						|| tokenReceived.equals("")
						|| tokenReceived.equals(TOKEN_CODE_INVALID)) {
					throw new WebconnectionException(statusCode, serverAnswer);
				}
				refreshUploadTokenAndUsername(tokenReceived, username, context);
			}
			return true;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
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
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			if (!(statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK)) {
				throw new WebconnectionException(statusCode, resultString);
			}

			return true;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public boolean googleLogin(String mail, String username, String id, String locale, Context context) throws
			WebconnectionException {

		if (context == null) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, "Context is null.");
		}

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_EMAIL_KEY, mail);
			postValues.put(SIGNIN_USERNAME_KEY, username);
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);
			postValues.put(SIGNIN_LOCALE_KEY, locale);

			String serverUrl = useTestUrl ? TEST_GOOGLE_LOGIN_URL : GOOGLE_LOGIN_URL;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));
			refreshUploadTokenAndUsername(jsonObject.getString(Constants.TOKEN), username, context);

			return true;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public boolean googleExchangeCode(String code, String id, String username,
			String mail, String locale, String idToken) throws WebconnectionException {

		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_GOOGLE_CODE_KEY, code);
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);
			postValues.put(SIGNIN_USERNAME_KEY, username);
			postValues.put(SIGNIN_EMAIL_KEY, mail);
			postValues.put(SIGNIN_LOCALE_KEY, locale);
			postValues.put(SIGNIN_ID_TOKEN, idToken);
			postValues.put(Constants.REQUEST_MOBILE, "Android");

			String serverUrl = useTestUrl ? TEST_EXCHANGE_GOOGLE_CODE_URL : EXCHANGE_GOOGLE_CODE_URL;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			int statusCode = jsonObject.getInt(JSON_STATUS_CODE);
			if (!(statusCode == SERVER_RESPONSE_TOKEN_OK || statusCode == SERVER_RESPONSE_REGISTER_OK)) {
				throw new WebconnectionException(statusCode, resultString);
			}

			return true;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
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
			resultString = getRequest(TEST_DELETE_TEST_USERS);
			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));
			return true;
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public Boolean checkFacebookServerTokenValidity(String id) throws WebconnectionException {
		try {
			HashMap<String, String> postValues = new HashMap<>();
			postValues.put(SIGNIN_OAUTH_ID_KEY, id);

			String serverUrl = useTestUrl ? TEST_FACEBOOK_CHECK_SERVER_TOKEN_VALIDITY : FACEBOOK_CHECK_SERVER_TOKEN_VALIDITY;
			resultString = httpFormUpload(serverUrl, postValues);

			JSONObject jsonObject = new JSONObject(resultString);
			checkStatusCode200(jsonObject.getInt(JSON_STATUS_CODE));

			return jsonObject.getBoolean(FACEBOOK_SERVER_TOKEN_INVALID);
		} catch (JSONException e) {
			throw new WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public void logout(String userName) {
		try {
			String serverUrl = CATROBAT_TOKEN_LOGIN_URL
					+ userName
					+ CATROBAT_TOKEN_LOGIN_AMP_TOKEN
					+ NO_TOKEN;

			getRequest(serverUrl);
		} catch (WebconnectionException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	static class UploadResponse {
		int projectId;
		int statusCode;
		String answer;
		String token;
	}

	public interface UploadSuccessCallback {
		void onSuccess(int projectId, String unsername, String token);
	}

	public interface UploadErrorCallback {
		void onError(int statusCode, String errorMessage);
	}

	private interface UploadCallSuccessCallback {
		void onSuccess(UploadResponse response);
	}
}
