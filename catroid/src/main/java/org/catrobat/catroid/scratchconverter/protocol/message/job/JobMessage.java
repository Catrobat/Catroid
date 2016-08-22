/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.scratchconverter.protocol.message.job;

import android.support.annotation.Nullable;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys.JsonDataKeys;
import org.catrobat.catroid.scratchconverter.protocol.message.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public abstract class JobMessage extends Message {

	private static final String TAG = JobMessage.class.getSimpleName();

	private final long jobID;

	public enum Type {
		JOB_FAILED(0),
		JOB_RUNNING(1),
		JOB_ALREADY_RUNNING(2),
		JOB_READY(3),
		JOB_OUTPUT(4),
		JOB_PROGRESS(5),
		JOB_FINISHED(6);

		private int typeID;

		private static Map<Integer, Type> map = new HashMap<>();
		static {
			for (Type legEnum : Type.values()) {
				map.put(legEnum.typeID, legEnum);
			}
		}
		Type(final int typeID) {
			this.typeID = typeID;
		}

		public static Type valueOf(int typeID) {
			return map.get(typeID);
		}

		public int getTypeID() {
			return typeID;
		}
	}

	public JobMessage(final long jobID) {
		this.jobID = jobID;
	}

	public long getJobID() {
		return jobID;
	}

	@Nullable
	public static <T extends JobMessage> T fromJson(JSONObject jsonMessage) throws JSONException {
		final JSONObject jsonData = jsonMessage.getJSONObject(JsonKeys.DATA.toString());
		final long jobID = jsonData.getLong(JsonDataKeys.JOB_ID.toString());
		switch (Type.valueOf(jsonMessage.getInt(JsonKeys.TYPE.toString()))) {
			case JOB_FAILED:
				return (T) new JobFailedMessage(jobID, jsonData.getString(JsonDataKeys.MSG.toString()));

			case JOB_RUNNING:
				final String jobTitle = jsonData.getString(JsonDataKeys.JOB_TITLE.toString());
				final String jobImageURLKey = JsonDataKeys.JOB_IMAGE_URL.toString();
				final String jobImageURL = jsonData.isNull(jobImageURLKey) ? null : jsonData.getString(jobImageURLKey);
				return (T) new JobRunningMessage(jobID, jobTitle, jobImageURL);

			case JOB_ALREADY_RUNNING:
				final String jTitle = jsonData.getString(JsonDataKeys.JOB_TITLE.toString());
				final String jImageURLKey = JsonDataKeys.JOB_IMAGE_URL.toString();
				final String jImageURL = jsonData.isNull(jImageURLKey) ? null : jsonData.getString(jImageURLKey);
				return (T) new JobAlreadyRunningMessage(jobID, jTitle, jImageURL);

			case JOB_READY:
				return (T) new JobReadyMessage(jobID);

			case JOB_OUTPUT:
				final JSONArray jsonLines = jsonData.getJSONArray(JsonDataKeys.LINES.toString());
				final List<String> lineList = new ArrayList<>();
				for (int i = 0; i < jsonLines.length(); ++i) {
					lineList.add(jsonLines.getString(i));
				}
				final String[] lines = lineList.toArray(new String[lineList.size()]);
				return (T) new JobOutputMessage(jobID, lines);

			case JOB_PROGRESS:
				final short progress = (short) jsonData.getInt(JsonDataKeys.PROGRESS.toString());
				return (T) new JobProgressMessage(jobID, progress);

			case JOB_FINISHED:
				final String downloadURL = jsonData.getString(JsonDataKeys.URL.toString());
				final String cachedUTCDateKey = JsonDataKeys.CACHED_UTC_DATE.toString();
				final String dateUTC = jsonData.isNull(cachedUTCDateKey) ? null : jsonData.getString(cachedUTCDateKey);
				final DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DEFAULT, Locale.US);
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date cachedDate = null;
				try {
					cachedDate = dateUTC != null ? dateFormat.parse(dateUTC) : null;
				} catch (ParseException e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
				return (T) new JobFinishedMessage(jobID, downloadURL, cachedDate);

			default:
				return null;
		}
	}
}
