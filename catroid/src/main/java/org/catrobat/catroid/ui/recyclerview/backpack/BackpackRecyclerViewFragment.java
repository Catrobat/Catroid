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

package org.catrobat.catroid.ui.recyclerview.backpack;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.adapter.ExtendedRVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BackpackRecyclerViewFragment<T> extends Fragment implements
		ActionMode.Callback,
		RVAdapter.SelectionListener,
		RVAdapter.OnItemClickListener<T> {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, UNPACK, DELETE})
	@interface ActionModeType {}
	protected static final int NONE = 0;
	protected static final int UNPACK = 1;
	protected static final int DELETE = 2;

	protected View parentView;
	protected RecyclerView recyclerView;

	protected ExtendedRVAdapter<T> adapter;
	protected ActionMode actionMode;

	protected String sharedPreferenceDetailsKey = "";
	public boolean hasDetails = false;

	protected ItemTouchHelper touchHelper;

	@ActionModeType
	protected int actionModeType = NONE;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		switch (actionModeType) {
			case UNPACK:
				mode.setTitle(getString(R.string.am_unpack));
				break;
			case DELETE:
				mode.setTitle(getString(R.string.am_delete));
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
	}

	private void handleContextualAction() {
		if (adapter.getSelectedItems().isEmpty()) {
			actionMode.finish();
			return;
		}

		switch (actionModeType) {
			case UNPACK:
				unpackItems(adapter.getSelectedItems());
				break;
			case DELETE:
				showDeleteAlert(adapter.getSelectedItems());
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
		}
	}

	protected void resetActionModeParameters() {
		actionModeType = NONE;
		actionMode = null;
		adapter.showCheckBoxes = false;
		adapter.selectionMode = adapter.MULTIPLE;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parentView.findViewById(R.id.recycler_view);
		setHasOptionsMenu(true);
		return parentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		initializeAdapter();
	}

	public void onAdapterReady() {
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
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onStop() {
		super.onStop();
		finishActionMode();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (hasDetails) {
			adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
					getActivity()).getBoolean(sharedPreferenceDetailsKey, false);

			menu.findItem(R.id.show_details).setTitle(adapter.showDetails
					? R.string.hide_details
					: R.string.show_details);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.unpack:
				startActionMode(UNPACK);
				break;
			case R.id.delete:
				startActionMode(DELETE);
				break;
			case R.id.show_details:
				adapter.showDetails = !adapter.showDetails;
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.edit()
						.putBoolean(sharedPreferenceDetailsKey, adapter.showDetails)
						.apply();
				adapter.notifyDataSetChanged();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void startActionMode(@ActionModeType int type) {
		if (adapter.getItems().isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
		} else {
			actionModeType = type;
			actionMode = getActivity().startActionMode(this);
		}
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		switch (actionModeType) {
			case UNPACK:
				actionMode.setTitle(getString(R.string.am_unpack) + " " + selectedItemCnt);
				break;
			case DELETE:
				actionMode.setTitle(getString(R.string.am_delete) + " " + selectedItemCnt);
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
		}
	}

	protected void finishActionMode() {
		adapter.clearSelection();
		setShowProgressBar(false);

		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	protected void showDeleteAlert(final List<T> selectedItems) {
		new AlertDialog.Builder(getContext())
				.setTitle(getResources().getQuantityString(getDeleteAlertTitleId(), selectedItems.size()))
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						deleteItems(selectedItems);
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.setCancelable(false)
				.show();
	}

	@Override
	public void onItemClick(final T item) {
		if (actionModeType != NONE) {
			return;
		}
		CharSequence[] items = new CharSequence[] {getString(R.string.unpack), getString(R.string.delete)};
		new AlertDialog.Builder(getContext())
				.setTitle(getItemName(item))
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								unpackItems(new ArrayList<>(Collections.singletonList(item)));
								break;
							case 1:
								showDeleteAlert(new ArrayList<>(Collections.singletonList(item)));
						}
					}
				})
				.show();
	}

	public void setShowProgressBar(boolean show) {
		parentView.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onItemLongClick(T item, CheckableVH holder) {
		onItemClick(item);
	}

	protected abstract void initializeAdapter();

	protected abstract void unpackItems(List<T> selectedItems);

	@PluralsRes
	protected abstract int getDeleteAlertTitleId();
	protected abstract void deleteItems(List<T> selectedItems);

	protected abstract String getItemName(T item);
}
