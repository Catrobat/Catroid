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
package org.catrobat.catroid.content;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ParameterizedData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.actions.AddItemToUserListAction;
import org.catrobat.catroid.content.actions.AdditiveParticleEffectAction;
import org.catrobat.catroid.content.actions.ArduinoSendDigitalValueAction;
import org.catrobat.catroid.content.actions.ArduinoSendPWMValueAction;
import org.catrobat.catroid.content.actions.AskAction;
import org.catrobat.catroid.content.actions.AskSpeechAction;
import org.catrobat.catroid.content.actions.AssertEqualsAction;
import org.catrobat.catroid.content.actions.AssertUserListAction;
import org.catrobat.catroid.content.actions.BroadcastAction;
import org.catrobat.catroid.content.actions.CameraBrickAction;
import org.catrobat.catroid.content.actions.ChangeBrightnessByNAction;
import org.catrobat.catroid.content.actions.ChangeColorByNAction;
import org.catrobat.catroid.content.actions.ChangeSizeByNAction;
import org.catrobat.catroid.content.actions.ChangeTempoByAction;
import org.catrobat.catroid.content.actions.ChangeTransparencyByNAction;
import org.catrobat.catroid.content.actions.ChangeVariableAction;
import org.catrobat.catroid.content.actions.ChangeVolumeByNAction;
import org.catrobat.catroid.content.actions.ChangeXByNAction;
import org.catrobat.catroid.content.actions.ChangeYByNAction;
import org.catrobat.catroid.content.actions.ChooseCameraAction;
import org.catrobat.catroid.content.actions.ClearBackgroundAction;
import org.catrobat.catroid.content.actions.ClearGraphicEffectAction;
import org.catrobat.catroid.content.actions.ClearUserListAction;
import org.catrobat.catroid.content.actions.CloneAction;
import org.catrobat.catroid.content.actions.ComeToFrontAction;
import org.catrobat.catroid.content.actions.CopyLookAction;
import org.catrobat.catroid.content.actions.DeleteItemOfUserListAction;
import org.catrobat.catroid.content.actions.DeleteLookAction;
import org.catrobat.catroid.content.actions.DeleteThisCloneAction;
import org.catrobat.catroid.content.actions.EditLookAction;
import org.catrobat.catroid.content.actions.EventAction;
import org.catrobat.catroid.content.actions.FadeParticleEffectAction;
import org.catrobat.catroid.content.actions.FinishStageAction;
import org.catrobat.catroid.content.actions.FlashAction;
import org.catrobat.catroid.content.actions.ForItemInUserListAction;
import org.catrobat.catroid.content.actions.ForVariableFromToAction;
import org.catrobat.catroid.content.actions.GlideToPhysicsAction;
import org.catrobat.catroid.content.actions.GoNStepsBackAction;
import org.catrobat.catroid.content.actions.GoToOtherSpritePositionAction;
import org.catrobat.catroid.content.actions.GoToRandomPositionAction;
import org.catrobat.catroid.content.actions.GoToTouchPositionAction;
import org.catrobat.catroid.content.actions.HideTextAction;
import org.catrobat.catroid.content.actions.IfLogicAction;
import org.catrobat.catroid.content.actions.InsertItemIntoUserListAction;
import org.catrobat.catroid.content.actions.LegoEv3MotorMoveAction;
import org.catrobat.catroid.content.actions.LegoEv3MotorStopAction;
import org.catrobat.catroid.content.actions.LegoEv3MotorTurnAngleAction;
import org.catrobat.catroid.content.actions.LegoEv3PlayToneAction;
import org.catrobat.catroid.content.actions.LegoEv3SetLedAction;
import org.catrobat.catroid.content.actions.LegoNxtMotorMoveAction;
import org.catrobat.catroid.content.actions.LegoNxtMotorStopAction;
import org.catrobat.catroid.content.actions.LegoNxtMotorTurnAngleAction;
import org.catrobat.catroid.content.actions.LegoNxtPlayToneAction;
import org.catrobat.catroid.content.actions.LookRequestAction;
import org.catrobat.catroid.content.actions.MoveNStepsAction;
import org.catrobat.catroid.content.actions.OpenUrlAction;
import org.catrobat.catroid.content.actions.PaintNewLookAction;
import org.catrobat.catroid.content.actions.ParameterizedAssertAction;
import org.catrobat.catroid.content.actions.PauseForBeatsAction;
import org.catrobat.catroid.content.actions.PenDownAction;
import org.catrobat.catroid.content.actions.PenUpAction;
import org.catrobat.catroid.content.actions.PhiroMotorMoveBackwardAction;
import org.catrobat.catroid.content.actions.PhiroMotorMoveForwardAction;
import org.catrobat.catroid.content.actions.PhiroMotorStopAction;
import org.catrobat.catroid.content.actions.PhiroPlayToneAction;
import org.catrobat.catroid.content.actions.PhiroRGBLightAction;
import org.catrobat.catroid.content.actions.PhiroSensorAction;
import org.catrobat.catroid.content.actions.PlayDrumForBeatsAction;
import org.catrobat.catroid.content.actions.PlayNoteForBeatsAction;
import org.catrobat.catroid.content.actions.PlaySoundAction;
import org.catrobat.catroid.content.actions.PlaySoundAtAction;
import org.catrobat.catroid.content.actions.PointInDirectionAction;
import org.catrobat.catroid.content.actions.PointToAction;
import org.catrobat.catroid.content.actions.RaspiIfLogicAction;
import org.catrobat.catroid.content.actions.RaspiPwmAction;
import org.catrobat.catroid.content.actions.RaspiSendDigitalValueAction;
import org.catrobat.catroid.content.actions.ReadListFromDeviceAction;
import org.catrobat.catroid.content.actions.ReadVariableFromDeviceAction;
import org.catrobat.catroid.content.actions.ReadVariableFromFileAction;
import org.catrobat.catroid.content.actions.RepeatAction;
import org.catrobat.catroid.content.actions.RepeatParameterizedAction;
import org.catrobat.catroid.content.actions.RepeatUntilAction;
import org.catrobat.catroid.content.actions.ReplaceItemInUserListAction;
import org.catrobat.catroid.content.actions.ReportAction;
import org.catrobat.catroid.content.actions.ResetTimerAction;
import org.catrobat.catroid.content.actions.RunningStitchAction;
import org.catrobat.catroid.content.actions.SceneStartAction;
import org.catrobat.catroid.content.actions.SceneTransitionAction;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.actions.SetBrightnessAction;
import org.catrobat.catroid.content.actions.SetCameraFocusPointAction;
import org.catrobat.catroid.content.actions.SetColorAction;
import org.catrobat.catroid.content.actions.SetInstrumentAction;
import org.catrobat.catroid.content.actions.SetListeningLanguageAction;
import org.catrobat.catroid.content.actions.SetLookAction;
import org.catrobat.catroid.content.actions.SetLookByIndexAction;
import org.catrobat.catroid.content.actions.SetNextLookAction;
import org.catrobat.catroid.content.actions.SetNfcTagAction;
import org.catrobat.catroid.content.actions.SetParticleColorAction;
import org.catrobat.catroid.content.actions.SetPenColorAction;
import org.catrobat.catroid.content.actions.SetPenSizeAction;
import org.catrobat.catroid.content.actions.SetPreviousLookAction;
import org.catrobat.catroid.content.actions.SetRotationStyleAction;
import org.catrobat.catroid.content.actions.SetSizeToAction;
import org.catrobat.catroid.content.actions.SetTempoAction;
import org.catrobat.catroid.content.actions.SetTextAction;
import org.catrobat.catroid.content.actions.SetThreadColorAction;
import org.catrobat.catroid.content.actions.SetTransparencyAction;
import org.catrobat.catroid.content.actions.SetVariableAction;
import org.catrobat.catroid.content.actions.SetVisibleAction;
import org.catrobat.catroid.content.actions.SetVolumeToAction;
import org.catrobat.catroid.content.actions.SetXAction;
import org.catrobat.catroid.content.actions.SetYAction;
import org.catrobat.catroid.content.actions.SewUpAction;
import org.catrobat.catroid.content.actions.ShowTextAction;
import org.catrobat.catroid.content.actions.ShowTextColorSizeAlignmentAction;
import org.catrobat.catroid.content.actions.SpeakAction;
import org.catrobat.catroid.content.actions.SpeakAndWaitAction;
import org.catrobat.catroid.content.actions.StampAction;
import org.catrobat.catroid.content.actions.StartListeningAction;
import org.catrobat.catroid.content.actions.StitchAction;
import org.catrobat.catroid.content.actions.StopAllScriptsAction;
import org.catrobat.catroid.content.actions.StopAllSoundsAction;
import org.catrobat.catroid.content.actions.StopOtherScriptsAction;
import org.catrobat.catroid.content.actions.StopRunningStitchAction;
import org.catrobat.catroid.content.actions.StopSoundAction;
import org.catrobat.catroid.content.actions.StopThisScriptAction;
import org.catrobat.catroid.content.actions.StoreCSVIntoUserListAction;
import org.catrobat.catroid.content.actions.TapAtAction;
import org.catrobat.catroid.content.actions.ThinkSayBubbleAction;
import org.catrobat.catroid.content.actions.TripleStitchAction;
import org.catrobat.catroid.content.actions.TurnLeftAction;
import org.catrobat.catroid.content.actions.TurnRightAction;
import org.catrobat.catroid.content.actions.UserDefinedBrickAction;
import org.catrobat.catroid.content.actions.VibrateAction;
import org.catrobat.catroid.content.actions.WaitAction;
import org.catrobat.catroid.content.actions.WaitForBubbleBrickAction;
import org.catrobat.catroid.content.actions.WaitForSoundAction;
import org.catrobat.catroid.content.actions.WaitTillIdleAction;
import org.catrobat.catroid.content.actions.WaitUntilAction;
import org.catrobat.catroid.content.actions.WebRequestAction;
import org.catrobat.catroid.content.actions.WriteEmbroideryToFileAction;
import org.catrobat.catroid.content.actions.WriteUserDataOnDeviceAction;
import org.catrobat.catroid.content.actions.WriteVariableToFileAction;
import org.catrobat.catroid.content.actions.ZigZagStitchAction;
import org.catrobat.catroid.content.actions.conditional.GlideToAction;
import org.catrobat.catroid.content.actions.conditional.IfOnEdgeBounceAction;
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick;
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.content.bricks.brickspinner.PickableDrum;
import org.catrobat.catroid.content.bricks.brickspinner.PickableMusicalInstrument;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceListAccessor;
import org.catrobat.catroid.io.DeviceUserDataAccessor;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.stage.SpeechSynthesizer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.catrobat.catroid.utils.MobileServiceAvailability;
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider;

import java.io.File;
import java.util.List;
import java.util.UUID;

import kotlin.Pair;

import static org.koin.java.KoinJavaComponent.get;

public class ActionFactory extends Actions {

	public EventAction createUserBrickAction(Sprite sprite, SequenceAction sequence,
			List<UserDefinedBrickInput> userDefinedBrickInputs, UUID userDefinedBrickID) {
		UserDefinedBrickAction action = action(UserDefinedBrickAction.class);

		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setInputs(userDefinedBrickInputs);
		action.setUserDefinedBrickID(userDefinedBrickID);
		action.setSprite(sprite);
		action.setWait(true);
		return action;
	}

	public EventAction createBroadcastAction(String broadcastMessage, boolean wait) {
		BroadcastAction action = action(BroadcastAction.class);
		action.setBroadcastMessage(broadcastMessage);
		action.setWait(wait);
		return action;
	}

	public Action createWaitAction(Sprite sprite, SequenceAction sequence, Formula delay) {
		WaitAction action = action(WaitAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDelay(delay);
		return action;
	}

	public Action createWaitForSoundAction(Sprite sprite, SequenceAction sequence, Formula delay,
			String soundFilePath) {
		WaitForSoundAction action = action(WaitForSoundAction.class);
		action.setSoundFilePath(soundFilePath);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDelay(delay);
		return action;
	}

	public Action createPlaySoundAtAction(Sprite sprite, SequenceAction sequence, Formula delay,
			SoundInfo sound) {
		PlaySoundAtAction action = action(PlaySoundAtAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setSprite(sprite);
		action.setSound(sound);
		action.setScope(scope);
		action.setOffset(delay);
		return action;
	}

	public Action createWaitForBubbleBrickAction(Sprite sprite, SequenceAction sequence, Formula delay) {
		WaitForBubbleBrickAction action = Actions.action(WaitForBubbleBrickAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDelay(delay);
		return action;
	}

	public Action createChangeBrightnessByNAction(Sprite sprite, SequenceAction sequence,
			Formula changeBrightness) {
		ChangeBrightnessByNAction action = Actions.action(ChangeBrightnessByNAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setBrightness(changeBrightness);
		return action;
	}

	public Action createChangeColorByNAction(Sprite sprite, SequenceAction sequence,
			Formula changeColor) {
		ChangeColorByNAction action = Actions.action(ChangeColorByNAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setColor(changeColor);
		return action;
	}

	public Action createChangeTransparencyByNAction(Sprite sprite, SequenceAction sequence,
			Formula transparency) {
		ChangeTransparencyByNAction action = Actions.action(ChangeTransparencyByNAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setTransparency(transparency);
		return action;
	}

	public Action createChangeSizeByNAction(Sprite sprite, SequenceAction sequence, Formula size) {
		ChangeSizeByNAction action = Actions.action(ChangeSizeByNAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSize(size);
		return action;
	}

	public Action createChangeVolumeByNAction(Sprite sprite, SequenceAction sequence, Formula volume) {
		ChangeVolumeByNAction action = Actions.action(ChangeVolumeByNAction.class);
		action.setVolume(volume);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createChangeXByNAction(Sprite sprite, SequenceAction sequence, Formula xMovement) {
		ChangeXByNAction action = Actions.action(ChangeXByNAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setxMovement(xMovement);
		return action;
	}

	public Action createChangeYByNAction(Sprite sprite, SequenceAction sequence, Formula yMovement) {
		ChangeYByNAction action = Actions.action(ChangeYByNAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setyMovement(yMovement);
		return action;
	}

	public Action createSetRotationStyleAction(Sprite sprite, @Look.RotationStyle int mode) {
		SetRotationStyleAction action = Actions.action(SetRotationStyleAction.class);
		action.setRotationStyle(mode);
		action.setSprite(sprite);
		return action;
	}

	public Action createClearGraphicEffectAction(Sprite sprite) {
		ClearGraphicEffectAction action = Actions.action(ClearGraphicEffectAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createComeToFrontAction(Sprite sprite) {
		ComeToFrontAction action = Actions.action(ComeToFrontAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createGlideToAction(Sprite sprite, SequenceAction sequence, Formula x, Formula y,
			Formula duration) {
		GlideToAction action = Actions.action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(duration);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createPlaceAtAction(Sprite sprite, SequenceAction sequence, Formula x, Formula y) {
		GlideToAction action = Actions.action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(0);
		action.setInterpolation(null);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createGoToAction(Sprite sprite, Sprite destinationSprite, int spinnerSelection) {
		switch (spinnerSelection) {
			case BrickValues.GO_TO_TOUCH_POSITION:
				GoToTouchPositionAction touchPositionAction = Actions.action(GoToTouchPositionAction.class);
				touchPositionAction.setSprite(sprite);
				return touchPositionAction;
			case BrickValues.GO_TO_RANDOM_POSITION:
				GoToRandomPositionAction randomPositionAction = Actions.action(GoToRandomPositionAction.class);
				randomPositionAction.setSprite(sprite);
				return randomPositionAction;
			case BrickValues.GO_TO_OTHER_SPRITE_POSITION:
				GoToOtherSpritePositionAction otherSpritePositionAction = Actions
						.action(GoToOtherSpritePositionAction.class);
				otherSpritePositionAction.setSprite(sprite);
				otherSpritePositionAction.setDestinationSprite(destinationSprite);
				return otherSpritePositionAction;
			default:
				return null;
		}
	}

	public Action createGoNStepsBackAction(Sprite sprite, SequenceAction sequence, Formula steps) {
		GoNStepsBackAction action = Actions.action(GoNStepsBackAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSteps(steps);
		return action;
	}

	public Action createHideAction(Sprite sprite) {
		SetVisibleAction action = Actions.action(SetVisibleAction.class);
		action.setSprite(sprite);
		action.setVisible(false);
		return action;
	}

	public Action createIfOnEdgeBounceAction(Sprite sprite) {
		IfOnEdgeBounceAction action = Actions.action(IfOnEdgeBounceAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createLegoNxtMotorMoveAction(Sprite sprite, SequenceAction sequence,
			LegoNxtMotorMoveBrick.Motor motorEnum, Formula speed) {
		LegoNxtMotorMoveAction action = Actions.action(LegoNxtMotorMoveAction.class);
		action.setMotorEnum(motorEnum);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSpeed(speed);
		return action;
	}

	public Action createLegoNxtMotorStopAction(LegoNxtMotorStopBrick.Motor motorEnum) {
		LegoNxtMotorStopAction action = Actions.action(LegoNxtMotorStopAction.class);
		action.setMotorEnum(motorEnum);
		return action;
	}

	public Action createLegoNxtMotorTurnAngleAction(Sprite sprite, SequenceAction sequence,
			LegoNxtMotorTurnAngleBrick.Motor motorEnum, Formula degrees) {
		LegoNxtMotorTurnAngleAction action = Actions.action(LegoNxtMotorTurnAngleAction.class);
		action.setMotorEnum(motorEnum);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDegrees(degrees);
		return action;
	}

	public Action createLegoNxtPlayToneAction(Sprite sprite, SequenceAction sequence, Formula hertz,
			Formula durationInSeconds) {
		LegoNxtPlayToneAction action = Actions.action(LegoNxtPlayToneAction.class);
		action.setHertz(hertz);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDurationInSeconds(durationInSeconds);
		return action;
	}

	public Action createLegoEv3SingleMotorMoveAction(Sprite sprite, SequenceAction sequence,
			LegoEv3MotorMoveBrick.Motor motorEnum, Formula speed) {
		LegoEv3MotorMoveAction action = action(LegoEv3MotorMoveAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setMotorEnum(motorEnum);
		action.setSpeed(speed);
		return action;
	}

	public Action createLegoEv3MotorStopAction(LegoEv3MotorStopBrick.Motor motorEnum) {
		LegoEv3MotorStopAction action = action(LegoEv3MotorStopAction.class);
		action.setMotorEnum(motorEnum);
		return action;
	}

	public Action createLegoEv3SetLedAction(LegoEv3SetLedBrick.LedStatus ledStatusEnum) {
		LegoEv3SetLedAction action = action(LegoEv3SetLedAction.class);
		action.setLedStatusEnum(ledStatusEnum);
		return action;
	}

	public Action createLegoEv3PlayToneAction(Sprite sprite, SequenceAction sequence,
			Formula hertz, Formula durationInSeconds, Formula volumeInPercent) {
		LegoEv3PlayToneAction action = action(LegoEv3PlayToneAction.class);
		action.setHertz(hertz);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDurationInSeconds(durationInSeconds);
		action.setVolumeInPercent(volumeInPercent);
		return action;
	}

	public Action createLegoEv3MotorTurnAngleAction(Sprite sprite, SequenceAction sequence,
			LegoEv3MotorTurnAngleBrick.Motor motorEnum, Formula degrees) {
		LegoEv3MotorTurnAngleAction action = action(LegoEv3MotorTurnAngleAction.class);
		action.setMotorEnum(motorEnum);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDegrees(degrees);
		return action;
	}

	public Action createPhiroPlayToneActionAction(Sprite sprite, SequenceAction sequence,
			PhiroPlayToneBrick.Tone toneEnum, Formula duration) {
		PhiroPlayToneAction action = action(PhiroPlayToneAction.class);
		action.setSelectedTone(toneEnum);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDurationInSeconds(duration);
		return action;
	}

	public Action createPhiroMotorMoveForwardActionAction(Sprite sprite, SequenceAction sequence,
			PhiroMotorMoveForwardBrick.Motor motorEnum, Formula speed) {
		PhiroMotorMoveForwardAction action = action(PhiroMotorMoveForwardAction.class);
		action.setMotorEnum(motorEnum);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSpeed(speed);
		return action;
	}

	public Action createPhiroMotorMoveBackwardActionAction(Sprite sprite, SequenceAction sequence,
			PhiroMotorMoveBackwardBrick.Motor motorEnum, Formula speed) {
		PhiroMotorMoveBackwardAction action = action(PhiroMotorMoveBackwardAction.class);
		action.setMotorEnum(motorEnum);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSpeed(speed);
		return action;
	}

	public Action createPhiroRgbLedEyeActionAction(Sprite sprite, SequenceAction sequence,
			PhiroRGBLightBrick.Eye eye, Formula red, Formula green, Formula blue) {
		PhiroRGBLightAction action = action(PhiroRGBLightAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setEyeEnum(eye);
		action.setRed(red);
		action.setGreen(green);
		action.setBlue(blue);
		return action;
	}

	public Action createPhiroSendSelectedSensorAction(Sprite sprite, SequenceAction sequence,
			int sensorNumber, Action ifAction, Action elseAction) {
		PhiroSensorAction action = action(PhiroSensorAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSensor(sensorNumber);
		action.setIfAction(ifAction);
		action.setElseAction(elseAction);
		return action;
	}

	public Action createPhiroMotorStopActionAction(PhiroMotorStopBrick.Motor motorEnum) {
		PhiroMotorStopAction action = action(PhiroMotorStopAction.class);
		action.setMotorEnum(motorEnum);
		return action;
	}

	public Action createMoveNStepsAction(Sprite sprite, SequenceAction sequence, Formula steps) {
		MoveNStepsAction action = Actions.action(MoveNStepsAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSteps(steps);
		return action;
	}

	public Action createPenDownAction(Sprite sprite) {
		PenDownAction action = Actions.action(PenDownAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createPenUpAction(Sprite sprite) {
		PenUpAction action = Actions.action(PenUpAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createSetPenSizeAction(Sprite sprite, SequenceAction sequence, Formula penSize) {
		SetPenSizeAction action = Actions.action(SetPenSizeAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPenSize(penSize);
		return action;
	}

	public Action createSetPenColorAction(Sprite sprite, SequenceAction sequence, Formula red,
			Formula green, Formula blue) {
		SetPenColorAction action = Actions.action(SetPenColorAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setRed(red);
		action.setGreen(green);
		action.setBlue(blue);
		return action;
	}

	public Action createClearBackgroundAction() {
		return Actions.action(ClearBackgroundAction.class);
	}

	public Action createSetCameraFocusPointAction(Sprite sprite, SequenceAction sequence,
			Formula horizontal, Formula vertical) {
		SetCameraFocusPointAction action = action(SetCameraFocusPointAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSprite(sprite);
		action.setHorizontal(horizontal);
		action.setVertical(vertical);
		return action;
	}

	public Action createStampAction(Sprite sprite) {
		StampAction action = Actions.action(StampAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createPlaySoundAction(Sprite sprite, SoundInfo sound) {
		PlaySoundAction action = Actions.action(PlaySoundAction.class);
		action.setSprite(sprite);
		action.setSound(sound);
		return action;
	}

	public Action createStopSoundAction(Sprite sprite, SoundInfo sound) {
		StopSoundAction action = Actions.action(StopSoundAction.class);
		action.setSprite(sprite);
		action.setSound(sound);
		return action;
	}

	public Action createPointInDirectionAction(Sprite sprite, SequenceAction sequence, Formula degrees) {
		PointInDirectionAction action = Actions.action(PointInDirectionAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDegreesInUserInterfaceDimensionUnit(degrees);
		return action;
	}

	public Action createPointToAction(Sprite sprite, Sprite pointedSprite) {
		PointToAction action = Actions.action(PointToAction.class);
		action.setSprite(sprite);
		action.setPointedSprite(pointedSprite);
		return action;
	}

	public Action createCloneAction(Sprite sprite) {
		CloneAction action = Actions.action(CloneAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createDeleteThisCloneAction(Sprite sprite) {
		DeleteThisCloneAction action = Actions.action(DeleteThisCloneAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createSetBrightnessAction(Sprite sprite, SequenceAction sequence, Formula brightness) {
		SetBrightnessAction action = Actions.action(SetBrightnessAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setBrightness(brightness);
		return action;
	}

	public Action createSetColorAction(Sprite sprite, SequenceAction sequence, Formula color) {
		SetColorAction action = Actions.action(SetColorAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setColor(color);
		return action;
	}

	public Action createSetTransparencyAction(Sprite sprite, SequenceAction sequence,
			Formula transparency) {
		SetTransparencyAction action = Actions.action(SetTransparencyAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setTransparency(transparency);
		return action;
	}

	public Action createSetLookAction(Sprite sprite, LookData lookData) {
		return createSetLookAction(sprite, lookData, false);
	}

	public Action createSetLookAction(Sprite sprite, LookData lookData, boolean wait) {
		SetLookAction action = Actions.action(SetLookAction.class);
		action.setSprite(sprite);
		action.setLookData(lookData);
		action.setWait(wait);
		return action;
	}

	public Action createSetLookByIndexAction(Sprite sprite, SequenceAction sequence, Formula formula) {
		SetLookByIndexAction action = Actions.action(SetLookByIndexAction.class);
		action.setSprite(sprite);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(formula);
		return action;
	}

	public Action createSetBackgroundAction(LookData lookData, boolean wait) {
		SetLookAction action = Actions.action(SetLookAction.class);
		action.setSprite(ProjectManager.getInstance().getCurrentlyPlayingScene().getBackgroundSprite());
		action.setLookData(lookData);
		action.setWait(wait);
		return action;
	}

	public Action createSetBackgroundByIndexAction(Sprite sprite, SequenceAction sequence,
			Formula formula, boolean wait) {
		SetLookByIndexAction action = Actions.action(SetLookByIndexAction.class);
		action.setSprite(ProjectManager.getInstance().getCurrentlyPlayingScene().getBackgroundSprite());
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(formula);
		action.setWait(wait);
		return action;
	}

	public Action createSetNextLookAction(Sprite sprite, SequenceAction sequence) {
		SetNextLookAction action = Actions.action(SetNextLookAction.class);
		action.setSprite(sprite);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createSetPreviousLookAction(Sprite sprite, SequenceAction sequence) {
		SetPreviousLookAction action = action(SetPreviousLookAction.class);
		action.setSprite(sprite);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createDeleteLookAction(Sprite sprite) {
		DeleteLookAction action = Actions.action(DeleteLookAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createSetSizeToAction(Sprite sprite, SequenceAction sequence, Formula size) {
		SetSizeToAction action = Actions.action(SetSizeToAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSize(size);
		return action;
	}

	public Action createGlideToPhysicsAction(Sprite sprite, PhysicsLook physicsLook,
			SequenceAction sequence, Formula x,
			Formula y, float duration, float delta) {

		GlideToPhysicsAction action = Actions.action(GlideToPhysicsAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPhysicsLook(physicsLook);
		action.setPosition(x, y);
		action.setDuration(duration);
		action.act(delta);
		return action;
	}

	public Action createSetVolumeToAction(Sprite sprite, SequenceAction sequence, Formula volume) {
		SetVolumeToAction action = Actions.action(SetVolumeToAction.class);
		action.setVolume(volume);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createSetXAction(Sprite sprite, SequenceAction sequence, Formula x) {
		SetXAction action = Actions.action(SetXAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setX(x);
		return action;
	}

	public Action createSetYAction(Sprite sprite, SequenceAction sequence, Formula y) {
		SetYAction action = Actions.action(SetYAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setY(y);
		return action;
	}

	public Action createShowAction(Sprite sprite) {
		SetVisibleAction action = Actions.action(SetVisibleAction.class);
		action.setSprite(sprite);
		action.setVisible(true);
		return action;
	}

	public Action createSpeakAction(Sprite sprite, SequenceAction sequence, Formula text) {
		SpeakAction action = action(SpeakAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setSpeechSynthesizer(new SpeechSynthesizer(scope, text));
		action.setMobileServiceAvailability(get(MobileServiceAvailability.class));
		action.setContext(StageActivity.activeStageActivity.get());

		return action;
	}

	public Action createSpeakAndWaitAction(Sprite sprite, SequenceAction sequence, Formula text) {
		SpeakAndWaitAction action = action(SpeakAndWaitAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setSpeechSynthesizer(new SpeechSynthesizer(scope, text));
		action.setMobileServiceAvailability(get(MobileServiceAvailability.class));
		action.setContext(StageActivity.activeStageActivity.get());
		return action;
	}

	public Action createStopAllSoundsAction() {
		return Actions.action(StopAllSoundsAction.class);
	}

	public Action createPauseForBeatsAction(Sprite sprite, SequenceAction sequence, Formula beats) {
		PauseForBeatsAction action = action(PauseForBeatsAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setBeats(beats);
		return action;
	}

	public Action createPlayNoteForBeatsAction(Sprite sprite, SequenceAction sequence, Formula note,
			Formula beats) {
		PlayNoteForBeatsAction action = action(PlayNoteForBeatsAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setMidiValue(note);
		action.setBeats(beats);
		return action;
	}

	public Action createSetInstrumentAction(PickableMusicalInstrument instrument) {
		SetInstrumentAction action = action(SetInstrumentAction.class);
		action.setInstrument(instrument);
		return action;
	}

	public Action createSetTempoAction(Sprite sprite, SequenceAction sequence, Formula tempo) {
		SetTempoAction action = action(SetTempoAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setTempo(tempo);
		return action;
	}

	public Action createChangeTempoAction(Sprite sprite, SequenceAction sequence, Formula tempo) {
		ChangeTempoByAction action = action(ChangeTempoByAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setTempo(tempo);
		return action;
	}

	public Action createPlayDrumForBeatsAction(Sprite sprite, SequenceAction sequence, Formula beats,
			PickableDrum drum) {
		PlayDrumForBeatsAction action = action(PlayDrumForBeatsAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setBeats(beats);
		action.setDrum(drum);
		return action;
	}

	public Action createTurnLeftAction(Sprite sprite, SequenceAction sequence, Formula degrees) {
		TurnLeftAction action = Actions.action(TurnLeftAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDegrees(degrees);
		return action;
	}

	public Action createTurnRightAction(Sprite sprite, SequenceAction sequence, Formula degrees) {
		TurnRightAction action = Actions.action(TurnRightAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDegrees(degrees);
		return action;
	}

	public Action createChangeVariableAction(Sprite sprite, SequenceAction sequence, Formula variableFormula, UserVariable userVariable) {
		ChangeVariableAction action = Actions.action(ChangeVariableAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setChangeVariable(variableFormula);
		action.setUserVariable(userVariable);
		return action;
	}

	public Action createSetVariableAction(Sprite sprite, SequenceAction sequence, Formula variableFormula,
			UserVariable userVariable) {
		SetVariableAction action = Actions.action(SetVariableAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setChangeVariable(variableFormula);
		action.setUserVariable(userVariable);
		return action;
	}

	public Action createAskAction(Sprite sprite, SequenceAction sequence, Formula questionFormula,
			UserVariable answerVariable) {
		AskAction action = Actions.action(AskAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setQuestionFormula(questionFormula);
		action.setAnswerVariable(answerVariable);
		return action;
	}

	public Action createAskSpeechAction(Sprite sprite, SequenceAction sequence, Formula questionFormula,
			UserVariable answerVariable) {
		AskSpeechAction action = Actions.action(AskSpeechAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setQuestionFormula(questionFormula);
		action.setAnswerVariable(answerVariable);
		return action;
	}

	public Action createDeleteItemOfUserListAction(Sprite sprite, SequenceAction sequence,
			Formula userListFormula, UserList userList) {
		DeleteItemOfUserListAction action = action(DeleteItemOfUserListAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormulaIndexToDelete(userListFormula);
		action.setUserList(userList);
		return action;
	}

	public Action createClearUserListAction(UserList userList) {
		ClearUserListAction action = action(ClearUserListAction.class);
		action.setUserList(userList);
		return action;
	}

	public Action createAddItemToUserListAction(Sprite sprite, SequenceAction sequence,
			Formula userListFormula, UserList userList) {
		AddItemToUserListAction action = action(AddItemToUserListAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormulaItemToAdd(userListFormula);
		action.setUserList(userList);
		return action;
	}

	public Action createInsertItemIntoUserListAction(Sprite sprite, SequenceAction sequence,
			Formula userListFormulaIndexToInsert,
			Formula userListFormulaItemToInsert, UserList userList) {
		InsertItemIntoUserListAction action = action(InsertItemIntoUserListAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormulaIndexToInsert(userListFormulaIndexToInsert);
		action.setFormulaItemToInsert(userListFormulaItemToInsert);
		action.setUserList(userList);
		return action;
	}

	public Action createStoreCSVIntoUserListAction(Sprite sprite, SequenceAction sequence,
			Formula userListFormulaColumn, Formula userListFormulaCSV, UserList userList) {
		StoreCSVIntoUserListAction action = action(StoreCSVIntoUserListAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormulaColumnToExtract(userListFormulaColumn);
		action.setFormulaCSVData(userListFormulaCSV);
		action.setUserList(userList);
		return action;
	}

	public Action createReplaceItemInUserListAction(Sprite sprite, SequenceAction sequence,
			Formula userListFormulaIndexToReplace,
			Formula userListFormulaItemToInsert, UserList userList) {
		ReplaceItemInUserListAction action = action(ReplaceItemInUserListAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormulaIndexToReplace(userListFormulaIndexToReplace);
		action.setFormulaItemToInsert(userListFormulaItemToInsert);
		action.setUserList(userList);
		return action;
	}

	public Action createResetTimerAction() {
		return Actions.action(ResetTimerAction.class);
	}

	public Action createThinkSayBubbleAction(Sprite sprite, SequenceAction sequence,
			AndroidStringProvider androidStringProvider, Formula text, int type) {
		ThinkSayBubbleAction action = action(ThinkSayBubbleAction.class);
		action.setText(text);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setAndroidStringProvider(androidStringProvider);
		action.setType(type);
		return action;
	}

	public Action createThinkSayForBubbleAction(Sprite sprite, SequenceAction sequence,
			AndroidStringProvider androidStringProvider, Formula text, int type) {
		ThinkSayBubbleAction action = action(ThinkSayBubbleAction.class);
		action.setText(text);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setAndroidStringProvider(androidStringProvider);
		action.setType(type);
		return action;
	}

	public Action createSceneTransitionAction(String sceneName, Sprite sprite) {
		SceneTransitionAction action = action(SceneTransitionAction.class);
		action.setScene(sceneName);
		action.setSprite(sprite);
		return action;
	}

	public Action createSceneStartAction(String sceneName, Sprite sprite) {
		SceneStartAction action = action(SceneStartAction.class);
		action.setScene(sceneName);
		action.setSprite(sprite);
		return action;
	}

	public Action createIfLogicAction(Sprite sprite, SequenceAction sequence, Formula condition,
			Action ifAction, Action elseAction) {
		IfLogicAction action = Actions.action(IfLogicAction.class);
		action.setIfAction(ifAction);
		action.setIfCondition(condition);
		action.setElseAction(elseAction);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createRepeatAction(Sprite sprite, SequenceAction sequence, Formula count, Action repeatedAction,
			boolean isLoopDelay) {
		RepeatAction action = Actions.action(RepeatAction.class);
		action.setRepeatCount(count);
		action.setAction(repeatedAction);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setLoopDelay(isLoopDelay);
		return action;
	}

	public Action createForVariableFromToAction(Sprite sprite,
			SequenceAction sequence, UserVariable controlVariable,
			Formula from, Formula to, Action repeatedAction, boolean isLoopDelay) {
		ForVariableFromToAction action = Actions.action(ForVariableFromToAction.class);
		action.setRange(from, to);
		action.setControlVariable(controlVariable);
		action.setAction(repeatedAction);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setLoopDelay(isLoopDelay);
		return action;
	}

	public Action createForItemInUserListAction(UserList userList,
			UserVariable userVariable, Action repeatedAction, boolean isLoopDelay) {
		ForItemInUserListAction action = Actions.action(ForItemInUserListAction.class);
		action.setAction(repeatedAction);
		action.setUserList(userList);
		action.setCurrentItemVariable(userVariable);
		action.setLoopDelay(isLoopDelay);
		return action;
	}

	public Action createWaitUntilAction(Sprite sprite, SequenceAction sequence, Formula condition) {
		WaitUntilAction action = Actions.action(WaitUntilAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setCondition(condition);
		return action;
	}

	public Action createRepeatUntilAction(Sprite sprite, SequenceAction sequence, Formula condition, Action repeatedAction,
			boolean isLoopDelay) {
		RepeatUntilAction action = action(RepeatUntilAction.class);
		action.setRepeatCondition(condition);
		action.setAction(repeatedAction);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setLoopDelay(isLoopDelay);
		return action;
	}

	public Action createDelayAction(Sprite sprite, SequenceAction sequence, Formula delay) {
		WaitAction action = Actions.action(WaitAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDelay(delay);
		return action;
	}

	public Action createForeverAction(Sprite sprite, SequenceAction sequence, ScriptSequenceAction foreverSequence,
			boolean isLoopDelay) {
		RepeatAction action = Actions.action(RepeatAction.class);
		action.setForeverRepeat(true);
		action.setAction(foreverSequence);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setLoopDelay(isLoopDelay);
		return action;
	}

	public static Action createStitchAction(Sprite sprite) {
		StitchAction action = Actions.action(StitchAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createRunningStitchAction(Sprite sprite, SequenceAction sequence, Formula length) {
		RunningStitchAction action = Actions.action(RunningStitchAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setLength(length);
		return action;
	}

	public Action createTripleStitchAction(Sprite sprite, SequenceAction sequence, Formula steps) {
		TripleStitchAction action = Actions.action(TripleStitchAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setSteps(steps);
		return action;
	}

	public Action createZigZagStitchAction(Sprite sprite, SequenceAction sequence, Formula length,
			Formula width) {
		ZigZagStitchAction action = Actions.action(ZigZagStitchAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setLength(length);
		action.setWidth(width);
		return action;
	}

	public static Action createStopRunningStitchAction(Sprite sprite) {
		StopRunningStitchAction action = Actions.action(StopRunningStitchAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createWriteEmbroideryToFileAction(Sprite sprite, SequenceAction sequence,
			Formula fileName) {
		WriteEmbroideryToFileAction action = Actions.action(WriteEmbroideryToFileAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(fileName);

		return action;
	}

	public Action createSewUpAction(Sprite sprite) {
		SewUpAction action = Actions.action(SewUpAction.class);
		action.setSprite(sprite);
		return action;
	}

	public Action createSetThreadColorAction(Sprite sprite, SequenceAction sequence, Formula color) {
		SetThreadColorAction action = Actions.action(SetThreadColorAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setSprite(sprite);
		action.setScope(scope);
		action.setColor(color);
		return action;
	}

	public static Action createScriptSequenceAction(Script script) {
		return new ScriptSequenceAction(script);
	}

	public Action createSetBounceFactorAction(Sprite sprite, SequenceAction sequence,
			Formula bounceFactor) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createTurnRightSpeedAction(Sprite sprite, SequenceAction sequence,
			Formula degreesPerSecond) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createTurnLeftSpeedAction(Sprite sprite, SequenceAction sequence,
			Formula degreesPerSecond) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetVelocityAction(Sprite sprite, SequenceAction sequence, Formula velocityX,
			Formula velocityY) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetPhysicsObjectTypeAction(Sprite sprite, PhysicsObject.Type type) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetMassAction(Sprite sprite, SequenceAction sequence, Formula mass) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetGravityAction(Sprite sprite, SequenceAction sequence, Formula gravityX,
			Formula gravityY) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetFrictionAction(Sprite sprite, SequenceAction sequence,
			Formula friction) {
		throw new RuntimeException("No physics action available in non-physics sprite!");
	}

	public Action createSetTextAction(Sprite sprite, SequenceAction sequence, Formula x, Formula y, Formula text) {
		SetTextAction action = action(SetTextAction.class);

		action.setPosition(x, y);
		action.setText(text);
		action.setDuration(5);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createShowVariableAction(Sprite sprite, SequenceAction sequence, Formula xPosition,
			Formula yPosition, UserVariable userVariable, AndroidStringProvider androidStringProvider) {
		ShowTextAction action = action(ShowTextAction.class);
		action.setPosition(xPosition, yPosition);
		action.setVariableToShow(userVariable);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setAndroidStringProvider(androidStringProvider);
		return action;
	}

	public Action createShowVariableColorAndSizeAction(Sprite sprite, SequenceAction sequence,
			Formula xPosition, Formula yPosition, Formula relativeTextSize, Formula color,
			UserVariable userVariable, int alignment, AndroidStringProvider androidStringProvider) {
		ShowTextColorSizeAlignmentAction action = action(ShowTextColorSizeAlignmentAction.class);
		action.setPosition(xPosition, yPosition);
		action.setRelativeTextSize(relativeTextSize);
		action.setColor(color);
		action.setVariableToShow(userVariable);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setAlignment(alignment);
		action.setAndroidStringProvider(androidStringProvider);
		return action;
	}

	public Action createHideVariableAction(Sprite sprite, UserVariable userVariable,
			AndroidStringProvider androidStringProvider) {
		HideTextAction action = action(HideTextAction.class);
		action.setVariableToHide(userVariable);
		action.setSprite(sprite);
		action.setAndroidStringProvider(androidStringProvider);
		return action;
	}

	public Action createFlashAction(boolean flashOn) {
		FlashAction action = action(FlashAction.class);
		action.setFlashOn(flashOn);
		return action;
	}

	public Action createVibrateAction(Sprite sprite, SequenceAction sequence, Formula duration) {
		VibrateAction action = action(VibrateAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDuration(duration);
		return action;
	}

	public Action createUpdateCameraPreviewAction(boolean turnOn) {
		CameraBrickAction action = action(CameraBrickAction.class);
		action.setActive(turnOn);
		return action;
	}

	public Action createFadeParticleEffectsAction(Sprite sprite, boolean turnOn) {
		FadeParticleEffectAction action = action(FadeParticleEffectAction.class);
		action.setFadeIn(turnOn);
		action.setSprite(sprite);
		action.setBackgroundSprite(ProjectManager.getInstance().getCurrentlyPlayingScene().getBackgroundSprite());
		return action;
	}

	public Action createAdditiveParticleEffectsAction(Sprite sprite, boolean turnOn) {
		AdditiveParticleEffectAction action = action(AdditiveParticleEffectAction.class);
		action.setFadeIn(turnOn);
		action.setSprite(sprite);
		return action;
	}

	public Action createSetParticleColorAction(Sprite sprite, Formula color, SequenceAction sequence) {
		SetParticleColorAction action = action(SetParticleColorAction.class);
		action.setColor(color);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		return action;
	}

	public Action createSetFrontCameraAction() {
		ChooseCameraAction action = action(ChooseCameraAction.class);
		action.setFrontCamera();
		return action;
	}

	public Action createSetBackCameraAction() {
		ChooseCameraAction action = action(ChooseCameraAction.class);
		action.setBackCamera();
		return action;
	}

	public Action createSendDigitalArduinoValueAction(Sprite sprite, SequenceAction sequence,
			Formula pinNumber, Formula pinValue) {
		ArduinoSendDigitalValueAction action = action(ArduinoSendDigitalValueAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPinNumber(pinNumber);
		action.setPinValue(pinValue);
		return action;
	}

	public Action createSendPWMArduinoValueAction(Sprite sprite, SequenceAction sequence,
			Formula pinNumber, Formula pinValue) {
		ArduinoSendPWMValueAction action = action(ArduinoSendPWMValueAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPinNumber(pinNumber);
		action.setPinValue(pinValue);
		return action;
	}

	public Action createSendDigitalRaspiValueAction(Sprite sprite, SequenceAction sequence,
			Formula pinNumber, Formula pinValue) {
		RaspiSendDigitalValueAction action = action(RaspiSendDigitalValueAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPinNumber(pinNumber);
		action.setPinValue(pinValue);
		return action;
	}

	public Action createSendRaspiPwmValueAction(Sprite sprite, SequenceAction sequence,
			Formula pinNumber, Formula pwmFrequency, Formula pwmPercentage) {
		RaspiPwmAction action = action(RaspiPwmAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPinNumberFormula(pinNumber);
		action.setPwmFrequencyFormula(pwmFrequency);
		action.setPwmPercentageFormula(pwmPercentage);
		return action;
	}

	public Action createRaspiIfLogicActionAction(Sprite sprite, SequenceAction sequence,
			Formula pinNumber, Action ifAction, Action elseAction) {
		RaspiIfLogicAction action = action(RaspiIfLogicAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPinNumber(pinNumber);
		action.setIfAction(ifAction);
		action.setElseAction(elseAction);
		return action;
	}

	public Action createStopScriptAction(int spinnerSelection, Script currentScript, Sprite sprite) {
		switch (spinnerSelection) {
			case BrickValues.STOP_THIS_SCRIPT:
				StopThisScriptAction stopThisScriptAction = Actions.action(StopThisScriptAction.class);
				stopThisScriptAction.setCurrentScript(currentScript);
				return stopThisScriptAction;
			case BrickValues.STOP_OTHER_SCRIPTS:
				StopOtherScriptsAction stopOtherScriptsAction = Actions.action(StopOtherScriptsAction.class);
				stopOtherScriptsAction.setCurrentScript(currentScript);
				stopOtherScriptsAction.setSprite(sprite);
				return stopOtherScriptsAction;
			default:
				return Actions.action(StopAllScriptsAction.class);
		}
	}

	public Action createReportAction(Sprite sprite, SequenceAction sequence, Script currentScript, Formula reportFormula) {
		if (currentScript instanceof UserDefinedScript) {
			ReportAction reportAction = Actions.action(ReportAction.class);
			Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
			reportAction.setScope(scope);
			reportAction.setCurrentScript(currentScript);
			reportAction.setReportFormula(reportFormula);
			return reportAction;
		} else {
			StopThisScriptAction stopThisScriptAction = Actions.action(StopThisScriptAction.class);
			stopThisScriptAction.setCurrentScript(currentScript);
			return stopThisScriptAction;
		}
	}

	public Action createSetNfcTagAction(Sprite sprite, SequenceAction sequence, Formula nfcNdefMessage, int nfcNdefSpinnerSelection) {
		SetNfcTagAction setNfcTagAction = Actions.action(SetNfcTagAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		setNfcTagAction.setScope(scope);
		setNfcTagAction.setNfcTagNdefSpinnerSelection(nfcNdefSpinnerSelection);
		setNfcTagAction.setNfcNdefMessage(nfcNdefMessage);
		return setNfcTagAction;
	}

	public Action createAssertEqualsAction(Sprite sprite, SequenceAction sequence, Formula actual,
			Formula expected,
			String position) {
		AssertEqualsAction action = action(AssertEqualsAction.class);
		action.setActualFormula(actual);
		action.setExpectedFormula(expected);

		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPosition(position);

		return action;
	}

	public Action createAssertUserListsAction(Sprite sprite, SequenceAction sequence, UserList actual, UserList expected,
			String position) {
		AssertUserListAction action = action(AssertUserListAction.class);
		action.setActualUserList(actual);
		action.setExpectedUserList(expected);

		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPosition(position);

		return action;
	}

	public Action createRepeatParameterizedAction(Sprite sprite, ParameterizedData data,
			List<? extends Pair<UserList, UserVariable>> parameters,
			String position, Action repeatedAction, boolean isLoopDelay) {
		RepeatParameterizedAction action = action(RepeatParameterizedAction.class);
		action.setParameterizedData(data);
		action.setParameters(parameters);
		action.setAction(repeatedAction);
		action.setLoopDelay(isLoopDelay);

		action.setSprite(sprite);
		action.setPosition(position);

		return action;
	}

	public Action createParameterizedAssertAction(Sprite sprite, SequenceAction sequence, Formula actual, UserList expected,
			ParameterizedData data, String position) {
		ParameterizedAssertAction action = action(ParameterizedAssertAction.class);
		action.setActualFormula(actual);
		action.setExpectedList(expected);
		action.setParameterizedData(data);

		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setPosition(position);

		return action;
	}

	public Action createFinishStageAction(boolean silent) {
		FinishStageAction action = action(FinishStageAction.class);
		action.setSilent(silent);
		return action;
	}

	public Action createTapAtAction(Sprite sprite, SequenceAction sequence, Formula x, Formula y) {
		TapAtAction action = Actions.action(TapAtAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setStartX(x);
		action.setStartY(y);
		return action;
	}

	public Action createTapForAction(Sprite sprite, SequenceAction sequence, Formula x, Formula y,
			Formula duration) {
		TapAtAction action = Actions.action(TapAtAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDurationFormula(duration);
		action.setStartX(x);
		action.setStartY(y);
		return action;
	}

	public Action createTouchAndSlideAction(Sprite sprite, SequenceAction sequence, Formula x, Formula y,
			Formula xChange, Formula yChange, Formula duration) {
		TapAtAction action = Actions.action(TapAtAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setDurationFormula(duration);
		action.setStartX(x);
		action.setStartY(y);
		action.setChangeX(xChange);
		action.setChangeY(yChange);
		return action;
	}

	public Action createWriteVariableOnDeviceAction(UserVariable userVariable) {
		WriteUserDataOnDeviceAction action = Actions.action(WriteUserDataOnDeviceAction.class);
		File projectDirectory = ProjectManager.getInstance().getCurrentProject().getDirectory();
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(projectDirectory);
		action.setUserData(userVariable);
		action.setAccessor(accessor);

		return action;
	}

	public Action createWriteVariableToFileAction(Sprite sprite, SequenceAction sequence,
			Formula variableFormula, UserVariable userVariable) {
		WriteVariableToFileAction action = Actions.action(WriteVariableToFileAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setUserVariable(userVariable);
		action.setFormula(variableFormula);

		return action;
	}

	public Action createReadVariableFromFileAction(Sprite sprite, SequenceAction sequence, Formula variableFormula,
			UserVariable userVariable, boolean deleteFile) {
		ReadVariableFromFileAction action = Actions.action(ReadVariableFromFileAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setUserVariable(userVariable);
		action.setFormula(variableFormula);
		action.setDeleteFile(deleteFile);

		return action;
	}

	public Action createWriteListOnDeviceAction(UserList userList) {
		WriteUserDataOnDeviceAction action = Actions.action(WriteUserDataOnDeviceAction.class);
		File projectDirectory = ProjectManager.getInstance().getCurrentProject().getDirectory();
		DeviceUserDataAccessor accessor = new DeviceListAccessor(projectDirectory);
		UserData data = userList;
		action.setUserData(data);
		action.setAccessor(accessor);

		return action;
	}

	public Action createWaitTillIdleAction() {
		return action(WaitTillIdleAction.class);
	}

	public Action createReadVariableFromDeviceAction(UserVariable userVariable) {
		ReadVariableFromDeviceAction action = Actions.action(ReadVariableFromDeviceAction.class);
		action.setUserVariable(userVariable);

		return action;
	}

	public Action createReadListFromDeviceAction(UserList userList) {
		ReadListFromDeviceAction action = Actions.action(ReadListFromDeviceAction.class);
		action.setUserList(userList);

		return action;
	}

	public Action createWebRequestAction(Sprite sprite, SequenceAction sequence, Formula variableFormula,
			UserVariable userVariable) {
		WebRequestAction action = action(WebRequestAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(variableFormula);
		action.setUserVariable(userVariable);
		return action;
	}

	public Action createLookRequestAction(Sprite sprite, SequenceAction sequence,
			Formula variableFormula) {
		LookRequestAction action = action(LookRequestAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(variableFormula);
		return action;
	}

	public Action createOpenUrlAction(Sprite sprite, SequenceAction sequence, Formula variableFormula) {
		OpenUrlAction action = action(OpenUrlAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(variableFormula);
		return action;
	}

	public Action createStartListeningAction(UserVariable userVariable) {
		// This is a fix to get the StartListeningBrick to work on Huawei Phones,
		// can be changed once HMS is fully implemented and working
		// As soon as this is the case, remove the if-statement and only use the else-branch
		if (get(MobileServiceAvailability.class).isHmsAvailable(ProjectManager.getInstance().getApplicationContext())) {
			AskSpeechAction action = Actions.action(AskSpeechAction.class);
			action.setAnswerVariable(userVariable);
			return action;
		} else {
			StartListeningAction action = Actions.action(StartListeningAction.class);
			action.setUserVariable(userVariable);
			return action;
		}
	}

	public Action createSetListeningLanguageAction(String listeningLanguageTag) {
		SetListeningLanguageAction action = action(SetListeningLanguageAction.class);
		action.listeningLanguageTag = listeningLanguageTag;
		return action;
	}

	public Action createPaintNewLookAction(Sprite sprite, SequenceAction sequence,
			Formula variableFormula, SetNextLookAction nextLookAction) {
		PaintNewLookAction action = action(PaintNewLookAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(variableFormula);
		action.nextLookAction(nextLookAction);
		return action;
	}

	public Action createCopyLookAction(Sprite sprite, SequenceAction sequence, Formula variableFormula,
			SetNextLookAction nextLookAction) {
		CopyLookAction action = action(CopyLookAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.setFormula(variableFormula);
		action.nextLookAction(nextLookAction);
		return action;
	}

	public Action createEditLookAction(Sprite sprite, SequenceAction sequence,
			SetNextLookAction nextLookAction) {
		EditLookAction action = action(EditLookAction.class);
		Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
		action.setScope(scope);
		action.nextLookAction(nextLookAction);
		return action;
	}
}
