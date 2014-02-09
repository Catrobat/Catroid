/*
 * DroneUpdaterListener
 *
 *  Created on: May 5, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.service.listeners;

import com.parrot.freeflight.updater.UpdaterCommand;

public interface DroneUpdaterListener 
{
	public enum ArDroneToolError {
		E_NONE,
		E_UNKNOWN,
		E_WIFI_NOT_AVAILABLE,
		E_UPDATE_BOOTLOADER_FAILED,
		E_UPDATE_BOOTLOADER_CANCELED,
		E_APPLICATION_SHOULD_BE_UPDATED,
		E_FIRMWARE_SHOULD_BE_UPDATED,
		E_CHECK_VERSION_FAILED,
		E_UPDATE_FIRMWARE_FAILED,
	}
	
	public void onPreCommandExecute(UpdaterCommand command);
	public void onUpdateCommand(UpdaterCommand command);
	public void onPostCommandExecute(UpdaterCommand command);
}
