/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.ExtendedRVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public abstract class RecyclerViewFragment<T> extends Fragment implements
		ActionMode.Callback,
		RVAdapter.SelectionListener,
		RVAdapter.OnItemClickListener<T>,
		NewItemInterface<T>,
		RenameDialogFragment.RenameInterface {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, BACKPACK, COPY, DELETE, RENAME})
	@interface ActionModeType {}
	protected static final int NONE = 0;
	protected static final int BACKPACK = 1;
	protected static final int COPY = 2;
	protected static final int DELETE = 3;
	protected static final int RENAME = 4;

	protected View parent;
	protected RecyclerView recyclerView;
	protected TextView emptyView;
	protected ExtendedRVAdapter<T> adapter;
	protected ActionMode actionMode;
	protected String actionModeTitle = "";
	protected String sharedPreferenceDetailsKey = "";

	protected UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	protected ItemTouchHelper touchHelper;

	protected RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			super.onItemRangeRemoved(positionStart, itemCount);
			setShowEmptyView(false);
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			super.onItemRangeRemoved(positionStart, itemCount);
			setShowEmptyView(adapter.getItemCount() == 0);
		}
	};

	@ActionModeType
	protected int actionModeType = NONE;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		switch (actionModeType) {
			case BACKPACK:
				actionModeTitle = getString(R.string.am_backpack);
				break;
			case COPY:
				actionModeTitle = getString(R.string.am_copy);
				break;
			case DELETE:
				actionModeTitle = getString(R.string.am_delete);
				break;
			case RENAME:
				adapter.allowMultiSelection = false;
				actionModeTitle = getString(R.string.am_rename);
				break;
			case NONE:
				return false;
		}

		mode.getMenuInflater().inflate(R.menu.context_menu, menu);
		mode.setTitle(actionModeTitle);

		adapter.showCheckBoxes = true;
		adapter.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.confirm:
				handleContextualAction();
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		resetActionModeParameters();
		adapter.clearSelection();
		BottomBar.showBottomBar(getActivity());
	}

	private void handleContextualAction() {
		if (adapter.getSelectedItems().isEmpty()) {
			actionMode.finish();
			return;
		}

		switch (actionModeType) {
			case BACKPACK:
				packItems(adapter.getSelectedItems());
				break;
			case COPY:
				copyItems(adapter.getSelectedItems());
				break;
			case DELETE:
				showDeleteAlert(adapter.getSelectedItems());
				break;
			case RENAME:
				showRenameDialog(adapter.getSelectedItems());
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
		}
	}

	protected void resetActionModeParameters() {
		actionModeType = NONE;
		actionModeTitle = "";
		adapter.showCheckBoxes = false;
		adapter.allowMultiSelection = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parent = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parent.findViewById(R.id.recycler_view);
		emptyView = parent.findViewById(R.id.empty);
		setShowProgressBar(true);
		setHasOptionsMenu(true);
		return parent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		initializeAdapter();
	}

	public void onAdapterReady() {
		setShowProgressBar(false);

		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(sharedPreferenceDetailsKey, false);
		recyclerView.setAdapter(adapter);

		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);

		ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
		touchHelper = new ItemTouchHelper(callback);
		touchHelper.attachToRecyclerView(recyclerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		setShowProgressBar(false);

		BackpackListManager.getInstance().loadBackpack();

		adapter.notifyDataSetChanged();
		adapter.registerAdapterDataObserver(observer);
		setShowEmptyView(adapter.getItemCount() == 0);
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager.getInstance().saveProject(getActivity());
		adapter.unregisterAdapterDataObserver(observer);
	}

	@Override
	public void onStop() {
		super.onStop();
		finishActionMode();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(sharedPreferenceDetailsKey, false);

		menu.findItem(R.id.show_details).setTitle(adapter.showDetails
				? R.string.hide_details
				: R.string.show_details);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.backpack:
				prepareActionMode(BACKPACK);
				break;
			case R.id.copy:
				prepareActionMode(COPY);
				break;
			case R.id.delete:
				prepareActionMode(DELETE);
				break;
			case R.id.rename:
				prepareActionMode(RENAME);
				break;
			case R.id.show_details:
				adapter.showDetails = !adapter.showDetails;
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.edit()
						.putBoolean(sharedPreferenceDetailsKey, adapter.showDetails)
						.commit();
				adapter.notifyDataSetChanged();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	protected void prepareActionMode(@ActionModeType int type) {
		if (type == BACKPACK) {
			if (isBackpackEmpty()) {
				startActionMode(BACKPACK);
			} else if (adapter.getItems().isEmpty()) {
				switchToBackpack();
			} else {
				showBackpackModeChooser();
			}
		} else {
			startActionMode(type);
		}
	}

	private void startActionMode(@ActionModeType int type) {
		if (adapter.getItems().isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
			resetActionModeParameters();
		} else {
			actionModeType = type;
			actionMode = getActivity().startActionMode(this);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	protected void finishActionMode() {
		adapter.clearSelection();
		setShowProgressBar(false);

		if (actionModeType != NONE) {
			actionMode.finish();
			actionMode = null;
		}
	}

	protected void showBackpackModeChooser() {
		CharSequence[] items = new CharSequence[] {getString(R.string.pack), getString(R.string.unpack)};
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.backpack_title)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								startActionMode(BACKPACK);
								break;
							case 1:
								switchToBackpack();
						}
					}
				})
				.show();
	}

	protected void showDeleteAlert(final List<T> selectedItems) {
		new AlertDialog.Builder(getActivity())
				.setTitle(getResources().getQuantityString(getDeleteAlertTitle(), selectedItems.size()))
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						deleteItems(selectedItems);
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						finishActionMode();
						dialog.dismiss();
					}
				})
				.setCancelable(false)
				.create()
				.show();
	}

	public void setShowProgressBar(boolean show) {
		parent.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	protected void setShowEmptyView(boolean visible) {
		recyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
		emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onItemLongClick(T item, ViewHolder holder) {
		touchHelper.startDrag(holder);
	}

	protected abstract void initializeAdapter();

	public abstract void handleAddButton();

	@Override
	public abstract void addItem(T item);

	protected abstract void packItems(List<T> selectedItems);
	protected abstract boolean isBackpackEmpty();
	protected abstract void switchToBackpack();

	protected abstract void copyItems(List<T> selectedItems);

	protected abstract int getDeleteAlertTitle();
	protected abstract void deleteItems(List<T> selectedItems);

	protected abstract void showRenameDialog(List<T> selectedItems);

	@Override
	public abstract boolean isNameUnique(String name);
}
