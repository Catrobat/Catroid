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
package org.catrobat.catroid.test;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.test.platform.app.InstrumentationRegistry;
import dalvik.system.DexFile;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class BricksHelpUrlTest {
	public static final String TAG = BricksHelpUrlTest.class.getSimpleName();
	public static Map<String, String> brickToHelpUrlMapping;

	static {
		brickToHelpUrlMapping = new HashMap<>();
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeSizeByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeSizeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoTakingPictureBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NoteBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/NoteBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/InsertItemIntoUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneFlipBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneFlipBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PhiroMotorMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayForBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SayForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneEmergencyBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneEmergencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneMoveRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/IfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NextLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/NextLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetBackgroundByIndexAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ShowBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SpeakAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/HideTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneSwitchCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetFrictionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetFrictionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneTurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PlaySoundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundAtBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PlaySoundAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroPlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PhiroPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetPhysicsObjectTypeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoTurnBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/RepeatBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForVariableFromToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ForVariableFromToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForItemInUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ForItemInUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SayBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBrightnessBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetBrightnessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WebRequestBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WebRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LookRequestBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LookRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BackgroundRequestBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/BackgroundRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoEv3MotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/HideBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeYByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeYByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/IfOnEdgeBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenNfcBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenNfcBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/BroadcastWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenUpBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PenUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/CameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/IfThenLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBounceOffBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenBounceOffBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneStartBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SceneStartBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoEv3MotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoNxtMotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatUntilBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/RepeatUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PhiroMotorMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicElseBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/IfLogicElseBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoNoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/RaspiIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeXByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeXByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeColorByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeColorByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetThreadColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetThreadColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TurnLeftSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoEv3PlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CloneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/CloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ClearGraphicEffectBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/BroadcastBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FlashBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/FlashBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StopSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopAllSoundsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StopAllSoundsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WriteListOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PreviousLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PreviousLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ComeToFrontBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ComeToFrontBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ReadListFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PenDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneMoveLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetSizeToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetSizeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitUntilBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WaitUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ArduinoSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeVolumeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoJumpLongBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StampBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StampBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PhiroIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiPwmBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/RaspiPwmBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaceAtBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PlaceAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopScriptBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StopScriptBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ArduinoSendPWMValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DronePlayLedAnimationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PointToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetXBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetXBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenConditionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/RaspiSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/BroadcastReceiverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SpeakBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/IfLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenClonedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneMoveDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearBackgroundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ClearBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AssertEqualsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/AssertEqualsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PhiroMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TapAtBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TapAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoJumpHighBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FinishStageBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/FinishStageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoRotateRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenSizeBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetPenSizeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeBrightnessByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoEv3SetLedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVolumeToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetVolumeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointInDirectionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PointInDirectionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneTransitionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SceneTransitionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoRotateLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVariableBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoNStepsBackBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/GoNStepsBackBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkForBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ThinkForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookByIndexBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetLookByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/IfThenLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTransparencyBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetTransparencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForeverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ForeverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeTransparencyByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetRotationStyleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetRotationStyleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetBackgroundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenStartedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DeleteItemOfUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ClearUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/GoToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoNxtMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenTouchDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetGravityBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetGravityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetMassBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetMassBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ReadVariableFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GlideToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/GlideToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoEv3MotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenBackgroundChangesBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoNxtMotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenRaspiPinChangedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetBackgroundByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneTakeOffLandBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LegoNxtPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitTillIdleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WaitTillIdleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteThisCloneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DeleteThisCloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetNfcTagBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetNfcTagBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroRGBLightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PhiroRGBLightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AddItemToUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/AddItemToUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneTurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WhenGamepadButtonBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskSpeechBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/AskSpeechBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ReplaceItemInUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChooseCameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChooseCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVelocityBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetVelocityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveUpBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DroneMoveUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightSpeedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TurnRightSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBounceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ShowTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.VibrationBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/VibrationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WriteVariableOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteVariableToFileBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WriteVariableToFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ReadVariableFromFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/JumpingSumoAnimationsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetPenColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndlessBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LoopEndlessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PlaySoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetInstrumentBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetInstrumentBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTempoBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetTempoBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeTempoByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeTempoByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PlayDrumForBeatsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ThinkBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/AskBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/LoopEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetYBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetYBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVariableBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ChangeVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.MoveNStepsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/MoveNStepsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ShowTextColorSizeAlignmentBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RunningStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/RunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopRunningStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StopRunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ZigZagStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ZigZagStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TripleStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TripleStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SewUpBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SewUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/WriteEmbroideryToFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.UserDefinedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/UserDefinedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/UserDefinedReceiverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReportBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ReportBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StoreCSVIntoUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AssertUserListsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/AssertUserListsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ExitStageBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ExitStageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParameterizedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ParameterizedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParameterizedEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ParameterizedEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TapForBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TapForBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TouchAndSlideBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/TouchAndSlideBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StartListeningBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/StartListeningBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetListeningLanguageBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetListeningLanguageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PauseForBeatsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PauseForBeatsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/DeleteLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ResetTimerBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ResetTimerBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PaintNewLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PaintNewLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/PlayNoteForBeatsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.OpenUrlBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/OpenUrlBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CopyLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/CopyLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.EditLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/EditLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.EmptyEventBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/EmptyEventBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FadeParticleEffectBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/FadeParticleEffectBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/ParticleEffectAdditivityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetCameraFocusPointBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetParticleColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/SetParticleColorBrick");
	}

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		List<Object[]> parameters = new ArrayList<>();
		Set<Class> brickClasses = getAllBrickClasses();

		brickClasses = removeAbstractClasses(brickClasses);
		brickClasses = removeInnerClasses(brickClasses);
		brickClasses = removeEndBrick(brickClasses);
		for (Class<?> brickClazz : brickClasses) {
			parameters.add(new Object[] {brickClazz.getName(), brickClazz});
		}

		return parameters;
	}

	@Parameterized.Parameter
	public String simpleName;

	@Parameterized.Parameter(1)
	public Class brickClass;

	private static Set<Class> getAllBrickClasses() {
		ArrayList<Class> classes = new ArrayList<>();
		try {
			String packageCodePath =
					InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageCodePath();
			DexFile dexFile = new DexFile(packageCodePath);
			for (Enumeration<String> iter = dexFile.entries(); iter.hasMoreElements(); ) {
				String className = iter.nextElement();
				if (className.contains("org.catrobat.catroid.content.bricks") && className.endsWith(
						"Brick")) {
					classes.add(Class.forName(className));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return new HashSet<>(classes);
	}

	@Before
	public void setUp() {
		ProjectManager.getInstance().setCurrentProject(
				new Project(InstrumentationRegistry.getInstrumentation().getTargetContext(), "empty"));
	}

	@Test
	public void testBrickHelpUrl() throws IllegalAccessException,
			InstantiationException {
		Brick brick = (Brick) brickClass.newInstance();
		String category = new CategoryBricksFactory().getBrickCategory(brick, false,
				InstrumentationRegistry.getInstrumentation().getTargetContext());
		String brickHelpUrl = brick.getHelpUrl(category);
		assertEquals(brickToHelpUrlMapping.get(simpleName), brickHelpUrl);
	}

	private static Set<Class> removeAbstractClasses(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			boolean isAbstract = Modifier.isAbstract(clazz.getModifiers());
			if (!isAbstract) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	private static Set<Class> removeInnerClasses(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			boolean isInnerClass = clazz.getEnclosingClass() != null;
			if (!isInnerClass) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	private static Set<Class> removeEndBrick(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			if (!clazz.getName().contains("EndBrick")) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}
}
