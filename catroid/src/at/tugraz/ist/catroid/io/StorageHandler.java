/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

public class StorageHandler {

	private static final int JPG_COMPRESSION_SETTING = 95;
	private static final String TAG = StorageHandler.class.getSimpleName();
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";
	private static StorageHandler instance;
	private XStream xstream;

	private StorageHandler() throws IOException {

		xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new CatroidFieldKeySorter())));
		xstream.processAnnotations(Project.class);
		xstream.aliasPackage("Bricks", "at.tugraz.ist.catroid.content.bricks");
		xstream.aliasPackage("Common", "at.tugraz.ist.catroid.common");
		xstream.aliasPackage("Content", "at.tugraz.ist.catroid.content");

		if (!Utils.hasSdCard()) {
			throw new IOException("Could not read external storage");
		}
		createCatroidRoot();
	}

	private void createCatroidRoot() {
		File catroidRoot = new File(Consts.DEFAULT_ROOT);
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
			if (NativeAppActivity.isRunning()) {
				InputStream spfFileStream = NativeAppActivity.getContext().getAssets().open(projectName);
				return (Project) xstream.fromXML(spfFileStream);
			}

			File projectDirectory = new File(Utils.buildProjectPath(projectName));

			if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Consts.PROJECTCODE_NAME));
				return (Project) xstream.fromXML(projectFileStream);
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean saveProject(Project project) {
		createCatroidRoot();
		if (project == null) {
			return false;
		}
		try {
			String projectFile = xstream.toXML(project);

			String projectDirectoryName = Utils.buildProjectPath(project.getName());
			File projectDirectory = new File(projectDirectoryName);

			if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
				projectDirectory.mkdir();

				File imageDirectory = new File(Utils.buildPath(projectDirectoryName, Consts.IMAGE_DIRECTORY));
				imageDirectory.mkdir();

				File noMediaFile = new File(Utils.buildPath(projectDirectoryName, Consts.IMAGE_DIRECTORY,
						Consts.NO_MEDIA_FILE));
				noMediaFile.createNewFile();

				File soundDirectory = new File(projectDirectoryName + "/" + Consts.SOUND_DIRECTORY);
				soundDirectory.mkdir();

				noMediaFile = new File(Utils.buildPath(projectDirectoryName, Consts.SOUND_DIRECTORY,
						Consts.NO_MEDIA_FILE));
				noMediaFile.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(Utils.buildPath(projectDirectoryName,
					Consts.PROJECTCODE_NAME)), Consts.BUFFER_8K);

			writer.write(XML_HEADER.concat(projectFile));
			writer.flush();
			writer.close();

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

	public boolean projectExists(String projectName) {
		File projectDirectory = new File(Utils.buildProjectPath(projectName));
		if (!projectDirectory.exists()) {
			return false;
		}
		return true;
	}

	public File copySoundFile(String path) throws IOException {
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File soundDirectory = new File(Utils.buildPath(Utils.buildProjectPath(currentProject), Consts.SOUND_DIRECTORY));

		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}
		String inputFileChecksum = Utils.md5Checksum(inputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;
		if (fileChecksumContainer.containsChecksum(inputFileChecksum)) {
			fileChecksumContainer.addChecksum(inputFileChecksum, null);
			return new File(fileChecksumContainer.getPath(inputFileChecksum));
		}
		File outputFile = new File(Utils.buildPath(soundDirectory.getAbsolutePath(), inputFileChecksum + "_"
				+ inputFile.getName()));

		return copyFile(outputFile, inputFile, soundDirectory);
	}

	public File copyImage(String currentProjectName, String inputFilePath, String newName) throws IOException {
		String newFilePath;
		File imageDirectory = new File(Utils.buildPath(Utils.buildProjectPath(currentProjectName),
				Consts.IMAGE_DIRECTORY));

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		int[] imageDimensions = new int[2];
		imageDimensions = ImageEditing.getImageDimensions(inputFilePath);
		FileChecksumContainer checksumCont = ProjectManager.getInstance().fileChecksumContainer;

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
			return copyFile(outputFile, inputFile, imageDirectory);
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

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;
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

	private File copyFile(File destinationFile, File sourceFile, File directory) throws IOException {
		FileChannel inputChannel = new FileInputStream(sourceFile).getChannel();
		FileChannel outputChannel = new FileOutputStream(destinationFile).getChannel();

		String checksumSource = Utils.md5Checksum(sourceFile);
		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;

		try {
			inputChannel.transferTo(0, inputChannel.size(), outputChannel);
			fileChecksumContainer.addChecksum(checksumSource, destinationFile.getAbsolutePath());
			return destinationFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (inputChannel != null) {
				inputChannel.close();
			}
			if (outputChannel != null) {
				outputChannel.close();
			}
		}
	}

	public void deleteFile(String filepath) {
		FileChecksumContainer container = ProjectManager.getInstance().fileChecksumContainer;
		try {
			if (container.decrementUsage(filepath)) {
				File toDelete = new File(filepath);
				toDelete.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
