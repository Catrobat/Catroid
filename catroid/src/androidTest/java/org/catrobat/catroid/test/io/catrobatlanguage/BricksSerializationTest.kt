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

import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTempoByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.CopyLookBrick
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick
import org.catrobat.catroid.content.bricks.FinishStageBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.OpenUrlBrick
import org.catrobat.catroid.content.bricks.PaintNewLookBrick
import org.catrobat.catroid.content.bricks.PauseForBeatsBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick
import org.catrobat.catroid.content.bricks.PlaySoundAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.RaspiPwmBrick
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.ReportBrick
import org.catrobat.catroid.content.bricks.RunningStitchBrick
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
import org.catrobat.catroid.content.bricks.SetVelocityBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.SewUpBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick
import org.catrobat.catroid.content.bricks.TripleStitchBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick
import org.catrobat.catroid.content.bricks.UserDataBrick
import org.catrobat.catroid.content.bricks.UserListBrick
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.VisualPlacementBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.catrobat.catroid.uiespresso.content.messagecontainer.BroadcastAndWaitBrickMessageContainerTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.Serializable

@RunWith(Parameterized::class)
class BricksSerializationTest(
    private val brick: Brick,
    private val expectedOutput: String
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): List<Array<out Serializable>> {
            val listOf = listOf(
                arrayOf(BroadcastBrick("test"), "Broadcast (message: ('test'));\n"),
                arrayOf(BroadcastBrick(), "Broadcast (message: ());\n"), // TODO: this or
                arrayOf(BroadcastBrick(), "Broadcast (message: (''));\n"), // TODO: this?
                arrayOf(
                    BroadcastWaitBrick("test"),
                    "Broadcast and wait (message: ('test'));\n"
                ),
                arrayOf(
                    ArduinoSendDigitalValueBrick(3, 1),
                    "Set Arduino (digital pin: (3), value: (1));\n"
                ),
                // TODO: detailed CloneBrick Test
                arrayOf(CloneBrick(), "Create clone of (actor or object: ());\n"),
                arrayOf(DeleteThisCloneBrick(), "Delete this clone;\n"),
                // TODO: clarify with newline
                arrayOf(NoteBrick("a comment"), "// a comment\n"),
                arrayOf(ForeverBrick(), "Forever {\n}\n"),

                // TODO: clarify default 0
                arrayOf(IfLogicBeginBrick(), "If (condition: (0)) {\n} else {\n}\n"),
                arrayOf(IfThenLogicBeginBrick(), "If (condition: (0)) {\n}\n"),
                arrayOf(WaitUntilBrick(), "Wait until (condition: (0));\n"),
                arrayOf(RepeatBrick(), "Repeat (times: (0)) {\n}\n"),
                arrayOf(RepeatUntilBrick(), "Repeat until (condition: (0)) {\n}\n"),
                arrayOf(ForVariableFromToBrick(), "For (value: (0), from: (0), to: (0)) {\n}\n"),
                // TODO: UserDataBrick
                arrayOf(
                    ForItemInUserListBrick(),
                    "For each value in list (value: (0), list: (0)) {\n}\n"
                ),
                // TODO: shoud testscene be escaped?
                arrayOf(SceneTransitionBrick("testscene"), "Continue (scene: (testscene));\n"),
                arrayOf(SceneTransitionBrick(), "Continue (scene: ());\n"),
                arrayOf(SceneStartBrick("testscene"), "Start (scene: (testscene));\n"),
                arrayOf(SceneStartBrick(), "Start (scene: ());\n"),
                arrayOf(FinishStageBrick(), "Finish stage;\n"),
                arrayOf(StopScriptBrick(), "Stop (script: (this script));\n"),
                arrayOf(StopScriptBrick(0), "Stop (script: (this script));\n"),
                arrayOf(StopScriptBrick(1), "Stop (script: (all scripts));\n"),
                arrayOf(StopScriptBrick(2), "Stop (script: (other scripts of this actor or " +
                    "object));\n"),

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
            return listOf
        }
    }

    @Test
    fun testBasicCatrobatLanguageImplementation() {
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        assertEquals(expectedOutput, actualOutput)
    }
}