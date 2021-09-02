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
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.ui.settingsfragments.LegoSensors.EV3_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.setToChosenLanguage

val EV3_SENSOR_SETTINGS_FRAGMENT_TAG = Ev3SensorsSettingsFragment::class.java.simpleName

class Ev3SensorsSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.ev3_preferences)
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

        val ev3SwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED)!!
        val ev3ConnectionSettings =
            findPreference<PreferenceCategory>(EV3_SETTINGS_CATEGORY)!!

        ev3ConnectionSettings.isEnabled = ev3SwitchPreference.isChecked
        ev3SwitchPreference.setOnPreferenceChangeListener { _, isChecked ->
            ev3ConnectionSettings.isEnabled = (isChecked as Boolean?)!!
            true
        }

        for (sensorPreference in EV3_SENSORS) {
            findPreference<ListPreference>(sensorPreference)?.apply {
                setEntries(R.array.ev3_sensor_chooser)
                entryValues = EV3Sensor.Sensor.getSensorCodes()
            }
        }
    }
}
