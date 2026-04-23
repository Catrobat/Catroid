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
package org.catrobat.catroid.test.web

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.format.DateUtils
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.common.Survey
import org.catrobat.catroid.transfers.GetSurveyTask
import org.catrobat.catroid.utils.Utils
import org.json.JSONException
import org.junit.Before
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.Date

@RunWith(PowerMockRunner::class)
@PrepareForTest(Utils::class, PreferenceManager::class, DateUtils::class)
class EmbeddedSurveyTest {
    private var currentTimeInMilliseconds: Long = 0
    private var surveySpy: Survey? = null
    private val URL1: String = "https://url1.at/"
    private val URL2: String = "https://url2.at/"

    @Mock
    var sharedPreferencesMock: SharedPreferences? = null
    var sharedPreferenceEditorMock: SharedPreferences.Editor? = null
    var contextMock: Context? = null
    var surveyMock: Survey? = null
    var getSurveyTaskMock: GetSurveyTask? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        sharedPreferencesMock = Mockito.mock(SharedPreferences::class.java)
        sharedPreferenceEditorMock = Mockito.mock(
            SharedPreferences.Editor::class.java
        )
        contextMock = Mockito.mock(Context::class.java)
        PowerMockito.mockStatic(PreferenceManager::class.java)
        PowerMockito.mockStatic(Utils::class.java)
        PowerMockito.mockStatic(DateUtils::class.java)
        currentTimeInMilliseconds = System.currentTimeMillis()
        surveyMock = Mockito.mock(Survey::class.java)
        getSurveyTaskMock = Mockito.mock(GetSurveyTask::class.java)
    }

    @Test
    fun doNotShowSurveyUnderMinimumTime() {
        val dateYesterday = Date(currentTimeInMilliseconds - dayInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS - 1).toLong()
        initMocks(
            dateYesterday, timeSpentInApp, showSurveyKey = false, networkConnected = true,
            uploadFlag = false
        )
        surveySpy?.showSurvey(contextMock)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .getSurvey(contextMock)
    }

    @Test
    fun doNotShowSurveyOnSameDay() {
        val dateToday = Date(currentTimeInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS + 1).toLong()
        initMocks(
            dateToday, timeSpentInApp, showSurveyKey = false, networkConnected = true,
            uploadFlag = false
        )
        surveySpy?.showSurvey(contextMock)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .getSurvey(contextMock)
    }

    @Test
    fun doNotShowSurveyWhenNoInternetConnection() {
        val dateYesterday = Date(currentTimeInMilliseconds - dayInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS + 1).toLong()
        initMocks(
            dateYesterday, timeSpentInApp, showSurveyKey = false, networkConnected = false,
            uploadFlag = false
        )
        surveySpy?.showSurvey(contextMock)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .getSurvey(contextMock)
    }

    @Test
    fun doNotShowSurveyTwice() {
        val dateYesterday = Date(currentTimeInMilliseconds - dayInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS + 1).toLong()
        initMocks(
            dateYesterday, timeSpentInApp, showSurveyKey = false, networkConnected = true,
            uploadFlag = false
        )
        Mockito.`when`(
            sharedPreferencesMock!!.getLong(
                ArgumentMatchers.eq(SharedPreferenceKeys.SURVEY_URL1_HASH_KEY),
                ArgumentMatchers.anyLong()
            )
        ).thenReturn(URL1.hashCode().toLong())
        Mockito.`when`(
            sharedPreferencesMock!!.getLong(
                ArgumentMatchers.eq(SharedPreferenceKeys.SURVEY_URL2_HASH_KEY),
                ArgumentMatchers.anyLong()
            )
        ).thenReturn(0)

        surveySpy?.onSurveyReceived(contextMock, URL1)
        Mockito.verify(surveySpy, Mockito.times(1))!!
            .isUrlNew(contextMock, URL1)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .saveUrlHash(contextMock, URL1)
    }

    @Test
    fun doNotShowSameSurveyAfterShowingAnotherSurvey() {
        val dateYesterday = Date(currentTimeInMilliseconds - dayInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS + 1).toLong()
        initMocks(
            dateYesterday, timeSpentInApp, showSurveyKey = false, networkConnected = true,
            uploadFlag = false
        )
        Mockito.`when`(
            sharedPreferencesMock!!.getLong(
                ArgumentMatchers.eq(SharedPreferenceKeys.SURVEY_URL1_HASH_KEY),
                ArgumentMatchers.anyLong()
            )
        ).thenReturn(URL2.hashCode().toLong())
        Mockito.`when`(
            sharedPreferencesMock!!.getLong(
                ArgumentMatchers.eq(SharedPreferenceKeys.SURVEY_URL2_HASH_KEY),
                ArgumentMatchers.anyLong()
            )
        ).thenReturn(URL1.hashCode().toLong())

        surveySpy?.onSurveyReceived(contextMock, URL1)
        Mockito.verify(surveySpy, Mockito.times(1))!!
            .isUrlNew(contextMock, URL1)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .saveUrlHash(contextMock, URL1)
    }

    @Test
    fun doNotShowSurveyForLanguageNotDeposited() {
        val dateYesterday = Date(currentTimeInMilliseconds - dayInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS + 1).toLong()
        initMocks(
            dateYesterday, timeSpentInApp, showSurveyKey = false, networkConnected = true,
            uploadFlag = false
        )

        PowerMockito.`when`(
            getSurveyTaskMock?.parseSurvey(ArgumentMatchers.anyString())
        ).thenReturn("")

        val exception = ExpectedException.none()
        exception.expect(JSONException::class.java)
    }

    @Test
    fun showSurveyAfterUploadWhenInternetConnection() {
        val dateYesterday = Date(currentTimeInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS + 1).toLong()
        initMocks(
            dateYesterday, timeSpentInApp, showSurveyKey = true, networkConnected = true,
            uploadFlag = true
        )
        surveySpy?.showSurvey(contextMock)
        Mockito.verify(surveySpy, Mockito.times(1))!!
            .getSurvey(contextMock)
    }

    @Test
    fun showSurveyFulfilledRequirements() {
        val dateYesterday = Date(currentTimeInMilliseconds - dayInMilliseconds)
        val timeSpentInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP_IN_SECONDS + 1).toLong()
        initMocks(
            dateYesterday, timeSpentInApp, showSurveyKey = false, networkConnected = true,
            uploadFlag = false
        )
        surveySpy?.showSurvey(contextMock)
        Mockito.verify(surveySpy, Mockito.times(1))!!
            .getSurvey(contextMock)
    }

    private fun initMocks(date: Date, timeSpentInApp: Long, showSurveyKey: Boolean, networkConnected: Boolean, uploadFlag: Boolean) {
        PowerMockito.`when`(Utils.isNetworkAvailable(contextMock)).thenReturn(networkConnected)
        PowerMockito.`when`(DateUtils.isToday(ArgumentMatchers.anyLong())).thenReturn(
            currentTimeInMilliseconds - date.time < dayInMilliseconds
        )
        PowerMockito.`when`(PreferenceManager.getDefaultSharedPreferences(contextMock))
            .thenReturn(sharedPreferencesMock)
        Mockito.`when`(
            sharedPreferencesMock!!.getLong(
                ArgumentMatchers.eq(SharedPreferenceKeys.TIME_SPENT_IN_APP_IN_SECONDS_KEY),
                ArgumentMatchers.anyLong()
            )
        ).thenReturn(timeSpentInApp)
        Mockito.`when`(
            sharedPreferencesMock!!.getLong(
                ArgumentMatchers.eq(SharedPreferenceKeys.LAST_USED_DATE_KEY),
                ArgumentMatchers.anyLong()
            )
        ).thenReturn(date.time)
        Mockito.`when`(
            sharedPreferencesMock!!.getBoolean(
                ArgumentMatchers.eq(SharedPreferenceKeys.SHOW_SURVEY_KEY),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(showSurveyKey)
        PowerMockito.`when`(sharedPreferencesMock!!.edit()).thenReturn(sharedPreferenceEditorMock)
        Mockito.`when`(
            sharedPreferenceEditorMock!!.putBoolean(
                ArgumentMatchers.eq(
                    SharedPreferenceKeys.SHOW_SURVEY_KEY
                ), ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(sharedPreferenceEditorMock)
        Mockito.doNothing().`when`(sharedPreferenceEditorMock)!!
            .apply()
        val survey = Survey(contextMock)
        surveySpy = Mockito.spy(survey)
        Mockito.`when`(surveySpy?.getUploadFlag()).thenReturn(uploadFlag)
    }

    companion object {
        private const val dayInMilliseconds = (24 * 60 * 60 * 1000).toLong()
    }
}
