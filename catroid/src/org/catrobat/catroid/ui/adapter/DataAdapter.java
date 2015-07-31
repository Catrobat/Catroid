/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class DataAdapter extends BaseAdapter implements ScriptActivityAdapterInterface {
	private Context context;
	private List<UserVariable> userBrickVariables;
	private List<UserList> spriteLists;
	private List<UserList> projectLists;
	private List<UserVariable> spriteVariables;
	private List<UserVariable> projectVariables;
	private int selectMode;
	private SortedSet<Integer> checkedItems = new TreeSet<Integer>();
	private OnCheckedChangeListener onCheckedChangeListener = null;
	private OnListItemClickListener onListItemClickListener = null;
	private int itemLayout;
	private int checkboxId;
	private int textViewId;
	private int textViewId2;
	private int linearLayoutLocalId;
	private int linearLayoutGlobalId;
	private int linearLayoutUserListId;
	private int linearLayoutUserListAboveItemId;
	private int linearLayoutUserVariablesId;
	private int spinnerUserListValuesId;
	private int linearLayoutUserBrickId;

	private static class ViewHolder {
		private CheckBox checkbox;
		private TextView text1;
		private TextView text2;
		private LinearLayout localHeadline;
		private LinearLayout globalHeadline;
		private LinearLayout userListsHeadline;
		private LinearLayout userListsHeadlineAboveItem;
		private LinearLayout userVariablesHeadline;
		private Spinner userListValuesSpinner;
		private LinearLayout userbrickHeadline;
	}

	public DataAdapter(Context context, List<UserList> spriteLists, List<UserList> projectLists, List<UserVariable> spriteVariables, List<UserVariable> projectVariables, List<UserVariable> userBrickVariables) {
		this.spriteLists = spriteLists;
		this.projectLists = projectLists;
		this.projectVariables = projectVariables;
		this.spriteVariables = spriteVariables;
		this.context = context;
		this.userBrickVariables = userBrickVariables;
		this.selectMode = ListView.CHOICE_MODE_NONE;
		this.itemLayout = R.layout.fragment_formula_editor_data_list_item;
		this.checkboxId = R.id.fragment_formula_editor_datalist_item_checkbox;
		this.textViewId = R.id.fragment_formula_editor_datalist_item_name_text_view;
		this.textViewId2 = R.id.fragment_formula_editor_data_list_item_value_text_view;
		this.linearLayoutGlobalId = R.id.data_global_headline;
		this.linearLayoutLocalId = R.id.data_local_headline;
		this.linearLayoutUserListId = R.id.data_user_lists_headline;
		this.linearLayoutUserVariablesId = R.id.data_user_variables_headline;
		this.linearLayoutUserListAboveItemId = R.id.data_user_lists_headline_above_item;
		this.spinnerUserListValuesId = R.id.fragment_formula_editor_data_list_item_spinner;
		this.linearLayoutUserBrickId = R.id.variablelist_userbrick_headline;
	}

	public void setItemLayout(int itemLayout, int textViewId) {
		this.itemLayout = itemLayout;
		this.textViewId = textViewId;
	}

	public int getItemLayout() {
		return itemLayout;
	}

	@Override
	public int getCount() {
		int count = spriteLists.size() + projectLists.size() + spriteVariables.size() + projectVariables.size();
		if (count == 0) {
			count = 1;
		}
		return count;
	}

	public int getUserListCount() {
		return spriteLists.size() + projectLists.size();
	}

	public int getUserVariablesCount() {
		return spriteVariables.size() + projectVariables.size() + userBrickVariables.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < userBrickVariables.size()) {
			return userBrickVariables.get(position);
		} else if (position < userBrickVariables.size() + spriteVariables.size()) {
			return spriteVariables.get(position - userBrickVariables.size());
		} else if (position < userBrickVariables.size() + spriteVariables.size() + projectVariables.size()) {
			return projectVariables.get(position - (spriteVariables.size() + userBrickVariables.size()));
		} else if (position < userBrickVariables.size() + spriteVariables.size() + projectVariables.size() + spriteLists.size()) {
			return spriteLists.get(position - (spriteVariables.size() + projectVariables.size() + userBrickVariables.size()));
		} else if (position < userBrickVariables.size() + spriteVariables.size() + projectVariables.size() + spriteLists.size() + projectLists.size()) {
			return projectLists.get(position - (spriteVariables.size() + projectVariables.size() + spriteLists.size() + userBrickVariables.size()));
		}
		return null;
	}

	public UserList getUserListItem(int position) {
		if (position < spriteLists.size()) {
			return spriteLists.get(position);
		} else if (position < spriteLists.size() + projectLists.size()) {
			return projectLists.get(position - spriteLists.size());
		}
		return null;
	}

	public UserVariable getUserVariableItem(int position) {
		if (position < spriteVariables.size()) {
			return spriteVariables.get(position);
		} else if (position < spriteVariables.size() + projectVariables.size()) {
			return projectVariables.get(position - spriteVariables.size());
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
		this.onListItemClickListener = onListItemClickListener;
	}

	public View getViewForUserListItem(final int position, View convertView, ViewGroup parent) {
		return getView(position + spriteVariables.size() + projectVariables.size(), convertView, parent);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;
		ViewHolder holder;

		if (spriteLists.size() + projectLists.size() + spriteVariables.size() + projectVariables.size() + userBrickVariables.size() == 0) {
			view = View.inflate(context, itemLayout, null);
			holder = new ViewHolder();
			holder.userListsHeadline = (LinearLayout) view.findViewById(linearLayoutUserListId);
			holder.userVariablesHeadline = (LinearLayout) view.findViewById(linearLayoutUserVariablesId);
			holder.userVariablesHeadline.setVisibility(View.VISIBLE);
			holder.userListsHeadline.setVisibility(View.VISIBLE);
			LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_formula_editor_data_list_item_layout);
			linearLayout.setVisibility(View.GONE);
			return view;
		}

		String nameOfCurrentDataItem = "";
		Object currentDataItem = getItem(position);
		if (currentDataItem instanceof UserVariable) {
			nameOfCurrentDataItem = ((UserVariable) currentDataItem).getName();
		} else {
			nameOfCurrentDataItem = ((UserList) currentDataItem).getName();
		}

		if (view == null || !(view.getTag() instanceof ViewHolder)) {
			view = View.inflate(context, itemLayout, null);
			holder = new ViewHolder();
			holder.checkbox = (CheckBox) view.findViewById(checkboxId);
			holder.text1 = (TextView) view.findViewById(textViewId);
			holder.text2 = (TextView) view.findViewById(textViewId2);
			holder.localHeadline = (LinearLayout) view.findViewById(linearLayoutLocalId);
			holder.globalHeadline = (LinearLayout) view.findViewById(linearLayoutGlobalId);
			holder.userListsHeadline = (LinearLayout) view.findViewById(linearLayoutUserListId);
			holder.userListsHeadlineAboveItem = (LinearLayout) view.findViewById(linearLayoutUserListAboveItemId);
			holder.userVariablesHeadline = (LinearLayout) view.findViewById(linearLayoutUserVariablesId);
			holder.userListValuesSpinner = (Spinner) view.findViewById(spinnerUserListValuesId);
			holder.userbrickHeadline = (LinearLayout) view.findViewById(linearLayoutUserBrickId);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (holder.text1 != null) {
			if (currentDataItem instanceof UserVariable) {
				holder.text1.setText(nameOfCurrentDataItem + ":");
			} else {
				holder.text1.setText(nameOfCurrentDataItem);
			}
		}

		if (holder.text2 != null) {
			if (currentDataItem instanceof UserVariable) {
				holder.text2.setVisibility(View.VISIBLE);
				holder.text2.setText(String.valueOf(((UserVariable) currentDataItem).getValue()));
			} else {
				holder.text2.setVisibility(View.GONE);
			}
		}

		if (holder.localHeadline != null && holder.globalHeadline != null && holder.userListsHeadline != null && holder.userVariablesHeadline != null) {
			holder.localHeadline.setVisibility(View.GONE);
			holder.globalHeadline.setVisibility(View.GONE);
			holder.userListsHeadline.setVisibility(View.GONE);
			holder.userVariablesHeadline.setVisibility(View.GONE);
			holder.userListsHeadlineAboveItem.setVisibility(View.GONE);
			holder.userbrickHeadline.setVisibility(View.GONE);

			if (position == 0) {
				holder.userVariablesHeadline.setVisibility(View.VISIBLE);
			}

			int sizeOfVariables = spriteVariables.size() + projectVariables.size() + userBrickVariables.size();
			if (sizeOfVariables == 0 && position == 0) {

				holder.userListsHeadlineAboveItem.setVisibility(View.VISIBLE);
			} else if (sizeOfVariables != 0 && position == sizeOfVariables - 1) {
				holder.userListsHeadline.setVisibility(View.VISIBLE);
			}

			if (userBrickVariables.size() != 0 && position == 0) {
				holder.userbrickHeadline.setVisibility(View.VISIBLE);
			} else if (spriteVariables.size() != 0 && position == userBrickVariables.size()) {
				holder.localHeadline.setVisibility(View.VISIBLE);
			} else if (projectVariables.size() != 0 && position == userBrickVariables.size() + spriteVariables.size()) {
				holder.globalHeadline.setVisibility(View.VISIBLE);
			} else if (spriteLists.size() != 0 && position == userBrickVariables.size() + spriteVariables.size() + projectVariables.size()) {
				holder.localHeadline.setVisibility(View.VISIBLE);
			} else if (projectLists.size() != 0 && position == userBrickVariables.size() + spriteVariables.size() + projectVariables.size() + spriteLists.size()) {
				holder.globalHeadline.setVisibility(View.VISIBLE);
			}
		}

		if (holder.userListValuesSpinner != null) {
			if (currentDataItem instanceof UserList) {
				UserList userList = (UserList) currentDataItem;
				holder.userListValuesSpinner.setVisibility(view.VISIBLE);
				List<String> userListEntries = new ArrayList<String>();
				for (Object userListItem : userList.getList()) {
					userListEntries.add(userListItem.toString());
				}

				UserListValuesAdapter userListValuesAdapter = new UserListValuesAdapter(view.getContext(), userListEntries);

				holder.userListValuesSpinner.setAdapter(userListValuesAdapter);
			} else {
				holder.userListValuesSpinner.setVisibility(view.GONE);
			}
		}

		if (onListItemClickListener != null) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onListItemClickListener.onListItemClick(position);
				}
			});
		}
		if (holder.checkbox == null) {
			return view;
		}

		view.setClickable(true);
		view.setFocusable(true);

		if (selectMode != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			clearCheckedItems();
		}

		holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedItems();
					}
					checkedItems.add(position);
				} else {
					checkedItems.remove(position);
				}
				if (onCheckedChangeListener != null) {
					onCheckedChangeListener.onCheckedChange();
				}
				notifyDataSetChanged();
			}
		});

		if (checkedItems.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}

		return view;
	}

	public View getDropDownViewForUserListItem(final int position, View convertView, ViewGroup parent) {
		return getDropDownView(position + spriteVariables.size() + projectVariables.size(), convertView, parent);
	}

	@Override
	public View getDropDownView(final int position, View convertView, ViewGroup parent) {
		String nameOfCurrentDataItem = "";
		Object currentDataItem = getItem(position);
		if (currentDataItem instanceof UserVariable) {
			nameOfCurrentDataItem = ((UserVariable) currentDataItem).getName();
		} else {
			nameOfCurrentDataItem = ((UserList) currentDataItem).getName();
		}

		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		} else if (view.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) view.getTag();
		} else {
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		}
		holder.text1.setText(nameOfCurrentDataItem);
		return view;
	}

	@Override
	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
	}

	@Override
	public boolean getShowDetails() {
		return false;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return checkedItems.size();
	}

	@Override
	public SortedSet<Integer> getCheckedItems() {
		return checkedItems;
	}

	public List<UserList> getCheckedUserLists() {
		List<UserList> userLists = new ArrayList<UserList>();
		for (int pos : getCheckedItems()) {
			Object checkedItem = getItem(pos);
			if (checkedItem instanceof UserList) {

				userLists.add((UserList) checkedItem);
			}
		}
		return userLists;
	}

	public List<UserVariable> getCheckedUserVariables() {
		List<UserVariable> userVariables = new ArrayList<UserVariable>();
		if (getCheckedItems().size() == 0) {
			return userVariables;
		}
		for (int pos : getCheckedItems()) {
			Object checkedItem = getItem(pos);
			if (checkedItem instanceof UserVariable) {
				userVariables.add((UserVariable) checkedItem);
			}
		}
		return userVariables;
	}

	public int getPositionOfUserListItem(UserList userList) {
		for (int index = 0; index < getUserListCount(); index++) {
			if (((UserList) getItem(spriteVariables.size() + projectVariables.size() + index)).getName().equals(userList.getName())) {
				return index;
			}
		}
		return -1;
	}

	public int getPositionOfUserVariableItem(UserVariable userVariable) {
		for (int index = 0; index < getUserVariablesCount(); index++) {
			if (((UserVariable) getItem(index)).getName().equals(userVariable.getName())) {
				return index;
			}
		}
		return -1;
	}

	public void addCheckedItem(int position) {
		checkedItems.add(position);
	}

	@Override
	public void clearCheckedItems() {
		checkedItems.clear();
	}

	public interface OnCheckedChangeListener {

		void onCheckedChange();
	}

	public interface OnListItemClickListener {

		void onListItemClick(int position);
	}
}
