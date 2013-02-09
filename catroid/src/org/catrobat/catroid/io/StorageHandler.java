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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.fragment.ProjectsListFragment.ProjectData;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.xml.parser.FullParser;
import org.catrobat.catroid.xml.serializer.XmlSerializer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class StorageHandler {

	private static final int JPG_COMPRESSION_SETTING = 95;
	private static final String TAG = StorageHandler.class.getSimpleName();
	private static StorageHandler instance;

	private StorageHandler() throws IOException {
		if (!Utils.externalStorageAvailable()) {
			throw new IOException("Could not read external storage");
		}
		createCatroidRoot();
	}

	private void createCatroidRoot() {
		File catroidRoot = new File(Constants.DEFAULT_ROOT);
		if (!catroidRoot.exists()) {
			catroidRoot.mkdirs();
		}
	}

	public synchronized static StorageHandler getInstance() {
		if (instance == null) {
			try {
				instance = new StorageHandler();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Exception in Storagehandler, please refer to the StackTrace");
			}
		}
		return instance;
	}

	public Project loadProject(String projectName) {
		createCatroidRoot();
		try {
			File projectDirectory = new File(Utils.buildProjectPath(projectName));

			if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Constants.PROJECTCODE_NAME));
				Project returned = FullParser.parseSpritesWithProject(projectFileStream);
				return returned;
			} else {
				return null;
			}

		} catch (Exception e) {
			Log.e("CATROID", "Cannot load project.", e);
			return null;
		}
	}

	public boolean saveProject(Project project) {
		createCatroidRoot();
		if (project == null) {
			return false;
		}

		try {
			String projectDirectoryName = Utils.buildProjectPath(project.getName());
			File projectDirectory = new File(projectDirectoryName);

			if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
				projectDirectory.mkdir();

				File imageDirectory = new File(Utils.buildPath(projectDirectoryName, Constants.IMAGE_DIRECTORY));
				imageDirectory.mkdir();

				File noMediaFile = new File(Utils.buildPath(projectDirectoryName, Constants.IMAGE_DIRECTORY,
						Constants.NO_MEDIA_FILE));
				noMediaFile.createNewFile();

				File soundDirectory = new File(projectDirectoryName + "/" + Constants.SOUND_DIRECTORY);
				soundDirectory.mkdir();

				noMediaFile = new File(Utils.buildPath(projectDirectoryName, Constants.SOUND_DIRECTORY,
						Constants.NO_MEDIA_FILE));
				noMediaFile.createNewFile();
			}

			XmlSerializer.toXml(project, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "saveProject threw an exception and failed.");
			return false;
		}
	}

	public boolean deleteProject(Project project) {
		if (project != null) {
			return UtilFile.deleteDirectory(new File(Utils.buildProjectPath(project.getName())));
		}
		return false;
	}

	public boolean deleteProject(ProjectData projectData) {
		if (projectData != null) {
			return UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectData.projectName)));
		}
		return false;
	}

	public boolean projectExistsCheckCase(String projectName) {
		List<String> projectNameList = UtilFile.getProjectNames(new File(Constants.DEFAULT_ROOT));
		for (String projectNameIterator : projectNameList) {
			if ((projectNameIterator.equals(projectName))) {
				return true;
			}
		}
		return false;
	}

	public boolean projectExistsIgnoreCase(String projectName) {
		File projectDirectory = new File(Utils.buildProjectPath(projectName));
		if (!projectDirectory.exists()) {
			return false;
		}
		return true;
	}

	public File copySoundFile(String path) throws IOException {
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File soundDirectory = new File(Utils.buildPath(Utils.buildProjectPath(currentProject),
				Constants.SOUND_DIRECTORY));

		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}
		String inputFileChecksum = Utils.md5Checksum(inputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		if (fileChecksumContainer.containsChecksum(inputFileChecksum)) {
			fileChecksumContainer.addChecksum(inputFileChecksum, null);
			return new File(fileChecksumContainer.getPath(inputFileChecksum));
		}
		File outputFile = new File(Utils.buildPath(soundDirectory.getAbsolutePath(), inputFileChecksum + "_"
				+ inputFile.getName()));

		return copyFileAddCheckSum(outputFile, inputFile, soundDirectory);
	}

	public File copyImage(String currentProjectName, String inputFilePath, String newName) throws IOException {
		String newFilePath;
		File imageDirectory = new File(Utils.buildPath(Utils.buildProjectPath(currentProjectName),
				Constants.IMAGE_DIRECTORY));

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		int[] imageDimensions = new int[2];
		imageDimensions = ImageEditing.getImageDimensions(inputFilePath);
		FileChecksumContainer checksumCont = ProjectManager.getInstance().getFileChecksumContainer();

		Project project = ProjectManager.getInstance().getCurrentProject();
		if ((imageDimensions[0] <= project.virtualScreenWidth) && (imageDimensions[1] <= project.virtualScreenHeight)) {
			String checksumSource = Utils.md5Checksum(inputFile);

			if (newName != null) {
				newFilePath = Utils.buildPath(imageDirectory.getAbsolutePath(), checksumSource + "_" + newName);
			} else {
				newFilePath = Utils.buildPath(imageDirectory.getAbsolutePath(),
						checksumSource + "_" + inputFile.getName());
				if (checksumCont.containsChecksum(checksumSource)) {
					checksumCont.addChecksum(checksumSource, newFilePath);
					return new File(checksumCont.getPath(checksumSource));
				}
			}
			File outputFile = new File(newFilePath);
			return copyFileAddCheckSum(outputFile, inputFile, imageDirectory);
		} else {
			File outputFile = new File(Utils.buildPath(imageDirectory.getAbsolutePath(), inputFile.getName()));
			return copyAndResizeImage(outputFile, inputFile, imageDirectory);
		}
	}

	private File copyAndResizeImage(File outputFile, File inputFile, File imageDirectory) throws IOException {
		Project project = ProjectManager.getInstance().getCurrentProject();
		Bitmap bitmap = ImageEditing.getScaledBitmapFromPath(inputFile.getAbsolutePath(), project.virtualScreenWidth,
				project.virtualScreenHeight, true);

		saveBitmapToImageFile(outputFile, bitmap);

		String checksumCompressedFile = Utils.md5Checksum(outputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		String newFilePath = Utils.buildPath(imageDirectory.getAbsolutePath(),
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
			if (outputFile.getName().endsWith(".jpg") || outputFile.getName().endsWith(".jpeg")
					|| outputFile.getName().endsWith(".JPG") || outputFile.getName().endsWith(".JPEG")) {
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

		Project newProject = ProjectManager.INSTANCE.getCurrentProject();
		List<Sprite> currentSpriteList = newProject.getSpriteList();

		for (Sprite currentSprite : currentSpriteList) {
			for (SoundInfo soundInfo : currentSprite.getSoundList()) {
				container.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
			}

			for (CostumeData costumeData : currentSprite.getCostumeDataList()) {
				container.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
			}
		}
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
