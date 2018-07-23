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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

public class DataAdapter extends BaseAdapter {
	private Context context;
	private List<UserList> spriteLists;
	private List<UserList> projectLists;
	private List<UserVariable> userBrickVariables;
	private List<UserVariable> spriteVariables;
	private List<UserVariable> projectVariables;

	private static class ViewHolder {
		private TextView text1;
	}

	public DataAdapter(Context context, List<UserList> spriteLists, List<UserList> projectLists,
			List<UserVariable> spriteVariables, List<UserVariable> projectVariables,
			List<UserVariable> userBrickVariables) {
		this.spriteLists = spriteLists;
		this.projectLists = projectLists;
		this.projectVariables = projectVariables;
		this.spriteVariables = spriteVariables;
		this.userBrickVariables = userBrickVariables;
		this.context = context;
	}

	public void setItemLayout(int itemLayout, int textViewId) {
	}

	@Override
	public int getCount() {
		int count = getProjectListsLastIndex();
		if (count == 0) {
			count = 1;
		}
		return count;
	}

	public int getUserListCount() {
		return spriteLists.size() + projectLists.size();
	}

	public int getUserVariablesCount() {
		return getProjectVariablesLastIndex();
	}

	@Override
	public Object getItem(int position) {
		if (position < getUserBrickVariablesLastIndex()) {
			return userBrickVariables.get(position);
		} else if (position < getSpriteVariablesLastIndex()) {
			return spriteVariables.get(position - getUserBrickVariablesLastIndex());
		} else if (position < getProjectVariablesLastIndex()) {
			return projectVariables.get(position - getSpriteVariablesLastIndex());
		} else if (position < getSpriteListsLastIndex()) {
			return spriteLists.get(position - getProjectVariablesLastIndex());
		} else if (position < getProjectListsLastIndex()) {
			return projectLists.get(position - getSpriteListsLastIndex());
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
		if (position < getUserBrickVariablesLastIndex()) {
			return userBrickVariables.get(position);
		} else if (position < getSpriteVariablesLastIndex()) {
			position -= userBrickVariables.size();
			return spriteVariables.get(position);
		} else if (position < getProjectVariablesLastIndex()) {
			position = position - userBrickVariables.size() - spriteVariables.size();
			return projectVariables.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getViewForUserListItem(final int position, View convertView, ViewGroup parent) {
		return getView(position + spriteVariables.size() + projectVariables.size(), convertView, parent);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		return getDropDownView(position, convertView, parent);
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
			holder.text1 = view.findViewById(android.R.id.text1);
			view.setTag(holder);
		} else if (view.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) view.getTag();
		} else {
			holder = new ViewHolder();
			holder.text1 = view.findViewById(android.R.id.text1);
			view.setTag(holder);
		}
		holder.text1.setText(nameOfCurrentDataItem);
		return view;
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
		if (userVariable == null) {
			return -1;
		}
		for (int index = 0; index < getUserVariablesCount(); index++) {
			if (((UserVariable) getItem(index)).getName().equals(userVariable.getName())) {
				return index;
			}
		}
		return -1;
	}

	public int getUserBrickVariablesLastIndex() {
		return userBrickVariables.size();
	}

	public int getSpriteVariablesLastIndex() {
		return getUserBrickVariablesLastIndex() + spriteVariables.size();
	}

	public int getProjectVariablesLastIndex() {
		return getSpriteVariablesLastIndex() + projectVariables.size();
	}

	public int getSpriteListsLastIndex() {
		return getProjectVariablesLastIndex() + spriteLists.size();
	}

	public int getProjectListsLastIndex() {
		return getSpriteListsLastIndex() + projectLists.size();
	}
}
