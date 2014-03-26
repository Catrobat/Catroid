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

import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.parrot.freeflight.drone.DroneProxyInterface;
import com.parrot.freeflight.drone.DroneProxyWrapper;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.DroneFlipAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.easymock.EasyMock;

/**
 *
 */
public class SimpleDroneTest extends ServiceTestCase<DroneControlService> {

	public SimpleDroneTest() {
		super(DroneControlService.class);
		// TODO Auto-generated constructor stub

	}

	private Sprite testSprite = null;
	private DroneProxyInterface droneProxyInterface = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		testSprite = new Sprite("testSprite");

		droneProxyInterface = EasyMock.createMock(DroneProxyInterface.class);
		DroneProxyWrapper.getSpecificInstanceForTesting(droneProxyInterface);

		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), DroneControlService.class);
		startService(startIntent);

		DroneServiceWrapper.getInstance().setDroneService(getService());

	}

	private void checkInit() {
		assertNotNull("Droneservicewrapper has to be initialised", DroneServiceWrapper.getInstance());
		assertNotNull("Service must be started correctly", getService());
		assertNotNull("Servicewrapper may not be null", DroneServiceWrapper.getInstance().getDroneService());
	}

	public void testDroneFlipAction() {
		checkInit();

		DroneFlipAction action = ExtendedActions.droneFlip();

		droneProxyInterface.doFlip();

		replay(droneProxyInterface);

		action.act(1.0f);

		verify(droneProxyInterface);

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		droneProxyInterface = null;
	}

}
