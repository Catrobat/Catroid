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
import android.os.Build
import org.catrobat.catroid.formulaeditor.InternFormula
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.sensor
import org.catrobat.catroid.formulaeditor.sensorNameResId
import org.catrobat.catroid.gamepad.GamepadButton
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GamepadButtonSensorDisplayTest {
    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun `every sensor name resource shows the button`() {
        assertEquals(
            EXPECTED_SENSOR_NAMES,
            GamepadButton.entries.map { button -> context.getString(button.sensorNameResId) }
        )
    }

    @Test
    fun `formula editor shows every gamepad button sensor by its name`() {
        assertEquals(
            EXPECTED_SENSOR_NAMES,
            GamepadButton.entries.map { button -> externSensorString(button.sensor).trim() }
        )
    }

    @Test
    fun `formula editor shows gamepad button sensors within a formula`() {
        val tokens = listOf(
            InternToken(InternTokenType.SENSOR, Sensors.GAMEPAD_BUTTON_X_PRESSED.name),
            InternToken(InternTokenType.OPERATOR, "LOGICAL_AND"),
            InternToken(InternTokenType.SENSOR, Sensors.GAMEPAD_BUTTON_SELECT_PRESSED.name)
        )

        assertEquals(
            "gamepad button X pressed and gamepad button select pressed",
            externFormulaString(tokens).trim()
        )
    }

    @Test
    fun `formula editor keeps showing legacy Chromecast gamepad sensors`() {
        assertEquals("gamepad A pressed", externSensorString(Sensors.GAMEPAD_A_PRESSED).trim())
        assertEquals("gamepad up pressed", externSensorString(Sensors.GAMEPAD_UP_PRESSED).trim())
    }

    private fun externSensorString(sensor: Sensors): String =
        externFormulaString(listOf(InternToken(InternTokenType.SENSOR, sensor.name)))

    private fun externFormulaString(tokens: List<InternToken>): String {
        val internFormula = InternFormula(tokens.toMutableList())
        internFormula.generateExternFormulaStringAndInternExternMapping(context)
        return internFormula.externFormulaString
    }

    private companion object {
        val EXPECTED_SENSOR_NAMES = listOf(
            "gamepad button A pressed",
            "gamepad button B pressed",
            "gamepad button X pressed",
            "gamepad button Y pressed",
            "gamepad button up pressed",
            "gamepad button down pressed",
            "gamepad button left pressed",
            "gamepad button right pressed",
            "gamepad button start pressed",
            "gamepad button select pressed",
            "gamepad button L1 pressed",
            "gamepad button L2 pressed",
            "gamepad button R1 pressed",
            "gamepad button R2 pressed"
        )
    }
}
