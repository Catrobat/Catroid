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
package org.catrobat.catroid.drone;

import com.parrot.freeflight.service.DroneControlService;

public final class DroneServiceWrapper {

	private static DroneServiceWrapper instance = null;
	private static DroneControlService droneControlService = null;

	private DroneServiceWrapper() {
	}

	public static DroneServiceWrapper getInstance() {
		if (instance == null) {
			instance = new DroneServiceWrapper();
		}

		return instance;
	}

	public void setDroneService(DroneControlService service) {
		droneControlService = service;
	}

	/* first we wait for autonomous reconnecting otherwise we try to reconnect to drone and then check if
	 * droneControlService is not null.
	   if connection is successful droneControlService should not be null!
	*/
	public DroneControlService getDroneService() {
		/*if(droneControlService == null){

			// polling: waiting for autonomous reconnecting
			int counter = 0;
			while(droneControlService == null && counter <= 15){
				try {
					Thread.sleep(100);
					counter++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(droneControlService == null) {
				DroneConnection droneConnection = new DroneConnection(CatroidApplication.getAppContext());

				if (droneConnection != null) {
					try {
						droneConnection.initialise();
					} catch (RuntimeException runtimeException) {
						Toast.makeText(CatroidApplication.getAppContext(), R.string.error_no_drone_connected, Toast.LENGTH_LONG).show();
						Log.e(getClass().getSimpleName(), "drone connection initialization was not successful!");
					}
				}
			}
		}

		while(droneControlService == null){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		return droneControlService;

	}
}
