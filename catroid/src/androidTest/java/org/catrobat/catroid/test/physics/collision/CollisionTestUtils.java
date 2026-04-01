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

package org.catrobat.catroid.test.physics.collision;

import android.content.Context;

import junit.framework.Assert;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.test.physics.PhysicsTestUtils.generateLookData;

public final class CollisionTestUtils {

	private CollisionTestUtils() {
		throw new AssertionError();
	}

	public static void initializeSprite(Sprite sprite, int resourceId, String filename, Context context,
			Project project) throws IOException {
		sprite.look = new Look(sprite);
		sprite.setActionFactory(new ActionFactory());

		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;

		File file = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				resourceId,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				hashedFileName,
				1);

		LookData lookData = generateLookData(file);
		Assert.assertNotNull(lookData);
		CollisionInformation collisionInformation = lookData.getCollisionInformation();
		collisionInformation.loadCollisionPolygon();

		sprite.look.setLookData(lookData);
		sprite.getLookList().add(lookData);
		sprite.look.setHeight(sprite.look.getLookData().getPixmap().getHeight());
		sprite.look.setWidth(sprite.look.getLookData().getPixmap().getWidth());
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
	}
}
