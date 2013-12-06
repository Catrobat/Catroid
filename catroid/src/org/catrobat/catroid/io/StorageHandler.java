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
package org.catrobat.catroid.io;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.NO_MEDIA_FILE;
import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY;
import static org.catrobat.catroid.utils.Utils.buildPath;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.BrickBaseType;
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
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
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
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class StorageHandler {
	private static final StorageHandler INSTANCE;
	private static final String TAG = StorageHandler.class.getSimpleName();
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";
	private static final int JPG_COMPRESSION_SETTING = 95;

	private XStream xstream;

	private File backPackSoundDirectory;

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
		xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new CatroidFieldKeySorter())));
		xstream.processAnnotations(Project.class);
		xstream.processAnnotations(XmlHeader.class);
		xstream.processAnnotations(UserVariablesContainer.class);
		setXstreamAliases();

		if (!Utils.externalStorageAvailable()) {
			throw new IOException("Could not read external storage");
		}
		createCatroidRoot();
	}

	private void setXstreamAliases() {
		xstream.alias("look", LookData.class);
		xstream.alias("sound", SoundInfo.class);
		xstream.alias("userVariable", UserVariable.class);

		xstream.alias("broadcastScript", BroadcastScript.class);
		xstream.alias("script", Script.class);
		xstream.alias("object", Sprite.class);
		xstream.alias("startScript", StartScript.class);
		xstream.alias("whenScript", WhenScript.class);

		xstream.aliasField("object", BrickBaseType.class, "sprite");

		xstream.alias("broadcastBrick", BroadcastBrick.class);
		xstream.alias("broadcastReceiverBrick", BroadcastReceiverBrick.class);
		xstream.alias("broadcastWaitBrick", BroadcastWaitBrick.class);
		xstream.alias("changeBrightnessByNBrick", ChangeBrightnessByNBrick.class);
		xstream.alias("changeGhostEffectByNBrick", ChangeGhostEffectByNBrick.class);
		xstream.alias("changeSizeByNBrick", ChangeSizeByNBrick.class);
		xstream.alias("changeVariableBrick", ChangeVariableBrick.class);
		xstream.alias("changeVolumeByNBrick", ChangeVolumeByNBrick.class);
		xstream.alias("changeXByNBrick", ChangeXByNBrick.class);
		xstream.alias("changeYByNBrick", ChangeYByNBrick.class);
		xstream.alias("clearGraphicEffectBrick", ClearGraphicEffectBrick.class);
		xstream.alias("comeToFrontBrick", ComeToFrontBrick.class);
		xstream.alias("foreverBrick", ForeverBrick.class);
		xstream.alias("glideToBrick", GlideToBrick.class);
		xstream.alias("goNStepsBackBrick", GoNStepsBackBrick.class);
		xstream.alias("hideBrick", HideBrick.class);
		xstream.alias("ifLogicBeginBrick", IfLogicBeginBrick.class);
		xstream.alias("ifLogicElseBrick", IfLogicElseBrick.class);
		xstream.alias("ifLogicEndBrick", IfLogicEndBrick.class);
		xstream.alias("ifOnEdgeBounceBrick", IfOnEdgeBounceBrick.class);
		xstream.alias("legoNxtMotorActionBrick", LegoNxtMotorActionBrick.class);
		xstream.alias("legoNxtMotorStopBrick", LegoNxtMotorStopBrick.class);
		xstream.alias("legoNxtMotorTurnAngleBrick", LegoNxtMotorTurnAngleBrick.class);
		xstream.alias("legoNxtPlayToneBrick", LegoNxtPlayToneBrick.class);
		xstream.alias("loopBeginBrick", LoopBeginBrick.class);
		xstream.alias("loopEndBrick", LoopEndBrick.class);
		xstream.alias("loopEndlessBrick", LoopEndlessBrick.class);
		xstream.alias("moveNStepsBrick", MoveNStepsBrick.class);
		xstream.alias("nextLookBrick", NextLookBrick.class);
		xstream.alias("noteBrick", NoteBrick.class);
		xstream.alias("placeAtBrick", PlaceAtBrick.class);
		xstream.alias("playSoundBrick", PlaySoundBrick.class);
		xstream.alias("pointInDirectionBrick", PointInDirectionBrick.class);
		xstream.alias("pointToBrick", PointToBrick.class);
		xstream.alias("repeatBrick", RepeatBrick.class);
		xstream.alias("setBrightnessBrick", SetBrightnessBrick.class);
		xstream.alias("setGhostEffectBrick", SetGhostEffectBrick.class);
		xstream.alias("setLookBrick", SetLookBrick.class);
		xstream.alias("setSizeToBrick", SetSizeToBrick.class);
		xstream.alias("setVariableBrick", SetVariableBrick.class);
		xstream.alias("setVolumeToBrick", SetVolumeToBrick.class);
		xstream.alias("setXBrick", SetXBrick.class);
		xstream.alias("setYBrick", SetYBrick.class);
		xstream.alias("showBrick", ShowBrick.class);
		xstream.alias("speakBrick", SpeakBrick.class);
		xstream.alias("stopAllSoundsBrick", StopAllSoundsBrick.class);
		xstream.alias("turnLeftBrick", TurnLeftBrick.class);
		xstream.alias("turnRightBrick", TurnRightBrick.class);
		xstream.alias("waitBrick", WaitBrick.class);
		xstream.alias("whenBrick", WhenBrick.class);
		xstream.alias("whenStartedBrick", WhenStartedBrick.class);
	}

	private void createCatroidRoot() {
		File catroidRoot = new File(DEFAULT_ROOT);
		if (!catroidRoot.exists()) {
			catroidRoot.mkdirs();
		}
	}

	public static StorageHandler getInstance() {
		return INSTANCE;
	}

	public File getBackPackSoundDirectory() {
		return backPackSoundDirectory;
	}

	public Project loadProject(String projectName) {
		loadSaveLock.lock();
		try {
			File projectCodeFile = new File(buildProjectPath(projectName), PROJECTCODE_NAME);
			return (Project) xstream.fromXML(new FileInputStream(projectCodeFile));
		} catch (Exception exception) {
			Log.e(TAG, "Loading project " + projectName + " failed.", exception);
			return null;
		} finally {
			loadSaveLock.unlock();
		}
	}

	public boolean saveProject(Project project) {
		BufferedWriter writer = null;

		loadSaveLock.lock();
		try {

			if (project == null) {
				return false;
			}

			File projectDirectory = new File(buildProjectPath(project.getName()));
			createProjectDataStructure(projectDirectory);

			writer = new BufferedWriter(new FileWriter(new File(projectDirectory, PROJECTCODE_NAME)),
					Constants.BUFFER_8K);
			String projectXml = XML_HEADER.concat(xstream.toXML(project));
			writer.write(projectXml);
			writer.flush();
			return true;
		} catch (Exception exception) {
			Log.e(TAG, "Saving project " + project.getName() + " failed.", exception);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ioException) {
					Log.e(TAG, "Failed closing the buffered writer", ioException);
				}
			}
			loadSaveLock.unlock();
		}
	}

	private void createProjectDataStructure(File projectDirectory) throws IOException {
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
		if (backPackSoundDirectory.listFiles().length > 1) {
			for (File node : backPackSoundDirectory.listFiles()) {
				if (!(node.getName().equals(".nomedia"))) {
					node.delete();
				}
			}
		}
	}

	public boolean deleteProject(Project project) {
		if (project != null) {
			return UtilFile.deleteDirectory(new File(buildProjectPath(project.getName())));
		}
		return false;
	}

	public boolean deleteProject(ProjectData projectData) {
		if (projectData != null) {
			return UtilFile.deleteDirectory(new File(buildProjectPath(projectData.projectName)));
		}
		return false;
	}

	public boolean projectExists(String projectName) {
		List<String> projectNameList = UtilFile.getProjectNames(new File(DEFAULT_ROOT));
		for (String projectNameIterator : projectNameList) {
			if ((projectNameIterator.equals(projectName))) {
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

		return copyFileAddCheckSum(outputFile, inputFile, soundDirectory);
	}

	public File copySoundFileBackPack(SoundInfo selectedSoundInfo) throws IOException, IllegalArgumentException {

		String path = selectedSoundInfo.getAbsolutePath();

		File backPackDirectory = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, BACKPACK_SOUND_DIRECTORY));

		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead()) {
			throw new IllegalArgumentException("file " + path + " doesn`t exist or can`t be read");
		}
		String inputFileChecksum = Utils.md5Checksum(inputFile);

		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();

		File outputFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, BACKPACK_SOUND_DIRECTORY, currentProject
				+ "_" + selectedSoundInfo.getTitle() + "_" + inputFileChecksum));

		return copyFileAddCheckSum(outputFile, inputFile, backPackDirectory);
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
		if (outputFileDirectory.exists() == false) {
			outputFileDirectory.mkdirs();
		}

		Project project = ProjectManager.getInstance().getCurrentProject();
		if ((imageDimensions[0] <= project.getXmlHeader().virtualScreenWidth)
				&& (imageDimensions[1] <= project.getXmlHeader().virtualScreenHeight)) {
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
			return copyFileAddCheckSum(outputFile, inputFile, imageDirectory);
		} else {
			File outputFile = new File(buildPath(imageDirectory.getAbsolutePath(), inputFile.getName()));
			return copyAndResizeImage(outputFile, inputFile, imageDirectory);
		}
	}

	public File makeTempImageCopy(String inputFilePath) throws IOException {
		File tempDirectory = new File(Constants.TMP_PATH);

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		File outputFileDirectory = new File(tempDirectory.getAbsolutePath());
		if (outputFileDirectory.exists() == false) {
			outputFileDirectory.mkdirs();
		}

		File outputFile = new File(Constants.TMP_IMAGE_PATH);

		File copiedFile = UtilFile.copyFile(outputFile, inputFile, tempDirectory);

		return copiedFile;
	}

	public void deletTempImageCopy() {
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
			outputFile.delete();
			return new File(fileChecksumContainer.getPath(checksumCompressedFile));
		}

		File compressedFile = new File(newFilePath);
		outputFile.renameTo(compressedFile);

		return compressedFile;
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
			outputStream.close();
		} catch (IOException e) {

		}
	}

	public void deleteFile(String filepath) {
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		try {
			if (container.decrementUsage(filepath)) {
				File toDelete = new File(filepath);
				toDelete.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//deleteFile(filepath);
		}
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
		return xstream.toXML(project);
	}

	private File copyFileAddCheckSum(File destinationFile, File sourceFile, File directory) throws IOException {
		File copiedFile = UtilFile.copyFile(destinationFile, sourceFile, directory);
		addChecksum(destinationFile, sourceFile);

		return copiedFile;
	}

	private void addChecksum(File destinationFile, File sourceFile) {
		String checksumSource = Utils.md5Checksum(sourceFile);
		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		fileChecksumContainer.addChecksum(checksumSource, destinationFile.getAbsolutePath());
	}

}
