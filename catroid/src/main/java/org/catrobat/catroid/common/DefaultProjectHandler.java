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
package org.catrobat.catroid.common;

import android.content.Context;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.common.defaultprojectcreators.ChromeCastProjectCreator;
import org.catrobat.catroid.common.defaultprojectcreators.DefaultExampleProject;
import org.catrobat.catroid.common.defaultprojectcreators.ProjectCreator;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.XstreamSerializer;

import java.io.IOException;

public final class DefaultProjectHandler {

	public enum ProjectCreatorType {
		PROJECT_CREATOR_DEFAULT,
		PROJECT_CREATOR_CAST
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
		String name = context.getString(getInstance().defaultProjectCreator.getDefaultProjectNameID());
		return createAndSaveDefaultProject(name, context, false);
	}

	public static Project createAndSaveDefaultProject(String name, Context context, boolean landscapeMode) throws IOException {
		return getInstance().defaultProjectCreator.createDefaultProject(name, context, landscapeMode);
	}

	public static Project createAndSaveEmptyProject(String name, Context context, boolean landscapeMode, boolean isCastEnabled) throws IOException {
		Project project = new Project(context, name, landscapeMode, isCastEnabled);

		if (project.getDirectory().exists()) {
			throw new IOException("Cannot create new project at "
					+ project.getDirectory().getAbsolutePath()
					+ ", directory already exists.");
		}

		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}

	public void setDefaultProjectCreator(ProjectCreatorType type) {
		switch (type) {
			case PROJECT_CREATOR_DEFAULT:
				defaultProjectCreator = new DefaultExampleProject();
				break;
			case PROJECT_CREATOR_CAST:
				if (BuildConfig.FEATURE_CAST_ENABLED) {
					defaultProjectCreator = new ChromeCastProjectCreator();
				}
				break;
		}
	}
}
