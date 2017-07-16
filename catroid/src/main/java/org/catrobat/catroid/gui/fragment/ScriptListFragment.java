/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.gui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.copypaste.Clipboard;
import org.catrobat.catroid.copypaste.ClipboardHandler;
import org.catrobat.catroid.data.SpriteInfo;
import org.catrobat.catroid.data.brick.Brick;
import org.catrobat.catroid.gui.adapter.ScriptListAdapter;
import org.catrobat.catroid.gui.adapter.TouchHelperCallback;
import org.catrobat.catroid.projecthandler.ProjectHolder;

import java.util.List;

public class ScriptListFragment extends Fragment implements ScriptListAdapter.SelectionListener,
		ScriptListAdapter.ReorderItemInterface {

	public static final String TAG = ScriptListFragment.class.getSimpleName();

	private RecyclerView view;
	private ScriptListAdapter adapter;
	private ItemTouchHelper touchHelper;

	private SpriteInfo sprite;
	private ActionMode actionMode;

	private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu_scripts, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			List<Brick> selectedItems = adapter.getSelectedItems();

			switch (item.getItemId()) {
				case R.id.btnCopy:
					copyItems(selectedItems);
					break;
				case R.id.btnDelete:
					deleteItems(selectedItems);
					break;
				default:
					return false;
			}

			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			adapter.clearSelection();
			getActivity().invalidateOptionsMenu();
			actionMode = null;
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		view = (RecyclerView) inflater.inflate(R.layout.fragment_recycler_view, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		String sceneName = getActivity().getIntent().getStringExtra(SceneListFragment.SELECTED_SCENE);
		String spriteName = getActivity().getIntent().getStringExtra(SpriteListFragment.SELECTED_SPRITE);
		sprite = ProjectHolder.getInstance().getCurrentProject().getSceneByName(sceneName).getSpriteByName(spriteName);

		adapter = new ScriptListAdapter(sprite.getBricks());
		adapter.setSelectionListener(this);
		adapter.setReorderItemInterface(this);
		view.setAdapter(adapter);

		ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
		touchHelper = new ItemTouchHelper(callback);
		touchHelper.attachToRecyclerView(view);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (actionMode != null) {
			actionMode.finish();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.options_menu, menu);
		menu.findItem(R.id.btnPaste).setVisible(ClipboardHandler.containsItemsOfType(Brick.class));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.btnPaste:
				pasteItems();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void copyItems(List<Brick> items) {
		actionMode.finish();
		try {
			Clipboard<Brick> clipboard = new Clipboard<>(Brick.class);
			clipboard.addToClipboard(items);
			ClipboardHandler.setClipboard(clipboard);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	protected void pasteItems() {
		try {
			Clipboard<Brick> clipboard = ClipboardHandler.getClipboard();
			List<Brick> itemsToInsert = clipboard.getItemsFromClipboard();

			for (Brick item : itemsToInsert) {
				adapter.addItem(item);
			}
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private void deleteItems(List<Brick> items) {
		actionMode.finish();
		for (Brick brick : items) {
			adapter.removeItem(brick);
		}
	}

	@Override
	public void onSelectionChanged(boolean isSelectionActive) {
		if (isSelectionActive && actionMode == null) {
			actionMode = getActivity().startActionMode(actionModeCallback);
			return;
		}

		if (actionMode != null) {
			actionMode.invalidate();
			if (!isSelectionActive) {
				actionMode.finish();
			}
		}
	}

	@Override
	public void onReorderIconClicked(RecyclerView.ViewHolder viewHolder) {
		touchHelper.startDrag(viewHolder);
	}
}
