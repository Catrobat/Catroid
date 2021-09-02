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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DroneConfigPreference
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.setToChosenLanguage

val PARROT_AR_DRONE_SETTINGS_FRAGMENT_TAG = ParrotARDroneSettingsFragment::class.java.simpleName
private const val FIRST_INDEX = 0
private const val SECOND_INDEX = 1
private const val THIRD_INDEX = 2
private const val FOURTH_INDEX = 3
class ParrotARDroneSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.drone_preferences)
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

        val droneSwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS)!!
        val droneConnectionSettings =
            findPreference<PreferenceCategory>(DRONE_SETTINGS_CATEGORY)!!

        droneConnectionSettings.isEnabled = droneSwitchPreference.isChecked
        droneSwitchPreference.setOnPreferenceChangeListener { _, isChecked ->
            droneConnectionSettings.isEnabled = (isChecked as Boolean?)!!
            true
        }

        val dronePreferences = arrayOf(
            DRONE_CONFIGS,
            DRONE_ALTITUDE_LIMIT,
            DRONE_VERTICAL_SPEED,
            DRONE_ROTATION_SPEED,
            DRONE_TILT_ANGLE
        )

        for (dronePreference in dronePreferences) {
            val listPreference = findPreference<ListPreference>(dronePreference)!!
            when (dronePreference) {
                DRONE_CONFIGS -> {
                    listPreference.setEntries(R.array.drone_setting_default_config)
                    listPreference.setOnPreferenceChangeListener { _, newValue ->
                        val index = listPreference.findIndexOfValue(newValue.toString())
                        for (dronePreference1 in dronePreferences) {
                            val listPreference1 = findPreference<ListPreference>(dronePreference1)!!
                            when (dronePreference1) {
                                DRONE_ALTITUDE_LIMIT -> listPreference1.value = "FIRST"
                                DRONE_VERTICAL_SPEED,
                                DRONE_ROTATION_SPEED,
                                DRONE_TILT_ANGLE -> {
                                    if (index == FIRST_INDEX || index == SECOND_INDEX) {
                                        listPreference1.value = "SECOND"
                                    }
                                    if (index == THIRD_INDEX || index == FOURTH_INDEX) {
                                        listPreference1.value = "THIRD"
                                    }
                                }
                            }
                        }
                        true
                    }
                }
                DRONE_ALTITUDE_LIMIT -> listPreference
                    .setEntries(R.array.drone_altitude_spinner_items)
                DRONE_VERTICAL_SPEED -> listPreference
                    .setEntries(R.array.drone_max_vertical_speed_items)
                DRONE_ROTATION_SPEED -> listPreference
                    .setEntries(R.array.drone_max_rotation_speed_items)
                DRONE_TILT_ANGLE -> listPreference
                    .setEntries(R.array.drone_max_tilt_angle_items)
            }
            listPreference.entryValues = DroneConfigPreference.Preferences.getPreferenceCodes()
        }
    }
}
