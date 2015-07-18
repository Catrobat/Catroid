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

package org.catrobat.catroid.test.devices.mindstorms.nxt;

import android.content.Context;
import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.MindstormsNXTTestModel;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXTImpl;

public class LegoNXTImplTestWithModel extends AndroidTestCase {

	private LegoNXT nxt;
	private MindstormsNXTTestModel nxtTestModel;
	ConnectionDataLogger logger;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Context applicationContext = this.getContext().getApplicationContext();

		nxtTestModel = new MindstormsNXTTestModel();

		nxt = new LegoNXTImpl(applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(nxtTestModel);
		nxt.setConnection(logger.getConnectionProxy());
	}

	@Override
	protected void tearDown() throws Exception {
		nxt.disconnect();
		logger.disconnectAndDestroy();
		super.tearDown();
	}

	public void testGetBatteryLevel() {
		int expectedVoltage = 7533;
		nxtTestModel.setBatteryValue(expectedVoltage);
		nxt.initialise();
		assertEquals("Expected battery voltage not the same as received from LegoNXT", expectedVoltage, nxt.getBatteryLevel());
	}

	public void testKeepAlive() {

		int expectedKeepAliveTime = 3600;
		nxtTestModel.setKeepAliveTime(expectedKeepAliveTime);
		nxt.initialise();
		assertEquals("Expected keep alive time not the same as received vom LegoNXT", expectedKeepAliveTime, nxt.getKeepAliveTime());
	}
}
