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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.adapter.BackPackScriptListAdapter;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackScriptController;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class BackPackScriptListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler, CheckBoxListAdapter.ListItemLongClickHandler {

	public static final String TAG = BackPackScriptListFragment.class.getSimpleName();
	private static final String SHARED_PREFERENCE_NAME = "showDetailsScripts";

	private BackPackScriptListAdapter scriptAdapter;
	private ListView listView;

	private String scriptToEdit;
	private int selectedScriptPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View backPackScriptListFragment = inflater.inflate(R.layout.fragment_groups_backpack, container, false);
		listView = (ListView) backPackScriptListFragment.findViewById(android.R.id.list);

		return backPackScriptListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(listView);

		singleItemTitle = getString(R.string.script_group);
		multipleItemsTitle = getString(R.string.script_groups);

		initializeList();
		checkEmptyBackgroundBackPack();
		BottomBar.hideAddButton(getActivity());
	}

	private void initializeList() {
		List<String> groupList = BackPackListManager.getInstance().getAllBackPackedScriptGroups();

		scriptAdapter = new BackPackScriptListAdapter(getActivity(), R.layout.list_item, groupList);
		setListAdapter(scriptAdapter);
		scriptAdapter.setListItemClickHandler(this);
		scriptAdapter.setListItemCheckHandler(this);
		scriptAdapter.setListItemLongClickHandler(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, scriptToEdit);
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

		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));
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

		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (BackPackListManager.getInstance().getBackPackedScripts().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(false);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		scriptToEdit = scriptAdapter.getItem(selectedScriptPosition);
		menu.setHeaderTitle(scriptToEdit);

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
	public void handleOnItemClick(int position, View view, Object listItem) {
		selectedScriptPosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		selectedScriptPosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	protected void showDeleteDialog(boolean singleItem) {
		int titleId;
		if (scriptAdapter.getCheckedItems().size() == 1  || singleItem) {
			titleId = R.string.dialog_confirm_delete_backpack_group_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_backpack_groups_title;
		}

		showDeleteDialog(titleId, singleItem);
	}

	@Override
	protected void deleteCheckedItems(boolean singleItem) {
	}

	protected void unpackCheckedItems(boolean singleItem) {
		if (singleItem) {
			BackPackScriptController.getInstance().unpack(scriptToEdit, false, true, getActivity(), false);
			return;
		}
		for (String script : scriptAdapter.getCheckedItems()) {
			BackPackScriptController.getInstance().unpack(script, false, true, getActivity(), false);
		}
	}
}
