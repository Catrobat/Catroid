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
package org.catrobat.catroid.test.web

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.format.DateUtils
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.common.Survey
import org.catrobat.catroid.utils.Utils
import org.junit.Before
import org.junit.Test
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
    private var currentTimeInMillis: Long = 0
    private var surveySpy: Survey? = null

    @Mock
    var sharedPreferencesMock: SharedPreferences? = null
    var sharedPreferenceEditorMock: SharedPreferences.Editor? = null
    var contextMock: Context? = null
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
        currentTimeInMillis = System.currentTimeMillis()
    }

    @Test
    fun doNotShowSurveyUnderMinimumTime() {
        val dateYesterday = Date(currentTimeInMillis - dayInMilliseconds)
        val timeSpendInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP - 1).toLong()
        initMocks(dateYesterday, timeSpendInApp, showSurveyKey = false, networkConnected = true, uploadFlag = false)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .getSurvey(contextMock)
    }

    @Test
    fun doNotShowSurveyOnSameDay() {
        val dateToday = Date(currentTimeInMillis)
        val timeSpendInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP + 1).toLong()
        initMocks(dateToday, timeSpendInApp, showSurveyKey = false, networkConnected = true, uploadFlag = false)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .getSurvey(contextMock)
    }

    @Test
    fun doNotShowSurveyWhenNoInternetCon() {
        val dateYesterday = Date(System.currentTimeMillis() - dayInMilliseconds)
        val timeSpendInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP + 1).toLong()
        initMocks(dateYesterday, timeSpendInApp, showSurveyKey = false, networkConnected = false, uploadFlag = false)
        Mockito.verify(surveySpy, Mockito.times(0))!!
            .getSurvey(contextMock)
    }

    @Test
    fun showSurveyAfterUploadWhenInternetCon() {
        val dateYesterday = Date(System.currentTimeMillis())
        val timeSpendInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP + 1).toLong()
        initMocks(dateYesterday, timeSpendInApp, showSurveyKey = true, networkConnected = true, uploadFlag = true)
        Mockito.verify(surveySpy, Mockito.times(1))!!
            .getSurvey(contextMock)
    }

    @Test
    fun showSurveyFulfilledRequirements() {
        val dateYesterday = Date(currentTimeInMillis - dayInMilliseconds)
        val timeSpendInApp = (Survey.MINIMUM_TIME_SPENT_IN_APP + 1).toLong()
        initMocks(dateYesterday, timeSpendInApp, showSurveyKey = false, networkConnected = true, uploadFlag = false)
        Mockito.verify(surveySpy, Mockito.times(1))!!
            .getSurvey(contextMock)
    }

    private fun initMocks(date: Date, timeSpentInApp: Long, showSurveyKey: Boolean, networkConnected: Boolean, uploadFlag: Boolean) {
        PowerMockito.`when`(Utils.isNetworkAvailable(contextMock)).thenReturn(networkConnected)
        PowerMockito.`when`(DateUtils.isToday(ArgumentMatchers.anyLong())).thenReturn(
            currentTimeInMillis - date.time < dayInMilliseconds
        )
        PowerMockito.`when`(PreferenceManager.getDefaultSharedPreferences(contextMock))
            .thenReturn(sharedPreferencesMock)
        Mockito.`when`(
            sharedPreferencesMock!!.getLong(
                ArgumentMatchers.eq(SharedPreferenceKeys.TIME_SPENT_IN_APP_KEY),
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
        surveySpy?.showSurvey(contextMock)
    }

    companion object {
        private const val dayInMilliseconds = (24 * 360 * 1000).toLong()
    }
}
