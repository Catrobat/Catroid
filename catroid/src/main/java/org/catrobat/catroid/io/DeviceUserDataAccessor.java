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

package org.catrobat.catroid.io;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.VisibleForTesting;

public abstract class DeviceUserDataAccessor {

	public static final String TAG = DeviceUserDataAccessor.class.getSimpleName();
	private File deviceFile;

	public DeviceUserDataAccessor(File projectDirectory) {
		deviceFile = new File(projectDirectory, getDeviceFileName());
	}

	protected abstract Object getLock();

	@VisibleForTesting
	public void setDeviceFile(File deviceFile) {
		this.deviceFile = deviceFile;
	}

	public boolean readUserData(UserData userData) {

		Map deviceMap = readMapFromJson();

		if (deviceMap == null) {
			userData.reset();
			return false;
		}

		if (!deviceMap.containsKey(userData.getDeviceKey())) {
			userData.reset();
			return false;
		}
		userData.setValue(deviceMap.get(userData.getDeviceKey()));
		return true;
	}

	public void writeUserData(UserData userData) {
		synchronized (getLock()) {
			try {
				if (!deviceFile.exists() && !deviceFile.createNewFile()) {
					Log.e(TAG, "Cannot create " + deviceFile.getAbsolutePath());
					return;
				}

				Map deviceVariableMap = readMapFromJson();

				if (deviceVariableMap == null) {
					deviceVariableMap = new HashMap<>();
				}

				deviceVariableMap.put(userData.getDeviceKey(), userData.getValue());
				writeMapToJson(deviceVariableMap);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	public void removeDeviceValue(UserData userData) {
		synchronized (getLock()) {
			if (!deviceFile.exists()) {
				return;
			}

			try {
				Map deviceVariableMap = readMapFromJson();
				if (deviceVariableMap == null) {
					return;
				}

				deviceVariableMap.remove(userData.getDeviceKey());
				writeMapToJson(deviceVariableMap);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	@VisibleForTesting
	public Map<UUID, Object> readMapFromJson() {
		try {
			Type mapType = new TypeToken<HashMap<UUID, Object>>() {
			}.getType();
			return new Gson().fromJson(new FileReader(deviceFile), mapType);
		} catch (FileNotFoundException e) {
			if (deviceFile.exists()) {
				Log.e(TAG, "Device Variable File corrupted!");
				deviceFile.delete();
			}
			return null;
		}
	}

	@VisibleForTesting
	public void writeMapToJson(Map map) {
		try (BufferedOutputStream bos =
				new BufferedOutputStream(new FileOutputStream(deviceFile))) {
			Type mapType = new TypeToken<HashMap<UUID, Object>>() {
			}.getType();
			String jsonString = new Gson().toJson(map, mapType);
			bos.write(jsonString.getBytes());
			bos.flush();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void cleanUpDeletedUserData(Project project) {
		synchronized (getLock()) {
			if (!deviceFile.exists()) {
				return;
			}

			Map<UUID, Object> deviceVariableMap = readMapFromJson();
			if (deviceVariableMap == null) {
				return;
			}

			List<UUID> globalVariableKeys = getKeyList(getUserData(project));
			List<UUID> localVariableKeys = new ArrayList<>();
			for (Scene scene : project.getSceneList()) {
				for (Sprite sprite : scene.getSpriteList()) {
					localVariableKeys.addAll(getKeyList(getUserData(sprite)));
				}
			}

			Set<UUID> keysToRemove = new HashSet<>(deviceVariableMap.keySet());
			keysToRemove.removeAll(globalVariableKeys);
			keysToRemove.removeAll(localVariableKeys);

			for (UUID key : keysToRemove) {
				deviceVariableMap.remove(key);
			}

			try {
				writeMapToJson(deviceVariableMap);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	protected abstract String getDeviceFileName();

	protected abstract List<? extends UserData> getUserData(Sprite sprite);

	protected abstract List<? extends UserData> getUserData(Project project);

	private List<UUID> getKeyList(List<? extends UserData> userDataList) {
		List<UUID> keyList = new ArrayList<>();
		for (UserData userData : userDataList) {
			keyList.add(userData.getDeviceKey());
		}
		return keyList;
	}
}
