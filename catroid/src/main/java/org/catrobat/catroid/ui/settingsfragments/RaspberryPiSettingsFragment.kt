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
package org.catrobat.catroid.ui.settingsfragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.setToChosenLanguage

val RASPBERRY_PI_SETTINGS_FRAGMENT_TAG = RaspberryPiSettingsFragment::class.java.simpleName
class RaspberryPiSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.raspberry_preferences)
    }

    override fun onResume() {
        super.onResume()
        requireActivity()
            .takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.title = preferenceScreen.title
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToChosenLanguage(requireActivity())

        val raspSwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_SHOW_RASPI_BRICKS)!!
        val rpiConnectionSettings =
            findPreference<PreferenceCategory>(RASPI_CONNECTION_SETTINGS_CATEGORY)!!

        rpiConnectionSettings.isEnabled = raspSwitchPreference.isChecked
        raspSwitchPreference.setOnPreferenceChangeListener { _, isChecked ->
            rpiConnectionSettings.isEnabled = (isChecked as Boolean?)!!
            true
        }

        findPreference<EditTextPreference>(RASPI_HOST)?.apply {
            setOnBindEditTextListener {
                it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
            }
            summary = text
            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue.toString()
                true
            }
        }

        findPreference<EditTextPreference>(RASPI_PORT)?.apply {
            setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER
            }
            summary = text
            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue.toString()
                true
            }
        }
    }

    companion object {
        @JvmStatic
        fun isRaspiSharedPreferenceEnabled(context: Context): Boolean {
            PreferenceManager.setDefaultValues(context, R.xml.raspberry_preferences, true)
            val isPreferenceEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_SHOW_RASPI_BRICKS, false)
            PreferenceManager.setDefaultValues(context, R.xml.preferences, true)
            return isPreferenceEnabled
        }
    }
}
