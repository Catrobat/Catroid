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
package org.catrobat.catroid.io;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.common.DroneVideoLookData;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.LegoNXTSetting;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.WhenClonedScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.backwardcompatibility.LegacyDataContainer;
import org.catrobat.catroid.content.backwardcompatibility.LegacyProjectWithoutScenes;
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;
import org.catrobat.catroid.content.backwardcompatibility.ProjectUntilLanguageVersion0999;
import org.catrobat.catroid.content.backwardcompatibility.SceneUntilLanguageVersion0999;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.AssertEqualsBrick;
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
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
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
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
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
import org.catrobat.catroid.content.bricks.SetNfcTagBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTextBrick;
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
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula;
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
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick;
import org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick;
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.utils.StringFinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.TMP_CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public final class XstreamSerializer {

	private static XstreamSerializer instance;
	private static final String TAG = XstreamSerializer.class.getSimpleName();
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";
	private static final String PROGRAM_NAME_START_TAG = "<programName>";
	private static final String PROGRAM_NAME_END_TAG = "</programName>";

	private BackwardCompatibleCatrobatLanguageXStream xstream;
	private Lock loadSaveLock = new ReentrantLock();

	private XstreamSerializer() {
		prepareXstream(Project.class, Scene.class);
	}

	public static XstreamSerializer getInstance() {
		if (instance == null) {
			instance = new XstreamSerializer();
		}
		return instance;
	}

	private void prepareXstream(Class projectClass, Class sceneClass) {
		xstream = new BackwardCompatibleCatrobatLanguageXStream(
				new PureJavaReflectionProvider(new FieldDictionary(new CatroidFieldKeySorter())));

		xstream.allowTypesByWildcard(new String[] {"org.catrobat.catroid.**"});

		xstream.processAnnotations(projectClass);
		xstream.processAnnotations(sceneClass);

		xstream.processAnnotations(Sprite.class);
		xstream.processAnnotations(XmlHeader.class);
		xstream.processAnnotations(Setting.class);
		xstream.processAnnotations(UserVariableBrickWithFormula.class);
		xstream.processAnnotations(UserListBrick.class);

		xstream.registerConverter(new XStreamConcurrentFormulaHashMapConverter());
		xstream.registerConverter(new XStreamUserVariableConverter(xstream.getMapper(), xstream.getReflectionProvider(),
				xstream.getClassLoaderReference()));
		xstream.registerConverter(new XStreamBrickConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamScriptConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamSpriteConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamSettingConverter(xstream.getMapper(), xstream.getReflectionProvider()));

		xstream.omitField(sceneClass, "originalWidth");
		xstream.omitField(sceneClass, "originalHeight");

		xstream.omitField(Sprite.class, "userBricks");

		xstream.omitField(LegacyDataContainer.class, "userBrickVariableList");

		xstream.omitField(CameraBrick.class, "spinnerValues");
		xstream.omitField(ChooseCameraBrick.class, "spinnerValues");
		xstream.omitField(FlashBrick.class, "spinnerValues");

		xstream.omitField(SetNfcTagBrick.class, "nfcTagNdefDefaultType");

		xstream.omitField(SpeakAndWaitBrick.class, "speechFile");
		xstream.omitField(SpeakAndWaitBrick.class, "duration");

		xstream.omitField(StopScriptBrick.class, "spinnerValue");

		xstream.omitField(ShowTextBrick.class, "userVariableName");
		xstream.omitField(HideTextBrick.class, "userVariableName");
		xstream.omitField(HideTextBrick.class, "formulaList");

		xstream.omitField(SayBubbleBrick.class, "type");
		xstream.omitField(SayBubbleBrick.class, "type");

		xstream.omitField(ThinkBubbleBrick.class, "type");
		xstream.omitField(ThinkForBubbleBrick.class, "type");

		xstream.omitField(StartScript.class, "isUserScript");
		xstream.omitField(WhenScript.class, "action");

		xstream.omitField(RaspiInterruptScript.class, "receivedMessage");

		xstream.alias("look", LookData.class);
		xstream.alias("droneLook", DroneVideoLookData.class);
		xstream.alias("sound", SoundInfo.class);
		xstream.alias("nfcTag", NfcTagData.class);
		xstream.alias("userVariable", UserVariable.class);
		xstream.alias("userList", UserList.class);

		xstream.alias("script", Script.class);
		xstream.alias("object", Sprite.class);
		xstream.alias("object", SingleSprite.class);
		xstream.alias("object", GroupSprite.class);
		xstream.alias("object", GroupItemSprite.class);

		xstream.alias("script", StartScript.class);
		xstream.alias("script", WhenClonedScript.class);
		xstream.alias("script", WhenScript.class);
		xstream.alias("script", WhenConditionScript.class);
		xstream.alias("script", WhenNfcScript.class);
		xstream.alias("script", BroadcastScript.class);
		xstream.alias("script", RaspiInterruptScript.class);
		xstream.alias("script", WhenTouchDownScript.class);
		xstream.alias("script", WhenBackgroundChangesScript.class);

		xstream.alias("brick", AddItemToUserListBrick.class);
		xstream.alias("brick", AskBrick.class);
		xstream.alias("brick", AskSpeechBrick.class);
		xstream.alias("brick", BroadcastBrick.class);
		xstream.alias("brick", BroadcastReceiverBrick.class);
		xstream.alias("brick", BroadcastWaitBrick.class);
		xstream.alias("brick", ChangeBrightnessByNBrick.class);
		xstream.alias("brick", ChangeColorByNBrick.class);
		xstream.alias("brick", ChangeTransparencyByNBrick.class);
		xstream.alias("brick", ChangeSizeByNBrick.class);
		xstream.alias("brick", ChangeVariableBrick.class);
		xstream.alias("brick", ChangeVolumeByNBrick.class);
		xstream.alias("brick", ChangeXByNBrick.class);
		xstream.alias("brick", ChangeYByNBrick.class);
		xstream.alias("brick", ClearBackgroundBrick.class);
		xstream.alias("brick", ClearGraphicEffectBrick.class);
		xstream.alias("brick", CloneBrick.class);
		xstream.alias("brick", ComeToFrontBrick.class);
		xstream.alias("brick", DeleteItemOfUserListBrick.class);
		xstream.alias("brick", DeleteThisCloneBrick.class);
		xstream.alias("brick", ForeverBrick.class);
		xstream.alias("brick", GlideToBrick.class);
		xstream.alias("brick", GoNStepsBackBrick.class);
		xstream.alias("brick", HideBrick.class);
		xstream.alias("brick", HideTextBrick.class);

		xstream.alias("brick", IfLogicBeginBrick.class);
		xstream.alias("brick", IfLogicElseBrick.class);
		xstream.alias("brick", IfLogicEndBrick.class);
		xstream.alias("brick", IfThenLogicBeginBrick.class);
		xstream.alias("brick", IfThenLogicEndBrick.class);

		xstream.alias("brick", IfOnEdgeBounceBrick.class);
		xstream.alias("brick", InsertItemIntoUserListBrick.class);
		xstream.alias("brick", FlashBrick.class);
		xstream.alias("brick", ChooseCameraBrick.class);
		xstream.alias("brick", CameraBrick.class);
		xstream.alias("brick", LegoNxtMotorMoveBrick.class);
		xstream.alias("brick", LegoNxtMotorStopBrick.class);
		xstream.alias("brick", LegoNxtMotorTurnAngleBrick.class);
		xstream.alias("brick", LegoNxtPlayToneBrick.class);
		xstream.alias("brick", LoopEndBrick.class);
		xstream.alias("brick", LoopEndlessBrick.class);
		xstream.alias("brick", MoveNStepsBrick.class);
		xstream.alias("brick", NextLookBrick.class);
		xstream.alias("brick", NoteBrick.class);
		xstream.alias("brick", PenDownBrick.class);
		xstream.alias("brick", PenUpBrick.class);
		xstream.alias("brick", PlaceAtBrick.class);
		xstream.alias("brick", GoToBrick.class);
		xstream.alias("brick", PlaySoundBrick.class);
		xstream.alias("brick", PlaySoundAndWaitBrick.class);
		xstream.alias("brick", PointInDirectionBrick.class);
		xstream.alias("brick", PointToBrick.class);
		xstream.alias("brick", PreviousLookBrick.class);
		xstream.alias("brick", RepeatBrick.class);
		xstream.alias("brick", RepeatUntilBrick.class);
		xstream.alias("brick", ReplaceItemInUserListBrick.class);
		xstream.alias("brick", SceneTransitionBrick.class);
		xstream.alias("brick", SceneStartBrick.class);
		xstream.alias("brick", SetBrightnessBrick.class);
		xstream.alias("brick", SetColorBrick.class);
		xstream.alias("brick", SetTransparencyBrick.class);
		xstream.alias("brick", SetLookBrick.class);
		xstream.alias("brick", SetLookByIndexBrick.class);
		xstream.alias("brick", SetBackgroundBrick.class);
		xstream.alias("brick", SetBackgroundByIndexBrick.class);
		xstream.alias("brick", SetBackgroundAndWaitBrick.class);
		xstream.alias("brick", SetBackgroundByIndexAndWaitBrick.class);
		xstream.alias("brick", SetPenColorBrick.class);
		xstream.alias("brick", SetPenSizeBrick.class);
		xstream.alias("brick", SetRotationStyleBrick.class);
		xstream.alias("brick", SetSizeToBrick.class);
		xstream.alias("brick", SetVariableBrick.class);
		xstream.alias("brick", SetVolumeToBrick.class);
		xstream.alias("brick", SetXBrick.class);
		xstream.alias("brick", SetYBrick.class);
		xstream.alias("brick", ShowBrick.class);
		xstream.alias("brick", ShowTextBrick.class);
		xstream.alias("brick", SpeakBrick.class);
		xstream.alias("brick", SpeakAndWaitBrick.class);
		xstream.alias("brick", StampBrick.class);
		xstream.alias("brick", StopAllSoundsBrick.class);
		xstream.alias("brick", ThinkBubbleBrick.class);
		xstream.alias("brick", SayBubbleBrick.class);
		xstream.alias("brick", ThinkForBubbleBrick.class);
		xstream.alias("brick", SayForBubbleBrick.class);
		xstream.alias("brick", TurnLeftBrick.class);
		xstream.alias("brick", TurnRightBrick.class);
		xstream.alias("brick", VibrationBrick.class);
		xstream.alias("brick", WaitBrick.class);
		xstream.alias("brick", WaitUntilBrick.class);
		xstream.alias("brick", WhenBrick.class);
		xstream.alias("brick", WhenConditionBrick.class);
		xstream.alias("brick", WhenBackgroundChangesBrick.class);
		xstream.alias("brick", WhenStartedBrick.class);
		xstream.alias("brick", WhenClonedBrick.class);
		xstream.alias("brick", WriteVariableOnDeviceBrick.class);
		xstream.alias("brick", WriteListOnDeviceBrick.class);
		xstream.alias("brick", ReadVariableFromDeviceBrick.class);
		xstream.alias("brick", ReadListFromDeviceBrick.class);
		xstream.alias("brick", StopScriptBrick.class);
		xstream.alias("brick", WebRequestBrick.class);

		xstream.alias("brick", WhenNfcBrick.class);
		xstream.alias("brick", SetNfcTagBrick.class);

		xstream.alias("brick", DronePlayLedAnimationBrick.class);
		xstream.alias("brick", DroneTakeOffLandBrick.class);
		xstream.alias("brick", DroneMoveForwardBrick.class);
		xstream.alias("brick", DroneMoveBackwardBrick.class);
		xstream.alias("brick", DroneMoveUpBrick.class);
		xstream.alias("brick", DroneMoveDownBrick.class);
		xstream.alias("brick", DroneMoveLeftBrick.class);
		xstream.alias("brick", DroneMoveRightBrick.class);
		xstream.alias("brick", DroneTurnLeftBrick.class);
		xstream.alias("brick", DroneTurnRightBrick.class);
		xstream.alias("brick", DroneSwitchCameraBrick.class);
		xstream.alias("brick", DroneEmergencyBrick.class);

		xstream.alias("brick", PhiroMotorMoveBackwardBrick.class);
		xstream.alias("brick", PhiroMotorMoveForwardBrick.class);
		xstream.alias("brick", PhiroMotorStopBrick.class);
		xstream.alias("brick", PhiroPlayToneBrick.class);
		xstream.alias("brick", PhiroRGBLightBrick.class);
		xstream.alias("brick", PhiroIfLogicBeginBrick.class);

		xstream.alias("brick", LegoEv3PlayToneBrick.class);
		xstream.alias("brick", LegoEv3MotorMoveBrick.class);
		xstream.alias("brick", LegoEv3MotorStopBrick.class);
		xstream.alias("brick", LegoEv3SetLedBrick.class);

		xstream.alias("brick", ArduinoSendPWMValueBrick.class);
		xstream.alias("brick", ArduinoSendDigitalValueBrick.class);

		xstream.alias("brick", RaspiSendDigitalValueBrick.class);
		xstream.alias("brick", RaspiIfLogicBeginBrick.class);
		xstream.alias("brick", RaspiPwmBrick.class);

		xstream.alias("script", WhenGamepadButtonScript.class);
		xstream.alias("brick", WhenGamepadButtonBrick.class);

		xstream.alias("brick", AssertEqualsBrick.class);
		xstream.alias("brick", TapAtBrick.class);
		xstream.alias("brick", DroneFlipBrick.class);
		xstream.alias("brick", JumpingSumoAnimationsBrick.class);
		xstream.alias("brick", JumpingSumoJumpHighBrick.class);
		xstream.alias("brick", JumpingSumoJumpLongBrick.class);
		xstream.alias("brick", JumpingSumoMoveBackwardBrick.class);
		xstream.alias("brick", JumpingSumoMoveForwardBrick.class);
		xstream.alias("brick", JumpingSumoNoSoundBrick.class);
		xstream.alias("brick", JumpingSumoRotateLeftBrick.class);
		xstream.alias("brick", JumpingSumoRotateRightBrick.class);
		xstream.alias("brick", JumpingSumoSoundBrick.class);
		xstream.alias("brick", JumpingSumoTakingPictureBrick.class);
		xstream.alias("brick", JumpingSumoTurnBrick.class);
		xstream.alias("brick", LegoEv3MotorTurnAngleBrick.class);
		xstream.alias("brick", SetTextBrick.class);
		xstream.alias("brick", ShowTextColorSizeAlignmentBrick.class);
		xstream.alias("brick", StitchBrick.class);
		xstream.alias("brick", RunningStitchBrick.class);
		xstream.alias("brick", WaitTillIdleBrick.class);
		xstream.alias("brick", WhenRaspiPinChangedBrick.class);
		xstream.alias("brick", WhenTouchDownBrick.class);

		xstream.alias("script", WhenBounceOffScript.class);
		xstream.alias("brick", WhenBounceOffBrick.class);

		xstream.alias("brick", SetBounceBrick.class);
		xstream.alias("brick", SetFrictionBrick.class);
		xstream.alias("brick", SetGravityBrick.class);
		xstream.alias("brick", SetMassBrick.class);
		xstream.alias("brick", SetPhysicsObjectTypeBrick.class);
		xstream.alias("brick", SetVelocityBrick.class);
		xstream.alias("brick", TurnLeftSpeedBrick.class);
		xstream.alias("brick", TurnRightSpeedBrick.class);

		xstream.alias("setting", LegoNXTSetting.class);
		xstream.alias("nxtPort", LegoNXTSetting.NXTPort.class);
	}

	public Project loadProject(File projectDir, Context context) throws IOException, LoadingProjectException {
		cleanUpTmpCodeFile(projectDir);

		File xmlFile = new File(projectDir, CODE_XML_FILE_NAME);
		if (!xmlFile.exists()) {
			throw new FileNotFoundException(xmlFile.getAbsolutePath() + " does not exist.");
		}
		xmlFile.setLastModified(System.currentTimeMillis());

		try {
			loadSaveLock.lock();

			Project project;
			ProjectData projectMetaData = new ProjectMetaDataParser(xmlFile).getProjectMetaData();

			if (!projectMetaData.hasScenes()) {
				new File(projectDir, IMAGE_DIRECTORY_NAME).mkdir();
				new File(projectDir, SOUND_DIRECTORY_NAME).mkdir();

				prepareXstream(LegacyProjectWithoutScenes.class, Scene.class);
				LegacyProjectWithoutScenes projectWithoutScenes =
						(LegacyProjectWithoutScenes) xstream.getProjectFromXML(xmlFile);
				prepareXstream(Project.class, Scene.class);

				project = projectWithoutScenes.toProject(context);
			} else if (projectMetaData.getLanguageVersion() < 0.9991) {
				prepareXstream(ProjectUntilLanguageVersion0999.class, SceneUntilLanguageVersion0999.class);
				ProjectUntilLanguageVersion0999 legacyProject =
						(ProjectUntilLanguageVersion0999) xstream.getProjectFromXML(xmlFile);
				prepareXstream(Project.class, Scene.class);

				project = legacyProject.toProject();
			} else {
				prepareXstream(Project.class, Scene.class);
				project = (Project) xstream.getProjectFromXML(xmlFile);

				for (Scene scene : project.getSceneList()) {
					scene.setProject(project);
				}
			}

			project.setDirectory(projectDir);
			setFileReferences(project);
			return project;
		} catch (Exception e) {
			throw new LoadingProjectException("Cannot load project from " + projectDir.getAbsolutePath()
					+ "\nException: " + e.getLocalizedMessage());
		} finally {
			loadSaveLock.unlock();
		}
	}

	public static boolean renameProject(File xmlFile, String dstName) throws IOException {
		if (!xmlFile.exists()) {
			throw new FileNotFoundException(xmlFile + " does not exist.");
		}

		String currentXml = Files.toString(xmlFile, Charsets.UTF_8);
		StringFinder stringFinder = new StringFinder();

		if (!stringFinder.findBetween(currentXml, PROGRAM_NAME_START_TAG, PROGRAM_NAME_END_TAG)) {
			return false;
		}

		String srcName = stringFinder.getResult();
		dstName = getXMLEncodedString(dstName);

		if (srcName.equals(dstName)) {
			return true;
		}

		String srcProjectNameTag = PROGRAM_NAME_START_TAG + srcName + PROGRAM_NAME_END_TAG;
		String dstProjectNameTag = PROGRAM_NAME_START_TAG + dstName + PROGRAM_NAME_END_TAG;
		String newXml = currentXml.replace(srcProjectNameTag, dstProjectNameTag);

		if (currentXml.equals(newXml)) {
			Log.e(TAG, "Cannot find projectNameTag in code.xml");
			return false;
		}

		StorageOperations.writeToFile(xmlFile, newXml);
		return true;
	}

	private static String getXMLEncodedString(String srcName) {
		srcName = new XStream().toXML(srcName);
		srcName = srcName.replace("<string>", "");
		srcName = srcName.replace("</string>", "");
		return srcName;
	}

	private static void setFileReferences(Project project) {
		for (Scene scene : project.getSceneList()) {
			File imageDir = new File(scene.getDirectory(), IMAGE_DIRECTORY_NAME);
			File soundDir = new File(scene.getDirectory(), SOUND_DIRECTORY_NAME);

			for (Sprite sprite : scene.getSpriteList()) {
				for (Iterator<LookData> iterator = sprite.getLookList().iterator(); iterator.hasNext(); ) {
					LookData lookData = iterator.next();
					File lookFile = new File(imageDir, lookData.getXstreamFileName());

					if (lookFile.exists()) {
						lookData.setFile(lookFile);
					} else {
						iterator.remove();
					}
				}

				for (Iterator<SoundInfo> iterator = sprite.getSoundList().iterator(); iterator.hasNext(); ) {
					SoundInfo soundInfo = iterator.next();
					File soundFile = new File(soundDir, soundInfo.getXstreamFileName());

					if (soundFile.exists()) {
						soundInfo.setFile(soundFile);
					} else {
						iterator.remove();
					}
				}
			}
		}
	}

	public boolean saveProject(Project project) {
		if (project == null) {
			return false;
		}

		try {
			cleanUpTmpCodeFile(project.getDirectory());
		} catch (LoadingProjectException e) {
			return false;
		}

		loadSaveLock.lock();
		project.getXmlHeader().setApplicationBuildType(BuildConfig.BUILD_TYPE);
		try {
			String currentXml = XML_HEADER.concat(xstream.toXML(project));
			File tmpCodeFile = new File(project.getDirectory(), TMP_CODE_XML_FILE_NAME);
			File currentCodeFile = new File(project.getDirectory(), CODE_XML_FILE_NAME);

			if (currentCodeFile.exists()) {
				try {
					String previousXml = Files.toString(currentCodeFile, Charsets.UTF_8);

					if (previousXml.equals(currentXml)) {
						Log.d(TAG, "Project version is the same. Do not update " + currentCodeFile.getName());
						return false;
					}
				} catch (Exception e) {
					Log.e(TAG, "Opening project at " + currentCodeFile.getAbsolutePath() + " failed.", e);
					return false;
				}
			}

			StorageOperations.createDir(DEFAULT_ROOT_DIRECTORY);
			StorageOperations.createDir(project.getDirectory());

			for (Scene scene : project.getSceneList()) {
				StorageOperations.createSceneDirectory(scene.getDirectory());
			}
			StorageOperations.writeToFile(tmpCodeFile, currentXml);

			if (currentCodeFile.exists() && !currentCodeFile.delete()) {
				Log.e(TAG, "Cannot delete " + currentCodeFile.getName());
			}

			if (!tmpCodeFile.renameTo(currentCodeFile)) {
				Log.e(TAG, "Cannot rename code.xml for " + project.getName());
			}

			return true;
		} catch (Exception exception) {
			Log.e(TAG, "Saving project " + project.getName() + " failed.", exception);
			return false;
		} finally {
			loadSaveLock.unlock();
		}
	}

	private void cleanUpTmpCodeFile(File projectDir) throws LoadingProjectException {
		loadSaveLock.lock();

		File tmpXmlFile = new File(projectDir, TMP_CODE_XML_FILE_NAME);
		File actualXmlFile = new File(projectDir, CODE_XML_FILE_NAME);

		try {
			if (tmpXmlFile.exists()) {
				if (actualXmlFile.exists()) {
					tmpXmlFile.delete();
				} else {
					if (!tmpXmlFile.renameTo(actualXmlFile)) {
						throw new LoadingProjectException(CODE_XML_FILE_NAME + " did not exist. But wait, renaming "
								+ tmpXmlFile.getAbsolutePath() + " failed too.");
					}
				}
			}
		} finally {
			loadSaveLock.unlock();
		}
	}

	public String getXmlAsStringFromProject(Project project) {
		loadSaveLock.lock();
		String xmlString;
		try {
			prepareXstream(project.getClass(), project.getSceneList().get(0).getClass());
			xmlString = xstream.toXML(project);
			prepareXstream(Project.class, Scene.class);
		} finally {
			loadSaveLock.unlock();
		}
		return xmlString;
	}

	public static String extractDefaultSceneNameFromXml(File projectDir) {
		File xmlFile = new File(projectDir, CODE_XML_FILE_NAME);

		StringFinder stringFinder = new StringFinder();

		try {
			String xml = Files.toString(xmlFile, Charsets.UTF_8);
			if (!stringFinder.findBetween(xml, "<scenes>\\s*<scene>\\s*<name>", "</name>")) {
				return null;
			} else {
				return stringFinder.getResult();
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return null;
	}

	@VisibleForTesting
	public BackwardCompatibleCatrobatLanguageXStream getXstream() {
		return xstream;
	}
}
