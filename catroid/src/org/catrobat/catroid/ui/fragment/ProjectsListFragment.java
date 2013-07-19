/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.fragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.adapter.ProjectAdapter;
import org.catrobat.catroid.ui.adapter.ProjectAdapter.OnProjectCheckedListener;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog.OnCopyProjectListener;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog.OnProjectRenameListener;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog.OnUpdateProjectDescriptionListener;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

public class ProjectsListFragment extends SherlockListFragment implements OnProjectRenameListener,
		OnUpdateProjectDescriptionListener, OnCopyProjectListener, OnProjectCheckedListener {

	private static final String BUNDLE_ARGUMENTS_PROJECT_DATA = "project_data";
	private static final String SHARED_PREFERENCE_NAME = "showDetailsMyProjects";

	private static String deleteActionModeTitle;
	private static String singleItemAppendixDeleteActionMode;
	private static String multipleItemAppendixDeleteActionMode;

	private List<ProjectData> projectList;
	private ProjectData projectToEdit;
	private ProjectAdapter adapter;
	private ProjectsListFragment parentFragment = this;

	private ActionMode actionMode;

	private boolean actionModeActive = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
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
	public void onResume() {
		super.onResume();

		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_projects_list, null);
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
		initClickListener();
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

	public void setShowDetails(boolean showDetails) {
		adapter.setShowDetails(showDetails);
		adapter.notifyDataSetChanged();
	}

	public boolean getShowDetails() {
		return adapter.getShowDetails();
	}

	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	public int getSelectMode() {
		return adapter.getSelectMode();
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
	}

	private void initClickListener() {
		adapter.setOnProjectCheckedListener(this);
		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					if (!ProjectManager.INSTANCE.loadProject((adapter.getItem(position)).projectName,
							getActivity(), true)) {
						return; // error message already in ProjectManager
								// loadProject
					}
				} catch (ClassCastException exception) {
					Log.e("CATROID", getActivity().toString() + " does not implement ErrorListenerInterface", exception);
					return;
				}

				Intent intent = new Intent(getActivity(), ProjectActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		projectToEdit = adapter.getItem(info.position);

		adapter.addCheckedProject(info.position);

		if (ProjectManager.INSTANCE.getCurrentProject().getSpriteList().indexOf(projectToEdit) == 0) {
			return;
		}

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

		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onProjectChecked() {
		boolean isSingleSelectMode = adapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE ? true : false;

		if (isSingleSelectMode || actionMode == null) {
			return;
		}

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

	public void startRenameActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(renameModeCallBack);
			BottomBar.disableButtons(getActivity());
		}
	}

	public void startDeleteActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);
			BottomBar.disableButtons(getActivity());
		}
	}

	public void startCopyActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(copyModeCallBack);
			BottomBar.disableButtons(getActivity());
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
		String yes = getActivity().getString(R.string.yes);
		String no = getActivity().getString(R.string.no);
		String title = "";
		if (adapter.getAmountOfCheckedProjects() == 1) {
			title = getActivity().getString(R.string.dialog_confirm_delete_program_title);
		} else {
			title = getActivity().getString(R.string.dialog_confirm_delete_multiple_programs_title);
		}

		String message = getActivity().getString(R.string.dialog_confirm_delete_program_message);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedProjects();
				clearCheckedProjectsAndEnableButtons();
			}
		});
		builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				clearCheckedProjectsAndEnableButtons();
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void deleteProject() {
		ProjectManager projectManager = ProjectManager.INSTANCE;
		Project currentProject = projectManager.getCurrentProject();

		if (currentProject != null && currentProject.getName().equalsIgnoreCase(projectToEdit.projectName)) {
			projectManager.deleteCurrentProject();
		} else {
			StorageHandler.getInstance().deleteProject(projectToEdit);
		}
		try {
			projectList.remove(projectToEdit);
			if (projectList.size() == 0) {
				projectManager.initializeDefaultProject(getActivity());
			} else {

				projectManager.loadProject((projectList.get(0)).projectName, getActivity(), false);
				projectManager.saveProject();
			}
		} catch (ClassCastException exception) {
			Log.e("CATROID", getActivity().toString() + " does not implement ErrorListenerInterface", exception);
		}

		initAdapter();
	}

	private void deleteCheckedProjects() {
		Set<Integer> checkedSprites = adapter.getCheckedProjects();
		Iterator<Integer> iterator = checkedSprites.iterator();
		int numDeleted = 0;
		while (iterator.hasNext()) {
			int position = iterator.next();
			projectToEdit = (ProjectData) getListView().getItemAtPosition(position - numDeleted);
			deleteProject();
			numDeleted++;
		}
	}

	private void clearCheckedProjectsAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedProjects();

		actionMode = null;
		actionModeActive = false;

		BottomBar.enableButtons(getActivity());
	}

	public static class ProjectData implements Serializable {

		private static final long serialVersionUID = 1L;

		public String projectName;
		public long lastUsed;

		public ProjectData(String projectName, long lastUsed) {
			this.projectName = projectName;
			this.lastUsed = lastUsed;
		}
	}

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
			mode.setTitle(getString(R.string.rename));

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
			mode.setTitle(getString(R.string.copy));

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

}
