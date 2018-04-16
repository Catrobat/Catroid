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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipArchiver {

	private static final String DIRECTORY_LEVEL_UP = "../";
	private static final int COMPRESSION_LEVEL = 0;

	public void zip(String archive, String... filePaths) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(archive);
		ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		zipOutputStream.setLevel(COMPRESSION_LEVEL);

		List<File> files = new ArrayList<>();
		for (String filePath : filePaths) {
			files.add(new File(filePath));
		}
		writeZipEntriesToStream(zipOutputStream, files);
	}

	private void writeZipEntriesToStream(ZipOutputStream zipOutputStream, List<File> files) throws IOException {
		for (File file : files) {
			if (!file.exists()) {
				throw new FileNotFoundException("File: " + file.getAbsolutePath() + " does NOT exist.");
			}

			if (file.isDirectory()) {
				writeZipEntriesToStream(zipOutputStream, Arrays.asList(file.listFiles()));
				continue;
			}

			zipOutputStream.putNextEntry(new ZipEntry(file.getName()));

			FileInputStream fileInputStream = new FileInputStream(file);

			byte[] b = new byte[Constants.BUFFER_8K];
			int len;

			try {
				while ((len = fileInputStream.read(b)) != -1) {
					zipOutputStream.write(b, 0, len);
				}
			} finally {
				fileInputStream.close();
				zipOutputStream.closeEntry();
			}
		}
	}

	public void unzip(String archive, String dstDir) throws IOException {
		InputStream inputStream = new FileInputStream(archive);
		unzip(inputStream, dstDir);
	}

	public void unzip(InputStream is, String dstDir) throws IOException {
		createDirIfNecessary(dstDir);

		ZipInputStream zipInputStream = new ZipInputStream(is);
		ZipEntry zipEntry;

		try {
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().contains(DIRECTORY_LEVEL_UP)) {
					continue;
				}
				if (zipEntry.isDirectory()) {
					createDirIfNecessary(Utils.buildPath(dstDir, zipEntry.getName()));
					continue;
				}

				FileOutputStream fileOutputStream = new FileOutputStream(
						Utils.buildPath(dstDir,
						zipEntry.getName()));

				byte[] b = new byte[Constants.BUFFER_8K];
				int len;

				try {
					while ((len = zipInputStream.read(b)) != -1) {
						fileOutputStream.write(b, 0, len);
					}
				} finally {
					fileOutputStream.close();
				}
			}
		} finally {
			zipInputStream.close();
		}
	}

	private void createDirIfNecessary(String path) throws IOException {
		File file = new File(path);
		if (!file.exists() && !file.mkdir()) {
			throw new IOException("Could NOT create Dir: " + path);
		}
	}
}
