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

package org.catrobat.catroid.scratchconverter.protocol.message.base;

import android.support.annotation.Nullable;

import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys.JsonDataKeys;
import org.catrobat.catroid.scratchconverter.protocol.message.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseMessage extends Message {

	public enum Type {
		ERROR(0),
		INFO(1),
		CLIENT_ID(2);

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

	@Nullable
	public static <T extends BaseMessage> T fromJson(final JSONObject jsonMessage) throws JSONException {
		final JSONObject jsonData = jsonMessage.getJSONObject(JsonKeys.DATA.toString());

		switch (Type.valueOf(jsonMessage.getInt(JsonKeys.TYPE.toString()))) {
			case ERROR:
				return (T) new ErrorMessage(jsonData.getString(JsonDataKeys.MSG.toString()));

			case CLIENT_ID:
				return (T) new ClientIDMessage(jsonData.getLong(JsonDataKeys.CLIENT_ID.toString()));

			case INFO:
				final double catrobatLangVersion = jsonData.getDouble(JsonDataKeys.CATROBAT_LANGUAGE_VERSION.toString());
				final JSONArray jobsInfo = jsonData.getJSONArray(JsonDataKeys.JOBS_INFO.toString());
				final List<Job> jobList = new ArrayList<>();
				if (jobsInfo != null) {
					for (int i = 0; i < jobsInfo.length(); ++i) {
						jobList.add(Job.fromJson(jobsInfo.getJSONObject(i)));
					}
				}
				return (T) new InfoMessage((float) catrobatLangVersion, jobList.toArray(new Job[jobList.size()]));

			default:
				return null;
		}
	}
}
