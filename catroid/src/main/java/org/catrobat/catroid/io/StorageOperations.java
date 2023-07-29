/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.InvalidPathException;

import static org.catrobat.catroid.common.Constants.BUFFER_8K;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.NO_MEDIA_FILE;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

public final class StorageOperations {

	public static final String TAG = StorageOperations.class.getSimpleName();

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
			try {
				try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
					if (cursor != null && cursor.moveToFirst()) {
						int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
						if (index >= 0) {
							result = cursor.getString(index);
						}
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Cannot query content resolver for filename.");
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

	public static void writeToFile(File file, String content) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file), BUFFER_8K);
			writer.write(content);
			writer.flush();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					Log.e(TAG, "Cannot close buffered writer after exception occurred during writing process.");
				}
			}
		}
	}

	public static File compressBitmapToPng(Bitmap bitmap, File destinationFile) throws IOException {
		try (FileOutputStream os = new FileOutputStream(destinationFile)) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		}
		return destinationFile;
	}

	public static File duplicateFile(File source) throws IOException {
		return copyFileToDir(source, source.getParentFile());
	}

	public static File copyFile(File sourceFile, File destinationFile) throws IOException {
		if (!sourceFile.exists()) {
			throw new FileNotFoundException(sourceFile.getAbsolutePath() + " does not exist.");
		}
		transferData(sourceFile, destinationFile);
		return destinationFile;
	}

	public static File copyFileToDir(File sourceFile, File destinationDir) throws IOException {
		if (!sourceFile.exists()) {
			throw new FileNotFoundException(sourceFile.getAbsolutePath() + " does not exist.");
		}

		if (!destinationDir.exists()) {
			throw new FileNotFoundException(destinationDir.getAbsolutePath() + " does not exist.");
		}

		if (!destinationDir.isDirectory()) {
			throw new IOException(destinationDir.getAbsolutePath() + " is not a directory.");
		}

		File destinationFile = getUniqueFile(sourceFile.getName(), destinationDir);
		transferData(sourceFile, destinationFile);

		return destinationFile;
	}

	public static File copyStreamToFile(InputStream inputStream, File destinationFile) throws IOException {
		if (!destinationFile.exists() && !destinationFile.createNewFile()) {
			throw new IOException("Cannot create file: " + destinationFile.getAbsolutePath() + ".");
		}

		return transferData(inputStream, destinationFile);
	}

	private static File getDestinationFile(File destinationDir, String fileName) throws
			IOException, InvalidPathException {
		if (!destinationDir.exists()) {
			throw new FileNotFoundException("Destination directory: " + destinationDir.getAbsolutePath() + " does not exist.");
		}

		if (!destinationDir.isDirectory()) {
			throw new IOException(destinationDir.getAbsolutePath() + " is not a directory.");
		}

		File destinationFile = getUniqueFile(fileName, destinationDir);

		if (!destinationFile.createNewFile()) {
			throw new IOException("Cannot create file: " + destinationFile.getAbsolutePath() + ".");
		}

		return destinationFile;
	}

	public static File copyStreamToDir(InputStream inputStream, File destinationDir, String fileName) throws
			IOException, InvalidPathException {
		File destinationFile = getDestinationFile(destinationDir, fileName);
		return transferData(inputStream, destinationFile);
	}

	public static File compressAndCopyStreamToDir(InputStream inputStream, File destinationDir, String fileName) throws
			IOException, InvalidPathException {
		File destinationFile = getDestinationFile(destinationDir, fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
		BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
		inputStream.close();
		fileOutputStream.close();
		return destinationFile;
	}

	public static File copyUriToDir(ContentResolver contentResolver, Uri uri, File destinationDir, String fileName) throws IOException {
		InputStream inputStream = contentResolver.openInputStream(uri);
		if ("image/webp".equals(contentResolver.getType(uri))) {
			return compressAndCopyStreamToDir(inputStream, destinationDir, fileName);
		}
		return copyStreamToDir(inputStream, destinationDir, fileName);
	}

	public static void copyFileContentToUri(ContentResolver contentResolver, Uri uri, File sourceFile) throws IOException {
		byte[] b = new byte[BUFFER_8K];
		int len;
		try (FileInputStream inputStream = new FileInputStream(sourceFile);
				OutputStream outputStream = contentResolver.openOutputStream(uri)) {
			while ((len = inputStream.read(b)) != -1) {
				outputStream.write(b, 0, len);
			}
		}
	}

	public static void transferData(File sourceFile, File destinationFile) throws IOException {
		try (FileChannel ic = new FileInputStream(sourceFile).getChannel(); FileChannel oc = new FileOutputStream(destinationFile).getChannel()) {
			ic.transferTo(0, ic.size(), oc);
		}
	}

	private static File transferData(InputStream inputStream, File destinationFile) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);

		byte[] b = new byte[BUFFER_8K];
		int len;

		try {
			while ((len = inputStream.read(b)) != -1) {
				fileOutputStream.write(b, 0, len);
			}
			return destinationFile;
		} finally {
			inputStream.close();
			fileOutputStream.close();
		}
	}

	public static File copyDir(File sourceDir, File destinationDir) throws IOException {
		if (!sourceDir.exists()) {
			throw new FileNotFoundException("Directory: " + sourceDir.getAbsolutePath() + " does not exist.");
		}

		if (!sourceDir.isDirectory()) {
			throw new IOException(sourceDir.getAbsolutePath() + " is not a directory.");
		}

		destinationDir.mkdir();

		if (!destinationDir.isDirectory()) {
			throw new IOException("Cannot create directory: " + destinationDir.getAbsolutePath());
		}

		File[] files = sourceDir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					copyDir(file, new File(destinationDir, file.getName()));
				} else {
					copyFileToDir(file, destinationDir);
				}
			}
		}

		return destinationDir;
	}

	public static synchronized File getUniqueFile(String originalName, File destinationDir) throws IOException {
		File destinationFile = new File(destinationDir, originalName);

		if (!destinationFile.exists()) {
			return destinationFile;
		}

		int extensionStartIndex = originalName.lastIndexOf('.');

		if (extensionStartIndex == -1) {
			extensionStartIndex = originalName.length();
		}

		int appendixStartIndex = originalName.lastIndexOf(FILE_NAME_APPENDIX);

		if (appendixStartIndex == -1) {
			appendixStartIndex = extensionStartIndex;
		}

		String extension = originalName.substring(extensionStartIndex);
		String fileName = originalName.substring(0, appendixStartIndex);

		int appendix = 0;

		while (appendix < Integer.MAX_VALUE) {
			String destinationFileName = fileName + FILE_NAME_APPENDIX + appendix + extension;
			destinationFile = new File(destinationDir, destinationFileName);

			if (!destinationFile.exists()) {
				return destinationFile;
			}

			appendix++;
		}

		throw new IOException("Cannot find a unique file name in " + destinationDir.getAbsolutePath() + ".");
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

		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDir(file);
				} else {
					deleteFile(file);
				}
			}
		}
		if (!dir.delete()) {
			throw new IOException("Cannot delete directory: " + dir.getAbsolutePath());
		}
	}
}
