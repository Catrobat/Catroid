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
import org.catrobat.catroid.common.defaultprojectcreators.ArDroneProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.ChromeCastProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.DefaultProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.JumpingSumoProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.ProjectCreator;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.StringFinder;

import java.io.IOException;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public final class DefaultProjectHandler {

	public static final String TAG = DefaultProjectHandler.class.getSimpleName();

	private final Context context;
	private final XstreamSerializer xstreamSerializer;

	public enum ProjectCreatorType {
		PROJECT_CREATOR_DEFAULT,
		PROJECT_CREATOR_DRONE,
		PROJECT_CREATOR_CAST,
		PROJECT_CREATOR_JUMPING_SUMO
	}

	private ProjectCreator defaultProjectCreator;

	public DefaultProjectHandler(Context context, XstreamSerializer xstreamSerializer) {
		this.context = context;
		this.xstreamSerializer = xstreamSerializer;
		setDefaultProjectCreator(ProjectCreatorType.PROJECT_CREATOR_DEFAULT);
	}

	public Project createAndSaveDefaultProject() throws IOException {
		String name = context.getString(defaultProjectCreator.getDefaultProjectNameID());
		return createAndSaveDefaultProject(name, false);
	}

	public Project createAndSaveDefaultProject(String name, boolean landscapeMode) throws IOException {
		return defaultProjectCreator.createDefaultProject(name, context, landscapeMode);
	}

	public Project createAndSaveEmptyProject(String name, boolean landscapeMode, boolean isCastEnabled) throws IOException {
		Project project = new Project(context, name, landscapeMode, isCastEnabled);

		if (project.getDirectory().exists()) {
			throw new IOException("Cannot create new project at "
					+ project.getDirectory().getAbsolutePath()
					+ ", directory already exists.");
		}

		xstreamSerializer.saveProject(project);
		return project;
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
			case PROJECT_CREATOR_CAST:
				if (BuildConfig.FEATURE_CAST_ENABLED) {
					defaultProjectCreator = new ChromeCastProjectCreator();
				}
				break;
		}
	}

	public boolean isDefaultProject(Project projectToCheck) {
		try {
			String uniqueProjectName = "project_" + System.currentTimeMillis();

			while (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(uniqueProjectName)) {
				uniqueProjectName = "project_" + System.currentTimeMillis();
			}

			Project defaultProject = createAndSaveDefaultProject(uniqueProjectName, false);

			String defaultProjectXml = xstreamSerializer.getXmlAsStringFromProject(defaultProject);

			StorageOperations.deleteDir(defaultProject.getDirectory());

			StringFinder stringFinder = new StringFinder();

			if (!stringFinder.findBetween(defaultProjectXml, "<scenes>", "</scenes>")) {
				return false;
			}

			String defaultProjectSpriteList = stringFinder.getResult();

			ProjectSaveTask
					.task(projectToCheck, context);

			String projectToCheckXML = xstreamSerializer.getXmlAsStringFromProject(projectToCheck);

			if (!stringFinder.findBetween(projectToCheckXML, "<scenes>", "</scenes")) {
				return false;
			}

			String projectToCheckSpriteList = stringFinder.getResult();
			return defaultProjectSpriteList.contentEquals(projectToCheckSpriteList);
		} catch (IllegalArgumentException illegalArgumentException) {
			Log.e(TAG, Log.getStackTraceString(illegalArgumentException));
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
		return true;
	}
}
