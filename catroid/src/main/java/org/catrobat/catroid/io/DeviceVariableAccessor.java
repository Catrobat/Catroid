/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;

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
import java.util.Set;
import java.util.UUID;

import static org.catrobat.catroid.common.Constants.DEVICE_VARIABLE_JSON_FILENAME;

public final class DeviceVariableAccessor {

	public static final String TAG = DeviceVariableAccessor.class.getSimpleName();
	private static final Object LOCK = new Object();
	private File deviceVariablesFile;

	public DeviceVariableAccessor(File projectDirectory) {
		deviceVariablesFile = new File(projectDirectory, DEVICE_VARIABLE_JSON_FILENAME);
	}

	@VisibleForTesting
	public void setDeviceVariablesFile(File deviceVariablesFile) {
		this.deviceVariablesFile = deviceVariablesFile;
	}

	public boolean readUserVariableValue(UserVariable variable) {
		synchronized (LOCK) {
			if (!deviceVariablesFile.exists()) {
				return false;
			}

			HashMap deviceVariableMap = readMapFromJson();

			if (deviceVariableMap == null) {
				return false;
			}

			if (!deviceVariableMap.containsKey(variable.getDeviceValueKey())) {
				return false;
			}
			variable.setValue(deviceVariableMap.get(variable.getDeviceValueKey()));
		}
		return true;
	}

	public void writeVariable(UserVariable userVariable) throws IOException {
		synchronized (LOCK) {
			if (!deviceVariablesFile.exists()) {
				deviceVariablesFile.createNewFile();
			}

			HashMap deviceVariableMap = readMapFromJson();

			if (deviceVariableMap == null) {
				deviceVariableMap = new HashMap<>();
			}

			deviceVariableMap.put(userVariable.getDeviceValueKey(), userVariable.getValue());
			writeMapToJson(deviceVariableMap);
		}
	}

	public void removeDeviceValue(UserVariable variable) {
		synchronized (LOCK) {
			if (!deviceVariablesFile.exists()) {
				return;
			}

			try {
				HashMap deviceVariableMap = readMapFromJson();
				if (deviceVariableMap == null) {
					return;
				}

				deviceVariableMap.remove(variable.getDeviceValueKey());
				writeMapToJson(deviceVariableMap);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	@VisibleForTesting
	public HashMap<UUID, Object> readMapFromJson() {
		try {
			Type mapType = new TypeToken<HashMap<UUID, Object>>() {}.getType();
			return new Gson().fromJson(new FileReader(deviceVariablesFile), mapType);
		} catch (FileNotFoundException e) {
			if (deviceVariablesFile.exists()) {
				Log.e(TAG, "Device Variable File corrupted!");
				deviceVariablesFile.delete();
			}
			return null;
		}
	}

	@VisibleForTesting
	public void writeMapToJson(HashMap map) throws IOException {
		BufferedOutputStream bos = null;
		try {
			Type mapType = new TypeToken<HashMap<UUID, Object>>() {}.getType();
			bos = new BufferedOutputStream(new FileOutputStream(deviceVariablesFile));
			String jsonString = new Gson().toJson(map, mapType);
			bos.write(jsonString.getBytes());
			bos.flush();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (bos != null) {
				bos.close();
			}
		}
	}

	public void cleanUpDeletedVariables(Project project) {
		synchronized (LOCK) {
			if (!deviceVariablesFile.exists()) {
				return;
			}

			try {
				HashMap deviceVariableMap = readMapFromJson();
				if (deviceVariableMap == null) {
					return;
				}

				Set<UUID> keysToRemove = new HashSet<UUID>(deviceVariableMap.keySet());

				List<UserVariable> globalVars = new ArrayList<>(project.getUserVariables());

				for (UserVariable variable: globalVars) {
					keysToRemove.remove(variable.getDeviceValueKey());
				}

				List<UUID> localVariableKeys = new ArrayList<>();
				for (Scene scene: project.getSceneList()) {
					for (Sprite sprite: scene.getSpriteList()) {
						for (UserVariable variable : sprite.getUserVariables()) {
							localVariableKeys.add(variable.getDeviceValueKey());
						}
					}
				}

				for (UUID key: localVariableKeys) {
					keysToRemove.remove(key);
				}

				for (UUID key: keysToRemove) {
					deviceVariableMap.remove(key);
				}

				writeMapToJson(deviceVariableMap);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}
