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

import android.hardware.camera2.CameraMetadata
import com.badlogic.gdx.math.Vector2
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.RaspiInterruptScript
import org.catrobat.catroid.content.WhenConditionScript
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTempoByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.CopyLookBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.OpenUrlBrick
import org.catrobat.catroid.content.bricks.PaintNewLookBrick
import org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick
import org.catrobat.catroid.content.bricks.PauseForBeatsBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RaspiPwmBrick
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.ReportBrick
import org.catrobat.catroid.content.bricks.RunningStitchBrick
import org.catrobat.catroid.content.bricks.SayBubbleBrick
import org.catrobat.catroid.content.bricks.SayForBubbleBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick
import org.catrobat.catroid.content.bricks.SetBounceBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.SetFrictionBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick
import org.catrobat.catroid.content.bricks.SetMassBrick
import org.catrobat.catroid.content.bricks.SetNfcTagBrick
import org.catrobat.catroid.content.bricks.SetParticleColorBrick
import org.catrobat.catroid.content.bricks.SetPenColorBrick
import org.catrobat.catroid.content.bricks.SetPenSizeBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.SetTempoBrick
import org.catrobat.catroid.content.bricks.SetTextBrick
import org.catrobat.catroid.content.bricks.SetThreadColorBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.SetVelocityBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick
import org.catrobat.catroid.content.bricks.TapAtBrick
import org.catrobat.catroid.content.bricks.TapForBrick
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick
import org.catrobat.catroid.content.bricks.TouchAndSlideBrick
import org.catrobat.catroid.content.bricks.TripleStitchBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick
import org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick
import org.catrobat.catroid.content.bricks.WriteVariableToFileBrick
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.Sensors
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
        fun floatToString(element: Float) = Formula(element).getTrimmedFormulaString(CatroidApplication.getAppContext()).trim()
        fun formulaToString(element: Formula) = element.getTrimmedFormulaString(CatroidApplication.getAppContext()).trim()

        private val testFormula1 = Formula(
            FormulaElement(
                FormulaElement.ElementType.OPERATOR, Operators.EQUAL.name, null, FormulaElement(FormulaElement.ElementType.NUMBER, "-12", null), FormulaElement(FormulaElement.ElementType.NUMBER, "15", null)
            )
        )
        private val testFormulaString1 = "-12 = 15"
        private val testFormulaNumber = Formula(-7)
        private val testVariable = UserVariable("testVariable")
        private val testString = "my TeSt StRiNg!!!"
        private val testColor = "ff0000"
        private val testScene = "testscene"
        private val testFloat = 3.14159265359f
        private val testFloat2 = 2.71828182846f
        private val testDouble = testFloat.toDouble()
        private val testDouble2 = testFloat2.toDouble()
        private val testInt1 = 5
        private val testInt2 = 7
        private val testInt3 = 3
        private val testInt4 = 90
        private val testUrl = "https://catrob.at"

        @Suppress("LongMethod")
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): List<Array<out Serializable?>> {
            return listOf(
                arrayOf(BroadcastBrick::class.simpleName, BroadcastBrick(testString), "Broadcast (message: ('$testString'));\n"),
                arrayOf(BroadcastWaitBrick::class.simpleName, BroadcastWaitBrick(testString), "Broadcast and wait (message: ('$testString'));\n"),
                arrayOf(NoteBrick::class.simpleName, NoteBrick(testString), "# $testString\n"),
                arrayOf(IfLogicBeginBrick::class.simpleName, IfLogicBeginBrick(testFormula1), "If (condition: ($testFormulaString1)) {\n} else {\n}\n"),
                arrayOf(IfThenLogicBeginBrick::class.simpleName, IfThenLogicBeginBrick(testFormula1), "If (condition: ($testFormulaString1)) {\n}\n"),
                arrayOf(WaitUntilBrick::class.simpleName, WaitUntilBrick(testFormula1), "Wait until (condition: ($testFormulaString1));\n"),
                arrayOf(RepeatBrick::class.simpleName, RepeatBrick(testFormula1), "Repeat (times: ($testFormulaString1)) {\n}\n"),
                arrayOf(RepeatUntilBrick::class.simpleName, RepeatUntilBrick(testFormula1), "Repeat until (condition: ($testFormulaString1)) {\n}\n"),
                arrayOf(ForVariableFromToBrick::class.simpleName, ForVariableFromToBrick(testInt1, testInt2), "For (value: (\"${testVariable.name}\"), from: ($testInt1), to: ($testInt2)) {\n}\n"),
                arrayOf(SceneTransitionBrick::class.simpleName, SceneTransitionBrick(testScene), "Continue (scene: ('$testScene'));\n"),
                arrayOf(SceneStartBrick::class.simpleName, SceneStartBrick(testScene), "Start (scene: ('$testScene'));\n"),
                arrayOf(TapAtBrick::class.simpleName, TapAtBrick(testInt1, testInt2), "Single tap at (x: ($testInt1), y: ($testInt2));\n"),
                arrayOf(TapForBrick::class.simpleName, TapForBrick(testInt1, testInt2, testDouble), "Touch at position for seconds (x: ($testInt1), y: ($testInt2), seconds: ($testDouble));\n"),
                arrayOf(TouchAndSlideBrick::class.simpleName, TouchAndSlideBrick(testInt1, testInt2, testInt3, testInt4, testDouble), "Touch at position and slide to position in seconds (start x: ($testInt1), start y: ($testInt2), to x: ($testInt3), to y: ($testInt4), seconds: ($testDouble));\n"),
                arrayOf(OpenUrlBrick::class.simpleName, OpenUrlBrick(testUrl), "Open in browser (url: ('$testUrl'));\n"),
                arrayOf(PlaceAtBrick::class.simpleName, PlaceAtBrick(testInt1, testInt2), "Place at (x: ($testInt1), y: ($testInt2));\n"),
                arrayOf(SetXBrick::class.simpleName, SetXBrick(testInt1), "Set (x: ($testInt1));\n"),
                arrayOf(SetYBrick::class.simpleName, SetYBrick(testInt1), "Set (y: ($testInt1));\n"),
                arrayOf(ChangeXByNBrick::class.simpleName, ChangeXByNBrick(testInt1), "Change x by (value: ($testInt1));\n"),
                arrayOf(ChangeYByNBrick::class.simpleName, ChangeYByNBrick(testInt1), "Change y by (value: ($testInt1));\n"),
                arrayOf(MoveNStepsBrick::class.simpleName, MoveNStepsBrick(testDouble), "Move (steps: ($testDouble));\n"),
                arrayOf(MoveNStepsBrick::class.simpleName, MoveNStepsBrick(testDouble), "Move (steps: ($testDouble));\n"),
                arrayOf(TurnLeftBrick::class.simpleName, TurnLeftBrick(testDouble), "Turn (direction: (left), degrees: ($testDouble));\n"),
                arrayOf(TurnLeftBrick::class.simpleName, TurnLeftBrick(testDouble), "Turn (direction: (left), degrees: ($testDouble));\n"),
                arrayOf(TurnRightBrick::class.simpleName, TurnRightBrick(testDouble), "Turn (direction: (right), degrees: ($testDouble));\n"),
                arrayOf(TurnRightBrick::class.simpleName, TurnRightBrick(testDouble), "Turn (direction: (right), degrees: ($testDouble));\n"),
                arrayOf(PointInDirectionBrick::class.simpleName, PointInDirectionBrick(testDouble), "Point in direction (degrees: ($testDouble));\n"),
                arrayOf(PointInDirectionBrick::class.simpleName, PointInDirectionBrick(testDouble), "Point in direction (degrees: ($testDouble));\n"),
                arrayOf(GlideToBrick::class.simpleName, GlideToBrick(testInt1, testInt2, testInt3 * 1000), "Glide to (x: ($testInt1), y: ($testInt2), seconds: ($testInt3));\n"),
                arrayOf(GlideToBrick::class.simpleName, GlideToBrick(testInt1, testInt2, 1234), "Glide to (x: ($testInt1), y: ($testInt2), seconds: (1.234));\n"),
                arrayOf(GoNStepsBackBrick::class.simpleName, GoNStepsBackBrick(testInt1), "Go back (number of layers: ($testInt1));\n"),
                arrayOf(SetCameraFocusPointBrick::class.simpleName, SetCameraFocusPointBrick(testFloat, testFloat2), "Become focus point with flexibility in percent (horizontal: (${floatToString(testFloat)}), vertical: (${floatToString(testFloat2)}));\n"),
                arrayOf(VibrationBrick::class.simpleName, VibrationBrick(testDouble), "Vibrate for (seconds: ($testDouble));\n"),
                arrayOf(SetVelocityBrick::class.simpleName, SetVelocityBrick(Vector2(testFloat, testFloat2)), "Set velocity to (x steps/second: (${floatToString(testFloat)}), y steps/second: (${floatToString(testFloat2)}));\n"),
                arrayOf(TurnLeftSpeedBrick::class.simpleName, TurnLeftSpeedBrick(testDouble), "Spin (direction: (left), degrees/second: ($testDouble));\n"),
                arrayOf(TurnRightSpeedBrick::class.simpleName, TurnRightSpeedBrick(testDouble), "Spin (direction: (right), degrees/second: ($testDouble));\n"),
                arrayOf(SetGravityBrick::class.simpleName, SetGravityBrick(Vector2(testFloat, testFloat2)), "Set gravity for all actors and objects to (x steps/second²: (${floatToString(testFloat)}), y steps/second²: (${floatToString(testFloat2)}));\n"),
                arrayOf(SetMassBrick::class.simpleName, SetMassBrick(testDouble), "Set (mass in kilograms: ($testDouble));\n"),
                arrayOf(SetBounceBrick::class.simpleName, SetBounceBrick(testDouble), "Set (bounce factor percentage: ($testDouble));\n"),
                arrayOf(SetFrictionBrick::class.simpleName, SetFrictionBrick(testDouble), "Set (friction percentage: ($testDouble));\n"),
                arrayOf(SetVolumeToBrick::class.simpleName, SetVolumeToBrick(testDouble), "Set (volume percentage: ($testDouble));\n"),
                arrayOf(ChangeVolumeByNBrick::class.simpleName, ChangeVolumeByNBrick(testDouble), "Change volume by (value: ($testDouble));\n"),
                arrayOf(PlayNoteForBeatsBrick::class.simpleName, PlayNoteForBeatsBrick(testInt1, testInt2), "Play (note: ($testInt1), number of beats: ($testInt2));\n"),
                arrayOf(PlayDrumForBeatsBrick::class.simpleName, PlayDrumForBeatsBrick(testInt1), "Play (drum: (snare drum), number of beats: ($testInt1));\n"),
                arrayOf(SetTempoBrick::class.simpleName, SetTempoBrick(testInt1), "Set (tempo: ($testInt1));\n"),
                arrayOf(ChangeTempoByNBrick::class.simpleName, ChangeTempoByNBrick(testInt1), "Change tempo by (value: ($testInt1));\n"),
                arrayOf(PauseForBeatsBrick::class.simpleName, PauseForBeatsBrick(testFloat), "Pause for (number of beats: (${floatToString(testFloat)}));\n"),
                arrayOf(SetLookByIndexBrick::class.simpleName, SetLookByIndexBrick(testInt1), "Switch to (look by number: ($testInt1));\n"),
                arrayOf(SetSizeToBrick::class.simpleName, SetSizeToBrick(testDouble), "Set (size percentage: ($testDouble));\n"),
                arrayOf(ChangeSizeByNBrick::class.simpleName, ChangeSizeByNBrick(testDouble), "Change size by (value: ($testDouble));\n"),
                arrayOf(AskSpeechBrick::class.simpleName, AskSpeechBrick(testString), "Ask question and store written answer to variable (question: ('$testString'), variable: (\"${testVariable.name}\"));\n"),
                arrayOf(SayBubbleBrick::class.simpleName, SayBubbleBrick(testString), "Say (text: ('$testString'));\n"),
                arrayOf(SayForBubbleBrick::class.simpleName, SayForBubbleBrick(testString, testFloat), "Say text for seconds (text: ('$testString'), seconds: (${floatToString(testFloat)}));\n"),
                arrayOf(ThinkBubbleBrick::class.simpleName, ThinkBubbleBrick(testString), "Think (text: ('$testString'));\n"),
                arrayOf(ThinkForBubbleBrick::class.simpleName, ThinkForBubbleBrick(testString, testFloat), "Think text for seconds (text: ('$testString'), seconds: (${floatToString(testFloat)}));\n"),
                arrayOf(ShowTextBrick::class.simpleName, ShowTextBrick(testInt1, testInt2), "Show (variable: (), x: ($testInt1), y: ($testInt2));\n"),
                arrayOf(ShowTextColorSizeAlignmentBrick::class.simpleName, ShowTextColorSizeAlignmentBrick(testInt1, testInt2, testDouble, testColor), "Show (variable: (), x: ($testInt1), y: ($testInt2), size: ($testDouble), color: (#$testColor), alignment: (centered));\n"),
                arrayOf(SetTransparencyBrick::class.simpleName, SetTransparencyBrick(testDouble), "Set (transparency percentage: ($testDouble));\n"),
                arrayOf(ChangeTransparencyByNBrick::class.simpleName, ChangeTransparencyByNBrick(testDouble), "Change transparency by (value: ($testDouble));\n"),
                arrayOf(SetBrightnessBrick::class.simpleName, SetBrightnessBrick(testDouble), "Set (brightness percentage: ($testDouble));\n"),
                arrayOf(ChangeBrightnessByNBrick::class.simpleName, ChangeBrightnessByNBrick(testDouble), "Change brightness by (value: ($testDouble));\n"),
                arrayOf(SetColorBrick::class.simpleName, SetColorBrick(testDouble), "Set (color: ($testDouble));\n"),
                arrayOf(ChangeColorByNBrick::class.simpleName, ChangeColorByNBrick(testDouble), "Change color by (value: ($testDouble));\n"),
                arrayOf(ParticleEffectAdditivityBrick::class.simpleName, ParticleEffectAdditivityBrick(1), "Turn (particle effect additivity: (off));\n"),
                arrayOf(SetParticleColorBrick::class.simpleName, SetParticleColorBrick(testColor), "Set (particle color: (#$testColor));\n"),
                arrayOf(SetBackgroundByIndexBrick::class.simpleName, SetBackgroundByIndexBrick(testInt1), "Set background to (look by number: ($testInt1));\n"),
                arrayOf(SetBackgroundByIndexAndWaitBrick::class.simpleName, SetBackgroundByIndexAndWaitBrick(testInt1), "Set background and wait (look by number: ($testInt1));\n"),
                arrayOf(CameraBrick::class.simpleName, CameraBrick(false), "Turn (camera: (off));\n"),
                arrayOf(ChooseCameraBrick::class.simpleName, ChooseCameraBrick(false), "Use (camera: (rear));\n"),
                arrayOf(FlashBrick::class.simpleName, FlashBrick(CameraMetadata.FLASH_MODE_OFF), "Turn (flashlight: (off));\n"),
                arrayOf(LookRequestBrick::class.simpleName, LookRequestBrick(testUrl), "Get image and use as current look (source: ('$testUrl'));\n"),
                arrayOf(PaintNewLookBrick::class.simpleName, PaintNewLookBrick(testString), "Paint new look (name: ('$testString'));\n"),
                arrayOf(CopyLookBrick::class.simpleName, CopyLookBrick(testString), "Copy look (name of copy: ('$testString'));\n"),
                arrayOf(SetPenSizeBrick::class.simpleName, SetPenSizeBrick(testDouble), "Set (pen size: ($testDouble));\n"),
                arrayOf(SetPenColorBrick::class.simpleName, SetPenColorBrick(255, 0, 0), "Set (pen color code: (#$testColor));\n"),
                arrayOf(ReportBrick::class.simpleName, ReportBrick(testString), "Return (value: ('$testString'));\n"),
                arrayOf(ReportBrick::class.simpleName, ReportBrick(testFormulaNumber), "Return (value: (${formulaToString(testFormulaNumber)}));\n"),
                arrayOf(SetVariableBrick::class.simpleName, SetVariableBrick(testDouble), "Set (variable: (\"${testVariable.name}\"), value: ($testDouble));\n"),
                arrayOf(SetVariableBrick::class.simpleName, SetVariableBrick(Sensors.PHIRO_FRONT_LEFT), "Set (variable: (\"${testVariable.name}\"), value: (phiro front left sensor));\n"),
                arrayOf(ChangeVariableBrick::class.simpleName, ChangeVariableBrick(testDouble), "Change (variable: (\"${testVariable.name}\"), value: ($testDouble));\n"),
                arrayOf(WriteVariableToFileBrick::class.simpleName, WriteVariableToFileBrick(Formula(testString)), "Write to file (variable: (\"${testVariable.name}\"), file: ('$testString'));\n"),
                arrayOf(ReadVariableFromFileBrick::class.simpleName, ReadVariableFromFileBrick(Formula(testString)), "Read from file (variable: (\"${testVariable.name}\"), file: ('$testString'), action: (keep the file));\n"),
                arrayOf(AddItemToUserListBrick::class.simpleName, AddItemToUserListBrick(testDouble), "Add (list: (), item: ($testDouble));\n"),
                arrayOf(DeleteItemOfUserListBrick::class.simpleName, DeleteItemOfUserListBrick(testInt1), "Delete item at (list: (), position: ($testInt1));\n"),
                arrayOf(InsertItemIntoUserListBrick::class.simpleName, InsertItemIntoUserListBrick(testDouble, testInt1), "Insert (list: (), position: ($testInt1), value: ($testDouble));\n"),
                arrayOf(ReplaceItemInUserListBrick::class.simpleName, ReplaceItemInUserListBrick(testDouble, testInt1), "Replace (list: (), position: ($testInt1), value: ($testDouble));\n"),
                arrayOf(StoreCSVIntoUserListBrick::class.simpleName, StoreCSVIntoUserListBrick(testInt1, "my;text"), "Store column of comma-separated values to list (list: (), csv: ('my;text'), column: ($testInt1));\n"),
                arrayOf(WebRequestBrick::class.simpleName, WebRequestBrick(testUrl), "Send web request (url: ('$testUrl'), answer variable: (\"${testVariable.name}\"));\n"),
                arrayOf(LegoNxtMotorTurnAngleBrick::class.simpleName, LegoNxtMotorTurnAngleBrick(LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, testInt1), "Turn NXT (motor: (A), degrees: ($testInt1));\n"),
                arrayOf(LegoNxtMotorStopBrick::class.simpleName, LegoNxtMotorStopBrick(LegoNxtMotorStopBrick.Motor.MOTOR_A), "Stop NXT (motor: (A));\n"),
                arrayOf(LegoNxtMotorMoveBrick::class.simpleName, LegoNxtMotorMoveBrick(LegoNxtMotorMoveBrick.Motor.MOTOR_A, testInt1), "Set NXT (motor: (A), speed percentage: ($testInt1));\n"),
                arrayOf(LegoNxtPlayToneBrick::class.simpleName, LegoNxtPlayToneBrick(testDouble, testDouble2), "Play NXT tone (seconds: ($testDouble2), frequency x100Hz: ($testDouble));\n"),
                arrayOf(LegoEv3MotorTurnAngleBrick::class.simpleName, LegoEv3MotorTurnAngleBrick(LegoEv3MotorTurnAngleBrick.Motor.MOTOR_B, testInt1), "Turn EV3 (motor: (B), degrees: ($testInt1));\n"),
                arrayOf(LegoEv3MotorMoveBrick::class.simpleName, LegoEv3MotorMoveBrick(LegoEv3MotorMoveBrick.Motor.MOTOR_B, testInt1), "Set EV3 (motor: (B), speed percentage: ($testInt1));\n"),
                arrayOf(LegoEv3MotorStopBrick::class.simpleName, LegoEv3MotorStopBrick(LegoEv3MotorStopBrick.Motor.MOTOR_B), "Stop EV3 (motor: (B));\n"),
                arrayOf(LegoEv3PlayToneBrick::class.simpleName, LegoEv3PlayToneBrick(testDouble, testDouble2, testInt1.toDouble()), "Play EV3 tone (seconds: ($testDouble2), frequency x100Hz: ($testDouble), volume: ($testInt1));\n"),
                arrayOf(LegoEv3SetLedBrick::class.simpleName, LegoEv3SetLedBrick(LegoEv3SetLedBrick.LedStatus.LED_RED), "Set EV3 (status: (red));\n"),
                arrayOf(ArduinoSendDigitalValueBrick::class.simpleName, ArduinoSendDigitalValueBrick(testInt1, testInt2), "Set Arduino (digital pin: ($testInt1), value: ($testInt2));\n"),
                arrayOf(ArduinoSendPWMValueBrick::class.simpleName, ArduinoSendPWMValueBrick(testInt1, testInt2), "Set Arduino (PWM~ pin: ($testInt1), value: ($testInt2));\n"),
                arrayOf(DroneMoveUpBrick::class.simpleName, DroneMoveUpBrick(testInt1 * 1000, testInt2), "Move AR.Drone 2.0 (direction: (up), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(DroneMoveDownBrick::class.simpleName, DroneMoveDownBrick(testInt1 * 1000, testInt2), "Move AR.Drone 2.0 (direction: (down), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(DroneMoveLeftBrick::class.simpleName, DroneMoveLeftBrick(testInt1 * 1000, testInt2), "Move AR.Drone 2.0 (direction: (left), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(DroneMoveRightBrick::class.simpleName, DroneMoveRightBrick(testInt1 * 1000, testInt2), "Move AR.Drone 2.0 (direction: (right), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(DroneMoveForwardBrick::class.simpleName, DroneMoveForwardBrick(testInt1 * 1000, testInt2), "Move AR.Drone 2.0 (direction: (forward), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(DroneMoveBackwardBrick::class.simpleName, DroneMoveBackwardBrick(testInt1 * 1000, testInt2), "Move AR.Drone 2.0 (direction: (backward), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(DroneTurnLeftBrick::class.simpleName, DroneTurnLeftBrick(testInt1 * 1000, testInt2), "Turn AR.Drone 2.0 (direction: (left), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(DroneTurnRightBrick::class.simpleName, DroneTurnRightBrick(testInt1 * 1000, testInt2), "Turn AR.Drone 2.0 (direction: (right), seconds: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(JumpingSumoMoveForwardBrick::class.simpleName, JumpingSumoMoveForwardBrick(testInt1 * 1000, testInt2), "Move Jumping Sumo (direction: (forward), steps: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(JumpingSumoMoveBackwardBrick::class.simpleName, JumpingSumoMoveBackwardBrick(testInt1 * 1000, testInt2), "Move Jumping Sumo (direction: (backward), steps: ($testInt1), power percentage: ($testInt2));\n"),
                arrayOf(JumpingSumoAnimationsBrick::class.simpleName, JumpingSumoAnimationsBrick(JumpingSumoAnimationsBrick.Animation.TAB), "Start Jumping Sumo (animation: (tab));\n"),
                arrayOf(JumpingSumoSoundBrick::class.simpleName, JumpingSumoSoundBrick(JumpingSumoSoundBrick.Sounds.ROBOT, testInt1), "Play Jumping Sumo (sound: (robot), volume: ($testInt1));\n"),
                arrayOf(JumpingSumoRotateLeftBrick::class.simpleName, JumpingSumoRotateLeftBrick(testDouble), "Turn Jumping Sumo (direction: (left), degrees: ($testDouble));\n"),
                arrayOf(JumpingSumoRotateRightBrick::class.simpleName, JumpingSumoRotateRightBrick(testDouble), "Turn Jumping Sumo (direction: (right), degrees: ($testDouble));\n"),
                arrayOf(PhiroMotorMoveForwardBrick::class.simpleName, PhiroMotorMoveForwardBrick(PhiroMotorMoveForwardBrick.Motor.MOTOR_RIGHT, testInt1), "Move Phiro (motor: (right), direction: (forward), speed percentage: ($testInt1));\n"),
                arrayOf(PhiroMotorMoveBackwardBrick::class.simpleName, PhiroMotorMoveBackwardBrick(PhiroMotorMoveBackwardBrick.Motor.MOTOR_BOTH, testInt1), "Move Phiro (motor: (both), direction: (backward), speed percentage: ($testInt1));\n"),
                arrayOf(PhiroMotorStopBrick::class.simpleName, PhiroMotorStopBrick(PhiroMotorStopBrick.Motor.MOTOR_RIGHT), "Stop Phiro (motor: (right));\n"),
                arrayOf(PhiroPlayToneBrick::class.simpleName, PhiroPlayToneBrick(PhiroPlayToneBrick.Tone.TI, testInt1), "Play Phiro (tone: (ti), seconds: ($testInt1));\n"),
                arrayOf(PhiroRGBLightBrick::class.simpleName, PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.LEFT, 255, 0, 0), "Set Phiro (light: (left), color: (#$testColor));\n"),
                arrayOf(PhiroIfLogicBeginBrick::class.simpleName, PhiroIfLogicBeginBrick(), "If (activated phiro: (front left sensor)) {\n} else {\n}\n"),
                arrayOf(RaspiIfLogicBeginBrick::class.simpleName, RaspiIfLogicBeginBrick(Formula(testInt1)), "If (Raspberry Pi pin: ($testInt1)) {\n} else {\n}\n"),
                arrayOf(RaspiSendDigitalValueBrick::class.simpleName, RaspiSendDigitalValueBrick(testInt1, testInt2), "Set (Raspberry Pi pin: ($testInt1), value: ($testInt2));\n"),
                arrayOf(RaspiPwmBrick::class.simpleName, RaspiPwmBrick(testInt1, testDouble, testDouble2), "Set (Raspberry Pi PWM~ pin: ($testInt1), percentage: ($testDouble2), Hz: ($testDouble));\n"),
                arrayOf(SetThreadColorBrick::class.simpleName, SetThreadColorBrick(Formula(testColor)), "Set (thread color: (#$testColor));\n"),
                arrayOf(RunningStitchBrick::class.simpleName, RunningStitchBrick(Formula(testInt1)), "Start running stitch (length: ($testInt1));\n"),
                arrayOf(ZigZagStitchBrick::class.simpleName, ZigZagStitchBrick(Formula(testInt1), Formula(testInt2)), "Start zigzag stitch (length: ($testInt1), width: ($testInt2));\n"),
                arrayOf(TripleStitchBrick::class.simpleName, TripleStitchBrick(Formula(testInt1)), "Start triple stitch (length: ($testInt1));\n"),
                arrayOf(WriteEmbroideryToFileBrick::class.simpleName, WriteEmbroideryToFileBrick(Formula(testString)), "Write embroidery data to (file: ('$testString'));\n"),
                arrayOf(AssertEqualsBrick::class.simpleName, AssertEqualsBrick(Formula(testInt1), Formula(testString)), "Assert equals (actual: ($testInt1), expected: ('$testString'));\n"),
                arrayOf(BroadcastReceiverBrick::class.simpleName, BroadcastReceiverBrick(BroadcastScript(testString)), "When you receive (message: ('$testString')) {\n}\n"),
                arrayOf(WhenConditionBrick::class.simpleName, WhenConditionBrick(WhenConditionScript(testFormula1)), "When condition becomes true (condition: (${formulaToString(testFormula1)})) {\n}\n"),
                arrayOf(WhenRaspiPinChangedBrick::class.simpleName, WhenRaspiPinChangedBrick(RaspiInterruptScript(testInt1.toString(), "low")), "When Raspberry Pi pin changes to (pin: ($testInt1), position: (low)) {\n}\n"),
                arrayOf(BackgroundRequestBrick::class.simpleName, BackgroundRequestBrick(), "Get image from source and use as background (url: (0));\n"),
                arrayOf(AskBrick::class.simpleName, AskBrick(testString), "Ask (question: ('$testString'), answer variable: (\"${testVariable.name}\"));\n"),
                arrayOf(SetNfcTagBrick::class.simpleName, SetNfcTagBrick(testString), "Set next NFC tag (text: ('$testString'));\n"),
                arrayOf(SetTextBrick::class.simpleName, SetTextBrick(testInt1, testInt2, testString), "Set (text: ('$testString'), x: ($testInt1), y: ($testInt2));\n"),
                arrayOf(SpeakAndWaitBrick::class.simpleName, SpeakAndWaitBrick(testString), "Speak and wait (text: ('$testString'));\n"),
                arrayOf(SpeakBrick::class.simpleName, SpeakBrick(testString), "Speak (text: ('$testString'));\n")
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
        val disabledValue = "// " + expectedOutput.replace(Regex("\\n(?!\$)"), "\n// ")
        brick.isCommentedOut = true
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        brick.isCommentedOut = false
        assertEquals(disabledValue, actualOutput)
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
