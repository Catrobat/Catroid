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

package org.catrobat.catroid.test.common;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.utils.ImageEditing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

@RunWith(AndroidJUnit4.class)
public class LookDataTest {
	private LookData lookData;
	private File imageFolder;
	private final String fileName = "collision_donut.png";

	@Before
	public void setUp() throws Exception {
		StorageOperations.deleteDir(ApplicationProvider.getApplicationContext().getCacheDir());
		imageFolder = new File(ApplicationProvider.getApplicationContext().getCacheDir(), IMAGE_DIRECTORY_NAME);
		if (!imageFolder.exists()) {
			imageFolder.mkdirs();
		}

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.collision_donut,
				imageFolder, fileName, 1.0);

		lookData = new LookData("test", imageFile);
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(ApplicationProvider.getApplicationContext().getCacheDir());
	}

	@Test
	public void testCollisionInformation() {
		String metadata = ImageEditing.readMetaDataStringFromPNG(imageFolder.getAbsolutePath() + "/" + fileName,
				Constants.COLLISION_PNG_META_TAG_KEY);

		assertEquals("", metadata);

		lookData.getCollisionInformation().loadCollisionPolygon();

		metadata = ImageEditing.readMetaDataStringFromPNG(imageFolder.getAbsolutePath() + "/" + fileName,
				Constants.COLLISION_PNG_META_TAG_KEY);

		final String expectedMetadata = "0.0;228.0;9.0;321.0;57.0;411.0;136.0;474.0;228.0;500.0;305.0;495.0;375.0;"
				+ "468.0;436.0;419.0;474.0;364.0;497.0;295.0;499.0;218.0;481.0;151.0;443.0;89.0;385.0;38.0;321.0;9.0;"
				+ "179.0;9.0;115.0;38.0;57.0;89.0;19.0;151.0|125.0;248.0;154.0;330.0;201.0;365.0;248.0;375.0;313.0;"
				+ "358.0;365.0;299.0;374.0;234.0;346.0;170.0;285.0;130.0;206.0;133.0;150.0;175.0";

		assertEquals(expectedMetadata, metadata);
	}
}
