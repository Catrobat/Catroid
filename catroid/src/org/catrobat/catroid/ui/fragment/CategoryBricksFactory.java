/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;

import java.util.ArrayList;
import java.util.List;

public class CategoryBricksFactory {

	public List<Brick> getBricks(String category, Sprite sprite, Context context) {
		if (category.equals(context.getString(R.string.category_control))) {
			return setupControlCategoryList(sprite, context);
		} else if (category.equals(context.getString(R.string.category_motion))) {
			return setupMotionCategoryList(sprite);
		} else if (category.equals(context.getString(R.string.category_sound))) {
			return setupSoundCategoryList(sprite, context);
		} else if (category.equals(context.getString(R.string.category_looks))) {
			return setupLooksCategoryList(sprite);
		} else if (category.equals(context.getString(R.string.category_variables))) {
			return setupVariablesCategoryList(sprite);
		} else if (category.equals(context.getString(R.string.category_lego_nxt))) {
			return setupLegoNxtCategoryList(sprite);
		} else if (category.equals(context.getString(R.string.category_drone))) {
			return setupDroneCategoryList(sprite);
		}

		return new ArrayList<Brick>();
	}

	private List<Brick> setupControlCategoryList(Sprite sprite, Context context) {
		List<Brick> controlBrickList = new ArrayList<Brick>();
		controlBrickList.add(new WhenStartedBrick(sprite, null));
		controlBrickList.add(new WhenBrick(sprite, null));
		controlBrickList.add(new WaitBrick(sprite, BrickValues.WAIT));

		final String broadcastMessage = MessageContainer.getFirst(context);
		controlBrickList.add(new BroadcastReceiverBrick(sprite, broadcastMessage));
		controlBrickList.add(new BroadcastBrick(sprite, broadcastMessage));
		controlBrickList.add(new BroadcastWaitBrick(sprite, broadcastMessage));

		controlBrickList.add(new NoteBrick(sprite, context.getString(R.string.brick_note_default_value)));
		controlBrickList.add(new ForeverBrick(sprite));
		controlBrickList.add(new IfLogicBeginBrick(sprite, 0));
		controlBrickList.add(new RepeatBrick(sprite, BrickValues.REPEAT));

		return controlBrickList;
	}

	private List<Brick> setupMotionCategoryList(Sprite sprite) {
		List<Brick> motionBrickList = new ArrayList<Brick>();
		motionBrickList.add(new PlaceAtBrick(sprite, BrickValues.X_POSITION, BrickValues.Y_POSITION));
		motionBrickList.add(new SetXBrick(sprite, BrickValues.X_POSITION));
		motionBrickList.add(new SetYBrick(sprite, BrickValues.Y_POSITION));
		motionBrickList.add(new ChangeXByNBrick(sprite, BrickValues.CHANGE_X_BY));
		motionBrickList.add(new ChangeYByNBrick(sprite, BrickValues.CHANGE_Y_BY));

		if (!isBackground(sprite)) {
			motionBrickList.add(new IfOnEdgeBounceBrick(sprite));
		}

		motionBrickList.add(new MoveNStepsBrick(sprite, BrickValues.MOVE_STEPS));
		motionBrickList.add(new TurnLeftBrick(sprite, BrickValues.TURN_DEGREES));
		motionBrickList.add(new TurnRightBrick(sprite, BrickValues.TURN_DEGREES));
		motionBrickList.add(new PointInDirectionBrick(sprite, Direction.RIGHT));
		motionBrickList.add(new PointToBrick(sprite, null));
		motionBrickList.add(new GlideToBrick(sprite, BrickValues.X_POSITION, BrickValues.Y_POSITION,
				BrickValues.GLIDE_SECONDS));

		if (!isBackground(sprite)) {
			motionBrickList.add(new GoNStepsBackBrick(sprite, BrickValues.GO_BACK));
			motionBrickList.add(new ComeToFrontBrick(sprite));
		}

		return motionBrickList;
	}

	private List<Brick> setupSoundCategoryList(Sprite sprite, Context context) {
		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new PlaySoundBrick(sprite));
		soundBrickList.add(new StopAllSoundsBrick(sprite));
		soundBrickList.add(new SetVolumeToBrick(sprite, BrickValues.SET_VOLUME_TO));

		// workaround to set a negative default value for a Brick
		float positiveDefaultValueChangeVolumeBy = Math.abs(BrickValues.CHANGE_VOLUME_BY);
		FormulaElement defaultValueChangeVolumeBy = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(),
				null, null, new FormulaElement(ElementType.NUMBER, String.valueOf(positiveDefaultValueChangeVolumeBy),
						null));
		soundBrickList.add(new ChangeVolumeByNBrick(sprite, new Formula(defaultValueChangeVolumeBy)));

		soundBrickList.add(new SpeakBrick(sprite, context.getString(R.string.brick_speak_default_value)));

		return soundBrickList;
	}

	private List<Brick> setupLooksCategoryList(Sprite sprite) {
		List<Brick> looksBrickList = new ArrayList<Brick>();
		looksBrickList.add(new SetLookBrick(sprite));
		looksBrickList.add(new NextLookBrick(sprite));
		looksBrickList.add(new SetSizeToBrick(sprite, BrickValues.SET_SIZE_TO));
		looksBrickList.add(new ChangeSizeByNBrick(sprite, BrickValues.CHANGE_SIZE_BY));
		looksBrickList.add(new HideBrick(sprite));
		looksBrickList.add(new ShowBrick(sprite));
		looksBrickList.add(new SetGhostEffectBrick(sprite, BrickValues.SET_GHOST_EFFECT));
		looksBrickList.add(new ChangeGhostEffectByNBrick(sprite, BrickValues.CHANGE_GHOST_EFFECT));
		looksBrickList.add(new SetBrightnessBrick(sprite, BrickValues.SET_BRIGHTNESS_TO));
		looksBrickList.add(new ChangeBrightnessByNBrick(sprite, BrickValues.CHANGE_BRITHNESS_BY));
		looksBrickList.add(new ClearGraphicEffectBrick(sprite));
		if (BuildConfig.FEATURE_LED_BRICK_ENABLED) {
			looksBrickList.add(new LedOffBrick(sprite));
			looksBrickList.add(new LedOnBrick(sprite));
		}
		if (BuildConfig.FEATURE_VIBRATION_BRICK_ENABLED) {
			looksBrickList.add(new VibrationBrick(sprite, BrickValues.VIBRATE_MILLISECONDS));
		}

		return looksBrickList;
	}

	private List<Brick> setupVariablesCategoryList(Sprite sprite) {
		List<Brick> userVariablesBrickList = new ArrayList<Brick>();
		userVariablesBrickList.add(new SetVariableBrick(sprite, 0));
		userVariablesBrickList.add(new ChangeVariableBrick(sprite, 0));
		return userVariablesBrickList;
	}

	private List<Brick> setupLegoNxtCategoryList(Sprite sprite) {
		List<Brick> legoNXTBrickList = new ArrayList<Brick>();
		legoNXTBrickList.add(new LegoNxtMotorTurnAngleBrick(sprite, LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A,
				BrickValues.LEGO_ANGLE));
		legoNXTBrickList.add(new LegoNxtMotorStopBrick(sprite, LegoNxtMotorStopBrick.Motor.MOTOR_A));
		legoNXTBrickList.add(new LegoNxtMotorActionBrick(sprite, LegoNxtMotorActionBrick.Motor.MOTOR_A,
				BrickValues.LEGO_SPEED));
		legoNXTBrickList.add(new LegoNxtPlayToneBrick(sprite, BrickValues.LEGO_FREQUENCY, BrickValues.LEGO_DURATION));

		return legoNXTBrickList;
	}

	private List<Brick> setupDroneCategoryList(Sprite sprite) {
		List<Brick> droneBrickList = new ArrayList<Brick>();
		droneBrickList.add(new DroneTakeOffBrick(sprite));
		droneBrickList.add(new DroneLandBrick(sprite));
		droneBrickList.add(new DroneFlipBrick(sprite));
		droneBrickList.add(new DroneMoveUpBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveDownBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveLeftBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveRightBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveForwardBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneMoveBackwardBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneTurnLeftBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));
		droneBrickList.add(new DroneTurnRightBrick(sprite, BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (int) (BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100)));

		return droneBrickList;
	}

	private boolean isBackground(Sprite sprite) {
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}
}
