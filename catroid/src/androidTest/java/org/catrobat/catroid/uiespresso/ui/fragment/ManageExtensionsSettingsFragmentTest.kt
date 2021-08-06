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

package org.catrobat.catroid.uiespresso.ui.fragment

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DroneConfigPreference.Preferences.FIRST
import org.catrobat.catroid.common.DroneConfigPreference.Preferences.FOURTH
import org.catrobat.catroid.common.DroneConfigPreference.Preferences.SECOND
import org.catrobat.catroid.common.DroneConfigPreference.Preferences.THIRD
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.COLOR
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.COLOR_AMBIENT
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.COLOR_REFLECT
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.HT_NXT_COLOR
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.INFRARED
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.NXT_LIGHT
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.NXT_LIGHT_ACTIVE
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.NXT_SOUND
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.NXT_TEMPERATURE_C
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.NXT_TEMPERATURE_F
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor.Sensor.NXT_ULTRASONIC
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor.Sensor.LIGHT_ACTIVE
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor.Sensor.LIGHT_INACTIVE
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor.Sensor.SOUND
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor.Sensor.ULTRASONIC
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.ui.settingsfragments.LegoSensors.EV3_SENSORS
import org.catrobat.catroid.ui.settingsfragments.LegoSensors.NXT_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_ALTITUDE_LIMIT
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_CONFIGS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_ROTATION_SPEED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_TILT_ANGLE
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.DRONE_VERTICAL_SPEED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.RASPI_HOST
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.RASPI_PORT
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.RASPI_VERSION_SPINNER
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_CAST_GLOBALLY_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_MULTIPLAYER_VARIABLES_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_ARDUINO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_EMBROIDERY_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_JUMPING_SUMO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_NFC_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_PHIRO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_RASPI_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_TEST_BRICKS
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.clickOnSettingsItem
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.loadSavedBooleanSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.loadSavedStringSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.saveBooleanSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.saveStringSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.switchPreference
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManageExtensionsSettingsFragmentTest {

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SettingsActivity::class.java, true, false
    )

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var resources: Resources
    private val savedSettings: HashMap<String, Boolean> = HashMap()
    private val savedNXTSensorSettings: HashMap<String, String> = HashMap()
    private val savedEV3SensorSettings: HashMap<String, String> = HashMap()
    private val savedParrotArSettings: HashMap<String, String> = HashMap()
    private val savedRasPiSettings: HashMap<String, String> = HashMap()

    private val extensionSettings: List<String> = listOf(
        SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
        SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
        SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
        SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
        SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
        SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
        SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED,
        SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED,
        SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED,
        SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS,
        SETTINGS_SHOW_RASPI_BRICKS,
        SETTINGS_MULTIPLAYER_VARIABLES_ENABLED,
        SETTINGS_SHOW_EMBROIDERY_BRICKS,
        SETTINGS_SHOW_JUMPING_SUMO_BRICKS,
        SETTINGS_SHOW_ARDUINO_BRICKS,
        SETTINGS_SHOW_NFC_BRICKS,
        SETTINGS_SHOW_PHIRO_BRICKS,
        SETTINGS_CAST_GLOBALLY_ENABLED,
        SETTINGS_TEST_BRICKS
        )

    private val sensorSettingItemsNXT: List<Int> = listOf(
        R.string.nxt_choose_sensor_1,
        R.string.nxt_choose_sensor_2,
        R.string.nxt_choose_sensor_3,
        R.string.nxt_choose_sensor_4
    )

    private val sensorSettingItemsEV3: List<Int> = listOf(
        R.string.ev3_choose_sensor_1,
        R.string.ev3_choose_sensor_2,
        R.string.ev3_choose_sensor_3,
        R.string.ev3_choose_sensor_4
    )

    private val parrotArSettingItems: List<Int> = listOf(
        R.string.brick_drone_set_config,
        R.string.brick_drone_set_altitude,
        R.string.brick_drone_set_vertical_speed,
        R.string.brick_drone_set_rotation_speed,
        R.string.brick_drone_set_tilt_limit
    )

    private val parrotArSettingItemOptions: List<Int> = listOf(
        R.array.drone_setting_default_config,
        R.array.drone_altitude_spinner_items,
        R.array.drone_max_vertical_speed_items,
        R.array.drone_max_rotation_speed_items,
        R.array.drone_max_tilt_angle_items
    )

    private val parrotArSettingIdentifier = arrayOf(
        DRONE_CONFIGS,
        DRONE_ALTITUDE_LIMIT,
        DRONE_VERTICAL_SPEED,
        DRONE_ROTATION_SPEED,
        DRONE_TILT_ANGLE
    )

    private val parrotArSettingValues = listOf(
        FIRST,
        SECOND,
        THIRD,
        FOURTH
    )

    private val rasPiSettingSettingItems = arrayOf(
        RASPI_HOST,
        RASPI_PORT,
        RASPI_VERSION_SPINNER
    )

    @Before
    fun setup() {
        baseActivityTestRule.launchActivity(null)
        sharedPreferences = getDefaultSharedPreferences(baseActivityTestRule.activity)
        resources = baseActivityTestRule.activity.resources
        saveSettings()
        SettingsFragmentTestUtils.setAllBooleanSettingsTo(false, extensionSettings)
        clickOnSettingsItem(R.string.preference_title_manage_extensions)
    }

    @After
    fun teardown() {
        loadSavedSettings()
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun artificialIntelligenceSettingsTest() {
        clickOnSettingsItem(R.string.preference_title_ai)
        switchPreference(R.string.preference_title_ai_speech_recognition, SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS)
        switchPreference(R.string.preference_title_ai_speech_synthetization, SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS)
        switchPreference(R.string.preference_title_ai_face_detection, SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS)
        switchPreference(R.string.preference_title_ai_pose_detection, SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS)
        switchPreference(R.string.preference_title_ai_text_recognition, SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS)
    }

    @Test
    fun basicExtensionSettingsTest() {
        switchPreference(R.string.preference_title_multiplayer_variables_enabled, SETTINGS_MULTIPLAYER_VARIABLES_ENABLED)
        switchPreference(R.string.preference_title_enable_embroidery_bricks, SETTINGS_SHOW_EMBROIDERY_BRICKS)
        switchPreference(R.string.preference_title_enable_jumpingsumo_bricks, SETTINGS_SHOW_JUMPING_SUMO_BRICKS)
        switchPreference(R.string.preference_title_enable_arduino_bricks, SETTINGS_SHOW_ARDUINO_BRICKS)
        switchPreference(R.string.preference_title_enable_nfc_bricks, SETTINGS_SHOW_NFC_BRICKS)
        switchPreference(R.string.preference_title_enable_phiro_bricks, SETTINGS_SHOW_PHIRO_BRICKS)
        switchPreference(R.string.preference_description_cast_feature_globally_enabled, SETTINGS_CAST_GLOBALLY_ENABLED)
        switchPreference(R.string.preference_title_enable_test_bricks, SETTINGS_TEST_BRICKS)
    }

    @Test
    fun legoNxtSettingsTest() {
        clickOnSettingsItem(R.string.preference_title_enable_mindstorms_nxt_bricks)

        switchPreference(R.string.preference_title_enable_mindstorms_nxt_bricks, SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED)
        switchPreference(R.string.preference_disable_nxt_info_dialog, SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED)

        clickOnSettingsItem(R.string.preference_title_enable_mindstorms_nxt_bricks)

        checkNXTSensors()
    }

    @Test
    fun legoEv3SettingsTest() {
        clickOnSettingsItem(R.string.preference_title_enable_mindstorms_ev3_bricks)

        switchPreference(R.string.preference_title_enable_mindstorms_ev3_bricks, SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED)
        switchPreference(R.string.preference_disable_nxt_info_dialog, SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED)

        clickOnSettingsItem(R.string.preference_title_enable_mindstorms_ev3_bricks)

        checkEV3Sensors()
    }

    @Test
    fun parrotArSettingsTest() {
        clickOnSettingsItem(R.string.preference_title_enable_quadcopter_bricks)

        switchPreference(R.string.preference_title_enable_quadcopter_bricks, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS)
        clickOnSettingsItem(R.string.preference_title_enable_quadcopter_bricks)

        checkParrotArSettings()
    }

    @Test
    fun rasPiSettingsTest() {
        clickOnSettingsItem(R.string.preference_title_enable_raspi_bricks)

        switchPreference(R.string.preference_title_enable_raspi_bricks, SETTINGS_SHOW_RASPI_BRICKS)

        clickOnSettingsItem(R.string.preference_title_enable_raspi_bricks)

        clickOnSettingsItem(R.string.preference_raspi_host)
        onView(withId(android.R.id.edit))
            .perform(replaceText("192.168.8.1"), closeSoftKeyboard())
        onView(withText(R.string.ok))
            .perform(click())
        assertEquals("192.168.8.1", sharedPreferences.getString(RASPI_HOST, ""))

        clickOnSettingsItem(R.string.preference_raspi_port)
        onView(withId(android.R.id.edit))
            .perform(replaceText("1234"), closeSoftKeyboard())
        onView(withText(R.string.ok))
            .perform(click())
        assertEquals("1234", sharedPreferences.getString(RASPI_PORT, ""))

        val settingOptions = resources.getStringArray(R.array.raspi_version_spinner_names)
        val settingOptionsValues = resources.getStringArray(R.array.raspi_version_spinner_revisions)
        for (i in settingOptions.indices) {
            clickOnSettingsItem(R.string.raspi_settings_gpio_version)
            onView(withText(settingOptions[i]))
                .perform(click())

            assertEquals(settingOptionsValues[i],
                sharedPreferences.getString(RASPI_VERSION_SPINNER, ""))
        }

        Intents.init()
        val expectedIntent: Matcher<Intent> = allOf(hasAction(Intent.ACTION_VIEW),
                                                    hasData(resources.getString(R.string.preference_raspi_help_link)))
        Intents.intending(expectedIntent).respondWith(ActivityResult(0, null))
        clickOnSettingsItem(R.string.preference_raspi_help)
        Intents.intended(expectedIntent)
    }

    private fun checkNXTSensors() {
        val sensorOptions = resources.getStringArray(R.array.nxt_sensor_chooser)

        for (i in sensorOptions.indices) {
            clickOnSettingsItem(sensorSettingItemsNXT[0])
            onView(withText(sensorOptions[i]))
                .perform(scrollTo())
                .perform(click())

            assertEquals(translateNXTTextToPreference(sensorOptions[i]),
                sharedPreferences.getString(NXT_SENSORS[0], ""))
        }

        for (i in 1 until sensorSettingItemsNXT.size) {
            clickOnSettingsItem(sensorSettingItemsNXT[i])
            for (j in sensorOptions.indices) {
                onView(withText(sensorOptions[j]))
                    .perform(scrollTo())
                    .check(matches(isDisplayed()))
            }
            onView(withText(R.string.cancel))
                .perform(click())
        }
    }

    private fun checkEV3Sensors() {
        val sensorOptions = resources.getStringArray(R.array.ev3_sensor_chooser)

        for (i in sensorOptions.indices) {
            clickOnSettingsItem(sensorSettingItemsEV3[0])
            onView(withText(sensorOptions[i]))
                .perform(scrollTo())
                .perform(click())

            assertEquals(translateEV3TextToPreference(sensorOptions[i]),
                sharedPreferences.getString(EV3_SENSORS[0], ""))
        }

        for (i in 1 until sensorSettingItemsEV3.size) {
            clickOnSettingsItem(sensorSettingItemsEV3[i])
            for (j in sensorOptions.indices) {
                onView(withText(sensorOptions[j]))
                    .perform(scrollTo())
                    .check(matches(isDisplayed()))
            }
            onView(withText(R.string.cancel))
                .perform(click())
        }
    }

    private fun checkParrotArSettings() {
        for (i in parrotArSettingItems.indices) {
            val settingOptions = resources.getStringArray(parrotArSettingItemOptions[i])
            for (j in settingOptions.indices) {
                clickOnSettingsItem(parrotArSettingItems[i])
                onView(withText(settingOptions[j]))
                    .perform(scrollTo())
                    .perform(click())

                assertEquals(parrotArSettingValues[j].toString(),
                    sharedPreferences.getString(parrotArSettingIdentifier[i], ""))
            }
        }
    }

    private fun translateNXTTextToPreference(text: String): String {
        return when (text) {
            resources.getString(R.string.nxt_no_sensor) -> NXTSensor.Sensor.NO_SENSOR.toString()
            resources.getString(R.string.nxt_sensor_touch) -> NXTSensor.Sensor.TOUCH.toString()
            resources.getString(R.string.nxt_sensor_sound) -> SOUND.toString()
            resources.getString(R.string.nxt_sensor_light) -> LIGHT_INACTIVE.toString()
            resources.getString(R.string.nxt_sensor_light_active) -> LIGHT_ACTIVE.toString()
            resources.getString(R.string.nxt_sensor_ultrasonic) -> ULTRASONIC.toString()
            else -> ""
        }
    }

    private fun translateEV3TextToPreference(text: String): String {
        return when (text) {
            resources.getString(R.string.ev3_no_sensor) -> EV3Sensor.Sensor.NO_SENSOR.toString()
            resources.getString(R.string.ev3_sensor_touch) -> EV3Sensor.Sensor.TOUCH.toString()
            resources.getString(R.string.ev3_sensor_color) -> COLOR.toString()
            resources.getString(R.string.ev3_sensor_color_ambient) -> COLOR_AMBIENT.toString()
            resources.getString(R.string.ev3_sensor_color_reflected) -> COLOR_REFLECT.toString()
            resources.getString(R.string.ev3_sensor_infrared) -> INFRARED.toString()
            resources.getString(R.string.ev3_sensor_hitechnic_color) -> HT_NXT_COLOR.toString()
            resources.getString(R.string.ev3_sensor_nxt_temperature_c) -> NXT_TEMPERATURE_C.toString()
            resources.getString(R.string.ev3_sensor_nxt_temperature_f) -> NXT_TEMPERATURE_F.toString()
            resources.getString(R.string.ev3_sensor_nxt_light) -> NXT_LIGHT.toString()
            resources.getString(R.string.ev3_sensor_nxt_light_active) -> NXT_LIGHT_ACTIVE.toString()
            resources.getString(R.string.ev3_sensor_nxt_sound) -> NXT_SOUND.toString()
            resources.getString(R.string.ev3_sensor_nxt_ultrasonic) -> NXT_ULTRASONIC.toString()
            else -> ""
        }
    }

    private fun saveSettings() {
        saveBooleanSettings(savedSettings, extensionSettings)

        saveStringSettings(savedNXTSensorSettings, NXT_SENSORS)
        saveStringSettings(savedNXTSensorSettings, EV3_SENSORS)
        saveStringSettings(savedParrotArSettings, parrotArSettingIdentifier)
        saveStringSettings(savedRasPiSettings, rasPiSettingSettingItems)
    }

    private fun loadSavedSettings() {
        loadSavedBooleanSettings(savedSettings)

        loadSavedStringSettings(savedNXTSensorSettings)
        loadSavedStringSettings(savedEV3SensorSettings)
        loadSavedStringSettings(savedParrotArSettings)
        loadSavedStringSettings(savedRasPiSettings)
    }
}
