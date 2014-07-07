/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.physics.content;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastEvent;
import org.catrobat.catroid.content.BroadcastEvent.BroadcastType;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.BroadcastAction;
import org.catrobat.catroid.content.actions.BroadcastNotifyAction;
import org.catrobat.catroid.content.actions.ChangeBrightnessByNAction;
import org.catrobat.catroid.content.actions.ChangeGhostEffectByNAction;
import org.catrobat.catroid.content.actions.ChangeSizeByNAction;
import org.catrobat.catroid.content.actions.ChangeVariableAction;
import org.catrobat.catroid.content.actions.ChangeVolumeByNAction;
import org.catrobat.catroid.content.actions.ChangeXByNAction;
import org.catrobat.catroid.content.actions.ChangeYByNAction;
import org.catrobat.catroid.content.actions.ClearGraphicEffectAction;
import org.catrobat.catroid.content.actions.ComeToFrontAction;
import org.catrobat.catroid.content.actions.DroneFlipAction;
import org.catrobat.catroid.content.actions.DroneMoveBackwardAction;
import org.catrobat.catroid.content.actions.DroneMoveDownAction;
import org.catrobat.catroid.content.actions.DroneMoveForwardAction;
import org.catrobat.catroid.content.actions.DroneMoveLeftAction;
import org.catrobat.catroid.content.actions.DroneMoveRightAction;
import org.catrobat.catroid.content.actions.DroneMoveUpAction;
import org.catrobat.catroid.content.actions.DronePlayLedAnimationAction;
import org.catrobat.catroid.content.actions.DroneTakeoffAction;
import org.catrobat.catroid.content.actions.DroneTurnLeftAction;
import org.catrobat.catroid.content.actions.DroneTurnLeftWithMagnetometerAction;
import org.catrobat.catroid.content.actions.DroneTurnRightAction;
import org.catrobat.catroid.content.actions.DroneTurnRightWithMagnetometerAction;
import org.catrobat.catroid.content.actions.GlideToAction;
import org.catrobat.catroid.content.actions.GoNStepsBackAction;
import org.catrobat.catroid.content.actions.HideAction;
import org.catrobat.catroid.content.actions.IfLogicAction;
import org.catrobat.catroid.content.actions.LedAction;
import org.catrobat.catroid.content.actions.LegoNxtMotorActionAction;
import org.catrobat.catroid.content.actions.LegoNxtMotorStopAction;
import org.catrobat.catroid.content.actions.LegoNxtMotorTurnAngleAction;
import org.catrobat.catroid.content.actions.LegoNxtPlayToneAction;
import org.catrobat.catroid.content.actions.MoveNStepsAction;
import org.catrobat.catroid.content.actions.NextLookAction;
import org.catrobat.catroid.content.actions.PlaySoundAction;
import org.catrobat.catroid.content.actions.PointInDirectionAction;
import org.catrobat.catroid.content.actions.PointToAction;
import org.catrobat.catroid.content.actions.RepeatAction;
import org.catrobat.catroid.content.actions.SetBrightnessAction;
import org.catrobat.catroid.content.actions.SetGhostEffectAction;
import org.catrobat.catroid.content.actions.SetLookAction;
import org.catrobat.catroid.content.actions.SetSizeToAction;
import org.catrobat.catroid.content.actions.SetVariableAction;
import org.catrobat.catroid.content.actions.SetVolumeToAction;
import org.catrobat.catroid.content.actions.SetXAction;
import org.catrobat.catroid.content.actions.SetYAction;
import org.catrobat.catroid.content.actions.ShowAction;
import org.catrobat.catroid.content.actions.SpeakAction;
import org.catrobat.catroid.content.actions.StopAllSoundsAction;
import org.catrobat.catroid.content.actions.TurnLeftAction;
import org.catrobat.catroid.content.actions.TurnRightAction;
import org.catrobat.catroid.content.actions.VibrateAction;
import org.catrobat.catroid.content.actions.WaitAction;
import org.catrobat.catroid.content.actions.conditional.IfOnEdgeBounceAction;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick.Motor;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.physics.PhysicsObject;

public class ActionFactory extends Actions {

	public Action createWaitAction(Sprite sprite, Formula delay) {
		WaitAction action = action(WaitAction.class);
		action.setSprite(sprite);
		action.setDelay(delay);
		return action;
	}

	public Action createBroadcastAction(Sprite sprite, String broadcastMessage) {
		BroadcastAction action = action(BroadcastAction.class);
		BroadcastEvent event = new BroadcastEvent();
		event.setSenderSprite(sprite);
		event.setBroadcastMessage(broadcastMessage);
		event.setType(BroadcastType.broadcast);
		action.setBroadcastEvent(event);
		return action;
	}

	public Action createBroadcastActionFromWaiter(Sprite sprite, String broadcastMessage) {
		BroadcastAction action = action(BroadcastAction.class);
		BroadcastEvent event = new BroadcastEvent();
		event.setSenderSprite(sprite);
		event.setBroadcastMessage(broadcastMessage);
		event.setRun(false);
		event.setType(BroadcastType.broadcastWait);
		action.setBroadcastEvent(event);
		return action;
	}

	public static Action createBroadcastNotifyAction(BroadcastEvent event) {
		BroadcastNotifyAction action = action(BroadcastNotifyAction.class);
		action.setEvent(event);
		return action;
	}

	public Action createChangeBrightnessByNAction(Sprite sprite, Formula changeBrightness) {
		ChangeBrightnessByNAction action = action(ChangeBrightnessByNAction.class);
		action.setSprite(sprite);
		action.setBrightness(changeBrightness);
		return action;
	}

	public Action createChangeGhostEffectByNAction(Sprite sprite, Formula ghostEffect) {
		ChangeGhostEffectByNAction action = action(ChangeGhostEffectByNAction.class);
		action.setSprite(sprite);
		action.setGhostEffect(ghostEffect);
		return action;
	}

	public Action createChangeSizeByNAction(Sprite sprite, Formula size) {
		ChangeSizeByNAction action = action(ChangeSizeByNAction.class);
		action.setSprite(sprite);
		action.setSize(size);
		return action;
	}

	public Action createChangeVolumeByNAction(Sprite sprite, Formula volume) {
		ChangeVolumeByNAction action = action(ChangeVolumeByNAction.class);
		action.setVolume(volume);
		action.setSprite(sprite);
		return action;
	}

	public Action createChangeXByNAction(Sprite sprite, Formula xMovement) {
		ChangeXByNAction action = action(ChangeXByNAction.class);
		action.setSprite(sprite);
		action.setxMovement(xMovement);
		return action;
	}

	public Action createChangeYByNAction(Sprite sprite, Formula yMovement) {
		ChangeYByNAction action = action(ChangeYByNAction.class);
		action.setSprite(sprite);
		action.setyMovement(yMovement);
		return action;
	}

	public Action createClearGraphicEffectAction(Sprite sprite) {
		ClearGraphicEffectAction action = action(ClearGraphicEffectAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createComeToFrontAction(Sprite sprite) {
		ComeToFrontAction action = action(ComeToFrontAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createGlideToAction(Sprite sprite, Formula x, Formula y, Formula duration) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(duration);
		action.setSprite(sprite);
		return action;
	}

	public Action createGlideToAction(Sprite sprite, Formula x, Formula y, Formula duration, Interpolation interpolation) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		action.setSprite(sprite);
		return action;
	}

	public Action createPlaceAtAction(Sprite sprite, Formula x, Formula y) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(0);
		action.setInterpolation(null);
		action.setSprite(sprite);
		return action;//XXX: wrong action???
	}

	public Action createGoNStepsBackAction(Sprite sprite, Formula steps) {
		GoNStepsBackAction action = action(GoNStepsBackAction.class);
		action.setSprite(sprite);
		action.setSteps(steps);
		return action;
	}

	public Action createHideAction(Sprite sprite) {
		HideAction action = action(HideAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createIfOnEdgeBounceAction(Sprite sprite) {
		IfOnEdgeBounceAction action = action(IfOnEdgeBounceAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createLegoNxtMotorActionAction(Sprite sprite, String motor, Motor motorEnum, Formula speed) {
		LegoNxtMotorActionAction action = action(LegoNxtMotorActionAction.class);
		action.setMotorEnum(motorEnum);
		action.setSprite(sprite);
		action.setSpeed(speed);
		return action;
	}

	public Action createLegoNxtMotorStopAction(org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick.Motor motorEnum) {
		LegoNxtMotorStopAction action = action(LegoNxtMotorStopAction.class);
		action.setMotorEnum(motorEnum);
		return action;
	}

	public Action createLegoNxtMotorTurnAngleAction(Sprite sprite,
			org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick.Motor motorEnum, Formula degrees) {
		LegoNxtMotorTurnAngleAction action = action(LegoNxtMotorTurnAngleAction.class);
		action.setMotorEnum(motorEnum);
		action.setSprite(sprite);
		action.setDegrees(degrees);
		return action;
	}

	public Action createLegoNxtPlayToneAction(Sprite sprite, Formula hertz, Formula durationInSeconds) {
		LegoNxtPlayToneAction action = action(LegoNxtPlayToneAction.class);
		action.setHertz(hertz);
		action.setSprite(sprite);
		action.setDurationInSeconds(durationInSeconds);
		return action;
	}

	public Action createMoveNStepsAction(Sprite sprite, Formula steps) {
		MoveNStepsAction action = action(MoveNStepsAction.class);
		action.setSprite(sprite);
		action.setSteps(steps);
		return action;
	}

	public Action createNextLookAction(Sprite sprite) {
		NextLookAction action = action(NextLookAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createPlaySoundAction(Sprite sprite, SoundInfo sound) {
		PlaySoundAction action = action(PlaySoundAction.class);
		action.setSprite(sprite);
		action.setSound(sound);
		return action;
	}

	public Action createPointInDirectionAction(Sprite sprite, Formula degrees) {
		PointInDirectionAction action = action(PointInDirectionAction.class);
		action.setSprite(sprite);
		action.setDegreesInUserInterfaceDimensionUnit(degrees);
		return action;
	}

	public Action createPointToAction(Sprite sprite, Sprite pointedSprite) {
		PointToAction action = action(PointToAction.class);
		action.setSprite(sprite);
		action.setPointedSprite(pointedSprite);
		return action;
	}

	public Action createSetBrightnessAction(Sprite sprite, Formula brightness) {
		SetBrightnessAction action = action(SetBrightnessAction.class);
		action.setSprite(sprite);
		action.setBrightness(brightness);
		return action;
	}

	public Action createSetGhostEffectAction(Sprite sprite, Formula transparency) {
		SetGhostEffectAction action = action(SetGhostEffectAction.class);
		action.setSprite(sprite);
		action.setTransparency(transparency);
		return action;
	}

	public Action createSetLookAction(Sprite sprite, LookData lookData) {
		SetLookAction action = action(SetLookAction.class);
		action.setSprite(sprite);
		action.setLookData(lookData);
		return action;
	}

	public Action createSetSizeToAction(Sprite sprite, Formula size) {
		SetSizeToAction action = action(SetSizeToAction.class);
		action.setSprite(sprite);
		action.setSize(size);
		return action;
	}

	public Action createSetVolumeToAction(Sprite sprite, Formula volume) {
		SetVolumeToAction action = action(SetVolumeToAction.class);
		action.setVolume(volume);
		action.setSprite(sprite);
		return action;
	}

	public Action createSetXAction(Sprite sprite, Formula x) {
		SetXAction action = action(SetXAction.class);
		action.setSprite(sprite);
		action.setX(x);
		return action;
	}

	public Action createSetYAction(Sprite sprite, Formula y) {
		SetYAction action = action(SetYAction.class);
		action.setSprite(sprite);
		action.setY(y);
		return action;
	}

	public Action createShowAction(Sprite sprite) {
		ShowAction action = action(ShowAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createSpeakAction(String text, SpeakBrick speakBrick) {
		SpeakAction action = action(SpeakAction.class);
		action.setText(text);
		return action;
	}

	public Action createStopAllSoundsAction() {
		return action(StopAllSoundsAction.class);
	}

	public Action createTurnLeftAction(Sprite sprite, Formula degrees) {
		TurnLeftAction action = action(TurnLeftAction.class);
		action.setSprite(sprite);
		action.setDegrees(degrees);
		return action;
	}

	public Action createTurnRightAction(Sprite sprite, Formula degrees) {
		TurnRightAction action = action(TurnRightAction.class);
		action.setSprite(sprite);
		action.setDegrees(degrees);
		return action;
	}

	public Action createChangeVariableAction(Sprite sprite, Formula variableFormula, UserVariable userVariable) {
		ChangeVariableAction action = action(ChangeVariableAction.class);
		action.setSprite(sprite);
		action.setChangeVariable(variableFormula);
		action.setUserVariable(userVariable);
		return action;
	}

	public Action createSetVariableAction(Sprite sprite, Formula variableFormula, UserVariable userVariable) {
		SetVariableAction action = action(SetVariableAction.class);
		action.setSprite(sprite);
		action.setChangeVariable(variableFormula);
		action.setUserVariable(userVariable);
		return action;
	}

	public Action createIfLogcAction(Sprite sprite, Formula condition, Action ifAction, Action elseAction) {
		IfLogicAction action = action(IfLogicAction.class);
		action.setIfAction(ifAction);
		action.setIfCondition(condition);
		action.setElseAction(elseAction);
		action.setSprite(sprite);
		return action;
	}

	public RepeatAction createRepeatAction(Sprite sprite, Formula count, Action repeatedAction) {
		RepeatAction action = action(RepeatAction.class);
		action.setRepeatCount(count);
		action.setAction(repeatedAction);
		action.setSprite(sprite);
		return action;
	}

	public WaitAction createDelayAction(Sprite sprite, Formula delay) {
		WaitAction action = action(WaitAction.class);
		action.setSprite(sprite);
		action.setDelay(delay);
		return action;
	}

	public Action createForeverAction(Sprite sprite, SequenceAction foreverSequence) {
		RepeatAction action = action(RepeatAction.class);
		action.setIsForeverRepeat(true);
		action.setAction(foreverSequence);
		action.setSprite(sprite);
		return action;
	}

	public Action createSetBounceFactorAction(Sprite sprite, Formula bounceFactor) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createTurnRightSpeedAction(Sprite sprite, Formula degreesPerSecond) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createTurnLeftSpeedAction(Sprite sprite, Formula degreesPerSecond) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetVelocityAction(Sprite sprite, Formula velocityX, Formula velocityY) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetPhysicsObjectTypeAction(Sprite sprite, PhysicsObject.Type type) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetMassAction(Sprite sprite, Formula mass) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetGravityAction(Sprite sprite, Formula gravityX, Formula gravityY) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetFrictionAction(Sprite sprite, Formula friction) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public static TemporalAction droneTakeOff() {
		return action(DroneTakeoffAction.class);
	}

	public static TemporalAction droneLand() {
		return action(DroneTakeoffAction.class);
	}

	public static TemporalAction droneMoveUp(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneMoveUpAction action = action(DroneMoveUpAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneMoveDown(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneMoveDownAction action = action(DroneMoveDownAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneMoveLeft(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneMoveLeftAction action = action(DroneMoveLeftAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneMoveRight(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneMoveRightAction action = action(DroneMoveRightAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneMoveForward(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneMoveForwardAction action = action(DroneMoveForwardAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneMoveBackward(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneMoveBackwardAction action = action(DroneMoveBackwardAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneTurnRight(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneTurnRightAction action = action(DroneTurnRightAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneTurnLeft(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneTurnLeftAction action = action(DroneTurnLeftAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneTurnLeftMagneto(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneTurnLeftWithMagnetometerAction action = action(DroneTurnLeftWithMagnetometerAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction droneTurnRightMagneto(Sprite sprite, Formula seconds, Formula powerInPercent) {
		DroneTurnRightWithMagnetometerAction action = action(DroneTurnRightWithMagnetometerAction.class);
		action.setSprite(sprite);
		action.setDelay(seconds);
		action.setPower(powerInPercent);
		return action;
	}

	public static TemporalAction dronePlayLedAnimation() {
		return action(DronePlayLedAnimationAction.class);
	}

	public static TemporalAction droneFlip() {
		return action(DroneFlipAction.class);
	}


	public static LedAction lights(boolean ledValue) {
		LedAction action = action(LedAction.class);
		action.setLedValue(ledValue);
		return action;
	}

	public static VibrateAction vibrate(Sprite sprite, Formula duration) {
		VibrateAction action = action(VibrateAction.class);
		action.setSprite(sprite);
		action.setDuration(duration);
		return action;
	}
}
