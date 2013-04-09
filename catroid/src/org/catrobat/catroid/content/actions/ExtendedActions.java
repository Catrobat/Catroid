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
package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastEvent;
import org.catrobat.catroid.content.BroadcastEvent.BroadcastType;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class ExtendedActions extends Actions {

	public static BroadcastAction broadcast(Sprite sprite, String broadcastMessage) {
		BroadcastAction action = action(BroadcastAction.class);
		BroadcastEvent event = new BroadcastEvent();
		event.setSenderSprite(sprite);
		event.setBroadcastMessage(broadcastMessage);
		event.setType(BroadcastType.broadcast);
		action.setBroadcastEvent(event);
		return action;
	}

	public static BroadcastAction broadcastFromWaiter(Sprite sprite, String broadcastMessage) {
		BroadcastAction action = action(BroadcastAction.class);
		BroadcastEvent event = new BroadcastEvent();
		event.setSenderSprite(sprite);
		event.setBroadcastMessage(broadcastMessage);
		event.setRun(false);
		event.setType(BroadcastType.broadcastWait);
		action.setBroadcastEvent(event);
		return action;
	}

	public static BroadcastNotifyAction broadcastNotify(BroadcastEvent event) {
		BroadcastNotifyAction action = action(BroadcastNotifyAction.class);
		action.setEvent(event);
		return action;
	}

	public static ChangeBrightnessByNAction changeBrightnessByN(Sprite sprite, Formula changeBrightness) {
		ChangeBrightnessByNAction action = action(ChangeBrightnessByNAction.class);
		action.setSprite(sprite);
		action.setBrightness(changeBrightness);
		return action;
	}

	public static ChangeGhostEffectByNAction changeGhostEffectByN(Sprite sprite, Formula ghostEffect) {
		ChangeGhostEffectByNAction action = action(ChangeGhostEffectByNAction.class);
		action.setSprite(sprite);
		action.setGhostEffect(ghostEffect);
		return action;
	}

	public static ChangeSizeByNAction changeSizeByN(Sprite sprite, Formula size) {
		ChangeSizeByNAction action = action(ChangeSizeByNAction.class);
		action.setSprite(sprite);
		action.setSize(size);
		return action;
	}

	public static ChangeVolumeByNAction changeVolumeByN(Sprite sprite, Formula volume) {
		ChangeVolumeByNAction action = action(ChangeVolumeByNAction.class);
		action.setVolume(volume);
		action.setSprite(sprite);
		return action;
	}

	public static ChangeXByNAction changeXByN(Sprite sprite, Formula xMovement) {
		ChangeXByNAction action = action(ChangeXByNAction.class);
		action.setSprite(sprite);
		action.setxMovement(xMovement);
		return action;
	}

	public static ChangeYByNAction changeYByN(Sprite sprite, Formula yMovement) {
		ChangeYByNAction action = action(ChangeYByNAction.class);
		action.setSprite(sprite);
		action.setyMovement(yMovement);
		return action;
	}

	public static ClearGraphicEffectAction clearGraphicEffect(Sprite sprite) {
		ClearGraphicEffectAction action = action(ClearGraphicEffectAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static ComeToFrontAction comeToFront(Sprite sprite) {
		ComeToFrontAction action = action(ComeToFrontAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static GlideToAction glideTo(Sprite sprite, Formula x, Formula y, Formula duration) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(duration);
		action.setSprite(sprite);
		return action;
	}

	public static GlideToAction glideTo(Sprite sprite, Formula x, Formula y, Formula duration,
			Interpolation interpolation) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		action.setSprite(sprite);
		return action;
	}

	public static GlideToAction placeAt(Sprite sprite, Formula x, Formula y) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(0);
		action.setInterpolation(null);
		action.setSprite(sprite);
		return action;
	}

	public static GoNStepsBackAction goNStepsBack(Sprite sprite, Formula steps) {
		GoNStepsBackAction action = action(GoNStepsBackAction.class);
		action.setSprite(sprite);
		action.setSteps(steps);
		return action;
	}

	public static HideAction hide(Sprite sprite) {
		HideAction action = action(HideAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static IfOnEdgeBounceAction ifOnEdgeBounce(Sprite sprite) {
		IfOnEdgeBounceAction action = action(IfOnEdgeBounceAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static LegoNxtMotorActionAction legoNxtMotorAction(Sprite sprite, String motor,
			org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick.Motor motorEnum, Formula speed) {
		LegoNxtMotorActionAction action = action(LegoNxtMotorActionAction.class);
		action.setMotorEnum(motorEnum);
		action.setSprite(sprite);
		action.setSpeed(speed);
		return action;
	}

	public static LegoNxtMotorStopAction legoNxtMotorStop(
			org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick.Motor motorEnum) {
		LegoNxtMotorStopAction action = action(LegoNxtMotorStopAction.class);
		action.setMotorEnum(motorEnum);
		return action;
	}

	public static LegoNxtMotorTurnAngleAction legoNxtMotorTurnAngle(Sprite sprite,
			org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick.Motor motorEnum, Formula degrees) {
		LegoNxtMotorTurnAngleAction action = action(LegoNxtMotorTurnAngleAction.class);
		action.setMotorEnum(motorEnum);
		action.setSprite(sprite);
		action.setDegrees(degrees);
		return action;
	}

	public static LegoNxtPlayToneAction legoNxtPlayTone(Sprite sprite, Formula hertz, Formula durationInSeconds) {
		LegoNxtPlayToneAction action = action(LegoNxtPlayToneAction.class);
		action.setHertz(hertz);
		action.setSprite(sprite);
		action.setDurationInSeconds(durationInSeconds);
		return action;
	}

	public static MoveNStepsAction moveNSteps(Sprite sprite, Formula steps) {
		MoveNStepsAction action = action(MoveNStepsAction.class);
		action.setSprite(sprite);
		action.setSteps(steps);
		return action;
	}

	public static NextLookAction nextLook(Sprite sprite) {
		NextLookAction action = action(NextLookAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static PlaySoundAction playSound(Sprite sprite, SoundInfo sound) {
		PlaySoundAction action = action(PlaySoundAction.class);
		action.setSprite(sprite);
		action.setSound(sound);
		return action;
	}

	public static PointInDirectionAction pointInDirection(Sprite sprite, Formula degrees) {
		PointInDirectionAction action = action(PointInDirectionAction.class);
		action.setSprite(sprite);
		action.setDegrees(degrees);
		return action;
	}

	public static PointToAction pointTo(Sprite sprite, Sprite pointedSprite) {
		PointToAction action = action(PointToAction.class);
		action.setSprite(sprite);
		action.setPointedSprite(pointedSprite);
		return action;
	}

	public static SetBrightnessAction setBrightness(Sprite sprite, Formula brightness) {
		SetBrightnessAction action = action(SetBrightnessAction.class);
		action.setSprite(sprite);
		action.setBrightness(brightness);
		return action;
	}

	public static SetGhostEffectAction setGhostEffect(Sprite sprite, Formula transparency) {
		SetGhostEffectAction action = action(SetGhostEffectAction.class);
		action.setSprite(sprite);
		action.setTransparency(transparency);
		return action;
	}

	public static SetLookAction setLook(Sprite sprite, LookData lookData) {
		SetLookAction action = action(SetLookAction.class);
		action.setSprite(sprite);
		action.setLookData(lookData);
		return action;
	}

	public static SetSizeToAction setSizeTo(Sprite sprite, Formula size) {
		SetSizeToAction action = action(SetSizeToAction.class);
		action.setSprite(sprite);
		action.setSize(size);
		return action;
	}

	public static SetVolumeToAction setVolumeTo(Sprite sprite, Formula volume) {
		SetVolumeToAction action = action(SetVolumeToAction.class);
		action.setVolume(volume);
		action.setSprite(sprite);
		return action;
	}

	public static SetXAction setX(Sprite sprite, Formula x) {
		SetXAction action = action(SetXAction.class);
		action.setSprite(sprite);
		action.setX(x);
		return action;
	}

	public static SetYAction setY(Sprite sprite, Formula y) {
		SetYAction action = action(SetYAction.class);
		action.setSprite(sprite);
		action.setY(y);
		return action;
	}

	public static ShowAction show(Sprite sprite) {
		ShowAction action = action(ShowAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static SpeakAction speak(String text, SpeakBrick speakBrick) {
		SpeakAction action = action(SpeakAction.class);
		action.setText(text);
		return action;
	}

	public static StopAllSoundsAction stopAllSounds() {
		return action(StopAllSoundsAction.class);
	}

	public static TurnLeftAction turnLeft(Sprite sprite, Formula degrees) {
		TurnLeftAction action = action(TurnLeftAction.class);
		action.setSprite(sprite);
		action.setDegrees(degrees);
		return action;
	}

	public static TurnRightAction turnRight(Sprite sprite, Formula degrees) {
		TurnRightAction action = action(TurnRightAction.class);
		action.setSprite(sprite);
		action.setDegrees(degrees);
		return action;
	}

	public static Action changeVariable(Sprite sprite, Formula variableFormula, UserVariable userVariable) {

		ChangeVariableAction action = action(ChangeVariableAction.class);
		action.setSprite(sprite);
		action.setChangeVariable(variableFormula);
		action.setUserVariable(userVariable);
		return action;
	}

	public static Action setVariable(Sprite sprite, Formula variableFormula, UserVariable userVariable) {

		SetVariableAction action = action(SetVariableAction.class);
		action.setSprite(sprite);
		action.setChangeVariable(variableFormula);
		action.setUserVariable(userVariable);
		return action;
	}

	static public IfLogicAction ifLogc(Sprite sprite, Formula condition, Action ifAction, Action elseAction) {
		IfLogicAction action = action(IfLogicAction.class);
		action.setIfAction(ifAction);
		action.setIfCondition(condition);
		action.setElseAction(elseAction);
		action.setSprite(sprite);
		return action;
	}

	static public RepeatAction repeat(Sprite sprite, Formula count, Action repeatedAction) {
		RepeatAction action = action(RepeatAction.class);
		action.setRepeatCount(count);
		action.setAction(repeatedAction);
		action.setSprite(sprite);
		return action;
	}

	static public WaitAction delay(Sprite sprite, Formula delay) {
		WaitAction action = action(WaitAction.class);
		action.setSprite(sprite);
		action.setDelay(delay);
		return action;
	}

	public static Action forever(Sprite sprite, SequenceAction foreverSequence) {
		RepeatAction action = action(RepeatAction.class);
		action.setIsForeverRepeat(true);
		action.setAction(foreverSequence);
		action.setSprite(sprite);
		return action;
	}
}
