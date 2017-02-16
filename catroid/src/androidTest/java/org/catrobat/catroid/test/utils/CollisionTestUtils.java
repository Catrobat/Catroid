/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.test.utils;

import android.content.Context;

import com.badlogic.gdx.graphics.Pixmap;

import junit.framework.Assert;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public final class CollisionTestUtils {

	private CollisionTestUtils() {
		throw new AssertionError();
	}

	public static LookData generateLookData(File testImage) {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
		return lookData;
	}

	public static void initializeSprite(Sprite sprite, int resourceId, String filename, Context context, Project
			project) {
		sprite.look = new Look(sprite);
		sprite.setActionFactory(new ActionFactory());

		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;
		File file = null;

		try {
			file = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
					hashedFileName, resourceId, context,
					TestUtils.TYPE_IMAGE_FILE);
		} catch (Exception e) {
			Assert.fail("Couldn't load file, exception thrown!");
		}

		LookData lookData = generateLookData(file);
		Assert.assertNotNull("lookData is null", lookData);
		CollisionInformation collisionInformation = lookData.getCollisionInformation();
		collisionInformation.loadOrCreateCollisionPolygon();

		sprite.look.setLookData(lookData);
		sprite.getLookDataList().add(lookData);
		sprite.look.setHeight(sprite.look.getLookData().getPixmap().getHeight());
		sprite.look.setWidth(sprite.look.getLookData().getPixmap().getWidth());
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
	}
}
