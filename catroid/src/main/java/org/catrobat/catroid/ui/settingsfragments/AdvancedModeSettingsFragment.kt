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

package org.catrobat.catroid.ui.settingsfragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.sync.ProjectsCategoriesSync
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.utils.ToastUtil
import org.koin.java.KoinJavaComponent.inject

import org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY
import org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment.CUSTOM_PROFILE

class AdvancedModeSettingsFragment : PreferenceFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val TAG: String = AdvancedModeSettingsFragment::class.java.simpleName
    }

    private var preferenceChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.advanced_mode)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        toggleSettings(sharedPreferences)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        SettingsFragment.setToChosenLanguage(activity)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        (activity as AppCompatActivity).supportActionBar?.title = preferenceScreen.title
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDetach() {
        super.onDetach()
        if (preferenceChanged) {
            startActivity(Intent(activity?.baseContext, MainMenuActivity::class.java))
            startActivity(Intent(activity?.baseContext, SettingsActivity::class.java))
            ToastUtil.showSuccess(activity, getString(R.string.accessibility_settings_applied))
            activity?.finishAffinity()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        preferenceChanged = true
        sharedPreferences.edit()
            .putBoolean(CUSTOM_PROFILE, true)
            .apply()

        toggleSettings(sharedPreferences)
    }

    private fun toggleSettings(sharedPreferences: SharedPreferences) {
        val isSetToAdvancedMode =
            sharedPreferences.getBoolean(SettingsFragment.SETTINGS_CATBLOCKS_ADVANCED_MODE, false)

        if (!isSetToAdvancedMode) {
            val setToEnglishCheckbox =
                findPreference(SettingsFragment.SETTINGS_SET_TO_ENGLISH_ADVANCED_MODE) as CheckBoxPreference
            setToEnglishCheckbox.isEnabled = false
        }

        val advancedModeCheckbox =
            findPreference(SettingsFragment.SETTINGS_CATBLOCKS_ADVANCED_MODE) as Preference
        val setToEnglishCheckbox =
            findPreference(SettingsFragment.SETTINGS_SET_TO_ENGLISH_ADVANCED_MODE) as Preference

        advancedModeCheckbox.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val isAdvancedChanged = newValue as Boolean
                if (isAdvancedChanged) {
                    setToEnglishCheckbox.isEnabled = true
                } else {
                    if (sharedPreferences.getBoolean(
                            SettingsFragment
                                .SETTINGS_SET_TO_ENGLISH_ADVANCED_MODE, false
                        )
                    ) {
                        SettingsFragment.setSetToEnglish(activity.baseContext, false)
                        changeToEnglish(
                            isAdvancedMode = false,
                            isSetToEnglish = false,
                            sharedPreferences
                        )
                    }
                }
                true
            }

        setToEnglishCheckbox.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val isEnglishChanged = newValue as Boolean
                changeToEnglish(isSetToAdvancedMode, isEnglishChanged, sharedPreferences)
                true
            }
    }

    private fun changeToEnglish(
        isAdvancedMode: Boolean,
        isSetToEnglish: Boolean,
        sharedPreferences: SharedPreferences
    ) {
        var language = "en"
        if (isAdvancedMode && isSetToEnglish) {
            if (sharedPreferences.getString(LANGUAGE_TAG_KEY, "") != "en") {
                SettingsFragment.setAdvancedModePreviousLanguage(
                    activity?.baseContext,
                    sharedPreferences.getString(
                        LANGUAGE_TAG_KEY,
                        ""
                    )
                )
            }
        } else {
            language = sharedPreferences.getString(
                SettingsFragment.SETTINGS_ADVANCED_MODE_PREVIOUS_LANGUAGE,
                ""
            ) ?: ""
        }
        if (!language.equals("")) {
            SettingsFragment.setLanguageSharedPreference(activity?.baseContext, language)
            startActivity(Intent(activity?.baseContext, MainMenuActivity::class.java))
            activity?.finishAffinity()
            Thread { inject(ProjectsCategoriesSync::class.java).value.sync(true) }.start()
        }
    }
}
