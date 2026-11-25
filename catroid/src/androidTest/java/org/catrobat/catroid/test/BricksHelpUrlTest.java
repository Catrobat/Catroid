/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import java.util.Locale;
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
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeSizeByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeSizeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoTakingPictureBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NoteBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/NoteBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/InsertItemIntoUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneFlipBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneFlipBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PhiroMotorMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayForBubbleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SayForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneEmergencyBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneEmergencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveRightBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneMoveRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicBeginBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/IfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NextLookBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/NextLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetBackgroundByIndexAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ShowBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakAndWaitBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SpeakAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideTextBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/HideTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneSwitchCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetFrictionBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetFrictionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnLeftBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneTurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PlaySoundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundAtBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PlaySoundAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroPlayToneBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PhiroPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetPhysicsObjectTypeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoTurnBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/RepeatBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForVariableFromToBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ForVariableFromToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForItemInUserListBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ForItemInUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayBubbleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SayBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBrightnessBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetBrightnessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WebRequestBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WebRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LookRequestBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LookRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BackgroundRequestBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/BackgroundRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoEv3MotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/HideBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeYByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeYByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/IfOnEdgeBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenNfcBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenNfcBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastWaitBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/BroadcastWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenUpBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PenUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CameraBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/CameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/IfThenLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBounceOffBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenBounceOffBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneStartBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SceneStartBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoEv3MotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoNxtMotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatUntilBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/RepeatUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PhiroMotorMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicElseBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/IfLogicElseBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoNoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/RaspiIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeXByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeXByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeColorByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeColorByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetThreadColorBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetThreadColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TurnLeftSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoEv3PlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CloneBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/CloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ClearGraphicEffectBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/BroadcastBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FlashBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/FlashBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopSoundBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StopSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopAllSoundsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StopAllSoundsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WriteListOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PreviousLookBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PreviousLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ComeToFrontBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ComeToFrontBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ReadListFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenDownBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PenDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveLeftBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneMoveLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetSizeToBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetSizeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitUntilBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WaitUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ArduinoSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeVolumeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoJumpLongBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StampBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StampBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PhiroIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiPwmBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/RaspiPwmBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaceAtBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PlaceAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopScriptBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StopScriptBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ArduinoSendPWMValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DronePlayLedAnimationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointToBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PointToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetXBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetXBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenConditionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/RaspiSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/BroadcastReceiverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SpeakBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicEndBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/IfLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenClonedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveDownBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneMoveDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearBackgroundBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ClearBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AssertEqualsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/AssertEqualsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorStopBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PhiroMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TapAtBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TapAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoJumpHighBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FinishStageBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/FinishStageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoRotateRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StitchBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenSizeBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetPenSizeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeBrightnessByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoEv3SetLedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVolumeToBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetVolumeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointInDirectionBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PointInDirectionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneTransitionBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SceneTransitionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoRotateLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVariableBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoNStepsBackBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/GoNStepsBackBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkForBubbleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ThinkForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookByIndexBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetLookByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicEndBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/IfThenLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTextBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTransparencyBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetTransparencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForeverBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ForeverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeTransparencyByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetColorBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetRotationStyleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetRotationStyleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetBackgroundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenStartedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DeleteItemOfUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearUserListBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ClearUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoToBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/GoToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoNxtMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenTouchDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetGravityBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetGravityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetMassBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetMassBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ReadVariableFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GlideToBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/GlideToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoEv3MotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenBackgroundChangesBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoNxtMotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenRaspiPinChangedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetBackgroundByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneTakeOffLandBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LegoNxtPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitTillIdleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WaitTillIdleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteThisCloneBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DeleteThisCloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetNfcTagBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetNfcTagBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroRGBLightBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PhiroRGBLightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AddItemToUserListBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/AddItemToUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnRightBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneTurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WhenGamepadButtonBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskSpeechBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/AskSpeechBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ReplaceItemInUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChooseCameraBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChooseCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVelocityBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetVelocityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveForwardBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveUpBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DroneMoveUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightSpeedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TurnRightSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBounceBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ShowTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.VibrationBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/VibrationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WriteVariableOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteVariableToFileBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WriteVariableToFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ReadVariableFromFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/JumpingSumoAnimationsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenColorBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetPenColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndlessBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LoopEndlessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PlaySoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetInstrumentBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetInstrumentBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTempoBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetTempoBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeTempoByNBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeTempoByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PlayDrumForBeatsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkBubbleBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ThinkBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/AskBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/LoopEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetYBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetYBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVariableBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ChangeVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.MoveNStepsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/MoveNStepsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ShowTextColorSizeAlignmentBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RunningStitchBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/RunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopRunningStitchBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StopRunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ZigZagStitchBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ZigZagStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TripleStitchBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TripleStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SewUpBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SewUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/WriteEmbroideryToFileBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.UserDefinedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/UserDefinedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/UserDefinedReceiverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReportBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ReportBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StoreCSVIntoUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AssertUserListsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/AssertUserListsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ExitStageBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ExitStageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParameterizedBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ParameterizedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParameterizedEndBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ParameterizedEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TapForBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TapForBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TouchAndSlideBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/TouchAndSlideBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StartListeningBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StartListeningBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetListeningLanguageBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetListeningLanguageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PauseForBeatsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PauseForBeatsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteLookBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/DeleteLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ResetTimerBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ResetTimerBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PaintNewLookBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PaintNewLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/PlayNoteForBeatsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.OpenUrlBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/OpenUrlBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CopyLookBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/CopyLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.EditLookBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/EditLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.EmptyEventBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/EmptyEventBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FadeParticleEffectBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/FadeParticleEffectBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ParticleEffectAdditivityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetCameraFocusPointBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetParticleColorBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SetParticleColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StartCutBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StartCutBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopCutBrick", "https://wiki"
				+ ".catrobat.org/bin/view/Documentation/BrickDocumentation/StopCutBrick");
		brickToHelpUrlMapping.put(".catrobat.org/bin/view/Documentation/BrickDocumentation"
				+ "/StartCutBrick", "http://wiki.catrobat"
				+ ".org/bin/view/Documentation/BrickDocumentation/StartCutBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SaveLaserBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SaveLaserBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopEngraveBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StopEngraveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StartEngraveBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation"
						+ "/StartEngraveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SavePlotBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SavePlotBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StartPlotBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StartPlotBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopPlotBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/StopPlotBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArcBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ArcBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoThroughBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/GoThroughBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShareLaserBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/ShareLaserBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SharePlotBrick",
				"https://share.catrobat.org/app/docs/reference/bricks/by-class/SharePlotBrick");
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
		assertEquals(brickToHelpUrlMapping.get(simpleName) + "?locale=" + Locale.getDefault().getLanguage(), brickHelpUrl);
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
