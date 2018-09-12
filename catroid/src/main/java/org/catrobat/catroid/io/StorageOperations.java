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
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.InvalidPathException;

import static org.catrobat.catroid.common.Constants.BUFFER_8K;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.NO_MEDIA_FILE;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

public final class StorageOperations {

	private static final String FILE_NAME_APPENDIX = "_#";

	private StorageOperations() {
		throw new AssertionError();
	}

	public static void createDir(File dir) throws IOException {
		if (dir.isFile()) {
			throw new IOException(dir.getAbsolutePath() + " exists, but is not a directory.");
		}

		if (!dir.exists() && !dir.mkdir()) {
			throw new IOException("Cannot create directory at: " + dir.getAbsolutePath());
		}
	}

	public static void createSceneDirectory(File sceneDir) throws IOException {
		createDir(sceneDir);

		File imageDir = new File(sceneDir, IMAGE_DIRECTORY_NAME);
		createDir(imageDir);

		File noMediaFile = new File(imageDir, NO_MEDIA_FILE);
		noMediaFile.createNewFile();

		File soundDir = new File(sceneDir, SOUND_DIRECTORY_NAME);
		createDir(soundDir);

		noMediaFile = new File(soundDir, NO_MEDIA_FILE);
		noMediaFile.createNewFile();
	}

	public static String getSanitizedFileName(String fileName) {
		int extensionStartIndex = fileName.lastIndexOf('.');
		int appendixStartIndex = fileName.lastIndexOf(FILE_NAME_APPENDIX);

		if (appendixStartIndex == -1) {
			appendixStartIndex = extensionStartIndex;
		}

		if (appendixStartIndex == -1) {
			return fileName;
		}

		return fileName.substring(0, appendixStartIndex);
	}

	public static String resolveFileName(ContentResolver contentResolver, Uri uri) {
		String result = null;

		if (uri.getScheme().equals("content")) {
			Cursor cursor = contentResolver.query(uri, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
			} finally {
				cursor.close();
			}
		}

		if (result == null) {
			result = uri.getPath();
			int cut = result.lastIndexOf('/');
			if (cut != -1) {
				result = result.substring(cut + 1);
			}
		}

		return result;
	}

	public static File duplicateFile(File src) throws IOException {
		return copyFileToDir(src, src.getParentFile());
	}

	public static File copyFileToDir(File srcFile, File dstDir) throws IOException {
		if (!srcFile.exists()) {
			throw new FileNotFoundException(srcFile.getAbsolutePath() + " does not exist.");
		}

		if (!dstDir.exists()) {
			throw new FileNotFoundException(dstDir.getAbsolutePath() + " does not exist.");
		}

		if (!dstDir.isDirectory()) {
			throw new IOException(dstDir.getAbsolutePath() + " is not a directory.");
		}

		File dstFile = getUniqueFile(srcFile.getName(), dstDir);
		transferData(srcFile, dstFile);

		return dstFile;
	}

	public static File copyStreamToFile(InputStream inputStream, File dstFile) throws IOException {
		if (!dstFile.exists() && !dstFile.createNewFile()) {
			throw new IOException("Cannot create file: " + dstFile.getAbsolutePath() + ".");
		}

		return transferData(inputStream, dstFile);
	}

	public static File copyStreamToDir(InputStream inputStream, File dstDir, String fileName) throws
			IOException, InvalidPathException {
		if (!dstDir.exists()) {
			throw new FileNotFoundException("Destination directory: " + dstDir.getAbsolutePath() + " does not exist.");
		}

		if (!dstDir.isDirectory()) {
			throw new IOException(dstDir.getAbsolutePath() + " is not a directory.");
		}

		File dstFile = getUniqueFile(fileName, dstDir);

		if (!dstFile.createNewFile()) {
			throw new IOException("Cannot create file: " + dstFile.getAbsolutePath() + ".");
		}

		return transferData(inputStream, dstFile);
	}

	public static File copyUriToDir(ContentResolver contentResolver, Uri uri, File dstDir, String fileName) throws IOException {
		InputStream inputStream = contentResolver.openInputStream(uri);
		return copyStreamToDir(inputStream, dstDir, fileName);
	}

	public static void transferData(File srcFile, File dstFile) throws IOException {
		FileChannel ic = new FileInputStream(srcFile).getChannel();
		FileChannel oc = new FileOutputStream(dstFile).getChannel();

		try {
			ic.transferTo(0, ic.size(), oc);
		} finally {
			if (ic != null) {
				ic.close();
			}
			oc.close();
		}
	}

	private static File transferData(InputStream inputStream, File dstFile) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(dstFile);

		byte[] b = new byte[BUFFER_8K];
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
			throw new IOException("Cannot create directory: " + dstDir.getAbsolutePath());
		}

		for (File file : srcDir.listFiles()) {
			if (file.isDirectory()) {
				copyDir(file, new File(dstDir, file.getName()));
			} else {
				copyFileToDir(file, dstDir);
			}
		}

		return dstDir;
	}

	public static synchronized File getUniqueFile(String originalName, File dstDir) throws IOException {
		File dstFile = new File(dstDir, originalName);

		if (!dstFile.exists()) {
			return dstFile;
		}

		int extensionStartIndex = originalName.lastIndexOf('.');

		if (extensionStartIndex == -1) {
			extensionStartIndex = originalName.length() - 1;
		}

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

		throw new IOException("Cannot find a unique file name in " + dstDir.getAbsolutePath() + ".");
	}

	public static void deleteFile(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException("File: " + file.getAbsolutePath() + " does not exist.");
		}
		if (!file.delete()) {
			throw new IOException("Cannot delete file: " + file.getAbsolutePath());
		}
	}

	public static void deleteDir(File dir) throws IOException {
		if (!dir.exists()) {
			throw new FileNotFoundException(dir.getAbsolutePath() + " does not exist.");
		}
		if (!dir.isDirectory()) {
			throw new FileNotFoundException(dir.getAbsolutePath() + " is not a directory.");
		}

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				deleteDir(file);
			} else {
				deleteFile(file);
			}
		}

		if (!dir.delete()) {
			throw new IOException("Cannot delete directory: " + dir.getAbsolutePath());
		}
	}
}
