/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.preference.PreferenceManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import junit.framework.Assert.assertTrue
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SpeechAIExtensionSettingsBricksTest(
    private val name: String,
    private val setting: String,
    private val categoryId: Int,
    private val brickClassAndLayoutPairs: ArrayList<Pair<Class<*>, Int>>
) {
    private var initialSettings = mutableMapOf<String, Boolean>()
    private val TAG = SpeechAIExtensionSettingsBricksTest::class.java.simpleName
    private val exceptionMessage = "Exception was triggered: "

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule<SpriteActivity>(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf(
                "Speech Recognition bricks in category Sound",
                SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                R.string.category_sound,
                arrayListOf(
                    Pair(AskSpeechBrick::class.java, R.id.brick_set_variable_layout),
                    Pair(StartListeningBrick::class.java, R.id.brick_start_listening_layout),
                    Pair(
                        SetListeningLanguageBrick::class.java,
                        R.id.brick_set_listening_language_layout
                    )
                )
            ),
            arrayOf(
                "Speech Recognition bricks in category Data",
                SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                R.string.category_data,
                arrayListOf(
                    Pair(AskSpeechBrick::class.java, R.id.brick_set_variable_layout),
                    Pair(StartListeningBrick::class.java, R.id.brick_start_listening_layout)
                )
            ),
            arrayOf(
                "Speech Recognition bricks in category Device",
                SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
                R.string.category_device,
                arrayListOf(
                    Pair(AskSpeechBrick::class.java, R.id.brick_set_variable_layout),
                    Pair(StartListeningBrick::class.java, R.id.brick_start_listening_layout)
                )
            ),
            arrayOf(
                "Speech Synthetization bricks in category Sound",
                SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                R.string.category_sound,
                arrayListOf(
                    Pair(SpeakBrick::class.java, R.id.brick_speak_layout),
                    Pair(SpeakAndWaitBrick::class.java, R.id.brick_speak_and_wait_layout)
                )
            ),
            arrayOf(
                "Speech Synthetization bricks in category Device",
                SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
                R.string.category_device,
                arrayListOf(
                    Pair(SpeakBrick::class.java, R.id.brick_speak_layout),
                    Pair(SpeakAndWaitBrick::class.java, R.id.brick_speak_and_wait_layout)
                )
            )
        )
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val script = UiTestUtils.createProjectAndGetStartScript("projectName")
        script.addBrick(ChangeSizeByNBrick(0.0))

        baseActivityTestRule.launchActivity()
        saveInitialSettings()
        allAISpeechBrickSettings.forEach { setting -> setSettingToBoolean(setting, false) }
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        restoreInitialSettings()
    }

    @Test
    fun testIfBricksAreVisible() {
        setSettingToBoolean(setting, true)
        clickAddButton()
        openCategory(categoryId)
        brickClassAndLayoutPairs.forEach { brickClassAndLayoutPair ->
            checkIfBrickIsDisplayed(brickClassAndLayoutPair.first, brickClassAndLayoutPair.second)
        }
    }

    @Test
    fun testIfBricksAreNotVisible() {
        setSettingToBoolean(setting, false)
        clickAddButton()
        openCategory(categoryId)
        brickClassAndLayoutPairs.forEach { brickClassAndLayoutPair ->
            checkIfBrickIsNotDisplayed(brickClassAndLayoutPair.first, brickClassAndLayoutPair.second)
        }
    }

    private fun clickAddButton() {
        onView(withId(R.id.button_add)).perform(click())
    }

    private fun openCategory(categoryId: Int) {
        onData(
            allOf(
                `is`(instanceOf<Any>(String::class.java)),
                `is`(UiTestUtils.getResourcesString(categoryId))
            )
        ).inAdapterView(BrickCategoryListMatchers.isBrickCategoryView()).perform(click())
    }

    private fun checkIfBrickIsDisplayed(brickClass: Class<*>, layoutId: Int) {
        onData(instanceOf(brickClass))
            .inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
            .onChildView(withId(layoutId))
            .check(matches(isDisplayed()))
    }

    private fun checkIfBrickIsNotDisplayed(brickClass: Class<*>, layoutId: Int) {
        var isBrickNotDisplayed = false
        try {
            onData(instanceOf(brickClass))
                .inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
                .onChildView(withId(layoutId))
                .check(matches(isDisplayed()))
        } catch (e: PerformException) {
            Log.d(TAG, exceptionMessage + e)
            isBrickNotDisplayed = true
        }
        assertTrue(isBrickNotDisplayed)
    }

    private fun saveInitialSettings() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        allAISpeechBrickSettings.forEach { setting ->
            initialSettings[setting] = sharedPreferences.getBoolean(setting, false)
        }
    }

    private fun restoreInitialSettings() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        allAISpeechBrickSettings.forEach { setting ->
            sharedPreferencesEditor.putBoolean(
                setting,
                initialSettings.getOrDefault(setting, false)
            )
        }
        sharedPreferencesEditor.commit()
    }

    private val allAISpeechBrickSettings: List<String> = listOf(
        SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
        SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
    )

    private fun setSettingToBoolean(setting: String, value: Boolean) {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        sharedPreferencesEditor.putBoolean(setting, value)
        sharedPreferencesEditor.commit()
    }
}
