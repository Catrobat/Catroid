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
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class LoadProjectDialog extends Dialog {
    private final Context context;
    private final ProjectManager contentManager;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> adapterFileList;

    public LoadProjectDialog(Context context, ProjectManager contentManager) {
        super(context);
        this.context = context;
        this.contentManager = contentManager;
        adapterFileList = new ArrayList<String>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.load_project_dialog);
        setTitle(R.string.laod_project_dialog_title);

        File rootDirectory = new File(context.getString(R.string.default_root));
        searchForProjectFiles(rootDirectory);
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, adapterFileList);
        
        listView = (ListView) findViewById(R.id.loadfilelist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
        	
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!contentManager.loadProject(adapter.getItem(position), context)) {
                    dismiss(); //TODO: should we dismiss here? or continue project choosing
                    return;
                }
                Intent intent = new Intent(context, ProjectActivity.class);
            	context.startActivity(intent);
                dismiss();
            }
        });
    }

	@Override
	protected void onStart() {
		adapterFileList.clear();
		File rootDirectory = new File(context.getString(R.string.default_root));
		searchForProjectFiles(rootDirectory);
		adapter.notifyDataSetChanged();
		super.onStart();
	}

    public void searchForProjectFiles(File directory) {
        File[] sdFileList = directory.listFiles();
        for (File file : sdFileList) {
        	if (file.isDirectory()) {
                searchForProjectFiles(file);
            } else if (file.isFile() && file.getName().endsWith(StorageHandler.PROJECT_EXTENTION)) {
            	adapterFileList.add(Utils.getProjectName(file.getName()));
            }
        }
    }
}
