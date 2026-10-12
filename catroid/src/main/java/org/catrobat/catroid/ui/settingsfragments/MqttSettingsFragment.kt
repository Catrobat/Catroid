/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import android.preference.CheckBoxPreference
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.recyclerview.repository.MqttPasswordRepository
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_CONNECTION_SETTINGS_CATEGORY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_CLIENT_ID
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_HOST
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_PASSWORD
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_PORT
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_USERNAME
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_MQTT_BRICKS
import org.koin.android.ext.android.inject

class MqttSettingsFragment : PreferenceFragment() {

    private val mqttPasswordRepository: MqttPasswordRepository by inject()

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = preferenceScreen.title
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        SettingsFragment.setToChosenLanguage(activity)
        addPreferencesFromResource(R.xml.mqtt_preferences)

        val mqttCheckBoxPreference = findPreference(SETTINGS_SHOW_MQTT_BRICKS) as CheckBoxPreference
        val mqttConnectionSettings = findPreference(MQTT_CONNECTION_SETTINGS_CATEGORY) as PreferenceCategory
        mqttConnectionSettings.isEnabled = mqttCheckBoxPreference.isChecked

        mqttCheckBoxPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, isChecked ->
            mqttConnectionSettings.isEnabled = isChecked as Boolean
            true
        }

        bindSummaryToValue(findPreference(MQTT_HOST) as EditTextPreference)
        bindSummaryToValue(findPreference(MQTT_USERNAME) as EditTextPreference)
        bindSummaryToValue(findPreference(MQTT_CLIENT_ID) as EditTextPreference)

        val portPreference = findPreference(MQTT_PORT) as EditTextPreference
        portPreference.summary = portPreference.text
        portPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            if (!isValidPort(newValue.toString())) {
                Toast.makeText(activity, R.string.preference_mqtt_port_invalid, Toast.LENGTH_SHORT).show()
                return@OnPreferenceChangeListener false
            }
            portPreference.summary = newValue.toString()
            true
        }

        val passwordPreference = findPreference(MQTT_PASSWORD) as EditTextPreference
        passwordPreference.isPersistent = false
        passwordPreference.text = mqttPasswordRepository.getPassword()
        passwordPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            mqttPasswordRepository.setPassword(newValue.toString())
            true
        }
    }

    private fun bindSummaryToValue(preference: EditTextPreference) {
        preference.summary = preference.text
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { pref, newValue ->
            (pref as EditTextPreference).summary = newValue.toString()
            true
        }
    }

    companion object {
        private const val MIN_PORT = 1
        private const val MAX_PORT = 65_535

        @JvmField val TAG: String = MqttSettingsFragment::class.java.simpleName

        @VisibleForTesting
        @JvmStatic
        fun isValidPort(value: String): Boolean {
            val port = value.toIntOrNull() ?: return false
            return port in MIN_PORT..MAX_PORT
        }
    }
}
