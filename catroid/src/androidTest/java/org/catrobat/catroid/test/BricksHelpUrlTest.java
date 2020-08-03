/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeSizeByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeSizeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoTakingPictureBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NoteBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/NoteBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/InsertItemIntoUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneFlipBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneFlipBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PhiroMotorMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayForBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SayForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneEmergencyBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneEmergencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneMoveRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/IfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NextLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/NextLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetBackgroundByIndexAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ShowBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SpeakAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/HideTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneSwitchCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetFrictionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetFrictionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneTurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PlaySoundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroPlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PhiroPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetPhysicsObjectTypeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoTurnBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/RepeatBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForVariableFromToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ForVariableFromToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForItemInUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ForItemInUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SayBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBrightnessBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetBrightnessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WebRequestBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WebRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LookRequestBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LookRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BackgroundRequestBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/BackgroundRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoEv3MotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/HideBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeYByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeYByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/IfOnEdgeBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenNfcBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenNfcBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/BroadcastWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenUpBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PenUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/CameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/IfThenLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBounceOffBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenBounceOffBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneStartBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SceneStartBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoEv3MotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoNxtMotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatUntilBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/RepeatUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PhiroMotorMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicElseBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/IfLogicElseBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoNoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/RaspiIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeXByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeXByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeColorByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeColorByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/TurnLeftSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoEv3PlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/TurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CloneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/CloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/TurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ClearGraphicEffectBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/BroadcastBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FlashBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/FlashBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StopSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopAllSoundsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StopAllSoundsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WriteListOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PreviousLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PreviousLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ComeToFrontBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ComeToFrontBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ReadListFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PenDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneMoveLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetSizeToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetSizeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitUntilBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WaitUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ArduinoSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeVolumeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoJumpLongBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StampBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StampBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PhiroIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiPwmBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/RaspiPwmBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaceAtBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PlaceAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopScriptBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StopScriptBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ArduinoSendPWMValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DronePlayLedAnimationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PointToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetXBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetXBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenConditionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/RaspiSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/BroadcastReceiverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SpeakBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/IfLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenClonedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneMoveDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearBackgroundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ClearBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AssertEqualsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AssertEqualsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PhiroMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TapAtBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/TapAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoJumpHighBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FinishStageBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/FinishStageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoRotateRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenSizeBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetPenSizeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeBrightnessByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoEv3SetLedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVolumeToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetVolumeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointInDirectionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PointInDirectionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneTransitionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SceneTransitionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoRotateLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVariableBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoNStepsBackBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/GoNStepsBackBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkForBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ThinkForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookByIndexBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetLookByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/IfThenLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTransparencyBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetTransparencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForeverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ForeverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeTransparencyByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetRotationStyleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetRotationStyleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetBackgroundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenStartedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DeleteItemOfUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ClearUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/GoToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoNxtMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenTouchDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetGravityBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetGravityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetMassBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetMassBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ReadVariableFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GlideToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/GlideToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoEv3MotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenBackgroundChangesBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoNxtMotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenRaspiPinChangedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetBackgroundByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneTakeOffLandBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LegoNxtPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitTillIdleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WaitTillIdleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteThisCloneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DeleteThisCloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetNfcTagBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetNfcTagBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroRGBLightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PhiroRGBLightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AddItemToUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AddItemToUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneTurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WhenGamepadButtonBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskSpeechBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AskSpeechBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ReplaceItemInUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChooseCameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChooseCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVelocityBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetVelocityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveUpBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/DroneMoveUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightSpeedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/TurnRightSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBounceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ShowTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.VibrationBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/VibrationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WriteVariableOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteVariableToFileBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WriteVariableToFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ReadVariableFromFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/JumpingSumoAnimationsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetPenColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndlessBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LoopEndlessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/PlaySoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetInstrumentBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetInstrumentBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ThinkBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AskBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/LoopEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetYBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetYBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVariableBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ChangeVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.MoveNStepsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/MoveNStepsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ShowTextColorSizeAlignmentBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RunningStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/RunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopRunningStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StopRunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ZigZagStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ZigZagStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TripleStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/TripleStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/WriteEmbroideryToFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.UserDefinedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/UserDefinedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/UserDefinedReceiverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StoreCSVIntoUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AssertUserListsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AssertUserListsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ExitStageBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ExitStageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParameterizedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ParameterizedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParameterizedEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/ParameterizedEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TapForBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/TapForBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StartListeningBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/StartListeningBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetListeningLanguageBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/SetListeningLanguageBrick");
	}

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		List<Object[]> parameters = new ArrayList<>();
		Set<Class> brickClasses = getAllBrickClasses();

		brickClasses = removeAbstractClasses(brickClasses);
		brickClasses = removeInnerClasses(brickClasses);
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
}
