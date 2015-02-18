/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneLandBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.LedOffBrick;
import org.catrobat.catroid.content.bricks.LedOnBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.ui.UserBrickScriptActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CategoryBricksFactory {

	public List<Brick> getBricks(String category, Sprite sprite, Context context) {
		UserBrickScriptActivity activity;
		try {
			activity = (UserBrickScriptActivity) context;
		} catch (ClassCastException e) {
			activity = null;
		}
		boolean isUserScriptMode = activity != null;
		List<Brick> tempList = new LinkedList<Brick>();
		List<Brick> toReturn = new ArrayList<Brick>();
		if (category.equals(context.getString(R.string.category_control))) {
			tempList = setupControlCategoryList(context);
		} else if (category.equals(context.getString(R.string.category_motion))) {
			tempList = setupMotionCategoryList(sprite);
		} else if (category.equals(context.getString(R.string.category_sound))) {
			tempList = setupSoundCategoryList();
		} else if (category.equals(context.getString(R.string.category_looks))) {
			tempList = setupLooksCategoryList();
		} else if (category.equals(context.getString(R.string.category_variables))) {
			tempList = setupVariablesCategoryList();
		} else if (category.equals(context.getString(R.string.category_user_bricks))) {
			tempList = setupUserBricksCategoryList();
		} else if (category.equals(context.getString(R.string.category_lego_nxt))) {
			tempList = setupLegoNxtCategoryList();
		} else if (category.equals(context.getString(R.string.category_drone))) {
			tempList = setupDroneCategoryList();
		}
		for (Brick brick : tempList) {
			ScriptBrick brickAsScriptBrick;
			try {
				brickAsScriptBrick = (ScriptBrick) brick;
			} catch (ClassCastException e) {
				brickAsScriptBrick = null;
			}
			if (!isUserScriptMode || brickAsScriptBrick == null) {
				toReturn.add(brick);
			}
		}
		return toReturn;
	}


	private List<Brick> setupControlCategoryList(Context context) {
		List<Brick> controlBrickList = new ArrayList<Brick>();
		controlBrickList.add(new WhenStartedBrick(null));
		controlBrickList.add(new WhenBrick(null));
		controlBrickList.add(new WaitBrick(BrickValues.WAIT));

		final String broadcastMessage = MessageContainer.getFirst(context);
		controlBrickList.add(new BroadcastReceiverBrick(broadcastMessage));
		controlBrickList.add(new BroadcastBrick(broadcastMessage));
		controlBrickList.add(new BroadcastWaitBrick(broadcastMessage));

		controlBrickList.add(new NoteBrick(context.getString(R.string.brick_note_default_value)));
		controlBrickList.add(new ForeverBrick());
		controlBrickList.add(new IfLogicBeginBrick(0));
		controlBrickList.add(new RepeatBrick(BrickValues.REPEAT));

		return controlBrickList;
	}

	private List<Brick> setupUserBricksCategoryList() {
		List<UserBrick> userBrickList = ProjectManager.getInstance().getCurrentSprite().getUserBrickList();
		ArrayList<Brick> newList = new ArrayList<Brick>();

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

	private List<Brick> setupMotionCategoryList(Sprite sprite) {
		List<Brick> motionBrickList = new ArrayList<Brick>();
		motionBrickList.add(new PlaceAtBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION));
		motionBrickList.add(new SetXBrick(BrickValues.X_POSITION));
		motionBrickList.add(new SetYBrick(BrickValues.Y_POSITION));
		motionBrickList.add(new ChangeXByNBrick(BrickValues.CHANGE_X_BY));
		motionBrickList.add(new ChangeYByNBrick(BrickValues.CHANGE_Y_BY));

		if (!isBackground(sprite)) {
			motionBrickList.add(new IfOnEdgeBounceBrick());
		}

		motionBrickList.add(new MoveNStepsBrick(BrickValues.MOVE_STEPS));
		motionBrickList.add(new TurnLeftBrick(BrickValues.TURN_DEGREES));
		motionBrickList.add(new TurnRightBrick(BrickValues.TURN_DEGREES));
		motionBrickList.add(new PointInDirectionBrick(Direction.RIGHT));
		motionBrickList.add(new PointToBrick(null));
		motionBrickList.add(new GlideToBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION,
				BrickValues.GLIDE_SECONDS));

		if (!isBackground(sprite)) {
			motionBrickList.add(new GoNStepsBackBrick(BrickValues.GO_BACK));
			motionBrickList.add(new ComeToFrontBrick());
		}
		if (BuildConfig.FEATURE_VIBRATION_BRICK_ENABLED) {
			motionBrickList.add(new VibrationBrick(BrickValues.VIBRATE_MILLISECONDS));
		}

		return motionBrickList;
	}

	private List<Brick> setupSoundCategoryList() {
		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new PlaySoundBrick());
		soundBrickList.add(new StopAllSoundsBrick());
		soundBrickList.add(new SetVolumeToBrick(BrickValues.SET_VOLUME_TO));

		// workaround to set a negative default value for a Brick
		float positiveDefaultValueChangeVolumeBy = Math.abs(BrickValues.CHANGE_VOLUME_BY);
		FormulaElement defaultValueChangeVolumeBy = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(),
				null, null, new FormulaElement(ElementType.NUMBER, String.valueOf(positiveDefaultValueChangeVolumeBy),
						null));
		soundBrickList.add(new ChangeVolumeByNBrick(new Formula(defaultValueChangeVolumeBy)));

		soundBrickList.add(new SpeakBrick(BrickValues.SPEAK));

		return soundBrickList;
	}

	private List<Brick> setupLooksCategoryList() {
		List<Brick> looksBrickList = new ArrayList<Brick>();
		looksBrickList.add(new SetLookBrick());
		looksBrickList.add(new NextLookBrick());
		looksBrickList.add(new SetSizeToBrick(BrickValues.SET_SIZE_TO));
		looksBrickList.add(new ChangeSizeByNBrick(BrickValues.CHANGE_SIZE_BY));
		looksBrickList.add(new HideBrick());
		looksBrickList.add(new ShowBrick());
		looksBrickList.add(new SetGhostEffectBrick(BrickValues.SET_GHOST_EFFECT));
		looksBrickList.add(new ChangeGhostEffectByNBrick(BrickValues.CHANGE_GHOST_EFFECT));
		looksBrickList.add(new SetBrightnessBrick(BrickValues.SET_BRIGHTNESS_TO));
		looksBrickList.add(new ChangeBrightnessByNBrick(BrickValues.CHANGE_BRITHNESS_BY));
		looksBrickList.add(new ClearGraphicEffectBrick());
		if (BuildConfig.FEATURE_LED_BRICK_ENABLED) {
			looksBrickList.add(new LedOffBrick());
			looksBrickList.add(new LedOnBrick());
		}

		return looksBrickList;
	}

	private List<Brick> setupVariablesCategoryList() {
		List<Brick> userVariablesBrickList = new ArrayList<Brick>();
		userVariablesBrickList.add(new SetVariableBrick(0));
		userVariablesBrickList.add(new ChangeVariableBrick(0));
		return userVariablesBrickList;
	}

	private List<Brick> setupLegoNxtCategoryList() {
		List<Brick> legoNXTBrickList = new ArrayList<Brick>();
		legoNXTBrickList.add(new LegoNxtMotorTurnAngleBrick(LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A,
				BrickValues.LEGO_ANGLE));
		legoNXTBrickList.add(new LegoNxtMotorStopBrick(LegoNxtMotorStopBrick.Motor.MOTOR_A));
		legoNXTBrickList.add(new LegoNxtMotorActionBrick(LegoNxtMotorActionBrick.Motor.MOTOR_A,
				BrickValues.LEGO_SPEED));
		legoNXTBrickList.add(new LegoNxtPlayToneBrick(BrickValues.LEGO_FREQUENCY, BrickValues.LEGO_DURATION));

		return legoNXTBrickList;
	}

	private List<Brick> setupDroneCategoryList() {
		List<Brick> droneBrickList = new ArrayList<Brick>();
		droneBrickList.add(new DroneTakeOffBrick());
		droneBrickList.add(new DroneLandBrick());
		droneBrickList.add(new DroneFlipBrick());
		droneBrickList.add(new DroneMoveUpBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveDownBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveLeftBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveRightBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveForwardBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveBackwardBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneTurnLeftBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneTurnRightBrick(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));

		return droneBrickList;
	}

	private boolean isBackground(Sprite sprite) {
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}
}
