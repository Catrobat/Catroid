/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.adapter.ProjectAdapter;
import org.catrobat.catroid.ui.adapter.ProjectAdapter.OnProjectEditListener;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog.OnCopyProjectListener;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog.OnProjectRenameListener;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog.OnUpdateProjectDescriptionListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilMerge;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ProjectsListFragment extends SherlockListFragment implements OnProjectRenameListener,
		OnUpdateProjectDescriptionListener, OnCopyProjectListener, OnProjectEditListener {

	private static final String BUNDLE_ARGUMENTS_PROJECT_DATA = "project_data";
	private static final String SHARED_PREFERENCE_NAME = "showDetailsMyProjects";
	private static final String TAG = ProjectsListFragment.class.getSimpleName();

	private static String deleteActionModeTitle;
	private static String singleItemAppendixDeleteActionMode;
	private static String multipleItemAppendixDeleteActionMode;

	private ProjectListInitReceiver projectListInitReceiver;

	private List<ProjectData> projectList;
	private ProjectData projectToEdit;
	private ProjectAdapter adapter;
	private ProjectsListFragment parentFragment = this;

	private ActionMode actionMode;
	private View selectAllActionModeButton;

	private boolean actionModeActive = false;
	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeActive = true;

			deleteActionModeTitle = getString(R.string.delete);
			singleItemAppendixDeleteActionMode = getString(R.string.program);
			multipleItemAppendixDeleteActionMode = getString(R.string.programs);

			mode.setTitle(deleteActionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedProjects() == 0) {
				clearCheckedProjectsAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};
	private ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_SINGLE);
			mode.setTitle(R.string.rename);

			actionModeActive = true;

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Set<Integer> checkedSprites = adapter.getCheckedProjects();
			Iterator<Integer> iterator = checkedSprites.iterator();
			if (iterator.hasNext()) {
				int position = iterator.next();
				projectToEdit = (ProjectData) getListView().getItemAtPosition(position);
				showRenameDialog();
			}
			clearCheckedProjectsAndEnableButtons();
		}
	};
	private ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_SINGLE);
			mode.setTitle(R.string.copy);

			actionModeActive = true;

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Set<Integer> checkedSprites = adapter.getCheckedProjects();
			Iterator<Integer> iterator = checkedSprites.iterator();
			if (iterator.hasNext()) {
				int position = iterator.next();
				projectToEdit = (ProjectData) getListView().getItemAtPosition(position);
				showCopyProjectDialog();
			}
			clearCheckedProjectsAndEnableButtons();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (projectListInitReceiver != null) {
			getActivity().unregisterReceiver(projectListInitReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}

		if (projectListInitReceiver == null) {
			projectListInitReceiver = new ProjectListInitReceiver();
		}

		IntentFilter intentFilterSpriteListInit = new IntentFilter(MyProjectsActivity.ACTION_PROJECT_LIST_INIT);
		getActivity().registerReceiver(projectListInitReceiver, intentFilterSpriteListInit);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));

		initAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_projects_list, container);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		if (savedInstanceState != null) {
			projectToEdit = (ProjectData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_PROJECT_DATA);
		}

		initAdapter();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_PROJECT_DATA, projectToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onProjectRename(boolean isCurrentProject) {
		initAdapter();
	}

	@Override
	public void onCopyProject() {
		initAdapter();
	}

	@Override
	public void onUpdateProjectDescription() {
		initAdapter();
	}

	public boolean getShowDetails() {
		return adapter.getShowDetails();
	}

	public void setShowDetails(boolean showDetails) {
		adapter.setShowDetails(showDetails);
		adapter.notifyDataSetChanged();
	}

	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	public boolean getActionModeActive() {
		return actionModeActive;
	}

	private void initAdapter() {
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		File projectCodeFile;
		projectList = new ArrayList<ProjectData>();
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

		adapter = new ProjectAdapter(getActivity(), R.layout.activity_my_projects_list_item,
				R.id.my_projects_activity_project_title, projectList);
		setListAdapter(adapter);
		initClickListener();
	}

	private void initClickListener() {
		adapter.setOnProjectEditListener(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		projectToEdit = adapter.getItem(info.position);

		adapter.addCheckedProject(info.position);

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
		} else if (currentProject.getSpriteList().indexOf(projectToEdit) == 0) {
			return;
		}

		menu.add(0, R.string.merge_button, 1, getString(R.string.merge_button) + ": " + ProjectManager.getInstance().getCurrentProject().getName());
		menu.setHeaderTitle(projectToEdit.projectName);

		getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_my_projects, menu);
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
				showConfirmDeleteDialog();
				break;

			case R.id.context_menu_set_description:
				showSetDescriptionDialog();
				break;

			case R.id.context_menu_upload:
				ProjectManager.getInstance().uploadProject(projectToEdit.projectName, this.getActivity());
				break;
			case R.string.merge_button:
				UtilMerge.mergeProjectInCurrentProject(projectToEdit.projectName, this.getActivity());
				break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onProjectChecked() {
		if (adapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE || actionMode == null) {
			return;
		}

		updateActionModeTitle();
		Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedProjects() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getAmountOfCheckedProjects();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(deleteActionModeTitle);
		} else {
			String appendix = multipleItemAppendixDeleteActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixDeleteActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = deleteActionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = deleteActionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	@Override
	public void onProjectEdit(int position) {
		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		intent.putExtra(Constants.PROJECTNAME_TO_LOAD, adapter.getItem(position).projectName);
		intent.putExtra(Constants.PROJECT_OPENED_FROM_PROJECTS_LIST, true);

		getActivity().startActivity(intent);
	}

	public void startRenameActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(renameModeCallBack);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	public void startDeleteActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	public void startCopyActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(copyModeCallBack);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	private void showRenameDialog() {
		RenameProjectDialog dialogRenameProject = RenameProjectDialog.newInstance(projectToEdit.projectName);
		dialogRenameProject.setOnProjectRenameListener(ProjectsListFragment.this);
		dialogRenameProject.show(getActivity().getSupportFragmentManager(), RenameProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	private void showSetDescriptionDialog() {
		SetDescriptionDialog dialogSetDescription = SetDescriptionDialog.newInstance(projectToEdit.projectName);
		dialogSetDescription.setOnUpdateProjectDescriptionListener(ProjectsListFragment.this);
		dialogSetDescription.show(getActivity().getSupportFragmentManager(), SetDescriptionDialog.DIALOG_FRAGMENT_TAG);
	}

	private void showCopyProjectDialog() {
		CopyProjectDialog dialogCopyProject = CopyProjectDialog.newInstance(projectToEdit.projectName);
		dialogCopyProject.setParentFragment(parentFragment);
		dialogCopyProject.show(getActivity().getSupportFragmentManager(), CopyProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	private void showConfirmDeleteDialog() {
		int titleId;
		if (adapter.getAmountOfCheckedProjects() == 1) {
			titleId = R.string.dialog_confirm_delete_program_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_programs_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_program_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedProjects();
				clearCheckedProjectsAndEnableButtons();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				clearCheckedProjectsAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void deleteProject(ProjectData project) {
		ProjectManager projectManager = ProjectManager.getInstance();
		try {
			projectManager.deleteProject(project.projectName, getActivity().getApplicationContext());
			projectList.remove(project);
		} catch (IOException exception) {
			Log.e(TAG, "Project could not be deleted", exception);
			ToastUtil.showError(getActivity(), R.string.error_delete_project);
		} catch (IllegalArgumentException exception) {
			Log.e(TAG, "Project does not exist!", exception);
			ToastUtil.showError(getActivity(), R.string.error_unknown_project);
		}
	}

	private void deleteCheckedProjects() {
		int numDeleted = 0;
		for (int position : adapter.getCheckedProjects()) {
			projectToEdit = (ProjectData) getListView().getItemAtPosition(position - numDeleted);
			deleteProject(projectToEdit);
			numDeleted++;
		}

		if (projectList.isEmpty()) {
			ProjectManager projectManager = ProjectManager.getInstance();
			projectManager.initializeDefaultProject(getActivity());
		} else if (ProjectManager.getInstance().getCurrentProject() == null) {
			Utils.saveToPreferences(getActivity().getApplicationContext(), Constants.PREF_PROJECTNAME_KEY,
					projectList.get(0).projectName);
		}

		initAdapter();
	}

	private void clearCheckedProjectsAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedProjects();

		actionMode = null;
		actionModeActive = false;

		BottomBar.showBottomBar(getActivity());
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getLayoutInflater(null), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				for (int position = 0; position < projectList.size(); position++) {
					adapter.addCheckedProject(position);
				}
				adapter.notifyDataSetChanged();
				onProjectChecked();
			}
		});
	}

	private class ProjectListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MyProjectsActivity.ACTION_PROJECT_LIST_INIT)) {
				adapter.notifyDataSetChanged();
			}
		}
	}
}
