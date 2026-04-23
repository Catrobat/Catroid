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

package org.catrobat.catroid.uiespresso.util;

import android.app.Instrumentation;
import android.os.ParcelFileDescriptor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemAnimations {

	private static final List<String> ANIMATION_PROPERTIES;
	static {
		ANIMATION_PROPERTIES = Collections.unmodifiableList(Arrays.asList(
				"window_animation_scale", "transition_animation_scale",
						"animator_duration_scale"));
	}

	//DISABLED would be 0.0
	private static final String DEFAULT = "1.0";
	private static final String UNSET = "null";

	private final Instrumentation instrumentation;

	private Map<String, String> storedAnimationSettings = new HashMap<>();

	public SystemAnimations(final Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	public void enableAll() throws IOException {
		setSystemAnimationsScale(DEFAULT);
	}

	public void storeCurrentSettings() throws IOException {
		storedAnimationSettings.clear();
		for (String property : ANIMATION_PROPERTIES) {
			final String val = getGlobalSetting(property);
			if (isFloat(val) || UNSET.equals(val)) {
				storedAnimationSettings.put(property, val);
			}
		}
	}

	public void resetToStoredSettings() throws IOException {
		for (String property : ANIMATION_PROPERTIES) {
			final String value = storedAnimationSettings.get(property);
			if (UNSET.equals(value)) {
				deleteGlobalSetting(property);
			} else if (value != null) {
				setGlobalSetting(property, value);
			}
		}
	}

	private void setSystemAnimationsScale(String animationScale) throws IOException {
		for (String property : ANIMATION_PROPERTIES) {
			setGlobalSetting(property, animationScale);
		}
	}

	private String getGlobalSetting(final String key) throws IOException {
		return executeShellCommand("settings get global " + key).trim();
	}

	private void setGlobalSetting(final String key, final String value) throws IOException {
		executeShellCommand("settings put global " + key + " " + value);
	}

	private void deleteGlobalSetting(final String key) throws IOException {
		executeShellCommand("settings delete global " + key);
	}

	/**
	 * Executes a shell command via the UIAutomator.
	 * @param command the command to execute
	 * @return the STDOUT of the command as String
	 * @throws IOException if the command can't be executed or the output could not be parsed
	 */
	private String executeShellCommand(final String command) throws IOException {
		StringBuilder cmdOutput = new StringBuilder();

		try (ParcelFileDescriptor pfd = instrumentation.getUiAutomation()
				.executeShellCommand(command);
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(pfd.getFileDescriptor())))) {

			final String line = reader.readLine();
			if (line != null) {
				cmdOutput.append(line);
			}
		}

		return cmdOutput.toString();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private boolean isFloat(final String valueToCheck) {
		boolean isFloat = true;
		try {
			Float.parseFloat(valueToCheck);
		} catch (NumberFormatException nfe) {
			isFloat = false;
		}
		return isFloat;
	}
}
