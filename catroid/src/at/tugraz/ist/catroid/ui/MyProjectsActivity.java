/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui;

import java.io.File;
import java.util.Collections;
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
import at.tugraz.ist.catroid.ui.dialogs.SetDescriptionDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class MyProjectsActivity extends ListActivity {

	private List<String> projectList;
	public String projectToEdit;
	private ProjectAdapter adapter;
	private CustomIconContextMenu iconContextMenu;
	private ActivityHelper activityHelper;

	public static final int DIALOG_NEW_PROJECT = 0;
	private static final int DIALOG_CONTEXT_MENU = 1;
	private static final int CONTEXT_MENU_ITEM_RENAME = 2;
	private static final int CONTEXT_MENU_ITEM_DELETE = 3;
	private static final int CONTEXT_MENU_ITEM_DESCRIPTION = 4;
	public static final int DIALOG_RENAME_PROJECT = 5;
	public static final int DIALOG_SET_DESCRIPTION = 6;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_projects);
		projectToEdit = (String) getLastNonConfigurationInstance();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setUpActionBar();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final String savedSelectedProject = projectToEdit;
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

		activityHelper.addActionButton(R.id.btn_action_add_sprite, R.drawable.ic_plus_black, R.string.add,
		        new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
				        showDialog(DIALOG_NEW_PROJECT);
			        }
		        }, false);
	}

	public void initAdapter() {
		File rootDirectory = new File(Consts.DEFAULT_ROOT);
		projectList = UtilFile.getProjectNames(rootDirectory);
		Collections.sort(projectList);
		adapter = new ProjectAdapter(this, R.layout.activity_my_projects_item, R.id.project_title, projectList);
		setListAdapter(adapter);
	}

	private void initClickListener() {
		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!ProjectManager.getInstance().loadProject(adapter.getItem(position), MyProjectsActivity.this, true)) {
					return; // error message already in ProjectManager
					        // loadProject
				}
				Intent intent = new Intent(MyProjectsActivity.this, ProjectActivity.class);
				MyProjectsActivity.this.startActivity(intent);
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				projectToEdit = projectList.get(position);
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
		iconContextMenu.addItem(resources, this.getString(R.string.set_description), R.drawable.ic_menu_description,
		        CONTEXT_MENU_ITEM_DESCRIPTION);
		iconContextMenu.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
		        CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) {
				switch (menuId) {
				case CONTEXT_MENU_ITEM_RENAME:
					showDialog(DIALOG_RENAME_PROJECT);
					break;
				case CONTEXT_MENU_ITEM_DESCRIPTION:
					showDialog(DIALOG_SET_DESCRIPTION);
					break;
				case CONTEXT_MENU_ITEM_DELETE:
					ProjectManager projectManager = ProjectManager.getInstance();
					Project currentProject = projectManager.getCurrentProject();

					UtilFile.deleteDirectory(new File(Utils.buildPath(Consts.DEFAULT_ROOT, projectToEdit)));

					if (!(currentProject != null && currentProject.getName().equalsIgnoreCase(projectToEdit))) {
						initAdapter();
						break;
					}

					projectList.remove(projectToEdit);
					if (projectList.size() == 0) { // no projects left
						projectManager.initializeDefaultProject(MyProjectsActivity.this);
					} else {
						projectManager.loadProject(projectList.get(0), MyProjectsActivity.this, true);
						projectManager.saveProject();
						Utils.saveToPreferences(MyProjectsActivity.this, MainMenuActivity.PREF_PROJECTNAME_KEY,
						        projectManager.getCurrentProject().getName());
					}

					updateProjectTitle();
					initAdapter();
					break;
				}
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_CONTEXT_MENU:
			if (iconContextMenu != null && projectToEdit != null) {
				dialog = iconContextMenu.createMenu(projectToEdit);
			}
			break;
		case DIALOG_RENAME_PROJECT:
			if (projectToEdit != null) {
				dialog = (new RenameProjectDialog(this, projectToEdit)).dialog;
			}
			break;
		case DIALOG_SET_DESCRIPTION:
			if (projectToEdit != null) {
				dialog = (new SetDescriptionDialog(this)).dialog;
			}
			break;
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
		switch (id) {
		case DIALOG_RENAME_PROJECT:
			EditText renameProjectEditText = (EditText) dialog.findViewById(R.id.dialog_text_EditText);
			renameProjectEditText.setText(projectToEdit);
			break;
		case DIALOG_SET_DESCRIPTION:
			EditText descriptionEditText = (EditText) dialog.findViewById(R.id.dialog_text_EditText);

			ProjectManager projectManager = ProjectManager.getInstance();
			String currentProjectName = projectManager.getCurrentProject().getName();

			if (projectToEdit.equalsIgnoreCase(currentProjectName)) {
				descriptionEditText.setText(projectManager.getCurrentProject().description);
			} else {
				projectManager.loadProject(projectToEdit, this, false);
				descriptionEditText.setText(projectManager.getCurrentProject().description);
				projectManager.loadProject(currentProjectName, this, false);
			}
			break;
		case DIALOG_NEW_PROJECT:
			EditText newProjectEditText = (EditText) dialog.findViewById(R.id.dialog_text_EditText);
			newProjectEditText.setText("");
			break;
		}
	}

	public boolean projectAlreadyExists(String projectName) {
		for (String project : projectList) {
			if (projectName.equalsIgnoreCase(project)) {
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
