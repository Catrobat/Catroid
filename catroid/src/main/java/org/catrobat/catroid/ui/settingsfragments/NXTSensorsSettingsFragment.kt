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

import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.setToChosenLanguage
import org.catrobat.catroid.ui.settingsfragments.LegoSensors.NXT_SENSORS
import androidx.preference.PreferenceFragmentCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import org.catrobat.catroid.R
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.NXT_SETTINGS_CATEGORY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED

class NXTSensorsSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.nxt_preferences)
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

        val nxtSwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED)!!
        val nxtConnectionSettings =
            findPreference<PreferenceCategory>(NXT_SETTINGS_CATEGORY)!!

        nxtConnectionSettings.isEnabled = nxtSwitchPreference.isChecked
        nxtSwitchPreference.setOnPreferenceChangeListener { _, isChecked ->
            nxtConnectionSettings.isEnabled = (isChecked as Boolean?)!!
            true
        }

        for (sensorPreference in NXT_SENSORS) {
            findPreference<ListPreference>(sensorPreference)?.apply {
                setEntries(R.array.nxt_sensor_chooser)
                entryValues = NXTSensor.Sensor.getSensorCodes()
            }
        }
    }

    companion object {
        val NXT_SENSORS_SETTINGS_FRAGMENT_TAG: String = NXTSensorsSettingsFragment::class.java.simpleName
    }
}
