/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class UserVariableAdapter extends BaseAdapter implements ScriptActivityAdapterInterface {
	private Context context;
	private List<UserVariable> spriteVariables;
	private List<UserVariable> projectVariables;
	private int selectMode;
	private SortedSet<Integer> checkedVariables = new TreeSet<Integer>();
	private OnCheckedChangeListener onCheckedChangeListener = null;
	private OnListItemClickListener onListItemClickListener = null;
	private int itemLayout;
	int textViewId;

	private static class ViewHolder {
		private TextView text1;
		private CheckBox checkbox;
	}

	private UserVariableAdapter() {}

	public UserVariableAdapter(Context context, List<UserVariable> spriteVariables, List<UserVariable> projectVariables) {
		this.spriteVariables = spriteVariables;
		this.projectVariables = projectVariables;
		this.context = context;
		this.selectMode = Constants.SELECT_NONE;
		this.itemLayout = R.layout.fragment_formula_editor_variablelist_item;
		this.textViewId = R.id.text1;
	}

	public void setItemLayout(int itemLayout, int textViewId) {
		this.itemLayout = itemLayout;
		this.textViewId = textViewId;
	}

	@Override
	public int getCount() {
		return spriteVariables.size() + projectVariables.size();
	}

	@Override
	public UserVariable getItem(int position) {
		if(position < spriteVariables.size())
			return spriteVariables.get(position);
		else
			return projectVariables.get(position - spriteVariables.size() );
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		UserVariable variable = getItem(position);
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			view = View.inflate(context, itemLayout, null);
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(textViewId);
			holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.text1.setText(variable.getName());


		if(onListItemClickListener != null) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onListItemClickListener.onListItemClick(position);
				}
			});
		}

		if(holder.checkbox == null)
			return view;

		view.setClickable(true);
		view.setFocusable(true);

		if (selectMode != Constants.SELECT_NONE) {
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
					if (selectMode == Constants.SINGLE_SELECT) {
						clearCheckedItems();
					}
					checkedVariables.add(position);
				} else {
					checkedVariables.remove(position);
				}
				if(onCheckedChangeListener != null)
					onCheckedChangeListener.onCheckedChange();
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
			view = View.inflate(context, android.R.layout.simple_spinner_dropdown_item, null);
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
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
		List<UserVariable> vars = new ArrayList<UserVariable>();
		for(int pos : getCheckedItems()) {
			vars.add(getItem(pos));
		}
		return vars;
	}

	@Override
	public void clearCheckedItems() {
		checkedVariables.clear();
	}


	public interface OnCheckedChangeListener {
		public void onCheckedChange();
	}

	public interface OnListItemClickListener {
		public void onListItemClick(int position);
	}

}
