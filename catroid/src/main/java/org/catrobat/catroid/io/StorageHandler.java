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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.parrot.freeflight.utils.FileUtils;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.common.Backpack;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DroneVideoLookData;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
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
import org.catrobat.catroid.content.SupportProject;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.WhenClonedScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
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
import org.catrobat.catroid.content.bricks.FormulaBrick;
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
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick;
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
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
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
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
import org.catrobat.catroid.content.bricks.UserBrickParameter;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.formulaeditor.datacontainer.SupportDataContainer;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.StringFinder;
import org.catrobat.catroid.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.InvalidPathException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_FILE;
import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SCENE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.NO_MEDIA_FILE;
import static org.catrobat.catroid.common.Constants.PERMISSIONS_FILE_NAME;
import static org.catrobat.catroid.common.Constants.SCENES_ENABLED_TAG;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.TMP_CODE_XML_FILE_NAME;
import static org.catrobat.catroid.utils.PathBuilder.buildProjectPath;
import static org.catrobat.catroid.utils.PathBuilder.buildScenePath;

public final class StorageHandler {

	private static final StorageHandler INSTANCE;
	private static final String TAG = StorageHandler.class.getSimpleName();
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";

	private BackwardCompatibleCatrobatLanguageXStream xstream;
	private Gson backpackGson;

	private Lock loadSaveLock = new ReentrantLock();
	static {
		try {
			INSTANCE = new StorageHandler();
		} catch (IOException e) {
			throw new RuntimeException("Initialize StorageHandler failed");
		}
	}
	private StorageHandler() throws IOException {
		prepareProgramXstream(false);
		prepareBackpackGson();

		if (!Utils.externalStorageAvailable()) {
			throw new IOException("Could not read external storage");
		}
		createRootAndBackpack();
	}

	public static StorageHandler getInstance() {
		return INSTANCE;
	}

	private void prepareProgramXstream(boolean forSupportProject) {
		xstream = new BackwardCompatibleCatrobatLanguageXStream(
				new PureJavaReflectionProvider(new FieldDictionary(new CatroidFieldKeySorter())));
		if (forSupportProject) {
			xstream.processAnnotations(SupportProject.class);
			xstream.processAnnotations(SupportDataContainer.class);
		} else {
			xstream.processAnnotations(Project.class);
			xstream.processAnnotations(DataContainer.class);
		}
		xstream.processAnnotations(Scene.class);
		xstream.processAnnotations(Sprite.class);
		xstream.processAnnotations(XmlHeader.class);
		xstream.processAnnotations(Setting.class);
		xstream.processAnnotations(UserVariableBrick.class);
		xstream.processAnnotations(UserListBrick.class);
		xstream.registerConverter(new XStreamConcurrentFormulaHashMapConverter());
		xstream.registerConverter(new XStreamUserVariableConverter());
		xstream.registerConverter(new XStreamBrickConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamScriptConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamSpriteConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamSettingConverter(xstream.getMapper(), xstream.getReflectionProvider()));

		xstream.omitField(CameraBrick.class, "spinnerValues");
		xstream.omitField(ChooseCameraBrick.class, "spinnerValues");
		xstream.omitField(FlashBrick.class, "spinnerValues");
		xstream.omitField(StopScriptBrick.class, "spinnerValue");

		xstream.omitField(ShowTextBrick.class, "userVariableName");
		xstream.omitField(HideTextBrick.class, "userVariableName");

		xstream.omitField(RaspiInterruptScript.class, "receivedMessage");
		xstream.omitField(WhenScript.class, "action");

		setProgramXstreamAliases();
	}

	private void prepareBackpackGson() {
		GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting();
		gsonBuilder.registerTypeAdapter(Script.class, new BackpackScriptSerializerAndDeserializer());
		gsonBuilder.registerTypeAdapter(Brick.class, new BackpackBrickSerializerAndDeserializer());
		backpackGson = gsonBuilder.create();
	}

	private void setProgramXstreamAliases() {
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
		xstream.alias("brick", LoopBeginBrick.class);
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
		xstream.alias("brick", UserBrick.class);
		xstream.alias("brick", UserScriptDefinitionBrick.class);
		xstream.alias("brick", VibrationBrick.class);
		xstream.alias("brick", WaitBrick.class);
		xstream.alias("brick", WaitUntilBrick.class);
		xstream.alias("brick", WhenBrick.class);
		xstream.alias("brick", WhenConditionBrick.class);
		xstream.alias("brick", WhenBackgroundChangesBrick.class);
		xstream.alias("brick", WhenStartedBrick.class);
		xstream.alias("brick", WhenClonedBrick.class);
		xstream.alias("brick", StopScriptBrick.class);

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

		xstream.alias("userBrickElement", UserScriptDefinitionBrickElement.class);
		xstream.alias("userBrickParameter", UserBrickParameter.class);

		//Cast
		xstream.alias("script", WhenGamepadButtonScript.class);
		xstream.alias("brick", WhenGamepadButtonBrick.class);

		// Physics Script
		xstream.alias("script", CollisionScript.class);

		// Physics Bricks
		xstream.alias("brick", CollisionReceiverBrick.class);
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
		xstream.aliasAttribute(LegoNXTSetting.NXTPort.class, "number", "number");

		xstream.aliasField("formulaList", FormulaBrick.class, "formulaMap");
		xstream.aliasField("object", BrickBaseType.class, "sprite");
	}

	private void createRootAndBackpack() {
		DEFAULT_ROOT_DIRECTORY.mkdirs();
		BACKPACK_DIRECTORY.mkdir();
		BACKPACK_SCENE_DIRECTORY.mkdir();
		BACKPACK_IMAGE_DIRECTORY.mkdir();
		BACKPACK_SOUND_DIRECTORY.mkdir();
	}

	public Project loadProject(String projectName, Context context) throws IOException, LoadingProjectException {
		if (!DEFAULT_ROOT_DIRECTORY.exists()) {
			throw new IOException("Pocket Code root dir does not exist.");
		}

		cleanUpTmpCodeFile(projectName);

		File xmlFile = new File(buildProjectPath(projectName), CODE_XML_FILE_NAME);
		if (!Files.toString(xmlFile, Charsets.UTF_8).contains(SCENES_ENABLED_TAG)) {
			return loadSupportProject(projectName, context);
		}

		loadSaveLock.lock();
		Project project;

		try {
			project = (Project) xstream.getProjectFromXML(xmlFile);
			for (Scene scene : project.getSceneList()) {
				scene.setProject(project);
				scene.getDataContainer().setProject(project);
			}
			setFileReferences(project);
			return project;
		} catch (Exception e) {
			throw new LoadingProjectException("An Error occurred while parsing the xml: " + e.getMessage());
		} finally {
			loadSaveLock.unlock();
		}
	}

	private static void setFileReferences(Project project) {
		for (Scene scene : project.getSceneList()) {
			File sndDir = new File(scene.getDirectory(), SOUND_DIRECTORY_NAME);

			for (Sprite sprite : scene.getSpriteList()) {
				for (Iterator<SoundInfo> iterator = sprite.getSoundList().iterator(); iterator.hasNext(); ) {
					SoundInfo soundInfo = iterator.next();
					File soundFile = new File(sndDir, soundInfo.getFileName());

					if (soundFile.exists()) {
						soundInfo.setFile(soundFile);
					} else {
						iterator.remove();
					}
				}
			}
		}
	}

	private Project loadSupportProject(String name, Context context) throws IOException {
		loadSaveLock.lock();

		File xmlFile = new File(PathBuilder.buildProjectPath(name), CODE_XML_FILE_NAME);

		prepareProgramXstream(true);
		SupportProject supportProject = (SupportProject) xstream.getProjectFromXML(xmlFile);
		prepareProgramXstream(false);
		Project project = new Project(supportProject, context);
		fixFolderStructureForSupportProject(name, project.getDefaultScene().getName());

		loadSaveLock.unlock();
		return project;
	}

	private void fixFolderStructureForSupportProject(String projectName, String sceneName) throws IOException {
		String projectPath = buildProjectPath(projectName);
		String scenePath = buildScenePath(projectName, sceneName);

		File sceneDir = new File(scenePath);
		File projectImgDir = new File(PathBuilder.buildPath(projectPath, IMAGE_DIRECTORY_NAME));
		File projectSndDir = new File(PathBuilder.buildPath(projectPath, SOUND_DIRECTORY_NAME));

		sceneDir.mkdir();
		projectImgDir.mkdir();
		projectSndDir.mkdir();

		File automaticScreenshot = new File(projectPath, StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File manualScreenshot = new File(projectPath, StageListener.SCREENSHOT_MANUAL_FILE_NAME);
		File sceneAutomaticScreenshot = new File(scenePath, StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File sceneManualScreenshot = new File(scenePath, StageListener.SCREENSHOT_MANUAL_FILE_NAME);

		copyDir(new File(projectPath, IMAGE_DIRECTORY_NAME), new File(scenePath, IMAGE_DIRECTORY_NAME));
		copyDir(new File(projectPath, SOUND_DIRECTORY_NAME), new File(scenePath, SOUND_DIRECTORY_NAME));

		if (automaticScreenshot.exists()) {
			FileUtils.copyFileToDir(automaticScreenshot, sceneAutomaticScreenshot);
			automaticScreenshot.delete();
		}
		if (manualScreenshot.exists()) {
			FileUtils.copyFileToDir(manualScreenshot, sceneManualScreenshot);
			manualScreenshot.delete();
		}

		deleteDir(new File(projectPath, IMAGE_DIRECTORY_NAME));
		deleteDir(new File(projectPath, SOUND_DIRECTORY_NAME));
	}

	public boolean saveProject(Project project) {
		BufferedWriter writer = null;

		if (project == null) {
			Log.d(TAG, "project is null!");
			return false;
		}

		try {
			cleanUpTmpCodeFile(project.getName());
		} catch (LoadingProjectException e) {
			return false;
		}

		loadSaveLock.lock();

		String projectXml;
		File tmpCodeFile = null;
		File currentCodeFile = null;

		try {
			projectXml = XML_HEADER.concat(xstream.toXML(project));
			tmpCodeFile = new File(buildProjectPath(project.getName()), TMP_CODE_XML_FILE_NAME);
			currentCodeFile = new File(buildProjectPath(project.getName()), CODE_XML_FILE_NAME);

			if (currentCodeFile.exists()) {
				try {
					String oldProjectXml = Files.toString(currentCodeFile, Charsets.UTF_8);

					if (oldProjectXml.equals(projectXml)) {
						Log.d(TAG, "Project version is the same. Do not update " + currentCodeFile.getName());
						return false;
					}
					Log.d(TAG, "Project version differ <" + oldProjectXml.length() + "> <"
							+ projectXml.length() + ">. update " + currentCodeFile.getName());
				} catch (Exception exception) {
					Log.e(TAG, "Opening old project " + currentCodeFile.getAbsolutePath() + " failed.", exception);
					return false;
				}
			}

			createProjectFileStructure(project);

			writer = new BufferedWriter(new FileWriter(tmpCodeFile), Constants.BUFFER_8K);
			writer.write(projectXml);
			writer.flush();

			File permissionFile = new File(buildProjectPath(project.getName()), PERMISSIONS_FILE_NAME);
			writer = new BufferedWriter(new FileWriter(permissionFile), Constants.BUFFER_8K);

			for (String resource : generatePermissionsSetFromResource(project.getRequiredResources())) {
				writer.write(resource);
				writer.newLine();
			}
			writer.flush();

			return true;
		} catch (Exception exception) {
			Log.e(TAG, "Saving project " + project.getName() + " failed.", exception);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
					if (currentCodeFile.exists() && !currentCodeFile.delete()) {
						Log.e(TAG, "Could not delete " + currentCodeFile.getName());
					}

					if (!tmpCodeFile.renameTo(currentCodeFile)) {
						Log.e(TAG, "Could not rename " + currentCodeFile.getName());
					}
				} catch (IOException e) {
					Log.e(TAG, "Cannot close Buffered Writer", e);
				}
			}

			loadSaveLock.unlock();
		}
	}

	public boolean saveBackpack(Backpack backpack) {
		FileWriter writer = null;
		String json = backpackGson.toJson(backpack);

		try {
			BACKPACK_FILE.createNewFile();
			writer = new FileWriter(BACKPACK_FILE);
			writer.write(json);
			return true;
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					Log.e(TAG, "Cannot close Buffered Writer", e);
				}
			}
		}
	}

	public Backpack loadBackpack() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(BACKPACK_FILE));
			return backpackGson.fromJson(bufferedReader, Backpack.class);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "BackpackFile at " + BACKPACK_FILE.getAbsolutePath() + " not found.");
			return new Backpack();
		} catch (JsonSyntaxException | JsonIOException jsonException) {
			Log.e(TAG, "Cannot load Backpack.. Deleting File.", jsonException);
			BACKPACK_FILE.delete();
			return new Backpack();
		}
	}

	private void cleanUpTmpCodeFile(String projectName) throws LoadingProjectException {
		loadSaveLock.lock();

		File projectDir = new File(buildProjectPath(projectName));
		File tmpXmlFile = new File(projectDir, TMP_CODE_XML_FILE_NAME);
		File actualXmlFile = new File(projectDir, CODE_XML_FILE_NAME);

		if (tmpXmlFile.exists()) {
			if (actualXmlFile.exists()) {
				tmpXmlFile.delete();
			} else {
				if (!tmpXmlFile.renameTo(actualXmlFile)) {
					loadSaveLock.unlock();
					throw new LoadingProjectException(CODE_XML_FILE_NAME + " did not exist. Renaming "
							+ tmpXmlFile.getAbsolutePath() + " failed too.");
				}
			}
		}

		loadSaveLock.unlock();
	}

	private void createProjectFileStructure(Project project) throws IOException {
		createRootAndBackpack();
		File projectDirectory = new File(buildProjectPath(project.getName()));
		projectDirectory.mkdir();

		for (Scene scene : project.getSceneList()) {
			File sceneDirectory = new File(buildScenePath(project.getName(), scene.getName()));
			createSceneFileStructure(sceneDirectory);
		}
	}

	private void createSceneFileStructure(File sceneDirectory) throws IOException {
		sceneDirectory.mkdir();

		File imageDirectory = new File(sceneDirectory, IMAGE_DIRECTORY_NAME);
		imageDirectory.mkdir();

		File noMediaFile = new File(imageDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();

		File soundDirectory = new File(sceneDirectory, SOUND_DIRECTORY_NAME);
		soundDirectory.mkdir();

		noMediaFile = new File(soundDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();
	}

	public boolean projectExists(String projectName) {
		List<String> projectNameList = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY);
		for (String projectNameIterator : projectNameList) {
			if (projectNameIterator.equals(projectName)) {
				return true;
			}
		}
		return false;
	}

	public String getXmlAsStringFromProject(Project project) {
		loadSaveLock.lock();
		String xmlString;
		try {
			xmlString = xstream.toXML(project);
		} finally {
			loadSaveLock.unlock();
		}
		return xmlString;
	}

	private Set<String> generatePermissionsSetFromResource(int resources) {
		Set<String> permissionsSet = new HashSet<>();

		if ((resources & Brick.TEXT_TO_SPEECH) > 0) {
			permissionsSet.add(Constants.TEXT_TO_SPEECH);
		}
		if ((resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			permissionsSet.add(Constants.BLUETOOTH_LEGO_NXT);
		}
		if ((resources & Brick.ARDRONE_SUPPORT) > 0) {
			permissionsSet.add(Constants.ARDRONE_SUPPORT);
		}
		if ((resources & Brick.JUMPING_SUMO) > 0) {
			permissionsSet.add(Constants.JUMPING_SUMO_SUPPORT);
		}
		if ((resources & Brick.BLUETOOTH_PHIRO) > 0) {
			permissionsSet.add(Constants.BLUETOOTH_PHIRO_PRO);
		}
		if ((resources & Brick.CAMERA_FLASH) > 0) {
			permissionsSet.add(Constants.CAMERA_FLASH);
		}
		if ((resources & Brick.VIBRATOR) > 0) {
			permissionsSet.add(Constants.VIBRATOR);
		}
		if ((resources & Brick.FACE_DETECTION) > 0) {
			permissionsSet.add(Constants.FACE_DETECTION);
		}
		if ((resources & Brick.NFC_ADAPTER) > 0) {
			permissionsSet.add(Constants.NFC);
		}
		return permissionsSet;
	}

	public void updateCodefileOnDownload(String projectName) {
		File projectCodeFile = new File(buildProjectPath(projectName), CODE_XML_FILE_NAME);
		xstream.updateCollisionReceiverBrickMessage(projectCodeFile);
	}

	// TODO: THIS IS NEW, In the course of refactoring this should probably moved somewhere else.
	//
	// here are some more utility functions concerned with storage operations.

	private static final String FILE_NAME_APPENDIX = "_#";

	public static String getPathFromUri(ContentResolver contentResolver, Uri uri) {

		if (uri.getScheme().equalsIgnoreCase("file")) {
			return uri.getPath();
		}

		String[] projection = {MediaStore.MediaColumns.DATA};
		String[] arguments;
		String selection = null;
		String[] selectionArgs = null;

		if (uri.getScheme().equalsIgnoreCase("content") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

			String identifier = DocumentsContract.getDocumentId(uri);

			// Downloads
			if (uri.getAuthority().equalsIgnoreCase("com.android.providers.downloads.documents")) {
				uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(identifier));
				return resolveContent(contentResolver, uri, projection, selection, selectionArgs);
			}

			arguments = identifier.split(":");

			// External Storage
			if (uri.getAuthority().equalsIgnoreCase("com.android.externalstorage.documents")) {
				return Environment.getExternalStorageDirectory() + "/" + arguments[1];
			}

			selection = "_id=?";

			// Media Documents
			if (uri.getAuthority().equalsIgnoreCase("com.android.providers.media.documents")) {
				if (arguments[0].equalsIgnoreCase("audio")) {
					uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				if (arguments[0].equalsIgnoreCase("image")) {
					uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				}
				if (arguments[0].equalsIgnoreCase("video")) {
					uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				}

				selectionArgs = new String[] {arguments[1]};
				return resolveContent(contentResolver, uri, projection, selection, selectionArgs);
			}

			// Google Photos
			if (uri.getAuthority().equalsIgnoreCase("com.google.android.apps.photos.content")) {
				selectionArgs = new String[] {arguments[1]};
				return resolveContent(contentResolver, uri, projection, selection, selectionArgs);
			}
		}

		return "";
	}

	private static String resolveContent(ContentResolver contentResolver,
			Uri uri,
			String[] projection,
			String selection,
			String[] selectionArgs) {

		String path = "";
		Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
		cursor.moveToFirst();
		int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
		try {
			path = cursor.getString(index);
		} catch (CursorIndexOutOfBoundsException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			cursor.close();
		}
		return path;
	}

	public static String getSanitizedFileName(File file) {
		if (file.isDirectory()) {
			return file.getName();
		}

		String name = file.getName();
		int extensionStartIndex = name.lastIndexOf('.');
		int appendixStartIndex = name.lastIndexOf(FILE_NAME_APPENDIX);

		if (appendixStartIndex == -1) {
			appendixStartIndex = extensionStartIndex;
		}

		if (appendixStartIndex == -1) {
			return name;
		}

		return name.substring(0, appendixStartIndex);
	}

	public static File copyFile(File srcFile) throws IOException {
		File dstDir = srcFile.getParentFile();
		return copyFileToDirectory(srcFile, dstDir);
	}

	public static File copyFileToDirectory(File srcFile, File dstDir) throws IOException {
		if (!srcFile.exists()) {
			throw new FileNotFoundException("File: " + srcFile.getAbsolutePath() + " does not exist.");
		}

		if (!dstDir.exists()) {
			throw new FileNotFoundException("File: " + dstDir.getAbsolutePath() + " does not exist.");
		}

		if (!dstDir.isDirectory()) {
			throw new IOException("Destination: " + dstDir.getAbsolutePath() + " is not a directory.");
		}

		File dstFile = getUniqueFile(srcFile.getName(), dstDir);
		transferFromFileToFile(srcFile, dstFile);

		return dstFile;
	}

	public static File copyStreamToFile(InputStream inputStream, File dstFile) throws IOException {
		if (!dstFile.exists() && !dstFile.createNewFile()) {
			throw new IOException("Could not create dstFile at: " + dstFile.getAbsolutePath() + ".");
		}

		return transferStreamToFile(inputStream, dstFile);
	}

	public static File createFileFromStreamInDir(InputStream inputStream, File dstDir, String fileName) throws
			IOException, InvalidPathException {
		if (!dstDir.exists()) {
			throw new FileNotFoundException("Directory: " + dstDir.getAbsolutePath() + " does not exist.");
		}

		if (!dstDir.isDirectory()) {
			throw new IOException(dstDir.getAbsolutePath() + " is not a directory.");
		}

		File dstFile = getUniqueFile(fileName, dstDir);

		if (!dstFile.createNewFile()) {
			throw new IOException("Could not create dstFile at: " + dstFile.getAbsolutePath() + ".");
		}

		return transferStreamToFile(inputStream, dstFile);
	}

	private static File transferStreamToFile(InputStream inputStream, File dstFile) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(dstFile);

		byte[] b = new byte[Constants.BUFFER_8K];
		int len;

		try {
			while ((len = inputStream.read(b)) != -1) {
				fileOutputStream.write(b, 0, len);
			}
			return dstFile;
		} finally {
			inputStream.close();
			fileOutputStream.close();
		}
	}

	public static File copyDir(File srcDir, File dstDir) throws IOException {
		if (!srcDir.exists()) {
			throw new FileNotFoundException("Directory: " + srcDir.getAbsolutePath() + " does not exist.");
		}

		if (!srcDir.isDirectory()) {
			throw new IOException(srcDir.getAbsolutePath() + " is not a directory.");
		}

		dstDir.mkdir();

		if (!dstDir.isDirectory()) {
			throw new IOException("Directory: " + dstDir.getAbsolutePath() + " could not be created.");
		}

		for (File file : srcDir.listFiles()) {
			if (file.isDirectory()) {
				copyDir(file, new File(dstDir, file.getName()));
			} else {
				copyFileToDirectory(file, dstDir);
			}
		}

		return dstDir;
	}

	private static synchronized File getUniqueFile(String originalName, File dstDir) throws IOException {
		File dstFile = new File(dstDir, originalName);

		if (!dstFile.exists()) {
			return dstFile;
		}

		int extensionStartIndex = originalName.lastIndexOf('.');
		int appendixStartIndex = originalName.lastIndexOf(FILE_NAME_APPENDIX);

		if (appendixStartIndex == -1) {
			appendixStartIndex = extensionStartIndex;
		}

		String extension = originalName.substring(extensionStartIndex);
		String fileName = originalName.substring(0, appendixStartIndex);

		int appendix = 0;

		while (appendix < Integer.MAX_VALUE) {
			String dstFileName = fileName + FILE_NAME_APPENDIX + appendix + extension;
			dstFile = new File(dstDir, dstFileName);

			if (!dstFile.exists()) {
				return dstFile;
			}

			appendix++;
		}

		throw new IOException("Could not find a unique file name in " + dstDir.getAbsolutePath() + ".");
	}

	public static void transferFromFileToFile(File src, File dst) throws IOException {
		FileChannel ic = new FileInputStream(src).getChannel();
		FileChannel oc = new FileOutputStream(dst).getChannel();

		try {
			ic.transferTo(0, ic.size(), oc);
		} finally {
			if (ic != null) {
				ic.close();
			}
			oc.close();
		}
	}

	public static void deleteFile(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException("File: " + file.getAbsolutePath() + " does not exist.");
		}
		if (!file.delete()) {
			throw new IOException("File: " + file.getAbsolutePath() + " could not be deleted.");
		}
	}

	public static void deleteDir(File dir) throws IOException {
		if (!dir.exists()) {
			throw new FileNotFoundException("Directory: " + dir.getAbsolutePath() + " does not exist.");
		}
		if (!dir.isDirectory()) {
			throw new FileNotFoundException("Directory: " + dir.getAbsolutePath() + " is not a directory.");
		}

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				deleteDir(file);
			} else {
				deleteFile(file);
			}
		}

		if (!dir.delete()) {
			throw new IOException("Directory: " + dir.getAbsolutePath() + " could not be deleted.");
		}
	}

	public static String extractDefaultSceneNameFromXml(String projectName) {
		File projectDir = new File(buildProjectPath(projectName));
		File xmlFile = new File(projectDir, CODE_XML_FILE_NAME);

		StringFinder stringFinder = new StringFinder();

		try {
			String xml = Files.toString(xmlFile, Charsets.UTF_8);
			if (!stringFinder.findBetween(xml, "<scenes><scene><name>", "</name>")) {
				return null;
			} else {
				return stringFinder.getResult();
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return null;
	}
}
