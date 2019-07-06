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
package org.catrobat.catroid.merge;

import android.content.Context;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.XstreamSerializer;

public class MergeTask {
	private Project firstProject = null;
	private Project secondProject = null;
	private Project mergedProject = null;
	private ConflictHelper conflictHelper = new ConflictHelper();
	private Context context;
	private boolean landscapeMode;

	public MergeTask(Project firstProject, Project secondProject, Context context, boolean landscapeMode) {
		this.firstProject = firstProject;
		this.secondProject = secondProject;
		this.context = context;
		this.landscapeMode = landscapeMode;
	}

	public boolean performMerge(String mergeProjectName) {
		if (conflictHelper.conflictExist(firstProject, secondProject)) {
			return false;
		}

		mergedProject = new Project(context, mergeProjectName, landscapeMode);

		if (mergedProject.getDefaultScene() != null) {
			mergedProject.removeScene(mergedProject.getDefaultScene());
		}

		mergedProject.setXmlHeader(firstProject.getXmlHeader());

		for (String sceneName : firstProject.getSceneNames()) {
			if (secondProject.getSceneNames().contains(sceneName)) {
				return false;
			}
		}

		mergedProject.getSceneList().addAll(firstProject.getSceneList());
		mergedProject.getSceneList().addAll(secondProject.getSceneList());
		mergedProject.getUserVariables().addAll(firstProject.getUserVariables());
		mergedProject.getUserVariables().addAll(secondProject.getUserVariables());
		mergedProject.getUserLists().addAll(firstProject.getUserLists());
		mergedProject.getUserLists().addAll(secondProject.getUserLists());
		mergedProject.getSettings().addAll(firstProject.getSettings());

		return XstreamSerializer.getInstance().saveProject(mergedProject) ? true : false;
	}
}
