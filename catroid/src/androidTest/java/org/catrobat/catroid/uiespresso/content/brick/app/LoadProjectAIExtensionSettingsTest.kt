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

package org.catrobat.catroid.uiespresso.content.brick.app

import android.content.Context
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.SENSOR
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.Sensors.FACE_DETECTED
import org.catrobat.catroid.formulaeditor.Sensors.FACE_SIZE
import org.catrobat.catroid.formulaeditor.Sensors.FACE_X_POSITION
import org.catrobat.catroid.formulaeditor.Sensors.FACE_Y_POSITION
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_DETECTED
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_SIZE
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_X_POSITION
import org.catrobat.catroid.formulaeditor.Sensors.SECOND_FACE_Y_POSITION
import org.catrobat.catroid.formulaeditor.Sensors.SPEECH_RECOGNITION_LANGUAGE
import org.catrobat.catroid.formulaeditor.Sensors.TEXT_FROM_CAMERA
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class LoadProjectAIExtensionSettingsTest(
    private val name: String,
    private val setting: String,
    private val brick: Brick
) {
    private var initialSettings = mutableMapOf<String, Boolean>()
    val applicationContext: Context = ApplicationProvider.getApplicationContext<Context>()
    var bufferedPrivacyPolicyPreferenceSetting = 0

    private lateinit var script: Script
    private val projectName = "projectName"

    @get:Rule
    var baseActivityTestRule: BaseActivityTestRule<MainMenuActivity> = BaseActivityTestRule(
        MainMenuActivity::class.java, false, false
    )

    companion object {
        private var speechRecognitionLanguageSensor =
            FormulaElement(SENSOR, SPEECH_RECOGNITION_LANGUAGE.name, null)

        private var faceDetectedSensor = FormulaElement(SENSOR, FACE_DETECTED.name, null)
        private var faceSizeSensor = FormulaElement(SENSOR, FACE_SIZE.name, null)
        private var faceXPositionSensor = FormulaElement(SENSOR, FACE_X_POSITION.name, null)
        private var faceYPositionSensor = FormulaElement(SENSOR, FACE_Y_POSITION.name, null)
        private var secondFaceDetectedSensor = FormulaElement(SENSOR, SECOND_FACE_DETECTED.name, null)
        private var secondFaceSizeSensor = FormulaElement(SENSOR, SECOND_FACE_SIZE.name, null)
        private var secondFaceXPositionSensor = FormulaElement(SENSOR, SECOND_FACE_X_POSITION.name, null)
        private var secondFaceYPositionSensor = FormulaElement(SENSOR, SECOND_FACE_Y_POSITION.name, null)

        private var textFromCameraSensor = FormulaElement(SENSOR, TEXT_FROM_CAMERA.name, null)
        private var textBlocksNumberSensor = FormulaElement(SENSOR, Sensors.TEXT_BLOCKS_NUMBER.name, null)
        private var textBlockXSensor = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_X.name, null)
        private var textBlockYSensor = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_Y.name, null)
        private var textBlockSizeSensor = FormulaElement(SENSOR, Sensors.TEXT_BLOCK_SIZE.name, null)
        private var textBlockFromCameraSensor =
            FormulaElement(SENSOR, Sensors.TEXT_BLOCK_FROM_CAMERA.name, null)
        private var textBlockLanguageFromCameraSensor =
            FormulaElement(SENSOR, Sensors.TEXT_BLOCK_LANGUAGE_FROM_CAMERA.name, null)

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("Speech Recognition AskSpeechBrick", SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                    AskSpeechBrick()),
            arrayOf("Speech Recognition StartListeningBrick", SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                StartListeningBrick()),
            arrayOf("Speech Recognition SetListeningLanguageBrick", SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                SetListeningLanguageBrick()),
            arrayOf("Speech Recognition speechRecognitionLanguageSensor", SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(speechRecognitionLanguageSensor))),
            arrayOf("Speech Synthetization SpeakBrick", SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                SpeakBrick()),
            arrayOf("Speech Synthetization SpeakAndWaitBrick", SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                SpeakAndWaitBrick()),
            arrayOf("Face Recognition faceDetectedSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceDetectedSensor))),
            arrayOf("Face Recognition faceSizeSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceSizeSensor))),
            arrayOf("Face Recognition faceXPositionSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceXPositionSensor))),
            arrayOf("Face Recognition faceYPositionSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(faceYPositionSensor))),
            arrayOf("Face Recognition secondFaceDetectedSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceDetectedSensor))),
            arrayOf("Face Recognition secondFaceSizeSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceSizeSensor))),
            arrayOf("Face Recognition secondFaceXPositionSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceXPositionSensor))),
            arrayOf("Face Recognition secondFaceYPositionSensor", SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
                ChangeSizeByNBrick(Formula(secondFaceYPositionSensor))),
            arrayOf("Text Recognition textFromCameraSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textFromCameraSensor))),
            arrayOf("Text Recognition textBlocksNumberSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlocksNumberSensor))),
            arrayOf("Text Recognition textBlockXSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockXSensor))),
            arrayOf("Text Recognition textBlockYSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockYSensor))),
            arrayOf("Text Recognition textBlockSizeSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockSizeSensor))),
            arrayOf("Text Recognition textBlockFromCameraSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockFromCameraSensor))),
            arrayOf("Text Recognition textBlockLanguageFromCameraSensor", SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
                ChangeSizeByNBrick(Formula(textBlockLanguageFromCameraSensor)))
        )
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        saveInitialSettings()

        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit().putInt(
                SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
                Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
            ).commit()

        allAIExtensionSettings.forEach { setting -> setSettingToBoolean(setting, false) }

        script = createProjectAndGetStartScript(projectName)
        baseActivityTestRule.launchActivity(null)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        restoreInitialSettings()
    }

    @Test
    fun testSettingsBeforeAndAfterLoadProject() {
        script.addBrick(brick)

        assertFalse(getSetting(setting))
        onView(ViewMatchers.withText(applicationContext.getString(R.string.main_menu_programs))).perform(
            click()
        )
        onView(ViewMatchers.withText(projectName)).perform(click())
        assertTrue(getSetting(setting))
    }

    private fun getSetting(setting: String): Boolean {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        return sharedPreferences.getBoolean(setting, false)
    }

    private fun saveInitialSettings() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        allAIExtensionSettings.forEach { setting ->
            initialSettings[setting] = sharedPreferences.getBoolean(setting, false)
        }
        bufferedPrivacyPolicyPreferenceSetting =
            sharedPreferences.getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)
    }

    private fun restoreInitialSettings() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        allAIExtensionSettings.forEach { setting ->
            sharedPreferencesEditor.putBoolean(
                setting,
                initialSettings.getOrDefault(setting, false)
            )
        }
        sharedPreferencesEditor.putInt(
            SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
            bufferedPrivacyPolicyPreferenceSetting
        )

        sharedPreferencesEditor.commit()
    }

    private val allAIExtensionSettings: List<String> = listOf(
        SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
        SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
        SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
        SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
    )

    private fun setSettingToBoolean(setting: String, value: Boolean) {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        sharedPreferencesEditor.putBoolean(setting, value)
        sharedPreferencesEditor.commit()
    }
}
