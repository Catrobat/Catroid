/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.adapter.ProjectListAdapter;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.MergeNameDialog;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.utils.Utils;

public final class MergeManager {

	private MergeManager() {
	}

	public static void merge(String firstProjectName, String secondProjectName, Activity activity, ProjectListAdapter
			adapter) {
		Project firstProject = StorageHandler.getInstance().loadProject(firstProjectName, activity);
		Project secondProject = StorageHandler.getInstance().loadProject(secondProjectName, activity);

		if (firstProject == null || secondProject == null) {
			Utils.showErrorDialog(activity, R.string.error_load_project);
			return;
		}

		boolean justAddAsScene = firstProject.getSceneList().size() == 1 ^ secondProject.getSceneList().size() == 1;
		showMergeDialog(firstProject, secondProject, activity, adapter, justAddAsScene);
	}

	private static void showMergeDialog(Project firstProject, Project secondProject, Activity activity,
			ProjectListAdapter adapter, boolean addScene) {
		XmlHeader firstHeader = firstProject.getXmlHeader();
		XmlHeader secondHeader = secondProject.getXmlHeader();
		boolean areScreenSizesDifferent = firstHeader.getVirtualScreenHeight() != secondHeader.getVirtualScreenHeight()
				|| firstHeader.getVirtualScreenWidth() != secondHeader.getVirtualScreenWidth();

		if (areScreenSizesDifferent) {
			showDifferentResolutionDialog(firstProject, secondProject, activity, adapter, addScene);
		} else {
			MergeTask merge = new MergeTask(firstProject, secondProject, activity, adapter, addScene);
			MergeNameDialog mergeDialog = new MergeNameDialog(merge);

			mergeDialog.show(activity.getFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
		}
	}

	public static boolean mergeScene(String firstSceneName, String secondSceneName, String resultName, Activity activity) {
		Scene firstScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(firstSceneName);
		Scene secondScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(secondSceneName);

		if (firstScene == null || secondScene == null) {
			Utils.showErrorDialog(activity, R.string.error_merge_scene_not_found);
			return false;
		}

		if (firstScene.getName().equals(secondScene.getName())) {
			Utils.showErrorDialog(activity, R.string.error_merge_with_self_scene);
			return false;
		}

		MergeTask merge = new MergeTask(firstScene, secondScene, activity);
		if (!merge.mergeScenes(resultName)) {
			Utils.showErrorDialog(activity, R.string.merge_conflict);
		}

		return true;
	}

	private static void showDifferentResolutionDialog(final Project firstProject, final Project secondProject,
			final Activity activity, final ProjectListAdapter adapter, final boolean addScene) {

		XmlHeader currentProject = firstProject.getXmlHeader();
		XmlHeader headerFrom = secondProject.getXmlHeader();

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						MergeTask merge = new MergeTask(firstProject, secondProject, activity, adapter, addScene);
						MergeNameDialog mergeDialog = new MergeNameDialog(merge);

						mergeDialog.show(activity.getFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
						break;
				}
			}
		};
		String msg = String.format(activity.getString(R.string.error_different_resolutions),
				currentProject.getProgramName(), currentProject.getVirtualScreenHeight(),
				currentProject.getVirtualScreenWidth(), headerFrom.getProgramName(),
				headerFrom.getVirtualScreenHeight(), headerFrom.getVirtualScreenWidth());

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(activity);
		builder.setTitle(R.string.warning);
		builder.setMessage(msg);
		builder.setPositiveButton(activity.getString(R.string.main_menu_continue), dialogClickListener);
		builder.setNegativeButton(activity.getString(R.string.abort), dialogClickListener);
		Dialog errorDialog = builder.create();
		errorDialog.show();
	}
}
