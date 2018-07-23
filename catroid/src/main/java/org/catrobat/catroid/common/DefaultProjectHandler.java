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
package org.catrobat.catroid.common;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.defaultprojectcreators.ArDroneProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.ChromeCastProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.DefaultProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.JumpingSumoProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.PhysicsProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.ProjectCreator;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.PathBuilder;

import java.io.File;
import java.io.IOException;

public final class DefaultProjectHandler {

	private static final String TAG = DefaultProjectHandler.class.getSimpleName();

	public enum ProjectCreatorType {
		PROJECT_CREATOR_DEFAULT, PROJECT_CREATOR_DRONE, PROJECT_CREATOR_PHYSICS, PROJECT_CREATOR_CAST,
		PROJECT_CREATOR_JUMPING_SUMO
	}

	private static DefaultProjectHandler instance = null;
	private ProjectCreator defaultProjectCreator;

	public static DefaultProjectHandler getInstance() {
		if (instance == null) {
			instance = new DefaultProjectHandler();
		}
		return instance;
	}

	private DefaultProjectHandler() {
		setDefaultProjectCreator(ProjectCreatorType.PROJECT_CREATOR_DEFAULT);
	}

	public static Project createAndSaveDefaultProject(Context context) throws IOException {
		String projectName = context.getString(getInstance().defaultProjectCreator.getDefaultProjectNameID());
		Project defaultProject = null;

		if (XstreamSerializer.getInstance().projectExists(projectName)) {
			StorageOperations.deleteDir(new File(PathBuilder.buildProjectPath(projectName)));
		}

		try {
			defaultProject = createAndSaveDefaultProject(projectName, context, false);
		} catch (IllegalArgumentException ilArgument) {
			Log.e(TAG, "Could not create standard project!", ilArgument);
		}

		return defaultProject;
	}

	public static Project createAndSaveDefaultProject(String projectName, Context context, boolean landscapeMode)
			throws IOException, IllegalArgumentException {
		return getInstance().defaultProjectCreator.createDefaultProject(projectName, context, landscapeMode);
	}

	public static Project createAndSaveDefaultProject(String projectName, Context context) throws
			IOException, IllegalArgumentException {
		return createAndSaveDefaultProject(projectName, context, false);
	}

	public static Project createAndSaveEmptyProject(String projectName, Context context, boolean landscapeMode,
			boolean isCastEnabled) {
		if (XstreamSerializer.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		Project emptyProject = new Project(context, projectName, landscapeMode, isCastEnabled);
		emptyProject.setDeviceData(context);
		XstreamSerializer.getInstance().saveProject(emptyProject);
		ProjectManager.getInstance().setProject(emptyProject);

		return emptyProject;
	}

	public void setDefaultProjectCreator(ProjectCreatorType type) {
		switch (type) {
			case PROJECT_CREATOR_DEFAULT:
				defaultProjectCreator = new DefaultProjectCreator();
				break;
			case PROJECT_CREATOR_DRONE:
				if (BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
					defaultProjectCreator = new ArDroneProjectCreator();
				} else {
					defaultProjectCreator = new DefaultProjectCreator();
				}
				break;
			case PROJECT_CREATOR_JUMPING_SUMO:
				if (BuildConfig.FEATURE_PARROT_JUMPING_SUMO_ENABLED) {
					defaultProjectCreator = new JumpingSumoProjectCreator();
				} else {
					defaultProjectCreator = new DefaultProjectCreator();
				}
				break;
			case PROJECT_CREATOR_PHYSICS:
				defaultProjectCreator = new PhysicsProjectCreator();
				break;
			case PROJECT_CREATOR_CAST:
				if (BuildConfig.FEATURE_CAST_ENABLED) {
					defaultProjectCreator = new ChromeCastProjectCreator();
				}
				break;
		}
	}
}
