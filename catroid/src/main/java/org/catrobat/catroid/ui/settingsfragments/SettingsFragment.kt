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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE
import org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAGS
import org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment.Companion.ACCESSIBILITY_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.ExtensionSettingsFragment.Companion.EXTENSION_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.LegoSensors.EV3_SENSORS
import org.catrobat.catroid.ui.settingsfragments.LegoSensors.NXT_SENSORS
import org.catrobat.catroid.ui.settingsfragments.WebAccessSettingsFragment.Companion.WEB_ACCESS_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.utils.SnackbarUtil
import org.koin.java.KoinJavaComponent
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToChosenLanguage(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        addAccessibilityPreferenceClickListener()
        addExtensionPreferenceClickListener()
        addHintPreferenceChangeListener()
        addTrustedDomainPreferenceClickListener()
        addLanguagesToListPreference()
        addLanguageListPreferenceChangeListener()
    }

    override fun onResume() {
        super.onResume()
        requireActivity()
            .takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.setTitle(R.string.preference_title)
            }
    }

    private fun navigateToFragment(preference: PreferenceFragmentCompat, tag: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame, preference, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun addAccessibilityPreferenceClickListener() {
        val accessibilityPreferenceScreen =
            findPreference<PreferenceScreen>(ACCESSIBILITY_SCREEN_KEY)

        accessibilityPreferenceScreen?.setOnPreferenceClickListener { _ ->
            navigateToFragment(AccessibilitySettingsFragment(), ACCESSIBILITY_SETTINGS_FRAGMENT_TAG)
            true
        }
    }

    private fun addLanguageListPreferenceChangeListener() {
        val listPreference = findPreference<ListPreference>(SETTINGS_MULTILINGUAL)
        listPreference?.setOnPreferenceChangeListener { _, languageTag ->
            val selectedLanguage = languageTag.toString()
            setLanguageSharedPreference(requireContext(), selectedLanguage)
            startActivity(Intent(requireContext(), MainMenuActivity::class.java))
            requireActivity().finishAffinity()
            true
        }
    }

    private fun addLanguagesToListPreference() {
        val languagesNames: MutableList<String> = ArrayList()
        for (languageTag in LANGUAGE_TAGS) {
            if (DEVICE_LANGUAGE == languageTag) {
                languagesNames.add(resources.getString(R.string.device_language))
            } else {
                val mLocale = Locale.forLanguageTag(languageTag)
                languagesNames.add(mLocale.getDisplayName(mLocale))
            }
        }

        val listPreference = findPreference<ListPreference>(SETTINGS_MULTILINGUAL)
        listPreference?.apply {
            entries = languagesNames.toTypedArray()
            entryValues = LANGUAGE_TAGS
        }
    }

    private fun addExtensionPreferenceClickListener() {
        val extensionPreferences = findPreference<PreferenceScreen>(SETTINGS_MANAGE_EXTENSION)
        extensionPreferences?.setOnPreferenceClickListener { _ ->
            navigateToFragment(ExtensionSettingsFragment(), EXTENSION_SETTINGS_FRAGMENT_TAG)
            true
        }
    }

    private fun addTrustedDomainPreferenceClickListener() {
        val trustedDomainPreference =
            findPreference<PreferenceScreen>(SETTINGS_EDIT_TRUSTED_DOMAINS)
        trustedDomainPreference?.setOnPreferenceClickListener { _ ->
            navigateToFragment(WebAccessSettingsFragment(), WEB_ACCESS_SETTINGS_FRAGMENT_TAG)
            true
        }
    }

    private fun addHintPreferenceChangeListener() {
        val hintSwitchPreference = findPreference<SwitchPreference>(SETTINGS_SHOW_HINTS)
        hintSwitchPreference?.setOnPreferenceChangeListener { preference, _ ->
            preference.preferenceManager.sharedPreferences
                .edit()
                .remove(SnackbarUtil.SHOWN_HINT_LIST)
                .apply()
            true
        }
    }

    companion object {

        const val LEGO_SENSORS_ARRAY_LENGTH = 4

        const val AI_SENSORS_SCREEN_KEY = "settings_ai_screen"
        const val ACCESSIBILITY_SCREEN_KEY = "setting_accessibility_screen"
        const val NXT_SCREEN_KEY = "setting_nxt_screen"
        const val NXT_SETTINGS_CATEGORY = "setting_nxt_category"
        const val EV3_SCREEN_KEY = "setting_ev3_screen"
        const val EV3_SETTINGS_CATEGORY = "setting_ev3_category"
        const val DRONE_SCREEN_KEY = "settings_drone_screen"
        const val RASPBERRY_SCREEN_KEY = "settings_raspberry_screen"

        const val SETTINGS_EDIT_TRUSTED_DOMAINS = "setting_trusted_domains"
        const val SETTINGS_MANAGE_EXTENSION = "setting_manage_extensions"

        const val SETTINGS_MULTILINGUAL = "setting_multilingual"

        const val SETTINGS_USE_CATBLOCKS = "settings_use_catblocks"

        const val SETTINGS_MULTIPLAYER_VARIABLES_ENABLED = "setting_multiplayer_variables_enabled"
        const val SETTINGS_CAST_GLOBALLY_ENABLED = "setting_cast_globally_enabled"
        const val SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED = "settings_mindstorms_nxt_bricks_enabled"
        const val SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED = "settings_mindstorms_ev3_bricks_enabled"
        const val SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED =
            "settings_mindstorms_nxt_show_sensor_info_box_disabled"
        const val SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED =
            "settings_mindstorms_ev3_show_sensor_info_box_disabled"

        const val SETTINGS_SHOW_HINTS = "setting_enable_hints"
        const val SETTINGS_SHOW_EMBROIDERY_BRICKS = "setting_embroidery_bricks"
        const val SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS = "setting_parrot_ar_drone_bricks"
        const val SETTINGS_SHOW_JUMPING_SUMO_BRICKS = "setting_parrot_jumping_sumo_bricks"
        const val SETTINGS_SHOW_ARDUINO_BRICKS = "setting_arduino_bricks"
        const val SETTINGS_SHOW_PHIRO_BRICKS = "setting_enable_phiro_bricks"
        const val SETTINGS_SHOW_NFC_BRICKS = "setting_nfc_bricks"
        const val SETTINGS_SHOW_RASPI_BRICKS = "setting_raspi_bricks"
        const val SETTINGS_SHOW_TEST_BRICKS = "setting_test_bricks"

        const val SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS = "setting_ai_face_detection"
        const val SETTINGS_SHOW_AI_OBJECT_DETECTION_SENSORS = "setting_ai_object_detection"
        const val SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS = "setting_ai_pose_detection"
        const val SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS = "setting_ai_text_recognition"
        const val SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS =
            "setting_ai_speech_synthetization"
        const val SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS = "setting_ai_speech_recognition"

        const val RASPI_CONNECTION_SETTINGS_CATEGORY = "setting_raspi_connection_settings_category"
        const val RASPI_HOST = "setting_raspi_host_preference"
        const val RASPI_PORT = "setting_raspi_port_preference"
        const val RASPI_VERSION_SPINNER = "setting_raspi_version_preference"

        const val DRONE_SETTINGS_CATEGORY = "setting_drone_category"
        const val DRONE_CONFIGS = "setting_drone_basic_configs"
        const val DRONE_ALTITUDE_LIMIT = "setting_drone_altitude_limit"
        const val DRONE_VERTICAL_SPEED = "setting_drone_vertical_speed"
        const val DRONE_ROTATION_SPEED = "setting_drone_rotation_speed"
        const val DRONE_TILT_ANGLE = "setting_drone_tilt_angle"

        const val SETTINGS_CRASH_REPORTS = "setting_enable_crash_reports"

        @JvmStatic
        fun setToChosenLanguage(activity: Activity) {
            val sharedPreferences = getSharedPreferences(activity.applicationContext)
            val languageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, "")
            val locale = if (languageTag == DEVICE_LANGUAGE) {
                Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
            } else {
                if (LANGUAGE_TAGS.contains(languageTag)) {
                    Locale.forLanguageTag(languageTag)
                } else {
                    Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
                }
            }
            Locale.setDefault(locale)
            updateLocale(activity, locale)
            updateLocale(activity.applicationContext, locale)
            SensorHandler.setUserLocaleTag(locale.toLanguageTag())
        }

        @JvmStatic
        fun updateLocale(context: Context, locale: Locale?) {
            val resources = context.resources
            val displayMetrics = resources.displayMetrics
            val configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, displayMetrics)
        }

        @JvmStatic
        fun isCastSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_CAST_GLOBALLY_ENABLED, false)

        @JvmStatic
        fun isAIFaceDetectionSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS, false)

        @JvmStatic
        fun isAIObjectDetectionSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_AI_OBJECT_DETECTION_SENSORS, false)

        @JvmStatic
        fun isAIPoseDetectionSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS, false)

        @JvmStatic
        fun isAITextRecognitionSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS, false)

        @JvmStatic
        fun isAISpeechSynthetizationSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(
                context,
                SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                false
            )

        @JvmStatic
        fun isAISpeechRecognitionSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS, false)

        @JvmStatic
        fun isEmbroiderySharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_EMBROIDERY_BRICKS, false)

        @JvmStatic
        fun isMindstormsNXTSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)

        @JvmStatic
        fun isMindstormsEV3SharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, false)

        @JvmStatic
        fun isDroneSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, false)

        @JvmStatic
        fun isJSSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_JUMPING_SUMO_BRICKS, false)

        @JvmStatic
        fun isArduinoSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_ARDUINO_BRICKS, false)

        @JvmStatic
        fun isPhiroSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_PHIRO_BRICKS, false)

        @JvmStatic
        fun isTestSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_TEST_BRICKS, BuildConfig.DEBUG)

        @JvmStatic
        fun isNfcSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_NFC_BRICKS, false)

        @JvmStatic
        fun isRaspiSharedPreferenceEnabled(context: Context): Boolean =
            getBooleanSharedPreference(context, SETTINGS_SHOW_RASPI_BRICKS, false)

        @JvmStatic
        fun isMultiplayerVariablesPreferenceEnabled(context: Context): Boolean {
            val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)
            return getBooleanSharedPreference(
                context,
                SETTINGS_MULTIPLAYER_VARIABLES_ENABLED,
                false
            ) ||
                projectManager.currentProject.hasMultiplayerVariables()
        }

        @JvmStatic
        fun getLegoNXTSensorMapping(context: Context): Array<NXTSensor.Sensor?> {
            val sensorMapping = arrayOfNulls<NXTSensor.Sensor>(LEGO_SENSORS_ARRAY_LENGTH)
            for (i in 0 until LEGO_SENSORS_ARRAY_LENGTH) {
                val sensor = getSharedPreferences(context).getString(NXT_SENSORS[i], null)
                sensorMapping[i] = NXTSensor.Sensor.getSensorFromSensorCode(sensor)
            }
            return sensorMapping
        }

        @JvmStatic
        fun getLegoEV3SensorMapping(context: Context): Array<EV3Sensor.Sensor?> {
            val sensorMapping = arrayOfNulls<EV3Sensor.Sensor>(LEGO_SENSORS_ARRAY_LENGTH)
            for (i in 0 until LEGO_SENSORS_ARRAY_LENGTH) {
                val sensor = getSharedPreferences(context).getString(EV3_SENSORS[i], null)
                sensorMapping[i] = EV3Sensor.Sensor.getSensorFromSensorCode(sensor)
            }
            return sensorMapping
        }

        @JvmStatic
        fun getRaspiHost(context: Context): String? =
            getSharedPreferences(context).getString(RASPI_HOST, null)

        @JvmStatic
        fun getRaspiPort(context: Context): Int =
            getSharedPreferences(context).getString(RASPI_PORT, null)?.toInt() ?: 0

        @JvmStatic
        fun getRaspiRevision(context: Context): String? =
            getSharedPreferences(context).getString(RASPI_VERSION_SPINNER, null)

        @JvmStatic
        fun useCatBlocks(context: Context) =
            getBooleanSharedPreference(context, SETTINGS_USE_CATBLOCKS, false)

        @JvmStatic
        fun setUseCatBlocks(context: Context?, useCatBlocks: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_USE_CATBLOCKS, useCatBlocks)
                .apply()
        }

        @JvmStatic
        fun setMultiplayerVariablesPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_MULTIPLAYER_VARIABLES_ENABLED, value)
                .apply()
        }

        @JvmStatic
        fun enableLegoMindstormsEV3Bricks(context: Context) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, true)
                .apply()
        }

        @JvmStatic
        fun setLegoMindstormsEV3SensorMapping(
            context: Context,
            sensorMapping: Array<EV3Sensor.Sensor>
        ) {
            val editor = getSharedPreferences(context).edit()
            for (i in 0 until LEGO_SENSORS_ARRAY_LENGTH) {
                editor.putString(NXT_SENSORS[i], sensorMapping[i].sensorCode)
            }
            editor.apply()
        }

        @JvmStatic
        fun setLegoMindstormsEV3Sensors(
            context: Context,
            sensor: EV3Sensor.Sensor,
            sensorSetting: String
        ) {
            getSharedPreferences(context)
                .edit()
                .putString(sensorSetting, sensor.sensorCode)
                .apply()
        }

        @JvmStatic
        fun enableLegoMindstormsNXTBricks(context: Context) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true)
                .apply()
        }

        @JvmStatic
        fun setLegoMindstormsNXTSensorMapping(
            context: Context,
            sensorMapping: Array<NXTSensor.Sensor>
        ) {
            val editor = getSharedPreferences(context).edit()
            for (i in 0 until LEGO_SENSORS_ARRAY_LENGTH) {
                editor.putString(NXT_SENSORS[i], sensorMapping[i].sensorCode)
            }
            editor.apply()
        }

        @JvmStatic
        fun setLegoMindstormsNXTSensors(
            context: Context,
            sensor: NXTSensor.Sensor,
            sensorSetting: String
        ) {
            getSharedPreferences(context)
                .edit()
                .putString(sensorSetting, sensor.sensorCode)
                .apply()
        }

        @JvmStatic
        fun setPhiroSharedPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_PHIRO_BRICKS, value)
                .apply()
        }

        @JvmStatic
        fun setArduinoSharedPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_ARDUINO_BRICKS, value)
                .apply()
        }

        @JvmStatic
        fun setAISpeechRecognitionPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS, value)
                .apply()
        }

        @JvmStatic
        fun setAIFaceDetectionPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS, value)
                .apply()
        }

        @JvmStatic
        fun setAIPoseDetectionPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS, value)
                .apply()
        }

        @JvmStatic
        fun setAISpeechSynthetizationPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS, value)
                .apply()
        }

        @JvmStatic
        fun setAITextRecognitionPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS, value)
                .apply()
        }

        @JvmStatic
        fun setAIObjectDetectionPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context)
                .edit()
                .putBoolean(SETTINGS_SHOW_AI_OBJECT_DETECTION_SENSORS, value)
                .apply()
        }

        @JvmStatic
        fun setLanguageSharedPreference(context: Context, language: String) {
            getSharedPreferences(context)
                .edit()
                .putString(LANGUAGE_TAG_KEY, language)
                .apply()
        }

        @JvmStatic
        fun removeLanguageSharedPreference(context: Context) {
            getSharedPreferences(context)
                .edit()
                .remove(LANGUAGE_TAG_KEY)
                .apply()
        }

        private fun getSharedPreferences(context: Context?) =
            PreferenceManager.getDefaultSharedPreferences(context)

        private fun getBooleanSharedPreference(
            context: Context,
            setting: String,
            default: Boolean
        ) =
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean(setting, default)
    }
}
