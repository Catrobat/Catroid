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

package org.catrobat.catroid.test.apkgenerator;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BrokenZipHashingTest {
	@Before
	public void setUp() throws IOException {

	}

	@After
	public void tearDown() throws IOException {

	}

	@Test
	public void testCompareZippedProjects() throws IOException, NoSuchAlgorithmException {
		String testProjectPath = Utils.buildProjectPath("testCompareZips");
		String outputZipPath = Utils.buildPath(Constants.DEFAULT_ROOT,"testCompareZips.zip");

		InputStream inputStream = InstrumentationRegistry.getContext().getAssets().open("generated965.zip");
		new ZipArchiver().unzip(inputStream, testProjectPath);
		new ZipArchiver().zip(outputZipPath, new File(testProjectPath).listFiles());

		InputStream assetsInputZip = InstrumentationRegistry.getContext().getAssets().open("generated965.zip");
		byte[] assetsZipMd5 = md5Checksum(assetsInputZip);

		InputStream outputZip = new FileInputStream(new File(outputZipPath));
		byte[] outputZipMd5 = md5Checksum(outputZip);

		assertEquals(assetsZipMd5, outputZipMd5);

	}

	public static byte[] md5Checksum(InputStream fis) throws IOException, NoSuchAlgorithmException{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[Constants.BUFFER_8K];

		int length;
		while ((length = fis.read(buffer)) != -1) {
			messageDigest.update(buffer, 0, length);
		}
		return messageDigest.digest();
	}
}
