/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

object SettingsFragmentTestUtils {

    private val sharedPreferences: SharedPreferences by lazy {
        getDefaultSharedPreferences(getApplicationContext())
    }

    fun switchPreference(stringResource: Int, sharedPreferenceTag: String) {
        clickOnSettingsItem(stringResource)
        assertTrue(sharedPreferences.getBoolean(sharedPreferenceTag, false))

        clickOnSettingsItem(stringResource)
        assertFalse(sharedPreferences.getBoolean(sharedPreferenceTag, true))

        checkSwitchWidgetIsDisplayed(stringResource)
    }

    fun clickOnSettingsItem(stringResource: Int) {
        onView(withId(R.id.recycler_view))
            .perform(actionOnItem<ViewHolder>(hasDescendant(withText(stringResource)), click()))
    }

    private fun checkSwitchWidgetIsDisplayed(text: Int) {
        onView(
            allOf(
                withId(android.R.id.switch_widget),
                isDescendantOfA(withId(R.id.recycler_view)),
                withParent(withParent(withChild(withChild(withText(text)))))
            )
        )
            .check(matches(isDisplayed()))
    }

    fun <T : Any> checkFragmentIs(clazz: Class<T>, fragments: List<Fragment>) {
        assertTrue(fragments.any { fragment -> fragment::class.java.simpleName == clazz.simpleName })
    }

    fun checkToolbarTitleIsDisplayed(stringResource: Int) {
        onView(withId(R.id.toolbar))
            .check(matches(hasDescendant(withText(stringResource))))
    }

    fun saveBooleanSettings(savedSettings: HashMap<String, Boolean>, settingItems: List<String>) {
        for (setting in settingItems) {
            savedSettings[setting] = sharedPreferences.getBoolean(setting, false)
        }
    }

    fun loadSavedBooleanSettings(savedSettings: HashMap<String, Boolean>) {
        for (setting in savedSettings) {
            sharedPreferences.edit {
                putBoolean(setting.key, setting.value)
            }
        }
    }

    fun setAllBooleanSettingsTo(value: Boolean, settingItems: List<String>) {
        for (setting in settingItems) {
            sharedPreferences.edit {
                putBoolean(setting, value)
            }
        }
    }

    fun saveStringSettings(savedSensorSettings: HashMap<String, String>, sensors: Array<String>) {
        for (sensor in sensors) {
            savedSensorSettings[sensor] = sharedPreferences.getString(sensor, "") ?: continue
        }
    }

    fun loadSavedStringSettings(savedSensorSettings: HashMap<String, String>) {
        for (sensorSetting in savedSensorSettings) {
            sharedPreferences.edit {
                putString(sensorSetting.key, sensorSetting.value)
            }
        }
    }
}
