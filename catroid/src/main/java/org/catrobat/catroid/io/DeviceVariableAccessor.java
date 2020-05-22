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

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.DEVICE_VARIABLE_JSON_FILENAME;

public final class DeviceVariableAccessor extends DeviceUserDataAccessor {

	private static final Object LOCK = new Object();

	public DeviceVariableAccessor(File projectDirectory) {
		super(projectDirectory);
	}

	@Override
	protected Object getLock() {
		return LOCK;
	}

	@Override
	protected String getDeviceFileName() {
		return DEVICE_VARIABLE_JSON_FILENAME;
	}

	@Override
	public List<? extends UserData> getUserData(Sprite sprite) {
		return sprite.getUserVariables();
	}

	@Override
	public List<? extends UserData> getUserData(Project project) {
		List<UserVariable> projectVariables = new ArrayList<>();
		projectVariables.addAll(project.getUserVariables());
		projectVariables.addAll(project.getMultiplayerVariables());
		return projectVariables;
	}
}
