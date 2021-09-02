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

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.setToChosenLanguage
import org.catrobat.catroid.utils.ToastUtil

private const val ACCESSIBILITY_PROFILES_SCREEN_KEY = "setting_accessibility_profile_screen"
const val CUSTOM_PROFILE = "accessibility_profile_is_custom"
val ACCESSIBILITY_SETTINGS_FRAGMENT_TAG = AccessibilitySettingsFragment::class.java.simpleName
class AccessibilitySettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    private var preferenceChanged = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.accessibility_preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToChosenLanguage(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        requireActivity()
            .takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.title = preferenceScreen.title
            }
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDetach() {
        super.onDetach()
        if (preferenceChanged) {
            startActivity(Intent(requireContext(), MainMenuActivity::class.java))
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
            ToastUtil.showSuccess(requireContext(), getString(R.string.accessibility_settings_applied))
            requireActivity().finishAffinity()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        preferenceChanged = true
        sharedPreferences.edit()
            .putBoolean(CUSTOM_PROFILE, true)
            .apply()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key
        if (ACCESSIBILITY_PROFILES_SCREEN_KEY == key) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.content_frame, AccessibilityProfilesFragment(),
                    AccessibilityProfilesFragment.TAG
                )
                .addToBackStack(AccessibilityProfilesFragment.TAG)
                .commit()
        }
        return super.onPreferenceTreeClick(preference)
    }
}
