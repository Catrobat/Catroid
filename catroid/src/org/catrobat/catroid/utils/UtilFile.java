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
package org.catrobat.catroid.utils;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.soundrecorder.SoundRecorder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UtilFile {
	private static final String TAG = UtilFile.class.getSimpleName();

	// Suppress default constructor for noninstantiability
	private UtilFile() {
		throw new AssertionError();
	}

	private static long getSizeOfFileOrDirectoryInByte(File fileOrDirectory) {
		if (!fileOrDirectory.exists()) {
			return 0;
		}
		if (fileOrDirectory.isFile()) {
			return fileOrDirectory.length();
		}

		File[] contents = fileOrDirectory.listFiles();
		if (contents == null) {
			return 0;
		}

		long size = 0;
		for (File file : contents) {
			size += file.isDirectory() ? getSizeOfFileOrDirectoryInByte(file) : file.length();
		}
		return size;
	}

	public static Long getProgressFromBytes(String projectName, Long progress) {
		Long fileByteSize = getSizeOfFileOrDirectoryInByte(new File(Utils.buildProjectPath(projectName)));
		if (fileByteSize == 0) {
			return (long) 0;
		}
		Long progressValue = progress * 100 / fileByteSize;
		return progressValue;
	}

	public static String getSizeAsString(File fileOrDirectory) {
		final int unit = 1024;
		long bytes = UtilFile.getSizeOfFileOrDirectoryInByte(fileOrDirectory);

		if (bytes < unit) {
			return bytes + " Byte";
		}

		/*
		 * Logarithm of "bytes" to base "unit"
		 * log(a) / log(b) == logarithm of a to the base of b
		 */
		int exponent = (int) (Math.log(bytes) / Math.log(unit));
		char prefix = "KMGTPE".charAt(exponent - 1);

		return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exponent), prefix);
	}

	public static boolean deleteDirectory(File fileOrDirectory) {
		return deleteDirectory(fileOrDirectory, 0);
	}

	private static boolean deleteDirectory(File fileOrDirectory, int space) {
		if (fileOrDirectory == null) {
			return false;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < space; i++) {
			sb.append('-');
		}

		boolean success = true;
		if (fileOrDirectory.exists() && fileOrDirectory.isDirectory()) {
			// Please note: especially with MyProjectsActivityTest.testAddNewProjectMixedCase(), it happens that listFiles
			// returns null (although fileOrDirectory exists and is a directory). This should definitely not happen
			// and there is probably an I/O Error from the system. So we just check this manually and abort if so.
			File[] files = fileOrDirectory.listFiles();
			if (files == null) {
				return false;
			}

			for (File child : files) {
				success = deleteDirectory(child, space + 1);
				if (!success) {
					return false;
				}
			}
		}

		Log.v(TAG, sb.toString() + "delete: " + fileOrDirectory.getName());

		//http://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy
		final File renameBeforeDelete = new File(fileOrDirectory.getAbsolutePath() + System.currentTimeMillis());
		fileOrDirectory.renameTo(renameBeforeDelete);
		return renameBeforeDelete.delete();
	}

	public static File saveFileToProject(String project, String name, int fileID, Context context, FileType type) {

		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Utils.buildProjectPath(name);
		} else {
			switch (type) {
				case TYPE_IMAGE_FILE:
					filePath = Utils.buildPath(Utils.buildProjectPath(project), Constants.IMAGE_DIRECTORY, name);
					break;
				case TYPE_SOUND_FILE:
					filePath = Utils.buildPath(Utils.buildProjectPath(project), Constants.SOUND_DIRECTORY, name);
					break;
				default:
					filePath = Utils.buildProjectPath(name);
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID),
				Constants.BUFFER_8K);

		try {
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			file.createNewFile();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Constants.BUFFER_8K);
			byte[] buffer = new byte[Constants.BUFFER_8K];
			int length = 0;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			out.close();

			return file;
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			return null;
		}
	}

	public static void createStandardProjectIfRootDirectoryIsEmpty(Context context) {
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		if (rootDirectory == null || rootDirectory.listFiles() == null || getProjectNames(rootDirectory).size() == 0) {
			ProjectManager.getInstance().initializeDefaultProject(context);
		}
	}

	public static void loadExistingOrCreateStandardDroneProject(Context context) {
		String droneStandardProjectName = context.getString(R.string.default_drone_project_name);
		try {
			ProjectManager.getInstance().loadProject(droneStandardProjectName, context);
		} catch (ProjectException cannotLoadDroneProjectException) {
			Log.e(TAG, "Cannot load standard drone project" + cannotLoadDroneProjectException);
		}

		String currentName = ProjectManager.getInstance().getCurrentProject().getName();
		if (!currentName.equals(droneStandardProjectName)) {
			ProjectManager.getInstance().initializeDroneProject(context);
		}
	}

	/**
	 * returns a list of strings of all projectnames in the catroid folder
	 */
	public static List<String> getProjectNames(File directory) {
		List<String> projectList = new ArrayList<String>();
		File[] fileList = directory.listFiles();
		if (fileList != null) {
			FilenameFilter filenameFilter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.contentEquals(Constants.PROJECTCODE_NAME);
				}
			};
			for (File file : fileList) {
				if (file.isDirectory() && file.list(filenameFilter).length != 0) {
					projectList.add(decodeSpecialCharsForFileSystem(file.getName()));
				}
			}
		}
		return projectList;
	}

	public static File copyFile(File destinationFile, File sourceFile) throws IOException {
		FileInputStream inputStream = null;
		FileChannel inputChannel = null;
		FileOutputStream outputStream = null;
		FileChannel outputChannel = null;
		try {
			inputStream = new FileInputStream(sourceFile);
			inputChannel = inputStream.getChannel();
			outputStream = new FileOutputStream(destinationFile);
			outputChannel = outputStream.getChannel();
			inputChannel.transferTo(0, inputChannel.size(), outputChannel);
			return destinationFile;
		} catch (IOException exception) {
			throw exception;
		} finally {
			if (inputChannel != null) {
				inputChannel.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputChannel != null) {
				outputChannel.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static File copyFromResourceIntoProject(String projectName, String directoryInProject,
			String outputFilename, int resourceId, Context context, boolean prependMd5ToFilename) throws IOException {
		String directoryPath = Utils.buildPath(Utils.buildProjectPath(projectName), directoryInProject);
		File copiedFile = new File(directoryPath, outputFilename);
		if (!copiedFile.exists()) {
			copiedFile.createNewFile();
		} else {
			throw new IllegalArgumentException("file " + copiedFile.getAbsolutePath() + " already exists!");
		}
		InputStream in = context.getResources().openRawResource(resourceId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(copiedFile), Constants.BUFFER_8K);
		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		if (!prependMd5ToFilename) {
			return copiedFile;
		}

		return prependMd5ToFilename(copiedFile);
	}

	public static File copySoundFromResourceIntoProject(String projectName, String outputFilename, int resourceId,
			Context context, boolean prependMd5ToFilename) throws IllegalArgumentException, IOException {
		if (!outputFilename.toLowerCase(Locale.US).endsWith(SoundRecorder.RECORDING_EXTENSION)) {
			throw new IllegalArgumentException("Only Files with extension " + SoundRecorder.RECORDING_EXTENSION
					+ " allowed");
		}
		return copyFromResourceIntoProject(projectName, Constants.SOUND_DIRECTORY, outputFilename, resourceId, context,
				prependMd5ToFilename);
	}

	public static File copyImageFromResourceIntoProject(String projectName, String outputFilename, int resourceId,
			Context context, boolean prependMd5ToFilename, double scaleFactor) throws IOException {
		if (scaleFactor <= 0) {
			throw new IllegalArgumentException("scale factor is smaller or equal zero");
		}
		outputFilename = UtilFile.encodeSpecialCharsForFileSystem(outputFilename);
		if (!outputFilename.toLowerCase(Locale.US).endsWith(Constants.IMAGE_STANDARD_EXTENTION)) {
			outputFilename = outputFilename + Constants.IMAGE_STANDARD_EXTENTION;
		}
		File copiedFile = copyFromResourceIntoProject(projectName, Constants.IMAGE_DIRECTORY, outputFilename,
				resourceId, context, false);

		ImageEditing.scaleImageFile(copiedFile, scaleFactor);

		if (!prependMd5ToFilename) {
			return copiedFile;
		}

		return prependMd5ToFilename(copiedFile);
	}

	private static File prependMd5ToFilename(File file) throws IOException {
		File fileWithMd5 = new File(file.getParent(), Utils.md5Checksum(file) + Constants.FILENAME_SEPARATOR
				+ file.getName());
		if (!file.renameTo(fileWithMd5)) {
			throw new IOException("renaming file " + file.getAbsoluteFile() + " to " + fileWithMd5.getAbsoluteFile()
					+ " failed");
		}
		return fileWithMd5;
	}

	public static String encodeSpecialCharsForFileSystem(String projectName) {
		if (projectName.equals(".") || projectName.equals("..")) {
			projectName = projectName.replace(".", "%2E");
		} else {
			projectName = projectName.replace("%", "%25");
			projectName = projectName.replace("\"", "%22");
			projectName = projectName.replace("/", "%2F");
			projectName = projectName.replace(":", "%3A");
			projectName = projectName.replace("<", "%3C");
			projectName = projectName.replace(">", "%3E");
			projectName = projectName.replace("?", "%3F");
			projectName = projectName.replace("\\", "%5C");
			projectName = projectName.replace("|", "%7C");
			projectName = projectName.replace("*", "%2A");
		}
		return projectName;
	}

	public static String decodeSpecialCharsForFileSystem(String projectName) {
		projectName = projectName.replace("%2E", ".");

		projectName = projectName.replace("%2A", "*");
		projectName = projectName.replace("%7C", "|");
		projectName = projectName.replace("%5C", "\\");
		projectName = projectName.replace("%3F", "?");
		projectName = projectName.replace("%3E", ">");
		projectName = projectName.replace("%3C", "<");
		projectName = projectName.replace("%3A", ":");
		projectName = projectName.replace("%2F", "/");
		projectName = projectName.replace("%22", "\"");
		projectName = projectName.replace("%25", "%");
		return projectName;
	}

	public enum FileType {
		TYPE_IMAGE_FILE, TYPE_SOUND_FILE
	}
}
