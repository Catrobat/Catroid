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

package org.catrobat.catroid.ui.controller;

import android.content.Context;
import android.content.Intent;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.transfers.project.ProjectUploadService;
import org.catrobat.catroid.transfers.project.ResultReceiverWrapper;

import static org.catrobat.catroid.common.Constants.EXTRA_PROJECT_DESCRIPTION;
import static org.catrobat.catroid.common.Constants.EXTRA_PROJECT_PATH;
import static org.catrobat.catroid.common.Constants.EXTRA_PROVIDER;
import static org.catrobat.catroid.common.Constants.EXTRA_RESULT_RECEIVER;
import static org.catrobat.catroid.common.Constants.EXTRA_SCENE_NAMES;
import static org.catrobat.catroid.common.Constants.EXTRA_UPLOAD_NAME;
import static org.catrobat.catroid.common.Constants.NO_OAUTH_PROVIDER;

public class ProjectUploadController {

	public interface ProjectUploadInterface {
		ResultReceiverWrapper getResultReceiverWrapper();
		Context getContext();
		void startUploadService(Intent intent);
	}

	private Context context;
	private ProjectUploadInterface projectUploadInterface;

	public ProjectUploadController(ProjectUploadInterface newProjectUploadInterface) {
		projectUploadInterface = newProjectUploadInterface;
		context = projectUploadInterface.getContext();
	}

	private Intent createUploadIntent(String projectName, String projectDescription, Project project) {
		Intent intent = new Intent(context, ProjectUploadService.class);

		String[] sceneNames = project.getSceneNames().toArray(new String[0]);
		ResultReceiverWrapper resultReceiverWrapper = projectUploadInterface.getResultReceiverWrapper();
		intent.putExtra(EXTRA_RESULT_RECEIVER, resultReceiverWrapper);

		intent.putExtra(EXTRA_UPLOAD_NAME, projectName);
		intent.putExtra(EXTRA_PROJECT_DESCRIPTION, projectDescription);
		intent.putExtra(EXTRA_PROJECT_PATH, project.getDirectory().getAbsolutePath());
		intent.putExtra(EXTRA_PROVIDER, NO_OAUTH_PROVIDER);
		intent.putExtra(EXTRA_SCENE_NAMES, sceneNames);

		return intent;
	}

	public void startUpload(String projectName, String projectDescription,
			String projectNotesAndCredits, Project project) {
		project.setDescription(projectDescription);
		project.setNotesAndCredits(projectNotesAndCredits);
		project.setDeviceData(context);
		project.setListeningLanguageTag();
		ProjectSaveTask.task(project, context);

		Intent uploadIntent = createUploadIntent(projectName, projectDescription, project);
		projectUploadInterface.startUploadService(uploadIntent);
	}
}
