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

package org.catrobat.catroid.test.formulaeditor

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.sensorNameResId
import org.catrobat.catroid.gamepad.GamepadButton
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Locale

/**
 * The name of a gamepad button sensor marks the button with a dropdown arrow in every language, so
 * that users see that they can tap the sensor to choose another button.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GamepadButtonSensorNameTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val languageTags: List<String> = context.assets.locales.filter { it.isNotEmpty() }

    @Test
    fun `app offers the languages that these tests check`() {
        assertTrue(languageTags.size >= MINIMUM_LANGUAGES)
    }

    @Test
    fun `every sensor name has one dropdown arrow in every language`() {
        languageTags.forEach { tag ->
            val localizedContext = contextFor(tag)
            GamepadButton.entries.forEach { button ->
                val name = localizedContext.getString(button.sensorNameResId)

                assertEquals("$tag $button: $name", 1, name.count { it == DROPDOWN_ARROW })
                assertFalse("$tag $button: $name", name.startsWith(DROPDOWN_ARROW))
            }
        }
    }

    @Test
    fun `dropdown arrow directly follows a button code in every language`() {
        languageTags.forEach { tag ->
            val localizedContext = contextFor(tag)
            BUTTON_CODES.forEach { button ->
                val name = localizedContext.getString(button.sensorNameResId)

                assertTrue("$tag $button: $name", name.contains("${button.name}$DROPDOWN_ARROW"))
            }
        }
    }

    @Test
    fun `sensor for any button has one dropdown arrow in every language`() {
        languageTags.forEach { tag ->
            val name = contextFor(tag).getString(R.string.formula_editor_sensor_gamepad_button_any_pressed)

            assertEquals("$tag: $name", 1, name.count { it == DROPDOWN_ARROW })
            assertFalse("$tag: $name", name.startsWith(DROPDOWN_ARROW))
        }
    }

    @Test
    fun `a space follows the dropdown arrow in languages that put spaces between words`() {
        languageTags.filter { !hasNoSpacesBetweenWords(it) }.forEach { tag ->
            val localizedContext = contextFor(tag)
            gamepadSensorNameResIds().forEach { resId ->
                val name = localizedContext.getString(resId)
                val textAfterArrow = name.substringAfter(DROPDOWN_ARROW)

                assertTrue("$tag: $name", textAfterArrow.isEmpty() || textAfterArrow.startsWith(' '))
            }
        }
    }

    @Test
    fun `no space follows the dropdown arrow in languages without spaces between words`() {
        languageTags.filter(::hasNoSpacesBetweenWords).forEach { tag ->
            val localizedContext = contextFor(tag)
            gamepadSensorNameResIds().forEach { resId ->
                val name = localizedContext.getString(resId)

                assertFalse("$tag: $name", name.substringAfter(DROPDOWN_ARROW).startsWith(' '))
            }
        }
    }

    private fun hasNoSpacesBetweenWords(languageTag: String): Boolean =
        Locale.forLanguageTag(languageTag).language in LANGUAGES_WITHOUT_SPACES_BETWEEN_WORDS

    private fun gamepadSensorNameResIds(): List<Int> =
        GamepadButton.entries.map { it.sensorNameResId } +
            R.string.formula_editor_sensor_gamepad_button_any_pressed

    private fun contextFor(languageTag: String): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(Locale.forLanguageTag(languageTag))
        return context.createConfigurationContext(configuration)
    }

    private companion object {
        const val DROPDOWN_ARROW = '▾'
        const val MINIMUM_LANGUAGES = 70
        val LANGUAGES_WITHOUT_SPACES_BETWEEN_WORDS = setOf("ja", "th", "zh")
        val BUTTON_CODES = listOf(
            GamepadButton.A, GamepadButton.B, GamepadButton.X, GamepadButton.Y,
            GamepadButton.L1, GamepadButton.L2, GamepadButton.R1, GamepadButton.R2
        )
    }
}
