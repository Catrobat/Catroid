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
package org.catrobat.catroid.drone;


import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.actions.JumpingSumoShowBatteryStatusAction;
import org.catrobat.catroid.formulaeditor.UserVariable;

public final class JumpingSumoDataContainer {

	public static final String TAG = JumpingSumoDataContainer.class.getSimpleName();
	private static JumpingSumoDataContainer ourInstance = new JumpingSumoDataContainer();
	public static final String BATTERY_STATUS = "Battery_Status";
	private JumpingSumoShowBatteryStatusAction batteryAction = null;
	private boolean positionHeadUp = true;
	private UserVariable batteryVariable = new UserVariable(BATTERY_STATUS, Constants.JUMPING_SUMO_BATTERY_STATUS);

	public static JumpingSumoDataContainer getInstance() {
		return ourInstance;
	}

	private JumpingSumoDataContainer() {
	}

	public void setBatteryAction(JumpingSumoShowBatteryStatusAction batAction) {
		batteryAction = batAction;
	}

	public void setPostion(boolean pos) {
		positionHeadUp = pos;
	}

	public boolean getPostion() {
		return positionHeadUp;
	}

	public void setBatteryStatus(Object battery) {
		//Object value = "Battery " + battery;
		batteryVariable.setValue(battery);
		if (batteryAction != null) {
			batteryAction.updateBatteryStatus();
		}
	}

	public Object getBatteryStatus() {
		return batteryVariable.getValue();
	}

	public UserVariable getBatteryVariable() {
		return batteryVariable;
	}
}
