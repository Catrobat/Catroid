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
package org.catrobat.catroid.ui.fragment

import android.content.Context
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.RaspiInterruptScript
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.WhenConditionScript
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
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
import org.catrobat.catroid.content.bricks.ParameterizedEndBrick
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
import org.catrobat.catroid.content.bricks.SavePlotBrick
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
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick
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
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.content.bricks.StartPlotBrick
import org.catrobat.catroid.content.bricks.StitchBrick
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick
import org.catrobat.catroid.content.bricks.StopPlotBrick
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
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
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
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.ui.controller.RecentBrickListManager
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import java.util.ArrayList
import java.util.Locale

open class CategoryBricksFactory {

    fun getBricks(category: String, isBackgroundSprite: Boolean, context: Context): List<Brick> {
        when (category) {
            context.getString(R.string.category_recently_used) -> return setupRecentBricksCategoryList(isBackgroundSprite)
            context.getString(R.string.category_event) -> return setupEventCategoryList(context, isBackgroundSprite)
            context.getString(R.string.category_control) -> return setupControlCategoryList(context)
            context.getString(R.string.category_motion) -> return setupMotionCategoryList(context, isBackgroundSprite)
            context.getString(R.string.category_sound) -> return setupSoundCategoryList(context)
            context.getString(R.string.category_looks) -> return setupLooksCategoryList(context, isBackgroundSprite)
            context.getString(R.string.category_pen) -> return setupPenCategoryList(isBackgroundSprite)
            context.getString(R.string.category_user_bricks) -> return setupUserBricksCategoryList()
            context.getString(R.string.category_data) -> return setupDataCategoryList(context, isBackgroundSprite)
            context.getString(R.string.category_device) -> return setupDeviceCategoryList(context, isBackgroundSprite)
            context.getString(R.string.category_lego_nxt) -> return setupLegoNxtCategoryList()
            context.getString(R.string.category_lego_ev3) -> return setupLegoEv3CategoryList()
            context.getString(R.string.category_arduino) -> return setupArduinoCategoryList()
            context.getString(R.string.category_drone) -> return setupDroneCategoryList()
            context.getString(R.string.category_jumping_sumo) -> return setupJumpingSumoCategoryList()
            context.getString(R.string.category_phiro) -> return setupPhiroProCategoryList()
            context.getString(R.string.category_cast) -> return setupChromecastCategoryList(context)
            context.getString(R.string.category_raspi) -> return setupRaspiCategoryList()
            context.getString(R.string.category_embroidery) -> return setupEmbroideryCategoryList(context)
            context.getString(R.string.category_plot) -> return setupPlotCategoryList(context)
            context.getString(R.string.category_assertions) -> return setupAssertionsCategoryList(context)
            else -> return emptyList()
        }
    }

    fun setupRecentBricksCategoryList(isBackgroundSprite: Boolean): List<Brick> = RecentBrickListManager.getInstance().getRecentBricks(isBackgroundSprite)

    protected open fun setupEventCategoryList(
        context: Context,
        isBackgroundSprite: Boolean
    ): List<Brick> {
        val defaultIf = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.SMALLER_THAN.toString(),
            null
        )
        defaultIf.setLeftChild(FormulaElement(FormulaElement.ElementType.NUMBER, "1", null))
        defaultIf.setRightChild(FormulaElement(FormulaElement.ElementType.NUMBER, "2", null))
        val eventBrickList: MutableList<Brick> = ArrayList()
        eventBrickList.add(WhenStartedBrick())
        eventBrickList.add(WhenBrick())
        eventBrickList.add(WhenTouchDownBrick())
        val broadcastMessages =
            ProjectManager.getInstance().currentProject?.broadcastMessageContainer?.broadcastMessages
        var broadcastMessage: String? = context.getString(R.string.brick_broadcast_default_value)
        if (broadcastMessages != null && broadcastMessages.size > 0) {
            broadcastMessage = broadcastMessages[0]
        }
        eventBrickList.add(BroadcastReceiverBrick(BroadcastScript(broadcastMessage)))
        eventBrickList.add(BroadcastBrick(broadcastMessage))
        eventBrickList.add(BroadcastWaitBrick(broadcastMessage))
        eventBrickList.add(WhenConditionBrick(WhenConditionScript(Formula(defaultIf))))
        if (!isBackgroundSprite) {
            eventBrickList.add(WhenBounceOffBrick(WhenBounceOffScript(null)))
        }
        eventBrickList.add(WhenBackgroundChangesBrick())
        eventBrickList.add(WhenClonedBrick())
        eventBrickList.add(CloneBrick())
        eventBrickList.add(DeleteThisCloneBrick())
        if (SettingsFragment.isNfcSharedPreferenceEnabled(context)) {
            eventBrickList.add(WhenNfcBrick())
        }
        return eventBrickList
    }

    protected open fun setupControlCategoryList(context: Context): List<Brick> {
        val ifConditionFormulaElement = FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.toString(), null)
        ifConditionFormulaElement.setLeftChild(FormulaElement(FormulaElement.ElementType.NUMBER, "1", null))
        ifConditionFormulaElement.setRightChild(FormulaElement(FormulaElement.ElementType.NUMBER, "2", null))
        val ifConditionFormula = Formula(ifConditionFormulaElement)
        val controlBrickList: MutableList<Brick> = ArrayList()
        controlBrickList.add(WaitBrick(BrickValues.WAIT))
        controlBrickList.add(NoteBrick(context.getString(R.string.brick_note_default_value)))
        controlBrickList.add(ForeverBrick())
        controlBrickList.add(IfLogicBeginBrick(ifConditionFormula))
        controlBrickList.add(IfThenLogicBeginBrick(ifConditionFormula))
        controlBrickList.add(WaitUntilBrick(ifConditionFormula))
        controlBrickList.add(RepeatBrick(Formula(BrickValues.REPEAT)))
        controlBrickList.add(RepeatUntilBrick(ifConditionFormula))
        controlBrickList.add(ForVariableFromToBrick(Formula(BrickValues.FOR_LOOP_FROM), Formula(BrickValues.FOR_LOOP_TO)))
        controlBrickList.add(ForItemInUserListBrick())
        controlBrickList.add(SceneTransitionBrick(null))
        controlBrickList.add(SceneStartBrick(null))
        if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {
            controlBrickList.add(PhiroIfLogicBeginBrick())
        }
        controlBrickList.add(ExitStageBrick())
        controlBrickList.add(StopScriptBrick(BrickValues.STOP_THIS_SCRIPT))
        controlBrickList.add(WaitTillIdleBrick())
        controlBrickList.add(WhenClonedBrick())
        controlBrickList.add(CloneBrick())
        controlBrickList.add(DeleteThisCloneBrick())
        if (SettingsFragment.isNfcSharedPreferenceEnabled(context)) {
            controlBrickList.add(SetNfcTagBrick(context.getString(R.string.brick_set_nfc_tag_default_value)))
        }
        val broadcastMessages =
            ProjectManager.getInstance().currentProject?.broadcastMessageContainer?.broadcastMessages
        var broadcastMessage: String? = context.getString(R.string.brick_broadcast_default_value)
        if (broadcastMessages != null && broadcastMessages.size > 0) {
            broadcastMessage = broadcastMessages[0]
        }
        controlBrickList.add(BroadcastReceiverBrick(BroadcastScript(broadcastMessage)))
        controlBrickList.add(BroadcastBrick(broadcastMessage))
        controlBrickList.add(BroadcastWaitBrick(broadcastMessage))
        controlBrickList.add(TapAtBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START))
        controlBrickList.add(TapForBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START, BrickValues.TOUCH_DURATION))
        controlBrickList.add(TouchAndSlideBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START, BrickValues.TOUCH_X_GOAL, BrickValues.TOUCH_Y_GOAL, BrickValues.TOUCH_DURATION))
        controlBrickList.add(OpenUrlBrick(BrickValues.OPEN_IN_BROWSER))
        return controlBrickList
    }

    private fun setupUserBricksCategoryList(): List<Brick> {
        val currentSprite = ProjectManager.getInstance().currentSprite
        var userDefinedBricks: MutableList<Brick> = ArrayList()
        if (currentSprite != null) userDefinedBricks = currentSprite.userDefinedBrickList
        userDefinedBricks = ArrayList(userDefinedBricks)
        if (BuildConfig.FEATURE_USER_REPORTERS_ENABLED) userDefinedBricks.add(ReportBrick())
        return userDefinedBricks
    }

    private fun setupChromecastCategoryList(context: Context): List<Brick> {
        val chromecastBrickList: MutableList<Brick> = ArrayList()
        chromecastBrickList.add(WhenGamepadButtonBrick(WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_A))))
        return chromecastBrickList
    }

    protected open fun setupMotionCategoryList(
        context: Context?,
        isBackgroundSprite: Boolean
    ): List<Brick> {
        val motionBrickList: MutableList<Brick> = ArrayList()
        motionBrickList.add(PlaceAtBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION))
        motionBrickList.add(SetXBrick(Formula(BrickValues.X_POSITION)))
        motionBrickList.add(SetYBrick(BrickValues.Y_POSITION))
        motionBrickList.add(ChangeXByNBrick(BrickValues.CHANGE_X_BY))
        motionBrickList.add(ChangeYByNBrick(BrickValues.CHANGE_Y_BY))
        motionBrickList.add(GoToBrick(null))
        if (!isBackgroundSprite) motionBrickList.add(IfOnEdgeBounceBrick())
        motionBrickList.add(MoveNStepsBrick(BrickValues.MOVE_STEPS))
        motionBrickList.add(TurnLeftBrick(BrickValues.TURN_DEGREES))
        motionBrickList.add(TurnRightBrick(BrickValues.TURN_DEGREES))
        motionBrickList.add(PointInDirectionBrick(BrickValues.POINT_IN_DIRECTION))
        motionBrickList.add(PointToBrick(null))
        motionBrickList.add(SetRotationStyleBrick())
        motionBrickList.add(GlideToBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION, BrickValues.GLIDE_SECONDS))
        if (!isBackgroundSprite) {
            motionBrickList.add(GoNStepsBackBrick(BrickValues.GO_BACK))
            motionBrickList.add(ComeToFrontBrick())
        }
        motionBrickList.add(SetCameraFocusPointBrick())
        motionBrickList.add(VibrationBrick(BrickValues.VIBRATE_SECONDS))
        motionBrickList.add(SetPhysicsObjectTypeBrick(BrickValues.PHYSIC_TYPE))
        if (!isBackgroundSprite) motionBrickList.add(WhenBounceOffBrick(WhenBounceOffScript(null)))
        motionBrickList.add(SetVelocityBrick(BrickValues.PHYSIC_VELOCITY))
        motionBrickList.add(TurnLeftSpeedBrick(BrickValues.PHYSIC_TURN_DEGREES))
        motionBrickList.add(TurnRightSpeedBrick(BrickValues.PHYSIC_TURN_DEGREES))
        motionBrickList.add(SetGravityBrick(BrickValues.PHYSIC_GRAVITY))
        motionBrickList.add(SetMassBrick(BrickValues.PHYSIC_MASS))
        motionBrickList.add(SetBounceBrick(BrickValues.PHYSIC_BOUNCE_FACTOR * BrickValues.PHYSIC_MULTIPLIER))
        motionBrickList.add(SetFrictionBrick(BrickValues.PHYSIC_FRICTION * BrickValues.PHYSIC_MULTIPLIER))
        if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {
            motionBrickList.add(PhiroMotorMoveForwardBrick(PhiroMotorMoveForwardBrick.Motor.MOTOR_LEFT, BrickValues.PHIRO_SPEED))
            motionBrickList.add(PhiroMotorMoveBackwardBrick(PhiroMotorMoveBackwardBrick.Motor.MOTOR_LEFT, BrickValues.PHIRO_SPEED))
            motionBrickList.add(PhiroMotorStopBrick(PhiroMotorStopBrick.Motor.MOTOR_BOTH))
        }
        motionBrickList.add(FadeParticleEffectBrick())
        return motionBrickList
    }

    protected open fun setupSoundCategoryList(context: Context): List<Brick> {
        val soundBrickList: MutableList<Brick> = ArrayList()
        soundBrickList.add(PlaySoundBrick())
        soundBrickList.add(PlaySoundAndWaitBrick())
        soundBrickList.add(PlaySoundAtBrick(BrickValues.PLAY_AT_DEFAULT_OFFSET))
        soundBrickList.add(StopSoundBrick())
        soundBrickList.add(StopAllSoundsBrick())
        soundBrickList.add(SetVolumeToBrick(BrickValues.SET_VOLUME_TO))
        soundBrickList.add(ChangeVolumeByNBrick(Formula(BrickValues.CHANGE_VOLUME_BY)))
        if (SettingsFragment.isAISpeechSynthetizationSharedPreferenceEnabled(context)) {
            soundBrickList.add(SpeakBrick(context.getString(R.string.brick_speak_default_value)))
            soundBrickList.add(SpeakAndWaitBrick(context.getString(R.string.brick_speak_default_value)))
        }
        if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {
            soundBrickList.add(PhiroPlayToneBrick(PhiroPlayToneBrick.Tone.DO, BrickValues.PHIRO_DURATION))
        }
        if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {
            soundBrickList.add(AskSpeechBrick(context.getString(R.string.brick_ask_speech_default_question)))
            soundBrickList.add(StartListeningBrick())
            soundBrickList.add(SetListeningLanguageBrick())
        }
        soundBrickList.add(SetInstrumentBrick())
        soundBrickList.add(PlayNoteForBeatsBrick(BrickValues.DEFAULT_NOTE, BrickValues.PAUSED_BEATS_INT))
        soundBrickList.add(PlayDrumForBeatsBrick(BrickValues.PAUSED_BEATS_INT))
        soundBrickList.add(SetTempoBrick(BrickValues.DEFAULT_TEMPO))
        soundBrickList.add(ChangeTempoByNBrick(BrickValues.CHANGE_TEMPO))
        soundBrickList.add(PauseForBeatsBrick(BrickValues.PAUSED_BEATS_FLOAT))
        return soundBrickList
    }

    protected open fun setupLooksCategoryList(context: Context, isBackgroundSprite: Boolean): List<Brick> {
        val looksBrickList = mutableListOf<Brick>()
        if (!isBackgroundSprite) {
            looksBrickList.add(SetLookBrick())
            looksBrickList.add(SetLookByIndexBrick(BrickValues.SET_LOOK_BY_INDEX))
        }
        looksBrickList.add(NextLookBrick())
        looksBrickList.add(PreviousLookBrick())
        looksBrickList.add(SetSizeToBrick(BrickValues.SET_SIZE_TO))
        looksBrickList.add(ChangeSizeByNBrick(BrickValues.CHANGE_SIZE_BY))
        looksBrickList.add(HideBrick())
        looksBrickList.add(ShowBrick())
        looksBrickList.add(AskBrick(context.getString(R.string.brick_ask_default_question)))
        if (!isBackgroundSprite) {
            looksBrickList.add(SayBubbleBrick(context.getString(R.string.brick_say_bubble_default_value)))
            looksBrickList.add(SayForBubbleBrick(context.getString(R.string.brick_say_bubble_default_value), 1.0f))
            looksBrickList.add(ThinkBubbleBrick(context.getString(R.string.brick_think_bubble_default_value)))
            looksBrickList.add(ThinkForBubbleBrick(context.getString(R.string.brick_think_bubble_default_value), 1.0f))
        }
        looksBrickList.add(ShowTextBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION))
        looksBrickList.add(ShowTextColorSizeAlignmentBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION, BrickValues.RELATIVE_SIZE_IN_PERCENT, BrickValues.SHOW_VARIABLE_COLOR))
        looksBrickList.add(SetTransparencyBrick(BrickValues.SET_TRANSPARENCY))
        looksBrickList.add(ChangeTransparencyByNBrick(BrickValues.CHANGE_TRANSPARENCY_EFFECT))
        looksBrickList.add(SetBrightnessBrick(BrickValues.SET_BRIGHTNESS_TO))
        looksBrickList.add(ChangeBrightnessByNBrick(BrickValues.CHANGE_BRIGHTNESS_BY))
        looksBrickList.add(SetColorBrick(BrickValues.SET_COLOR_TO))
        looksBrickList.add(ChangeColorByNBrick(BrickValues.CHANGE_COLOR_BY))
        looksBrickList.add(FadeParticleEffectBrick())
        looksBrickList.add(ParticleEffectAdditivityBrick())
        looksBrickList.add(SetParticleColorBrick(BrickValues.PARTICLE_COLOR))
        looksBrickList.add(ClearGraphicEffectBrick())
        looksBrickList.add(SetCameraFocusPointBrick())
        looksBrickList.add(WhenBackgroundChangesBrick())
        looksBrickList.add(SetBackgroundBrick())
        looksBrickList.add(SetBackgroundByIndexBrick(BrickValues.SET_LOOK_BY_INDEX))
        looksBrickList.add(SetBackgroundAndWaitBrick())
        looksBrickList.add(SetBackgroundByIndexAndWaitBrick(BrickValues.SET_LOOK_BY_INDEX))
        if (!ProjectManager.getInstance().currentProject.isCastProject) {
            looksBrickList.add(CameraBrick())
            looksBrickList.add(ChooseCameraBrick())
            looksBrickList.add(FlashBrick())
        }
        when {
            !isBackgroundSprite -> looksBrickList.add(LookRequestBrick(BrickValues.LOOK_REQUEST))
            ProjectManager.getInstance().currentProject.xmlHeader.islandscapeMode() -> looksBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST_LANDSCAPE))
            else -> looksBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST))
        }
        if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) looksBrickList.add(
            PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, BrickValues.PHIRO_VALUE_RED, BrickValues.PHIRO_VALUE_GREEN, BrickValues.PHIRO_VALUE_BLUE)
        )
        looksBrickList.add(PaintNewLookBrick(context.getString(R.string.brick_paint_new_look_name)))
        looksBrickList.add(EditLookBrick())
        looksBrickList.add(CopyLookBrick(context.getString(R.string.brick_copy_look_name)))
        looksBrickList.add(DeleteLookBrick())
        looksBrickList.add(OpenUrlBrick(BrickValues.OPEN_IN_BROWSER))
        return looksBrickList
    }

    private fun setupPenCategoryList(isBackgroundSprite: Boolean): List<Brick> {
        val penBrickList: MutableList<Brick> = ArrayList()
        if (!isBackgroundSprite) {
            penBrickList.add(PenDownBrick())
            penBrickList.add(PenUpBrick())
            penBrickList.add(SetPenSizeBrick(BrickValues.PEN_SIZE))
            penBrickList.add(SetPenColorBrick(BrickValues.PEN_COLOR_R, BrickValues.PEN_COLOR_G, BrickValues.PEN_COLOR_B))
            penBrickList.add(StampBrick())
        }
        penBrickList.add(ClearBackgroundBrick())
        return penBrickList
    }

    protected open fun setupDataCategoryList(
        context: Context,
        isBackgroundSprite: Boolean
    ): List<Brick> {
        val dataBrickList: MutableList<Brick> = ArrayList()
        dataBrickList.add(SetVariableBrick(BrickValues.SET_VARIABLE))
        dataBrickList.add(ChangeVariableBrick(BrickValues.CHANGE_VARIABLE))
        dataBrickList.add(ShowTextBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION))
        dataBrickList.add(ShowTextColorSizeAlignmentBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION, BrickValues.RELATIVE_SIZE_IN_PERCENT, BrickValues.SHOW_VARIABLE_COLOR))
        dataBrickList.add(HideTextBrick())
        dataBrickList.add(WriteVariableOnDeviceBrick())
        dataBrickList.add(ReadVariableFromDeviceBrick())
        dataBrickList.add(WriteVariableToFileBrick(context.getString(R.string.brick_write_variable_to_file_default_value)))
        dataBrickList.add(ReadVariableFromFileBrick(context.getString(R.string.brick_write_variable_to_file_default_value)))
        dataBrickList.add(AddItemToUserListBrick(BrickValues.ADD_ITEM_TO_USERLIST))
        dataBrickList.add(DeleteItemOfUserListBrick(BrickValues.DELETE_ITEM_OF_USERLIST))
        dataBrickList.add(ClearUserListBrick())
        dataBrickList.add(InsertItemIntoUserListBrick(BrickValues.INSERT_ITEM_INTO_USERLIST_VALUE, BrickValues.INSERT_ITEM_INTO_USERLIST_INDEX))
        dataBrickList.add(ReplaceItemInUserListBrick(BrickValues.REPLACE_ITEM_IN_USERLIST_VALUE, BrickValues.REPLACE_ITEM_IN_USERLIST_INDEX))
        dataBrickList.add(WriteListOnDeviceBrick())
        dataBrickList.add(ReadListFromDeviceBrick())
        dataBrickList.add(StoreCSVIntoUserListBrick(BrickValues.STORE_CSV_INTO_USERLIST_COLUMN, context.getString(R.string.brick_store_csv_into_userlist_data)))
        dataBrickList.add(WebRequestBrick(context.getString(R.string.brick_web_request_default_value)))
        when {
            !isBackgroundSprite -> dataBrickList.add(LookRequestBrick(BrickValues.LOOK_REQUEST))
            ProjectManager.getInstance().currentProject.xmlHeader.islandscapeMode() -> dataBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST_LANDSCAPE))
            else -> dataBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST))
        }
        dataBrickList.add(AskBrick(context.getString(R.string.brick_ask_default_question)))
        if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {
            dataBrickList.add(AskSpeechBrick(context.getString(R.string.brick_ask_speech_default_question)))
        }
        if (SettingsFragment.isEmroiderySharedPreferenceEnabled(context)) {
            dataBrickList.add(WriteEmbroideryToFileBrick(context.getString(R.string.brick_default_embroidery_file)))
        }
        if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {
            dataBrickList.add(StartListeningBrick())
        }
        if (SettingsFragment.isNfcSharedPreferenceEnabled(context)) {
            dataBrickList.add(SetNfcTagBrick(context.getString(R.string.brick_set_nfc_tag_default_value)))
        }
        return dataBrickList
    }

    @SuppressWarnings("ComplexMethod")
    protected fun setupDeviceCategoryList(context: Context, isBackgroundSprite: Boolean): List<Brick> {
        val deviceBrickList: MutableList<Brick> = ArrayList()
        deviceBrickList.add(ResetTimerBrick())
        deviceBrickList.add(WhenBrick())
        deviceBrickList.add(WhenTouchDownBrick())
        if (SettingsFragment.isNfcSharedPreferenceEnabled(context)) {
            deviceBrickList.add(WhenNfcBrick())
            deviceBrickList.add(SetNfcTagBrick(context.getString(R.string.brick_set_nfc_tag_default_value)))
        }
        deviceBrickList.add(WebRequestBrick(context.getString(R.string.brick_web_request_default_value)))
        when {
            !isBackgroundSprite -> deviceBrickList.add(LookRequestBrick(BrickValues.LOOK_REQUEST))
            ProjectManager.getInstance().currentProject.xmlHeader.islandscapeMode() -> deviceBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST_LANDSCAPE))
            else -> deviceBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST))
        }
        deviceBrickList.add(OpenUrlBrick(BrickValues.OPEN_IN_BROWSER))
        deviceBrickList.add(VibrationBrick(BrickValues.VIBRATE_SECONDS))

        if (SettingsFragment.isAISpeechSynthetizationSharedPreferenceEnabled(context)) {
            deviceBrickList.add(SpeakBrick(context.getString(R.string.brick_speak_default_value)))
            deviceBrickList.add(SpeakAndWaitBrick(context.getString(R.string.brick_speak_default_value)))
        }

        if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {
            deviceBrickList.add(AskSpeechBrick(context.getString(R.string.brick_ask_speech_default_question)))
            deviceBrickList.add(StartListeningBrick())
        }
        if (ProjectManager.getInstance().currentProject != null && !ProjectManager.getInstance().currentProject.isCastProject) {
            deviceBrickList.add(CameraBrick())
            deviceBrickList.add(ChooseCameraBrick())
            deviceBrickList.add(FlashBrick())
        }
        deviceBrickList.add(WriteVariableOnDeviceBrick())
        deviceBrickList.add(ReadVariableFromDeviceBrick())
        deviceBrickList.add(WriteVariableToFileBrick(context.getString(R.string.brick_write_variable_to_file_default_value)))
        deviceBrickList.add(ReadVariableFromFileBrick(context.getString(R.string.brick_write_variable_to_file_default_value)))
        deviceBrickList.add(WriteListOnDeviceBrick())
        deviceBrickList.add(ReadListFromDeviceBrick())
        deviceBrickList.add(TapAtBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START))
        deviceBrickList.add(TapForBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START, BrickValues.TOUCH_DURATION))
        deviceBrickList.add(TouchAndSlideBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START, BrickValues.TOUCH_X_GOAL, BrickValues.TOUCH_Y_GOAL, BrickValues.TOUCH_DURATION))
        if (SettingsFragment.isCastSharedPreferenceEnabled(context)) deviceBrickList.addAll(setupChromecastCategoryList(context))
        if (SettingsFragment.isMindstormsNXTSharedPreferenceEnabled(context)) deviceBrickList.addAll(setupLegoNxtCategoryList())
        if (SettingsFragment.isMindstormsEV3SharedPreferenceEnabled(context)) deviceBrickList.addAll(setupLegoEv3CategoryList())
        if (SettingsFragment.isDroneSharedPreferenceEnabled(context)) deviceBrickList.addAll(setupDroneCategoryList())
        if (SettingsFragment.isJSSharedPreferenceEnabled(context)) deviceBrickList.addAll(setupJumpingSumoCategoryList())
        if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) deviceBrickList.addAll(setupPhiroProCategoryList())
        if (SettingsFragment.isArduinoSharedPreferenceEnabled(context)) deviceBrickList.addAll(setupArduinoCategoryList())
        if (SettingsFragment.isRaspiSharedPreferenceEnabled(context)) deviceBrickList.addAll(setupRaspiCategoryList())
        if (SettingsFragment.isEmroiderySharedPreferenceEnabled(context)) deviceBrickList.addAll(setupEmbroideryCategoryList(context))
        return deviceBrickList
    }

    private fun setupLegoNxtCategoryList(): List<Brick> {
        val legoNXTBrickList: MutableList<Brick> = ArrayList()
        legoNXTBrickList.add(LegoNxtMotorTurnAngleBrick(LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, BrickValues.LEGO_ANGLE))
        legoNXTBrickList.add(LegoNxtMotorStopBrick(LegoNxtMotorStopBrick.Motor.MOTOR_A))
        legoNXTBrickList.add(LegoNxtMotorMoveBrick(LegoNxtMotorMoveBrick.Motor.MOTOR_A, BrickValues.LEGO_SPEED))
        legoNXTBrickList.add(LegoNxtPlayToneBrick(BrickValues.LEGO_FREQUENCY, BrickValues.LEGO_DURATION))
        return legoNXTBrickList
    }

    private fun setupLegoEv3CategoryList(): List<Brick> {
        val legoEV3BrickList: MutableList<Brick> = ArrayList()
        legoEV3BrickList.add(LegoEv3MotorTurnAngleBrick(LegoEv3MotorTurnAngleBrick.Motor.MOTOR_A, BrickValues.LEGO_ANGLE))
        legoEV3BrickList.add(LegoEv3MotorMoveBrick(LegoEv3MotorMoveBrick.Motor.MOTOR_A, BrickValues.LEGO_SPEED))
        legoEV3BrickList.add(LegoEv3MotorStopBrick(LegoEv3MotorStopBrick.Motor.MOTOR_A))
        legoEV3BrickList.add(LegoEv3PlayToneBrick(BrickValues.LEGO_FREQUENCY, BrickValues.LEGO_DURATION, BrickValues.LEGO_VOLUME))
        legoEV3BrickList.add(LegoEv3SetLedBrick(LegoEv3SetLedBrick.LedStatus.LED_GREEN))
        return legoEV3BrickList
    }

    private fun setupDroneCategoryList(): List<Brick> {
        val droneBrickList: MutableList<Brick> = ArrayList()
        droneBrickList.add(DroneTakeOffLandBrick())
        droneBrickList.add(DroneEmergencyBrick())
        droneBrickList.add(DroneMoveUpBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneMoveDownBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneMoveLeftBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneMoveRightBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneMoveForwardBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneMoveBackwardBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneTurnLeftBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneTurnRightBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT))
        droneBrickList.add(DroneFlipBrick())
        droneBrickList.add(DronePlayLedAnimationBrick())
        droneBrickList.add(DroneSwitchCameraBrick())
        return droneBrickList
    }

    private fun setupJumpingSumoCategoryList(): List<Brick> {
        val jumpingSumoBrickList: MutableList<Brick> = ArrayList()
        jumpingSumoBrickList.add(JumpingSumoMoveForwardBrick(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT))
        jumpingSumoBrickList.add(JumpingSumoMoveBackwardBrick(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT))
        jumpingSumoBrickList.add(JumpingSumoAnimationsBrick(JumpingSumoAnimationsBrick.Animation.SPIN))
        jumpingSumoBrickList.add(JumpingSumoSoundBrick(JumpingSumoSoundBrick.Sounds.DEFAULT, BrickValues.JUMPING_SUMO_SOUND_BRICK_DEFAULT_VOLUME_PERCENT))
        jumpingSumoBrickList.add(JumpingSumoNoSoundBrick())
        jumpingSumoBrickList.add(JumpingSumoJumpLongBrick())
        jumpingSumoBrickList.add(JumpingSumoJumpHighBrick())
        jumpingSumoBrickList.add(JumpingSumoRotateLeftBrick(BrickValues.JUMPING_SUMO_ROTATE_DEFAULT_DEGREE))
        jumpingSumoBrickList.add(JumpingSumoRotateRightBrick(BrickValues.JUMPING_SUMO_ROTATE_DEFAULT_DEGREE))
        jumpingSumoBrickList.add(JumpingSumoTurnBrick())
        jumpingSumoBrickList.add(JumpingSumoTakingPictureBrick())
        return jumpingSumoBrickList
    }

    private fun setupPhiroProCategoryList(): List<Brick> {
        val phiroProBrickList: MutableList<Brick> = ArrayList()
        phiroProBrickList.add(PhiroMotorMoveForwardBrick(PhiroMotorMoveForwardBrick.Motor.MOTOR_LEFT, BrickValues.PHIRO_SPEED))
        phiroProBrickList.add(PhiroMotorMoveBackwardBrick(PhiroMotorMoveBackwardBrick.Motor.MOTOR_LEFT, BrickValues.PHIRO_SPEED))
        phiroProBrickList.add(PhiroMotorStopBrick(PhiroMotorStopBrick.Motor.MOTOR_BOTH))
        phiroProBrickList.add(PhiroPlayToneBrick(PhiroPlayToneBrick.Tone.DO, BrickValues.PHIRO_DURATION))
        phiroProBrickList.add(PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, BrickValues.PHIRO_VALUE_RED, BrickValues.PHIRO_VALUE_GREEN, BrickValues.PHIRO_VALUE_BLUE))
        phiroProBrickList.add(PhiroIfLogicBeginBrick())
        phiroProBrickList.add(SetVariableBrick(Sensors.PHIRO_FRONT_LEFT))
        phiroProBrickList.add(SetVariableBrick(Sensors.PHIRO_FRONT_RIGHT))
        phiroProBrickList.add(SetVariableBrick(Sensors.PHIRO_SIDE_LEFT))
        phiroProBrickList.add(SetVariableBrick(Sensors.PHIRO_SIDE_RIGHT))
        phiroProBrickList.add(SetVariableBrick(Sensors.PHIRO_BOTTOM_LEFT))
        phiroProBrickList.add(SetVariableBrick(Sensors.PHIRO_BOTTOM_RIGHT))
        return phiroProBrickList
    }

    private fun setupArduinoCategoryList(): List<Brick> {
        val arduinoBrickList: MutableList<Brick> = ArrayList()
        arduinoBrickList.add(ArduinoSendDigitalValueBrick(BrickValues.ARDUINO_DIGITAL_INITIAL_PIN_NUMBER, BrickValues.ARDUINO_DIGITAL_INITIAL_PIN_VALUE))
        arduinoBrickList.add(ArduinoSendPWMValueBrick(BrickValues.ARDUINO_PWM_INITIAL_PIN_NUMBER, BrickValues.ARDUINO_PWM_INITIAL_PIN_VALUE))
        return arduinoBrickList
    }

    private fun setupRaspiCategoryList(): List<Brick> {
        val defaultScript = RaspiInterruptScript("3", "pressed")
        val raspiBrickList: MutableList<Brick> = ArrayList()
        raspiBrickList.add(WhenRaspiPinChangedBrick(defaultScript))
        raspiBrickList.add(RaspiIfLogicBeginBrick(Formula(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER)))
        raspiBrickList.add(RaspiSendDigitalValueBrick(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER, BrickValues.RASPI_DIGITAL_INITIAL_PIN_VALUE))
        raspiBrickList.add(RaspiPwmBrick(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER, BrickValues.RASPI_PWM_INITIAL_FREQUENCY, BrickValues.RASPI_PWM_INITIAL_PERCENTAGE))
        return raspiBrickList
    }

    private fun setupEmbroideryCategoryList(context: Context): List<Brick> {
        val embroideryBrickList: MutableList<Brick> = ArrayList()
        embroideryBrickList.add(StitchBrick())
        embroideryBrickList.add(SetThreadColorBrick(Formula(BrickValues.THREAD_COLOR)))
        embroideryBrickList.add(RunningStitchBrick(Formula(BrickValues.STITCH_LENGTH)))
        embroideryBrickList.add(ZigZagStitchBrick(Formula(BrickValues.ZIGZAG_STITCH_LENGTH), Formula(BrickValues.ZIGZAG_STITCH_WIDTH)))
        embroideryBrickList.add(TripleStitchBrick(Formula(BrickValues.STITCH_LENGTH)))
        embroideryBrickList.add(SewUpBrick())
        embroideryBrickList.add(StopRunningStitchBrick())
        embroideryBrickList.add(WriteEmbroideryToFileBrick(context.getString(R.string.brick_default_embroidery_file)))
        return embroideryBrickList
    }

    private fun setupPlotCategoryList(context: Context): List<Brick> {
        val plotBrickList: MutableList<Brick> = ArrayList()
        plotBrickList.add(StartPlotBrick())
        plotBrickList.add(StopPlotBrick())
        plotBrickList.add(SavePlotBrick(context.getString(R.string.brick_default_plot_file)))
        return plotBrickList
    }

    private fun setupAssertionsCategoryList(context: Context): List<Brick> {
        val assertionsBrickList: MutableList<Brick> = ArrayList()
        assertionsBrickList.add(AssertEqualsBrick())
        assertionsBrickList.add(AssertUserListsBrick())
        assertionsBrickList.add(ParameterizedBrick())
        assertionsBrickList.add(WaitTillIdleBrick())
        assertionsBrickList.add(TapAtBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START))
        assertionsBrickList.add(TapForBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START, BrickValues.TOUCH_DURATION))
        assertionsBrickList.add(TouchAndSlideBrick(BrickValues.TOUCH_X_START, BrickValues.TOUCH_Y_START, BrickValues.TOUCH_X_GOAL, BrickValues.TOUCH_Y_GOAL, BrickValues.TOUCH_DURATION))
        assertionsBrickList.add(FinishStageBrick())
        assertionsBrickList.add(StoreCSVIntoUserListBrick(BrickValues.STORE_CSV_INTO_USERLIST_COLUMN, context.getString(R.string.brick_store_csv_into_userlist_data)))
        assertionsBrickList.add(WebRequestBrick(context.getString(R.string.brick_web_request_default_value)))
        return assertionsBrickList
    }

    fun searchList(searchBrick: Brick, list: List<Brick>): Boolean = list.any { it == searchBrick.javaClass }

    fun getBrickCategory(brick: Brick, isBackgroundSprite: Boolean, context: Context): String {
        val res = context.resources
        val config = res.configuration
        val savedLocale = config.locale
        config.locale = Locale.ENGLISH
        res.updateConfiguration(config, null)
        var category: String
        category = when {
            searchList(brick, setupControlCategoryList(context)) -> res.getString(R.string.category_control)
            searchList(brick, setupEventCategoryList(context, isBackgroundSprite)) -> res.getString(R.string.category_event)
            searchList(brick, setupMotionCategoryList(context, isBackgroundSprite)) -> res.getString(R.string.category_motion)
            searchList(brick, setupSoundCategoryList(context)) -> res.getString(R.string.category_sound)
            searchList(brick, setupLooksCategoryList(context, isBackgroundSprite)) -> res.getString(R.string.category_looks)
            searchList(brick, setupPenCategoryList(isBackgroundSprite)) -> res.getString(R.string.category_pen)
            searchList(brick, setupDataCategoryList(context, isBackgroundSprite)) -> res.getString(R.string.category_data)
            searchList(brick, setupLegoNxtCategoryList()) -> res.getString(R.string.category_lego_nxt)
            searchList(brick, setupLegoEv3CategoryList()) -> res.getString(R.string.category_lego_ev3)
            searchList(brick, setupArduinoCategoryList()) -> res.getString(R.string.category_arduino)
            searchList(brick, setupDroneCategoryList()) -> res.getString(R.string.category_drone)
            searchList(brick, setupJumpingSumoCategoryList()) -> res.getString(R.string.category_jumping_sumo)
            searchList(brick, setupPhiroProCategoryList()) -> res.getString(R.string.category_phiro)
            searchList(brick, setupRaspiCategoryList()) -> res.getString(R.string.category_raspi)
            searchList(brick, setupChromecastCategoryList(context)) -> res.getString(R.string.category_cast)
            searchList(brick, setupEmbroideryCategoryList(context)) -> res.getString(R.string.category_embroidery)
            searchList(brick, setupAssertionsCategoryList(context)) -> res.getString(R.string.category_assertions)
            else -> "No Match"
        }

        when (brick) {
            is AskBrick -> category = res.getString(R.string.category_looks)
            is AskSpeechBrick -> category = res.getString(R.string.category_sound)
            is LookRequestBrick -> category = res.getString(R.string.category_looks)
            is BackgroundRequestBrick -> category = res.getString(R.string.category_looks)
            is WhenClonedBrick -> category = res.getString(R.string.category_control)
            is WhenBackgroundChangesBrick -> category = res.getString(R.string.category_event)
            is SetVariableBrick -> category = res.getString(R.string.category_data)
            is WebRequestBrick -> category = res.getString(R.string.category_data)
            is StoreCSVIntoUserListBrick -> category = res.getString(R.string.category_data)
            is UserDefinedBrick -> category = res.getString(R.string.category_user_bricks)
            is UserDefinedReceiverBrick -> category = res.getString(R.string.category_user_bricks)
            is ParameterizedEndBrick -> category = res.getString(R.string.category_assertions)
            is WriteEmbroideryToFileBrick -> category = res.getString(R.string.category_embroidery)
        }

        config.locale = savedLocale
        res.updateConfiguration(config, null)
        return category
    }
}
