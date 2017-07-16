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
import org.catrobat.catroid.gui.adapter.ListItem;
import org.catrobat.catroid.gui.adapter.RecyclerViewAdapter;
import org.catrobat.catroid.gui.adapter.TouchHelperCallback;
import org.catrobat.catroid.gui.dialog.NewItemDialog;
import org.catrobat.catroid.gui.dialog.RenameItemDialog;
import org.catrobat.catroid.storage.DirectoryPathInfo;

import java.io.IOException;
import java.util.List;

public abstract class RecyclerViewListFragment<T extends ListItem> extends Fragment implements
		RecyclerViewAdapter.SelectionListener,
		RecyclerViewAdapter.OnItemClickListener<T>,
		NewItemDialog.NewItemInterface,
		RenameItemDialog.RenameItemInterface {

	public static final String TAG = RecyclerViewListFragment.class.getSimpleName();

	protected RecyclerView view;
	protected RecyclerViewAdapter<T> adapter;
	protected ItemTouchHelper touchHelper;
	protected ActionMode actionMode;

	protected ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			menu.findItem(R.id.btnEdit).setVisible(adapter.getSelectedItemCount() == 1);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			List<T> selectedItems = adapter.getSelectedItems();

			switch (item.getItemId()) {
				case R.id.btnEdit:
					showRenameDialog(selectedItems.get(0).getName());
					break;
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
			actionMode = null;
			adapter.clearSelection();
			getActivity().invalidateOptionsMenu();
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

		adapter = createAdapter();
		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);
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

	protected abstract RecyclerViewAdapter<T> createAdapter();

	protected abstract Class getItemType();

	protected abstract DirectoryPathInfo getCurrentDirectory();

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.options_menu, menu);
		menu.findItem(R.id.btnPaste).setVisible(ClipboardHandler.containsItemsOfType(getItemType()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.btnPaste:
				pasteItems();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}

		return true;
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
	public void clearSelection() {
		actionMode.finish();
	}

	@Override
	public void onReorderIconClick(RecyclerView.ViewHolder viewHolder) {
		touchHelper.startDrag(viewHolder);
	}

	@Override
	public void addItem(String name) {
	}

	protected abstract void showRenameDialog(String name);

	@Override
	public void renameItem(String name) {
		adapter.getSelectedItems().get(0).setName(name);
		actionMode.finish();
	}

	protected void copyItems(List<T> items) {
		actionMode.finish();
		try {
			Clipboard<T> clipboard = new Clipboard<>(getItemType());
			clipboard.addToClipboard(items);
			ClipboardHandler.setClipboard(clipboard);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	protected void pasteItems() {
		try {
			Clipboard<T> clipboard = ClipboardHandler.getClipboard();
			List<T> items = clipboard.getItemsFromClipboard();

			for (T item : items) {
				item.setName(getUniqueItemName(item.getName()));
				item.copyResourcesToDirectory(getCurrentDirectory());
				adapter.addItem(item);
			}
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	protected void deleteItems(List<T> items) {
		actionMode.finish();
		for (T item : items) {
			try {
				item.removeResources();
				adapter.removeItem(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}

	protected String getUniqueItemName(String name) {

		if (isItemNameUnique(name)) {
			return name;
		}

		int i;
		String prefix;

		try {
			prefix = name.substring(0, name.lastIndexOf('('));
			prefix = prefix.trim();
			String suffix = name.substring(name.lastIndexOf('(') + 1, name.lastIndexOf(')'));
			i = Integer.parseInt(suffix);
			i++;
		} catch (Exception e) {
			prefix = name;
			i = 1;
		}

		return getUniqueItemName(prefix + " (" + i + ")");
	}

	@Override
	public boolean isItemNameUnique(String name) {
		return adapter.isItemNameUnique(name);
	}
}
