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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.fragment.ProjectDetailsFragment;
import org.catrobat.catroid.ui.recyclerview.adapter.ProjectAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.ViewHolder;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectCopyTask;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectCreatorTask;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.recyclerview.controller.ProjectController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewProjectDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameItemDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectListFragment extends RecyclerViewFragment<ProjectData> implements
		ProjectCreatorTask.ProjectCreatorListener,
		ProjectLoaderTask.ProjectLoaderListener,
		ProjectCopyTask.ProjectCopyListener {

	public static final String TAG = ProjectListFragment.class.getSimpleName();

	private ProjectController projectController = new ProjectController();

	@Override
	public void onResume() {
		adapter.setItems(getItemList());
		BottomBar.showBottomBar(getActivity());
		super.onResume();
	}

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = "showDetailsProjectList";
		hasDetails = true;
		adapter = new ProjectAdapter(getItemList());
		onAdapterReady();
	}

	private List<ProjectData> getItemList() {
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		List<ProjectData> items = new ArrayList<>();

		for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
			File codeFile = new File(Utils.buildPath(Utils.buildProjectPath(projectName), Constants.PROJECTCODE_NAME));
			items.add(new ProjectData(projectName, codeFile.lastModified()));
		}

		Collections.sort(items, new Comparator<ProjectData>() {
			@Override
			public int compare(ProjectData project1, ProjectData project2) {
				return Long.valueOf(project2.lastUsed).compareTo(project1.lastUsed);
			}
		});

		return items;
	}

	@Override
	public void handleAddButton() {
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.show(getFragmentManager(), NewProjectDialog.TAG);
	}

	@Override
	public void addItem(ProjectData item) {
		// This is handled through the NewProjectDialog.
	}

	@Override
	protected void prepareActionMode(@ActionModeType int type) {
		if (type == COPY) {
			adapter.allowMultiSelection = false;
		}
		super.prepareActionMode(type);
	}

	@Override
	protected void packItems(List<ProjectData> selectedItems) {
		throw new IllegalStateException(TAG + ": Projects cannot be backpacked");
	}

	@Override
	protected boolean isBackpackEmpty() {
		return true;
	}

	@Override
	protected void switchToBackpack() {
		throw new IllegalStateException(TAG + ": Projects cannot be backpacked");
	}

	@Override
	protected void copyItems(List<ProjectData> selectedItems) {
		finishActionMode();
		setShowProgressBar(true);
		ProjectCopyTask copyTask = new ProjectCopyTask(getActivity(), this);
		String name = uniqueNameProvider.getUniqueName(selectedItems.get(0).projectName, getScope());
		copyTask.execute(selectedItems.get(0).projectName, name);
	}

	@Override
	protected int getDeleteAlertTitle() {
		return R.plurals.delete_projects;
	}

	@Override
	protected void deleteItems(List<ProjectData> selectedItems) {
		finishActionMode();
		for (ProjectData item : selectedItems) {
			try {
				projectController.delete(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}

		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_projects,
				selectedItems.size(),
				selectedItems.size()));

		adapter.setItems(getItemList());

		if (adapter.getItems().isEmpty()) {
			setShowProgressBar(true);
			ProjectCreatorTask creatorTask = new ProjectCreatorTask(getActivity(), this);
			creatorTask.execute();
		}
	}

	@Override
	protected void showRenameDialog(List<ProjectData> selectedItems) {
		String name = selectedItems.get(0).projectName;
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_project, R.string.project_name_label, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}

	private Set<String> getScope() {
		Set<String> scope = new HashSet<>();
		for (ProjectData item : adapter.getItems()) {
			scope.add(item.projectName);
		}
		return scope;
	}

	@Override
	public boolean isNameUnique(String name) {
		return !getScope().contains(name);
	}

	@Override
	public void renameItem(String name) {
		ProjectManager projectManager = ProjectManager.getInstance();

		if (!name.equals(adapter.getSelectedItems().get(0).projectName)) {
			try {
				projectManager.loadProject(adapter.getSelectedItems().get(0).projectName, getActivity());
				projectManager.renameProject(name, getActivity());
				projectManager.loadProject(name, getActivity());
			} catch (ProjectException e) {
				Log.e(TAG, Log.getStackTraceString(e));
				Utils.showErrorDialog(getActivity(), R.string.error_rename_incompatible_project);
			}
		}

		finishActionMode();
		adapter.setItems(getItemList());
	}

	@Override
	public void onCreateFinished(boolean success) {
		if (success) {
			updateAdapter();
		} else {
			ToastUtil.showError(getActivity(), R.string.wtf_error);
			getActivity().finish();
		}
	}

	private void updateAdapter() {
		adapter.setItems(getItemList());
		setShowProgressBar(false);
	}

	@Override
	public void onLoadFinished(boolean success, String message) {
		if (success) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			startActivity(intent);
		} else {
			setShowProgressBar(false);
			ToastUtil.showError(getActivity(), message);
		}
	}

	@Override
	public void onCopyFinished(boolean success) {
		if (success) {
			adapter.setItems(getItemList());
		} else {
			ToastUtil.showError(getActivity(), R.string.error_copy_project);
		}
		setShowProgressBar(false);
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_projects_title,
				selectedItemCnt,
				selectedItemCnt));
	}

	@Override
	public void onItemClick(ProjectData item) {
		if (actionModeType == NONE) {
			ProjectLoaderTask loaderTask = new ProjectLoaderTask(getActivity(), this);
			setShowProgressBar(true);
			loaderTask.execute(item.projectName);
		}
	}

	@Override
	public void onItemLongClick(final ProjectData item, ViewHolder holder) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		CharSequence[] items = new CharSequence[] {
				getString(R.string.copy),
				getString(R.string.delete),
				getString(R.string.rename),
				getString(R.string.show_details),
				getString(R.string.upload_button)
		};

		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						copyItems(new ArrayList<>(Collections.singletonList(item)));
						break;
					case 1:
						showDeleteAlert(new ArrayList<>(Collections.singletonList(item)));
						break;
					case 2:
						adapter.setSelection(item, true);
						showRenameDialog(adapter.getSelectedItems());
						break;
					case 3:
						ProjectDetailsFragment fragment = new ProjectDetailsFragment();
						Bundle args = new Bundle();
						args.putSerializable(ProjectDetailsFragment.SELECTED_PROJECT_KEY, item);
						fragment.setArguments(args);
						getFragmentManager().beginTransaction()
								.replace(R.id.fragment_container, fragment, ProjectDetailsFragment.TAG)
								.addToBackStack(ProjectDetailsFragment.TAG)
								.commit();
						break;
					case 4:
						ProjectManager.getInstance().uploadProject(item.projectName, getActivity());
						break;
					default:
						dialog.dismiss();
				}
			}
		});

		builder.setTitle(item.projectName);
		builder.setCancelable(true);
		builder.show();
	}
}
