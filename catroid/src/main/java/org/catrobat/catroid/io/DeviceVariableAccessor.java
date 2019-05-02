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

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

import static org.catrobat.catroid.common.Constants.DEVICE_VARIABLE_JSON_FILENAME;

public final class DeviceVariableAccessor {

	public static final String TAG = DeviceVariableAccessor.class.getSimpleName();
	private static final int MAX_GENERATE_KEY_TRIES = 100;
	private static final Object LOCK = new Object();
	private File deviceVariablesFile;

	public DeviceVariableAccessor(File projectDirectory) {
		deviceVariablesFile = new File(projectDirectory, DEVICE_VARIABLE_JSON_FILENAME);
	}

	public boolean readUserVariableValue(UserVariable variable) throws IOException {
		if (checkDeviceVariableKeyEmpty(variable)) {
			return false;
		}

		if (!deviceVariablesFile.exists()) {
			return false;
		}

		synchronized (LOCK) {
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
		if (checkDeviceVariableKeyEmpty(userVariable)) {
			addKeyToUserVariable(userVariable);
		}

		if (!deviceVariablesFile.exists()) {
			deviceVariablesFile.createNewFile();
		}

		synchronized (LOCK) {
			HashMap deviceVariableMap = readMapFromJson();

			if (deviceVariableMap == null) {
				deviceVariableMap = new HashMap<>();
			}

			deviceVariableMap.put(userVariable.getDeviceValueKey(), userVariable.getValue());
			writeMapToJson(deviceVariableMap);
		}
	}

	private UUID generateNewKey() {
		UUID key = UUID.randomUUID();

		synchronized (LOCK) {
			HashMap deviceVariableMap;

			try {
				deviceVariableMap = readMapFromJson();
			} catch (FileNotFoundException e) {
				return key;
			}

			if (deviceVariableMap == null) {
				return key;
			}
			for (int tries = 0; deviceVariableMap.containsKey(key) && tries < MAX_GENERATE_KEY_TRIES; tries++) {
				Log.i(TAG, "Try Nr" + tries + ": HashMap already contains the key: " + key + ". Generating new key..");
				key = UUID.randomUUID();
			}
		}
		return key;
	}

	public void addKeyToUserVariable(UserVariable variable) {
		try {
			variable.setDeviceValueKey(generateNewKey());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public static boolean checkDeviceVariableKeyEmpty(UserVariable variable) {
		return variable.getDeviceValueKey() == null;
	}

	public void deleteAllLocalVariables(Sprite sprite) throws IOException {
		if (sprite == null) {
			return;
		}
		synchronized (LOCK) {
			HashMap deviceVariableMap = readMapFromJson();
			for (UserVariable userVariable : sprite.getUserVariables()) {
				if (checkDeviceVariableKeyEmpty(userVariable)) {
					continue;
				}
				deviceVariableMap.remove(userVariable.getDeviceValueKey());
				userVariable.setDeviceValueKey(null);
			}
			writeMapToJson(deviceVariableMap);
		}
	}

	public void removeDeviceValue(UserVariable variable) {
		if (!deviceVariablesFile.exists() || checkDeviceVariableKeyEmpty(variable)) {
			return;
		}

		synchronized (LOCK) {
			try {
				HashMap deviceVariableMap = readMapFromJson();
				deviceVariableMap.remove(variable.getDeviceValueKey());
				variable.setDeviceValueKey(null);
				writeMapToJson(deviceVariableMap);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private HashMap<UUID, Object> readMapFromJson() throws FileNotFoundException {
		Type mapType = new TypeToken<HashMap<UUID, Object>>() {}.getType();
		return new Gson().fromJson(new FileReader(deviceVariablesFile), mapType);
	}

	private void writeMapToJson(HashMap map) throws IOException {
		BufferedOutputStream bos = null;
		try {
			Type mapType = new TypeToken<HashMap<UUID, Object>>() {}.getType();
			bos = new BufferedOutputStream(new FileOutputStream(deviceVariablesFile));
			bos.write(new Gson().toJson(map, mapType).getBytes());
			bos.flush();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (bos != null) {
				bos.close();
			}
		}
	}
}
