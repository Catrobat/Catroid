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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BackPackGroupViewHolder;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BackPackUserBrickAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackUserBrickController;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.utils.Utils;

public class BackPackUserBrickFragment extends BackPackActivityFragment {

	public static final String TAG = BackPackUserBrickFragment.class.getSimpleName();
	private static final String SHARED_PREFERENCE_NAME = "showDetailsUserBricks";

	private BackPackUserBrickAdapter adapter;
	private String selectedUserBrickGroupBackPack;
	private int selectedUserBrickGroupPosition;

	private ListView listView;

	protected String singleItemAppendixActionMode;
	protected String multipleItemAppendixActionMode;

	private UserBrickGroupDeletedReceiver userBrickGroupDeletedReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_groups_backpack, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();
		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);
		listView.setLongClickable(false);

		adapter = new BackPackUserBrickAdapter(getActivity(), R.layout.fragment_group_backpack_item, R.id
				.fragment_group_backpack_item_name_text_view, BackPackListManager.getInstance()
				.getBackPackedUserBrickGroups(), this);
		setListAdapter(adapter);
		checkEmptyBackgroundBackPack();
		initClickListener();

		singleItemAppendixActionMode = getString(R.string.userbrick_group);
		multipleItemAppendixActionMode = getString(R.string.userbrick_groups);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(false);
		if (!BackPackListManager.getInstance().getBackPackedUserBricks().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(true);
		}

		BottomBar.hideBottomBar(getActivity());
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		selectedUserBrickGroupBackPack = adapter.getItem(selectedUserBrickGroupPosition);
		menu.setHeaderTitle(selectedUserBrickGroupBackPack);
		adapter.addCheckedItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_unpacking:
				BackPackUserBrickController.getInstance().unpack(selectedUserBrickGroupBackPack, false, getActivity());
				break;
			case R.id.context_menu_delete:
				showDeleteDialog(true);
				break;
		}
		return super.onContextItemSelected(item);
	}

	protected void showDeleteDialog(boolean singleItem) {
		int titleId;
		if (adapter.getAmountOfCheckedItems() == 1) {
			titleId = R.string.dialog_confirm_delete_backpack_group_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_backpack_groups_title;
		}
		showDeleteDialog(titleId, singleItem);
	}

	private void initClickListener() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedUserBrickGroupPosition = position;
				listView.showContextMenuForChild(view);
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedUserBrickGroupBackPack);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void deleteCheckedItems(boolean singleItem) {
	}

	@Override
	protected void unpackCheckedItems(boolean singleItem) {
	}

	public View getView(final int position, View convertView) {
		BackPackGroupViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(getActivity(), R.layout.fragment_group_backpack_item, null);

			holder = new BackPackGroupViewHolder();

			holder.backPackGroupImageView = (ImageView) convertView.findViewById(R.id.fragment_group_backpack_item_image_view);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_group_backpack_item_checkbox);
			holder.backPackGroupNameTextView = (TextView) convertView.findViewById(R.id.fragment_group_backpack_item_name_text_view);
			holder.backPackGroupDetailsLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_group_backpack_item_detail_linear_layout);
			holder.backPackGroupNumberOfBricksTextView = (TextView) holder.backPackGroupDetailsLinearLayout
					.findViewById(R.id.fragment_group_backpack_item_number_bricks_text_view);
			holder.backPackGroupNumberOfBricksValue = (TextView) holder.backPackGroupDetailsLinearLayout
					.findViewById(R.id.fragment_group_backpack_item_number_bricks_value);
			holder.backPackGroupElement = (RelativeLayout) convertView.findViewById(R.id.fragment_group_backpack_item_relative_layout);
			convertView.setTag(holder);
		} else {
			holder = (BackPackGroupViewHolder) convertView.getTag();
		}

		holder.backPackGroupElement.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					selectedUserBrickGroupPosition = position;
					listView.showContextMenuForChild(v);
				}
				return false;
			}
		});

		adapter.updateUserBrickGroupLogic(position, holder);
		return convertView;
	}

	private class UserBrickGroupDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_USERBRICK_GROUP_DELETED)) {
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (userBrickGroupDeletedReceiver == null) {
			userBrickGroupDeletedReceiver = new UserBrickGroupDeletedReceiver();
		}

		IntentFilter intentFilterDeleteLook = new IntentFilter(ScriptActivity.ACTION_LOOK_DELETED);
		getActivity().registerReceiver(userBrickGroupDeletedReceiver, intentFilterDeleteLook);

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

		if (userBrickGroupDeletedReceiver != null) {
			getActivity().unregisterReceiver(userBrickGroupDeletedReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	public BackPackUserBrickAdapter getAdapter() {
		return adapter;
	}

	public void onUserBrickGroupChecked() {
		if (actionMode == null) {
			return;
		}

		updateActionModeTitle();
	}
}
