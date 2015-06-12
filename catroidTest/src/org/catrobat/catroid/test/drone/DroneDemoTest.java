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

import com.parrot.freeflight.drone.DroneProxy;

import org.mockito.Mockito;

public class DroneDemoTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		//Workaround for Android 4.4 Devices
		//https://code.google.com/p/dexmaker/issues/detail?id=2
		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
	}

	public void testDemo() {
		DroneProxy droneMock = Mockito.mock(DroneProxy.class);

		droneMock.doFlip();
		// was the method called once?
		Mockito.verify(droneMock, Mockito.atLeast(1)).doFlip();
	}
}
