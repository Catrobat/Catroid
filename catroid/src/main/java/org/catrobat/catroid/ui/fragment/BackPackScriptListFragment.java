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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.adapter.BackPackScriptListAdapter;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackScriptController;
import org.catrobat.catroid.ui.controller.OldLookController;

import java.util.List;

public class BackPackScriptListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler {

	public static final String TAG = BackPackScriptListFragment.class.getSimpleName();
	private static final String SHARED_PREFERENCE_NAME = "showDetailsScriptGroups";

	private BackPackScriptListAdapter scriptAdapter;
	private String scriptToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_backpack, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.script_groups;
		deleteDialogTitle = R.plurals.dialog_delete_group;

		initializeList();
	}

	private void initializeList() {
		List<String> groupList = BackPackListManager.getInstance().getBackPackedScriptGroups();

		scriptAdapter = new BackPackScriptListAdapter(getActivity(), R.layout.list_item, groupList);
		setListAdapter(scriptAdapter);
		scriptAdapter.setListItemClickHandler(this);
		scriptAdapter.setListItemCheckHandler(this);
		scriptAdapter.setListItemLongClickHandler(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(OldLookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, scriptToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		saveCurrentProject();
		putShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		scriptToEdit = scriptAdapter.getItem(selectedItemPosition);
		menu.setHeaderTitle(scriptToEdit);

		getActivity().getMenuInflater().inflate(R.menu.context_menu_backpack, menu);
	}

	@Override
	public void handleOnItemClick(int position, View view, Object listItem) {
		selectedItemPosition = position;
		getListView().showContextMenuForChild(view);
	}

	@Override
	public void deleteCheckedItems() {
		if (scriptAdapter.getCheckedItems().isEmpty()) {
			deleteScript();
			return;
		}
		for (String script : scriptAdapter.getCheckedItems()) {
			scriptToEdit = script;
			deleteScript();
		}
	}

	private void deleteScript() {
		BackPackListManager.getInstance().removeItemFromScriptBackPack(scriptToEdit);
		checkEmptyBackgroundBackPack();
		scriptAdapter.remove(scriptToEdit);
	}

	@Override
	protected void unpackCheckedItems() {
		if (scriptAdapter.getCheckedItems().isEmpty()) {
			BackPackScriptController.getInstance().unpack(scriptToEdit, false, true, getActivity(), false);
			showUnpackingCompleteToast(1);
			return;
		}
		for (String script : scriptAdapter.getCheckedItems()) {
			BackPackScriptController.getInstance().unpack(script, false, true, getActivity(), false);
		}
		showUnpackingCompleteToast(scriptAdapter.getCheckedItems().size());
		clearCheckedItems();
	}
}
