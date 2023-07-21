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

package org.catrobat.catroid.test.io.catrobatlanguage

import com.badlogic.gdx.math.Vector2
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.OpenUrlBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SetBounceBrick
import org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick
import org.catrobat.catroid.content.bricks.SetFrictionBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.catrobat.catroid.content.bricks.SetMassBrick
import org.catrobat.catroid.content.bricks.SetVelocityBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.TapAtBrick
import org.catrobat.catroid.content.bricks.TapForBrick
import org.catrobat.catroid.content.bricks.TouchAndSlideBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.UserVariableBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.Serializable
import java.util.Random

@RunWith(Parameterized::class)
class ValueSerializationTest(
    private val name: String,
    private val brick: Brick,
    private val expectedOutput: String
) {
    companion object {
        private val testFormula1 = Formula(
            FormulaElement(
                FormulaElement.ElementType.OPERATOR,
                Operators.EQUAL.name, null,
                FormulaElement(FormulaElement.ElementType.NUMBER, "-12", null),
                FormulaElement(FormulaElement.ElementType.NUMBER, "15", null)
            )
        )
        private val testFormulaString1 = "-12 = 15"
        private val testVariable = UserVariable("testVariable")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): List<Array<out Serializable?>> {
            return listOf(
                arrayOf(BroadcastBrick::class.simpleName, BroadcastBrick("test"), "Broadcast (message: ('test'));\n"),
                arrayOf(BroadcastWaitBrick::class.simpleName, BroadcastWaitBrick("test"), "Broadcast and wait (message: ('test'));\n"),
                arrayOf(NoteBrick::class.simpleName, NoteBrick("a comment"), "// a comment\n"),
                arrayOf(IfLogicBeginBrick::class.simpleName, IfLogicBeginBrick(testFormula1), "If (condition: ($testFormulaString1)) {\n} else {\n}\n"),
                arrayOf(IfThenLogicBeginBrick::class.simpleName, IfThenLogicBeginBrick(testFormula1), "If (condition: ($testFormulaString1)) {\n}\n"),
                arrayOf(WaitUntilBrick::class.simpleName, WaitUntilBrick(testFormula1), "Wait until (condition: ($testFormulaString1));\n"),
                arrayOf(RepeatBrick::class.simpleName, RepeatBrick(testFormula1), "Repeat (times: ($testFormulaString1)) {\n}\n"),
                arrayOf(RepeatUntilBrick::class.simpleName, RepeatUntilBrick(testFormula1), "Repeat until (condition: ($testFormulaString1)) {\n}\n"),
                arrayOf(ForVariableFromToBrick::class.simpleName, ForVariableFromToBrick(5, 10), "For (value: (\"${testVariable.name}\"), from: (5), to: (10)) {\n}\n"),
                arrayOf(SceneTransitionBrick::class.simpleName, SceneTransitionBrick("testscene"), "Continue (scene: ('testscene'));\n"),
                arrayOf(SceneStartBrick::class.simpleName, SceneStartBrick("testscene"), "Start (scene: ('testscene'));\n"),
                arrayOf(TapAtBrick::class.simpleName, TapAtBrick(5, 9), "Single tap at (x: (5), y: (9));\n"),
                arrayOf(TapForBrick::class.simpleName, TapForBrick(2, 3, 4.5), "Touch at position for seconds (x: (2), y: (3), seconds: (4.5));\n"),
                arrayOf(TouchAndSlideBrick::class.simpleName, TouchAndSlideBrick(2, 3, 4, 5, 6.7), "Touch at position and slide to position in seconds (start x: (2), start y: (3), to x: (4), to y: (5), seconds: (6.7));\n"),
                arrayOf(OpenUrlBrick::class.simpleName, OpenUrlBrick("https://catrob.at"), "Open in browser (url: ('https://catrob.at'));\n"),
                arrayOf(PlaceAtBrick::class.simpleName, PlaceAtBrick(2, 3), "Place at (x: (2), y: (3));\n"),
                arrayOf(SetXBrick::class.simpleName, SetXBrick(2), "Set (x: (2));\n"),
                arrayOf(SetYBrick::class.simpleName, SetYBrick(2), "Set (y: (2));\n"),
                arrayOf(ChangeXByNBrick::class.simpleName, ChangeXByNBrick(2), "Change x by (value: (2));\n"),
                arrayOf(ChangeYByNBrick::class.simpleName, ChangeYByNBrick(2), "Change y by (value: (2));\n"),
                arrayOf(MoveNStepsBrick::class.simpleName, MoveNStepsBrick(5.5), "Move (steps: (5.5));\n"),
                arrayOf(MoveNStepsBrick::class.simpleName, MoveNStepsBrick(3.0), "Move (steps: (3));\n"),
                arrayOf(TurnLeftBrick::class.simpleName, TurnLeftBrick(30.9), "Turn (direction: (left), degrees: (30.9));\n"),
                arrayOf(TurnLeftBrick::class.simpleName, TurnLeftBrick(30.0), "Turn (direction: (left), degrees: (30));\n"),
                arrayOf(TurnRightBrick::class.simpleName, TurnRightBrick(21.7252), "Turn (direction: (right), degrees: (21.7252));\n"),
                arrayOf(TurnRightBrick::class.simpleName, TurnRightBrick(21.0), "Turn (direction: (right), degrees: (21));\n"),
                arrayOf(PointInDirectionBrick::class.simpleName, PointInDirectionBrick(90.0), "Point in direction (degrees: (90));\n"),
                arrayOf(PointInDirectionBrick::class.simpleName, PointInDirectionBrick(11.11), "Point in direction (degrees: (11.11));\n"),
                arrayOf(GlideToBrick::class.simpleName, GlideToBrick(2, 3, 4), "Glide to (x: (2), y: (3), seconds: (4));\n"),
                arrayOf(GoNStepsBackBrick::class.simpleName, GoNStepsBackBrick(12), "Go back (number of layers: (12));\n"),
                // TODO
//                arrayOf(SetCameraFocusPointBrick::class.simpleName, SetCameraFocusPointBrick(), "Become focus point with flexibility in percent (horizontal: (0), vertical: (0));\n"),
                arrayOf(VibrationBrick::class.simpleName, VibrationBrick(25.0), "Vibrate for (seconds: (25));\n"),
                arrayOf(SetVelocityBrick::class.simpleName, SetVelocityBrick(Vector2(5f, 6f)), "Set velocity to (x steps/second: (5), y steps/second: (6));\n"),
                arrayOf(TurnLeftBrick::class.simpleName, TurnLeftBrick(20.0), "Spin (direction: (left), degrees/second: (20));\n"),
                arrayOf(TurnRightBrick::class.simpleName, TurnRightBrick(30.0), "Spin (direction: (right), degrees/second: (30));\n"),
                arrayOf(SetGravityBrick::class.simpleName, SetGravityBrick(Vector2(10f, 20f)), "Set gravity for all actors and objects to (x steps/second²: (10), y steps/second²: (20));\n"),
                arrayOf(SetMassBrick::class.simpleName, SetMassBrick(-33.0), "Set (mass in kilograms: (-33));\n"),
                arrayOf(SetBounceBrick::class.simpleName, SetBounceBrick(28.0), "Set (bounce factor percentage: (28));\n"),
                arrayOf(SetFrictionBrick::class.simpleName, SetFrictionBrick(78.0), "Set (friction percentage: (78));\n"),
                arrayOf(SetVolumeToBrick::class.simpleName, SetVolumeToBrick(34.3), "Set (volume percentage: (34.3));\n"),
                arrayOf(ChangeVolumeByNBrick::class.simpleName, ChangeVolumeByNBrick(5.3), "Change volume by (value: (5.3));\n"),





//
//
////
//
//                arrayOf(IfOnEdgeBounceBrick(), "If on edge, bounce;\n")
//                arrayOf(
//                    ArduinoSendDigitalValueBrick(3, 1),
//                    "Set Arduino (digital pin: (3), value: (1));\n"
//                ),

//                arrayOf(ArduinoSendPWMValueBrick(), "Set Arduino (PWM~ pin: (0), value: (0));"),
//                arrayOf(AssertEqualsBrick(), "Assert equals (actual: (0), expected: (0));"),
//                arrayOf(BackgroundRequestBrick(), ""),
//                arrayOf(ChangeBrightnessByNBrick(), "Change brightness by (value: (0));"),
//                arrayOf(ChangeColorByNBrick(), "Change color by (value: (0));"),
//                arrayOf(ChangeSizeByNBrick(), "Change size by (value: (0));"),
//                arrayOf(ChangeTempoByNBrick(), ""),
//                arrayOf(ChangeTransparencyByNBrick(), ""),
//                arrayOf(ChangeVolumeByNBrick(), ""),
//                arrayOf(ChangeXByNBrick(), ""),
//                arrayOf(ChangeYByNBrick(), ""),
//                arrayOf(CopyLookBrick(), ""),
//                arrayOf(DroneMoveBackwardBrick(), ""),
//                arrayOf(DroneMoveDownBrick(), ""),
//                arrayOf(DroneMoveForwardBrick(), ""),
//                arrayOf(DroneMoveLeftBrick(), ""),
//                arrayOf(DroneMoveRightBrick(), ""),
//                arrayOf(DroneTurnLeftBrick(), ""),
//                arrayOf(DroneTurnRightBrick(), ""),
//                arrayOf(GoNStepsBackBrick(), ""),
//                arrayOf(IfLogicBeginBrick(), ""),
//                arrayOf(IfThenLogicBeginBrick(), ""),
//                arrayOf(JumpingSumoMoveBackwardBrick(), ""),
//                arrayOf(JumpingSumoMoveForwardBrick(), ""),
//                arrayOf(JumpingSumoRotateLeftBrick(), ""),
//                arrayOf(JumpingSumoRotateRightBrick(), ""),
//                arrayOf(JumpingSumoSoundBrick(), ""),
//                arrayOf(LegoEv3MotorMoveBrick(), ""),
//                arrayOf(LegoEv3MotorTurnAngleBrick(), ""),
//                arrayOf(LegoEv3PlayToneBrick(), ""),
//                arrayOf(LegoNxtMotorMoveBrick(), ""),
//                arrayOf(LegoNxtMotorTurnAngleBrick(), ""),
//                arrayOf(LegoNxtPlayToneBrick(), ""),
//                arrayOf(LookRequestBrick(), ""),
//                arrayOf(MoveNStepsBrick(), ""),
//                arrayOf(NoteBrick(), ""),
//                arrayOf(OpenUrlBrick(), ""),
//                arrayOf(PaintNewLookBrick(), ""),
//                arrayOf(PauseForBeatsBrick(), ""),
//                arrayOf(PhiroMotorMoveBackwardBrick(), ""),
//                arrayOf(PhiroMotorMoveForwardBrick(), ""),
//                arrayOf(PhiroPlayToneBrick(), ""),
//                arrayOf(PhiroRGBLightBrick(), ""),
//                arrayOf(PlayDrumForBeatsBrick(), ""),
//                arrayOf(PlayNoteForBeatsBrick(), ""),
//                arrayOf(PlaySoundAtBrick(), ""),
//                arrayOf(PointInDirectionBrick(), ""),
//                arrayOf(RaspiPwmBrick(), ""),
//                arrayOf(RaspiSendDigitalValueBrick(), ""),
//                arrayOf(RepeatBrick(), ""),
//                arrayOf(RepeatUntilBrick(), ""),
//                arrayOf(ReportBrick(), ""),
//                arrayOf(RunningStitchBrick(), ""),
//                arrayOf(SetBackgroundByIndexAndWaitBrick(), ""),
//                arrayOf(SetBackgroundByIndexBrick(), ""),
//                arrayOf(SetBounceBrick(), ""),
//                arrayOf(SetBrightnessBrick(), "Set (brightness percentage: (0));"),
//                arrayOf(SetCameraFocusPointBrick(), ""),
//                arrayOf(SetColorBrick(), ""),
//                arrayOf(SetFrictionBrick(), ""),
//                arrayOf(SetGravityBrick(), ""),
//                arrayOf(SetLookByIndexBrick(), ""),
//                arrayOf(SetMassBrick(), ""),
//                arrayOf(SetNfcTagBrick(), ""),
//                arrayOf(SetParticleColorBrick(), ""),
//                arrayOf(SetPenColorBrick(), ""),
//                arrayOf(SetPenSizeBrick(), ""),
//                arrayOf(SetSizeToBrick(), ""),
//                arrayOf(SetTempoBrick(), ""),
//                arrayOf(SetTextBrick(), ""),
//                arrayOf(SetThreadColorBrick(), ""),
//                arrayOf(SetTransparencyBrick(), ""),
//                arrayOf(SetVelocityBrick(), ""),
//                arrayOf(SetVolumeToBrick(), ""),
//                arrayOf(SetXBrick(), ""),
//                arrayOf(SetYBrick(), ""),
//                arrayOf(SewUpBrick(), ""),
//                arrayOf(SpeakAndWaitBrick(), ""),
//                arrayOf(SpeakBrick(), ""),
//                arrayOf(ThinkBubbleBrick(), ""),
//                arrayOf(ThinkForBubbleBrick(), ""),
//                arrayOf(TripleStitchBrick(), ""),
//                arrayOf(TurnLeftBrick(), ""),
//                arrayOf(TurnLeftSpeedBrick(), ""),
//                arrayOf(TurnRightBrick(), ""),
//                arrayOf(TurnRightSpeedBrick(), ""),
//                arrayOf(VibrationBrick(), ""),
//                arrayOf(WaitBrick(), "Wait (seconds: (0));"),
//                arrayOf(WaitUntilBrick(), ""),
//                arrayOf(WhenConditionBrick(), ""),
//                arrayOf(WriteEmbroideryToFileBrick(), ""),
//                arrayOf(ZigZagStitchBrick(), "")
            )
        }
    }

    @Before
    fun setUp() {
        if (brick is UserVariableBrickWithFormula) {
            brick.userVariable = testVariable
        }
    }


    @Test
    fun testBasicCatrobatLanguageImplementation() {
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun testDisabledBrick() {
        val trimmedBaseValue = expectedOutput.substring(0, expectedOutput.length - 1)
        brick.isCommentedOut = true
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        brick.isCommentedOut = false
        val newOutput = "/* $trimmedBaseValue */\n"
        assertEquals(newOutput, actualOutput)
    }

    @Test
    fun testIndention() {
        val randomIndention = Random().nextInt(4) + 2
        val indention = CatrobatLanguageUtils.getIndention(randomIndention)
        val actualOutput = brick.serializeToCatrobatLanguage(randomIndention)
        val newOutput = indention + expectedOutput.replace(Regex("\\n(?!\$)"), "\n$indention")
        assertEquals(newOutput, actualOutput)
    }
}