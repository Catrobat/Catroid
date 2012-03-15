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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.ui.adapter.ProjectAdapter;
import at.tugraz.ist.catroid.ui.dialogs.CustomIconContextMenu;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameProjectDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class MyProjectsActivity extends ListActivity {

	private List<File> projectList;
	public File projectToEdit;
	private ProjectAdapter adapter;
	private CustomIconContextMenu iconContextMenu;
	private CustomIconContextMenu iconContextMenu2;
	private ActivityHelper activityHelper;

	public static final int DIALOG_NEW_PROJECT = 0;
	private static final int DIALOG_CONTEXT_MENU = 1;
	private static final int CONTEXT_MENU_ITEM_RENAME = 2;
	private static final int CONTEXT_MENU_ITEM_DELETE = 3;
	// temporarily removed - because of upcoming release, and bad performance of projectdescription	
	//	private static final int CONTEXT_MENU_ITEM_DESCRIPTION = 4;
	public static final int DIALOG_RENAME_PROJECT = 5;
	// temporarily removed - because of upcoming release, and bad performance of projectdescription	
	//	public static final int DIALOG_SET_DESCRIPTION = 6;
	public static final int DIALOG_CONTEXT_MENU2 = 7;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_projects);
		projectToEdit = (File) getLastNonConfigurationInstance();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setUpActionBar();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final File savedSelectedProject = projectToEdit;
		return savedSelectedProject;
	}

	@Override
	protected void onStart() {
		super.onStart();
		initAdapter();
		initClickListener();
		initCustomContextMenu();
	}

	private void setUpActionBar() {
		String title = this.getResources().getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName();

		activityHelper = new ActivityHelper(this);
		activityHelper.setupActionBar(false, title);

		activityHelper.addActionButton(R.id.btn_action_add_button, R.drawable.ic_plus_black, R.string.add,
				new View.OnClickListener() {
					public void onClick(View v) {
						showDialog(DIALOG_NEW_PROJECT);
					}
				}, false);
	}

	public void initAdapter() {
		File rootDirectory = new File(Consts.DEFAULT_ROOT);
		projectList = UtilFile.getProjectFiles(rootDirectory);
		Collections.sort(projectList, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			}
		});

		adapter = new ProjectAdapter(this, R.layout.activity_my_projects_item, R.id.project_title, projectList);
		setListAdapter(adapter);
	}

	private void initClickListener() {
		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!ProjectManager.getInstance().loadProject((adapter.getItem(position)).getName(),
						MyProjectsActivity.this, true)) {
					return; // error message already in ProjectManager
							// loadProject
				}
				Intent intent = new Intent(MyProjectsActivity.this, ProjectActivity.class);
				MyProjectsActivity.this.startActivity(intent);
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				projectToEdit = projectList.get(position);
				if (projectToEdit == null) {
					return true;
				}
				if (!ProjectManager.getInstance().canLoadProject((projectToEdit.getName()))) {
					removeDialog(DIALOG_CONTEXT_MENU2);
					showDialog(DIALOG_CONTEXT_MENU2);
					return true;
				}
				removeDialog(DIALOG_CONTEXT_MENU);
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
		// temporarily removed - because of upcoming release, and bad performance of projectdescription
		//		iconContextMenu.addItem(resources, this.getString(R.string.set_description), R.drawable.ic_menu_description,
		//				CONTEXT_MENU_ITEM_DESCRIPTION);
		iconContextMenu.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			public void onClick(int menuId) {
				switch (menuId) {
					case CONTEXT_MENU_ITEM_RENAME:
						showDialog(DIALOG_RENAME_PROJECT);
						break;
					// temporarily removed - because of upcoming release, and bad performance of projectdescription						
					//					case CONTEXT_MENU_ITEM_DESCRIPTION:
					//						showDialog(DIALOG_SET_DESCRIPTION);
					//						break;
					case CONTEXT_MENU_ITEM_DELETE:
						deleteProject();
						break;
				}
			}
		});

		iconContextMenu2 = new CustomIconContextMenu(this, DIALOG_CONTEXT_MENU2);
		iconContextMenu2.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu2.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			public void onClick(int menuId) {
				switch (menuId) {
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

		String project = (projectToEdit.getName());
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(project)));

		if (!(currentProject != null && currentProject.getName().equalsIgnoreCase(project))) {
			initAdapter();
			return;
		}

		projectList.remove(projectToEdit);
		if (projectList.size() == 0) { // no projects left
			projectManager.initializeDefaultProject(MyProjectsActivity.this);
		} else {
			projectManager.loadProject((projectList.get(0)).getName(), MyProjectsActivity.this, false);
			projectManager.saveProject();
			Utils.saveToPreferences(MyProjectsActivity.this, Consts.PREF_PROJECTNAME_KEY, projectManager
					.getCurrentProject().getName());
		}

		updateProjectTitle();
		initAdapter();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		String project = "";
		if (projectToEdit != null) {
			project = (projectToEdit.getName());
		}
		switch (id) {
			case DIALOG_CONTEXT_MENU:
				if (iconContextMenu != null && projectToEdit != null) {
					dialog = iconContextMenu.createMenu(project);
				}
				break;
			case DIALOG_CONTEXT_MENU2:
				if (iconContextMenu2 != null && projectToEdit != null) {
					dialog = iconContextMenu2.createMenu(project);
				}
				break;
			case DIALOG_RENAME_PROJECT:
				if (projectToEdit != null) {
					dialog = (new RenameProjectDialog(this, project)).dialog;
				}
				break;
			// temporarily removed - because of upcoming release, and bad performance of projectdescription
			//			case DIALOG_SET_DESCRIPTION:
			//				if (projectToEdit != null) {
			//					dialog = (new SetDescriptionDialog(this)).dialog;
			//				}
			//				break;
			case DIALOG_NEW_PROJECT:
				dialog = (new NewProjectDialog(this)).dialog;
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
		String project = "";
		if (projectToEdit != null) {
			project = projectToEdit.getName();
		}
		switch (id) {
			case DIALOG_RENAME_PROJECT:
				EditText renameProjectEditText = (EditText) dialog.findViewById(R.id.dialog_text_EditText);
				renameProjectEditText.setText(project);
				break;
			// temporarily removed - because of upcoming release, and bad performance of projectdescription
			//			case DIALOG_SET_DESCRIPTION:
			//				EditText descriptionEditText = (EditText) dialog.findViewById(R.id.dialog_text_EditText);
			//
			//				ProjectManager projectManager = ProjectManager.getInstance();
			//				String currentProjectName = projectManager.getCurrentProject().getName();
			//
			//				if (project.equalsIgnoreCase(currentProjectName)) {
			//					descriptionEditText.setText(projectManager.getCurrentProject().description);
			//				} else {
			//					projectManager.loadProject(project, this, false); //TODO: check something
			//					descriptionEditText.setText(projectManager.getCurrentProject().description);
			//					projectManager.loadProject(currentProjectName, this, false);
			//				}
			//				break;
			case DIALOG_NEW_PROJECT:
				EditText newProjectEditText = (EditText) dialog.findViewById(R.id.dialog_text_EditText);
				newProjectEditText.setText("");
				break;
		}
	}

	public boolean projectAlreadyExists(String projectName) {
		for (File project : projectList) {
			if (projectName.equalsIgnoreCase(project.getName())) {
				return true;
			}
		}
		return false;
	}

	public void updateProjectTitle() {
		TextView titleTextView = (TextView) MyProjectsActivity.this.findViewById(R.id.tv_title);
		titleTextView.setText(MyProjectsActivity.this.getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName());
	}
}
