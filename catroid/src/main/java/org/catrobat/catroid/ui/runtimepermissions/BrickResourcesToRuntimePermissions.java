/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.runtimepermissions;

import android.annotation.SuppressLint;

import org.catrobat.catroid.content.bricks.Brick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.NFC;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public final class BrickResourcesToRuntimePermissions {
	private BrickResourcesToRuntimePermissions() {
		throw new AssertionError("No.");
	}

	@SuppressLint("UseSparseArrays")
	public static List<String> translate(Brick.ResourcesSet brickResources) {
		Map<Integer, List<String>> brickResourcesToPermissions = new HashMap<>();
		brickResourcesToPermissions.put(Brick.SENSOR_GPS, Arrays.asList(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION));
		List<String> bluetoothPermissions = Arrays.asList(BLUETOOTH_ADMIN, BLUETOOTH);
		brickResourcesToPermissions.put(Brick.BLUETOOTH_LEGO_NXT, bluetoothPermissions);
		brickResourcesToPermissions.put(Brick.BLUETOOTH_LEGO_EV3, bluetoothPermissions);
		brickResourcesToPermissions.put(Brick.BLUETOOTH_PHIRO, bluetoothPermissions);
		brickResourcesToPermissions.put(Brick.BLUETOOTH_SENSORS_ARDUINO, bluetoothPermissions);
		List<String> wifiPermissions = Arrays.asList(CHANGE_WIFI_MULTICAST_STATE, CHANGE_WIFI_STATE, ACCESS_WIFI_STATE);
		brickResourcesToPermissions.put(Brick.CAST_REQUIRED, wifiPermissions);
		brickResourcesToPermissions.put(Brick.CAMERA_BACK, Arrays.asList(CAMERA));
		brickResourcesToPermissions.put(Brick.CAMERA_FRONT, Arrays.asList(CAMERA));
		brickResourcesToPermissions.put(Brick.VIDEO, Arrays.asList(CAMERA));
		brickResourcesToPermissions.put(Brick.CAMERA_FLASH, Arrays.asList(CAMERA));
		brickResourcesToPermissions.put(Brick.VIBRATION, Arrays.asList(VIBRATE));
		brickResourcesToPermissions.put(Brick.NFC_ADAPTER, Arrays.asList(NFC));
		brickResourcesToPermissions.put(Brick.FACE_DETECTION, Collections.singletonList(CAMERA));
		brickResourcesToPermissions.put(Brick.OBJECT_DETECTION, Collections.singletonList(CAMERA));
		brickResourcesToPermissions.put(Brick.POSE_DETECTION, Collections.singletonList(CAMERA));
		brickResourcesToPermissions.put(Brick.TEXT_DETECTION, Collections.singletonList(CAMERA));
		brickResourcesToPermissions.put(Brick.MICROPHONE, Arrays.asList(RECORD_AUDIO));
		brickResourcesToPermissions.put(Brick.STORAGE_READ, Arrays.asList(READ_EXTERNAL_STORAGE));
		brickResourcesToPermissions.put(Brick.STORAGE_WRITE, Arrays.asList(WRITE_EXTERNAL_STORAGE));

		Set<String> requiredPermissions = new HashSet<>();
		for (int brickResource : brickResources) {
			if (brickResourcesToPermissions.containsKey(brickResource)) {
				requiredPermissions.addAll(brickResourcesToPermissions.get(brickResource));
			}
		}

		return new ArrayList<>(requiredPermissions);
	}
}
