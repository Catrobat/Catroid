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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.merge.MergeManager;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.ProjectListAdapter;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog.OnCopyProjectListener;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog.OnProjectRenameListener;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog.OnUpdateProjectDescriptionListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectListFragment extends ListActivityFragment implements OnProjectRenameListener,
		OnUpdateProjectDescriptionListener, OnCopyProjectListener, LoadProjectTask.OnLoadProjectCompleteListener,
		CheckBoxListAdapter.ListItemClickHandler<ProjectData>, CheckBoxListAdapter.ListItemLongClickHandler {

	private static final String TAG = ProjectListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_PROJECT_DATA = "project_data";
	private static final String SHARED_PREFERENCE_NAME = "showDetailsMyProjects";

	private ProjectListAdapter projectAdapter;
	private ListView listView;

	private List<ProjectData> projectList;
	private ProjectData projectToEdit;
	private int selectedProjectPosition;

	private ProjectListFragment parentFragment = this;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View projectListFragment = inflater.inflate(R.layout.fragment_projects_list, container);
		listView = (ListView) projectListFragment.findViewById(android.R.id.list);
		return projectListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());

		singleItemTitle = getString(R.string.program);
		multipleItemsTitle = getString(R.string.programs);

		if (savedInstanceState != null) {
			projectToEdit = (ProjectData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_PROJECT_DATA);
		}

		initializeList();
	}

	private void initializeList() {
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

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

		if (ProjectManager.getInstance().getHandleNewSceneFromScriptActivity()) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
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
				Utils.showErrorDialog(getActivity(), R.string.error_load_project);
			} catch (OutdatedVersionProjectException outdatedVersionException) {
				Log.e(TAG, "Projectcode version is outdated", outdatedVersionException);
				Utils.showErrorDialog(getActivity(), R.string.error_outdated_pocketcode_version);
			} catch (CompatibilityProjectException compatibilityException) {
				Log.e(TAG, "Project is not compatible", compatibilityException);
				Utils.showErrorDialog(getActivity(), R.string.error_project_compatability);
			}
		}

		menu.add(0, R.string.merge_button, 1, getString(R.string.merge_button) + ": " + ProjectManager.getInstance().getCurrentProject().getName());
		menu.setHeaderTitle(projectToEdit.projectName);

		getActivity().getMenuInflater().inflate(R.menu.context_menu_my_projects, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_copy:
				showCopyProjectDialog();
				break;

			case R.id.context_menu_rename:
				showRenameDialog();
				break;

			case R.id.context_menu_delete:
				showDeleteDialog();
				break;

			case R.id.context_menu_set_description:
				showSetDescriptionDialog();
				break;

			case R.id.context_menu_upload:
				ProjectManager.getInstance().uploadProject(projectToEdit.projectName, this.getActivity());
				break;
			case R.string.merge_button:
				String firstProjectName = ProjectManager.getInstance().getCurrentProject().getName();
				String secondProjectName = projectToEdit.projectName;

				MergeManager.merge(firstProjectName, secondProjectName, getActivity(), projectAdapter);
				break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void handleOnItemClick(int position, View view, ProjectData listItem) {
		LoadProjectTask loadProjectTask = new LoadProjectTask(getActivity(), listItem.projectName, true, false);
		loadProjectTask.setOnLoadProjectCompleteListener(this);
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
		loadProjectTask.execute();
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		selectedProjectPosition = position;
		listView.showContextMenuForChild(view);
	}

	public void showDeleteDialog() {
		int titleId;
		if (adapter.getCheckedItems().size() == 1) {
			titleId = R.string.dialog_confirm_delete_program_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_programs_title;
		}
		showDeleteDialog(titleId);
	}

	private void showCopyProjectDialog() {
		CopyProjectDialog dialogCopyProject = CopyProjectDialog.newInstance(projectToEdit.projectName);
		dialogCopyProject.setParentFragment(parentFragment);
		dialogCopyProject.show(getActivity().getFragmentManager(), CopyProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public void showRenameDialog() {
		if (!projectAdapter.getCheckedItems().isEmpty()) {
			projectToEdit = projectAdapter.getCheckedItems().get(0);
		}
		RenameProjectDialog dialogRenameProject = RenameProjectDialog.newInstance(projectToEdit.projectName);
		dialogRenameProject.setOnProjectRenameListener(ProjectListFragment.this);
		dialogRenameProject.show(getActivity().getFragmentManager(), RenameProjectDialog.DIALOG_FRAGMENT_TAG);
		clearCheckedItems();
	}

	private void showSetDescriptionDialog() {
		SetDescriptionDialog dialogSetDescription = SetDescriptionDialog.newInstance(projectToEdit.projectName);
		dialogSetDescription.setOnUpdateProjectDescriptionListener(ProjectListFragment.this);
		dialogSetDescription.show(getActivity().getFragmentManager(), SetDescriptionDialog.DIALOG_FRAGMENT_TAG);
		clearCheckedItems();
	}

	@Override
	protected void packCheckedItems() {
		//NO BackPack for Projects.
	}

	protected void deleteCheckedItems() {
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
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initializeDefaultProjectAfterDelete() {
		final ProjectManager projectManager = ProjectManager.getInstance();
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
		getActivity().findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				projectManager.initializeDefaultProject(getActivity());
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
						getActivity().findViewById(R.id.progress_circle).setVisibility(View.GONE);
					}
				});
			}
		};
		(new Thread(r)).start();
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity) {
		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		intent.putExtra(Constants.PROJECT_OPENED_FROM_PROJECTS_LIST, true);
		getActivity().startActivity(intent);
	}

	@Override
	public void onCopyProject() {
		initializeList();
	}

	@Override
	public void onProjectRename() {
		initializeList();
	}

	@Override
	public void onUpdateProjectDescription() {
		initializeList();
	}

	@Override
	public void onLoadProjectFailure() {
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
	}
}
