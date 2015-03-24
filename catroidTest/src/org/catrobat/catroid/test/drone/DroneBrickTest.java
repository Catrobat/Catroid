/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.drone;

import android.test.InstrumentationTestCase;
import android.util.Log;

import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.drone.DroneBrickFactory;
import org.catrobat.catroid.drone.DroneBrickFactory.DroneBricks;

public class DroneBrickTest extends InstrumentationTestCase {

	private static final String TAG = DroneBrickTest.class.getSimpleName();
	private static final int DRONE_RESOURCE = 0x20;

	public void testAllBrickResources() {
		for (DroneBricks brick : DroneBrickFactory.DroneBricks.values()) {
			BrickBaseType brickFromFactory = DroneBrickFactory.getInstanceOfDroneBrick(brick, null, 0, 0);
			String brickName = brickFromFactory.getClass().getSimpleName();
			Log.d(TAG, "brickName: " + brickName);
			assertEquals("Resorce is wrong for brick: " + brickName, DRONE_RESOURCE,
					brickFromFactory.getRequiredResources());
		}
	}
}
