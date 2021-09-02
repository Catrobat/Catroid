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

package org.catrobat.catroid.ui.settingsfragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import org.catrobat.catroid.R
import org.catrobat.catroid.BuildConfig

val MANAGE_EXTENSIONS_SETTINGS_FRAGMENT_TAG =
    ManageExtensionsSettingsFragment::class.java.simpleName

class ManageExtensionsSettingsFragment : PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        requireActivity()
            .takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.title = preferenceScreen.title
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsFragment.setToChosenLanguage(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.extensions_preferences)
        setupAiPreference()
        setupNxtPreference()
        setupEv3Preference()
        setupDronePreference()
        setupRaspberryPiPreference()

        setUpMultiplayerPreference()
        setupEmbroideryPreference()
        setupJumpingSumoPreference()
        setupArduinoPreference()
        setupNfcPreference()
        setupPhiroPreference()
        setupGlobalCastPreference()
        setupTestBricksPreference()
    }

    private fun setupAiPreference() {
        findPreference<PreferenceScreen>(AI_SENSORS_SCREEN_KEY)?.apply {
            isVisible = BuildConfig.FEATURE_AI_SENSORS_ENABLED
            setOnPreferenceClickListener {
                navigatesTo(AISettingsFragment(), AISettingsFragment.TAG)
                true
            }
        }
    }

    private fun setupEmbroideryPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_EMBROIDERY_BRICKS)?.apply {
            isVisible = BuildConfig.FEATURE_EMBROIDERY_ENABLED
        }
    }

    private fun setUpMultiplayerPreference() {
        findPreference<SwitchPreference>(SETTINGS_MULTIPLAYER_VARIABLES_ENABLED)?.apply {
            if (!BuildConfig.FEATURE_MULTIPLAYER_VARIABLES_ENABLED) {
                isEnabled = false
                preferenceScreen.removePreference(this)
            }
        }
    }

    private fun setupJumpingSumoPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_JUMPING_SUMO_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_PARROT_JUMPING_SUMO_ENABLED) {
                isEnabled = false
                preferenceScreen.removePreference(this)
            }
        }
    }

    private fun setupArduinoPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_ARDUINO_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_ARDUINO_ENABLED) {
                isEnabled = false
                preferenceScreen.removePreference(this)
            }
        }
    }

    private fun setupNfcPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_NFC_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_NFC_ENABLED) {
                isEnabled = false
                preferenceScreen.removePreference(this)
            }
        }
    }

    private fun setupPhiroPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_PHIRO_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_PHIRO_ENABLED) {
                isEnabled = false
                preferenceScreen.removePreference(this)
            }
        }
    }

    private fun setupGlobalCastPreference() {
        findPreference<SwitchPreference>(SETTINGS_CAST_GLOBALLY_ENABLED)?.apply {
            if (!BuildConfig.FEATURE_CAST_ENABLED) {
                isEnabled = false
                preferenceScreen.removePreference(this)
            }
        }
    }

    private fun setupTestBricksPreference() {
        findPreference<SwitchPreference>(SETTINGS_TEST_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_TESTBRICK_ENABLED) {
                isEnabled = BuildConfig.DEBUG
                preferenceScreen.removePreference(this)
            }
        }
    }

    private fun setupEv3Preference() {
        findPreference<PreferenceScreen>(EV3_SCREEN_KEY)?.apply {
            isVisible = BuildConfig.FEATURE_LEGO_EV3_ENABLED
            setOnPreferenceClickListener {
                navigatesTo(
                    Ev3SensorsSettingsFragment(),
                    EV3_SENSOR_SETTINGS_FRAGMENT_TAG
                )
                true
            }
        }
    }

    private fun setupNxtPreference() {
        findPreference<PreferenceScreen>(NXT_SCREEN_KEY)?.apply {
            isVisible = BuildConfig.FEATURE_LEGO_NXT_ENABLED
            setOnPreferenceClickListener {
                navigatesTo(
                    NXTSensorsSettingsFragment(),
                    NXT_SENSORS_SETTINGS_FRAGMENT_TAG
                )
                true
            }
        }
    }

    private fun setupRaspberryPiPreference() {
        findPreference<PreferenceScreen>(RASPBERRY_SCREEN_KEY)?.apply {
            isVisible = BuildConfig.FEATURE_RASPI_ENABLED
            setOnPreferenceClickListener {
                navigatesTo(
                    RaspberryPiSettingsFragment(),
                    RASPBERRY_PI_SETTINGS_FRAGMENT_TAG
                )
                true
            }
        }
    }

    private fun setupDronePreference() {
        findPreference<PreferenceScreen>(DRONE_SCREEN_KEY)?.apply {
            isVisible = BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED
            setOnPreferenceClickListener {
                navigatesTo(
                    ParrotARDroneSettingsFragment(),
                    PARROT_AR_DRONE_SETTINGS_FRAGMENT_TAG
                )
                true
            }
        }
    }

    private fun navigatesTo(preference: PreferenceFragmentCompat, tag: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame, preference, tag)
            .addToBackStack(tag)
            .commit()
    }
}
