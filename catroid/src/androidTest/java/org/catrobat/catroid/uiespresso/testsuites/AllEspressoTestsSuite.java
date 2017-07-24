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
import org.catrobat.catroid.uiespresso.content.brick.AskBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.BrickValueParameterTest;
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
import org.catrobat.catroid.uiespresso.content.brick.SetColorBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.StopScriptBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.WhenNfcBrickStageTest;
import org.catrobat.catroid.uiespresso.content.brick.WhenNfcBrickTest;
import org.catrobat.catroid.uiespresso.formulaeditor.FormulaEditorKeyboardTest;
import org.catrobat.catroid.uiespresso.formulaeditor.FormulaEditorTest;
import org.catrobat.catroid.uiespresso.pocketmusic.PocketMusicActivityTest;
import org.catrobat.catroid.uiespresso.stage.StageSimpleTest;
import org.catrobat.catroid.uiespresso.ui.dialog.AboutDialogTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		PocketMusicActivityTest.class,
		SmokeTest.class,
		FlakyTestTest.class,
		WhenNfcBrickStageTest.class,
		LegoEv3PlayToneBrickTest.class,
		ComeToFrontBrickTest.class,
		HideBrickTest.class,
		ChangeTransparencyByNBrickTest.class,
		DragNDropBricksTest.class,
		GoNStepsBackTest.class,
		ChangeVolumeByNBrickTest.class,
		AskBrickTest.class,
		BrickValueParameterTest.class,
		NextLookBrickTest.class,
		ChangeColorByNBrickTest.class,
		DeleteItemOfUserListBrickTest.class,
		StopScriptBrickTest.class,
		GlideToBrickTest.class,
		MoveNStepsBrickTest.class,
		ChangeYByNBrickTest.class,
		LegoNXTMotorStopBrickTest.class,
		GoToBrickTest.class,
		WhenNfcBrickTest.class,
		LegoEV3SetLedBrickTest.class,
		LegoEv3MotorMoveBrickTest.class,
		NoteBrickTest.class,
		LegoNxtPlayToneBrickTest.class,
		ChangeBrightnessByNBrickTest.class,
		ChangeXByNBrickTest.class,
		SetColorBrickTest.class,
		LegoEv3MotorStopBrickTest.class,
		LegoNxtMotorTurnAngleBrickTest.class,
		ForeverBrickTest.class,
		ChangeSizeByNBrickTest.class,
		ClearGraphicEffectBrickTest.class,
		LegoNXTMotorMoveBrickTest.class,
		LegoEv3MotorTurnAngleBrickTest.class,
		StageSimpleTest.class,
		FormulaEditorKeyboardTest.class,
		FormulaEditorTest.class,
		AboutDialogTest.class
})
public class AllEspressoTestsSuite {
}
