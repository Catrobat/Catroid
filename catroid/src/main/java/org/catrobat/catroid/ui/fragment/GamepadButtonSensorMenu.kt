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

package org.catrobat.catroid.ui.fragment

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.isGamepadButtonSensor
import org.catrobat.catroid.formulaeditor.sensorNameResId
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.ui.gamepad.labelResId

/**
 * The menu that changes which button a selected "gamepad button ... pressed" sensor in the formula
 * editor checks.
 *
 * The menu lists "any" and the localized labels of all gamepad buttons and never offers free text.
 * Choosing a label asks [replaceSelection] to replace the selected sensor by the sensor of that
 * choice.
 * Cancelling the menu leaves the formula unchanged.
 */
class GamepadButtonSensorMenu(private val replaceSelection: ReplaceSelectionListener) {

    /** Replaces the selected formula token by the formula editor entry with the given name. */
    fun interface ReplaceSelectionListener {
        fun replaceSelectionWith(@StringRes sensorNameResId: Int)
    }

    /** Returns the localized labels of the menu items, in the order in which buttons are offered. */
    fun getLabels(context: Context): List<String> =
        listOf(context.getString(R.string.gamepad_button_any)) +
            GamepadButton.entries.map { button -> context.getString(button.labelResId) }

    /**
     * Replaces the selected sensor by the sensor of the button at the menu [position]. Positions
     * outside of the menu are ignored.
     */
    fun choose(position: Int) {
        if (position == ANY_POSITION) {
            replaceSelection.replaceSelectionWith(R.string.formula_editor_sensor_gamepad_button_any_pressed)
            return
        }
        GamepadButton.entries.getOrNull(position - FIRST_BUTTON_POSITION)?.let { button ->
            replaceSelection.replaceSelectionWith(button.sensorNameResId)
        }
    }

    /**
     * Shows the menu if [selectedTokens] consist of exactly one physical gamepad button sensor or
     * the sensor for any button.
     *
     * @return whether the menu is shown
     */
    fun showIfGamepadButtonSensorSelected(context: Context, selectedTokens: List<InternToken>?): Boolean {
        if (!isGamepadButtonSensorSelection(selectedTokens)) {
            return false
        }
        AlertDialog.Builder(context)
            .setItems(getLabels(context).toTypedArray()) { _, position -> choose(position) }
            .show()
        return true
    }

    companion object {
        private const val ANY_POSITION = 0
        private const val FIRST_BUTTON_POSITION = 1

        /**
         * Returns whether [selectedTokens] consist of exactly one physical gamepad button sensor or
         * the sensor for any button.
         */
        @JvmStatic
        fun isGamepadButtonSensorSelection(selectedTokens: List<InternToken>?): Boolean =
            selectedTokens?.singleOrNull()?.isGamepadButtonSensorToken() == true

        private fun InternToken.isGamepadButtonSensorToken(): Boolean =
            isSensor && Sensors.getSensorByValue(tokenStringValue)?.isGamepadButtonSensor == true
    }
}
