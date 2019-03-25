/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
package org.catrobat.catroid.test.physics;

import com.badlogic.gdx.graphics.Pixmap;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public final class PhysicsTestUtils {

	private PhysicsTestUtils() {
		throw new AssertionError();
	}

	public static PhysicsObject.Type getType(PhysicsObject physicsObject) throws Exception {
		return (PhysicsObject.Type) Reflection.getPrivateField(physicsObject, "type");
	}

	public static String getInternalImageFilenameFromFilename(String filename) {
		return Utils.md5Checksum(filename) + "_" + filename;
	}

	public static LookData generateLookData(File testImage) {
		LookData lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName(testImage.getName());
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
		return lookData;
	}
}
