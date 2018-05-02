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

package org.catrobat.catroid.test.io;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ResourceImporterTest {

	private File testDir = new File(Constants.DEFAULT_ROOT_DIRECTORY, "ResourceImporterTest");

	@Before
	public void setUp() {
		testDir.mkdirs();
	}

	@After
	public void tearDown() throws IOException {
		StorageHandler.deleteDir(testDir);
	}

	@Test
	public void testImportImageFile() throws IOException {
		File fileFromDrawables = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(), testDir, "drawable.png",
				org.catrobat.catroid.test.R.drawable.catroid_banzai, 1);

		assertTrue(fileFromDrawables.getAbsolutePath() + " does not exist", fileFromDrawables.exists());

		File fileFromRaw = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(), testDir, "raw.png",
				org.catrobat.catroid.test.R.raw.alpha_test_image, 1);

		assertTrue(fileFromRaw.getAbsolutePath() + " does not exist", fileFromRaw.exists());
	}

	@Test
	public void testImportSoundFile() throws IOException {
		File fileFromRaw = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(), testDir, "sound.m4a",
				org.catrobat.catroid.test.R.raw.longtestsound);

		assertTrue(fileFromRaw.getAbsolutePath() + " does not exist", fileFromRaw.exists());
	}

	@Test
	public void testExceptionWhenImportingInvalidSoundFileFormat() throws IOException {
		File fileFromRaw = new File(testDir, "sound.m8");
		try {
			fileFromRaw = ResourceImporter.createSoundFileFromResourcesInDirectory(
					InstrumentationRegistry.getContext().getResources(), testDir, "sound.m8",
					org.catrobat.catroid.test.R.raw.longtestsound);
			fail("Expected an IllegalArgumentException.");
		} catch (IllegalArgumentException expectedException) {
			assertFalse(fileFromRaw.getAbsolutePath() + " exists.", fileFromRaw.exists());
		}
	}
}
