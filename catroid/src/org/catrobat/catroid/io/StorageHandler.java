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
package org.catrobat.catroid.io;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Backpack;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.DroneVideoLookData;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.LegoNXTSetting;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.SupportProject;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
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
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
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
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
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
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick;
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiPwmBrick;
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
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
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.SupportDataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
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
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;
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
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.NO_MEDIA_FILE;
import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME;
import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME_TMP;
import static org.catrobat.catroid.common.Constants.PROJECTPERMISSIONS_NAME;
import static org.catrobat.catroid.common.Constants.SCENES_DIRECTORY;
import static org.catrobat.catroid.common.Constants.SCENES_ENABLED_TAG;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY;
import static org.catrobat.catroid.utils.Utils.buildPath;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;
import static org.catrobat.catroid.utils.Utils.buildScenePath;

public final class StorageHandler {
	private static final StorageHandler INSTANCE;
	private static final String TAG = StorageHandler.class.getSimpleName();
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";
	private static final int JPG_COMPRESSION_SETTING = 95;
	public static final String BACKPACK_FILENAME = "backpack.json";

	private XStreamToSupportCatrobatLanguageVersion0991AndBefore xstream;
	private Gson backpackGson;

	private FileInputStream fileInputStream;

	private File backPackSoundDirectory;
	private File backPackImageDirectory;

	private Lock loadSaveLock = new ReentrantLock();
	// TODO: Since the StorageHandler constructor throws an exception, the member INSTANCE couldn't be assigned
	// directly and therefore we need this static block. Should be refactored and removed in the future.
	static {
		try {
			INSTANCE = new StorageHandler();
		} catch (IOException ioException) {
			throw new RuntimeException("Initialize StorageHandler failed");
		}
	}
	private StorageHandler() throws IOException {
		prepareProgramXstream(false);
		prepareBackpackGson();

		if (!Utils.externalStorageAvailable()) {
			throw new IOException("Could not read external storage");
		}
		createCatroidRoot();
	}

	public static StorageHandler getInstance() {
		return INSTANCE;
	}

	private void prepareProgramXstream(boolean forSupportProject) {
		xstream = new XStreamToSupportCatrobatLanguageVersion0991AndBefore(new PureJavaReflectionProvider(new
				FieldDictionary(new CatroidFieldKeySorter())));
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
		xstream.registerConverter(new XStreamSettingConverter(xstream.getMapper(), xstream.getReflectionProvider()));

		setProgramXstreamAliases();
	}

	private void prepareBackpackGson() {
		GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting();
		gsonBuilder.registerTypeAdapter(Script.class, new BackpackScriptSerializerAndDeserializer());
		gsonBuilder.registerTypeAdapter(Brick.class, new BackpackBrickSerializerAndDeserializer());
		backpackGson = gsonBuilder.create();
	}

	public static void saveBitmapToImageFile(File outputFile, Bitmap bitmap) throws FileNotFoundException {
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		try {
			if (outputFile.getName().toLowerCase(Locale.US).endsWith(".jpg")
					|| outputFile.getName().toLowerCase(Locale.US).endsWith(".jpeg")) {
				bitmap.compress(CompressFormat.JPEG, JPG_COMPRESSION_SETTING, outputStream);
			} else {
				bitmap.compress(CompressFormat.PNG, 0, outputStream);
			}
			outputStream.flush();
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				Log.e(TAG, "Could not close outputstream.", e);
			}
		}
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

		xstream.alias("script", StartScript.class);
		xstream.alias("script", WhenScript.class);
		xstream.alias("script", WhenNfcScript.class);
		xstream.alias("script", BroadcastScript.class);
		xstream.alias("script", RaspiInterruptScript.class);
		xstream.alias("script", WhenTouchDownScript.class);

		xstream.alias("brick", AddItemToUserListBrick.class);
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
		xstream.alias("brick", ClearGraphicEffectBrick.class);
		xstream.alias("brick", ComeToFrontBrick.class);
		xstream.alias("brick", DeleteItemOfUserListBrick.class);
		xstream.alias("brick", ForeverBrick.class);
		xstream.alias("brick", GlideToBrick.class);
		xstream.alias("brick", GoNStepsBackBrick.class);
		xstream.alias("brick", HideBrick.class);
		xstream.alias("brick", HideTextBrick.class);
		xstream.alias("brick", IfLogicBeginBrick.class);
		xstream.alias("brick", IfLogicElseBrick.class);
		xstream.alias("brick", IfLogicEndBrick.class);
		xstream.alias("brick", IfThenLogicBeginBrick.class);
		xstream.alias("brick", IfThenLogicEndBrick .class);
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
		xstream.alias("brick", PlaceAtBrick.class);
		xstream.alias("brick", PlaySoundBrick.class);
		xstream.alias("brick", PointInDirectionBrick.class);
		xstream.alias("brick", PointToBrick.class);
		xstream.alias("brick", RepeatBrick.class);
		xstream.alias("brick", RepeatUntilBrick.class);
		xstream.alias("brick", ReplaceItemInUserListBrick.class);
		xstream.alias("brick", SceneTransitionBrick.class);
		xstream.alias("brick", SceneStartBrick.class);
		xstream.alias("brick", SetBrightnessBrick.class);
		xstream.alias("brick", SetColorBrick.class);
		xstream.alias("brick", SetTransparencyBrick.class);
		xstream.alias("brick", SetLookBrick.class);
		xstream.alias("brick", SetSizeToBrick.class);
		xstream.alias("brick", SetVariableBrick.class);
		xstream.alias("brick", SetVolumeToBrick.class);
		xstream.alias("brick", SetXBrick.class);
		xstream.alias("brick", SetYBrick.class);
		xstream.alias("brick", ShowBrick.class);
		xstream.alias("brick", ShowTextBrick.class);
		xstream.alias("brick", SpeakBrick.class);
		xstream.alias("brick", StopAllSoundsBrick.class);
		xstream.alias("brick", TurnLeftBrick.class);
		xstream.alias("brick", TurnRightBrick.class);
		xstream.alias("brick", UserBrick.class);
		xstream.alias("brick", UserScriptDefinitionBrick.class);
		xstream.alias("brick", VibrationBrick.class);
		xstream.alias("brick", WaitBrick.class);
		xstream.alias("brick", WaitUntilBrick.class);
		xstream.alias("brick", WhenBrick.class);
		xstream.alias("brick", WhenStartedBrick.class);

		xstream.alias("brick", WhenNfcBrick.class);

		xstream.alias("brick", DronePlayLedAnimationBrick.class);
		xstream.alias("brick", DroneFlipBrick.class);
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

		xstream.alias("brick", ArduinoSendPWMValueBrick.class);
		xstream.alias("brick", ArduinoSendDigitalValueBrick.class);

		xstream.alias("brick", RaspiSendDigitalValueBrick.class);
		xstream.alias("brick", RaspiIfLogicBeginBrick.class);
		xstream.alias("brick", RaspiPwmBrick.class);

		xstream.alias("userBrickElement", UserScriptDefinitionBrickElement.class);
		xstream.alias("userBrickParameter", UserBrickParameter.class);

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

	private void createCatroidRoot() {
		File catroidRoot = new File(DEFAULT_ROOT);
		if (!catroidRoot.exists()) {
			catroidRoot.mkdirs();
		}
		try {
			createBackPackFileStructure();
		} catch (IOException e) {
			Log.e(TAG, "Creating backpack file structure failed");
		}
	}

	private boolean checkIfProjectHasScenes(String projectName) throws IOException {
		File projectXmlFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
		String projectXml = Files.toString(projectXmlFile, Charsets.UTF_8);
		return projectXml.contains(SCENES_ENABLED_TAG);
	}

	public String getFirstSceneName(String projectName) {
		try {
			if (!checkIfProjectHasScenes(projectName)) {
				return null;
			}
		} catch (IOException e) {
			Log.e(TAG, "Exception getFirstSceneName", e);
			return null;
		}
		File projectXmlFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
		String projectXml;
		try {
			projectXml = Files.toString(projectXmlFile, Charsets.UTF_8);
		} catch (IOException e) {
			Log.e(TAG, "Exception getFirstSceneName", e);
			return null;
		}
		int start = projectXml.indexOf("<scene>");
		int end = projectXml.indexOf("</name>", start);

		return projectXml.substring(start + 20, end);
	}

	public Project loadProject(String projectName, Context context) {
		File file = new File(DEFAULT_ROOT);
		if (!file.exists()) {
			Log.d(TAG, "Directory does not exist!");
			return null;
		}

		try {
			if (!checkIfProjectHasScenes(projectName)) {
				return loadSupportProject(projectName, context);
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not check Scene Tag!", e);
			return null;
		}

		assertTrue(codeFileSanityCheck(projectName));
		Log.d(TAG, "loadProject " + projectName);
		if (!projectExists(projectName)) {
			return null;
		}

		loadSaveLock.lock();
		Project project = null;
		try {
			project = (Project) xstream.getProjectFromXML(new File(buildProjectPath(projectName), PROJECTCODE_NAME));
			for (String sceneName : project.getSceneOrder()) {
				project.getSceneByName(sceneName).setProject(project);
				project.getSceneByName(sceneName).getDataContainer().setProject(project);
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not get Project from xml and get Scene from order", e);
			loadSaveLock.unlock();
			return null;
		}
		loadSaveLock.unlock();

		return project;
	}

	public static void copyDirectory(File destinationFile, File sourceFile) throws IOException {
		if (!sourceFile.exists()) {
			return;
		}
		if (sourceFile.isDirectory()) {
			destinationFile.mkdirs();
			for (String subDirectoryName : sourceFile.list()) {
				copyDirectory(new File(destinationFile, subDirectoryName), new File(sourceFile, subDirectoryName));
			}
		} else {
			UtilFile.copyFile(destinationFile, sourceFile);
		}
	}

	private void fixFolderStructureForSupportProject(String projectName, String sceneName) throws IOException {
		projectName = UtilFile.encodeSpecialCharsForFileSystem(projectName);
		sceneName = UtilFile.encodeSpecialCharsForFileSystem(sceneName);
		String projectPath = buildProjectPath(projectName);
		String scenePath = buildScenePath(projectName, sceneName);
		File looksDirectory = new File(buildPath(projectPath, IMAGE_DIRECTORY));
		File soundsDirectory = new File(buildPath(projectPath, SOUND_DIRECTORY));
		File sceneDirectoryLooks = new File(buildPath(scenePath, IMAGE_DIRECTORY));
		File sceneDirectorySounds = new File(buildPath(scenePath, SOUND_DIRECTORY));
		File automaticScreenshot = new File(projectPath, StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File manualScreenshot = new File(projectPath, StageListener.SCREENSHOT_MANUAL_FILE_NAME);
		File sceneAutomaticScreenshot = new File(scenePath, StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File sceneManualScreenshot = new File(scenePath, StageListener.SCREENSHOT_MANUAL_FILE_NAME);

		copyDirectory(sceneDirectoryLooks, looksDirectory);
		copyDirectory(sceneDirectorySounds, soundsDirectory);

		if (automaticScreenshot.exists()) {
			FileUtils.copyFileToDir(automaticScreenshot, sceneAutomaticScreenshot);
			automaticScreenshot.delete();
		}
		if (manualScreenshot.exists()) {
			FileUtils.copyFileToDir(manualScreenshot, sceneManualScreenshot);
			manualScreenshot.delete();
		}
		UtilFile.deleteDirectory(looksDirectory);
		UtilFile.deleteDirectory(soundsDirectory);
	}

	public Project loadSupportProject(String projectName, Context context) {
		File file = new File(DEFAULT_ROOT);
		if (!file.exists()) {
			Log.d(TAG, "Directory does not exist!");
			return null;
		}

		assertTrue(codeFileSanityCheck(projectName));
		Log.d(TAG, "loadSupportProject " + projectName);
		if (!projectExists(projectName)) {
			return null;
		}

		loadSaveLock.lock();
		try {
			File projectCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
			fileInputStream = new FileInputStream(projectCodeFile);
			prepareProgramXstream(true);
			SupportProject supportProject = (SupportProject) xstream.getProjectFromXML(projectCodeFile);
			prepareProgramXstream(false);
			Project project = new Project(supportProject, context);
			fixFolderStructureForSupportProject(projectName, project.getDefaultScene().getName());
			return project;
		} catch (IOException e) {
			Log.d(TAG, "Could not load project!");
			UtilFile.deleteDirectory(file);
			Log.d(TAG, "loadProject: directory is deleted and "
					+ "default project should be restored!");
			Log.e(TAG, "Exception: ", e);
			return null;
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException ioException) {
					Log.e(TAG, "can't close fileStream.", ioException);
				}
			}
			loadSaveLock.unlock();
		}
	}

	public boolean cancelLoadProject() {
		if (fileInputStream != null) {
			try {
				fileInputStream.close();
				return true;
			} catch (IOException ioException) {
				Log.e(TAG, "can't close fileStream.", ioException);
			}
		}
		return false;
	}

	public boolean saveProject(Project project) {
		BufferedWriter writer = null;

		if (project == null) {
			Log.d(TAG, "project is null!");
			return false;
		}

		Log.d(TAG, "saveProject " + project.getName());

		codeFileSanityCheck(project.getName());

		loadSaveLock.lock();

		String projectXml;
		File tmpCodeFile = null;
		File currentCodeFile = null;

		try {
			projectXml = XML_HEADER.concat(xstream.toXML(project));
			tmpCodeFile = new File(buildProjectPath(project.getName()), PROJECTCODE_NAME_TMP);
			currentCodeFile = new File(buildProjectPath(project.getName()), PROJECTCODE_NAME);

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

			File permissionFile = new File(buildProjectPath(project.getName()), PROJECTPERMISSIONS_NAME);
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
				} catch (IOException ioException) {
					Log.e(TAG, "Failed closing the buffered writer", ioException);
				}
			}

			loadSaveLock.unlock();
		}
	}

	public Scene createDefaultScene(String sceneName, boolean drone, boolean landscape, Context context) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		try {
			if (drone) {
				DefaultProjectHandler.getInstance().setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
						.PROJECT_CREATOR_DRONE);
			} else {
				DefaultProjectHandler.getInstance().setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
						.PROJECT_CREATOR_DEFAULT);
			}
			Project project = DefaultProjectHandler.createDefaultProjectForScene(context, landscape);
			if (!project.getDefaultScene().rename(sceneName, context, false)) {
				loadSaveLock.lock();
				deleteProject(project.getName());
				loadSaveLock.unlock();
				ProjectManager.getInstance().setProject(currentProject);
				ProjectManager.getInstance().setCurrentScene(currentProject.getDefaultScene());
				return null;
			}

			File defaultSceneDir = new File(buildScenePath(project.getName(), project.getDefaultScene().getName()));
			File targetSceneDir = new File(buildScenePath(currentProject.getName(), sceneName));

			copyDirectory(targetSceneDir, defaultSceneDir);
			project.getDefaultScene().setProject(currentProject);
			project.getDefaultScene().resetDataContainerForDefaultScene();
			loadSaveLock.lock();
			deleteProject(project.getName());
			loadSaveLock.unlock();
			ProjectManager.getInstance().setProject(currentProject);
			ProjectManager.getInstance().setCurrentScene(currentProject.getDefaultScene());
			return project.getDefaultScene();
		} catch (IOException e) {
			Log.e(TAG, "Error while creating default Scene!", e);
			return null;
		}
	}

	public boolean saveBackpack(Backpack backpack) {
		Log.d(TAG, "Saving backpack json");
		FileWriter writer = null;
		String json = backpackGson.toJson(backpack);
		Log.d(TAG, json);

		try {
			File backpackFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, BACKPACK_FILENAME));
			if (!backpackFile.exists()) {
				backpackFile.createNewFile();
			}
			writer = new FileWriter(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, BACKPACK_FILENAME));
			writer.write(json);
			return true;
		} catch (IOException e) {
			Log.e(TAG, "Could not write backpack file", e);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ioException) {
					Log.e(TAG, "Failed closing the buffered writer", ioException);
				}
			}
		}
	}

	public Backpack loadBackpack() {
		Log.d(TAG, "Loading backpack json");
		File backpackFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, BACKPACK_FILENAME));
		if (!backpackFile.exists()) {
			Log.d(TAG, "Backpack file does not exist!");
			return null;
		}

		try {
			BufferedReader bufferedBackpackReader = new BufferedReader(new FileReader(backpackFile));
			return backpackGson.fromJson(bufferedBackpackReader, Backpack.class);
		} catch (FileNotFoundException e) {
			Log.d(TAG, "Could not find backpack file!");
			return new Backpack();
		} catch (JsonSyntaxException | JsonIOException jsonException) {
			Log.d(TAG, "Could not load backpack file! File will be deleted!", jsonException);
			deleteBackpackFile();
			return new Backpack();
		}
	}

	public boolean deleteBackpackFile() {
		File backpackFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, BACKPACK_FILENAME));
		if (!backpackFile.exists()) {
			Log.d(TAG, "Backpack file does not exist!");
			return false;
		}
		Log.d(TAG, "Deleting backpack.json");
		return backpackFile.delete();
	}

	public boolean codeFileSanityCheck(String projectName) {
		loadSaveLock.lock();

		try {
			File tmpCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME_TMP);

			if (tmpCodeFile.exists()) {
				File currentCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
				if (currentCodeFile.exists()) {
					Log.w(TAG, "TMP File probably corrupted. Both files exist. Discard " + tmpCodeFile.getName());

					if (!tmpCodeFile.delete()) {
						Log.e(TAG, "Could not delete " + tmpCodeFile.getName());
//						fail("Could not delete " + tmpCodeFile.getName());
					}
//					fail("TMP File probably corrupted. Both files exist. Discard " + tmpCodeFile.getName());
					return false;
				}

				Log.w(TAG, "Process interrupted before renaming. Rename " + PROJECTCODE_NAME_TMP
						+ " to " + PROJECTCODE_NAME);

				if (!tmpCodeFile.renameTo(currentCodeFile)) {
					Log.e(TAG, "Could not rename " + tmpCodeFile.getName());
//					fail("Could not rename " + tmpCodeFile.getName());
					return false;
				}
			}
		} catch (Exception exception) {
			Log.e(TAG, "Exception " + exception);
		} finally {
			loadSaveLock.unlock();
		}
		return true;
	}

	public Scene cloneScene(Scene sourceScene) throws IOException, NullPointerException {
		Project project = new Project();
		List<Scene> list = new ArrayList<>();
		File codeFile = createTempCodeFile();
		list.add(sourceScene);
		project.setSceneList(list);

		String xml = xstream.toXML(project);
		Files.write(xml, codeFile, Charset.defaultCharset());

		project = (Project) xstream.getProjectFromXML(codeFile);
		Scene result = project.getDefaultScene();

		UtilFile.deleteDirectory(new File(Constants.TMP_PATH));

		if (result == null) {
			throw new NullPointerException("Scene was not found in Project!");
		}
		return result;
	}

	private File createTempCodeFile() throws IOException {
		File tmpDir = new File(Constants.TMP_PATH);
		if (!tmpDir.exists()) {
			tmpDir.mkdir();
		}
		File codeFile = new File(Constants.TMP_PATH, Constants.PROJECTCODE_NAME);
		codeFile.createNewFile();
		return codeFile;
	}

	private void createProjectFileStructure(Project project) throws IOException {
		Log.d(TAG, "create Project Data structure");
		createCatroidRoot();
		File projectDirectory = new File(buildProjectPath(project.getName()));
		projectDirectory.mkdir();

		for (Scene scene : project.getSceneList()) {
			File sceneDirectory = new File(buildScenePath(project.getName(), scene.getName()));
			createSceneFileStructure(sceneDirectory);
		}
	}

	private void createSceneFileStructure(File sceneDirectory) throws IOException {
		Log.d(TAG, "create Scene Data structure");
		sceneDirectory.mkdir();

		File imageDirectory = new File(sceneDirectory, IMAGE_DIRECTORY);
		imageDirectory.mkdir();

		File noMediaFile = new File(imageDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();

		File soundDirectory = new File(sceneDirectory, SOUND_DIRECTORY);
		soundDirectory.mkdir();

		noMediaFile = new File(soundDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();
	}

	private void createBackPackFileStructure() throws IOException {
		File backPackDirectory = new File(DEFAULT_ROOT, BACKPACK_DIRECTORY);
		backPackDirectory.mkdir();

		File backPackSceneDirectory = new File(backPackDirectory, SCENES_DIRECTORY);
		backPackSceneDirectory.mkdir();

		backPackSoundDirectory = new File(backPackDirectory, BACKPACK_SOUND_DIRECTORY);
		backPackSoundDirectory.mkdir();

		backPackImageDirectory = new File(backPackDirectory, BACKPACK_IMAGE_DIRECTORY);
		backPackImageDirectory.mkdir();
	}

	public void clearBackPackSoundDirectory() {
		if (backPackSoundDirectory == null) {
			Log.d(TAG, "Backpack sound directory not created yet - probably project was never saved before");
			return;
		}
		File[] backPackFiles = backPackSoundDirectory.listFiles();
		if (backPackFiles != null && backPackFiles.length > 1) {
			for (File node : backPackSoundDirectory.listFiles()) {
				if (!(node.getName().equals(".nomedia"))) {
					node.delete();
				}
			}
		}
	}

	public void clearBackPackLookDirectory() {
		if (backPackImageDirectory == null) {
			Log.d(TAG, "Backpack image directory not created yet - probably project was never saved before");
			return;
		}
		File[] backPackFiles = backPackImageDirectory.listFiles();
		if (backPackFiles != null && backPackFiles.length > 1) {
			for (File node : backPackImageDirectory.listFiles()) {
				if (!(node.getName().equals(".nomedia"))) {
					node.delete();
				}
			}
		}
	}

	public boolean deleteProject(String projectName) {
		return UtilFile.deleteDirectory(new File(buildProjectPath(projectName)));
	}

	public boolean deleteScene(String projectName, String sceneName) {
		return UtilFile.deleteDirectory(new File(buildScenePath(projectName, sceneName)));
	}

	public boolean projectExists(String projectName) {
		List<String> projectNameList = UtilFile.getProjectNames(new File(DEFAULT_ROOT));
		for (String projectNameIterator : projectNameList) {
			if (projectNameIterator.equals(projectName)) {
				return true;
			}
		}
		return false;
	}

	public File copySoundFile(String path) throws IOException, IllegalArgumentException {
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		String currentScene = ProjectManager.getInstance().getCurrentScene().getName();
		File soundDirectory = new File(buildPath(buildScenePath(currentProject, currentScene), SOUND_DIRECTORY));

		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead()) {
			throw new IllegalArgumentException("file " + path + " doesn`t exist or can`t be read");
		}
		String inputFileChecksum = Utils.md5Checksum(inputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		if (fileChecksumContainer.containsChecksum(inputFileChecksum)) {
			fileChecksumContainer.addChecksum(inputFileChecksum, null);
			return new File(fileChecksumContainer.getPath(inputFileChecksum));
		}
		File outputFile = new File(buildPath(soundDirectory.getAbsolutePath(),
				inputFileChecksum + "_" + inputFile.getName()));

		return copyFileAddCheckSum(outputFile, inputFile);
	}

	private File copyFileBackPack(String programSubDirectory, String backpackSubDirectory, String inputFilePath,
			String newTitle, boolean copyFromBackpack) throws IOException, IllegalArgumentException {
		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			Log.e(TAG, "file " + inputFilePath + " doesn`t exist or can`t be read");
			return null;
		}
		String inputFileChecksum = Utils.md5Checksum(inputFile);

		String fileFormat = inputFilePath.substring(inputFilePath.lastIndexOf('.'), inputFilePath.length());
		String outputFilePath;
		if (copyFromBackpack) {
			String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
			String currentScene = ProjectManager.getInstance().getCurrentScene().getName();
			outputFilePath = buildPath(buildScenePath(currentProject, currentScene), programSubDirectory,
					inputFileChecksum + "_" + newTitle + fileFormat);
		} else {
			outputFilePath = buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, backpackSubDirectory,
					inputFileChecksum + "_" + newTitle + fileFormat);
			FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
			if (!fileChecksumContainer.containsChecksumBackPack(inputFileChecksum)) {
				fileChecksumContainer.addChecksumBackPack(inputFileChecksum, outputFilePath);
			}
		}

		File outputFile = new File(outputFilePath);
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		return copyFileAddCheckSum(outputFile, inputFile);
	}

	public File copySoundFileBackPack(SoundInfo selectedSoundInfo, String newTitle, boolean copyFromBackpack) throws IOException, IllegalArgumentException {
		if (selectedSoundInfo == null) {
			return null;
		}
		String inputFilePath;
		if (copyFromBackpack) {
			inputFilePath = selectedSoundInfo.getAbsoluteBackPackPath();
		} else {
			inputFilePath = selectedSoundInfo.getAbsoluteProjectPath();
		}
		return copyFileBackPack(SOUND_DIRECTORY, BACKPACK_SOUND_DIRECTORY, inputFilePath, newTitle, copyFromBackpack);
	}

	public File copyImageBackPack(LookData selectedLookData, String newName, boolean copyFromBackpack)
			throws IOException {
		if (selectedLookData == null) {
			return null;
		}
		String inputFilePath;
		if (copyFromBackpack) {
			inputFilePath = selectedLookData.getAbsoluteBackPackPath();
		} else {
			inputFilePath = selectedLookData.getAbsoluteProjectPath();
		}
		return copyFileBackPack(IMAGE_DIRECTORY, BACKPACK_IMAGE_DIRECTORY, inputFilePath, newName, copyFromBackpack);
	}

	public File copyImageFromResourceToCatroid(Activity activity, int imageId, String defaultImageName) throws IOException {
		Bitmap newImage = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), imageId);
		String projectName = ProjectManager.getInstance().getCurrentProject().getName();
		String sceneName = ProjectManager.getInstance().getCurrentScene().getName();
		return createImageFromBitmap(projectName, sceneName, newImage, defaultImageName);
	}

	public File createImageFromBitmap(String currentProjectName, String currentSceneName, Bitmap inputImage, String
			newName) throws
			IOException {

		File imageDirectory = new File(buildPath(buildScenePath(currentProjectName, currentSceneName), IMAGE_DIRECTORY));

		File outputFileDirectory = new File(imageDirectory.getAbsolutePath());

		if (!outputFileDirectory.exists()) {
			outputFileDirectory.mkdirs();
		}

		File outputFile = new File(buildPath(imageDirectory.getAbsolutePath(), newName));

		return createFileFromBitmap(outputFile, inputImage, imageDirectory);
	}

	public File copyImage(String currentProjectName, String currentSceneName, String inputFilePath, String newName)
			throws IOException {
		String newFilePath;
		File imageDirectory = new File(buildPath(buildScenePath(currentProjectName, currentSceneName), IMAGE_DIRECTORY));

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		int[] imageDimensions = ImageEditing.getImageDimensions(inputFilePath);
		FileChecksumContainer checksumCont = ProjectManager.getInstance().getFileChecksumContainer();

		File outputFileDirectory = new File(imageDirectory.getAbsolutePath());
		if (!outputFileDirectory.exists()) {
			outputFileDirectory.mkdirs();
		}

		Project project = ProjectManager.getInstance().getCurrentProject();

		if ((imageDimensions[0] > project.getXmlHeader().virtualScreenWidth)
				&& (imageDimensions[1] > project.getXmlHeader().virtualScreenHeight)) {
			File outputFile = new File(buildPath(imageDirectory.getAbsolutePath(), inputFile.getName()));
			return copyAndResizeImage(outputFile, inputFile, imageDirectory);
		} else {
			String checksumSource = Utils.md5Checksum(inputFile);

			if (newName != null) {
				newFilePath = buildPath(imageDirectory.getAbsolutePath(), checksumSource + "_" + newName);
			} else {
				newFilePath = buildPath(imageDirectory.getAbsolutePath(), checksumSource + "_" + inputFile.getName());
				if (checksumCont.containsChecksum(checksumSource)) {
					checksumCont.addChecksum(checksumSource, newFilePath);
					return new File(checksumCont.getPath(checksumSource));
				}
			}

			File outputFile = new File(newFilePath);
			return copyFileAddCheckSum(outputFile, inputFile);
		}
	}

	public File makeTempImageCopy(String inputFilePath) throws IOException {
		File tempDirectory = new File(Constants.TMP_PATH);

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		File outputFileDirectory = new File(tempDirectory.getAbsolutePath());
		if (!outputFileDirectory.exists()) {
			outputFileDirectory.mkdirs();
		}

		File outputFile = new File(Constants.TMP_IMAGE_PATH);

		File copiedFile = UtilFile.copyFile(outputFile, inputFile);

		return copiedFile;
	}

	public void deleteTempImageCopy() {
		File temporaryPictureFileInPocketPaint = new File(Constants.TMP_IMAGE_PATH);
		if (temporaryPictureFileInPocketPaint.exists()) {
			temporaryPictureFileInPocketPaint.delete();
		}
	}

	private File createFileFromBitmap(File outputFile, Bitmap inputImage, File imageDirectory) throws IOException {
		saveBitmapToImageFile(outputFile, inputImage);

		String checksumCompressedFile = Utils.md5Checksum(outputFile);
		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		String newFilePath = buildPath(imageDirectory.getAbsolutePath(),
				checksumCompressedFile + "_" + outputFile.getName());

		if (!fileChecksumContainer.addChecksum(checksumCompressedFile, newFilePath)) {

			return new File(fileChecksumContainer.getPath(checksumCompressedFile));
		}

		File compressedFile = new File(newFilePath);
		outputFile.renameTo(compressedFile);

		return compressedFile;
	}

	private File copyAndResizeImage(File outputFile, File inputFile, File imageDirectory) throws IOException {
		Project project = ProjectManager.getInstance().getCurrentProject();
		Bitmap bitmap = ImageEditing.getScaledBitmapFromPath(inputFile.getAbsolutePath(),
				project.getXmlHeader().virtualScreenWidth, project.getXmlHeader().virtualScreenHeight,
				ImageEditing.ResizeType.FILL_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);

		saveBitmapToImageFile(outputFile, bitmap);

		String checksumCompressedFile = Utils.md5Checksum(outputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		String newFilePath = buildPath(imageDirectory.getAbsolutePath(),
				checksumCompressedFile + "_" + inputFile.getName());

		if (!fileChecksumContainer.addChecksum(checksumCompressedFile, newFilePath)) {
			if (!outputFile.getAbsolutePath().equalsIgnoreCase(inputFile.getAbsolutePath())) {
				outputFile.delete();
			}
			return new File(fileChecksumContainer.getPath(checksumCompressedFile));
		}

		File compressedFile = new File(newFilePath);
		outputFile.renameTo(compressedFile);

		return compressedFile;
	}

	public void deleteFile(String filepath, boolean isBackPackFile) {
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		try {
			if (isBackPackFile) {
				File toDelete = new File(filepath);
				Log.d(TAG, "delete" + toDelete);
				toDelete.delete();
			} else if (container.decrementUsage(filepath)) {
				File toDelete = new File(filepath);
				Log.d(TAG, "delete" + toDelete);
				toDelete.delete();
			}
		} catch (FileNotFoundException fileNotFoundException) {
			Log.e(TAG, Log.getStackTraceString(fileNotFoundException));
		}
	}

	public void deleteAllFile(String filepath) {

		File toDelete = new File(filepath);

		if (toDelete.isDirectory()) {
			Log.d(TAG, "file is directory" + filepath);
			for (String file : toDelete.list()) {
				deleteAllFile(file);
			}
		}
		toDelete.delete();
	}

	public void fillChecksumContainer() {
		//FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		//if (container == null) {
		ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());
		//}
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();

		Project newProject = ProjectManager.getInstance().getCurrentProject();

		for (Scene scene : newProject.getSceneList()) {
			for (Sprite currentSprite : scene.getSpriteList()) {
				for (SoundInfo soundInfo : currentSprite.getSoundList()) {
					container.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
				}

				for (LookData lookData : currentSprite.getLookDataList()) {
					container.addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
				}
			}
		}
	}

	public String getXMLStringOfAProject(Project project) {
		loadSaveLock.lock();
		String xmlProject = "";
		try {
			xmlProject = xstream.toXML(project);
		} finally {
			loadSaveLock.unlock();
		}
		return xmlProject;
	}

	private File copyFileAddCheckSum(File destinationFile, File sourceFile) throws IOException {
		File copiedFile = UtilFile.copyFile(destinationFile, sourceFile);
		addChecksum(destinationFile, sourceFile);

		return copiedFile;
	}

	private void addChecksum(File destinationFile, File sourceFile) {
		String checksumSource = Utils.md5Checksum(sourceFile);
		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		fileChecksumContainer.addChecksum(checksumSource, destinationFile.getAbsolutePath());
	}

	private Set<String> generatePermissionsSetFromResource(int resources) {
		Set<String> permissionsSet = new HashSet<String>();

		if ((resources & Brick.TEXT_TO_SPEECH) > 0) {
			permissionsSet.add(Constants.TEXT_TO_SPEECH);
		}
		if ((resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			permissionsSet.add(Constants.BLUETOOTH_LEGO_NXT);
		}
		if ((resources & Brick.ARDRONE_SUPPORT) > 0) {
			permissionsSet.add(Constants.ARDRONE_SUPPORT);
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

	public boolean copyImageFiles(String targetScene, String targetProject, String sourceScene, String sourceProject) {
		return copyFiles(targetScene, targetProject, sourceScene, sourceProject, false);
	}

	public boolean copySoundFiles(String targetScene, String targetProject, String sourceScene, String sourceProject) {
		return copyFiles(targetScene, targetProject, sourceScene, sourceProject, true);
	}

	private boolean copyFiles(String targetScene, String targetProject, String sourceScene, String sourceProject, boolean copySoundFiles) {
		String type = IMAGE_DIRECTORY;
		if (copySoundFiles) {
			type = SOUND_DIRECTORY;
		}
		File targetDirectory = new File(buildPath(buildScenePath(targetProject, targetScene), type));
		File sourceDirectory = new File(buildPath(buildScenePath(sourceProject, sourceScene), type));
		if (!targetDirectory.exists() || !sourceDirectory.exists()) {
			return false;
		}
		try {
			for (File sourceFile : sourceDirectory.listFiles()) {
				File targetFile = new File(targetDirectory.getAbsolutePath(), sourceFile.getName());
				FileChannel source = new FileInputStream(sourceFile).getChannel();
				FileChannel target = new FileOutputStream(targetFile).getChannel();
				target.transferFrom(source, 0, source.size());
				source.close();
				target.close();
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	public void updateCodefileOnDownload(String projectName) {
		File projectCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
		xstream.updateCollisionReceiverBrickMessage(projectCodeFile);
	}
}
