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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
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
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.ExtendedRVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.RenameItemTextWatcher;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public abstract class RecyclerViewFragment<T extends Nameable> extends Fragment implements
		ActionMode.Callback,
		RVAdapter.SelectionListener,
		RVAdapter.OnItemClickListener<T> {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, BACKPACK, COPY, DELETE, RENAME})
	@interface ActionModeType {}
	protected static final int NONE = 0;
	protected static final int BACKPACK = 1;
	protected static final int COPY = 2;
	protected static final int DELETE = 3;
	protected static final int RENAME = 4;

	protected View parentView;
	protected RecyclerView recyclerView;
	protected TextView emptyView;

	protected ExtendedRVAdapter<T> adapter;
	protected ActionMode actionMode;

	protected String sharedPreferenceDetailsKey = "";

	protected UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	protected ItemTouchHelper touchHelper;

	protected RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {

		@Override
		public void onChanged() {
			super.onChanged();
			setShowEmptyView(shouldShowEmptyView());
		}
	};

	boolean shouldShowEmptyView() {
		return adapter.getItemCount() == 0;
	}

	@ActionModeType
	protected int actionModeType = NONE;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		switch (actionModeType) {
			case BACKPACK:
				mode.setTitle(getString(R.string.am_backpack));
				break;
			case COPY:
				mode.setTitle(getString(R.string.am_copy));
				break;
			case DELETE:
				mode.setTitle(getString(R.string.am_delete));
				break;
			case RENAME:
				adapter.allowMultiSelection = false;
				mode.setTitle(getString(R.string.am_rename));
				break;
			case NONE:
				return false;
		}

		mode.getMenuInflater().inflate(R.menu.context_menu, menu);

		adapter.showCheckBoxes = true;
		adapter.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		updateSelectionToggle(menu.findItem(R.id.toggle_selection));
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.confirm:
				handleContextualAction();
				break;
			case R.id.toggle_selection:
				adapter.toggleSelection();
				updateSelectionToggle(item);
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
				throw new IllegalStateException("ActionModeType not set correctly");
		}
	}

	protected void resetActionModeParameters() {
		actionModeType = NONE;
		actionMode = null;
		adapter.showCheckBoxes = false;
		adapter.allowMultiSelection = true;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parentView.findViewById(R.id.recycler_view);
		emptyView = parentView.findViewById(R.id.empty_view);
		setShowProgressBar(true);
		setHasOptionsMenu(true);
		return parentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		if (getActivity().isFinishing()) {
			return;
		}
		initializeAdapter();
	}

	public void onAdapterReady() {
		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(getActivity())
				.getBoolean(sharedPreferenceDetailsKey, false);
		recyclerView.setAdapter(adapter);

		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);

		ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
		touchHelper = new ItemTouchHelper(callback);
		touchHelper.attachToRecyclerView(recyclerView);

		setShowProgressBar(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		setShowProgressBar(false);

		BackpackListManager.getInstance().loadBackpack();

		adapter.notifyDataSetChanged();
		adapter.registerAdapterDataObserver(observer);
		setShowEmptyView(shouldShowEmptyView());
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
		Context context = getActivity();
		// necessary because of cast! blows up when activity is restored (CATROID-37)
		// see BaseCastActivity
		if (context != null) {
			adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
					context).getBoolean(sharedPreferenceDetailsKey, false);

			menu.findItem(R.id.show_details).setTitle(adapter.showDetails
					? R.string.hide_details
					: R.string.show_details);
		}
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

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		updateSelectionToggle(actionMode.getMenu().findItem(R.id.toggle_selection));
		actionMode.setTitle(getResources()
				.getQuantityString(getActionModeTitleId(actionModeType), selectedItemCnt, selectedItemCnt));
	}

	protected void updateSelectionToggle(MenuItem selectionToggle) {
		if (adapter.allowMultiSelection) {

			selectionToggle.setVisible(true);

			if (adapter.getSelectedItems().size() == adapter.getSelectableItemCount()) {
				selectionToggle.setTitle(R.string.deselect_all);
			} else {
				selectionToggle.setTitle(R.string.select_all);
			}
		}
	}

	protected void finishActionMode() {
		adapter.clearSelection();
		setShowProgressBar(false);

		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	public void setShowProgressBar(boolean show) {
		parentView.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	void setShowEmptyView(boolean visible) {
		emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@PluralsRes
	protected abstract int getActionModeTitleId(@ActionModeType int actionModeType);

	@Override
	public void onItemLongClick(T item, CheckableVH holder) {
		touchHelper.startDrag(holder);
	}

	protected abstract void initializeAdapter();

	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	protected void showBackpackModeChooser() {
		CharSequence[] items = new CharSequence[] {getString(R.string.pack), getString(R.string.unpack)};
		new AlertDialog.Builder(getContext())
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

	protected abstract void packItems(List<T> selectedItems);
	protected abstract boolean isBackpackEmpty();
	protected abstract void switchToBackpack();

	protected abstract void copyItems(List<T> selectedItems);

	@PluralsRes
	protected abstract int getDeleteAlertTitleId();

	protected void showDeleteAlert(final List<T> selectedItems) {
		new AlertDialog.Builder(getContext())
				.setTitle(getResources().getQuantityString(getDeleteAlertTitleId(), selectedItems.size()))
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						deleteItems(selectedItems);
					}
				})
				.setNegativeButton(R.string.no, null)
				.setCancelable(false)
				.create()
				.show();
	}

	protected abstract void deleteItems(List<T> selectedItems);

	protected void showRenameDialog(List<T> selectedItems) {
		final T item = selectedItems.get(0);

		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext());

		builder.setHint(getString(getRenameDialogHint()))
				.setText(item.getName())
				.setTextWatcher(new RenameItemTextWatcher<>(item, adapter.getItems()))
				.setPositiveButton(getString(R.string.ok), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
						renameItem(item, textInput);
					}
				});

		builder.setTitle(getRenameDialogTitle())
				.setNegativeButton(R.string.cancel, null)
				.create()
				.show();
	}

	@StringRes
	protected abstract int getRenameDialogTitle();
	@StringRes
	protected abstract int getRenameDialogHint();

	protected void renameItem(T item, String newName) {
		item.setName(newName);
		finishActionMode();
	}
}
