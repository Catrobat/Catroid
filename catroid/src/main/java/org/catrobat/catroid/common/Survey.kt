/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.common

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.text.format.DateUtils
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.transfers.GetSurveyTask
import org.catrobat.catroid.transfers.GetSurveyTask.SurveyResponseListener
import org.catrobat.catroid.ui.WebViewActivity
import org.catrobat.catroid.utils.Utils
import java.util.Date

class Survey(context: Context) : SurveyResponseListener {
    var sessionTimeSpentInIdeInSeconds: Long = 0
    var sessionStartTimeInMilliseconds: Long = 0
    var sessionTimeSpentInStageInMilliseconds: Long = 0
    var stageStartTimeInMilliseconds: Long = 0
    var fulfilledSurveyRequirements = false

    @get:VisibleForTesting
    var uploadFlag = false
    fun startAppTime(context: Context) {
        checkSurveyRequirement(context)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putLong(SharedPreferenceKeys.LAST_USED_DATE_KEY, Date(System.currentTimeMillis()).time)
            .apply()
        if (!fulfilledSurveyRequirements && sessionStartTimeInMilliseconds == 0L) {
            sessionStartTimeInMilliseconds = System.currentTimeMillis()
        }
    }

    fun endAppTime(context: Context?) {
        if (!fulfilledSurveyRequirements) {
            sessionTimeSpentInIdeInSeconds =
                (System.currentTimeMillis() - sessionStartTimeInMilliseconds - sessionTimeSpentInStageInMilliseconds) / 1000
            sessionStartTimeInMilliseconds = 0
            val oldTimeSpentInApp = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(SharedPreferenceKeys.TIME_SPENT_IN_APP_IN_SECONDS_KEY, 0)
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(
                    SharedPreferenceKeys.TIME_SPENT_IN_APP_IN_SECONDS_KEY,
                    oldTimeSpentInApp + sessionTimeSpentInIdeInSeconds
                )
                .apply()
        }
    }

    fun startStageTime() {
        if (!fulfilledSurveyRequirements) {
            stageStartTimeInMilliseconds = System.currentTimeMillis()
        }
    }

    fun endStageTime() {
        if (!fulfilledSurveyRequirements) {
            sessionTimeSpentInStageInMilliseconds += System.currentTimeMillis() - stageStartTimeInMilliseconds
        }
    }

    private fun checkSurveyRequirement(context: Context) {
        if (!fulfilledSurveyRequirements) {
            val timeSpentInApp = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(SharedPreferenceKeys.TIME_SPENT_IN_APP_IN_SECONDS_KEY, 0)
            fulfilledSurveyRequirements = timeSpentInApp > MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS
        }
    }

    fun showSurvey(context: Context?) {
        if (fulfilledSurveyRequirements) {
            val oldDate = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(
                    SharedPreferenceKeys.LAST_USED_DATE_KEY,
                    Date(System.currentTimeMillis()).time
                )
            val showSurvey = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SharedPreferenceKeys.SHOW_SURVEY_KEY, false)
            if (!DateUtils.isToday(oldDate) || showSurvey && uploadFlag) {
                if (Utils.isNetworkAvailable(context)) {
                    getSurvey(context)
                } else {
                    PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putBoolean(SharedPreferenceKeys.SHOW_SURVEY_KEY, true)
                        .apply()
                }
            }
        }
        uploadFlag = false
    }

    @VisibleForTesting
    fun getSurvey(context: Context?) {
        val getSurveyTask = GetSurveyTask(context)
        getSurveyTask.setOnSurveyResponseListener(this)
        getSurveyTask.execute()
    }

    override fun onSurveyReceived(context: Context, surveyUrl: String) {
        if (isUrlNew(context, surveyUrl)) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, surveyUrl)
            intent.putExtra(WebViewActivity.INTENT_FORCE_OPEN_IN_APP, true)
            context.startActivity(intent)
            saveUrlHash(context, surveyUrl)
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(
                    SharedPreferenceKeys.LAST_USED_DATE_KEY,
                    Date(System.currentTimeMillis()).time
                )
                .apply()
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SharedPreferenceKeys.SHOW_SURVEY_KEY, false)
                .apply()
        }
    }

    @VisibleForTesting
    fun saveUrlHash(context: Context?, surveyUrl: String) {
        val firstSurveyHash = PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(SharedPreferenceKeys.SURVEY_URL1_HASH_KEY, 0)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putLong(SharedPreferenceKeys.SURVEY_URL2_HASH_KEY, firstSurveyHash)
            .apply()
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putLong(SharedPreferenceKeys.SURVEY_URL1_HASH_KEY, surveyUrl.hashCode().toLong())
            .apply()
    }

    @VisibleForTesting
    fun isUrlNew(context: Context?, surveyUrl: String): Boolean {
        val firstSurveyHash = PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(SharedPreferenceKeys.SURVEY_URL1_HASH_KEY, 0)
        val secondSurveyHash = PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(SharedPreferenceKeys.SURVEY_URL2_HASH_KEY, 0)
        return surveyUrl.hashCode().toLong() != firstSurveyHash && surveyUrl.hashCode()
            .toLong() != secondSurveyHash
    }

    companion object {
        @VisibleForTesting
        val MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS = 60 * 60
    }

    init {
        checkSurveyRequirement(context)
    }
}