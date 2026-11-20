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

package org.catrobat.catroid.stage.gamepad

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.toGamepadButton
import org.catrobat.catroid.gamepad.GamepadButton

/**
 * The physical gamepad buttons that a project reacts to, and the resulting policy which controller
 * input the stage consumes instead of leaving it to Android.
 *
 * A button is used when a script that is not commented out is a "When gamepad button pressed"
 * script for it, or contains a brick that is not commented out and reads the "gamepad button
 * pressed" sensor of the button in one of its formulas. Legacy Chromecast gamepad scripts and
 * sensors, unknown button IDs and the Testing brick "Simulate gamepad button" do not count,
 * because none of them reacts to a physical controller.
 *
 * @property usedButtons the buttons the project reacts to
 */
class GamepadUsage private constructor(val usedButtons: Set<GamepadButton>) {

    /** Whether the project reacts to any physical gamepad button. */
    @get:JvmName("usesGamepad")
    val usesGamepad: Boolean
        get() = usedButtons.isNotEmpty()

    /** Whether the project reacts to [button]. */
    fun isUsed(button: GamepadButton): Boolean = button in usedButtons

    /**
     * Whether the stage consumes the input of [button] instead of passing it on to Android.
     *
     * A project without gamepad elements leaves all controller input to Android. Otherwise the
     * stage consumes every button except B, which it only consumes when the project uses B, so
     * that B keeps working as Android's Back button that opens the stage menu.
     */
    fun isHandledByStage(button: GamepadButton): Boolean =
        usesGamepad && (button != GamepadButton.B || isUsed(button))

    companion object {
        /** Scans all scenes, sprites and scripts of [project]; `null` uses no gamepad. */
        @JvmStatic
        fun of(project: Project?): GamepadUsage =
            GamepadUsage(project?.let(::usedButtonsOf).orEmpty())

        private fun usedButtonsOf(project: Project): Set<GamepadButton> = buildSet {
            project.sceneList.asSequence()
                .flatMap { scene -> scene.spriteList }
                .flatMap { sprite -> sprite.scriptList }
                .filterNot { script -> script.isCommentedOut }
                .forEach { script -> addButtonsUsedBy(script) }
        }

        private fun MutableSet<GamepadButton>.addButtonsUsedBy(script: Script) {
            eventButtonOf(script)?.let { add(it) }
            activeBricksOf(script)
                .filterIsInstance<FormulaBrick>()
                .flatMap { brick -> brick.formulas }
                .forEach { formula -> addAll(sensorButtonsOf(formula)) }
        }

        private fun eventButtonOf(script: Script): GamepadButton? =
            (script as? WhenGamepadButtonPressedScript)?.let { GamepadButton.fromId(it.button) }

        /**
         * The bricks of [script] that are not commented out, including its script brick and all
         * nested bricks. Commenting out a brick also comments out the bricks nested in it.
         */
        private fun activeBricksOf(script: Script): Sequence<Brick> =
            mutableListOf<Brick>()
                .also { script.addToFlatList(it) }
                .asSequence()
                .filterNot { brick -> brick.isCommentedOut }

        private fun sensorButtonsOf(formula: Formula): List<GamepadButton> =
            formula.root?.getUserDataRecursive(ElementType.SENSOR).orEmpty()
                .mapNotNull { sensorName -> Sensors.getSensorByValue(sensorName)?.toGamepadButton() }
    }
}
