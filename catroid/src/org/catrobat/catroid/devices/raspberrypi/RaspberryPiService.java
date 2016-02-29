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
package org.catrobat.catroid.devices.raspberrypi;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RaspberryPiService {

	private static final String TAG = RaspberryPiService.class.getSimpleName();

	public RPiSocketConnection connection = null;

	private static RaspberryPiService instance;

	private enum GpioVersionType {
		SMALL_GPIO, BIG_GPIO, COMPUTE_MODULE
	}

	private Map<String, GpioVersionType> gpioVersionMap = new HashMap<>();

	private Set<Integer> pinInterrupts = null;

	public static RaspberryPiService getInstance() {
		if (instance == null) {
			instance = new RaspberryPiService();
		}
		return instance;
	}

	private RaspberryPiService() {
		initGpioVersionMap();
		pinInterrupts = new HashSet<Integer>();
	}

	public void addPinInterrupt(int pin) {
		pinInterrupts.add(pin);
	}

	public boolean connect(String host, int port) {
		AsyncRPiTaskRunner rpi;
		try {
			rpi = new AsyncRPiTaskRunner();
			rpi.connect(host, port);
			connection = rpi.getConnection();
		} catch (Exception e) {
			Log.e(TAG, "connecting to " + host + ":" + port + " failed" + e);
			return false;
		}

		if (rpi.getConnection().isConnected()) {
			return true;
		}

		return false;
	}

	public void disconnect() {
		if (connection != null) {

			try {
				connection.disconnect();
				connection = null;
			} catch (IOException e) {
				Log.e(TAG, "Exception during disconnect: " + e);
			}
		}
		pinInterrupts.clear();
	}

	public ArrayList<Integer> getGpioList(String revision) {
		GpioVersionType version = gpioVersionMap.get(revision);
		ArrayList<Integer> availableGPIOs = new ArrayList<>();
		if (version == GpioVersionType.SMALL_GPIO) {
			availableGPIOs.add(3);
			availableGPIOs.add(5);
			availableGPIOs.add(7);
			availableGPIOs.add(8);
			availableGPIOs.add(10);
			availableGPIOs.add(11);
			availableGPIOs.add(12);
			availableGPIOs.add(13);
			availableGPIOs.add(15);
			availableGPIOs.add(16);
			availableGPIOs.add(18);
			availableGPIOs.add(19);
			availableGPIOs.add(21);
			availableGPIOs.add(22);
			availableGPIOs.add(23);
			availableGPIOs.add(24);
			availableGPIOs.add(26);
		} else if (version == GpioVersionType.BIG_GPIO) {
			availableGPIOs.add(3);
			availableGPIOs.add(5);
			availableGPIOs.add(7);
			availableGPIOs.add(8);
			availableGPIOs.add(10);
			availableGPIOs.add(11);
			availableGPIOs.add(12);
			availableGPIOs.add(13);
			availableGPIOs.add(15);
			availableGPIOs.add(16);
			availableGPIOs.add(18);
			availableGPIOs.add(19);
			availableGPIOs.add(21);
			availableGPIOs.add(22);
			availableGPIOs.add(23);
			availableGPIOs.add(24);
			availableGPIOs.add(26);
			availableGPIOs.add(29);
			availableGPIOs.add(31);
			availableGPIOs.add(32);
			availableGPIOs.add(33);
			availableGPIOs.add(35);
			availableGPIOs.add(36);
			availableGPIOs.add(37);
			availableGPIOs.add(38);
			availableGPIOs.add(40);
		} else { // legacy mode, try to support if we don't know the version
			availableGPIOs.add(3);
			availableGPIOs.add(5);
			availableGPIOs.add(7);
			availableGPIOs.add(8);
			availableGPIOs.add(10);
			availableGPIOs.add(11);
			availableGPIOs.add(12);
			availableGPIOs.add(13);
			availableGPIOs.add(15);
			availableGPIOs.add(16);
			availableGPIOs.add(18);
			availableGPIOs.add(19);
			availableGPIOs.add(21);
			availableGPIOs.add(22);
			availableGPIOs.add(23);
			availableGPIOs.add(24);
			availableGPIOs.add(26);
		}

		return availableGPIOs;
	}

	private void initGpioVersionMap() {
		gpioVersionMap.put("a01041", GpioVersionType.BIG_GPIO);
		gpioVersionMap.put("a21041", GpioVersionType.BIG_GPIO);
		gpioVersionMap.put("0013", GpioVersionType.BIG_GPIO);
		gpioVersionMap.put("0012", GpioVersionType.BIG_GPIO);
		gpioVersionMap.put("0011", GpioVersionType.COMPUTE_MODULE);
		gpioVersionMap.put("0010", GpioVersionType.BIG_GPIO);
		gpioVersionMap.put("000f", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("000e", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("000d", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0009", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0008", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0007", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0006", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0005", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0004", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0003", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("0002", GpioVersionType.SMALL_GPIO);
		gpioVersionMap.put("Beta", GpioVersionType.SMALL_GPIO);
	}

	public boolean isValidPin(String revision, int pinNumber) {
		return getGpioList(revision).contains(pinNumber);
	}

	public Set<Integer> getPinInterrupts() {
		return pinInterrupts;
	}
}
