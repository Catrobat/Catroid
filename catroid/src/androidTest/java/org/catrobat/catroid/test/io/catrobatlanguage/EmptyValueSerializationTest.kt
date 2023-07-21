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

import org.catrobat.catroid.content.actions.LegoNxtMotorMoveAction
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTempoByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.ClearBackgroundBrick
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick
import org.catrobat.catroid.content.bricks.ClearUserListBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.ComeToFrontBrick
import org.catrobat.catroid.content.bricks.CopyLookBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.DeleteLookBrick
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick
import org.catrobat.catroid.content.bricks.DroneFlipBrick
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick
import org.catrobat.catroid.content.bricks.EditLookBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.FinishStageBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.HideBrick
import org.catrobat.catroid.content.bricks.HideTextBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick
import org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.OpenUrlBrick
import org.catrobat.catroid.content.bricks.PaintNewLookBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick
import org.catrobat.catroid.content.bricks.PauseForBeatsBrick
import org.catrobat.catroid.content.bricks.PenDownBrick
import org.catrobat.catroid.content.bricks.PenUpBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.PlaySoundAtBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RaspiPwmBrick
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.ReportBrick
import org.catrobat.catroid.content.bricks.ResetTimerBrick
import org.catrobat.catroid.content.bricks.RunningStitchBrick
import org.catrobat.catroid.content.bricks.SayBubbleBrick
import org.catrobat.catroid.content.bricks.SayForBubbleBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick
import org.catrobat.catroid.content.bricks.SetBounceBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.SetFrictionBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.catrobat.catroid.content.bricks.SetInstrumentBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick
import org.catrobat.catroid.content.bricks.SetMassBrick
import org.catrobat.catroid.content.bricks.SetParticleColorBrick
import org.catrobat.catroid.content.bricks.SetPenColorBrick
import org.catrobat.catroid.content.bricks.SetPenSizeBrick
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.SetTempoBrick
import org.catrobat.catroid.content.bricks.SetThreadColorBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.SetVelocityBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.SewUpBrick
import org.catrobat.catroid.content.bricks.ShowBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.StampBrick
import org.catrobat.catroid.content.bricks.StitchBrick
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick
import org.catrobat.catroid.content.bricks.StopRunningStitchBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.StopSoundBrick
import org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick
import org.catrobat.catroid.content.bricks.TapAtBrick
import org.catrobat.catroid.content.bricks.TapForBrick
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick
import org.catrobat.catroid.content.bricks.TouchAndSlideBrick
import org.catrobat.catroid.content.bricks.TripleStitchBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.WhenBrick
import org.catrobat.catroid.content.bricks.WhenClonedBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick
import org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick
import org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableToFileBrick
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.catrobat.catroid.test.physics.PhysicsObjectTypesTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.Serializable
import java.util.Random

@RunWith(Parameterized::class)
class EmptyValueSerializationTest(
    private val name: String,
    private val brick: Brick,
    private val expectedOutput: String
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): List<Array<Serializable?>> {
            return listOf(
                arrayOf(BroadcastBrick::class.simpleName, BroadcastBrick(), "Broadcast (message: (''));\n"),
                arrayOf(BroadcastWaitBrick::class.simpleName, BroadcastWaitBrick(), "Broadcast and wait (message: (''));\n"),
                arrayOf(DeleteThisCloneBrick::class.simpleName, DeleteThisCloneBrick(), "Delete this clone;\n"),
                arrayOf(WaitBrick::class.simpleName, WaitBrick(), "Wait (seconds: (0));\n"),
                arrayOf(NoteBrick::class.simpleName, NoteBrick(), "// 0\n"),
                arrayOf(ForeverBrick::class.simpleName, ForeverBrick(), "Forever {\n}\n"),
                arrayOf(IfLogicBeginBrick::class.simpleName, IfLogicBeginBrick(), "If (condition: (0)) {\n} else {\n}\n"),
                arrayOf(IfThenLogicBeginBrick::class.simpleName, IfThenLogicBeginBrick(), "If (condition: (0)) {\n}\n"),
                arrayOf(WaitUntilBrick::class.simpleName, WaitUntilBrick(), "Wait until (condition: (0));\n"),
                arrayOf(RepeatBrick::class.simpleName, RepeatBrick(), "Repeat (times: (0)) {\n}\n"),
                arrayOf(RepeatUntilBrick::class.simpleName, RepeatUntilBrick(), "Repeat until (condition: (0)) {\n}\n"),
                arrayOf(ForVariableFromToBrick::class.simpleName, ForVariableFromToBrick(), "For (value: (0), from: (0), to: (0)) {\n}\n"),
                arrayOf(ForItemInUserListBrick::class.simpleName, ForItemInUserListBrick(), "For each value in list (value: (0), list: (0)) {\n}\n"),
                arrayOf(SceneTransitionBrick::class.simpleName, SceneTransitionBrick(), "Continue (scene: (''));\n"),
                arrayOf(SceneStartBrick::class.simpleName, SceneStartBrick(), "Start (scene: (''));\n"),
                arrayOf(FinishStageBrick::class.simpleName, FinishStageBrick(), "Finish stage;\n"),
                arrayOf(StopScriptBrick::class.simpleName, StopScriptBrick(), "Stop (script: (this script));\n"),
                arrayOf(WaitTillIdleBrick::class.simpleName, WaitTillIdleBrick(), "Wait until all other scripts have stopped;\n"),
                arrayOf(TapAtBrick::class.simpleName, TapAtBrick(), "Single tap at (x: (0), y: (0));\n"),
                arrayOf(TapForBrick::class.simpleName, TapForBrick(), "Touch at position for seconds (x: (0), y: (0), seconds: (0));\n"),
                arrayOf(TouchAndSlideBrick::class.simpleName, TouchAndSlideBrick(), "Touch at position and slide to position in seconds (start x: (0), start y: (0), to x: (0), to y: (0), seconds: (0));\n"),
                arrayOf(OpenUrlBrick::class.simpleName, OpenUrlBrick(), "Open in browser (url: (0));\n"),
                arrayOf(PlaceAtBrick::class.simpleName, PlaceAtBrick(), "Place at (x: (0), y: (0));\n"),
                arrayOf(SetXBrick::class.simpleName, SetXBrick(), "Set (x: (0));\n"),
                arrayOf(SetYBrick::class.simpleName, SetYBrick(), "Set (y: (0));\n"),
                arrayOf(ChangeXByNBrick::class.simpleName, ChangeXByNBrick(0), "Change x by (value: (0));\n"),
                arrayOf(ChangeYByNBrick::class.simpleName, ChangeYByNBrick(0), "Change y by (value: (0));\n"),
                arrayOf(IfOnEdgeBounceBrick::class.simpleName, IfOnEdgeBounceBrick(), "If on edge, bounce;\n"),
                arrayOf(MoveNStepsBrick::class.simpleName, MoveNStepsBrick(), "Move (steps: (0));\n"),
                arrayOf(TurnLeftBrick::class.simpleName, TurnLeftBrick(), "Turn (direction: (left), degrees: (0));\n"),
                arrayOf(TurnRightBrick::class.simpleName, TurnRightBrick(), "Turn (direction: (right), degrees: (0));\n"),
                arrayOf(PointInDirectionBrick::class.simpleName, PointInDirectionBrick(), "Point in direction (degrees: (0));\n"),
                arrayOf(GlideToBrick::class.simpleName, GlideToBrick(), "Glide to (x: (0), y: (0), seconds: (0));\n"),
                arrayOf(GoNStepsBackBrick::class.simpleName, GoNStepsBackBrick(), "Go back (number of layers: (0));\n"),
                arrayOf(ComeToFrontBrick::class.simpleName, ComeToFrontBrick(), "Come to front;\n"),
                arrayOf(SetCameraFocusPointBrick::class.simpleName, SetCameraFocusPointBrick(), "Become focus point with flexibility in percent (horizontal: (0), vertical: (0));\n"),
                arrayOf(VibrationBrick::class.simpleName, VibrationBrick(), "Vibrate for (seconds: (0));\n"),
                arrayOf(SetPhysicsObjectTypeBrick::class.simpleName, SetPhysicsObjectTypeBrick(), "Set (motion type: ());\n"),
                arrayOf(SetVelocityBrick::class.simpleName, SetVelocityBrick(), "Set velocity to (x steps/second: (0), y steps/second: (0));\n"),
                arrayOf(TurnLeftBrick::class.simpleName, TurnLeftBrick(), "Spin (direction: (left), degrees/second: (0));\n"),
                arrayOf(TurnRightBrick::class.simpleName, TurnRightBrick(), "Spin (direction: (right), degrees/second: (0));\n"),
                arrayOf(SetGravityBrick::class.simpleName, SetGravityBrick(), "Set gravity for all actors and objects to (x steps/second²: (0), y steps/second²: (0));\n"),
                arrayOf(SetMassBrick::class.simpleName, SetMassBrick(), "Set (mass in kilograms: (0));\n"),
                arrayOf(SetBounceBrick::class.simpleName, SetBounceBrick(), "Set (bounce factor percentage: (0));\n"),
                arrayOf(SetFrictionBrick::class.simpleName, SetFrictionBrick(), "Set (friction percentage: (0));\n"),
                arrayOf(FadeParticleEffectBrick::class.simpleName, FadeParticleEffectBrick(), "Fade particle (effect: ());\n"),
                arrayOf(PlaySoundBrick::class.simpleName, PlaySoundBrick(), "Start (sound: ());\n"),
                arrayOf(PlaySoundAndWaitBrick::class.simpleName, PlaySoundAndWaitBrick(), "Start sound and skip seconds (sound: (), seconds: (0));\n"),
                arrayOf(PlaySoundAtBrick::class.simpleName, PlaySoundAtBrick(), "Start sound and skip seconds (sound: (), seconds: (0));\n"),
                arrayOf(StopSoundBrick::class.simpleName, StopSoundBrick(), "Stop (sound: ());\n"),
                arrayOf(StopAllSoundsBrick::class.simpleName, StopAllSoundsBrick(), "Stop all sounds;\n"),
                arrayOf(SetVolumeToBrick::class.simpleName, SetVolumeToBrick(), "Set (volume percentage: (0));\n"),
                arrayOf(ChangeVolumeByNBrick::class.simpleName, ChangeVolumeByNBrick(), "Change volume by (value: (0));\n"),
                arrayOf(SetInstrumentBrick::class.simpleName, SetInstrumentBrick(), "Set (instrument: ());\n"),
                arrayOf(PlayNoteForBeatsBrick::class.simpleName, PlayNoteForBeatsBrick(), "Play (note: (), number of beats: (0));\n"),
                arrayOf(PlayDrumForBeatsBrick::class.simpleName, PlayDrumForBeatsBrick(), "Play (drum: (), number of beats: (0));\n"),
                arrayOf(SetTempoBrick::class.simpleName, SetTempoBrick(), "Set (tempo: (0));\n"),
                arrayOf(ChangeTempoByNBrick::class.simpleName, ChangeTempoByNBrick(), "Change tempo by (value: (0));\n"),

                /*


                           arrayOf(PauseForBeatsBrick(), "Pause for (number of beats: (0));\n"),
                           arrayOf(SetLookBrick(), "Switch to (look: ());\n"),
                           arrayOf(SetLookByIndexBrick(), "Switch to (look by number: (0));\n"),
                           arrayOf(NextLookBrick(), "Next look;\n"),
                           arrayOf(PreviousLookBrick(), "Previous look;\n"),
                           arrayOf(SetSizeToBrick(), "Set (size percentage: (0));\n"),
                           arrayOf(ChangeSizeByNBrick(), "Change size by (value: (0));"),
                           arrayOf(HideBrick(), "Hide;\n"),
                           arrayOf(ShowBrick(), "Show;\n"),
                           arrayOf(
                               AskSpeechBrick(),
                               "Ask question and store written answer to variable (question: (), variable: ());\n"
                           ),
                           arrayOf(SayBubbleBrick(), "Say (text: ());\n"),
                           arrayOf(SayForBubbleBrick(), "Say text for seconds (text: (), seconds: (0));\n"),
                           arrayOf(ThinkBubbleBrick(), "Think (text: ());\n"),
                           arrayOf(
                               ThinkForBubbleBrick(),
                               "Think text for seconds (text: (), seconds: (0));\n"
                           ),
                           arrayOf(ShowTextBrick(), "Show (variable: (), x: (0), y: (0));\n"),
                           arrayOf(
                               ShowTextColorSizeAlignmentBrick(),
                               "Show (variable: (0), x: (0), y: (0), size: (0), color: (0), alignment: (0));\n"
                           ),
                           arrayOf(SetTransparencyBrick(), "Set (transparency percentage: (0));\n"),
                           arrayOf(ChangeTransparencyByNBrick(), "Change transparency by (value: (0));\n"),
                           arrayOf(SetBrightnessBrick(), "Set (brightness percentage: (0));"),
                           arrayOf(ChangeBrightnessByNBrick(), "Change brightness by (value: (0));"),
                           arrayOf(SetColorBrick(), "Set (color: (0));\n"),
                           arrayOf(ParticleEffectAdditivityBrick(), "Turn (particle effect additivity: (0));\n"),
                           arrayOf(SetParticleColorBrick(), "Set (particle color: (0));\n"),
                           arrayOf(ClearGraphicEffectBrick(), "Clear graphic effects;\n"),
                           arrayOf(SetBackgroundBrick(), "Set background to (look: ());\n"),
                           arrayOf(SetBackgroundByIndexBrick(), "Set background to (look by number: (0));\n"),
                           arrayOf(SetBackgroundAndWaitBrick(), "Set background and wait (look: ());\n"),
                           arrayOf(SetBackgroundByIndexAndWaitBrick(), "Set background and wait (look by number: (0));\n"),
                           arrayOf(CameraBrick(), "Turn (camera: (0));\n"),
                           arrayOf(ChooseCameraBrick(), "Use (camera: (0));\n"),
                           arrayOf(FlashBrick(), "Turn (flashlight: (0));\n"),
                           arrayOf(LookRequestBrick(), "Get image and use as current look (source: ());\n"),
                           arrayOf(PaintNewLookBrick(), "Paint new look (name: ());\n"),
                           arrayOf(EditLookBrick(), "Edit look;\n"),
                           arrayOf(CopyLookBrick(), "Copy look (name of copy: ());\n"),
                           arrayOf(DeleteLookBrick(), "Delete look;\n"),
                           arrayOf(PenDownBrick(), "Pen down;\n"),
                           arrayOf(PenUpBrick(), "Pen up;\n"),
                           arrayOf(SetPenSizeBrick(), "Set (pen size: (0));\n"),
                           arrayOf(SetPenColorBrick(), "Set (pen color code: (0));\n"),
                           arrayOf(ClearBackgroundBrick(), "Clear;\n"),
                           arrayOf(StampBrick(), "Stamp;\n"),
                           arrayOf(ReportBrick(), "Report (value: (0));\n"),
                           arrayOf(SetVariableBrick(), "Set (variable: (0), value: (0));\n"),
                           arrayOf(ChangeVariableBrick(), "Change (variable: (0), value: (0));\n"),
                           arrayOf(HideTextBrick(), "Hide (variable: (0));\n"),
                           arrayOf(WriteVariableOnDeviceBrick(), "Write on device (variable: (0));\n"),
                           arrayOf(ReadVariableFromDeviceBrick(), "Read from device (variable: (0));\n"),
                           arrayOf(WriteVariableToFileBrick(), "Write to file (variable: (0), file: (0));\n"),
                           arrayOf(ReadVariableFromFileBrick(), "Read from file (variable: (0), file: (0), action: ());\n"),
                           arrayOf(AddItemToUserListBrick(), "Add (list: (), item: (0));\n"),
                           arrayOf(DeleteItemOfUserListBrick(), "Delete (list: (), item: (0));\n"),
                           arrayOf(ClearUserListBrick(), "Delete all items (list: ());\n"),
                           arrayOf(InsertItemIntoUserListBrick(), "Insert (list: (), position: (0), value: (0));\n"),
                           arrayOf(ReplaceItemInUserListBrick(), "Replace (list: (), position: (0), value: (0));\n"),
                           arrayOf(WriteListOnDeviceBrick(), "Write on device (list: ());\n"),
                           arrayOf(ReadListFromDeviceBrick(), "Read from device (list: ());\n"),
                           arrayOf(
                               StoreCSVIntoUserListBrick(),
                               "Store column of comma-separated values to list (list: (), csv: (), column: ());\n"
                           ),
                           arrayOf(WebRequestBrick(), "Send web request (url: (), answer variable: ());\n"),
                           arrayOf(ResetTimerBrick(), "Reset timer;\n"),
                           arrayOf(LegoNxtMotorTurnAngleBrick(), "Turn NXT (motor: (), degrees: (0));\n"),
                           arrayOf(LegoNxtMotorStopBrick(), "Stop NXT (motor: ());\n"),
                           arrayOf(LegoNxtMotorMoveAction(), "Set NXT (motor: (), speed percentage: (0));\n"),
                           arrayOf(LegoNxtPlayToneBrick(), "Play NXT tone (seconds: (0), frequency x100Hz: (0));\n"),
                           arrayOf(LegoEv3MotorTurnAngleBrick(), "Turn EV3 (motor: (), degrees: (0));\n"),
                           arrayOf(LegoEv3MotorMoveBrick(), "Set EV3 (motor: (), speed percentage: (0));\n"),
                           arrayOf(LegoEv3MotorStopBrick(), "Stop EV3 (motor: ());\n"),
                           arrayOf(LegoEv3PlayToneBrick(), "Play EV3 tone (seconds: (0), frequency x100Hz: (0), volume: (0));\n"),
                           arrayOf(LegoEv3SetLedBrick(), "Set EV3 (status: ());\n"),
                           arrayOf(
                               ArduinoSendDigitalValueBrick(),
                               "Set Arduino (digital pin: (0), value: (0));\n"
                           ),
                           arrayOf(ArduinoSendPWMValueBrick(), "Set Arduino (PWM~ pin: (0), value: (0));\n"),
                           arrayOf(DroneTakeOffLandBrick(), "Take off / land AR.Drone 2.0;\n"),
                           arrayOf(DroneEmergencyBrick(), "Emergency AR.Drone 2.0;\n"),
                           arrayOf(DroneMoveUpBrick(), "Move AR.Drone 2.0 (direction: (up), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneMoveDownBrick(), "Move AR.Drone 2.0 (direction: (down), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneMoveLeftBrick(), "Move AR.Drone 2.0 (direction: (left), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneMoveRightBrick(), "Move AR.Drone 2.0 (direction: (right), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneMoveForwardBrick(), "Move AR.Drone 2.0 (direction: (forward), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneMoveBackwardBrick(), "Move AR.Drone 2.0 (direction: (backward), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneTurnLeftBrick(), "Turn AR.Drone 2.0 (direction: (left), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneTurnRightBrick(), "Turn AR.Drone 2.0 (direction: (right), seconds: (0), power percentage: (0));\n"),
                           arrayOf(DroneFlipBrick(), "Flip AR.Drone 2.0;\n"),
                           arrayOf(DronePlayLedAnimationBrick(), "Play AR.Drone 2.0 (flash animation: ());\n"),
                           arrayOf(DroneSwitchCameraBrick(), "Switch AR.Drone 2.0 camera;\n"),
                           arrayOf(JumpingSumoMoveForwardBrick(), "Move Jumping Sumo (direction: (forward), steps: (0), power percentage: (0));\n"),
                           arrayOf(JumpingSumoMoveBackwardBrick(), "Move Jumping Sumo (direction: (backward), steps: (0), power percentage: (0));\n"),
                           arrayOf(JumpingSumoAnimationsBrick(), "Start Jumping Sumo (animation: ());\n"),
                           arrayOf(JumpingSumoSoundBrick(), "Play Jumping Sumo (sound: (), volume: (0));\n"),
                           arrayOf(JumpingSumoNoSoundBrick(), "Stop Jumping Sumo sound;\n"),
                           arrayOf(JumpingSumoJumpLongBrick(), "Jump Jumping Sumo (type: (long));\n"),
                           arrayOf(JumpingSumoJumpHighBrick(), "Jump Jumping Sumo (type: (high));\n"),
                           arrayOf(JumpingSumoRotateLeftBrick(), "Turn Jumping Sumo (direction: (left), degrees: (0));\n"),
                           arrayOf(JumpingSumoRotateRightBrick(), "Turn Jumping Sumo (direction: (right), degrees: (0));\n"),
                           arrayOf(JumpingSumoTurnBrick(), "Flip Jumping Sumo;\n"),
                           arrayOf(JumpingSumoTakingPictureBrick(), "Take picture with Jumping Sumo;\n"),
                           arrayOf(PhiroMotorMoveForwardBrick(), "Move Phiro (motor: (), direction: (forward), speed percentage: (0));\n"),
                           arrayOf(PhiroMotorMoveBackwardBrick(), "Move Phiro (motor: (), direction: (backward), speed percentage: (0));\n"),
                           arrayOf(PhiroMotorStopBrick(), "Stop Phiro (motor: ());\n"),
                           arrayOf(PhiroPlayToneBrick(), "Play Phiro (tone: (0), seconds: (0));\n"),
                           arrayOf(PhiroRGBLightBrick(), "Set Phiro (light: (), color: (0));\n"),
                           arrayOf(PhiroIfLogicBeginBrick(), "If (activated phiro: ()) {\n} else {\n}\n"),
                           arrayOf(RaspiIfLogicBeginBrick(), "If (Raspberry Pi pin: ()) {\n} else {\n}\n"),
                           arrayOf(RaspiSendDigitalValueBrick(), "Set (Raspberry Pi pin: (0), value: (0));\n"),
                           arrayOf(RaspiPwmBrick(), "Set (Raspberry Pi PWM~ pin: (0), percentage: (0), Hz: (0));\n"),
                           arrayOf(StitchBrick(), "Stitch;\n"),
                           arrayOf(SetThreadColorBrick(), "Set (thread color: (0));\n"),
                           arrayOf(RunningStitchBrick(), "Start running stitch (length: (0));\n"),
                           arrayOf(ZigZagStitchBrick(), "Start zigzag stitch (length: (0), width: (0));\n"),
                           arrayOf(TripleStitchBrick(), "Start triple stitch (length: (0));\n"),
                           arrayOf(SewUpBrick(), "Sew up;\n"),
                           arrayOf(StopRunningStitchBrick(), "Stop current stitch;\n"),
                           arrayOf(WriteEmbroideryToFileBrick(), "Write embroidery data to (file: ());\n"),
                           arrayOf(AssertEqualsBrick(), "Assert equals (actual: (0), expected: (0));\n"),
                           arrayOf(AssertUserListsBrick(), "Assert lists (actual: (), expected: ());\n"),
                           arrayOf(
                               ParameterizedBrick(),
                               "For each tuple of items in selected lists stored in variables with the same name, " +
                                   "assert value equals to the expected item of reference list " +
                                   "(lists: (), value: (), reference list: ()) {\n}\n"
                           ),
                           arrayOf(FinishStageBrick(), "Finish tests;\n"),
                           arrayOf(WhenGamepadButtonBrick(), "When tapped (gamepad button: ()) {\n}\n"),
                           arrayOf(WhenTouchDownBrick(), "When stage is tapped {\n}\n"),
                           arrayOf(BroadcastReceiverBrick(), "When you receive (message: ()) {\n}\n"),
                           arrayOf(WhenConditionBrick(), "When condition becomes true (condition: ()) {\n}\n"),
                           arrayOf(WhenBounceOffBrick(), "When you bounce off (actor or object: ()) {\n}\n"),
                           arrayOf(WhenBackgroundChangesBrick(), "When background changes to (look: ()) {\n}\n"),
                           arrayOf(WhenClonedBrick(), "When you start as a clone {\n}\n"),
                           arrayOf(WhenRaspiPinChangedBrick(), "When Raspberry Pi pin changes to (pin: (), position: ()) {\n}\n"),
                           arrayOf(WhenBrick(), "When tapped {\n}\n"),
                           arrayOf(WhenStartedBrick(), "When scene starts {\n}\n"),
                           arrayOf(WhenNfcBrick(), "When NFC {\n}\n")*/
            )
        }
    }

    @Test
    fun testBasicImplementation() {
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