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
import org.catrobat.catroid.formulaeditor.FormulaElement
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
class GamepadUsage private constructor(
    val usedButtons: Set<GamepadButton>,
    private val hasAnyButtonScript: Boolean
) {

    /** Whether the project reacts to any physical gamepad button. */
    @get:JvmName("usesGamepad")
    val usesGamepad: Boolean
        get() = usedButtons.isNotEmpty()

    /** Whether the project reacts to [button]. */
    fun isUsed(button: GamepadButton): Boolean = button in usedButtons

    /**
     * Whether the stage consumes the input of [button] instead of passing it on to Android.
     *
     * The stage always consumes B: an unused B leaves the stage directly, see
     * [org.catrobat.catroid.stage.StageExitController.onGamepadButtonPressed], so it never falls
     * through to Android's Back button. A project without gamepad elements leaves all other
     * controller input to Android, a project with gamepad elements gets every button.
     */
    fun isHandledByStage(button: GamepadButton): Boolean =
        button == GamepadButton.B || usesGamepad

    /**
     * Whether [button] counts for the choice "any". While the project does not use B explicitly, B
     * is the exit button of the stage, so "any" neither reacts to it nor counts as using it.
     */
    fun countsForAny(button: GamepadButton): Boolean =
        button != GamepadButton.B || isUsed(GamepadButton.B)

    /** Whether a press of [button] starts the project's "any button" event scripts. */
    fun startsAnyButtonScripts(button: GamepadButton): Boolean =
        hasAnyButtonScript && countsForAny(button)

    companion object {
        /** Scans all scenes, sprites and scripts of [project]; `null` uses no gamepad. */
        @JvmStatic
        fun of(project: Project?): GamepadUsage =
            GamepadUsage(
                project?.let(::usedButtonsOf).orEmpty(),
                project?.let(::hasAnyButtonScript) ?: false
            )

        /**
         * The buttons that the Compute dialog of [formula] takes: the buttons of the formula, and B
         * for the sensor "any" only if [project] uses B explicitly.
         */
        @JvmStatic
        fun buttonsForCompute(formula: Formula, project: Project?): Set<GamepadButton> {
            val buttons = buttonsUsedBy(formula)
            val countsB = sensorsOf(formula).contains(Sensors.GAMEPAD_BUTTON_ANY_PRESSED) &&
                of(project).isUsed(GamepadButton.B)
            return if (countsB) buttons + GamepadButton.B else buttons
        }

        private fun usedButtonsOf(project: Project): Set<GamepadButton> = buildSet {
            activeScriptsOf(project).forEach { script -> addButtonsUsedBy(script) }
        }

        private fun hasAnyButtonScript(project: Project): Boolean =
            activeScriptsOf(project).any { script ->
                script is WhenGamepadButtonPressedScript && script.button == GamepadButton.ANY_ID
            }

        private fun activeScriptsOf(project: Project): Sequence<Script> =
            project.sceneList.asSequence()
                .flatMap { scene -> scene.spriteList }
                .flatMap { sprite -> sprite.scriptList }
                .filterNot { script -> script.isCommentedOut }

        private fun MutableSet<GamepadButton>.addButtonsUsedBy(script: Script) {
            addAll(eventButtonsOf(script))
            activeBricksOf(script)
                .filterIsInstance<FormulaBrick>()
                .flatMap { brick -> brick.formulas }
                .forEach { formula -> addAll(buttonsUsedBy(formula)) }
        }

        private fun eventButtonsOf(script: Script): Set<GamepadButton> {
            val button = (script as? WhenGamepadButtonPressedScript)?.button
            return if (button == GamepadButton.ANY_ID) {
                BUTTONS_OF_ANY
            } else {
                setOfNotNull(GamepadButton.fromId(button))
            }
        }

        /**
         * The bricks of [script] that are not commented out, including its script brick and all
         * nested bricks. Commenting out a brick also comments out the bricks nested in it.
         */
        private fun activeBricksOf(script: Script): Sequence<Brick> =
            mutableListOf<Brick>()
                .also { script.addToFlatList(it) }
                .asSequence()
                .filterNot { brick -> brick.isCommentedOut }

        /** The buttons of the gamepad button sensors in [formula]; "any" never includes B. */
        @JvmStatic
        fun buttonsUsedBy(formula: Formula): Set<GamepadButton> =
            sensorsOf(formula).flatMapTo(mutableSetOf()) { sensor ->
                if (sensor == Sensors.GAMEPAD_BUTTON_ANY_PRESSED) {
                    BUTTONS_OF_ANY
                } else {
                    listOfNotNull(sensor.toGamepadButton())
                }
            }

        private fun sensorsOf(formula: Formula): List<Sensors> {
            val root: FormulaElement? = formula.root
            return root?.getUserDataRecursive(ElementType.SENSOR).orEmpty()
                .mapNotNull { sensorName -> Sensors.getSensorByValue(sensorName) }
        }

        /** What the choice "any" uses by itself, so it keeps an otherwise unused B as exit button. */
        private val BUTTONS_OF_ANY: Set<GamepadButton> =
            (GamepadButton.entries - GamepadButton.B).toSet()
    }
}
