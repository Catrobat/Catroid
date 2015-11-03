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
package org.catrobat.catroid.io;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.LegoNXTSetting;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneLandBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffBrick;
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
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.content.bricks.LedOffBrick;
import org.catrobat.catroid.content.bricks.LedOnBrick;
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
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
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
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElements;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.NO_MEDIA_FILE;
import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME;
import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME_TMP;
import static org.catrobat.catroid.common.Constants.PROJECTPERMISSIONS_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY;
import static org.catrobat.catroid.utils.Utils.buildPath;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;

public final class StorageHandler {
	private static final StorageHandler INSTANCE;
	private static final String TAG = StorageHandler.class.getSimpleName();
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";
	private static final int JPG_COMPRESSION_SETTING = 95;

	private XStreamToSupportCatrobatLanguageVersion096AndBefore xstream;

	private File backPackSoundDirectory;
	private FileInputStream fileInputStream;

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
		xstream = new XStreamToSupportCatrobatLanguageVersion096AndBefore(new PureJavaReflectionProvider(new FieldDictionary(new CatroidFieldKeySorter())));
		xstream.processAnnotations(Project.class);
		xstream.processAnnotations(XmlHeader.class);
		xstream.processAnnotations(DataContainer.class);
		xstream.processAnnotations(Setting.class);
		xstream.registerConverter(new XStreamConcurrentFormulaHashMapConverter());
		xstream.registerConverter(new XStreamUserVariableConverter());
		xstream.registerConverter(new XStreamBrickConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamScriptConverter(xstream.getMapper(), xstream.getReflectionProvider()));
		xstream.registerConverter(new XStreamSettingConverter(xstream.getMapper(), xstream.getReflectionProvider()));

		setXstreamAliases();

		if (!Utils.externalStorageAvailable()) {
			throw new IOException("Could not read external storage");
		}
		createCatroidRoot();
	}

	public static StorageHandler getInstance() {
		return INSTANCE;
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

	private void setXstreamAliases() {
		xstream.alias("look", LookData.class);
		xstream.alias("sound", SoundInfo.class);
		xstream.alias("userVariable", UserVariable.class);
		xstream.alias("userList", UserList.class);

		xstream.alias("script", Script.class);
		xstream.alias("object", Sprite.class);

		xstream.alias("script", StartScript.class);
		xstream.alias("script", WhenScript.class);
		xstream.alias("script", BroadcastScript.class);

		xstream.alias("brick", AddItemToUserListBrick.class);
		xstream.alias("brick", BroadcastBrick.class);
		xstream.alias("brick", BroadcastReceiverBrick.class);
		xstream.alias("brick", BroadcastWaitBrick.class);
		xstream.alias("brick", ChangeBrightnessByNBrick.class);
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
		xstream.alias("brick", IfOnEdgeBounceBrick.class);
		xstream.alias("brick", InsertItemIntoUserListBrick.class);
		xstream.alias("brick", LedOffBrick.class);
		xstream.alias("brick", LedOnBrick.class);
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
		xstream.alias("brick", ReplaceItemInUserListBrick.class);
		xstream.alias("brick", SetBrightnessBrick.class);
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
		xstream.alias("brick", WhenBrick.class);
		xstream.alias("brick", WhenStartedBrick.class);

		xstream.alias("brick", DronePlayLedAnimationBrick.class);
		xstream.alias("brick", DroneFlipBrick.class);
		xstream.alias("brick", DroneTakeOffBrick.class);
		xstream.alias("brick", DroneLandBrick.class);
		xstream.alias("brick", DroneMoveForwardBrick.class);
		xstream.alias("brick", DroneMoveBackwardBrick.class);
		xstream.alias("brick", DroneMoveUpBrick.class);
		xstream.alias("brick", DroneMoveDownBrick.class);
		xstream.alias("brick", DroneMoveLeftBrick.class);
		xstream.alias("brick", DroneMoveRightBrick.class);

		xstream.alias("brick", PhiroMotorMoveBackwardBrick.class);
		xstream.alias("brick", PhiroMotorMoveForwardBrick.class);
		xstream.alias("brick", PhiroMotorStopBrick.class);
		xstream.alias("brick", PhiroPlayToneBrick.class);
		xstream.alias("brick", PhiroRGBLightBrick.class);
		xstream.alias("brick", PhiroIfLogicBeginBrick.class);

		xstream.alias("brick", ArduinoSendPWMValueBrick.class);
		xstream.alias("brick", ArduinoSendDigitalValueBrick.class);

		xstream.alias("userBrickElements", UserScriptDefinitionBrickElements.class);
		xstream.alias("userBrickElement", UserScriptDefinitionBrickElement.class);
		xstream.alias("userBrickParameter", UserBrickParameter.class);

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
	}

	public String[] getLookFileList(String projectName) {
		File directoryLooks = new File(buildPath(Constants.DEFAULT_ROOT, projectName, Constants.IMAGE_DIRECTORY));

		return directoryLooks.list();
	}

	public String[] getSoundFileList(String projectName) {
		File directorySounds = new File(buildPath(Constants.DEFAULT_ROOT, projectName, Constants.SOUND_DIRECTORY));
		return directorySounds.list();
	}

	public File getBackPackSoundDirectory() {
		return backPackSoundDirectory;
	}

	public Project loadProject(String projectName) {
		codeFileSanityCheck(projectName);

		Log.d(TAG, "loadProject " + projectName);

		loadSaveLock.lock();
		try {
			File projectCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
			Log.d(TAG, "path: " + projectCodeFile.getAbsolutePath());
			fileInputStream = new FileInputStream(projectCodeFile);
			Project project = (Project) xstream.getProjectFromXML(projectCodeFile);
			return project;
		} catch (Exception exception) {
			Log.e(TAG, "Loading project " + projectName + " failed.", exception);
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
					Log.e(TAG, "Opening old project " + currentCodeFile.getName() + " failed.", exception);
					return false;
				}
			}

			File projectDirectory = new File(buildProjectPath(project.getName()));
			createProjectDataStructure(projectDirectory);

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

	public void codeFileSanityCheck(String projectName) {
		loadSaveLock.lock();

		try {
			File tmpCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME_TMP);

			if (tmpCodeFile.exists()) {
				File currentCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
				if (currentCodeFile.exists()) {
					Log.w(TAG, "TMP File probably corrupted. Both files exist. Discard " + tmpCodeFile.getName());

					if (!tmpCodeFile.delete()) {
						Log.e(TAG, "Could not delete " + tmpCodeFile.getName());
					}

					return;
				}

				Log.w(TAG, "Process interrupted before renaming. Rename " + PROJECTCODE_NAME_TMP
						+ " to " + PROJECTCODE_NAME);

				if (!tmpCodeFile.renameTo(currentCodeFile)) {
					Log.e(TAG, "Could not rename " + tmpCodeFile.getName());
				}
			}
		} catch (Exception exception) {
			Log.e(TAG, "Exception " + exception);
		} finally {
			loadSaveLock.unlock();
		}
	}

	private void createProjectDataStructure(File projectDirectory) throws IOException {
		Log.d(TAG, "create Project Data structure");
		createCatroidRoot();
		projectDirectory.mkdir();

		File imageDirectory = new File(projectDirectory, IMAGE_DIRECTORY);
		imageDirectory.mkdir();

		File noMediaFile = new File(imageDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();

		File soundDirectory = new File(projectDirectory, SOUND_DIRECTORY);
		soundDirectory.mkdir();

		noMediaFile = new File(soundDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();

		File backPackDirectory = new File(DEFAULT_ROOT, BACKPACK_DIRECTORY);
		backPackDirectory.mkdir();

		noMediaFile = new File(backPackDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();

		backPackSoundDirectory = new File(backPackDirectory, BACKPACK_SOUND_DIRECTORY);
		backPackSoundDirectory.mkdir();

		noMediaFile = new File(backPackSoundDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();

		File backPackImageDirectory = new File(backPackDirectory, BACKPACK_IMAGE_DIRECTORY);
		backPackImageDirectory.mkdir();

		noMediaFile = new File(backPackImageDirectory, NO_MEDIA_FILE);
		noMediaFile.createNewFile();
	}

	public void clearBackPackSoundDirectory() {
		try {
			if (backPackSoundDirectory.listFiles().length > 1) {
				for (File node : backPackSoundDirectory.listFiles()) {
					if (!(node.getName().equals(".nomedia"))) {
						node.delete();
					}
				}
			}
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, Log.getStackTraceString(nullPointerException));
		}
	}

	public boolean deleteProject(String projectName) {
		return UtilFile.deleteDirectory(new File(buildProjectPath(projectName)));
	}

	public boolean deleteProject(Project project) {
		if (project != null) {
			return deleteProject(project.getName());
		}
		return false;
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
		File soundDirectory = new File(buildPath(buildProjectPath(currentProject), SOUND_DIRECTORY));

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

	public File copySoundFileBackPack(SoundInfo selectedSoundInfo) throws IOException, IllegalArgumentException {

		String path = selectedSoundInfo.getAbsolutePath();

		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead()) {
			throw new IllegalArgumentException("file " + path + " doesn`t exist or can`t be read");
		}
		String inputFileChecksum = Utils.md5Checksum(inputFile);

		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();

		File outputFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, BACKPACK_SOUND_DIRECTORY, currentProject
				+ "_" + selectedSoundInfo.getTitle() + "_" + inputFileChecksum));

		return copyFileAddCheckSum(outputFile, inputFile);
	}

	public File copyImage(String currentProjectName, String inputFilePath, String newName) throws IOException {
		String newFilePath;
		File imageDirectory = new File(buildPath(buildProjectPath(currentProjectName), IMAGE_DIRECTORY));

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		int[] imageDimensions = new int[2];
		imageDimensions = ImageEditing.getImageDimensions(inputFilePath);
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

	public void deleteFile(String filepath) {
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		try {
			if (container.decrementUsage(filepath)) {
				File toDelete = new File(filepath);
				toDelete.delete();
			}
		} catch (FileNotFoundException fileNotFoundException) {
			Log.e(TAG, Log.getStackTraceString(fileNotFoundException));
			//deleteFile(filepath);
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
		List<Sprite> currentSpriteList = newProject.getSpriteList();

		for (Sprite currentSprite : currentSpriteList) {
			for (SoundInfo soundInfo : currentSprite.getSoundList()) {
				container.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
			}

			for (LookData lookData : currentSprite.getLookDataList()) {
				container.addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
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
		if ((resources & Brick.CAMERA_LED) > 0) {
			permissionsSet.add(Constants.CAMERA_LED);
		}
		if ((resources & Brick.VIBRATOR) > 0) {
			permissionsSet.add(Constants.VIBRATOR);
		}
		if ((resources & Brick.FACE_DETECTION) > 0) {
			permissionsSet.add(Constants.FACE_DETECTION);
		}
		return permissionsSet;
	}

	public boolean copyImageFiles(String targetProject, String sourceProject) {
		return copyFiles(targetProject, sourceProject, false);
	}

	public boolean copySoundFiles(String targetProject, String sourceProject) {
		return copyFiles(targetProject, sourceProject, true);
	}

	private boolean copyFiles(String targetProject, String sourceProject, boolean copySoundFiles) {
		String type = IMAGE_DIRECTORY;
		if (copySoundFiles) {
			type = SOUND_DIRECTORY;
		}
		File targetDirectory = new File(buildPath(buildProjectPath(targetProject), type));
		File sourceDirectory = new File(buildPath(buildProjectPath(sourceProject), type));
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
}
