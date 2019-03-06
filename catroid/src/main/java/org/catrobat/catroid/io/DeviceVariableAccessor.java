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

import com.parrot.freeflight.utils.FileUtils;

import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.catrobat.catroid.common.Constants.DEVICE_VARIABLE_DIR;

public final class DeviceVariableAccessor {
	public static final String TAG = DeviceVariableAccessor.class.getSimpleName();

	private static Object lock = new Object();
	private File projectDeviceVariablesFolder;

	public DeviceVariableAccessor(String projectName) {
		projectDeviceVariablesFolder = new File(DEVICE_VARIABLE_DIR, projectName);
	}

	public boolean readUserVariableValue(UserVariable variable) throws IOException,
			ClassNotFoundException {

		if (checkVariableDeviceFileNameEmpty(variable)) {
			return false;
		}

		synchronized (lock) {
			File valueFile = new File(projectDeviceVariablesFolder, variable.getDeviceValueFileName());
			if (!valueFile.exists()) {
				return false;
			}
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(valueFile));
			variable.setValue(inputStream.readObject());
			inputStream.close();
		}
		return true;
	}

	public void writeVariable(UserVariable userVariable) throws IOException {
		addDeviceFileNameToUserVariable(userVariable);

		synchronized (lock) {
			File valueFile = new File(projectDeviceVariablesFolder, userVariable.getDeviceValueFileName());
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(valueFile));
			outputStream.writeObject(userVariable.getValue());
			outputStream.flush();
			outputStream.close();
		}
	}

	private String createNewFileName(File projectDeviceVariablesFolder) {
		String fileName = UUID.randomUUID().toString();
		ArrayList<String> existingFileNames = new ArrayList<>(Arrays.asList(projectDeviceVariablesFolder.list()));
		while (existingFileNames.contains(fileName)) {
			fileName = UUID.randomUUID().toString();
		}
		return fileName;
	}

	public void addDeviceFileNameToUserVariable(UserVariable variable) {
		synchronized (lock) {
			if (!checkVariableDeviceFileNameEmpty(variable)) {
				return;
			}
			makeDeviceVariableFolder();
			createDeviceFileForUserVariable(variable);
		}
	}

	private static boolean checkVariableDeviceFileNameEmpty(UserVariable variable) {
		String fileName = variable.getDeviceValueFileName();
		return fileName == null || fileName.isEmpty();
	}

	private void makeDeviceVariableFolder() {
		if (!projectDeviceVariablesFolder.exists() || !projectDeviceVariablesFolder.isDirectory()) {
			projectDeviceVariablesFolder.mkdirs();
		}
	}

	private void createDeviceFileForUserVariable(UserVariable variable) {
		String fileName = createNewFileName(projectDeviceVariablesFolder);
		variable.setDeviceValueFileName(fileName);

		try {
			new File(projectDeviceVariablesFolder, fileName).createNewFile();
		} catch (IOException e) {
			Log.i(TAG, e.getMessage());
		}
	}

	public void deleteAllLocalVariables(Scene scene, Sprite sprite) {
		if (scene == null || sprite == null) {
			return;
		}

		for (UserVariable userVariable: scene.getDataContainer().getSpriteUserVariables(sprite)) {
			if (userVariable.getDeviceValueFileName() == null) {
				continue;
			}

			synchronized (lock) {
				File file = new File(projectDeviceVariablesFolder, userVariable.getDeviceValueFileName());
				FileUtils.deleteFile(file);
			}
		}
	}

	public void deleteAllVariables() {
		try {
			synchronized (lock) {
				StorageOperations.deleteDir(projectDeviceVariablesFolder);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void renameProject(String newName) {
		if (!projectDeviceVariablesFolder.exists()) {
			return;
		}

		synchronized (lock) {
			File file = new File(DEVICE_VARIABLE_DIR, newName);
			if (!projectDeviceVariablesFolder.renameTo(file)) {
				Log.e(TAG, "Not able to rename project to " + newName);
			}
		}
	}

	public void copyDeviceVariables(String destinationName) throws IOException {
		if (!projectDeviceVariablesFolder.exists()) {
			return;
		}

		File destinationFolder = new File(DEVICE_VARIABLE_DIR, destinationName);
		if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
			destinationFolder.mkdirs();
		}

		synchronized (lock) {
			StorageOperations.copyDir(projectDeviceVariablesFolder, destinationFolder);
		}
	}

	public void removeFile(UserVariable item) {
		if (!projectDeviceVariablesFolder.exists() || !projectDeviceVariablesFolder.isDirectory()) {
			return;
		}
		File file = new File(projectDeviceVariablesFolder, item.getDeviceValueFileName());
		synchronized (lock) {
			FileUtils.deleteFile(file);
		}
	}
}
