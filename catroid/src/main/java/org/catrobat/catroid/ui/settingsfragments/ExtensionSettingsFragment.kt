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
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.settingsfragments.AISettingsFragment.Companion.AI_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.EV3SensorsSettingsFragment.Companion.EV3_SENSORS_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.NXTSensorsSettingsFragment.Companion.NXT_SENSOR_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.ParrotARDroneSettingsFragment.Companion.PARROT_AR_DRONE_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.RaspberryPiSettingsFragment.Companion.RASPBERRY_PI_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.AI_SENSORS_SCREEN_KEY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_SCREEN_KEY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.EV3_SCREEN_KEY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.NXT_SCREEN_KEY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.RASPBERRY_SCREEN_KEY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_CAST_GLOBALLY_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MULTIPLAYER_VARIABLES_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_ARDUINO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_EMBROIDERY_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_JUMPING_SUMO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_NFC_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_PHIRO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_TEST_BRICKS

class ExtensionSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsFragment.setToChosenLanguage(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.extensions_preferences)

        addAIPreferenceClickListener()
        addNXTPreferenceClickListener()
        addEV3PreferenceClickListener()
        addParrotArDronePreferenceClickListener()
        addRaspberryPiPreferenceClickListener()
        setArduinoPreferenceVisibility()
        setChromecastPreferenceVisibility()
        setEmbroideryPreferenceVisibility()
        setNfcPreferenceVisibility()
        setJumpingSumoPreferenceVisibility()
        setPhiroPreferenceVisibility()
        setMultiplayerPreferenceVisibility()
        setTestingPreferenceVisibility()
    }

    override fun onResume() {
        super.onResume()
        requireActivity()
            .takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.setTitle(R.string.preference_title_extension)
            }
    }

    private fun addAIPreferenceClickListener() {
        val aiPreferenceScreen = findPreference<PreferenceScreen>(AI_SENSORS_SCREEN_KEY)
        aiPreferenceScreen?.setOnPreferenceClickListener { _ ->
            navigateToFragment(AISettingsFragment(), AI_SETTINGS_FRAGMENT_TAG)
            true
        }
        aiPreferenceScreen?.isVisible = BuildConfig.FEATURE_AI_SENSORS_ENABLED
    }

    private fun addNXTPreferenceClickListener() {
        val nxtPreferenceScreen = findPreference<PreferenceScreen>(NXT_SCREEN_KEY)
        nxtPreferenceScreen?.setOnPreferenceClickListener { _ ->
            navigateToFragment(NXTSensorsSettingsFragment(), NXT_SENSOR_SETTINGS_FRAGMENT_TAG)
            true
        }
        nxtPreferenceScreen?.isVisible = BuildConfig.FEATURE_LEGO_NXT_ENABLED
    }

    private fun addEV3PreferenceClickListener() {
        val ev3PreferenceScreen = findPreference<PreferenceScreen>(EV3_SCREEN_KEY)
        ev3PreferenceScreen?.setOnPreferenceClickListener { _ ->
            navigateToFragment(EV3SensorsSettingsFragment(), EV3_SENSORS_SETTINGS_FRAGMENT_TAG)
            true
        }
        ev3PreferenceScreen?.isVisible = BuildConfig.FEATURE_LEGO_EV3_ENABLED
    }

    private fun addParrotArDronePreferenceClickListener() {
        val parrotArDronePreferenceScreen = findPreference<PreferenceScreen>(DRONE_SCREEN_KEY)
        parrotArDronePreferenceScreen?.setOnPreferenceClickListener { _ ->
            navigateToFragment(ParrotARDroneSettingsFragment(), PARROT_AR_DRONE_SETTINGS_FRAGMENT_TAG)
            true
        }
        parrotArDronePreferenceScreen?.isVisible = BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED
    }

    private fun addRaspberryPiPreferenceClickListener() {
        val raspberryPiPreferenceScreen = findPreference<PreferenceScreen>(RASPBERRY_SCREEN_KEY)
        raspberryPiPreferenceScreen?.setOnPreferenceClickListener { _ ->
            navigateToFragment(RaspberryPiSettingsFragment(), RASPBERRY_PI_SETTINGS_FRAGMENT_TAG)
            true
        }
        raspberryPiPreferenceScreen?.isVisible = BuildConfig.FEATURE_RASPI_ENABLED
    }

    private fun setArduinoPreferenceVisibility() {
        val arduinoSwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_SHOW_ARDUINO_BRICKS)
        arduinoSwitchPreference?.isVisible = BuildConfig.FEATURE_ARDUINO_ENABLED
    }

    private fun setChromecastPreferenceVisibility() {
        val chromecastSwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_CAST_GLOBALLY_ENABLED)
        chromecastSwitchPreference?.isVisible = BuildConfig.FEATURE_CAST_ENABLED
    }
    private fun setEmbroideryPreferenceVisibility() {
        val embroiderySwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_SHOW_EMBROIDERY_BRICKS)
        embroiderySwitchPreference?.isVisible = BuildConfig.FEATURE_EMBROIDERY_ENABLED
    }

    private fun setNfcPreferenceVisibility() {
        val nfcSwitchPreference = findPreference<SwitchPreference>(SETTINGS_SHOW_NFC_BRICKS)
        nfcSwitchPreference?.isVisible = BuildConfig.FEATURE_NFC_ENABLED
    }

    private fun setJumpingSumoPreferenceVisibility() {
        val jumpingSumoSwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_SHOW_JUMPING_SUMO_BRICKS)
        jumpingSumoSwitchPreference?.isVisible = BuildConfig.FEATURE_PARROT_JUMPING_SUMO_ENABLED
    }

    private fun setPhiroPreferenceVisibility() {
        val phiroSwitchPreference = findPreference<SwitchPreference>(SETTINGS_SHOW_PHIRO_BRICKS)
        phiroSwitchPreference?.isVisible = BuildConfig.FEATURE_PHIRO_ENABLED
    }

    private fun setMultiplayerPreferenceVisibility() {
        val multiplayerSwitchPreference =
            findPreference<SwitchPreference>(SETTINGS_MULTIPLAYER_VARIABLES_ENABLED)
        multiplayerSwitchPreference?.isVisible = BuildConfig.FEATURE_MULTIPLAYER_VARIABLES_ENABLED
    }

    private fun setTestingPreferenceVisibility() {
        val testingSwitchPreference = findPreference<SwitchPreference>(SETTINGS_SHOW_TEST_BRICKS)
        testingSwitchPreference?.isVisible = BuildConfig.FEATURE_TESTBRICK_ENABLED
    }

    private fun navigateToFragment(preference: PreferenceFragmentCompat, tag: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame, preference, tag)
            .addToBackStack(tag)
            .commit()
    }

    companion object {
        val EXTENSION_SETTINGS_FRAGMENT_TAG: String = ExtensionSettingsFragment::class.java.simpleName
    }
}
