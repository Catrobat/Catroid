/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.AskBrick;
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
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick;
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
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.PreviousLookBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiPwmBrick;
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.SayBubbleBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
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
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StampBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick;
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.UserBrickScriptActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CategoryBricksFactory {

	public List<Brick> getBricks(String category, Sprite sprite, Context context) {

		boolean isUserScriptMode = context instanceof UserBrickScriptActivity;
		List<Brick> tempList = new LinkedList<>();
		List<Brick> toReturn = new ArrayList<>();
		if (category.equals(context.getString(R.string.category_control))) {
			tempList = setupControlCategoryList(context);
		} else if (category.equals(context.getString(R.string.category_event))) {
			tempList = setupEventCategoryList(context);
		} else if (category.equals(context.getString(R.string.category_motion))) {
			tempList = setupMotionCategoryList(sprite, context);
		} else if (category.equals(context.getString(R.string.category_sound))) {
			tempList = setupSoundCategoryList(context);
		} else if (category.equals(context.getString(R.string.category_looks))) {
			boolean isBackgroundSprite = sprite.getName().equals(context.getString(R.string.background));
			tempList = setupLooksCategoryList(context, isBackgroundSprite);
		} else if (category.equals(context.getString(R.string.category_pen))) {
			tempList = setupPenCategoryList(sprite);
		} else if (category.equals(context.getString(R.string.category_user_bricks))) {
			tempList = setupUserBricksCategoryList();
		} else if (category.equals(context.getString(R.string.category_data))) {
			tempList = setupDataCategoryList(context);
		} else if (category.equals(context.getString(R.string.category_lego_nxt))) {
			tempList = setupLegoNxtCategoryList();
		} else if (category.equals(context.getString(R.string.category_arduino))) {
			tempList = setupArduinoCategoryList();
		} else if (category.equals(context.getString(R.string.category_drone))) {
			tempList = setupDroneCategoryList();
		} else if (category.equals(context.getString(R.string.category_jumping_sumo))) {
			tempList = setupJumpingSumoCategoryList();
		} else if (category.equals(context.getString(R.string.category_phiro))) {
			tempList = setupPhiroProCategoryList();
		} else if (category.equals(context.getString(R.string.category_raspi))) {
			tempList = setupRaspiCategoryList();
		}

		for (Brick brick : tempList) {
			if (!isUserScriptMode || !(brick instanceof ScriptBrick)) {
				toReturn.add(brick);
			}
		}
		return toReturn;
	}

	private List<Brick> setupEventCategoryList(Context context) {
		FormulaElement defaultIf = new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.toString(), null);
		defaultIf.setLeftChild(new FormulaElement(ElementType.NUMBER, "1", null));
		defaultIf.setRightChild(new FormulaElement(ElementType.NUMBER, "2", null));

		List<Brick> eventBrickList = new ArrayList<>();
		eventBrickList.add(new WhenStartedBrick(null));
		eventBrickList.add(new WhenBrick(null));
		eventBrickList.add(new WhenTouchDownBrick());
		final String broadcastMessage = MessageContainer.getFirst(context);
		eventBrickList.add(new BroadcastReceiverBrick(broadcastMessage));
		eventBrickList.add(new BroadcastBrick(broadcastMessage));
		eventBrickList.add(new BroadcastWaitBrick(broadcastMessage));
		eventBrickList.add(new WhenConditionBrick(new Formula(defaultIf)));
		eventBrickList.add(new CollisionReceiverBrick("object"));
		eventBrickList.add(new WhenBackgroundChangesBrick());
		eventBrickList.add(new WhenClonedBrick());

		if (SettingsActivity.isNfcSharedPreferenceEnabled(context)) {
			eventBrickList.add(new WhenNfcBrick());
		}
		return eventBrickList;
	}

	private List<Brick> setupControlCategoryList(Context context) {
		FormulaElement defaultIf = new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.toString(), null);
		defaultIf.setLeftChild(new FormulaElement(ElementType.NUMBER, "1", null));
		defaultIf.setRightChild(new FormulaElement(ElementType.NUMBER, "2", null));

		List<Brick> controlBrickList = new ArrayList<>();
		controlBrickList.add(new WaitBrick(BrickValues.WAIT));
		controlBrickList.add(new NoteBrick(context.getString(R.string.brick_note_default_value)));
		controlBrickList.add(new ForeverBrick());
		controlBrickList.add(new IfLogicBeginBrick(new Formula(defaultIf)));
		controlBrickList.add(new IfThenLogicBeginBrick(new Formula(defaultIf)));
		controlBrickList.add(new WaitUntilBrick(new Formula(defaultIf)));
		controlBrickList.add(new RepeatBrick(BrickValues.REPEAT));
		controlBrickList.add(new RepeatUntilBrick(new Formula(defaultIf)));
		controlBrickList.add(new SceneTransitionBrick(null));
		controlBrickList.add(new SceneStartBrick(null));

		if (SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
			controlBrickList.add(new PhiroIfLogicBeginBrick());
		}

		controlBrickList.add(new StopScriptBrick(BrickValues.STOP_THIS_SCRIPT));

		controlBrickList.add(new CloneBrick());
		controlBrickList.add(new DeleteThisCloneBrick());
		controlBrickList.add(new WhenClonedBrick());

		return controlBrickList;
	}

	private List<Brick> setupUserBricksCategoryList() {
		List<UserBrick> userBrickList = ProjectManager.getInstance().getCurrentSprite().getUserBrickList();
		ArrayList<Brick> newList = new ArrayList<>();

//		UserBrick userBrickWeAreAddingTo = ProjectManager.getInstance().getCurrentUserBrick();
//		if (userBrickWeAreAddingTo != null) {
//			// Maintain a Directed Acyclic Graph of UserBrick call order: Don't allow cycles.
//			for (UserBrick brick : userBrickList) {
//				if (!checkForCycle(brick, userBrickWeAreAddingTo)) {
//					newList.add(brick);

//				}
//			}
//		} else {
		if (userBrickList != null) {
			for (UserBrick brick : userBrickList) {
				newList.add(brick);
			}
		}
//		}
		return newList;
	}

//	public boolean checkForCycle(UserBrick currentBrick, UserBrick parentBrick) {
//		if (parentBrick.getId() == currentBrick.getId()) {
//			return true;
//		}
//
//		for (Brick childBrick : currentBrick.getDefinitionBrick().getUserScript().getBrickList()) {
//			if (childBrick instanceof UserBrick && checkForCycle(((UserBrick) childBrick), parentBrick)) {
//				return true;
//			}
//		}
//
//		return false;
//	}

	private List<Brick> setupMotionCategoryList(Sprite sprite, Context context) {
		List<Brick> motionBrickList = new ArrayList<>();
		motionBrickList.add(new PlaceAtBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION));
		motionBrickList.add(new SetXBrick(BrickValues.X_POSITION));
		motionBrickList.add(new SetYBrick(BrickValues.Y_POSITION));
		motionBrickList.add(new ChangeXByNBrick(BrickValues.CHANGE_X_BY));
		motionBrickList.add(new ChangeYByNBrick(BrickValues.CHANGE_Y_BY));
		motionBrickList.add(new GoToBrick(null));

		if (!isBackground(sprite)) {
			motionBrickList.add(new IfOnEdgeBounceBrick());
		}

		motionBrickList.add(new MoveNStepsBrick(BrickValues.MOVE_STEPS));
		motionBrickList.add(new TurnLeftBrick(BrickValues.TURN_DEGREES));
		motionBrickList.add(new TurnRightBrick(BrickValues.TURN_DEGREES));
		motionBrickList.add(new PointInDirectionBrick(Direction.RIGHT));
		motionBrickList.add(new PointToBrick(null));
		motionBrickList.add(new SetRotationStyleBrick());
		motionBrickList.add(new GlideToBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION,
				BrickValues.GLIDE_SECONDS));

		if (!isBackground(sprite)) {
			motionBrickList.add(new GoNStepsBackBrick(BrickValues.GO_BACK));
			motionBrickList.add(new ComeToFrontBrick());
		}

		motionBrickList.add(new VibrationBrick(BrickValues.VIBRATE_SECONDS));

		motionBrickList.add(new SetPhysicsObjectTypeBrick(BrickValues.PHYSIC_TYPE));
		motionBrickList.add(new SetVelocityBrick(BrickValues.PHYSIC_VELOCITY));
		motionBrickList.add(new TurnLeftSpeedBrick(BrickValues.PHYSIC_TURN_DEGREES));
		motionBrickList.add(new TurnRightSpeedBrick(BrickValues.PHYSIC_TURN_DEGREES));
		motionBrickList.add(new SetGravityBrick(BrickValues.PHYSIC_GRAVITY));
		motionBrickList.add(new SetMassBrick(BrickValues.PHYSIC_MASS));
		motionBrickList.add(new SetBounceBrick(BrickValues.PHYSIC_BOUNCE_FACTOR * 100));
		motionBrickList.add(new SetFrictionBrick(BrickValues.PHYSIC_FRICTION * 100));

		if (SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
			motionBrickList.add(new PhiroMotorMoveForwardBrick(PhiroMotorMoveForwardBrick.Motor.MOTOR_LEFT,
					BrickValues.PHIRO_SPEED));
			motionBrickList.add(new PhiroMotorMoveBackwardBrick(PhiroMotorMoveBackwardBrick.Motor.MOTOR_LEFT,
					BrickValues.PHIRO_SPEED));
			motionBrickList.add(new PhiroMotorStopBrick(PhiroMotorStopBrick.Motor.MOTOR_BOTH));
		}

		return motionBrickList;
	}

	private List<Brick> setupSoundCategoryList(Context context) {
		List<Brick> soundBrickList = new ArrayList<>();
		soundBrickList.add(new PlaySoundBrick());
		soundBrickList.add(new PlaySoundAndWaitBrick());
		soundBrickList.add(new StopAllSoundsBrick());
		soundBrickList.add(new SetVolumeToBrick(BrickValues.SET_VOLUME_TO));

		// workaround to set a negative default value for a Brick
		float positiveDefaultValueChangeVolumeBy = Math.abs(BrickValues.CHANGE_VOLUME_BY);
		FormulaElement defaultValueChangeVolumeBy = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(),
				null, null, new FormulaElement(ElementType.NUMBER, String.valueOf(positiveDefaultValueChangeVolumeBy),
				null)
		);
		soundBrickList.add(new ChangeVolumeByNBrick(new Formula(defaultValueChangeVolumeBy)));

		soundBrickList.add(new SpeakBrick(context.getString(R.string.brick_speak_default_value)));
		soundBrickList.add(new SpeakAndWaitBrick(context.getString(R.string.brick_speak_default_value)));

		if (SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
			soundBrickList.add(new PhiroPlayToneBrick(PhiroPlayToneBrick.Tone.DO,
					BrickValues.PHIRO_DURATION));
		}

		return soundBrickList;
	}

	private List<Brick> setupLooksCategoryList(Context context, boolean isBackgroundSprite) {
		List<Brick> looksBrickList = new ArrayList<>();

		if (!isBackgroundSprite) {
			looksBrickList.add(new SetLookBrick());
		}
		looksBrickList.add(new NextLookBrick());
		looksBrickList.add(new PreviousLookBrick());
		looksBrickList.add(new SetSizeToBrick(BrickValues.SET_SIZE_TO));
		looksBrickList.add(new ChangeSizeByNBrick(BrickValues.CHANGE_SIZE_BY));
		looksBrickList.add(new HideBrick());
		looksBrickList.add(new ShowBrick());
		looksBrickList.add(new AskBrick(context.getString(R.string.brick_ask_default_question)));
		if (!isBackgroundSprite) {
			looksBrickList.add(new SayBubbleBrick(context.getString(R.string.brick_say_bubble_default_value)));
			looksBrickList.add(new SayForBubbleBrick(context.getString(R.string.brick_say_bubble_default_value), 1.0f));
			looksBrickList.add(new ThinkBubbleBrick(context.getString(R.string.brick_think_bubble_default_value)));
			looksBrickList.add(new ThinkForBubbleBrick(context.getString(R.string.brick_think_bubble_default_value), 1.0f));
		}
		looksBrickList.add(new SetTransparencyBrick(BrickValues.SET_TRANSPARENCY));
		looksBrickList.add(new ChangeTransparencyByNBrick(BrickValues.CHANGE_TRANSPARENCY_EFFECT));
		looksBrickList.add(new SetBrightnessBrick(BrickValues.SET_BRIGHTNESS_TO));
		looksBrickList.add(new ChangeBrightnessByNBrick(BrickValues.CHANGE_BRITHNESS_BY));
		looksBrickList.add(new SetColorBrick(BrickValues.SET_COLOR_TO));
		looksBrickList.add(new ChangeColorByNBrick(BrickValues.CHANGE_COLOR_BY));
		looksBrickList.add(new ClearGraphicEffectBrick());
		looksBrickList.add(new WhenBackgroundChangesBrick());
		looksBrickList.add(new SetBackgroundBrick());
		looksBrickList.add(new SetBackgroundAndWaitBrick());
		looksBrickList.add(new CameraBrick());
		looksBrickList.add(new ChooseCameraBrick());
		looksBrickList.add(new FlashBrick());

		if (SettingsActivity.isPhiroSharedPreferenceEnabled(context)) {
			looksBrickList.add(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, BrickValues.PHIRO_VALUE_RED, BrickValues.PHIRO_VALUE_GREEN, BrickValues.PHIRO_VALUE_BLUE));
		}

		return looksBrickList;
	}

	private List<Brick> setupPenCategoryList(Sprite sprite) {
		List<Brick> penBrickList = new ArrayList<>();

		if (!isBackground(sprite)) {
			penBrickList.add(new PenDownBrick());
			penBrickList.add(new PenUpBrick());
			penBrickList.add(new SetPenSizeBrick(4));
			penBrickList.add(new SetPenColorBrick(0, 0, 255));
			penBrickList.add(new StampBrick());
		}

		penBrickList.add(new ClearBackgroundBrick());
		return penBrickList;
	}

	private List<Brick> setupDataCategoryList(Context context) {
		List<Brick> dataBrickList = new ArrayList<>();
		dataBrickList.add(new SetVariableBrick(BrickValues.SET_VARIABLE));
		dataBrickList.add(new ChangeVariableBrick(BrickValues.CHANGE_VARIABLE));
		dataBrickList.add(new ShowTextBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION));
		dataBrickList.add(new HideTextBrick());
		dataBrickList.add(new AddItemToUserListBrick(BrickValues.ADD_ITEM_TO_USERLIST));
		dataBrickList.add(new DeleteItemOfUserListBrick(BrickValues.DELETE_ITEM_OF_USERLIST));
		dataBrickList.add(new InsertItemIntoUserListBrick(BrickValues.INSERT_ITEM_INTO_USERLIST_VALUE, BrickValues.INSERT_ITEM_INTO_USERLIST_INDEX));
		dataBrickList.add(new ReplaceItemInUserListBrick(BrickValues.REPLACE_ITEM_IN_USERLIST_VALUE, BrickValues.REPLACE_ITEM_IN_USERLIST_INDEX));
		dataBrickList.add(new AskBrick(context.getString(R.string.brick_ask_default_question)));
		return dataBrickList;
	}

	private List<Brick> setupLegoNxtCategoryList() {
		List<Brick> legoNXTBrickList = new ArrayList<>();
		legoNXTBrickList.add(new LegoNxtMotorTurnAngleBrick(LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A,
				BrickValues.LEGO_ANGLE));
		legoNXTBrickList.add(new LegoNxtMotorStopBrick(LegoNxtMotorStopBrick.Motor.MOTOR_A));
		legoNXTBrickList.add(new LegoNxtMotorMoveBrick(LegoNxtMotorMoveBrick.Motor.MOTOR_A,
				BrickValues.LEGO_SPEED));
		legoNXTBrickList.add(new LegoNxtPlayToneBrick(BrickValues.LEGO_FREQUENCY, BrickValues.LEGO_DURATION));

		return legoNXTBrickList;
	}

	private List<Brick> setupDroneCategoryList() {
		List<Brick> droneBrickList = new ArrayList<>();
		droneBrickList.add(new DroneTakeOffLandBrick());
		droneBrickList.add(new DroneFlipBrick());
		droneBrickList.add(new DroneEmergencyBrick());
		droneBrickList.add(new DroneMoveUpBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveDownBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveLeftBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveRightBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveForwardBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveBackwardBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneTurnLeftBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneTurnRightBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneSwitchCameraBrick());

		/*
			 Deprecated
		     droneBrickList.add(new DroneAdvancedConfigBrick());
		*/

		// Only for demo purpose
/*		droneBrickList.add(new SetTextBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION,
				BrickValues.STRING_VALUE));*/

		return droneBrickList;
	}

	private List<Brick> setupJumpingSumoCategoryList() {
		List<Brick> jumpingSumoBrickList = new ArrayList<>();
		jumpingSumoBrickList.add(new JumpingSumoTurnBrick());
		jumpingSumoBrickList.add(new JumpingSumoJumpLongBrick());
		jumpingSumoBrickList.add(new JumpingSumoJumpHighBrick());
		jumpingSumoBrickList.add(new JumpingSumoMoveForwardBrick(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT));
		jumpingSumoBrickList.add(new JumpingSumoMoveBackwardBrick(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT));
		jumpingSumoBrickList.add(new JumpingSumoRotateLeftBrick(BrickValues.JUMPING_SUMO_ROTATE_DEFAULT_DEGREE));
		jumpingSumoBrickList.add(new JumpingSumoRotateRightBrick(BrickValues.JUMPING_SUMO_ROTATE_DEFAULT_DEGREE));


		return jumpingSumoBrickList;
	}

	private List<Brick> setupPhiroProCategoryList() {
		List<Brick> phiroProBrickList = new ArrayList<>();
		phiroProBrickList.add(new PhiroMotorMoveForwardBrick(PhiroMotorMoveForwardBrick.Motor.MOTOR_LEFT,
				BrickValues.PHIRO_SPEED));
		phiroProBrickList.add(new PhiroMotorMoveBackwardBrick(PhiroMotorMoveBackwardBrick.Motor.MOTOR_LEFT,
				BrickValues.PHIRO_SPEED));
		phiroProBrickList.add(new PhiroMotorStopBrick(PhiroMotorStopBrick.Motor.MOTOR_BOTH));
		phiroProBrickList.add(new PhiroPlayToneBrick(PhiroPlayToneBrick.Tone.DO,
				BrickValues.PHIRO_DURATION));
		phiroProBrickList.add(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, BrickValues.PHIRO_VALUE_RED, BrickValues.PHIRO_VALUE_GREEN, BrickValues.PHIRO_VALUE_BLUE));
		phiroProBrickList.add(new PhiroIfLogicBeginBrick());
		phiroProBrickList.add(new SetVariableBrick(Sensors.PHIRO_FRONT_LEFT));
		phiroProBrickList.add(new SetVariableBrick(Sensors.PHIRO_FRONT_RIGHT));
		phiroProBrickList.add(new SetVariableBrick(Sensors.PHIRO_SIDE_LEFT));
		phiroProBrickList.add(new SetVariableBrick(Sensors.PHIRO_SIDE_RIGHT));
		phiroProBrickList.add(new SetVariableBrick(Sensors.PHIRO_BOTTOM_LEFT));
		phiroProBrickList.add(new SetVariableBrick(Sensors.PHIRO_BOTTOM_RIGHT));

		return phiroProBrickList;
	}

	private List<Brick> setupArduinoCategoryList() {
		List<Brick> arduinoBrickList = new ArrayList<>();
		arduinoBrickList.add(new ArduinoSendDigitalValueBrick(BrickValues.ARDUINO_DIGITAL_INITIAL_PIN_NUMBER, BrickValues.ARDUINO_DIGITAL_INITIAL_PIN_VALUE));
		arduinoBrickList.add(new ArduinoSendPWMValueBrick(BrickValues.ARDUINO_PWM_INITIAL_PIN_NUMBER, BrickValues.ARDUINO_PWM_INITIAL_PIN_VALUE));

		return arduinoBrickList;
	}

	private List<Brick> setupRaspiCategoryList() {
		List<Brick> raspiBrickList = new ArrayList<>();
		raspiBrickList.add(new WhenRaspiPinChangedBrick(null));
		raspiBrickList.add(new RaspiIfLogicBeginBrick(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER));
		raspiBrickList.add(new RaspiSendDigitalValueBrick(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER, BrickValues.RASPI_DIGITAL_INITIAL_PIN_VALUE));
		raspiBrickList.add(new RaspiPwmBrick(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER, BrickValues
				.RASPI_PWM_INITIAL_FREQUENCY, BrickValues.RASPI_PWM_INITIAL_PERCENTAGE));

		return raspiBrickList;
	}

	private boolean isBackground(Sprite sprite) {
		if (ProjectManager.getInstance().getCurrentScene().getSpriteList().indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}

	public String getBrickCategory(Brick brick, Sprite sprite, Context context) {
		List<Brick> categoryBricks = new LinkedList<>();
		categoryBricks = setupControlCategoryList(context);

		Resources res = context.getResources();
		Configuration config = res.getConfiguration();
		Locale savedLocale = config.locale;
		config.locale = Locale.ENGLISH;
		res.updateConfiguration(config, null);
		String category = "No match";

		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_control);
			}
		}
		categoryBricks = setupEventCategoryList(context);
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_event);
			}
		}
		categoryBricks = setupMotionCategoryList(sprite, context);
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_motion);
			}
		}
		categoryBricks = setupSoundCategoryList(context);
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_sound);
			}
		}
		boolean isBackgroundSprite = sprite.getName().equals(context.getString(R.string.background));
		categoryBricks = setupLooksCategoryList(context, isBackgroundSprite);
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_looks);
			}
		}
		categoryBricks = setupPenCategoryList(sprite);
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_pen);
			}
		}
		categoryBricks = setupUserBricksCategoryList();
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_user_bricks);
			}
		}
		categoryBricks = setupDataCategoryList(context);
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_data);
			}
		}
		categoryBricks = setupLegoNxtCategoryList();
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_lego_nxt);
			}
		}
		categoryBricks = setupArduinoCategoryList();
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_arduino);
			}
		}
		categoryBricks = setupDroneCategoryList();
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_drone);
			}
		}
		categoryBricks = setupPhiroProCategoryList();
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_phiro);
			}
		}
		categoryBricks = setupRaspiCategoryList();
		for (Brick categoryBrick : categoryBricks) {
			if (brick.getClass().equals(categoryBrick.getClass())) {
				category = res.getString(R.string.category_raspi);
			}
		}

		if (brick instanceof AskBrick) {
			category = res.getString(R.string.category_looks);
		} else if (brick instanceof WhenClonedBrick) {
			category = res.getString(R.string.category_control);
		} else if (brick instanceof WhenBackgroundChangesBrick) {
			category = res.getString(R.string.category_event);
		} else if (brick instanceof SetVariableBrick) {
			category = res.getString(R.string.category_data);
		}

		config.locale = savedLocale;
		res.updateConfiguration(config, null);

		return category;
	}
}
