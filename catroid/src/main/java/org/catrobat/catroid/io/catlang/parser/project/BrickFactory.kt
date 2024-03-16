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

package org.catrobat.catroid.io.catlang.parser.project

import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.RaspiInterruptScript
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenBackgroundChangesScript
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.WhenClonedScript
import org.catrobat.catroid.content.WhenConditionScript
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.catrobat.catroid.content.WhenNfcScript
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.WhenTouchDownScript
import org.catrobat.catroid.content.bricks.*
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException

object BrickFactory {
    fun createBrickFromCatrobatLanguage(brickName: String, arguments: Map<String, String>, hasSecondaryList: Boolean) : BrickBaseType {
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

            else -> throw CatrobatLanguageParsingException("Unknown brick $brickName")
        }
    }

    private fun createHideBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.isNullOrEmpty()) {
            return HideBrick()
        }
        return HideTextBrick()
    }

    private fun createIfBrick(arguments: Map<String, String>, hasSecondaryList: Boolean) : BrickBaseType {
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

    private fun createJumpJumpingSumoBrick(arguments: Map<String, String>) : BrickBaseType {
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

    private fun createMoveARDrone2Brick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("direction")) {
            if (arguments["direction"] == "up") {
                return DroneMoveUpBrick()
            }
            if (arguments["direction"] == "down") {
                return DroneMoveDownBrick()
            }
            if (arguments["direction"] == "left") {
                return DroneMoveLeftBrick()
            }
            if (arguments["direction"] == "right") {
                return DroneMoveRightBrick()
            }
            if (arguments["direction"] == "forward") {
                return DroneMoveForwardBrick()
            }
            if (arguments["direction"] == "backward") {
                return DroneMoveBackwardBrick()
            }

        }
        throw CatrobatLanguageParsingException("Move AR.Drone 2.0 requires parameter 'direction' with the either one of the following values: 'up', 'down', 'left', 'right', 'forward' or 'backward'")
    }

    private fun createMoveJumpingSumoBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("direction")) {
            if (arguments["direction"] == "forward") {
                return JumpingSumoMoveForwardBrick()
            }
            if (arguments["direction"] == "backward") {
                return JumpingSumoMoveBackwardBrick()
            }
        }
        throw CatrobatLanguageParsingException("Move Jumping Sumo requires parameter 'direction' with the either one of the following values: 'forward' or 'backward'")
    }

    private fun createMovePhiroBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("direction")) {
            if (arguments["direction"] == "forward") {
                return PhiroMotorMoveForwardBrick()
            }
            if (arguments["direction"] == "backward") {
                return PhiroMotorMoveBackwardBrick()
            }
        }
        throw CatrobatLanguageParsingException("Move Phiro requires parameter 'direction' with the either one of the following values: 'forward' or 'backward'")
    }

    private fun createPlayBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("note")) {
            return PlayNoteForBeatsBrick()
        }
        if (arguments.containsKey("drum")) {
            return PlayDrumForBeatsBrick()
        }
        throw CatrobatLanguageParsingException("Play requires either parameter 'note' or 'drum'")
    }

    private fun createReadFromDeviceBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("variable")) {
            return ReadVariableFromDeviceBrick()
        }
        if (arguments.containsKey("list")) {
            return ReadListFromDeviceBrick()
        }
        throw CatrobatLanguageParsingException("Read from device requires either parameter 'variable' or 'list'")
    }

    private fun createSetBrick(arguments: Map<String, String>) : BrickBaseType {
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
        if (arguments.containsKey("pen color code")) {
            return SetPenColorBrick()
        }
        if (arguments.containsKey("friction percentage")) {
            return SetFrictionBrick()
        }
        throw CatrobatLanguageParsingException("Set requires either parameter 'bounce factor percentage', 'particle color', 'brightness percentage', 'transparency percentage', 'variable', 'x', 'pen size', 'text', 'Raspberry Pi PWM~ pin', 'mass in kilograms', 'color', 'y', 'volume percentage', 'tempo', 'instrument', 'thread color', 'Raspberry Pi pin', 'motion type', 'rotation style', 'size percentage', 'pen color code' or 'friction percentage'")
    }

    private fun createSetArduinoBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("digital pin")) {
            return ArduinoSendDigitalValueBrick()
        }
        if (arguments.containsKey("PWM~ pin")) {
            return ArduinoSendPWMValueBrick()
        }
        throw CatrobatLanguageParsingException("Set Arduino requires either parameter 'digital pin' or 'PWM~ pin'")
    }

    private fun createSetBackgroundAndWaitBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("look by number")) {
            return SetBackgroundByIndexAndWaitBrick()
        }
        if (arguments.containsKey("look")) {
            return SetBackgroundAndWaitBrick()
        }
        throw CatrobatLanguageParsingException("Set background and wait requires either parameter 'look by number' or 'look'")
    }

    private fun createSetBackgroundToBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("look by number")) {
            return SetBackgroundByIndexBrick()
        }
        if (arguments.containsKey("look")) {
            return SetBackgroundBrick()
        }
        throw CatrobatLanguageParsingException("Set background to requires either parameter 'look by number' or 'look'")
    }

    private fun createShowBrick(arguments: Map<String, String>) : BrickBaseType {
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

    private fun createSpinBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("direction")) {
            if (arguments["direction"] == "left") {
                return TurnLeftSpeedBrick()
            }
            if (arguments["direction"] == "right") {
                return TurnRightSpeedBrick()
            }
        }
        throw CatrobatLanguageParsingException("Spin requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
    }

    private fun createStartBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("scene")) {
            return SceneStartBrick()
        }
        if (arguments.containsKey("sound")) {
            return PlaySoundBrick()
        }
        throw CatrobatLanguageParsingException("Start requires either parameter 'scene' or 'sound'")
    }

    private fun createStopBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("sound")) {
            return StopSoundBrick()
        }
        if (arguments.containsKey("script")) {
            return StopScriptBrick()
        }
        throw CatrobatLanguageParsingException("Stop requires either parameter 'sound' or 'script'")
    }

    private fun createSwitchToBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("look")) {
            return SetLookBrick()
        }
        if (arguments.containsKey("look by number")) {
            return SetLookByIndexBrick()
        }
        throw CatrobatLanguageParsingException("Switch to requires either parameter 'look' or 'look by number'")
    }

    private fun createTurnBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("camera")) {
            return CameraBrick()
        }
        if (arguments.containsKey("flashlight")) {
            return FlashBrick()
        }
        if (arguments.containsKey("particle effect additivity")) {
            return ParticleEffectAdditivityBrick()
        }
        if (arguments.containsKey("direction") && arguments.containsKey("degrees")) {
            if (arguments["direction"] == "left") {
                return TurnLeftBrick()
            }
            if (arguments["direction"] == "right") {
                return TurnRightBrick()
            }
            throw CatrobatLanguageParsingException("Turn requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
        }
        throw CatrobatLanguageParsingException("Turn requires either parameter 'camera', 'flashlight', 'particle effect additivity' or 'direction' and 'degrees'")
    }

    private fun createTurnARDrone2Brick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("direction")) {
            if (arguments["direction"] == "left") {
                return DroneTurnLeftBrick()
            }
            if (arguments["direction"] == "right") {
                return DroneTurnRightBrick()
            }
        }
        throw CatrobatLanguageParsingException("Turn AR.Drone 2.0 requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
    }

    private fun createTurnJumpingSumoBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("direction")) {
            if (arguments["direction"] == "left") {
                return JumpingSumoRotateLeftBrick()
            }
            if (arguments["direction"] == "right") {
                return JumpingSumoRotateRightBrick()
            }
        }
        throw CatrobatLanguageParsingException("Turn Jumping Sumo requires parameter 'direction' with the either one of the following values: 'left' or 'right'")
    }

    private fun createWriteOnDeviceBrick(arguments: Map<String, String>) : BrickBaseType {
        if (arguments.containsKey("variable")) {
            return WriteVariableOnDeviceBrick()
        }
        if (arguments.containsKey("list")) {
            return WriteListOnDeviceBrick()
        }
        throw CatrobatLanguageParsingException("Write on device requires either parameter 'variable' or 'list'")
    }


}