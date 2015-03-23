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
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class UserVariableAdapter extends BaseAdapter implements ScriptActivityAdapterInterface {
	private Context context;
	private List<UserVariable> brickVariables;
	private List<UserVariable> spriteVariables;
	private List<UserVariable> projectVariables;
	private int selectMode;
	private SortedSet<Integer> checkedVariables = new TreeSet<Integer>();
	private OnCheckedChangeListener onCheckedChangeListener = null;
	private OnListItemClickListener onListItemClickListener = null;
	private int itemLayout;
	private int checkboxId;
	private int textViewId;
	private int textViewId2;
	private int linearLayoutLocalId;
	private int linearLayoutGlobalId;
	private int linearLayoutUserBrickId;

	private static class ViewHolder {
		private CheckBox checkbox;
		private TextView text1;
		private TextView text2;
		private LinearLayout userbrickHeadline;
		private LinearLayout localHeadline;
		private LinearLayout globalHeadline;
	}

	public UserVariableAdapter(Context context, List<UserVariable> brickVariables, List<UserVariable> spriteVariables,
			List<UserVariable> projectVariables) {
		this.brickVariables = brickVariables;
		this.spriteVariables = spriteVariables;
		this.projectVariables = projectVariables;
		this.context = context;
		this.selectMode = ListView.CHOICE_MODE_NONE;
		this.itemLayout = R.layout.fragment_formula_editor_variablelist_item;
		this.checkboxId = R.id.fragment_formula_editor_variablelist_item_checkbox;
		this.textViewId = R.id.fragment_formula_editor_variablelist_item_name_text_view;
		this.textViewId2 = R.id.fragment_formula_editor_variablelist_item_value_text_view;
		this.linearLayoutGlobalId = R.id.variablelist_global_headline;
		this.linearLayoutLocalId = R.id.variablelist_local_headline;
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
		return brickVariables.size() + spriteVariables.size() + projectVariables.size();
	}

	@Override
	public UserVariable getItem(int position) {
		if (position < brickVariables.size()) {
			return brickVariables.get(position);
		} else if (position < brickVariables.size() + spriteVariables.size()) {
			return spriteVariables.get(position - brickVariables.size());
		} else {
			return projectVariables.get(position - (brickVariables.size() + spriteVariables.size()));
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getPositionOfItem(UserVariable item) {
		for (int i = 0; i < getCount(); i++) {
			if (getItem(i).getName().equals(item.getName())) {
				return i;
			}
		}
		return -1;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
		this.onListItemClickListener = onListItemClickListener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		UserVariable variable = getItem(position);
		View view = convertView;
		ViewHolder holder;
		if (view == null || !(view.getTag() instanceof ViewHolder)) {
			view = View.inflate(context, itemLayout, null);
			holder = new ViewHolder();
			holder.checkbox = (CheckBox) view.findViewById(checkboxId);
			holder.text1 = (TextView) view.findViewById(textViewId);
			holder.text2 = (TextView) view.findViewById(textViewId2);
			holder.userbrickHeadline = (LinearLayout) view.findViewById(linearLayoutUserBrickId);
			holder.localHeadline = (LinearLayout) view.findViewById(linearLayoutLocalId);
			holder.globalHeadline = (LinearLayout) view.findViewById(linearLayoutGlobalId);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.text1.setText(variable.getName() + ":");
		if (holder.text2 != null) {
			holder.text2.setText(String.valueOf(variable.getValue()));
		}

		if (holder.localHeadline != null && holder.userbrickHeadline != null && holder.globalHeadline != null) {
			if (brickVariables.size() != 0 && position == 0) {
				holder.localHeadline.setVisibility(View.GONE);
				holder.globalHeadline.setVisibility(View.GONE);
				holder.userbrickHeadline.setVisibility(View.VISIBLE);
			} else if (spriteVariables.size() != 0 && position == brickVariables.size()) {
				holder.localHeadline.setVisibility(View.VISIBLE);
				holder.globalHeadline.setVisibility(View.GONE);
				holder.userbrickHeadline.setVisibility(View.GONE);
			} else if (projectVariables.size() != 0 && position == brickVariables.size() + spriteVariables.size()) {
				holder.localHeadline.setVisibility(View.GONE);
				holder.globalHeadline.setVisibility(View.VISIBLE);
				holder.userbrickHeadline.setVisibility(View.GONE);
			} else {
				holder.localHeadline.setVisibility(View.GONE);
				holder.globalHeadline.setVisibility(View.GONE);
				holder.userbrickHeadline.setVisibility(View.GONE);
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
					checkedVariables.add(position);
				} else {
					checkedVariables.remove(position);
				}
				if (onCheckedChangeListener != null) {
					onCheckedChangeListener.onCheckedChange();
				}
				notifyDataSetChanged();
			}
		});

		if (checkedVariables.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}

		return view;
	}

	@Override
	public View getDropDownView(final int position, View convertView, ViewGroup parent) {
		UserVariable variable = getItem(position);
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
		holder.text1.setText(variable.getName());
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
		return checkedVariables.size();
	}

	@Override
	public SortedSet<Integer> getCheckedItems() {
		return checkedVariables;
	}

	public List<UserVariable> getCheckedUserVariables() {
		List<UserVariable> variables = new ArrayList<UserVariable>();
		for (int pos : getCheckedItems()) {
			variables.add(getItem(pos));
		}
		return variables;
	}

	public void addCheckedItem(int position) {
		checkedVariables.add(position);
	}

	@Override
	public void clearCheckedItems() {
		checkedVariables.clear();
	}

	public interface OnCheckedChangeListener {
		void onCheckedChange();
	}

	public interface OnListItemClickListener {
		void onListItemClick(int position);
	}

}
