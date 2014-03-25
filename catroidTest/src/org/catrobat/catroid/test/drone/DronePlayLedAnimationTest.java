/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.drone;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import android.test.InstrumentationTestCase;

import com.parrot.freeflight.drone.DroneConfig;
import com.parrot.freeflight.drone.DroneProxyInterface;
import com.parrot.freeflight.service.DroneControlServiceInterface;

import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.easymock.EasyMock;
import org.junit.Test;

public class DronePlayLedAnimationTest extends InstrumentationTestCase {

	@Test
	public void testActionPlayLedAnimation() {

		DroneControlServiceInterface droneControlServiceMock = EasyMock.createMock(DroneControlServiceInterface.class);

		droneControlServiceMock.playLedAnimation(0.1f, 1, 1);

		expect(droneControlServiceMock.getDroneConfig()).andReturn(new DroneConfig());
		replay(droneControlServiceMock);

		droneControlServiceMock.playLedAnimation(0.1f, 1, 1);

		droneControlServiceMock.getDroneConfig();
		verify(droneControlServiceMock);

		assertNotNull("Oject is Null!", droneControlServiceMock);

		DroneProxyInterface proxy = EasyMock.createMock(DroneProxyInterface.class);

	}

	public void testResourcesTakeoffBrick() {
		DronePlayLedAnimationBrick brick = new DronePlayLedAnimationBrick(null);
		assertEquals("Drone brick resources configured wrong", 0x20, brick.getRequiredResources());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
