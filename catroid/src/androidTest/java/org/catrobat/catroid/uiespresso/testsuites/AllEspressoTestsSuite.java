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
import org.catrobat.catroid.uiespresso.content.brick.MoveNStepsBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.NextLookBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.NoteBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PhiroMoveMotorBackwardBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PhiroMoveMotorForwardBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PlaySoundAndWaitBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.PointInDirectionBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.RepeatBrickTest;
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
		TermsOfUseDialogTest.class,
		FormulaEditorComputeDialogTest.class,
		AboutDialogTest.class,
		RenameSpriteDialogTest.class,
		DeleteSpriteDialogTest.class,
		DeleteLookDialogTest.class,
		DeleteSoundDialogTest.class,
		SettingsActivityTest.class,
		NoteBrickTest.class,
		PointInDirectionBrickTest.class,
		ChangeYByNBrickTest.class,
		ChangeBrightnessByNBrickTest.class,
		StopScriptBrickTest.class,
		StopAllSoundsBrickTest.class,
		ChangeTransparencyByNBrickTest.class,
		BroadcastBricksTest.class,
		LegoNXTMotorMoveBrickTest.class,
		SceneTransmitionBrickTest.class,
		TurnRightBrickTest.class,
		ChangeVolumeByNBrickTest.class,
		ShowTextBrickTest.class,
		SetXBrickTest.class,
		SetYBrickTest.class,
		LegoNxtPlayToneBrickTest.class,
		SpeakAndWaitBrickTest.class,
		DeleteItemOfUserListBrickTest.class,
		BrickValueParameterTest.class,
		PlaySoundAndWaitBrickTest.class,
		SetColorBrickTest.class,
		MoveNStepsBrickTest.class,
		NextLookBrickTest.class,
		ChangeXByNBrickTest.class,
		ShowBrickTest.class,
		SetLookByIndexBrickTest.class,
		SpeakBrickTest.class,
		SetTransparencyBrickTest.class,
		WhenNfcBrickStageTest.class,
		WhenStartedBrickTest.class,
		GoNStepsBackTest.class,
		LegoEv3MotorTurnAngleBrickTest.class,
		ClearGraphicEffectBrickTest.class,
		SetRotationStyleBrickTest.class,
		SetBrightnessBrickTest.class,
		ArduinoSendDigitalValueBrickTest.class,
		WhenNfcBrickTest.class,
		AskBrickTest.class,
		ChangeColorByNBrickTest.class,
		ChangeSizeByNBrickTest.class,
		LegoEv3PlayToneBrickTest.class,
		LegoNXTMotorStopBrickTest.class,
		LegoEv3MotorStopBrickTest.class,
		RepeatBrickTest.class,
		ComeToFrontBrickTest.class,
		GoToBrickTest.class,
		GlideToBrickTest.class,
		LegoEv3MotorMoveBrickTest.class,
		ForeverBrickTest.class,
		HideBrickTest.class,
		TurnLeftBrickTest.class,
		ArduinoSendPWMValueBrickTest.class,
		LegoNxtMotorTurnAngleBrickTest.class,
		PhiroMoveMotorForwardBrickTest.class,
		DragNDropBricksTest.class,
		AddItemToUserListTest.class,
		SetVolumeToBrickTest.class,
		PhiroMoveMotorBackwardBrickTest.class,
		LegoEV3SetLedBrickTest.class,
		CameraBrickTest.class,
		SetSizeToBrickTest.class,
		VariableBrickTest.class,
		WaitBrickTest.class,
		MultipleBroadcastsTest.class,
		BroadcastReceiverRegressionTest.class,
		StagePausedTest.class,
		StageSimpleTest.class,
		ObjectVariableTest.class,
		SmokeTest.class,
		FormulaEditorTest.class,
		FormulaEditorKeyboardTest.class,
		FaceDetectionResourceStartedTest.class,
		PocketMusicActivityTest.class,
		FlakyTestTest.class
})
public class AllEspressoTestsSuite {
}
