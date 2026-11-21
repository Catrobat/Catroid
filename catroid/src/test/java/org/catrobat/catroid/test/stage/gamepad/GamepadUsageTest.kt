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

package org.catrobat.catroid.test.stage.gamepad

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.WhenConditionScript
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SimulateGamepadButtonBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.sensor
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.catrobat.catroid.gamepad.GamepadButton.DOWN
import org.catrobat.catroid.gamepad.GamepadButton.L1
import org.catrobat.catroid.gamepad.GamepadButton.L2
import org.catrobat.catroid.gamepad.GamepadButton.R1
import org.catrobat.catroid.gamepad.GamepadButton.R2
import org.catrobat.catroid.gamepad.GamepadButton.SELECT
import org.catrobat.catroid.gamepad.GamepadButton.START
import org.catrobat.catroid.gamepad.GamepadButton.UP
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadButton.Y
import org.catrobat.catroid.stage.gamepad.GamepadUsage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GamepadUsageTest {

    @Test
    fun `no project uses no gamepad`() {
        assertUsesNoGamepad(GamepadUsage.of(null))
    }

    @Test
    fun `project without scenes uses no gamepad`() {
        assertUsesNoGamepad(GamepadUsage.of(Project()))
    }

    @Test
    fun `project without gamepad elements uses no gamepad`() {
        val project = projectWith(
            StartScript().withBricks(SetXBrick(Formula(NUMBER_VALUE)), SetXBrick(formulaOf(numberElement())))
        )

        assertUsesNoGamepad(GamepadUsage.of(project))
    }

    @Test
    fun `event script uses its button`() {
        val usage = GamepadUsage.of(projectWith(WhenGamepadButtonPressedScript("A")))

        assertEquals(setOf(A), usage.usedButtons)
        assertTrue(usage.usesGamepad)
        assertTrue(usage.isUsed(A))
        assertFalse(usage.isUsed(X))
    }

    @Test
    fun `event scripts of every button are found`() {
        val scripts = GamepadButton.entries.map { WhenGamepadButtonPressedScript(it.name) }

        val usage = GamepadUsage.of(projectWith(*scripts.toTypedArray()))

        assertEquals(GamepadButton.entries.toSet(), usage.usedButtons)
    }

    @Test
    fun `event script of an unknown button uses no gamepad`() {
        assertUsesNoGamepad(GamepadUsage.of(projectWith(WhenGamepadButtonPressedScript("HOME"))))
    }

    @Test
    fun `sensor in a formula of a brick uses its button`() {
        val project = projectWith(StartScript().withBricks(SetXBrick(sensorFormula(X))))

        assertEquals(setOf(X), GamepadUsage.of(project).usedButtons)
    }

    @Test
    fun `sensor in a brick nested in an if brick uses its button`() {
        val ifBrick = IfLogicBeginBrick(Formula(NUMBER_VALUE)).apply {
            addBrickToIfBranch(SetXBrick(sensorFormula(UP)))
            addBrickToElseBranch(SetXBrick(sensorFormula(DOWN)))
        }

        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(ifBrick)))

        assertEquals(setOf(UP, DOWN), usage.usedButtons)
    }

    @Test
    fun `sensor in a brick nested in a repeat brick inside an if brick uses its button`() {
        val repeatBrick = RepeatBrick(Formula(NUMBER_VALUE)).apply {
            addBrick(ChangeXByNBrick(sensorFormula(L1)))
        }
        val ifBrick = IfLogicBeginBrick(Formula(NUMBER_VALUE)).apply { addBrickToIfBranch(repeatBrick) }

        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(ifBrick)))

        assertEquals(setOf(L1), usage.usedButtons)
    }

    @Test
    fun `sensor in the condition of an if brick uses its button`() {
        val ifBrick = IfLogicBeginBrick(sensorFormula(SELECT))

        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(ifBrick)))

        assertEquals(setOf(SELECT), usage.usedButtons)
    }

    @Test
    fun `sensor in the condition of a when condition script uses its button`() {
        val usage = GamepadUsage.of(projectWith(WhenConditionScript(sensorFormula(START))))

        assertEquals(setOf(START), usage.usedButtons)
    }

    @Test
    fun `sensors deep inside operators and function arguments use their buttons`() {
        val condition = FormulaElement(
            ElementType.OPERATOR, Operators.LOGICAL_AND.name, null,
            FormulaElement(
                ElementType.OPERATOR, Operators.LOGICAL_NOT.name, null,
                null, sensorElement(L2.sensor)
            ),
            FormulaElement(
                ElementType.FUNCTION, Functions.JOIN3.name, null,
                numberElement(), sensorElement(R1.sensor), listOf(sensorElement(R2.sensor))
            )
        )

        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(WaitUntilBrick(formulaOf(condition)))))

        assertEquals(setOf(L2, R1, R2), usage.usedButtons)
    }

    @Test
    fun `every gamepad button sensor is found`() {
        val bricks = GamepadButton.entries.map { SetXBrick(sensorFormula(it)) }

        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(*bricks.toTypedArray())))

        assertEquals(GamepadButton.entries.toSet(), usage.usedButtons)
    }

    @Test
    fun `sensor in a user defined script uses its button`() {
        val usage = GamepadUsage.of(projectWith(UserDefinedScript().withBricks(SetXBrick(sensorFormula(Y)))))

        assertEquals(setOf(Y), usage.usedButtons)
    }

    @Test
    fun `commented out event script is ignored`() {
        val script = WhenGamepadButtonPressedScript("A").apply { isCommentedOut = true }

        assertUsesNoGamepad(GamepadUsage.of(projectWith(script)))
    }

    @Test
    fun `sensors in a commented out script are ignored`() {
        val script = StartScript().withBricks(SetXBrick(sensorFormula(A)))
        script.isCommentedOut = true

        assertUsesNoGamepad(GamepadUsage.of(projectWith(script)))
    }

    @Test
    fun `script whose script brick is commented out is ignored`() {
        val script = WhenGamepadButtonPressedScript("X").withBricks(SetXBrick(sensorFormula(Y)))
        script.scriptBrick.isCommentedOut = true

        assertUsesNoGamepad(GamepadUsage.of(projectWith(script)))
    }

    @Test
    fun `commented out brick is ignored`() {
        val commentedOutBrick = SetXBrick(sensorFormula(A)).apply { isCommentedOut = true }
        val script = StartScript().withBricks(commentedOutBrick, SetXBrick(sensorFormula(X)))

        assertEquals(setOf(X), GamepadUsage.of(projectWith(script)).usedButtons)
    }

    @Test
    fun `bricks inside a commented out if brick are ignored`() {
        val ifBrick = IfLogicBeginBrick(sensorFormula(A)).apply {
            addBrickToIfBranch(SetXBrick(sensorFormula(B)))
            isCommentedOut = true
        }

        assertUsesNoGamepad(GamepadUsage.of(projectWith(StartScript().withBricks(ifBrick))))
    }

    @Test
    fun `commented out when condition script is ignored`() {
        val script = WhenConditionScript(sensorFormula(START)).apply { isCommentedOut = true }

        assertUsesNoGamepad(GamepadUsage.of(projectWith(script)))
    }

    @Test
    fun `legacy Chromecast gamepad script and sensors are ignored`() {
        val legacySensorBricks = LEGACY_GAMEPAD_SENSORS.map { SetXBrick(formulaOf(sensorElement(it))) }
        val project = projectWith(
            WhenGamepadButtonScript("A"),
            StartScript().withBricks(*legacySensorBricks.toTypedArray())
        )

        assertUsesNoGamepad(GamepadUsage.of(project))
    }

    @Test
    fun `simulate gamepad button testing brick is ignored`() {
        val script = StartScript().withBricks(SimulateGamepadButtonBrick("B", true))

        assertUsesNoGamepad(GamepadUsage.of(projectWith(script)))
    }

    @Test
    fun `buttons of all scenes and sprites are collected`() {
        val project = Project()
        project.addScene(
            sceneOf(
                project, "Scene 1",
                spriteOf("Sprite 1", WhenGamepadButtonPressedScript("A")),
                spriteOf("Sprite 2", StartScript().withBricks(SetXBrick(sensorFormula(B))))
            )
        )
        project.addScene(sceneOf(project, "Scene 2", spriteOf("Sprite 3")))
        project.addScene(
            sceneOf(
                project, "Scene 3",
                spriteOf("Sprite 4", WhenGamepadButtonPressedScript("R2")),
                spriteOf("Sprite 5", WhenConditionScript(sensorFormula(UP)))
            )
        )

        assertEquals(setOf(A, B, R2, UP), GamepadUsage.of(project).usedButtons)
    }

    @Test
    fun `stage handles only B for a project without gamepad elements`() {
        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(SetXBrick(Formula(NUMBER_VALUE)))))

        GamepadButton.entries.forEach { button ->
            assertEquals(button.name, button == B, usage.isHandledByStage(button))
        }
    }

    @Test
    fun `stage handles only B without a project`() {
        val usage = GamepadUsage.of(null)

        GamepadButton.entries.forEach { button ->
            assertEquals(button.name, button == B, usage.isHandledByStage(button))
        }
    }

    @Test
    fun `stage handles every button when the project uses another button`() {
        val usage = GamepadUsage.of(projectWith(WhenGamepadButtonPressedScript("A")))

        assertFalse(usage.isUsed(B))
        GamepadButton.entries.forEach { button -> assertTrue(button.name, usage.isHandledByStage(button)) }
    }

    @Test
    fun `stage handles every button when the project only uses a sensor`() {
        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(SetXBrick(sensorFormula(START)))))

        assertFalse(usage.isUsed(B))
        GamepadButton.entries.forEach { button -> assertTrue(button.name, usage.isHandledByStage(button)) }
    }

    @Test
    fun `stage handles every button when the project uses B`() {
        val usage = GamepadUsage.of(projectWith(WhenGamepadButtonPressedScript("B")))

        assertTrue(usage.isUsed(B))
        GamepadButton.entries.forEach { button -> assertTrue(button.name, usage.isHandledByStage(button)) }
    }

    @Test
    fun `stage handles every button when the project uses the B sensor`() {
        val usage = GamepadUsage.of(projectWith(StartScript().withBricks(SetXBrick(sensorFormula(B)))))

        assertTrue(usage.isUsed(B))
        GamepadButton.entries.forEach { button -> assertTrue(button.name, usage.isHandledByStage(button)) }
    }

    @Test
    fun `B used only by a commented out script is unused but still handled by the stage`() {
        val commentedOutScript = WhenGamepadButtonPressedScript("B").apply { isCommentedOut = true }

        val usage = GamepadUsage.of(projectWith(WhenGamepadButtonPressedScript("A"), commentedOutScript))

        assertFalse(usage.isUsed(B))
        assertTrue(usage.isHandledByStage(A))
        assertTrue(usage.isHandledByStage(B))
    }

    @Test
    fun `B used only by the simulate gamepad button testing brick is unused`() {
        val script = StartScript().withBricks(SimulateGamepadButtonBrick("B", true))

        val usage = GamepadUsage.of(projectWith(script))

        assertFalse(usage.isUsed(B))
        assertTrue(usage.isHandledByStage(B))
    }

    private fun assertUsesNoGamepad(usage: GamepadUsage) {
        assertEquals(emptySet<GamepadButton>(), usage.usedButtons)
        assertFalse(usage.usesGamepad)
        GamepadButton.entries.forEach { button ->
            assertFalse(button.name, usage.isUsed(button))
            assertEquals(button.name, button == B, usage.isHandledByStage(button))
        }
    }

    private fun projectWith(vararg scripts: Script): Project {
        val project = Project()
        project.addScene(sceneOf(project, "Scene", spriteOf("Sprite", *scripts)))
        return project
    }

    private fun sceneOf(project: Project, name: String, vararg sprites: Sprite): Scene =
        Scene(name, project).apply { sprites.forEach { addSprite(it) } }

    private fun spriteOf(name: String, vararg scripts: Script): Sprite =
        Sprite(name).apply { scripts.forEach { addScript(it) } }

    private fun <T : Script> T.withBricks(vararg bricks: Brick): T =
        apply { bricks.forEach { addBrick(it) } }

    private fun sensorFormula(button: GamepadButton): Formula = formulaOf(sensorElement(button.sensor))

    private fun formulaOf(root: FormulaElement): Formula = Formula(root)

    private fun sensorElement(sensor: Sensors): FormulaElement =
        FormulaElement(ElementType.SENSOR, sensor.name, null)

    private fun numberElement(): FormulaElement =
        FormulaElement(ElementType.NUMBER, NUMBER_VALUE.toString(), null)

    private companion object {
        const val NUMBER_VALUE = 1

        val LEGACY_GAMEPAD_SENSORS = listOf(
            Sensors.GAMEPAD_A_PRESSED,
            Sensors.GAMEPAD_B_PRESSED,
            Sensors.GAMEPAD_UP_PRESSED,
            Sensors.GAMEPAD_DOWN_PRESSED,
            Sensors.GAMEPAD_LEFT_PRESSED,
            Sensors.GAMEPAD_RIGHT_PRESSED
        )
    }
}
