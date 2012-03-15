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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.utils.Utils;

public class ScriptActivity extends Activity implements OnCancelListener {
	private BrickAdapter adapter;
	private DragAndDropListView listView;
	private Sprite sprite;
	private Script scriptToEdit;
	private boolean addNewScript;
	private static final int DIALOG_ADD_BRICK = 2;

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}
		listView = (DragAndDropListView) findViewById(R.id.brick_list_view);
		adapter = new BrickAdapter(this, sprite, listView);
		if (adapter.getScriptCount() > 0) {
			ProjectManager.getInstance().setCurrentScript((Script) adapter.getItem(0));
			adapter.setCurrentScriptPosition(0);
		}

		listView.setTrashView((ImageView) findViewById(R.id.trash));
		listView.setOnCreateContextMenuListener(this);
		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);

		registerForContextMenu(listView);
		addNewScript = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_script);
	}

	@Override
	protected void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}
		initListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}

		initListeners();

		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		if (scriptTabActivity != null && scriptTabActivity.activityHelper != null) {
			//set new functionality for actionbar add button:
			scriptTabActivity.activityHelper.changeClickListener(R.id.btn_action_add_button,
					createAddBrickClickListener());
			//set new icon for actionbar plus button:
			scriptTabActivity.activityHelper.changeButtonIcon(R.id.btn_action_add_button, R.drawable.ic_plus_black);
		}
	}

	private View.OnClickListener createAddBrickClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				getParent().showDialog(DIALOG_ADD_BRICK);
			}
		};
	}

	public void setAddNewScript() {
		addNewScript = true;
	}

	public void updateAdapterAfterAddNewBrick(DialogInterface dialog) {

		if (addNewScript) {
			addNewScript = false;
		} else {
			int visibleF = listView.getFirstVisiblePosition();
			int visibleL = listView.getLastVisiblePosition();
			int pos = ((visibleL - visibleF) / 2);
			pos += visibleF;
			pos = adapter.rearangeBricks(pos);
			adapter.setInsertedBrickpos(pos);
			listView.setInsertedBrick(pos);
		}
		adapter.notifyDataSetChanged();
	}

	public void onCancel(DialogInterface arg0) {
		adapter.notifyDataSetChanged();
	}

	public BrickAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.brick_list_view) {

			menu.setHeaderTitle(R.string.script_context_menu_title);

			if (adapter.getItem(listView.getTouchedListPosition()) instanceof Script) {
				scriptToEdit = (Script) adapter.getItem(listView.getTouchedListPosition());
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.script_menu, menu);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.script_menu_delete: {
				sprite.removeScript(scriptToEdit);
				if (sprite.getNumberOfScripts() == 0) {
					ProjectManager.getInstance().setCurrentScript(null);
					adapter.notifyDataSetChanged();
					return true;
				}
				int lastScriptIndex = sprite.getNumberOfScripts() - 1;
				Script lastScript = sprite.getScript(lastScriptIndex);
				ProjectManager.getInstance().setCurrentScript(lastScript);
				adapter.setCurrentScriptPosition(lastScriptIndex);
				adapter.notifyDataSetChanged();
			}
		}
		return true;
	}

}
