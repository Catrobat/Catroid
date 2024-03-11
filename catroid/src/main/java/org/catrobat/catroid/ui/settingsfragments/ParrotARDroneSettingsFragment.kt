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

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DroneConfigPreference
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_ALTITUDE_LIMIT
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_CONFIGS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_ROTATION_SPEED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_SETTINGS_CATEGORY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_TILT_ANGLE
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_VERTICAL_SPEED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS

class ParrotARDroneSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsFragment.setToChosenLanguage(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.drone_preferences)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
            val droneSwitchPreference =
                findPreference<SwitchPreference>(SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS)!!
            val droneConnectionSettings =
                findPreference<PreferenceCategory>(DRONE_SETTINGS_CATEGORY)!!

            droneConnectionSettings.isEnabled = droneSwitchPreference.isChecked

            droneSwitchPreference.setOnPreferenceChangeListener { _, isChecked ->
                droneConnectionSettings.isEnabled = isChecked as Boolean
                true
            }

            setUpDroneListEntries()
        }
    }

    private fun setUpDroneListEntries() {
        val dronePreferences = arrayOf(
            DRONE_CONFIGS, DRONE_ALTITUDE_LIMIT, DRONE_VERTICAL_SPEED,
            DRONE_ROTATION_SPEED, DRONE_TILT_ANGLE
        )

        for (dronePreference in dronePreferences) {
            val listPreference = findPreference<ListPreference>(dronePreference)!!
            when (dronePreference) {
                DRONE_CONFIGS -> {
                    listPreference.setEntries(R.array.drone_setting_default_config)
                    addDroneConfigPreferenceChangeListener(listPreference, dronePreferences)
                }
                DRONE_ALTITUDE_LIMIT ->
                    listPreference.setEntries(R.array.drone_altitude_spinner_items)
                DRONE_VERTICAL_SPEED ->
                    listPreference.setEntries(R.array.drone_max_vertical_speed_items)
                DRONE_ROTATION_SPEED ->
                    listPreference.setEntries(R.array.drone_max_rotation_speed_items)
                DRONE_TILT_ANGLE ->
                    listPreference.setEntries(R.array.drone_max_tilt_angle_items)
            }
            listPreference.entryValues = DroneConfigPreference.Preferences.getPreferenceCodes()
        }
    }

    private fun addDroneConfigPreferenceChangeListener(
        listPreference: ListPreference,
        dronePreferences: Array<String>
    ) {
        listPreference.setOnPreferenceChangeListener { _, newValue ->
            val index = listPreference.findIndexOfValue(newValue.toString())
            for (dronePreference in dronePreferences) {
                val currentListPreference = findPreference<ListPreference>(dronePreference)!!
                when (dronePreference) {
                    DRONE_ALTITUDE_LIMIT -> currentListPreference.value = "FIRST"
                    DRONE_VERTICAL_SPEED -> setListPreferenceValue(currentListPreference, index)
                    DRONE_ROTATION_SPEED -> setListPreferenceValue(currentListPreference, index)
                    DRONE_TILT_ANGLE -> setListPreferenceValue(currentListPreference, index)
                }
            }
            true
        }
    }

    private fun setListPreferenceValue(listPreference: ListPreference, index: Int) {
        if (index < 2) {
            listPreference.value = "SECOND"
        } else {
            listPreference.value = "THIRD"
        }
    }

    companion object {
        val PARROT_AR_DRONE_SETTINGS_FRAGMENT_TAG: String =
            ParrotARDroneSettingsFragment::class.java.simpleName
    }
}
