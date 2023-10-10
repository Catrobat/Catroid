/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.test.content.bricks

import android.content.Context
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.junit.Before
import kotlin.Throws
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.ComeToFrontBrick
import org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.SetVelocityBrick
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.catrobat.catroid.content.bricks.SetMassBrick
import org.catrobat.catroid.content.bricks.SetBounceBrick
import org.catrobat.catroid.content.bricks.SetFrictionBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.StitchBrick
import org.catrobat.catroid.content.bricks.SetThreadColorBrick
import org.catrobat.catroid.content.bricks.RunningStitchBrick
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick
import org.catrobat.catroid.content.bricks.TripleStitchBrick
import org.catrobat.catroid.content.bricks.SewUpBrick
import org.catrobat.catroid.content.bricks.StopRunningStitchBrick
import org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.content.bricks.WhenBrick
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenClonedBrick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.HideBrick
import org.catrobat.catroid.content.bricks.ShowBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.SayBubbleBrick
import org.catrobat.catroid.content.bricks.SayForBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick
import org.catrobat.catroid.content.bricks.SetParticleColorBrick
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.PaintNewLookBrick
import org.catrobat.catroid.content.bricks.EditLookBrick
import org.catrobat.catroid.content.bricks.CopyLookBrick
import org.catrobat.catroid.content.bricks.DeleteLookBrick
import org.catrobat.catroid.content.bricks.OpenUrlBrick
import org.catrobat.catroid.content.bricks.PenDownBrick
import org.catrobat.catroid.content.bricks.PenUpBrick
import org.catrobat.catroid.content.bricks.SetPenSizeBrick
import org.catrobat.catroid.content.bricks.SetPenColorBrick
import org.catrobat.catroid.content.bricks.StampBrick
import org.catrobat.catroid.content.bricks.ClearBackgroundBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.PlaySoundAtBrick
import org.catrobat.catroid.content.bricks.StopSoundBrick
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick
import org.catrobat.catroid.content.bricks.SetInstrumentBrick
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick
import org.catrobat.catroid.content.bricks.SetTempoBrick
import org.catrobat.catroid.content.bricks.ChangeTempoByNBrick
import org.catrobat.catroid.content.bricks.PauseForBeatsBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.ExitStageBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick
import org.catrobat.catroid.content.bricks.TapAtBrick
import org.catrobat.catroid.content.bricks.TapForBrick
import org.catrobat.catroid.content.bricks.TouchAndSlideBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.HideTextBrick
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick
import org.catrobat.catroid.content.bricks.WriteVariableToFileBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick
import org.catrobat.catroid.content.bricks.ClearUserListBrick
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick
import org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick
import org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick
import org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.ResetTimerBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick
import org.catrobat.catroid.content.bricks.DroneFlipBrick
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick
import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick
import org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.RaspiPwmBrick
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.FinishStageBrick
import org.junit.After
import org.junit.Test
import java.lang.Exception
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class BrickCategoryTest {
    private val speechAISettings: List<String> = ArrayList(
        Arrays.asList(
            SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
            SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
        )
    )
    @JvmField
    @Parameterized.Parameter
    var category: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var expectedClasses: List<Class<*>>? = null
    private var categoryBricksFactory: CategoryBricksFactory? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        sharedPreferencesEditor.clear()
        // The speech AI settings have to be activated here, because these bricks have no own
        // brick category.
        for (setting in speechAISettings) {
            sharedPreferencesEditor.putBoolean(setting, true)
        }
        sharedPreferencesEditor.commit()
        createProject(ApplicationProvider.getApplicationContext())
        categoryBricksFactory = CategoryBricksFactory()
    }

    @After
    fun tearDown() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        sharedPreferencesEditor.clear().commit()
    }

    fun createProject(context: Context?) {
        val project = Project(context, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        script.addBrick(SetXBrick())
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    @Test
    fun testBrickCategory() {
        val categoryBricks = categoryBricksFactory!!.getBricks(
            category!!, false,
            ApplicationProvider.getApplicationContext()
        )
        val brickClasses: MutableList<Class<*>> = ArrayList()
        for (brick in categoryBricks) {
            brickClasses.add(brick.javaClass)
        }
        Assert.assertEquals(expectedClasses, brickClasses)
    }

    companion object {


        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "Motion", Arrays.asList(
                            PlaceAtBrick::class.java,
                            SetXBrick::class.java,
                            SetYBrick::class.java,
                            ChangeXByNBrick::class.java,
                            ChangeYByNBrick::class.java,
                            GoToBrick::class.java,
                            IfOnEdgeBounceBrick::class.java,
                            MoveNStepsBrick::class.java,
                            TurnLeftBrick::class.java,
                            TurnRightBrick::class.java,
                            PointInDirectionBrick::class.java,
                            PointToBrick::class.java,
                            SetRotationStyleBrick::class.java,
                            GlideToBrick::class.java,
                            GoNStepsBackBrick::class.java,
                            ComeToFrontBrick::class.java,
                            SetCameraFocusPointBrick::class.java,
                            VibrationBrick::class.java,
                            SetPhysicsObjectTypeBrick::class.java,
                            WhenBounceOffBrick::class.java,
                            SetVelocityBrick::class.java,
                            TurnLeftSpeedBrick::class.java,
                            TurnRightSpeedBrick::class.java,
                            SetGravityBrick::class.java,
                            SetMassBrick::class.java,
                            SetBounceBrick::class.java,
                            SetFrictionBrick::class.java,
                            FadeParticleEffectBrick::class.java
                        )
                    ), arrayOf(
                        "Embroidery", Arrays.asList(
                            StitchBrick::class.java,
                            SetThreadColorBrick::class.java,
                            RunningStitchBrick::class.java,
                            ZigZagStitchBrick::class.java,
                            TripleStitchBrick::class.java,
                            SewUpBrick::class.java,
                            StopRunningStitchBrick::class.java,
                            WriteEmbroideryToFileBrick::class.java
                        )
                    ), arrayOf(
                        "Event", Arrays.asList(
                            WhenStartedBrick::class.java,
                            WhenBrick::class.java,
                            WhenTouchDownBrick::class.java,
                            BroadcastReceiverBrick::class.java,
                            BroadcastBrick::class.java,
                            BroadcastWaitBrick::class.java,
                            WhenConditionBrick::class.java,
                            WhenBounceOffBrick::class.java,
                            WhenBackgroundChangesBrick::class.java,
                            WhenClonedBrick::class.java,
                            CloneBrick::class.java,
                            DeleteThisCloneBrick::class.java
                        )
                    ), arrayOf(
                        "Looks", Arrays.asList(
                            SetLookBrick::class.java,
                            SetLookByIndexBrick::class.java,
                            NextLookBrick::class.java,
                            PreviousLookBrick::class.java,
                            SetSizeToBrick::class.java,
                            ChangeSizeByNBrick::class.java,
                            HideBrick::class.java,
                            ShowBrick::class.java,
                            AskBrick::class.java,
                            SayBubbleBrick::class.java,
                            SayForBubbleBrick::class.java,
                            ThinkBubbleBrick::class.java,
                            ThinkForBubbleBrick::class.java,
                            ShowTextBrick::class.java,
                            ShowTextColorSizeAlignmentBrick::class.java,
                            SetTransparencyBrick::class.java,
                            ChangeTransparencyByNBrick::class.java,
                            SetBrightnessBrick::class.java,
                            ChangeBrightnessByNBrick::class.java,
                            SetColorBrick::class.java,
                            ChangeColorByNBrick::class.java,
                            FadeParticleEffectBrick::class.java,
                            ParticleEffectAdditivityBrick::class.java,
                            SetParticleColorBrick::class.java,
                            ClearGraphicEffectBrick::class.java,
                            SetCameraFocusPointBrick::class.java,
                            WhenBackgroundChangesBrick::class.java,
                            SetBackgroundBrick::class.java,
                            SetBackgroundByIndexBrick::class.java,
                            SetBackgroundAndWaitBrick::class.java,
                            SetBackgroundByIndexAndWaitBrick::class.java,
                            CameraBrick::class.java,
                            ChooseCameraBrick::class.java,
                            FlashBrick::class.java,
                            LookRequestBrick::class.java,
                            PaintNewLookBrick::class.java,
                            EditLookBrick::class.java,
                            CopyLookBrick::class.java,
                            DeleteLookBrick::class.java,
                            OpenUrlBrick::class.java
                        )
                    ), arrayOf(
                        "Pen", Arrays.asList(
                            PenDownBrick::class.java,
                            PenUpBrick::class.java,
                            SetPenSizeBrick::class.java,
                            SetPenColorBrick::class.java,
                            StampBrick::class.java,
                            ClearBackgroundBrick::class.java
                        )
                    ), arrayOf(
                        "Sound", Arrays.asList(
                            PlaySoundBrick::class.java,
                            PlaySoundAndWaitBrick::class.java,
                            PlaySoundAtBrick::class.java,
                            StopSoundBrick::class.java,
                            StopAllSoundsBrick::class.java,
                            SetVolumeToBrick::class.java,
                            ChangeVolumeByNBrick::class.java,
                            SpeakBrick::class.java,
                            SpeakAndWaitBrick::class.java,
                            AskSpeechBrick::class.java,
                            StartListeningBrick::class.java,
                            SetListeningLanguageBrick::class.java,
                            SetInstrumentBrick::class.java,
                            PlayNoteForBeatsBrick::class.java,
                            PlayDrumForBeatsBrick::class.java,
                            SetTempoBrick::class.java,
                            ChangeTempoByNBrick::class.java,
                            PauseForBeatsBrick::class.java
                        )
                    ), arrayOf(
                        "Control", Arrays.asList(
                            WaitBrick::class.java,
                            NoteBrick::class.java,
                            ForeverBrick::class.java,
                            IfLogicBeginBrick::class.java,
                            IfThenLogicBeginBrick::class.java,
                            WaitUntilBrick::class.java,
                            RepeatBrick::class.java,
                            RepeatUntilBrick::class.java,
                            ForVariableFromToBrick::class.java,
                            ForItemInUserListBrick::class.java,
                            SceneTransitionBrick::class.java,
                            SceneStartBrick::class.java,
                            ExitStageBrick::class.java,
                            StopScriptBrick::class.java,
                            WaitTillIdleBrick::class.java,
                            WhenClonedBrick::class.java,
                            CloneBrick::class.java,
                            DeleteThisCloneBrick::class.java,
                            BroadcastReceiverBrick::class.java,
                            BroadcastBrick::class.java,
                            BroadcastWaitBrick::class.java,
                            TapAtBrick::class.java,
                            TapForBrick::class.java,
                            TouchAndSlideBrick::class.java,
                            OpenUrlBrick::class.java
                        )
                    ), arrayOf(
                        "Data", Arrays.asList(
                            SetVariableBrick::class.java,
                            ChangeVariableBrick::class.java,
                            ShowTextBrick::class.java,
                            ShowTextColorSizeAlignmentBrick::class.java,
                            HideTextBrick::class.java,
                            WriteVariableOnDeviceBrick::class.java,
                            ReadVariableFromDeviceBrick::class.java,
                            WriteVariableToFileBrick::class.java,
                            ReadVariableFromFileBrick::class.java,
                            AddItemToUserListBrick::class.java,
                            DeleteItemOfUserListBrick::class.java,
                            ClearUserListBrick::class.java,
                            InsertItemIntoUserListBrick::class.java,
                            ReplaceItemInUserListBrick::class.java,
                            WriteListOnDeviceBrick::class.java,
                            ReadListFromDeviceBrick::class.java,
                            StoreCSVIntoUserListBrick::class.java,
                            WebRequestBrick::class.java,
                            LookRequestBrick::class.java,
                            AskBrick::class.java,
                            AskSpeechBrick::class.java,
                            StartListeningBrick::class.java
                        )
                    ), arrayOf(
                        "Device", Arrays.asList(
                            ResetTimerBrick::class.java,
                            WhenBrick::class.java,
                            WhenTouchDownBrick::class.java,
                            WebRequestBrick::class.java,
                            LookRequestBrick::class.java,
                            OpenUrlBrick::class.java,
                            VibrationBrick::class.java,
                            SpeakBrick::class.java,
                            SpeakAndWaitBrick::class.java,
                            AskSpeechBrick::class.java,
                            StartListeningBrick::class.java,
                            CameraBrick::class.java,
                            ChooseCameraBrick::class.java,
                            FlashBrick::class.java,
                            WriteVariableOnDeviceBrick::class.java,
                            ReadVariableFromDeviceBrick::class.java,
                            WriteVariableToFileBrick::class.java,
                            ReadVariableFromFileBrick::class.java,
                            WriteListOnDeviceBrick::class.java,
                            ReadListFromDeviceBrick::class.java,
                            TapAtBrick::class.java,
                            TapForBrick::class.java,
                            TouchAndSlideBrick::class.java
                        )
                    ), arrayOf(
                        "Lego NXT", Arrays.asList(
                            LegoNxtMotorTurnAngleBrick::class.java,
                            LegoNxtMotorStopBrick::class.java,
                            LegoNxtMotorMoveBrick::class.java,
                            LegoNxtPlayToneBrick::class.java
                        )
                    ), arrayOf(
                        "Lego EV3", Arrays.asList(
                            LegoEv3MotorTurnAngleBrick::class.java,
                            LegoEv3MotorMoveBrick::class.java,
                            LegoEv3MotorStopBrick::class.java,
                            LegoEv3PlayToneBrick::class.java,
                            LegoEv3SetLedBrick::class.java
                        )
                    ), arrayOf(
                        "AR.Drone 2.0", Arrays.asList(
                            DroneTakeOffLandBrick::class.java,
                            DroneEmergencyBrick::class.java,
                            DroneMoveUpBrick::class.java,
                            DroneMoveDownBrick::class.java,
                            DroneMoveLeftBrick::class.java,
                            DroneMoveRightBrick::class.java,
                            DroneMoveForwardBrick::class.java,
                            DroneMoveBackwardBrick::class.java,
                            DroneTurnLeftBrick::class.java,
                            DroneTurnRightBrick::class.java,
                            DroneFlipBrick::class.java,
                            DronePlayLedAnimationBrick::class.java,
                            DroneSwitchCameraBrick::class.java
                        )
                    ), arrayOf(
                        "Jumping Sumo", Arrays.asList(
                            JumpingSumoMoveForwardBrick::class.java,
                            JumpingSumoMoveBackwardBrick::class.java,
                            JumpingSumoAnimationsBrick::class.java,
                            JumpingSumoSoundBrick::class.java,
                            JumpingSumoNoSoundBrick::class.java,
                            JumpingSumoJumpLongBrick::class.java,
                            JumpingSumoJumpHighBrick::class.java,
                            JumpingSumoRotateLeftBrick::class.java,
                            JumpingSumoRotateRightBrick::class.java,
                            JumpingSumoTurnBrick::class.java,
                            JumpingSumoTakingPictureBrick::class.java
                        )
                    ), arrayOf(
                        "Phiro", Arrays.asList(
                            PhiroMotorMoveForwardBrick::class.java,
                            PhiroMotorMoveBackwardBrick::class.java,
                            PhiroMotorStopBrick::class.java,
                            PhiroPlayToneBrick::class.java,
                            PhiroRGBLightBrick::class.java,
                            PhiroIfLogicBeginBrick::class.java,
                            SetVariableBrick::class.java,
                            SetVariableBrick::class.java,
                            SetVariableBrick::class.java,
                            SetVariableBrick::class.java,
                            SetVariableBrick::class.java,
                            SetVariableBrick::class.java
                        )
                    ), arrayOf(
                        "Arduino", Arrays.asList(
                            ArduinoSendDigitalValueBrick::class.java,
                            ArduinoSendPWMValueBrick::class.java
                        )
                    ), arrayOf(
                        "Chromecast", Arrays.asList(
                            WhenGamepadButtonBrick::class.java
                        )
                    ), arrayOf(
                        "Raspberry Pi", Arrays.asList(
                            WhenRaspiPinChangedBrick::class.java,
                            RaspiIfLogicBeginBrick::class.java,
                            RaspiSendDigitalValueBrick::class.java,
                            RaspiPwmBrick::class.java
                        )
                    ), arrayOf(
                        "Testing", Arrays.asList(
                            AssertEqualsBrick::class.java,
                            AssertUserListsBrick::class.java,
                            ParameterizedBrick::class.java,
                            WaitTillIdleBrick::class.java,
                            TapAtBrick::class.java,
                            TapForBrick::class.java,
                            TouchAndSlideBrick::class.java,
                            FinishStageBrick::class.java,
                            StoreCSVIntoUserListBrick::class.java,
                            WebRequestBrick::class.java
                        )
                    )
                )
            )
        }
    }
}