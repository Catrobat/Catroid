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

package org.catrobat.catroid.transfers;

import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.web.CatrobatServerCalls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GetTagsTask extends AsyncTask<String, Void, String> {

	private static final String TAG = GetTagsTask.class.getSimpleName();
	private static final String TAGS_JSON_KEY = "constantTags";
	private TagResponseListener onTagsResponseListener;

	public void setOnTagsResponseListener(TagResponseListener listener) {
		onTagsResponseListener = listener;
	}

	@Override
	protected String doInBackground(String... arg0) {
		return new CatrobatServerCalls().getTags(Locale.getDefault().getLanguage());
	}

	@Override
	protected void onPostExecute(String response) {
		try {
			onTagsResponseListener.onTagsReceived(parseTags(response));
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse tags json", e);
		}
	}

	private List<String> parseTags(String response) throws JSONException {
		List<String> tags = new ArrayList<>();
		JSONObject json = new JSONObject(response);
		JSONArray tagsJson = json.getJSONArray(TAGS_JSON_KEY);
		for (int i = 0; i < tagsJson.length(); i++) {
			tags.add(tagsJson.getString(i));
		}
		return Collections.unmodifiableList(tags);
	}

	public interface TagResponseListener {

		void onTagsReceived(List<String> tags);
	}
}
