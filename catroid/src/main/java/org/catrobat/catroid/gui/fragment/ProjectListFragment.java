/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.gui.fragment;

import android.content.Intent;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.gui.activity.SceneListActivity;
import org.catrobat.catroid.gui.adapter.RecyclerViewAdapter;
import org.catrobat.catroid.gui.dialog.RenameItemDialog;
import org.catrobat.catroid.projecthandler.ProjectCreator;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.storage.DirectoryPathInfo;
import org.catrobat.catroid.storage.StorageManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectListFragment extends RecyclerViewListFragment<ProjectInfo> {

	public static final String TAG = ProjectListFragment.class.getSimpleName();

	@Override
	protected RecyclerViewAdapter<ProjectInfo> createAdapter() {
		List<ProjectInfo> projects = new ArrayList<>();

		try {
			for (String name : StorageManager.getProjectNames()) {
				projects.add(ProjectHolder.getInstance().deserialize(name));
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return new RecyclerViewAdapter<ProjectInfo>(projects) {

			@Override
			public void updateProject() {
			}
		};
	}

	@Override
	protected Class getItemType() {
		return ProjectInfo.class;
	}

	@Override
	protected DirectoryPathInfo getCurrentDirectory() {
		return StorageManager.getProjectsDirectory();
	}

	@Override
	public void addItem(String name) {
		try {
			ProjectInfo project = ProjectCreator.createDefaultProject(name, getActivity());
			adapter.addItem(project);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	public void onItemClick(ProjectInfo item) {
		try {
			ProjectInfo project = ProjectHolder.getInstance().deserialize(item.getName());
			ProjectHolder.getInstance().setCurrentProject(project);

			Intent intent = new Intent(getActivity(), SceneListActivity.class);
			startActivity(intent);
		} catch (FileNotFoundException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	protected void copyItems(List<ProjectInfo> items) {
		actionMode.finish();
		try {
			for (ProjectInfo project : items) {
				ProjectInfo clone = new ProjectInfo(getUniqueItemName(project.getName()));

				for (SceneInfo scene : project.getScenes()) {
					clone.addScene(scene.clone());
				}

				clone.copyResourcesToDirectory(clone.getDirectoryInfo());
				ProjectHolder.getInstance().serialize(clone);

				adapter.addItem(clone);
			}
		} catch (IOException e) {
			Log.e(TAG, "Cannot create project folder: " + Log.getStackTraceString(e));
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, "Cannot clone scene in project" + Log.getStackTraceString(e));
		}
	}

	@Override
	protected void showRenameDialog(String name) {
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_project, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}
}
