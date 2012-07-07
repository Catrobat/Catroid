/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.ui.adapter.ProjectAdapter;
import at.tugraz.ist.catroid.ui.dialogs.CustomIconContextMenu;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameProjectDialog.OnProjectRenameListener;
import at.tugraz.ist.catroid.ui.dialogs.SetDescriptionDialog;
import at.tugraz.ist.catroid.ui.dialogs.SetDescriptionDialog.OnUpdateProjectDescriptionListener;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MyProjectsActivity extends SherlockFragmentActivity 
		implements OnProjectRenameListener, OnUpdateProjectDescriptionListener {

	private List<ProjectData> projectList;
	public ProjectData projectToEdit;
	private ProjectAdapter adapter;
	private CustomIconContextMenu iconContextMenu;

	private ActionBar actionBar;
	private ListView projectsListView;

	private static final int DIALOG_CONTEXT_MENU = 1;
	private static final int CONTEXT_MENU_ITEM_RENAME = 2;
	private static final int CONTEXT_MENU_ITEM_DELETE = 3;
	private static final int CONTEXT_MENU_ITEM_DESCRIPTION = 4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_my_projects);
		projectsListView = (ListView) findViewById(R.id.projects_list);
		
		projectToEdit = (ProjectData) getLastNonConfigurationInstance();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setUpActionBar();
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		final ProjectData savedSelectedProject = projectToEdit;
		return savedSelectedProject;
	}

	@Override
	protected void onStart() {
		super.onStart();
		initAdapter();
		initClickListener();
		initCustomContextMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_myprojects, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				Intent intent = new Intent(this, MainMenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}
			case R.id.menu_add: {
				NewProjectDialog dialog = new NewProjectDialog();
				dialog.show(getSupportFragmentManager(), "dialog_new_project");
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpActionBar() {
		String title = this.getResources().getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName();

		actionBar = getSupportActionBar();
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	public void initAdapter() {
		File rootDirectory = new File(Constants.DEFAULT_ROOT);
		File projectCodeFile;
		projectList = new ArrayList<ProjectData>();
		for (String projectName : UtilFile.getProjectNames(rootDirectory)) {
			projectCodeFile = new File(Utils.buildPath(Utils.buildProjectPath(projectName), Constants.PROJECTCODE_NAME));
			projectList.add(new ProjectData(projectName, projectCodeFile.lastModified()));
		}
		Collections.sort(projectList, new Comparator<ProjectData>() {
			public int compare(ProjectData project1, ProjectData project2) {
				return Long.valueOf(project2.lastUsed).compareTo(project1.lastUsed);
			}
		});

		adapter = new ProjectAdapter(this, R.layout.activity_my_projects_item, R.id.my_projects_activity_project_title,
				projectList);
		projectsListView.setAdapter(adapter);
	}

	private void initClickListener() {
		projectsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!ProjectManager.getInstance().loadProject((adapter.getItem(position)).projectName,
						MyProjectsActivity.this, true)) {
					return; // error message already in ProjectManager
							// loadProject
				}
				Intent intent = new Intent(MyProjectsActivity.this, ProjectActivity.class);
				MyProjectsActivity.this.startActivity(intent);
			}
		});
		projectsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				projectToEdit = projectList.get(position);
				if (projectToEdit == null) {
					return true;
				}
				showDialog(DIALOG_CONTEXT_MENU);
				return true;
			}
		});
	}

	private void initCustomContextMenu() {
		Resources resources = getResources();
		iconContextMenu = new CustomIconContextMenu(this, DIALOG_CONTEXT_MENU);
		iconContextMenu.addItem(resources, this.getString(R.string.rename), R.drawable.ic_context_rename,
				CONTEXT_MENU_ITEM_RENAME);
		iconContextMenu.addItem(resources, this.getString(R.string.set_description), R.drawable.ic_menu_description,
				CONTEXT_MENU_ITEM_DESCRIPTION);
		iconContextMenu.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			public void onClick(int menuId) {
				switch (menuId) {
					case CONTEXT_MENU_ITEM_RENAME:
						RenameProjectDialog dialogRenameProject = 
							RenameProjectDialog.newInstance(projectToEdit.projectName);
						dialogRenameProject.setOnProjectRenameListener(MyProjectsActivity.this);
						dialogRenameProject.show(getSupportFragmentManager(), "dialog_rename_project");
//						showDialog(DIALOG_RENAME_PROJECT);
						break;
					case CONTEXT_MENU_ITEM_DESCRIPTION:
						SetDescriptionDialog dialogSetDescription = 
							SetDescriptionDialog.newInstance(projectToEdit.projectName);
						dialogSetDescription.setOnUpdateProjectDescriptionListener(MyProjectsActivity.this);
						dialogSetDescription.show(getSupportFragmentManager(), "dialog_set_description");
//						showDialog(DIALOG_SET_DESCRIPTION);
						break;
					case CONTEXT_MENU_ITEM_DELETE:
						deleteProject();
						break;
				}
			}
		});
	}

	private void deleteProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project currentProject = projectManager.getCurrentProject();

		String project = projectToEdit.projectName;
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(project)));

		if (!(currentProject != null && currentProject.getName().equalsIgnoreCase(project))) {
			initAdapter();
			return;
		}

		projectList.remove(projectToEdit);
		if (projectList.size() == 0) { // no projects left
			projectManager.initializeDefaultProject(MyProjectsActivity.this);
		} else {
			projectManager.loadProject((projectList.get(0)).projectName, MyProjectsActivity.this, false);
			projectManager.saveProject();
		}

		updateProjectTitle();
		initAdapter();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		String project = "";
		if (projectToEdit != null) {
			project = projectToEdit.projectName;
		}
		switch (id) {
			case DIALOG_CONTEXT_MENU:
				if (iconContextMenu != null && projectToEdit != null) {
					dialog = iconContextMenu.createMenu(project);
				}
				break;
			default:
				dialog = null;
				break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		TextView customTitleTextView = null;
		switch (id) {
			case DIALOG_CONTEXT_MENU:
				customTitleTextView = (TextView) dialog.findViewById(R.id.alert_dialog_title);
				customTitleTextView.setText(projectToEdit.projectName);
				break;
		}
	}

	private void updateProjectTitle() {
		String title = getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName();
		actionBar.setTitle(title);
	}

	public class ProjectData {
		public String projectName;
		public long lastUsed;

		public ProjectData(String projectName, long lastUsed) {
			this.projectName = projectName;
			this.lastUsed = lastUsed;
		}
	}

	@Override
	public void onProjectRename(boolean isCurrentProject) {
		if (isCurrentProject) {
			updateProjectTitle();
		}
		
		initAdapter();
	}

	@Override
	public void onUpdateProjectDescription() {
		initAdapter();
	}
}
