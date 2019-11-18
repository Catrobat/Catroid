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

package org.catrobat.catroid.test.sensing;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.util.Pair;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

@RunWith(AndroidJUnit4.class)
public class CollisionInformationTest {
	private LookData lookData;
	private File imageFolder;
	private final String fileName = "cube_with_alpha.png";

	@Before
	public void setUp() throws Exception {
		StorageOperations.deleteDir(InstrumentationRegistry.getTargetContext().getCacheDir());
		imageFolder = new File(InstrumentationRegistry.getTargetContext().getCacheDir(), IMAGE_DIRECTORY_NAME);
		if (!imageFolder.exists()) {
			imageFolder.mkdirs();
		}

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.cube_with_alpha,
				imageFolder, fileName, 1.0);

		lookData = new LookData("Test", imageFile);
	}

	@Test
	public void testBubblePositionCalculation() {
		Integer xLeft = -285;
		Integer yLeft = 285;
		Integer xRight = 284;
		Integer yRight = 285;

		lookData.getCollisionInformation().loadOrCreateCollisionPolygon();

		Pair<Integer, Integer> positionLeft = lookData.getCollisionInformation().getLeftBubblePos();
		Pair<Integer, Integer> positionRight = lookData.getCollisionInformation().getRightBubblePos();

		assertEquals(fileName, lookData.getFile().getName());

		assertEquals(xLeft, positionLeft.first);
		assertEquals(yLeft, positionLeft.second);
		assertEquals(xRight, positionRight.first);
		assertEquals(yRight, positionRight.second);
	}
}

