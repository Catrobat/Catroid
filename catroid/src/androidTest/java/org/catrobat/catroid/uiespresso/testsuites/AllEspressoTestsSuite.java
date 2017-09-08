/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.testsuites;

import org.catrobat.catroid.uiespresso.SmokeTest;
import org.catrobat.catroid.uiespresso.annotations.FlakyTestTest;
import org.catrobat.catroid.uiespresso.content.brick.AddItemToUserListTest;
import org.catrobat.catroid.uiespresso.content.brick.ArduinoSendDigitalValueBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ArduinoSendPWMValueBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.AskBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.BrickValueParameterTest;
import org.catrobat.catroid.uiespresso.content.brick.BroadcastBricksTest;
import org.catrobat.catroid.uiespresso.content.brick.CameraBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ChangeBrightnessByNBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ChangeColorByNBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ChangeSizeByNBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ChangeTransparencyByNBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ChangeVolumeByNBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ChangeXByNBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ChangeYByNBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ClearGraphicEffectBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ComeToFrontBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.DeleteItemOfUserListBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.DragNDropBricksTest;
import org.catrobat.catroid.uiespresso.content.brick.ForeverBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.GlideToBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.GoNStepsBackTest;
import org.catrobat.catroid.uiespresso.content.brick.GoToBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.HideBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoEV3SetLedBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoEv3MotorMoveBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoEv3MotorStopBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoEv3MotorTurnAngleBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoEv3PlayToneBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoNXTMotorMoveBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoNXTMotorStopBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoNxtMotorTurnAngleBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LegoNxtPlayToneBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.LoopBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.MoveNStepsBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.NextLookBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.NoteBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PhiroIfBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PhiroMoveMotorBackwardBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PhiroMoveMotorForwardBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PhiroPlayToneBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PhiroStopMotorBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PlaySoundAndWaitBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PointInDirectionBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.RepeatBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ReplaceItemInUserListTest;
import org.catrobat.catroid.uiespresso.content.brick.SceneTransmitionBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetBrightnessBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetColorBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetLookByIndexBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetRotationStyleBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetSizeToBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetTransparencyBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetVolumeToBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetXBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SetYBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ShowBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.ShowTextBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SpeakAndWaitBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.SpeakBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.StopAllSoundsBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.StopScriptBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.TurnLeftBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.TurnRightBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.VariableBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.WaitBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.WhenNfcBrickStageTest;
import org.catrobat.catroid.uiespresso.content.brick.WhenNfcBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.WhenStartedBrickTest;
import org.catrobat.catroid.uiespresso.facedetection.FaceDetectionResourceStartedTest;
import org.catrobat.catroid.uiespresso.formulaeditor.FormulaEditorKeyboardTest;
import org.catrobat.catroid.uiespresso.formulaeditor.FormulaEditorTest;
import org.catrobat.catroid.uiespresso.pocketmusic.PocketMusicActivityTest;
import org.catrobat.catroid.uiespresso.stage.BroadcastForClonesRegressionTest;
import org.catrobat.catroid.uiespresso.stage.BroadcastReceiverRegressionTest;
import org.catrobat.catroid.uiespresso.stage.MultipleBroadcastsTest;
import org.catrobat.catroid.uiespresso.stage.ObjectVariableTest;
import org.catrobat.catroid.uiespresso.stage.StagePausedTest;
import org.catrobat.catroid.uiespresso.stage.StageSimpleTest;
import org.catrobat.catroid.uiespresso.ui.activity.SettingsActivityTest;
import org.catrobat.catroid.uiespresso.ui.dialog.AboutDialogTest;
import org.catrobat.catroid.uiespresso.ui.dialog.DeleteLookDialogTest;
import org.catrobat.catroid.uiespresso.ui.dialog.DeleteSoundDialogTest;
import org.catrobat.catroid.uiespresso.ui.dialog.DeleteSpriteDialogTest;
import org.catrobat.catroid.uiespresso.ui.dialog.FormulaEditorComputeDialogTest;
import org.catrobat.catroid.uiespresso.ui.dialog.RenameSpriteDialogTest;
import org.catrobat.catroid.uiespresso.ui.dialog.TermsOfUseDialogTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		PocketMusicActivityTest.class,
		SmokeTest.class,
		FlakyTestTest.class,
		FaceDetectionResourceStartedTest.class,
		WhenNfcBrickStageTest.class,
		LegoEv3PlayToneBrickTest.class,
		SetYBrickTest.class,
		ComeToFrontBrickTest.class,
		SetBrightnessBrickTest.class,
		HideBrickTest.class,
		ChangeTransparencyByNBrickTest.class,
		SetRotationStyleBrickTest.class,
		SceneTransmitionBrickTest.class,
		DragNDropBricksTest.class,
		GoNStepsBackTest.class,
		PhiroMoveMotorForwardBrickTest.class,
		RepeatBrickTest.class,
		PointInDirectionBrickTest.class,
		BroadcastBricksTest.class,
		SpeakAndWaitBrickTest.class,
		SpeakBrickTest.class,
		VariableBrickTest.class,
		ChangeVolumeByNBrickTest.class,
		AskBrickTest.class,
		BrickValueParameterTest.class,
		ArduinoSendPWMValueBrickTest.class,
		NextLookBrickTest.class,
		ChangeColorByNBrickTest.class,
		ArduinoSendDigitalValueBrickTest.class,
		DeleteItemOfUserListBrickTest.class,
		ReplaceItemInUserListTest.class,
		StopAllSoundsBrickTest.class,
		StopScriptBrickTest.class,
		SetTransparencyBrickTest.class,
		GlideToBrickTest.class,
		MoveNStepsBrickTest.class,
		WaitBrickTest.class,
		PlaySoundAndWaitBrickTest.class,
		ChangeYByNBrickTest.class,
		LegoNXTMotorStopBrickTest.class,
		PhiroIfBrickTest.class,
		PhiroMoveMotorBackwardBrickTest.class,
		TurnRightBrickTest.class,
		PhiroPlayToneBrickTest.class,
		GoToBrickTest.class,
		WhenNfcBrickTest.class,
		SetVolumeToBrickTest.class,
		SetSizeToBrickTest.class,
		PhiroStopMotorBrickTest.class,
		LegoEV3SetLedBrickTest.class,
		LegoEv3MotorMoveBrickTest.class,
		NoteBrickTest.class,
		LegoNxtPlayToneBrickTest.class,
		SetLookByIndexBrickTest.class,
		ChangeBrightnessByNBrickTest.class,
		ChangeXByNBrickTest.class,
		ShowBrickTest.class,
		SetColorBrickTest.class,
		CameraBrickTest.class,
		LoopBrickTest.class,
		ShowTextBrickTest.class,
		LegoEv3MotorStopBrickTest.class,
		LegoNxtMotorTurnAngleBrickTest.class,
		ForeverBrickTest.class,
		ChangeSizeByNBrickTest.class,
		AddItemToUserListTest.class,
		ClearGraphicEffectBrickTest.class,
		LegoNXTMotorMoveBrickTest.class,
		WhenStartedBrickTest.class,
		LegoEv3MotorTurnAngleBrickTest.class,
		BroadcastForClonesRegressionTest.class,
		TurnLeftBrickTest.class,
		SetXBrickTest.class,
		StagePausedTest.class,
		StageSimpleTest.class,
		MultipleBroadcastsTest.class,
		BroadcastReceiverRegressionTest.class,
		ObjectVariableTest.class,
		MultipleBroadcastsTest.class,
		BroadcastReceiverRegressionTest.class,
		FormulaEditorKeyboardTest.class,
		FormulaEditorTest.class,
		SettingsActivityTest.class,
		SettingsActivityTest.class,
		FormulaEditorComputeDialogTest.class,
		RenameSpriteDialogTest.class,
		DeleteSpriteDialogTest.class,
		DeleteSoundDialogTest.class,
		TermsOfUseDialogTest.class,
		DeleteLookDialogTest.class,
		AboutDialogTest.class
})
public class AllEspressoTestsSuite {
}
