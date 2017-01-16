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
package org.catrobat.catroid.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.merge.MergeManager;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.ui.SceneListActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.ProjectListAdapter;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectListFragment extends ListActivityFragment implements LoadProjectTask.OnLoadProjectCompleteListener,
		CheckBoxListAdapter.ListItemClickHandler<ProjectData>, CheckBoxListAdapter.ListItemLongClickHandler,
		SetDescriptionDialog.ChangeDescriptionInterface, NewProjectDialog.LoadNewProjectInterface {

	public static final String TAG = ProjectListFragment.class.getSimpleName();
	public static final String SHARED_PREFERENCE_NAME = "showProjectDetails";
	private static final String BUNDLE_ARGUMENTS_PROJECT_DATA = "project_data";

	private ProjectListAdapter projectAdapter;
	private ListView listView;

	private List<ProjectData> projectList;
	private ProjectData projectToEdit;
	private int selectedProjectPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View projectListFragment = inflater.inflate(R.layout.fragment_project_list, container, false);
		listView = (ListView) projectListFragment.findViewById(android.R.id.list);

		BottomBar.showBottomBar(getActivity());

		return projectListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());
		itemIdentifier = R.plurals.programs;
		deleteDialogTitle = R.plurals.dialog_delete_program;

		if (savedInstanceState != null) {
			projectToEdit = (ProjectData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_PROJECT_DATA);
		}

		initializeList();
	}

	public void initializeList() {
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		File projectCodeFile;
		projectList = new ArrayList<>();
		for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
			projectCodeFile = new File(Utils.buildPath(Utils.buildProjectPath(projectName), Constants.PROJECTCODE_NAME));
			projectList.add(new ProjectData(projectName, projectCodeFile.lastModified()));
		}
		Collections.sort(projectList, new Comparator<ProjectData>() {
			@Override
			public int compare(ProjectData project1, ProjectData project2) {
				return Long.valueOf(project2.lastUsed).compareTo(project1.lastUsed);
			}
		});

		projectAdapter = new ProjectListAdapter(getActivity(), R.layout.list_item, projectList);

		setListAdapter(projectAdapter);
		projectAdapter.setListItemClickHandler(this);
		projectAdapter.setListItemLongClickHandler(this);
		projectAdapter.setListItemCheckHandler(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_PROJECT_DATA, projectToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		initializeList();
		loadShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		putShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void handleAddButton() {
		String defaultProjectName = Utils.getUniqueProjectName(getString(R.string.new_project_dialog_hint));
		NewProjectDialog dialog = new NewProjectDialog(defaultProjectName, this);
		dialog.show(getFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void loadNewProject(String projectName) {
		LoadProjectTask loadProjectTask = new LoadProjectTask(getActivity(), projectName, true, true);
		loadProjectTask.setOnLoadProjectCompleteListener(this);
		setProgressCircleVisibility(true);
		loadProjectTask.execute();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		projectToEdit = projectAdapter.getItem(selectedProjectPosition);

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null) {
			try {
				ProjectManager.getInstance().loadProject(projectToEdit.projectName, getActivity());
			} catch (LoadingProjectException loadingProjectException) {
				Log.e(TAG, "Project cannot load", loadingProjectException);
				ToastUtil.showError(getActivity(), R.string.error_load_project);
			} catch (OutdatedVersionProjectException outdatedVersionException) {
				Log.e(TAG, "Project Code version is outdated", outdatedVersionException);
				ToastUtil.showError(getActivity(), R.string.error_outdated_pocketcode_version);
			} catch (CompatibilityProjectException compatibilityException) {
				Log.e(TAG, "Project is not compatible", compatibilityException);
				ToastUtil.showError(getActivity(), R.string.error_project_compatability);
			}
		}

		boolean isCurrentProject = ProjectManager.getInstance().getCurrentProject().getName().equals(projectToEdit
				.projectName);
		if (!isCurrentProject) {
			menu.add(0, R.string.merge_button, 1, getString(R.string.merge_button) + ": " + ProjectManager.getInstance().getCurrentProject().getName());
		}
		menu.setHeaderTitle(projectToEdit.projectName);

		getActivity().getMenuInflater().inflate(R.menu.context_menu_project_list, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete:
				showDeleteDialog();
				break;
			case R.id.copy:
				showCopyProjectDialog();
				break;
			case R.id.rename:
				showRenameDialog();
				break;
			case R.id.show_details:
				showDetailsFragment();
				break;
			case R.id.set_description:
				showSetDescriptionDialog();
				break;
			case R.id.upload:
				ProjectManager.getInstance().uploadProject(projectToEdit.projectName, getActivity());
				break;
			case R.string.merge_button:
				String firstProjectName = ProjectManager.getInstance().getCurrentProject().getName();
				String secondProjectName = projectToEdit.projectName;

				MergeManager.merge(firstProjectName, secondProjectName, getActivity(), projectAdapter);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return true;
	}

	@Override
	public void handleOnItemClick(int position, View view, ProjectData listItem) {
		LoadProjectTask loadProjectTask = new LoadProjectTask(getActivity(), listItem.projectName, true, false);
		loadProjectTask.setOnLoadProjectCompleteListener(this);
		setProgressCircleVisibility(true);
		loadProjectTask.execute();
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		selectedProjectPosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	public int getCheckedItemCount() {
		int checkedItems = projectAdapter.getCheckedItems().size();
		boolean fromContextMenu = checkedItems == 0;
		return fromContextMenu ? 1 : checkedItems;
	}

	@Override
	public void deleteCheckedItems() {
		if (projectAdapter.getCheckedItems().isEmpty()) {
			deleteProject();
		} else {
			for (ProjectData projectData : projectAdapter.getCheckedItems()) {
				projectToEdit = projectData;
				deleteProject();
			}
		}

		if (projectList.isEmpty()) {
			initializeDefaultProjectAfterDelete();
		} else if (ProjectManager.getInstance().getCurrentProject() == null) {
			Utils.saveToPreferences(getActivity().getApplicationContext(), Constants.PREF_PROJECTNAME_KEY,
					projectList.get(0).projectName);
		}
	}

	private void deleteProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		try {
			projectManager.deleteProject(projectToEdit.projectName, getActivity().getApplicationContext());
			projectList.remove(projectToEdit);
		} catch (IOException exception) {
			Log.e(TAG, "Project could not be deleted", exception);
			ToastUtil.showError(getActivity(), R.string.error_delete_project);
		} catch (IllegalArgumentException exception) {
			Log.e(TAG, "Project does not exist!", exception);
			ToastUtil.showError(getActivity(), R.string.error_unknown_project);
		}
	}

	@Override
	protected void copyCheckedItems() {
		for (ProjectData projectData : projectAdapter.getCheckedItems()) {
			projectToEdit = projectData;
			showCopyProjectDialog();
		}
		clearCheckedItems();
	}

	private void showCopyProjectDialog() {
		CopyProjectDialog dialog = new CopyProjectDialog(R.string.dialog_copy_project_title, R.string.new_project_name,
				projectToEdit.projectName);
		dialog.setTargetFragment(this, 0);
		dialog.show(getActivity().getFragmentManager(), CopyProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public void showRenameDialog() {
		if (!projectAdapter.getCheckedItems().isEmpty()) {
			projectToEdit = projectAdapter.getCheckedItems().get(0);
		}

		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_project, R.string.new_project_name, projectToEdit.projectName,
				this);
		dialog.show(getActivity().getFragmentManager(), RenameItemDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean itemNameExists(String newName) {
		return Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(newName);
	}

	@Override
	public void renameItem(String newName) {
		ProjectManager projectManager = ProjectManager.getInstance();
		try {
			projectManager.loadProject(projectToEdit.projectName, getActivity());
			projectManager.renameProject(newName, getActivity());
			projectManager.loadProject(newName, getActivity());
			clearCheckedItems();
			initializeList();
		} catch (ProjectException projectException) {
			Log.e(TAG, "Renaming an incompatible project isn't possible", projectException);
			ToastUtil.showError(getActivity(), R.string.error_rename_incompatible_project);
		}
	}

	private void showDetailsFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ShowDetailsFragment.SELECTED_PROJECT_KEY, projectToEdit);
		((ProjectListActivity) getActivity()).loadFragment(ShowDetailsFragment.class, bundle, true);
	}

	@Override
	public void showReplaceItemsInBackPackDialog() {
		//NO BackPack for Projects.
	}

	@Override
	public void packCheckedItems() {
		//NO BackPack for Projects.
	}

	@Override
	protected boolean isBackPackEmpty() {
		//NO BackPack for Projects.
		return true;
	}

	@Override
	protected void changeToBackPack() {
		//NO BackPack for Projects.
	}

	private void showSetDescriptionDialog() {
		projectToEdit = projectAdapter.getItem(selectedProjectPosition);
		Project project = StorageHandler.getInstance().loadProject(projectToEdit.projectName, getActivity());
		SetDescriptionDialog dialog = new SetDescriptionDialog(R.string.set_description, R.string.description,
				project.getDescription(), this);
		dialog.show(getActivity().getFragmentManager(), SetDescriptionDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void setDescription(String newDescription) {
		Project project = StorageHandler.getInstance().loadProject(projectToEdit.projectName, getActivity());
		project.setDescription(newDescription);
		StorageHandler.getInstance().saveProject(project);
		initializeList();
	}

	private void initializeDefaultProjectAfterDelete() {
		ProjectManager projectManager = ProjectManager.getInstance();

		setProgressCircleVisibility(true);
		projectManager.initializeDefaultProject(getActivity());
		setProgressCircleVisibility(false);

		initializeList();
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity) {
		Intent intent = new Intent(getActivity(), SceneListActivity.class);
		getActivity().startActivity(intent);
	}

	@Override
	public void onLoadProjectFailure() {
		setProgressCircleVisibility(false);
	}
}
