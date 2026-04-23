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

package org.catrobat.catroid.common;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import org.catrobat.catroid.transfers.GetSurveyTask;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.utils.Utils;

import java.util.Date;

import androidx.annotation.VisibleForTesting;

import static org.catrobat.catroid.common.SharedPreferenceKeys.LAST_USED_DATE_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_SURVEY_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SURVEY_URL1_HASH_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SURVEY_URL2_HASH_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.TIME_SPENT_IN_APP_IN_SECONDS_KEY;

public class Survey implements GetSurveyTask.SurveyResponseListener {
	@VisibleForTesting
	public static final int MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS = 60 * 60;

	long sessionTimeSpentInIdeInSeconds;
	long sessionStartTimeInMilliseconds;
	long sessionTimeSpentInStageInMilliseconds;
	long stageStartTimeInMilliseconds;
	boolean fulfilledSurveyRequirements;
	boolean uploadFlag;

	public Survey(Context context) {
		fulfilledSurveyRequirements = false;
		uploadFlag = false;
		sessionTimeSpentInStageInMilliseconds = 0;
		sessionStartTimeInMilliseconds = 0;

		checkSurveyRequirement(context);
	}

	public void startAppTime(Context context) {
		checkSurveyRequirement(context);

		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putLong(LAST_USED_DATE_KEY, new Date(System.currentTimeMillis()).getTime())
				.apply();

		if (!fulfilledSurveyRequirements && sessionStartTimeInMilliseconds == 0) {
			sessionStartTimeInMilliseconds = System.currentTimeMillis();
		}
	}

	public void endAppTime(Context context) {
		if (!fulfilledSurveyRequirements) {
			sessionTimeSpentInIdeInSeconds = (System.currentTimeMillis() - sessionStartTimeInMilliseconds - sessionTimeSpentInStageInMilliseconds) / 1000;
			sessionStartTimeInMilliseconds = 0;

			long oldTimeSpentInApp = PreferenceManager.getDefaultSharedPreferences(context)
					.getLong(TIME_SPENT_IN_APP_IN_SECONDS_KEY, 0);

			PreferenceManager.getDefaultSharedPreferences(context)
					.edit()
					.putLong(TIME_SPENT_IN_APP_IN_SECONDS_KEY, oldTimeSpentInApp + sessionTimeSpentInIdeInSeconds)
					.apply();
		}
	}

	public void startStageTime() {
		if (!fulfilledSurveyRequirements) {
			stageStartTimeInMilliseconds = System.currentTimeMillis();
		}
	}

	public void endStageTime() {
		if (!fulfilledSurveyRequirements) {
			sessionTimeSpentInStageInMilliseconds += System.currentTimeMillis() - stageStartTimeInMilliseconds;
		}
	}

	private void checkSurveyRequirement(Context context) {
		if (!fulfilledSurveyRequirements) {
			long timeSpentInApp = PreferenceManager.getDefaultSharedPreferences(context)
					.getLong(TIME_SPENT_IN_APP_IN_SECONDS_KEY, 0);

			fulfilledSurveyRequirements = timeSpentInApp > MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS;
		}
	}

	@VisibleForTesting
	public boolean getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(boolean value) {
		uploadFlag = value;
	}

	public void showSurvey(Context context) {
		if (fulfilledSurveyRequirements) {
			long oldDate = PreferenceManager.getDefaultSharedPreferences(context)
					.getLong(LAST_USED_DATE_KEY, new Date(System.currentTimeMillis()).getTime());

			boolean showSurvey = PreferenceManager.getDefaultSharedPreferences(context)
					.getBoolean(SHOW_SURVEY_KEY, false);

			if (!DateUtils.isToday(oldDate) || (showSurvey && getUploadFlag())) {
				if (Utils.isNetworkAvailable(context)) {
					getSurvey(context);
				} else {
					PreferenceManager.getDefaultSharedPreferences(context)
							.edit()
							.putBoolean(SHOW_SURVEY_KEY, true)
							.apply();
				}
			}
		}

		uploadFlag = false;
	}

	@VisibleForTesting
	public void getSurvey(Context context) {
		GetSurveyTask getSurveyTask = new GetSurveyTask(context);
		getSurveyTask.setOnSurveyResponseListener(this);
		getSurveyTask.execute();
	}

	@Override
	public void onSurveyReceived(Context context, String surveyUrl) {
		if (isUrlNew(context, surveyUrl)) {
			Intent intent = new Intent(context, WebViewActivity.class);
			intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, surveyUrl);
			intent.putExtra(WebViewActivity.INTENT_FORCE_OPEN_IN_APP, true);
			context.startActivity(intent);

			saveUrlHash(context, surveyUrl);

			PreferenceManager.getDefaultSharedPreferences(context)
					.edit()
					.putLong(LAST_USED_DATE_KEY, new Date(System.currentTimeMillis()).getTime())
					.apply();

			PreferenceManager.getDefaultSharedPreferences(context)
					.edit()
					.putBoolean(SHOW_SURVEY_KEY, false)
					.apply();
		}
	}

	@VisibleForTesting
	public void saveUrlHash(Context context, String surveyUrl) {
		long firstSurveyHash = PreferenceManager.getDefaultSharedPreferences(context)
				.getLong(SURVEY_URL1_HASH_KEY, 0);

		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putLong(SURVEY_URL2_HASH_KEY, firstSurveyHash)
				.apply();

		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putLong(SURVEY_URL1_HASH_KEY, surveyUrl.hashCode())
				.apply();
	}

	@VisibleForTesting
	public boolean isUrlNew(Context context, String surveyUrl) {
		long firstSurveyHash = PreferenceManager.getDefaultSharedPreferences(context)
				.getLong(SURVEY_URL1_HASH_KEY, 0);

		long secondSurveyHash = PreferenceManager.getDefaultSharedPreferences(context)
				.getLong(SURVEY_URL2_HASH_KEY, 0);

		return surveyUrl.hashCode() != firstSurveyHash && surveyUrl.hashCode() != secondSurveyHash;
	}
}
