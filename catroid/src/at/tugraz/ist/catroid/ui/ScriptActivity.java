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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.utils.Utils;

public class ScriptActivity extends Activity implements OnDismissListener, OnCancelListener {
	private BrickAdapter adapter;
	private DragAndDropListView listView;
	private Sprite sprite;
	private Script scriptToEdit;

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		listView = (DragAndDropListView) findViewById(R.id.brick_list_view);
		adapter = new BrickAdapter(this, sprite, listView);
		if (adapter.getGroupCount() > 0) {
			ProjectManager.getInstance().setCurrentScript(adapter.getGroup(adapter.getGroupCount() - 1));
		}

		listView.setTrashView((ImageView) findViewById(R.id.trash));
		listView.setOnCreateContextMenuListener(this);
		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);
		listView.setGroupIndicator(null);
		listView.setOnGroupClickListener(adapter);
		registerForContextMenu(listView);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_script);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch (id) {
			case Consts.DIALOG_ADD_BRICK:
				dialog = new AddBrickDialog(this);
				dialog.setOnDismissListener(this);
				break;
			default:
				dialog = null;
				break;
		}
		return dialog;
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
		initListeners();
		if (adapter.getGroupCount() > 0) {
			listView.expandGroup(adapter.getGroupCount() - 1);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}

		initListeners();
		if (adapter.getGroupCount() > 0) {
			listView.expandGroup(adapter.getGroupCount() - 1);
		}

		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		if (scriptTabActivity != null && scriptTabActivity.activityHelper != null) {
			//set new functionality for actionbar add button:
			scriptTabActivity.activityHelper.changeClickListener(R.id.btn_action_add_sprite,
					createAddBrickClickListener());
			//set new icon for actionbar plus button:
			scriptTabActivity.activityHelper.changeButtonIcon(R.id.btn_action_add_sprite, R.drawable.ic_plus_black);
		}
	}

	private View.OnClickListener createAddBrickClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(Consts.DIALOG_ADD_BRICK);
			}
		};
	}

	public void onDismiss(DialogInterface dialog) {
		for (int i = 0; i < adapter.getGroupCount() - 1; ++i) {
			listView.collapseGroup(i);
		}

		adapter.notifyDataSetChanged();
		if (adapter.getGroupCount() > 0) {
			listView.expandGroup(adapter.getGroupCount() - 1);
		}
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
			ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Script Menu");

			if (ExpandableListView.getPackedPositionType(info.packedPosition) != ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

				int position = ExpandableListView.getPackedPositionGroup(info.packedPosition);
				scriptToEdit = adapter.getGroup(position);

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
				adapter.notifyDataSetChanged();
				listView.expandGroup(adapter.getGroupCount() - 1);
			}
		}
		return true;
	}
}
