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
package org.catrobat.catroid.utils;

import android.util.Log;

import org.catrobat.catroid.common.Constants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class UtilZip {
	private static final int QUICKEST_COMPRESSION = 0;
    private static final String TAG = UtilZip.class.getSimpleName();

	private static ZipOutputStream zipOutputStream;

	// Suppress default constructor for noninstantiability
	private UtilZip() {
		throw new AssertionError();
	}

	public static boolean writeToZipFile(String[] filePaths, String zipFile) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			zipOutputStream = new ZipOutputStream(fileOutputStream);
			zipOutputStream.setLevel(QUICKEST_COMPRESSION);

			for (String filePath : filePaths) {
				File file = new File(filePath);
				if (file.isDirectory()) {
					writeDirToZip(file, file.getName() + "/");
				} else {
					writeFileToZip(file, "");
				}
			}

			return true;
		} catch (IOException ioException) {
            Log.e(TAG, ioException.getMessage(), ioException);
		} finally {
			try {
				if (zipOutputStream != null) {
					zipOutputStream.close();
				}
			} catch (IOException ignoredException) {
                Log.e(TAG, "Error closing stream", ignoredException);
			}
		}

		return false;
	}

	private static void writeDirToZip(File dir, String zipEntryPath) throws IOException {
		for (String dirListEntry : dir.list()) {
			File file = new File(dir, dirListEntry);
			if (file.isDirectory()) {
				writeDirToZip(file, zipEntryPath + file.getName() + "/");
				continue;
			}
			writeFileToZip(file, zipEntryPath);
		}
	}

	private static void writeFileToZip(File file, String zipEntryPath) throws IOException {
		byte[] readBuffer = new byte[Constants.BUFFER_8K];
		int bytesIn = 0;

		FileInputStream fileInputStream = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(zipEntryPath + file.getName());
		zipOutputStream.putNextEntry(zipEntry);

		while ((bytesIn = fileInputStream.read(readBuffer)) != -1) {
			zipOutputStream.write(readBuffer, 0, bytesIn);
		}
		zipOutputStream.closeEntry();
		fileInputStream.close();
	}

	public static boolean unZipFile(String zipFileName, String outDirectory) {
		ZipFile zipFile = null;
		BufferedOutputStream destinationOutputStream = null;
		InputStream zipInputStream = null;
		try {
			byte[] data = new byte[Constants.BUFFER_8K];
			zipFile = new ZipFile(zipFileName);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {

				ZipEntry zipEntry = e.nextElement();
				zipInputStream = zipFile.getInputStream(zipEntry);

				if (zipEntry.isDirectory()) {
					File file = new File(Utils.buildPath(outDirectory, zipEntry.getName()));
					file.mkdir();
					zipInputStream.close();
					continue;
				}
				File file = new File(Utils.buildPath(outDirectory, zipEntry.getName()));
				file.getParentFile().mkdirs();
				FileOutputStream fileOutputStream = new FileOutputStream(file);

				int count;
				destinationOutputStream = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
				while ((count = zipInputStream.read(data, 0, Constants.BUFFER_8K)) != -1) {
					destinationOutputStream.write(data, 0, count);
				}
				destinationOutputStream.flush();
			}

			return true;
		} catch (FileNotFoundException fileNotFoundException) {
            Log.e(TAG, fileNotFoundException.getMessage(), fileNotFoundException);
		} catch (IOException ioException) {
            Log.e(TAG, ioException.getMessage(), ioException);
		} finally {
			try {
				if (destinationOutputStream != null) {
					destinationOutputStream.close();
				}
				if (zipInputStream != null) {
					zipInputStream.close();
				}
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException ignoredException) {
                Log.e(TAG, "Error closing File", ignoredException);
			}
		}
		return false;
	}
}
