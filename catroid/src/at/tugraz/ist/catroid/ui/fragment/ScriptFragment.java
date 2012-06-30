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
package at.tugraz.ist.catroid.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter.BrickInteractionListener;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class ScriptFragment extends SherlockFragment implements BrickInteractionListener {
	
	private BrickAdapter adapter;
	private DragAndDropListView listView;
	private Sprite sprite;
	private Script scriptToEdit;
	private boolean addNewScript;
	
	private NewBrickAddedReceiver brickAddedReceiver;
	
	private static final int DIALOG_ADD_BRICK = 2;
	
	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}
		
		adapter = new BrickAdapter(getActivity(), sprite, listView);
		adapter.setBrickInteractionListener(this);
		if (adapter.getScriptCount() > 0) {
			ProjectManager.getInstance().setCurrentScript((Script) adapter.getItem(0));
			adapter.setCurrentScriptPosition(0);
		}

		listView.setOnCreateContextMenuListener(this);
		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);

		registerForContextMenu(listView);
		addNewScript = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_script, null);
		
		listView = (DragAndDropListView) rootView.findViewById(R.id.brick_list_view);
		listView.setTrashView((ImageView) rootView.findViewById(R.id.trash));
		setHasOptionsMenu(true);
		
		return rootView;
	}
	
	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}
		
		if (brickAddedReceiver != null) {
			getActivity().unregisterReceiver(brickAddedReceiver);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}
		
		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (!Utils.checkForSdCard(getActivity())) {
			return;
		}

		if (brickAddedReceiver == null) {
			brickAddedReceiver = new NewBrickAddedReceiver();
		}

		IntentFilter filter = new IntentFilter(ScriptTabActivity.ACTION_BRICKS_LIST_CHANGED);
		getActivity().registerReceiver(brickAddedReceiver, filter);

		initListeners();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		final MenuItem addItem = menu.findItem(R.id.menu_add);
		addItem.setIcon(R.drawable.ic_plus_black);
		addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getActivity().showDialog(DIALOG_ADD_BRICK);
				return true;
			}
		});
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

	public BrickAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.brick_list_view) {

			menu.setHeaderTitle(R.string.script_context_menu_title);

			if (adapter.getItem(listView.getTouchedListPosition()) instanceof Script) {
				scriptToEdit = (Script) adapter.getItem(listView.getTouchedListPosition());
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.script_menu, menu);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
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
	
	@Override
	public void onInsertedBrickChanged(int position) {
		listView.setInsertedBrick(position);
	}

	@Override
	public void onBrickLongClick(View brickView) {
		listView.onLongClick(brickView);
	}
	
	private class NewBrickAddedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptTabActivity.ACTION_BRICKS_LIST_CHANGED)) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
