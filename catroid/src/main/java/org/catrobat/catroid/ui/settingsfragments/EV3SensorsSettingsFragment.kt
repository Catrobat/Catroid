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

package org.catrobat.catroid.ui.settingsfragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.EV3_SETTINGS_CATEGORY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED

class EV3SensorsSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsFragment.setToChosenLanguage(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.ev3_preferences)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (BuildConfig.FEATURE_LEGO_EV3_ENABLED) {
            val ev3SwitchPreference =
                findPreference<SwitchPreference>(SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED)!!
            val ev3ConnectionSettings =
                findPreference<PreferenceCategory>(EV3_SETTINGS_CATEGORY)!!

            ev3ConnectionSettings.isEnabled = ev3SwitchPreference.isChecked
            ev3SwitchPreference.setOnPreferenceChangeListener { _, isChecked ->
                ev3ConnectionSettings.isEnabled = isChecked as Boolean
                true
            }

            for (sensorPreference in LegoSensors.EV3_SENSORS) {
                val listPreference = findPreference<ListPreference>(sensorPreference)
                listPreference?.apply {
                    setEntries(R.array.ev3_sensor_chooser)
                    entryValues = EV3Sensor.Sensor.getSensorCodes()
                }
            }
        }
    }

    companion object {
        val EV3_SENSORS_SETTINGS_FRAGMENT_TAG: String =
            EV3SensorsSettingsFragment::class.java.simpleName
    }
}
