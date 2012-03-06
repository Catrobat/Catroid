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
package at.tugraz.ist.catroid.ui.dialogs;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.ui.ProjectActivity;

public class LoadProjectDialog extends Dialog {
	private final Context context;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> adapterFileList;

	public LoadProjectDialog(Context context) {
		super(context);
		this.context = context;
		adapterFileList = new ArrayList<String>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_load_project);
		setTitle(R.string.load_project_dialog_title);
		setCanceledOnTouchOutside(true);

		File rootDirectory = new File(Consts.DEFAULT_ROOT);
		searchForProjectFiles(rootDirectory);
		adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, adapterFileList);

		listView = (ListView) findViewById(R.id.loadfilelist);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!ProjectManager.getInstance().loadProject(adapter.getItem(position), context, true)) {
					return; // error message already in ProjectManager loadProject
				}
				Intent intent = new Intent(context, ProjectActivity.class);
				context.startActivity(intent);
				dismiss();
			}
		});
	}

	@Override
	protected void onStart() {
		// update List:
		adapterFileList.clear();
		File rootDirectory = new File(Consts.DEFAULT_ROOT);
		searchForProjectFiles(rootDirectory);
		adapter.notifyDataSetChanged();
		super.onStart();
	}

	public void searchForProjectFiles(File directory) {
		File[] sdFileList = directory.listFiles();
		for (File file : sdFileList) {
			if (file.isDirectory()) {
				//searchForProjectFiles(file);
				adapterFileList.add(file.getName());
			}
		}
	}
}
