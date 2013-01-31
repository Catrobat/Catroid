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
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.adapter.IconMenuAdapter;
import org.catrobat.catroid.ui.adapter.ProjectAdapter;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog;
import org.catrobat.catroid.ui.dialogs.CopyProjectDialog.OnCopyProjectListener;
import org.catrobat.catroid.ui.dialogs.CustomIconContextMenu;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog;
import org.catrobat.catroid.ui.dialogs.RenameProjectDialog.OnProjectRenameListener;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog.OnUpdateProjectDescriptionListener;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ProjectsListFragment extends SherlockListFragment implements OnProjectRenameListener,
		OnUpdateProjectDescriptionListener, OnCopyProjectListener {

	private static final String BUNDLE_ARGUMENTS_PROJECT_DATA = "project_data";

	private List<ProjectData> projectList;
	private ProjectData projectToEdit;
	private ProjectAdapter adapter;
	private ProjectsListFragment parentFragment = this;

	private static final int CONTEXT_MENU_ITEM_RENAME = 0;
	private static final int CONTEXT_MENU_ITEM_DESCRIPTION = 1;
	private static final int CONTEXT_MENU_ITEM_DELETE = 2;
	private static final int CONTEXT_MENU_ITEM_COPY = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_projects_list, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

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
		if (isCurrentProject) {
			updateProjectTitle();
		}
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

		adapter = new ProjectAdapter(getActivity(), R.layout.activity_my_projects_item,
				R.id.my_projects_activity_project_title, projectList);
		setListAdapter(adapter);
	}

	private void initClickListener() {
		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					if (!ProjectManager.getInstance().loadProject((adapter.getItem(position)).projectName,
							getActivity(), (ErrorListenerInterface) getActivity(), true)) {
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
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				projectToEdit = projectList.get(position);

				if (projectToEdit != null) {
					showEditProjectContextDialog();
				}

				return true;
			}
		});
	}

	private void showEditProjectContextDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag(CustomIconContextMenu.DIALOG_FRAGMENT_TAG);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		CustomIconContextMenu dialog = CustomIconContextMenu.newInstance(projectToEdit.projectName);
		initCustomContextMenu(dialog);
		dialog.show(getFragmentManager(), CustomIconContextMenu.DIALOG_FRAGMENT_TAG);
	}

	private void initCustomContextMenu(CustomIconContextMenu iconContextMenu) {
		Resources resources = getResources();

		IconMenuAdapter adapter = new IconMenuAdapter(getActivity());
		adapter.addItem(resources, this.getString(R.string.rename), R.drawable.ic_context_rename,
				CONTEXT_MENU_ITEM_RENAME);
		adapter.addItem(resources, this.getString(R.string.set_description), R.drawable.ic_menu_description,
				CONTEXT_MENU_ITEM_DESCRIPTION);
		adapter.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);
		adapter.addItem(resources, this.getString(R.string.copy), R.drawable.ic_context_copy, CONTEXT_MENU_ITEM_COPY);
		iconContextMenu.setAdapter(adapter);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) {
				switch (menuId) {
					case CONTEXT_MENU_ITEM_RENAME:
						RenameProjectDialog dialogRenameProject = RenameProjectDialog
								.newInstance(projectToEdit.projectName);
						dialogRenameProject.setOnProjectRenameListener(ProjectsListFragment.this);
						dialogRenameProject.show(getActivity().getSupportFragmentManager(),
								RenameProjectDialog.DIALOG_FRAGMENT_TAG);
						break;
					case CONTEXT_MENU_ITEM_DESCRIPTION:
						SetDescriptionDialog dialogSetDescription = SetDescriptionDialog
								.newInstance(projectToEdit.projectName);
						dialogSetDescription.setOnUpdateProjectDescriptionListener(ProjectsListFragment.this);
						dialogSetDescription.show(getActivity().getSupportFragmentManager(),
								SetDescriptionDialog.DIALOG_FRAGMENT_TAG);
						break;
					case CONTEXT_MENU_ITEM_DELETE:
						deleteProject();
						break;
					case CONTEXT_MENU_ITEM_COPY:
						CopyProjectDialog dialogCopyProject = CopyProjectDialog.newInstance(projectToEdit.projectName);
						dialogCopyProject.setParentFragment(parentFragment);
						dialogCopyProject.show(getActivity().getSupportFragmentManager(),
								CopyProjectDialog.DIALOG_FRAGMENT_TAG);
						break;
				}
			}
		});
	}

	private void deleteProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project currentProject = projectManager.getCurrentProject();

		if (currentProject != null && currentProject.getName().equalsIgnoreCase(projectToEdit.projectName)) {
			projectManager.deleteCurrentProject();
		} else {
			StorageHandler.getInstance().deleteProject(projectToEdit);
		}
		try {
			projectList.remove(projectToEdit);
			if (projectList.size() == 0) {
				projectManager.initializeDefaultProject(getActivity(), (ErrorListenerInterface) getActivity());
			} else {

				projectManager.loadProject((projectList.get(0)).projectName, getActivity(),
						(ErrorListenerInterface) getActivity(), false);
				projectManager.saveProject();
			}
		} catch (ClassCastException exception) {
			Log.e("CATROID", getActivity().toString() + " does not implement ErrorListenerInterface", exception);
		}

		updateProjectTitle();
		initAdapter();
	}

	private void updateProjectTitle() {
		String title = getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName();
		getSherlockActivity().getSupportActionBar().setTitle(title);
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
}
