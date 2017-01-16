/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.ui.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.LookListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;

import java.util.List;

public class BackPackLookListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler<LookData> {

	public static final String TAG = BackPackLookListFragment.class.getSimpleName();
	public static final String BUNDLE_ARGUMENTS_LOOK_TO_EDIT = "look_to_edit";

	private LookListAdapter lookAdapter;
	private LookData lookToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_backpack, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.looks;
		deleteDialogTitle = R.plurals.dialog_delete_look;

		if (savedInstanceState != null) {
			lookToEdit = (LookData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_LOOK_TO_EDIT);
		}

		initializeList();
	}

	private void initializeList() {
		List<LookData> lookList = BackPackListManager.getInstance().getBackPackedLooks();

		lookAdapter = new LookListAdapter(getActivity(), R.layout.list_item, lookList);
		setListAdapter(lookAdapter);
		lookAdapter.setListItemClickHandler(this);
		lookAdapter.setListItemCheckHandler(this);
		lookAdapter.setListItemLongClickHandler(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_LOOK_TO_EDIT, lookToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadShowDetailsPreferences(LookListFragment.SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		saveCurrentProject();
		putShowDetailsPreferences(LookListFragment.SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		lookToEdit = lookAdapter.getItem(selectedItemPosition);
		menu.setHeaderTitle(lookToEdit.getName());
	}

	@Override
	public void handleOnItemClick(int position, View view, LookData listItem) {
		selectedItemPosition = position;
		getListView().showContextMenuForChild(view);
	}

	@Override
	public void deleteCheckedItems() {
		if (lookAdapter.getCheckedItems().isEmpty()) {
			deleteLook();
			return;
		}
		for (LookData look : lookAdapter.getCheckedItems()) {
			lookToEdit = look;
			deleteLook();
		}
	}

	private void deleteLook() {
		BackPackListManager.getInstance().removeItemFromLookBackPack(lookToEdit);
		checkEmptyBackgroundBackPack();
		lookAdapter.notifyDataSetChanged();
	}

	@Override
	protected void unpackCheckedItems() {
		if (lookAdapter.getCheckedItems().isEmpty()) {
			unpackLook();
			showUnpackingCompleteToast(1);
			return;
		}
		for (LookData lookData : lookAdapter.getCheckedItems()) {
			lookToEdit = lookData;
			unpackLook();
		}
		showUnpackingCompleteToast(lookAdapter.getCheckedItems().size());
		clearCheckedItems();
	}

	private void unpackLook() {
		LookController.unpack(lookToEdit, true);
	}
}
