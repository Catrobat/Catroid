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
package org.catrobat.catroid.common;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.standardprojectcreators.StandardProjectCreator;
import org.catrobat.catroid.common.standardprojectcreators.StandardProjectCreatorDefault;
import org.catrobat.catroid.common.standardprojectcreators.StandardProjectCreatorDrone;
import org.catrobat.catroid.common.standardprojectcreators.StandardProjectCreatorPhysics;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;

import java.io.IOException;

public final class StandardProjectHandler {

	private static final String TAG = StandardProjectHandler.class.getSimpleName();

	public enum StandardProjectCreatorType {
		STANDARD_PROJECT_CREATOR_DEFAULT, STANDARD_PROJECT_CREATOR_DRONE, STANDARD_PROJECT_CREATOR_PHYSICS
	}

	private static StandardProjectHandler instance = null;
	private StandardProjectCreator standardProjectCreator;

	public static StandardProjectHandler getInstance() {
		if (instance == null) {
			instance = new StandardProjectHandler();
		}
		return instance;
	}

	private StandardProjectHandler() {
		if (BuildConfig.BUILD_TYPE == BuildConfig.BUILD_TYPE_PHYSICS) {
			setStandardProjectCreator(StandardProjectCreatorType.STANDARD_PROJECT_CREATOR_PHYSICS);
		} else {
			setStandardProjectCreator(StandardProjectCreatorType.STANDARD_PROJECT_CREATOR_DEFAULT);
		}
	}

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(getInstance().standardProjectCreator.getStandardProjectNameID());
		Project standardProject = null;

		if (StorageHandler.getInstance().projectExists(projectName)) {
			StorageHandler.getInstance().deleteProject(projectName);
		}

		try {
			standardProject = createAndSaveStandardProject(projectName, context);
		} catch (IllegalArgumentException ilArgument) {
			Log.e(TAG, "Could not create standard project!", ilArgument);
		}

		return standardProject;
	}

	public static Project createAndSaveStandardProject(String projectName, Context context) throws IOException,
			IllegalArgumentException {
		return getInstance().standardProjectCreator.createStandardProject(projectName, context);
	}

	public static Project createAndSaveEmptyProject(String projectName, Context context) {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		Project emptyProject = new Project(context, projectName);
		emptyProject.setDeviceData(context);
		StorageHandler.getInstance().saveProject(emptyProject);
		ProjectManager.getInstance().setProject(emptyProject);

		return emptyProject;
	}

	public void setStandardProjectCreator(StandardProjectCreatorType type) {
		switch (type) {
			case STANDARD_PROJECT_CREATOR_DEFAULT:
				standardProjectCreator = new StandardProjectCreatorDefault();
				break;
			case STANDARD_PROJECT_CREATOR_DRONE:
				if (BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
					standardProjectCreator = new StandardProjectCreatorDrone();
				} else {
					standardProjectCreator = new StandardProjectCreatorDefault();
				}
				break;
			case STANDARD_PROJECT_CREATOR_PHYSICS:
				standardProjectCreator = new StandardProjectCreatorPhysics();
				break;
		}
	}
}
