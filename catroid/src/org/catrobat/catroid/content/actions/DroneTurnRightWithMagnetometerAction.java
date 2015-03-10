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
package org.catrobat.catroid.content.actions;

import android.util.Log;

public class DroneTurnRightWithMagnetometerAction extends DroneMoveAction {
	private static final String TAG = DroneTurnRightWithMagnetometerAction.class.getSimpleName();
	private boolean isCalled = false;

	@Override
	protected void begin() {
		super.begin();
		if (isCalled == false) {
			super.getDroneService().setMagnetoEnabled(true);
			super.getDroneService().calibrateMagneto();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, Log.getStackTraceString(e));

			}
			Log.d(getClass().getSimpleName(), "isCalled");
		}
	}

	@Override
	protected void move() {
		int value = (int) (super.getPowerNormalized() * 100);
		super.setCommandAndYawEnabled(true);
		super.getDroneService().setDeviceOrientation(0, -value);
	}

	@Override
	protected void moveEnd() {
		super.setCommandAndYawEnabled(false);
		isCalled = true;
	}

}
