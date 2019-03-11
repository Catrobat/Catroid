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

package org.catrobat.catroid.ui.recyclerview.controller;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.backwardcompatibility.LegacyProjectWithoutScenes;
import org.catrobat.catroid.content.backwardcompatibility.ProjectUntilLanguageVersion0999;
import org.catrobat.catroid.content.backwardcompatibility.SceneUntilLanguageVersion0999;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.utils.PathBuilder;

import java.io.File;
import java.io.IOException;

public class ProjectController {

	public Project createProject(LegacyProjectWithoutScenes legacyProject, Context context) {
		Project project = new Project();
		project.setXmlHeader(legacyProject.getXmlHeader());
		project.getSettings().addAll(legacyProject.getSettings());

		project.getUserVariables().addAll(legacyProject.getProjectUserVariables());
		project.getUserLists().addAll(legacyProject.getProjectUserLists());

		for (Sprite sprite : legacyProject.getSpriteList()) {
			sprite.getUserVariables().addAll(legacyProject.getSpriteUserVariables(sprite));
			sprite.getUserLists().addAll(legacyProject.getSpriteUserLists(sprite));
		}

		Scene scene = new Scene(context.getString(R.string.default_scene_name, 1), project);
		scene.getSpriteList().addAll(legacyProject.getSpriteList());
		project.addScene(scene);
		return project;
	}

	public Project createProject(ProjectUntilLanguageVersion0999 legacyProject) {
		Project project = new Project();
		project.setXmlHeader(legacyProject.getXmlHeader());
		project.getSettings().addAll(legacyProject.getSettings());

		project.getUserVariables().addAll(legacyProject.getUserVariables());
		project.getUserLists().addAll(legacyProject.getUserLists());

		for (SceneUntilLanguageVersion0999 legacyScene : legacyProject.getSceneList()) {
			Scene scene = new Scene(legacyScene.getName(), project);

			for (Sprite sprite : legacyScene.getSpriteList()) {
				sprite.getUserVariables().addAll(legacyScene.getSpriteUserVariables(sprite));
				sprite.getUserLists().addAll(legacyScene.getSpriteUserLists(sprite));
				scene.addSprite(sprite);
			}

			project.addScene(scene);
		}

		return project;
	}

	public void delete(ProjectData projectToDelete) throws IOException {
		StorageOperations.deleteDir(new File(PathBuilder.buildProjectPath(projectToDelete.projectName)));
	}
}
