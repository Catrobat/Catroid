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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DroneConfigPreference
import org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE
import org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAGS
import org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.settingsfragments.webaccess.WEB_ACCESS_SETTINGS_FRAGMENT_TAG
import org.catrobat.catroid.ui.settingsfragments.webaccess.WebAccessSettingsFragment
import org.catrobat.catroid.utils.SnackbarUtil.SHOWN_HINT_LIST
import java.util.Locale

const val SETTINGS_MULTILINGUAL = "setting_multilingual"
const val SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED = "settings_mindstorms_nxt_bricks_enabled"
const val SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED =
    "settings_mindstorms_nxt_show_sensor_info_box_disabled"
const val SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED = "settings_mindstorms_ev3_bricks_enabled"
const val SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED =
    "settings_mindstorms_ev3_show_sensor_info_box_disabled"
const val SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS = "setting_parrot_ar_drone_bricks"
const val SETTINGS_EDIT_TRUSTED_DOMAINS = "setting_trusted_domains"
const val SETTINGS_SHOW_JUMPING_SUMO_BRICKS = "setting_parrot_jumping_sumo_bricks"
const val SETTINGS_SHOW_EMBROIDERY_BRICKS = "setting_embroidery_bricks"
const val SETTINGS_SHOW_PHIRO_BRICKS = "setting_enable_phiro_bricks"
const val SETTINGS_SHOW_ARDUINO_BRICKS = "setting_arduino_bricks"
const val SETTINGS_SHOW_RASPI_BRICKS = "setting_raspi_bricks"
const val SETTINGS_SHOW_NFC_BRICKS = "setting_nfc_bricks"
const val SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY =
    "setting_parrot_ar_drone_catrobat_terms_of_service_accepted_permanently"
const val SETTINGS_CAST_GLOBALLY_ENABLED = "setting_cast_globally_enabled"
const val SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS = "setting_ai_speech_recognition"
const val SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS = "setting_ai_speech_synthetization"
const val SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS = "setting_ai_face_detection"
const val SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS = "setting_ai_pose_detection"
const val SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS = "setting_ai_text_recognition"

const val SETTINGS_MULTIPLAYER_VARIABLES_ENABLED = "setting_multiplayer_variables_enabled"
const val SETTINGS_SHOW_HINTS = "setting_enable_hints"
const val SETTINGS_PARROT_JUMPING_SUMO_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY =
    "setting_parrot_jumping_sumo_catrobat_terms_of_service_accepted_permanently"
const val SETTINGS_TEST_BRICKS = "setting_test_bricks"
const val AI_SENSORS_SCREEN_KEY = "setting_ai_screen"
const val ACCESSIBILITY_SCREEN_KEY = "setting_accessibility_screen"
const val NXT_SCREEN_KEY = "setting_nxt_screen"
const val EV3_SCREEN_KEY = "setting_ev3_screen"
const val DRONE_SCREEN_KEY = "settings_drone_screen"
const val RASPBERRY_SCREEN_KEY = "settings_raspberry_screen"

const val NXT_SETTINGS_CATEGORY = "setting_nxt_category"
const val EV3_SETTINGS_CATEGORY = "setting_ev3_category"

const val DRONE_SETTINGS_CATEGORY = "setting_drone_category"
const val DRONE_CONFIGS = "setting_drone_basic_configs"
const val DRONE_ALTITUDE_LIMIT = "setting_drone_altitude_limit"
const val DRONE_VERTICAL_SPEED = "setting_drone_vertical_speed"
const val DRONE_ROTATION_SPEED = "setting_drone_rotation_speed"
const val DRONE_TILT_ANGLE = "setting_drone_tilt_angle"

const val RASPI_CONNECTION_SETTINGS_CATEGORY = "setting_raspi_connection_settings_category"
const val RASPI_HOST = "setting_raspi_host_preference"
const val RASPI_PORT = "setting_raspi_port_preference"
const val RASPI_VERSION_SPINNER = "setting_raspi_version_preference"

const val SETTINGS_CRASH_REPORTS = "setting_enable_crash_reports"
const val SETTINGS_USE_CATBLOCKS = "settings_use_catblocks"

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToChosenLanguage(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setHintPreferences()

        setupLanguageListPreference()
        setupAiPreference()
        setupTrustedDomainPreference()
        setupEmbroideryPreference()
        setUpMultiplayerPreference()
        setupPhiroPreference()
        setupJumpingSumoPreference()
        setupArduinoPreference()
        setupRaspberryPiPreference()
        setupNfcPreference()
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

    private fun navigatesTo(preference: PreferenceFragmentCompat, tag: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame, preference, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun setHintPreferences() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_HINTS)?.apply {
            setOnPreferenceChangeListener { preference, _ ->
                preference.preferenceManager.sharedPreferences.edit {
                    remove(SHOWN_HINT_LIST)
                }
                true
            }
        }
    }

    private fun setupLanguageListPreference() {
        findPreference<ListPreference>(SETTINGS_MULTILINGUAL)?.apply {
            entries = getLanguages().toTypedArray()
            entryValues = LANGUAGE_TAGS
            setOnPreferenceChangeListener { _, languageTag ->
                val selectedLanguageTag = languageTag.toString()
                setLanguageSharedPreference(
                    requireContext(),
                    selectedLanguageTag
                )
                startActivity(Intent(requireContext(), MainMenuActivity::class.java))
                requireActivity().finishAffinity()
                true
            }
        }
    }

    private fun getLanguages(): Set<String> {
        val languagesNameSet = mutableSetOf<String>()

        for (languageTag in LANGUAGE_TAGS) {
            if (DEVICE_LANGUAGE == languageTag) {
                languagesNameSet.add(resources.getString(R.string.device_language))
            } else {
                val mLocale = Locale.forLanguageTag(languageTag)
                languagesNameSet.add(mLocale.getDisplayName(mLocale))
            }
        }
        return languagesNameSet
    }

    private fun setupTrustedDomainPreference() {
        findPreference<Preference>(SETTINGS_EDIT_TRUSTED_DOMAINS)
            ?.setOnPreferenceClickListener {
                navigatesTo(WebAccessSettingsFragment(), WEB_ACCESS_SETTINGS_FRAGMENT_TAG)
                true
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

    private fun setupPhiroPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_PHIRO_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_PHIRO_ENABLED) {
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

    private fun setupRaspberryPiPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_RASPI_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_RASPI_ENABLED) {
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

    private fun setupNfcPreference() {
        findPreference<SwitchPreference>(SETTINGS_SHOW_NFC_BRICKS)?.apply {
            if (!BuildConfig.FEATURE_NFC_ENABLED) {
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

    override fun onResume() {
        super.onResume()
        requireActivity().takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.setTitle(R.string.preference_title)
            }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference!!.key) {
            /*ACCESSIBILITY_SCREEN_KEY -> parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.content_frame,
                    AccessibilitySettingsFragment(),
                    AccessibilitySettingsFragment.TAG
                )
                .addToBackStack(AccessibilitySettingsFragment.TAG)
                .commit()

            NXT_SCREEN_KEY -> parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.content_frame,
                    NXTSensorsSettingsFragment(),
                    NXTSensorsSettingsFragment.TAG
                )
                .addToBackStack(NXTSensorsSettingsFragment.TAG)
                .commit()

            EV3_SCREEN_KEY -> parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.content_frame,
                    Ev3SensorsSettingsFragment(),
                    Ev3SensorsSettingsFragment.TAG
                )
                .addToBackStack(Ev3SensorsSettingsFragment.TAG)
                .commit()
            DRONE_SCREEN_KEY -> parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.content_frame,
                    ParrotARDroneSettingsFragment(),
                    ParrotARDroneSettingsFragment.TAG
                )
                .addToBackStack(ParrotARDroneSettingsFragment.TAG)
                .commit()

            RASPBERRY_SCREEN_KEY -> parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.content_frame,
                    RaspberryPiSettingsFragment(),
                    RaspberryPiSettingsFragment.TAG
                )
                .addToBackStack(RaspberryPiSettingsFragment.TAG)
                .commit()*/
        }
        return super.onPreferenceTreeClick(preference)
    }

    companion object {
        val TAG = SettingsFragment::class.java.simpleName

        @JvmStatic
        val NXT_SENSORS = arrayOf(
            "setting_mindstorms_nxt_sensor_1",
            "setting_mindstorms_nxt_sensor_2",
            "setting_mindstorms_nxt_sensor_3",
            "setting_mindstorms_nxt_sensor_4"
        )

        @JvmStatic
        val EV3_SENSORS = arrayOf(
            "setting_mindstorms_ev3_sensor_1",
            "setting_mindstorms_ev3_sensor_2",
            "setting_mindstorms_ev3_sensor_3",
            "setting_mindstorms_ev3_sensor_4"
        )

        @JvmStatic
        fun isEmbroiderySharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_EMBROIDERY_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isDroneSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isJSSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_JUMPING_SUMO_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isMindstormsNXTSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
                context
            )
        }

        @JvmStatic
        fun isMindstormsEV3SharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED,
                context
            )
        }

        @JvmStatic
        fun isPhiroSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_PHIRO_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isCastSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_CAST_GLOBALLY_ENABLED,
                context
            )
        }

        @JvmStatic
        fun setPhiroSharedPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_PHIRO_BRICKS, newValue)
            }
        }

        @JvmStatic
        fun setJumpingSumoSharedPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_JUMPING_SUMO_BRICKS, newValue)
            }
        }

        @JvmStatic
        fun setArduinoSharedPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_ARDUINO_BRICKS, newValue)
            }
        }

        @JvmStatic
        fun setRaspiSharedPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_RASPI_BRICKS, newValue)
            }
        }

        @JvmStatic
        fun isAISpeechRecognitionSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                context
            )
        }

        @JvmStatic
        fun setAISpeechRecognitionPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS, newValue)
            }
        }

        @JvmStatic
        fun isAIFaceDetectionSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                context
            )
        }

        @JvmStatic
        fun setAIFaceDetectionPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS, newValue)
            }
        }

        @JvmStatic
        fun isAIPoseDetectionSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false, SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
                context
            )
        }

        @JvmStatic
        fun setAIPoseDetectionPreferenceEnabled(context: Context, value: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS, value)
            }
        }

        @JvmStatic
        fun isAISpeechSynthetizationSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                context
            )
        }

        @JvmStatic
        fun setAISpeechSynthetizationPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS, newValue)
            }
        }

        @JvmStatic
        fun isAITextRecognitionSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                context
            )
        }

        @JvmStatic
        fun setAITextRecognitionPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS, newValue)
            }
        }

        @JvmStatic
        fun isArduinoSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_ARDUINO_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isRaspiSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_RASPI_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isNfcSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_SHOW_NFC_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isTestSharedPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                BuildConfig.DEBUG,
                SETTINGS_TEST_BRICKS,
                context
            )
        }

        @JvmStatic
        fun isMultiplayerVariablesPreferenceEnabled(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_MULTIPLAYER_VARIABLES_ENABLED,
                context
            )
        }

        @JvmStatic
        fun useCatBlocks(context: Context): Boolean {
            return getBooleanSharedPreference(
                false,
                SETTINGS_USE_CATBLOCKS,
                context
            )
        }

        @JvmStatic
        fun setUseCatBlocks(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_USE_CATBLOCKS, newValue)
            }
        }

        @JvmStatic
        fun setMultiplayerVariablesPreferenceEnabled(context: Context, newValue: Boolean) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_MULTIPLAYER_VARIABLES_ENABLED, newValue)
            }
        }

        @JvmStatic
        fun enableLegoMindstormsNXTBricks(context: Context) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true)
            }
        }

        @JvmStatic
        fun enableLegoMindstormsEV3Bricks(context: Context) {
            getSharedPreferences(context).edit {
                putBoolean(SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, true)
            }
        }

        private fun getSharedPreferences(context: Context) =
            PreferenceManager.getDefaultSharedPreferences(context)

        private fun getBooleanSharedPreference(
            defaultValue: Boolean,
            settingsString: String,
            context: Context
        ) = getSharedPreferences(context).getBoolean(settingsString, defaultValue)

        @JvmStatic
        fun setLanguageSharedPreference(context: Context, value: String) {
            getSharedPreferences(context).edit {
                putString(LANGUAGE_TAG_KEY, value)
            }
        }

        @JvmStatic
        fun removeLanguageSharedPreference(context: Context) {
            getSharedPreferences(context).edit {
                remove(LANGUAGE_TAG_KEY)
            }
        }

        @JvmStatic
        fun setToChosenLanguage(activity: Activity) {
            val sharedPreferences = getSharedPreferences(activity.applicationContext)
            val languageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, "")!!
            val mLocale = if (languageTag == DEVICE_LANGUAGE) {
                Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
            } else {
                if (listOf(*LANGUAGE_TAGS).contains(languageTag)) {
                    Locale.forLanguageTag(languageTag)
                } else {
                    Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
                }
            }
            Locale.setDefault(mLocale)
            updateLocale(activity, mLocale)
            updateLocale(activity.applicationContext, mLocale)
            SensorHandler.setUserLocaleTag(mLocale.toLanguageTag())
        }

        @JvmStatic
        fun updateLocale(context: Context, locale: Locale) {
            val configuration = context.resources.configuration
            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
        }

        @JvmStatic
        fun getLegoNXTSensorMapping(context: Context): Array<NXTSensor.Sensor?> {
            val sensorMapping = arrayOfNulls<NXTSensor.Sensor>(4)
            for (i in 0..3) {
                val sensor = getSharedPreferences(context).getString(NXT_SENSORS[i], null)
                sensorMapping[i] = NXTSensor.Sensor.getSensorFromSensorCode(sensor)
            }
            return sensorMapping
        }

        @JvmStatic
        fun getLegoEV3SensorMapping(context: Context): Array<EV3Sensor.Sensor?> {
            val sensorMapping = arrayOfNulls<EV3Sensor.Sensor>(4)
            for (i in 0..3) {
                val sensor = getSharedPreferences(context).getString(EV3_SENSORS[i], null)
                sensorMapping[i] = EV3Sensor.Sensor.getSensorFromSensorCode(sensor)
            }
            return sensorMapping
        }

        @JvmStatic
        fun getRaspiHost(context: Context): String? {
            return getSharedPreferences(context)
                .getString(RASPI_HOST, null)
        }

        @JvmStatic
        fun getRaspiPort(context: Context): Int {
            return getSharedPreferences(context).getString(RASPI_PORT, null)?.toInt() ?: 0
        }

        @JvmStatic
        fun getRaspiRevision(context: Context): String? {
            return getSharedPreferences(context)
                .getString(RASPI_VERSION_SPINNER, null)
        }

        @JvmStatic
        fun setLegoMindstormsNXTSensorMapping(
            context: Context,
            sensorMapping: Array<NXTSensor.Sensor>
        ) {
            getSharedPreferences(context).edit {
                for (i in NXT_SENSORS.indices) {
                    putString(NXT_SENSORS[i], sensorMapping[i].sensorCode)
                }
            }
        }

        @JvmStatic
        fun setLegoMindstormsEV3SensorMapping(
            context: Context,
            sensorMapping: Array<EV3Sensor.Sensor>
        ) {
            getSharedPreferences(context).edit {
                for (i in EV3_SENSORS.indices) {
                    putString(EV3_SENSORS[i], sensorMapping[i].sensorCode)
                }
            }
        }

        @JvmStatic
        fun setLegoMindstormsNXTSensorMapping(
            context: Context,
            sensor: NXTSensor.Sensor,
            sensorSetting: String
        ) {
            getSharedPreferences(context).edit {
                putString(sensorSetting, sensor.sensorCode)
            }
        }

        @JvmStatic
        fun setLegoMindstormsEV3SensorMapping(
            context: Context,
            sensor: EV3Sensor.Sensor,
            sensorSetting: String
        ) {
            getSharedPreferences(context).edit {
                putString(sensorSetting, sensor.sensorCode)
            }
        }

        @JvmStatic
        fun getDronePreferenceMapping(context: Context): Array<DroneConfigPreference.Preferences?> {
            val dronePreferences = arrayOf(
                DRONE_CONFIGS,
                DRONE_ALTITUDE_LIMIT,
                DRONE_VERTICAL_SPEED,
                DRONE_ROTATION_SPEED,
                DRONE_TILT_ANGLE
            )
            val preferenceMapping = arrayOfNulls<DroneConfigPreference.Preferences>(5)
            for (i in 0..4) {
                val preference = getSharedPreferences(context).getString(dronePreferences[i], null)
                preferenceMapping[i] =
                    DroneConfigPreference.Preferences.getPreferenceFromPreferenceCode(preference)
            }

            return preferenceMapping
        }

        @JvmStatic
        fun getDronePreferenceMapping(
            context: Context,
            preferenceSetting: String
        ): DroneConfigPreference.Preferences {
            val preference = getSharedPreferences(context).getString(preferenceSetting, null)
            return DroneConfigPreference.Preferences.getPreferenceFromPreferenceCode(preference)
        }
    }
}
