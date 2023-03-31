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

package org.catrobat.catroid.test.io;

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ResourceImporterTest {

	private File testDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "ResourceImporterTest");

	@Before
	public void setUp() {
		testDir.mkdirs();
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(testDir);
	}

	@Test
	public void testImportImageFile() throws IOException {
		File fileFromDrawables = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai, testDir, "drawable.png", 1);

		assertTrue(fileFromDrawables.getAbsolutePath() + " does not exist", fileFromDrawables.exists());

		File fileFromRaw = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(), org.catrobat.catroid.test.R.raw.alpha_test_image,
				testDir, "raw.png", 1);

		assertTrue(fileFromRaw.getAbsolutePath() + " does not exist", fileFromRaw.exists());
	}

	@Test
	public void testImportSoundFile() throws IOException {
		File fileFromRaw = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(), org.catrobat.catroid.test.R.raw.longtestsound,
				testDir, "sound.m4a"
		);

		assertTrue(fileFromRaw.getAbsolutePath() + " does not exist", fileFromRaw.exists());
	}
}
