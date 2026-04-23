/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.utiltests;

import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.TMP_PATH;

@RunWith(AndroidJUnit4.class)
public class MD5ChecksumTest {

	private static final String MD5_EMPTY = "D41D8CD98F00B204E9800998ECF8427E";
	private static final String MD5_CATROID = "4F982D927F4784F69AD6D6AF38FD96AD";
	private static final String MD5_HELLO_WORLD = "ED076287532E86365E841E92BFC50D8C";

	@Test
	public void testMD5CheckSumOfFile() throws IOException {
		PrintWriter printWriter;

		File tempDir = new File(TMP_PATH);
		tempDir.mkdirs();

		File md5TestFile = new File(tempDir, "catroid.txt");

		if (md5TestFile.exists()) {
			md5TestFile.delete();
		}

		md5TestFile.createNewFile();
		assertEquals(MD5_EMPTY.toLowerCase(Locale.US), Utils.md5Checksum(md5TestFile));

		printWriter = new PrintWriter(md5TestFile);
		printWriter.print("catroid");
		printWriter.close();

		assertEquals(MD5_CATROID.toLowerCase(Locale.US), Utils.md5Checksum(md5TestFile));

		StorageOperations.deleteDir(tempDir);
	}

	@Test
	public void testMD5CheckSumOfString() {
		assertEquals(MD5_CATROID.toLowerCase(Locale.US), Utils.md5Checksum("catroid"));
		assertEquals(MD5_EMPTY.toLowerCase(Locale.US), Utils.md5Checksum(""));
		assertEquals(MD5_HELLO_WORLD.toLowerCase(Locale.US), Utils.md5Checksum("Hello World!"));
	}
}
