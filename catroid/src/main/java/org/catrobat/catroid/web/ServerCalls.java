/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.common.ScratchVisibilityState;
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

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public final class ServerCalls implements ScratchDataFetcher {
	public static final String BASE_URL_TEST_HTTPS = "https://catroid-test.catrob.at/pocketcode/";
	public static final String TAG = ServerCalls.class.getSimpleName();
	public static boolean useTestUrl = false;
	private final OkHttpClient okHttpClient;
	private String resultString;

	public ServerCalls(OkHttpClient httpClient) {
		okHttpClient = httpClient;
	}

	public ScratchProgramData fetchScratchProgramDetails(final long programID) throws WebConnectionException,
			WebScratchProgramException {

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
				throw new WebConnectionException(WebConnectionException.ERROR_EMPTY_PROJECT_DATA, "Field projectData "
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
			throw new WebConnectionException(WebConnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public ScratchSearchResult fetchDefaultScratchPrograms() throws WebConnectionException, InterruptedIOException {
		try {
			final String url = Constants.SCRATCH_CONVERTER_API_DEFAULT_PROJECTS_URL;
			resultString = getRequestInterruptable(url);

			final JSONObject jsonObject = new JSONObject(resultString);
			final JSONArray results = jsonObject.getJSONArray("results");
			final List<ScratchProgramData> programDataList = extractScratchProgramDataListFromJson(results);

			return new ScratchSearchResult(programDataList, null, 0);
		} catch (JSONException | ParseException e) {
			throw new WebConnectionException(WebConnectionException.ERROR_JSON, Log.getStackTraceString(e));
		}
	}

	public ScratchSearchResult scratchSearch(final String query, final int numberOfItems, final int pageNumber)
			throws WebConnectionException {

		if (query == null) {
			throw new WebConnectionException(WebConnectionException.ERROR_JSON, "Query is null.");
		}
		if (numberOfItems < 1) {
			throw new WebConnectionException(WebConnectionException.ERROR_JSON, "numberOfItems has to be positive and"
					+ " non-zero.");
		}
		if (pageNumber < 0) {
			throw new WebConnectionException(WebConnectionException.ERROR_JSON, "pageNumber has to be positive.");
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
			throw new WebConnectionException(WebConnectionException.ERROR_JSON, Log.getStackTraceString(e));
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

	public void downloadMedia(final String url, final String filePath, final ResultReceiver receiver)
			throws IOException, WebConnectionException {

		File file = new File(filePath);
		if (!(file.getParentFile().mkdirs() || file.getParentFile().isDirectory())) {
			throw new IOException("Directory not created");
		}

		Request request = new Request.Builder()
				.url(url)
				.build();

		OkHttpClient.Builder httpClientBuilder = okHttpClient.newBuilder();
		httpClientBuilder.networkInterceptors().add(chain -> {
			Response originalResponse = chain.proceed(chain.request());
			ProgressResponseBody body = new ProgressResponseBody(originalResponse.body(),
					progress -> {
				Bundle bundle = new Bundle();
				bundle.putLong(ProgressResponseBody.TAG_PROGRESS, progress);
				receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, bundle);
			});
			return originalResponse.newBuilder()
					.body(body)
					.build();
		});
		OkHttpClient httpClient = httpClientBuilder.build();
		Response response = httpClient.newCall(request).execute();

		try (BufferedSink bufferedSink = Okio.buffer(Okio.sink(file))) {
			if (response.body() != null) {
				bufferedSink.writeAll(response.body().source());
			} else {
				throw new WebConnectionException(WebConnectionException.ERROR_NETWORK, "FAIL");
			}
		} catch (IOException e) {
			throw new WebConnectionException(WebConnectionException.ERROR_NETWORK, Log.getStackTraceString(e));
		}
	}
	private String getRequestInterruptable(String url) throws WebConnectionException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		try {
			OkHttpClient httpClient = okHttpClient;

			if (url.startsWith("http://")) {
				httpClient = new OkHttpClient.Builder()
						.connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT))
						.build();
			}

			Response response = httpClient.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			throw new WebConnectionException(WebConnectionException.ERROR_NETWORK, Log.getStackTraceString(e));
		}
	}
}
