/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.test.devices.mindstorms.ev3;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.MindstormsEV3TestModel;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl;

public class LegoEV3ImplTestWithModel extends AndroidTestCase {
	private static final String TAG = LegoEV3ImplTestWithModel.class.getSimpleName();

	private LegoEV3 ev3;
	private MindstormsEV3TestModel ev3TestModel;
	ConnectionDataLogger logger;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Context applicationContext = this.getContext().getApplicationContext();

		ev3TestModel = new MindstormsEV3TestModel();

		ev3 = new LegoEV3Impl(applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(ev3TestModel);
		ev3.setConnection(logger.getConnectionProxy());
	}

	public void testKeepAlive() {
		int expectedKeepAliveTime = 5; // defined in LegoEV3Impl

		ev3.initialise();
		ev3.isAlive();

		boolean timeOut = false;
		int timer = 0;

		while (!ev3TestModel.isKeepAliveSet() && !timeOut) {
			try {
				Thread.sleep(50);
				timer += 50;
			} catch (InterruptedException e) {
				Log.w(TAG, "Sleep was interrupted", e);
			}

			if (timer >= 10000) {
				timeOut = true;
			}
		}

		assertEquals("Expected keep alive time deosn't match", expectedKeepAliveTime, ev3TestModel.getKeepAliveTime());
	}
}
