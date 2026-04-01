/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.transfers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.web.CatrobatServerCalls;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.VisibleForTesting;

public class GetSurveyTask extends AsyncTask<String, Void, String> {

	private static final String TAG = GetSurveyTask.class.getSimpleName();
	private static final String SURVEY_URL_JSON_KEY = "url";
	private SurveyResponseListener onSurveyResponseListener;
	private final WeakReference<Context> contextWeakReference;

	public GetSurveyTask(Context context) {
		this.contextWeakReference = new WeakReference<>(context);
	}

	public void setOnSurveyResponseListener(SurveyResponseListener listener) {
		onSurveyResponseListener = listener;
	}

	@Override
	protected String doInBackground(String... arg0) {
		String jsonString = new CatrobatServerCalls().getSurvey(Locale.getDefault().getLanguage());
		String surveyUrl = null;

		if (!jsonString.isEmpty()) {
			try {
				surveyUrl = parseSurvey(jsonString);
				surveyUrl = isUrlStatusCodeOk(surveyUrl) ? surveyUrl : null;
			} catch (Exception e) {
				Log.e(TAG, "Failed to get survey url", e);
			}
		}

		return surveyUrl;
	}

	@Override
	protected void onPostExecute(String response) {
		Context context = contextWeakReference.get();
		if (context == null) {
			return;
		}

		if (response != null) {
			onSurveyResponseListener.onSurveyReceived(context, response);
		}
	}

	@VisibleForTesting
	public String parseSurvey(String response) throws JSONException {
		JSONObject json = new JSONObject(response);
		String surveyUrl = json.getString(SURVEY_URL_JSON_KEY);

		return surveyUrl;
	}

	private boolean isUrlStatusCodeOk(String surveyUrl) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) (new URL(surveyUrl)).openConnection();
		connection.connect();
		int status = connection.getResponseCode();
		connection.disconnect();

		return status == HttpURLConnection.HTTP_OK;
	}

	public interface SurveyResponseListener {
		void onSurveyReceived(Context context, String surveyUrl);
	}
}
