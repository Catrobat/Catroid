/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.io.catlang.parser.project

import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.BroadcastBrick
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
import org.catrobat.catroid.content.bricks.ExitStageBrick
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
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
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
import org.catrobat.catroid.content.bricks.SetNfcTagBrick
import org.catrobat.catroid.content.bricks.SetParticleColorBrick
import org.catrobat.catroid.content.bricks.SetPenColorBrick
import org.catrobat.catroid.content.bricks.SetPenSizeBrick
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
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
import org.catrobat.catroid.content.bricks.SewUpBrick
import org.catrobat.catroid.content.bricks.ShowBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
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
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick
import org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableToFileBrick
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException

object BrickFactory {
    private const val DIRECTION = "direction"

    @Suppress("ComplexMethod", "LongMethod")
    fun createBrickFromCatrobatLanguage(brickName: String, arguments: Map<String, String>, hasSecondaryList: Boolean): BrickBaseType {
        return when (brickName.trim()) {
            "#" -> NoteBrick()
            "Add" -> AddItemToUserListBrick()
            "Ask" -> AskBrick()
            "Ask question and store written answer to variable" -> AskSpeechBrick()
            "Assert equals" -> AssertEqualsBrick()
            "Assert lists" -> AssertUserListsBrick()
            "Become focus point with flexibility in percent" -> SetCameraFocusPointBrick()
            "Broadcast" -> BroadcastBrick()
            "Broadcast and wait" -> BroadcastWaitBrick()
            "Change" -> ChangeVariableBrick()
            "Change brightness by" -> ChangeBrightnessByNBrick()
            "Change color by" -> ChangeColorByNBrick()
            "Change size by" -> ChangeSizeByNBrick()
            "Change tempo by" -> ChangeTempoByNBrick()
            "Change transparency by" -> ChangeTransparencyByNBrick()
            "Change volume by" -> ChangeVolumeByNBrick()
            "Change x by" -> ChangeXByNBrick()
            "Change y by" -> ChangeYByNBrick()
            "Clear" -> ClearBackgroundBrick()
            "Clear graphic effects" -> ClearGraphicEffectBrick()
            "Come to front" -> ComeToFrontBrick()
            "Continue" -> SceneTransitionBrick()
            "Copy look" -> CopyLookBrick()
            "Create clone of" -> CloneBrick()
            "Delete all items" -> ClearUserListBrick()
            "Delete item at" -> DeleteItemOfUserListBrick()
            "Delete look" -> DeleteLookBrick()
            "Delete this clone" -> DeleteThisCloneBrick()
            "Edit look" -> EditLookBrick()
            "Emergency AR.Drone 2.0" -> DroneEmergencyBrick()
            "Fade particle" -> FadeParticleEffectBrick()
            "Finish stage" -> ExitStageBrick()
            "Finish tests" -> FinishStageBrick()
            "Flip AR.Drone 2.0" -> DroneFlipBrick()
            "Flip Jumping Sumo" -> JumpingSumoTurnBrick()
            "For" -> ForVariableFromToBrick()
            "For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list" -> ParameterizedBrick()
            "For each value in list" -> ForItemInUserListBrick()
            "Forever" -> ForeverBrick()
            "Get image and use as current look" -> LookRequestBrick()
            "Get image from source and use as background" -> BackgroundRequestBrick()
            "Glide to" -> GlideToBrick()
            "Go back" -> GoNStepsBackBrick()
            "Go to" -> GoToBrick()
            "If on edge, bounce" -> IfOnEdgeBounceBrick()
            "Insert" -> InsertItemIntoUserListBrick()
            "Move" -> MoveNStepsBrick()
            "Next look" -> NextLookBrick()
            "Open in browser" -> OpenUrlBrick()
            "Paint new look" -> PaintNewLookBrick()
            "Pause for" -> PauseForBeatsBrick()
            "Pen down" -> PenDownBrick()
            "Pen up" -> PenUpBrick()
            "Place at" -> PlaceAtBrick()
            "Play AR.Drone 2.0" -> DronePlayLedAnimationBrick()
            "Play EV3 tone" -> LegoEv3PlayToneBrick()
            "Play Jumping Sumo" -> JumpingSumoSoundBrick()
            "Play NXT tone" -> LegoNxtPlayToneBrick()
            "Play Phiro" -> PhiroPlayToneBrick()
            "Point in direction" -> PointInDirectionBrick()
            "Point towards" -> PointToBrick()
            "Previous look" -> PreviousLookBrick()
            "Read from file" -> ReadVariableFromFileBrick()
            "Repeat" -> RepeatBrick()
            "Repeat until" -> RepeatUntilBrick()
            "Replace" -> ReplaceItemInUserListBrick()
            "Reset timer" -> ResetTimerBrick()
            "Return" -> ReportBrick()
            "Say" -> SayBubbleBrick()
            "Say text for seconds" -> SayForBubbleBrick()
            "Send web request" -> WebRequestBrick()
            "Set EV3" -> LegoEv3MotorMoveBrick()
            "Set EV3 LED" -> LegoEv3SetLedBrick()
            "Set gravity for all actors and objects to" -> SetGravityBrick()
            "Set next NFC tag" -> SetNfcTagBrick()
            "Set NXT" -> LegoNxtMotorMoveBrick()
            "Set pen color" -> SetPenColorBrick()
            "Set Phiro" -> PhiroRGBLightBrick()
            "Set velocity to" -> SetVelocityBrick()
            "Sew up" -> SewUpBrick()
            "Single tap at" -> TapAtBrick()
            "Speak" -> SpeakBrick()
            "Speak and wait" -> SpeakAndWaitBrick()
            "Stamp" -> StampBrick()
            "Start Jumping Sumo" -> JumpingSumoAnimationsBrick()
            "Start running stitch" -> RunningStitchBrick()
            "Start sound and skip seconds" -> PlaySoundAtBrick()
            "Start sound and wait" -> PlaySoundAndWaitBrick()
            "Start triple stitch" -> TripleStitchBrick()
            "Start zigzag stitch" -> ZigZagStitchBrick()
            "Stitch" -> StitchBrick()
            "Stop all sounds" -> StopAllSoundsBrick()
            "Stop current stitch" -> StopRunningStitchBrick()
            "Stop EV3" -> LegoEv3MotorStopBrick()
            "Stop Jumping Sumo sound" -> JumpingSumoNoSoundBrick()
            "Stop NXT" -> LegoNxtMotorStopBrick()
            "Stop Phiro" -> PhiroMotorStopBrick()
            "Store column of comma-separated values to list" -> StoreCSVIntoUserListBrick()
            "Switch AR.Drone 2.0 camera" -> DroneSwitchCameraBrick()
            "Take off / land AR.Drone 2.0" -> DroneTakeOffLandBrick()
            "Take picture with Jumping Sumo" -> JumpingSumoTakingPictureBrick()
            "Think" -> ThinkBubbleBrick()
            "Think text for seconds" -> ThinkForBubbleBrick()
            "Touch at position and slide to position in seconds" -> TouchAndSlideBrick()
            "Touch at position for seconds" -> TapForBrick()
            "Turn EV3" -> LegoEv3MotorTurnAngleBrick()
            "Turn NXT" -> LegoNxtMotorTurnAngleBrick()
            "Use" -> ChooseCameraBrick()
            "Vibrate for" -> VibrationBrick()
            "Wait" -> WaitBrick()
            "Wait until" -> WaitUntilBrick()
            "Wait until all other scripts have stopped" -> WaitTillIdleBrick()
            "Write embroidery data to" -> WriteEmbroideryToFileBrick()
            "Write to file" -> WriteVariableToFileBrick()
            "Hide" -> createHideBrick(arguments)
            "If" -> createIfBrick(arguments, hasSecondaryList)
            "Jump Jumping Sumo" -> createJumpJumpingSumoBrick(arguments)
            "Move AR.Drone 2.0" -> createMoveARDrone2Brick(arguments)
            "Move Jumping Sumo" -> createMoveJumpingSumoBrick(arguments)
            "Move Phiro" -> createMovePhiroBrick(arguments)
            "Play" -> createPlayBrick(arguments)
            "Read from device" -> createReadFromDeviceBrick(arguments)
            "Set" -> createSetBrick(arguments)
            "Set Arduino" -> createSetArduinoBrick(arguments)
            "Set background and wait" -> createSetBackgroundAndWaitBrick(arguments)
            "Set background to" -> createSetBackgroundToBrick(arguments)
            "Show" -> createShowBrick(arguments)
            "Spin" -> createSpinBrick(arguments)
            "Start" -> createStartBrick(arguments)
            "Stop" -> createStopBrick(arguments)
            "Switch to" -> createSwitchToBrick(arguments)
            "Turn" -> createTurnBrick(arguments)
            "Turn AR.Drone 2.0" -> createTurnARDrone2Brick(arguments)
            "Turn Jumping Sumo" -> createTurnJumpingSumoBrick(arguments)
            "Write on device" -> createWriteOnDeviceBrick(arguments)

            else -> throw CatrobatLanguageParsingException("Unknown brick: ${brickName.trim()}")
        }
    }

    private fun createHideBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.isNullOrEmpty()) {
            return HideBrick()
        }
        return HideTextBrick()
    }

    private fun createIfBrick(arguments: Map<String, String>, hasSecondaryList: Boolean): BrickBaseType {
        if (arguments.containsKey("activated phiro")) {
            return PhiroIfLogicBeginBrick()
        }
        if (arguments.containsKey("Raspberry Pi pin")) {
            return RaspiIfLogicBeginBrick()
        }
        if (arguments.containsKey("condition")) {
            if (hasSecondaryList) {
                return IfLogicBeginBrick()
            }
            return IfThenLogicBeginBrick()
        }
        throw CatrobatLanguageParsingException("If requires either parameter 'activated phiro', 'Raspberry Pi pin' or 'condition'")
    }

    private fun createJumpJumpingSumoBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("type")) {
            if (arguments["type"] == "high") {
                return JumpingSumoJumpHighBrick()
            }
            if (arguments["type"] == "long") {
                return JumpingSumoJumpLongBrick()
            }
        }
        throw CatrobatLanguageParsingException("Jump Jumping Sumo may only have the parameter 'type' with the values 'high' or 'long'")
    }

    private fun createMoveARDrone2Brick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey(DIRECTION)) {
            if (arguments[DIRECTION] == "up") {
                return DroneMoveUpBrick()
            }
            if (arguments[DIRECTION] == "down") {
                return DroneMoveDownBrick()
            }
            if (arguments[DIRECTION] == "left") {
                return DroneMoveLeftBrick()
            }
            if (arguments[DIRECTION] == "right") {
                return DroneMoveRightBrick()
            }
            if (arguments[DIRECTION] == "forward") {
                return DroneMoveForwardBrick()
            }
            if (arguments[DIRECTION] == "backward") {
                return DroneMoveBackwardBrick()
            }
        }
        throw CatrobatLanguageParsingException("Move AR.Drone 2.0 requires parameter 'direction' with the either one of the following values: 'up', 'down', 'left', 'right', 'forward' or 'backward'")
    }

    private fun createMoveJumpingSumoBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey(DIRECTION)) {
            if (arguments[DIRECTION] == "forward") {
                return JumpingSumoMoveForwardBrick()
            }
            if (arguments[DIRECTION] == "backward") {
                return JumpingSumoMoveBackwardBrick()
            }
        }
        throw CatrobatLanguageParsingException("Move Jumping Sumo requires parameter 'direction' with the either one of the following values: 'forward' or 'backward'")
    }

    private fun createMovePhiroBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey(DIRECTION)) {
            if (arguments[DIRECTION] == "forward") {
                return PhiroMotorMoveForwardBrick()
            }
            if (arguments[DIRECTION] == "backward") {
                return PhiroMotorMoveBackwardBrick()
            }
        }
        throw CatrobatLanguageParsingException("Move Phiro requires parameter 'direction' with the either one of the following values: 'forward' or 'backward'")
    }

    private fun createPlayBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("note")) {
            return PlayNoteForBeatsBrick()
        }
        if (arguments.containsKey("drum")) {
            return PlayDrumForBeatsBrick()
        }
        throw CatrobatLanguageParsingException("Play requires either parameter 'note' or 'drum'")
    }

    private fun createReadFromDeviceBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("variable")) {
            return ReadVariableFromDeviceBrick()
        }
        if (arguments.containsKey("list")) {
            return ReadListFromDeviceBrick()
        }
        throw CatrobatLanguageParsingException("Read from device requires either parameter 'variable' or 'list'")
    }

    @Suppress("ComplexMethod")
    private fun createSetBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("bounce factor percentage")) {
            return SetBounceBrick()
        }
        if (arguments.containsKey("particle color")) {
            return SetParticleColorBrick()
        }
        if (arguments.containsKey("brightness percentage")) {
            return SetBrightnessBrick()
        }
        if (arguments.containsKey("transparency percentage")) {
            return SetTransparencyBrick()
        }
        if (arguments.containsKey("variable")) {
            return SetVariableBrick()
        }
        if (arguments.containsKey("x") && arguments.size == 1) {
            return SetXBrick()
        }
        if (arguments.containsKey("pen size")) {
            return SetPenSizeBrick()
        }
        if (arguments.containsKey("text") && arguments.containsKey("x") && arguments.containsKey("y")) {
            return SetTextBrick()
        }
        if (arguments.containsKey("Raspberry Pi PWM~ pin")) {
            return RaspiPwmBrick()
        }
        if (arguments.containsKey("mass in kilograms")) {
            return SetMassBrick()
        }
        if (arguments.containsKey("color")) {
            return SetColorBrick()
        }
        if (arguments.containsKey("y") && arguments.size == 1) {
            return SetYBrick()
        }
        if (arguments.containsKey("volume percentage")) {
            return SetVolumeToBrick()
        }
        if (arguments.containsKey("tempo")) {
            return SetTempoBrick()
        }
        if (arguments.containsKey("instrument")) {
            return SetInstrumentBrick()
        }
        if (arguments.containsKey("thread color")) {
            return SetThreadColorBrick()
        }
        if (arguments.containsKey("Raspberry Pi pin")) {
            return RaspiSendDigitalValueBrick()
        }
        if (arguments.containsKey("motion type")) {
            return SetPhysicsObjectTypeBrick()
        }
        if (arguments.containsKey("rotation style")) {
            return SetRotationStyleBrick()
        }
        if (arguments.containsKey("size percentage")) {
            return SetSizeToBrick()
        }
        if (arguments.containsKey("friction percentage")) {
            return SetFrictionBrick()
        }
        throw CatrobatLanguageParsingException("Set requires either parameter 'bounce factor percentage', 'particle color', 'brightness percentage', 'transparency percentage', 'variable', 'x', 'pen size', 'text', 'Raspberry Pi PWM~ pin', 'mass in kilograms', 'color', 'y', 'volume percentage', 'tempo', 'instrument', 'thread color', 'Raspberry Pi pin', 'motion type', 'rotation style', 'size percentage' or 'friction percentage'")
    }

    private fun createSetArduinoBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("digital pin")) {
            return ArduinoSendDigitalValueBrick()
        }
        if (arguments.containsKey("PWM~ pin")) {
            return ArduinoSendPWMValueBrick()
        }
        throw CatrobatLanguageParsingException("Set Arduino requires either parameter 'digital pin' or 'PWM~ pin'")
    }

    private fun createSetBackgroundAndWaitBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("look by number")) {
            return SetBackgroundByIndexAndWaitBrick()
        }
        if (arguments.containsKey("look")) {
            return SetBackgroundAndWaitBrick()
        }
        throw CatrobatLanguageParsingException("Set background and wait requires either parameter 'look by number' or 'look'")
    }

    private fun createSetBackgroundToBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("look by number")) {
            return SetBackgroundByIndexBrick()
        }
        if (arguments.containsKey("look")) {
            return SetBackgroundBrick()
        }
        throw CatrobatLanguageParsingException("Set background to requires either parameter 'look by number' or 'look'")
    }

    private fun createShowBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.isNullOrEmpty()) {
            return ShowBrick()
        }
        if (arguments.containsKey("variable") && arguments.containsKey("x") && arguments.containsKey("y")) {
            if (arguments.containsKey("size") && arguments.containsKey("color") && arguments.containsKey("alignment")) {
                return ShowTextColorSizeAlignmentBrick()
            }
            return ShowTextBrick()
        }
        throw CatrobatLanguageParsingException("Show requires either no parameters or 'variable', 'x' and 'y' or 'variable', 'x', 'y', 'size', 'color' and 'alignment'")
    }

    private fun createSpinBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey(DIRECTION)) {
            if (arguments[DIRECTION] == "left") {
                return TurnLeftSpeedBrick()
            }
            if (arguments[DIRECTION] == "right") {
                return TurnRightSpeedBrick()
            }
        }
        throw CatrobatLanguageParsingException("Spin requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
    }

    private fun createStartBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("scene")) {
            return SceneStartBrick()
        }
        if (arguments.containsKey("sound")) {
            return PlaySoundBrick()
        }
        throw CatrobatLanguageParsingException("Start requires either parameter 'scene' or 'sound'")
    }

    private fun createStopBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("sound")) {
            return StopSoundBrick()
        }
        if (arguments.containsKey("script")) {
            return StopScriptBrick()
        }
        throw CatrobatLanguageParsingException("Stop requires either parameter 'sound' or 'script'")
    }

    private fun createSwitchToBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("look")) {
            return SetLookBrick()
        }
        if (arguments.containsKey("look by number")) {
            return SetLookByIndexBrick()
        }
        throw CatrobatLanguageParsingException("Switch to requires either parameter 'look' or 'look by number'")
    }

    private fun createTurnBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("camera")) {
            return CameraBrick()
        }
        if (arguments.containsKey("flashlight")) {
            return FlashBrick()
        }
        if (arguments.containsKey("particle effect additivity")) {
            return ParticleEffectAdditivityBrick()
        }
        if (arguments.containsKey(DIRECTION) && arguments.containsKey("degrees")) {
            if (arguments[DIRECTION] == "left") {
                return TurnLeftBrick()
            }
            if (arguments[DIRECTION] == "right") {
                return TurnRightBrick()
            }
            throw CatrobatLanguageParsingException("Turn requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
        }
        throw CatrobatLanguageParsingException("Turn requires either parameter 'camera', 'flashlight', 'particle effect additivity' or 'direction' and 'degrees'")
    }

    private fun createTurnARDrone2Brick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey(DIRECTION)) {
            if (arguments[DIRECTION] == "left") {
                return DroneTurnLeftBrick()
            }
            if (arguments[DIRECTION] == "right") {
                return DroneTurnRightBrick()
            }
        }
        throw CatrobatLanguageParsingException("Turn AR.Drone 2.0 requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
    }

    private fun createTurnJumpingSumoBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey(DIRECTION)) {
            if (arguments[DIRECTION] == "left") {
                return JumpingSumoRotateLeftBrick()
            }
            if (arguments[DIRECTION] == "right") {
                return JumpingSumoRotateRightBrick()
            }
        }
        throw CatrobatLanguageParsingException("Turn Jumping Sumo requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
    }

    private fun createWriteOnDeviceBrick(arguments: Map<String, String>): BrickBaseType {
        if (arguments.containsKey("variable")) {
            return WriteVariableOnDeviceBrick()
        }
        if (arguments.containsKey("list")) {
            return WriteListOnDeviceBrick()
        }
        throw CatrobatLanguageParsingException("Write on device requires either parameter 'variable' or 'list'")
    }
}
