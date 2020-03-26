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
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeSizeByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ChangeSizeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoTakingPictureBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NoteBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#NoteBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#InsertItemIntoUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneFlipBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneFlipBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Phiro%20Bricks/#PhiroMotorMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayForBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SayForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneEmergencyBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneEmergencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneMoveRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#IfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.NextLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#NextLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetBackgroundByIndexAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ShowBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#SpeakAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#HideTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneSwitchCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetFrictionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetFrictionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneTurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#PlaySoundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroPlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Phiro%20Bricks/#PhiroPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetPhysicsObjectTypeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoTurnBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#RepeatBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SayBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SayBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBrightnessBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetBrightnessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WebRequestBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#WebRequestBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego EV3%20Bricks/#LegoEv3MotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.HideBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#HideBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeYByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#ChangeYByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#IfOnEdgeBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenNfcBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#WhenNfcBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#BroadcastWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenUpBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Pen%20Bricks/#PenUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#CameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#IfThenLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBounceOffBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#WhenBounceOffBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneStartBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#SceneStartBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego EV3%20Bricks/#LegoEv3MotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego NXT%20Bricks/#LegoNxtMotorMoveBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RepeatUntilBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#RepeatUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Phiro%20Bricks/#PhiroMotorMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicElseBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#IfLogicElseBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoNoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Raspberry Pi%20Bricks/#RaspiIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeXByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#ChangeXByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeColorByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ChangeColorByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#TurnLeftSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego EV3%20Bricks/#LegoEv3PlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#TurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.CloneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#CloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#TurnLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ClearGraphicEffectBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#BroadcastBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FlashBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#FlashBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopAllSoundsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#StopAllSoundsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#WriteListOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoSoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PreviousLookBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#PreviousLookBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ComeToFrontBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#ComeToFrontBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#ReadListFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PenDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Pen%20Bricks/#PenDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneMoveLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetSizeToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetSizeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitUntilBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#WaitUntilBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Arduino%20Bricks/#ArduinoSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#ChangeVolumeByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoJumpLongBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneMoveBackwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StampBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Pen%20Bricks/#StampBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Phiro%20Bricks/#PhiroIfLogicBeginBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiPwmBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Raspberry Pi%20Bricks/#RaspiPwmBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaceAtBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#PlaceAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopScriptBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#StopScriptBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Arduino%20Bricks/#ArduinoSendPWMValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DronePlayLedAnimationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#PointToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetXBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetXBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#WhenConditionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Raspberry Pi%20Bricks/#RaspiSendDigitalValueBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#BroadcastReceiverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SpeakBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#SpeakBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfLogicEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#IfLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#WhenClonedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneMoveDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearBackgroundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Pen%20Bricks/#ClearBackgroundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AssertEqualsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Testing%20Bricks/#AssertEqualsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroMotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Phiro%20Bricks/#PhiroMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TapAtBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Testing%20Bricks/#TapAtBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoJumpHighBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.FinishStageBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Testing%20Bricks/#FinishStageBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoRotateRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Embroidery%20Bricks/#StitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenSizeBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Pen%20Bricks/#SetPenSizeBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ChangeBrightnessByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego EV3%20Bricks/#LegoEv3SetLedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVolumeToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#SetVolumeToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PointInDirectionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#PointInDirectionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SceneTransitionBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#SceneTransitionBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoRotateLeftBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVariableBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#SetVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoNStepsBackBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#GoNStepsBackBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkForBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ThinkForBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetLookByIndexBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetLookByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.IfThenLogicEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#IfThenLogicEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#SetTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetTransparencyBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetTransparencyBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ForeverBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#ForeverBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ChangeTransparencyByNBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetRotationStyleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetRotationStyleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetBackgroundAndWaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#WhenBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#WhenStartedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#DeleteItemOfUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ClearUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#ClearUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GoToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#GoToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego NXT%20Bricks/#LegoNxtMotorStopBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#WhenTouchDownBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#WaitBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetGravityBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetGravityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetMassBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetMassBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#ReadVariableFromDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.GlideToBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#GlideToBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego EV3%20Bricks/#LegoEv3MotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Event%20Bricks/#WhenBackgroundChangesBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego NXT%20Bricks/#LegoNxtMotorTurnAngleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Raspberry Pi%20Bricks/#WhenRaspiPinChangedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#SetBackgroundByIndexBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneTakeOffLandBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Lego NXT%20Bricks/#LegoNxtPlayToneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WaitTillIdleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Testing%20Bricks/#WaitTillIdleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DeleteThisCloneBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Control%20Bricks/#DeleteThisCloneBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetNfcTagBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#SetNfcTagBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PhiroRGBLightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Phiro%20Bricks/#PhiroRGBLightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AddItemToUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#AddItemToUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneTurnRightBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneTurnRightBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Chromecast%20Bricks/#WhenGamepadButtonBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskSpeechBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#AskSpeechBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#ReplaceItemInUserListBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChooseCameraBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ChooseCameraBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetVelocityBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetVelocityBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveForwardBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneMoveForwardBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.DroneMoveUpBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/AR.Drone 2.0%20Bricks/#DroneMoveUpBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TurnRightSpeedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#TurnRightSpeedBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetBounceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetBounceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#ShowTextBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.VibrationBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#VibrationBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#WriteVariableOnDeviceBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Jumping Sumo%20Bricks/#JumpingSumoAnimationsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetPenColorBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Pen%20Bricks/#SetPenColorBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndlessBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#LoopEndlessBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.PlaySoundBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Sound%20Bricks/#PlaySoundBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ThinkBubbleBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#ThinkBubbleBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.AskBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Looks%20Bricks/#AskBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.LoopEndBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/No match%20Bricks/#LoopEndBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.SetYBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#SetYBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ChangeVariableBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#ChangeVariableBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.MoveNStepsBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Motion%20Bricks/#MoveNStepsBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Data%20Bricks/#ShowTextColorSizeAlignmentBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.RunningStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Embroidery%20Bricks/#RunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.StopRunningStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Embroidery%20Bricks/#StopRunningStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.ZigZagStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Embroidery%20Bricks/#ZigZagStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.TripleStitchBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Embroidery%20Bricks/#TripleStitchBrick");
		brickToHelpUrlMapping.put("org.catrobat.catroid.content.bricks.UserDefinedBrick",
				"https://wiki.catrobat.org/bin/view/Documentation/Brick%20Documentation/Your bricks%20Bricks/#UserDefinedBrick");
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
