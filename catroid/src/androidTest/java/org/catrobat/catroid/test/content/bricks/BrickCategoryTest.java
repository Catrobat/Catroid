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

package org.catrobat.catroid.test.content.bricks;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.AssertEqualsBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.CameraBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ChooseCameraBrick;
import org.catrobat.catroid.content.bricks.ClearBackgroundBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.content.bricks.FlashBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick;
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick;
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.PreviousLookBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiPwmBrick;
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick;
import org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.RunningStitchBrick;
import org.catrobat.catroid.content.bricks.SayBubbleBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick;
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StampBrick;
import org.catrobat.catroid.content.bricks.StitchBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.TapAtBrick;
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick;
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WebRequestBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick;
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick;
import org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick;
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class BrickCategoryTest {

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Motion", Arrays.asList(PlaceAtBrick.class,
						SetXBrick.class,
						SetYBrick.class,
						ChangeXByNBrick.class,
						ChangeYByNBrick.class,
						GoToBrick.class,
						IfOnEdgeBounceBrick.class,
						MoveNStepsBrick.class,
						TurnLeftBrick.class,
						TurnRightBrick.class,
						PointInDirectionBrick.class,
						PointToBrick.class,
						SetRotationStyleBrick.class,
						GlideToBrick.class,
						GoNStepsBackBrick.class,
						ComeToFrontBrick.class,
						VibrationBrick.class,
						SetPhysicsObjectTypeBrick.class,
						WhenBounceOffBrick.class,
						SetVelocityBrick.class,
						TurnLeftSpeedBrick.class,
						TurnRightSpeedBrick.class,
						SetGravityBrick.class,
						SetMassBrick.class,
						SetBounceBrick.class,
						SetFrictionBrick.class)},
				{"Embroidery", Arrays.asList(StitchBrick.class,
						RunningStitchBrick.class)},
				{"Event", Arrays.asList(WhenStartedBrick.class,
						WhenBrick.class,
						WhenTouchDownBrick.class,
						BroadcastReceiverBrick.class,
						BroadcastBrick.class,
						BroadcastWaitBrick.class,
						WhenConditionBrick.class,
						WhenBounceOffBrick.class,
						WhenBackgroundChangesBrick.class,
						WhenClonedBrick.class)},
				{"Looks", Arrays.asList(SetLookBrick.class,
						SetLookByIndexBrick.class,
						NextLookBrick.class,
						PreviousLookBrick.class,
						SetSizeToBrick.class,
						ChangeSizeByNBrick.class,
						HideBrick.class,
						ShowBrick.class,
						AskBrick.class,
						SayBubbleBrick.class,
						SayForBubbleBrick.class,
						ThinkBubbleBrick.class,
						ThinkForBubbleBrick.class,
						ShowTextBrick.class,
						ShowTextColorSizeAlignmentBrick.class,
						SetTransparencyBrick.class,
						ChangeTransparencyByNBrick.class,
						SetBrightnessBrick.class,
						ChangeBrightnessByNBrick.class,
						SetColorBrick.class,
						ChangeColorByNBrick.class,
						ClearGraphicEffectBrick.class,
						WhenBackgroundChangesBrick.class,
						SetBackgroundBrick.class,
						SetBackgroundByIndexBrick.class,
						SetBackgroundAndWaitBrick.class,
						SetBackgroundByIndexAndWaitBrick.class,
						CameraBrick.class,
						ChooseCameraBrick.class,
						FlashBrick.class)},
				{"Pen", Arrays.asList(PenDownBrick.class,
						PenUpBrick.class,
						SetPenSizeBrick.class,
						SetPenColorBrick.class,
						StampBrick.class,
						ClearBackgroundBrick.class)},
				{"Sound", Arrays.asList(PlaySoundBrick.class,
						PlaySoundAndWaitBrick.class,
						StopAllSoundsBrick.class,
						SetVolumeToBrick.class,
						ChangeVolumeByNBrick.class,
						SpeakBrick.class,
						SpeakAndWaitBrick.class,
						AskSpeechBrick.class)},
				{"Control", Arrays.asList(WaitBrick.class,
						NoteBrick.class,
						ForeverBrick.class,
						IfLogicBeginBrick.class,
						IfThenLogicBeginBrick.class,
						WaitUntilBrick.class,
						RepeatBrick.class,
						RepeatUntilBrick.class,
						SceneTransitionBrick.class,
						SceneStartBrick.class,
						StopScriptBrick.class,
						CloneBrick.class,
						DeleteThisCloneBrick.class,
						WhenClonedBrick.class,
						WebRequestBrick.class)},
				{"Data", Arrays.asList(SetVariableBrick.class,
						ChangeVariableBrick.class,
						ShowTextBrick.class,
						ShowTextColorSizeAlignmentBrick.class,
						HideTextBrick.class,
						WriteVariableOnDeviceBrick.class,
						ReadVariableFromDeviceBrick.class,
						AddItemToUserListBrick.class,
						DeleteItemOfUserListBrick.class,
						InsertItemIntoUserListBrick.class,
						ReplaceItemInUserListBrick.class,
						WriteListOnDeviceBrick.class,
						ReadListFromDeviceBrick.class,
						AskBrick.class,
						AskSpeechBrick.class,
						WebRequestBrick.class)},
				{"Lego NXT", Arrays.asList(LegoNxtMotorTurnAngleBrick.class,
						LegoNxtMotorStopBrick.class,
						LegoNxtMotorMoveBrick.class,
						LegoNxtPlayToneBrick.class)},
				{"Lego EV3", Arrays.asList(LegoEv3MotorTurnAngleBrick.class,
						LegoEv3MotorMoveBrick.class,
						LegoEv3MotorStopBrick.class,
						LegoEv3PlayToneBrick.class,
						LegoEv3SetLedBrick.class)},
				{"AR.Drone 2.0", Arrays.asList(DroneTakeOffLandBrick.class,
						DroneEmergencyBrick.class,
						DroneMoveUpBrick.class,
						DroneMoveDownBrick.class,
						DroneMoveLeftBrick.class,
						DroneMoveRightBrick.class,
						DroneMoveForwardBrick.class,
						DroneMoveBackwardBrick.class,
						DroneTurnLeftBrick.class,
						DroneTurnRightBrick.class,
						DroneFlipBrick.class,
						DronePlayLedAnimationBrick.class,
						DroneSwitchCameraBrick.class)},
				{"Jumping Sumo", Arrays.asList(JumpingSumoMoveForwardBrick.class,
						JumpingSumoMoveBackwardBrick.class,
						JumpingSumoAnimationsBrick.class,
						JumpingSumoSoundBrick.class,
						JumpingSumoNoSoundBrick.class,
						JumpingSumoJumpLongBrick.class,
						JumpingSumoJumpHighBrick.class,
						JumpingSumoRotateLeftBrick.class,
						JumpingSumoRotateRightBrick.class,
						JumpingSumoTurnBrick.class,
						JumpingSumoTakingPictureBrick.class)},
				{"Phiro", Arrays.asList(PhiroMotorMoveForwardBrick.class,
						PhiroMotorMoveBackwardBrick.class,
						PhiroMotorStopBrick.class,
						PhiroPlayToneBrick.class,
						PhiroRGBLightBrick.class,
						PhiroIfLogicBeginBrick.class,
						SetVariableBrick.class,
						SetVariableBrick.class,
						SetVariableBrick.class,
						SetVariableBrick.class,
						SetVariableBrick.class,
						SetVariableBrick.class)},
				{"Arduino", Arrays.asList(ArduinoSendDigitalValueBrick.class,
						ArduinoSendPWMValueBrick.class)},
				{"Chromecast", Arrays.asList(WhenGamepadButtonBrick.class)},
				{"Raspberry Pi", Arrays.asList(WhenRaspiPinChangedBrick.class,
						RaspiIfLogicBeginBrick.class,
						RaspiSendDigitalValueBrick.class,
						RaspiPwmBrick.class)},
				{"Testing", Arrays.asList(AssertEqualsBrick.class,
						WaitTillIdleBrick.class,
						TapAtBrick.class)},
		});
	}

	@Parameterized.Parameter
	public String category;

	@Parameterized.Parameter(1)
	public List<Class> expectedClasses;

	private CategoryBricksFactory categoryBricksFactory;

	@Before
	public void setUp() throws Exception {
		PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit().clear().commit();

		createProject(InstrumentationRegistry.getTargetContext());

		categoryBricksFactory = new CategoryBricksFactory();
	}

	public void createProject(Context context) {
		Project project = new Project(context, getClass().getSimpleName());
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(new SetXBrick());
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}

	@Test
	public void testBrickCategory() {
		List<Brick> categoryBricks = categoryBricksFactory.getBricks(category, false,
				InstrumentationRegistry.getTargetContext());

		List<Class> brickClasses = new ArrayList<>();
		for (Brick brick : categoryBricks) {
			brickClasses.add(brick.getClass());
		}

		assertEquals(expectedClasses, brickClasses);
	}
}
