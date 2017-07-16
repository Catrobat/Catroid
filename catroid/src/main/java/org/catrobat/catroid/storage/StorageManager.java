/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.catrobat.catroid.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public final class StorageManager {

	public static final String TAG = StorageManager.class.getSimpleName();

	private static final String FILE_NAME_APPENDIX = "_#";

	private StorageManager() {
	}

	public static DirectoryPathInfo getRootDirectory() {
		File root = new File(Constants.DEFAULT_ROOT);
		root.mkdir();
		return new DirectoryPathInfo(null, Constants.DEFAULT_ROOT);
	}

	public static DirectoryPathInfo getProjectsDirectory() {
		File root = new File(getRootDirectory().getAbsolutePath());
		File projectsDir = new File(root, Constants.PROJECTS_DIRECTORY);
		projectsDir.mkdir();
		return new DirectoryPathInfo(getRootDirectory(), Constants.PROJECTS_DIRECTORY);
	}

	public static List<String> getProjectNames() {
		List<String> projects = new ArrayList<>();
		File projectsDir = new File(getProjectsDirectory().getAbsolutePath());

		for (File file : projectsDir.listFiles()) {
			projects.add(file.getName());
		}

		return projects;
	}

	public static DirectoryPathInfo mkDir(DirectoryPathInfo parentDirectoryInfo, String name) throws IOException {
		File directory = new File(parentDirectoryInfo.getAbsolutePath(), name);

		if (!directory.mkdir()) {
			throw new IOException("Directory could NOT be created! " + directory.getAbsolutePath());
		}

		return new DirectoryPathInfo(parentDirectoryInfo, name);
	}

	public static DirectoryPathInfo getDirectory(DirectoryPathInfo parent, String name) throws IOException {
		File directory = new File(parent.getAbsolutePath() + "/" + name);

		if (directory.exists()) {
			return new DirectoryPathInfo(parent, name);
		}

		return mkDir(parent, name);
	}

	public static FilePathInfo copyFile(FilePathInfo srcPathInfo) throws Exception {
		String srcPath = srcPathInfo.getAbsolutePath();
		File dstFile = copyFile(srcPath);

		return new FilePathInfo(srcPathInfo.getParent(), dstFile.getName());
	}

	public static FilePathInfo copyFile(FilePathInfo srcPathInfo, DirectoryPathInfo dstDirectoryInfo) throws IOException {
		String srcPath = srcPathInfo.getAbsolutePath();
		String dstPath = dstDirectoryInfo.getAbsolutePath();
		File dstFile = copyFile(srcPath, dstPath);

		return new FilePathInfo(dstDirectoryInfo, dstFile.getName());
	}

	private static File copyFile(String srcPath) throws IOException {
		String dstPath = new File(srcPath).getParent();
		return copyFile(srcPath, dstPath);
	}

	private static File copyFile(String srcPath, String dstPath) throws IOException {
		File srcFile = new File(srcPath);
		if (!srcFile.exists()) {
			throw new FileNotFoundException("File: " + srcPath + "does not exist.");
		}

		File dstFile = getUniqueFile(srcFile.getName(), dstPath);
		copyFile(srcFile, dstFile);

		return dstFile;
	}

	private static synchronized File getUniqueFile(String originalName, String dstDirectory)
			throws IOException {
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
			File dstFile = new File(dstDirectory, dstFileName);

			if (!dstFile.exists()) {
				return dstFile;
			}

			appendix++;
		}

		throw new IOException("Could not find a unique file name in " + dstDirectory + ".");
	}

	private static void copyFile(File srcFile, File dstFile) throws IOException {
		FileChannel ic = new FileInputStream(srcFile).getChannel();
		FileChannel oc = new FileOutputStream(dstFile).getChannel();

		try {
			ic.transferTo(0, ic.size(), oc);
		} finally {
			if (ic != null) {
				ic.close();
			}
			if (oc != null) {
				oc.close();
			}
		}
	}

	public static void deleteFile(PathInfo srcPathInfo) throws IOException {
		deleteFile(srcPathInfo.getAbsolutePath());
	}

	private static void deleteFile(String srcPath) throws IOException {
		File file = new File(srcPath);
		if (!file.exists()) {
			throw new FileNotFoundException("File: " + srcPath + "does not exist.");
		}
		if (!file.delete()) {
			throw new IOException("File: " + srcPath + " could not be deleted.");
		}
	}

	public static void clearDirectory(DirectoryPathInfo directory) throws IOException {
		File dir = new File(directory.getAbsolutePath());

		if (!dir.exists()) {
			throw new FileNotFoundException("Directory: " + dir.getName() + "does not exist");
		}

		if (!dir.isDirectory()) {
			throw new IOException(dir.getName() + " is not a directory");
		}

		for (File file : dir.listFiles()) {
			deleteFile(file.getAbsolutePath());
		}
	}

	public static FilePathInfo saveDrawableToSDCard(int resourceId, DirectoryPathInfo dstDirectory, Context context)
			throws IOException {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
		return saveBitmapToSDCard(bitmap, dstDirectory);
	}

	public static FilePathInfo createEmptyPngOnSDCard(int width, int height, DirectoryPathInfo dstDirectory) throws
			IOException {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		return saveBitmapToSDCard(bitmap, dstDirectory);
	}

	private static FilePathInfo saveBitmapToSDCard(Bitmap bitmap, DirectoryPathInfo dstDirectory) throws IOException {
		File file = getUniqueFile("img" + Constants.IMAGE_STANDARD_EXTENSION, dstDirectory.getAbsolutePath());
		FileOutputStream os = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
		os.flush();
		os.close();

		return new FilePathInfo(dstDirectory, file.getName());
	}

	public static FilePathInfo saveSoundResourceToSDCard(int resourceId, DirectoryPathInfo dstDirectory, Context
			context) throws IOException {
		InputStream is = context.getResources().openRawResource(resourceId);

		File file = getUniqueFile("snd" + Constants.SOUND_STANDARD_EXTENSION, dstDirectory.getAbsolutePath());
		FileOutputStream os = new FileOutputStream(file);

		byte[] buffer = new byte[Constants.BUFFER_8K];

		int byteCount;
		while ((byteCount = is.read(buffer)) > 0) {
			os.write(buffer, 0, byteCount);
		}

		is.close();
		os.flush();
		os.close();

		return new FilePathInfo(dstDirectory, file.getName());
	}
}
