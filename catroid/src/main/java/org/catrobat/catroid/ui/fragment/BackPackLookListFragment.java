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
package org.catrobat.catroid.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.LookListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class BackPackLookListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler<LookData>, CheckBoxListAdapter.ListItemLongClickHandler {

	public static final String TAG = BackPackLookListFragment.class.getSimpleName();

	private LookListAdapter lookAdapter;
	private ListView listView;

	private LookData lookToEdit;
	private int selectedLookPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View backPackLookListFragment = inflater.inflate(R.layout.fragment_backpack, container, false);
		listView = (ListView) backPackLookListFragment.findViewById(android.R.id.list);

		return backPackLookListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(listView);

		singleItemTitle = getString(R.string.look);
		multipleItemsTitle = getString(R.string.looks);

		if (savedInstanceState != null) {
			lookToEdit = (LookData) savedInstanceState.getSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK);
		}

		initializeList();
		checkEmptyBackgroundBackPack();
		BottomBar.hideBottomBar(getActivity());
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
		outState.putSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, lookToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(LookController.SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
		}

		BackPackListManager.getInstance().saveBackpack();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(LookController.SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (BackPackListManager.getInstance().getBackPackedLooks().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(false);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		lookToEdit = lookAdapter.getItem(selectedLookPosition);
		menu.setHeaderTitle(lookToEdit.getLookName());

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_unpacking:
				unpackCheckedItems(true);
				break;
			case R.id.context_menu_delete:
				showDeleteDialog(true);
				break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void handleOnItemClick(int position, View view, LookData listItem) {
		selectedLookPosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		selectedLookPosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	protected void showDeleteDialog(boolean singleItem) {
		int titleId;
		if (lookAdapter.getCheckedItems().size() == 1 || singleItem) {
			titleId = R.string.dialog_confirm_delete_look_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_looks_title;
		}
		showDeleteDialog(titleId, singleItem);
	}

	@Override
	protected void deleteCheckedItems(boolean singleItem) {
		if (singleItem) {
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

	protected void unpackCheckedItems(boolean singleItem) {
		if (singleItem) {
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
		LookController.getInstance().unpack(lookToEdit, false, false);
	}
}
