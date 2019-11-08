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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceListAccessor;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<CheckableVH> implements RVAdapter.SelectionListener {

	public boolean allowMultiSelection = true;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({VAR_GLOBAL, VAR_LOCAL, LIST_GLOBAL, LIST_LOCAL})
	@interface DataType {}
	private static final int VAR_GLOBAL = 0;
	private static final int VAR_LOCAL = 1;
	private static final int LIST_GLOBAL = 2;
	private static final int LIST_LOCAL = 3;

	private VariableRVAdapter globalVarAdapter;
	private VariableRVAdapter localVarAdapter;
	private ListRVAdapter globalListAdapter;
	private ListRVAdapter localListAdapter;

	private RVAdapter.SelectionListener selectionListener;

	public DataListAdapter(List<UserVariable> globalVars,
			List<UserVariable> localVars,
			List<UserList> globalLists,
			List<UserList> localLists) {

		globalVarAdapter = new VariableRVAdapter(globalVars) {
			@Override
			public void onBindViewHolder(CheckableVH holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline)).setText(R.string.global_vars_headline);
				}
			}
		};
		globalVarAdapter.setSelectionListener(this);

		localVarAdapter = new VariableRVAdapter(localVars){
			@Override
			public void onBindViewHolder(CheckableVH holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline)).setText(R.string.local_vars_headline);
				}
			}
			@Override
			protected void onCheckBoxClick(int position) {
				super.onCheckBoxClick(getRelativeItemPosition(position, VAR_LOCAL));
			}
		};
		localVarAdapter.setSelectionListener(this);

		globalListAdapter = new ListRVAdapter(globalLists) {
			@Override
			public void onBindViewHolder(CheckableVH holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline)).setText(R.string.global_lists_headline);
				}
			}
			@Override
			protected void onCheckBoxClick(int position) {
				super.onCheckBoxClick(getRelativeItemPosition(position, LIST_GLOBAL));
			}
		};
		globalListAdapter.setSelectionListener(this);

		localListAdapter = new ListRVAdapter(localLists) {
			@Override
			public void onBindViewHolder(CheckableVH holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline)).setText(R.string.local_lists_headline);
				}
			}
			@Override
			protected void onCheckBoxClick(int position) {
				super.onCheckBoxClick(getRelativeItemPosition(position, LIST_LOCAL));
			}
		};
		localListAdapter.setSelectionListener(this);
	}

	private int getRelativeItemPosition(int position, @DataType int dataType) {
		switch (dataType) {
			case VAR_GLOBAL:
				return position;
			case VAR_LOCAL:
				return position - globalVarAdapter.getItemCount();
			case LIST_GLOBAL:
				return position - (globalVarAdapter.getItemCount() + localVarAdapter.getItemCount());
			case LIST_LOCAL:
				return position - (globalVarAdapter.getItemCount()
						+ localVarAdapter.getItemCount()
						+ globalListAdapter.getItemCount());
			default:
				throw new IllegalArgumentException("DataType is not specified: this would throw an index out of "
						+ "bounds exception.");
		}
	}

	private @DataType int getDataType(int position) {
		if (position < globalVarAdapter.getItemCount()) {
			return VAR_GLOBAL;
		}
		if (position < (globalVarAdapter.getItemCount() + localVarAdapter.getItemCount())) {
			return VAR_LOCAL;
		}
		if (position < (globalVarAdapter.getItemCount()
				+ localVarAdapter.getItemCount()
				+ globalListAdapter.getItemCount())) {
			return LIST_GLOBAL;
		}
		if (position < (globalVarAdapter.getItemCount()
				+ localVarAdapter.getItemCount()
				+ globalListAdapter.getItemCount()
				+ localListAdapter.getItemCount())) {
			return LIST_LOCAL;
		}
		throw new IndexOutOfBoundsException("None of the sub adapters provides this position. size:" + getItemCount()
				+ "index: " + position);
	}

	public void showCheckBoxes(boolean visible) {
		globalVarAdapter.showCheckBoxes = visible;
		localVarAdapter.showCheckBoxes = visible;
		globalListAdapter.showCheckBoxes = visible;
		localListAdapter.showCheckBoxes = visible;
	}

	public void setSelectionListener(RVAdapter.SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setOnItemClickListener(RVAdapter.OnItemClickListener onItemClickListener) {
		globalVarAdapter.setOnItemClickListener(onItemClickListener);
		localVarAdapter.setOnItemClickListener(onItemClickListener);
		globalListAdapter.setOnItemClickListener(onItemClickListener);
		localListAdapter.setOnItemClickListener(onItemClickListener);
	}

	@NonNull
	@Override
	public CheckableVH onCreateViewHolder(@NonNull ViewGroup parent, @LayoutRes int viewType) {
		switch (viewType) {
			case R.layout.vh_variable_with_headline:
			case R.layout.vh_variable:
				return globalVarAdapter.onCreateViewHolder(parent, viewType);
			case R.layout.vh_list_with_headline:
			case R.layout.vh_list:
				return globalListAdapter.onCreateViewHolder(parent, viewType);
			default:
				throw new IllegalArgumentException("ViewType was not defined correctly.");
		}
	}

	@Override
	public @LayoutRes int getItemViewType(int position) {
		@DataType
		int dataType = getDataType(position);
		position = getRelativeItemPosition(position, dataType);
		switch (dataType) {
			case VAR_GLOBAL:
			case VAR_LOCAL:
				return position == 0 ? R.layout.vh_variable_with_headline : R.layout.vh_variable;
			case LIST_GLOBAL:
			case LIST_LOCAL:
				return position == 0 ? R.layout.vh_list_with_headline : R.layout.vh_list;
		}
		throw new ArrayIndexOutOfBoundsException("position is not within any of the adapters");
	}

	@Override
	public void onBindViewHolder(@NonNull CheckableVH holder, int position) {
		@DataType
		int dataType = getDataType(position);
		position = getRelativeItemPosition(position, dataType);

		switch (holder.getItemViewType()) {
			case R.layout.vh_variable_with_headline:
			case R.layout.vh_variable:
				if (dataType == VAR_GLOBAL) {
					globalVarAdapter.onBindViewHolder(holder, position);
				} else {
					localVarAdapter.onBindViewHolder(holder, position);
				}
				break;
			case R.layout.vh_list_with_headline:
			case R.layout.vh_list:
				if (dataType == LIST_GLOBAL) {
					globalListAdapter.onBindViewHolder(holder, position);
				} else {
					localListAdapter.onBindViewHolder(holder, position);
				}
				break;
		}
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		selectionListener.onSelectionChanged(globalVarAdapter.getSelectedItems().size()
				+ localVarAdapter.getSelectedItems().size()
				+ globalListAdapter.getSelectedItems().size()
				+ localListAdapter.getSelectedItems().size());
	}

	public void updateDataSet() {
		globalVarAdapter.notifyDataSetChanged();
		localVarAdapter.notifyDataSetChanged();
		globalListAdapter.notifyDataSetChanged();
		localListAdapter.notifyDataSetChanged();
		notifyDataSetChanged();
	}

	public void clearSelection() {
		globalVarAdapter.clearSelection();
		localVarAdapter.clearSelection();
		globalListAdapter.clearSelection();
		localListAdapter.clearSelection();
		notifyDataSetChanged();
	}

	public void remove(UserData item) {
		if (item instanceof UserVariable) {
			if (!globalVarAdapter.remove((UserVariable) item)) {
				localVarAdapter.remove((UserVariable) item);
			}
			File projectDir = ProjectManager.getInstance().getCurrentProject().getDirectory();
			new DeviceVariableAccessor(projectDir).removeDeviceValue((UserVariable) item);
		} else {
			if (!globalListAdapter.remove((UserList) item)) {
				localListAdapter.remove((UserList) item);
			}
			File projectDir = ProjectManager.getInstance().getCurrentProject().getDirectory();
			new DeviceListAccessor(projectDir).removeDeviceValue(item);
		}
		notifyDataSetChanged();
	}

	public List<UserData> getItems() {
		List<UserData> items = new ArrayList<>();
		items.addAll(globalVarAdapter.getItems());
		items.addAll(localVarAdapter.getItems());
		items.addAll(globalListAdapter.getItems());
		items.addAll(localListAdapter.getItems());
		return items;
	}

	public List<UserVariable> getVariables() {
		List<UserVariable> items = new ArrayList<>();
		items.addAll(globalVarAdapter.getItems());
		items.addAll(localVarAdapter.getItems());
		return items;
	}

	public List<UserList> getLists() {
		List<UserList> items = new ArrayList<>();
		items.addAll(globalListAdapter.getItems());
		items.addAll(localListAdapter.getItems());
		return items;
	}

	public List<UserData> getSelectedItems() {
		List<UserData> selectedItems = new ArrayList<>();
		selectedItems.addAll(globalVarAdapter.getSelectedItems());
		selectedItems.addAll(localVarAdapter.getSelectedItems());
		selectedItems.addAll(globalListAdapter.getSelectedItems());
		selectedItems.addAll(localListAdapter.getSelectedItems());
		return selectedItems;
	}

	public void setSelection(UserData item, boolean selection) {
		if (item instanceof UserVariable) {
			if (!globalVarAdapter.setSelection((UserVariable) item, selection)) {
				localVarAdapter.setSelection((UserVariable) item, selection);
			}
		} else {
			if (!globalListAdapter.setSelection((UserList) item, selection)) {
				localListAdapter.setSelection((UserList) item, selection);
			}
		}
	}

	@Override
	public int getItemCount() {
		return globalVarAdapter.getItemCount()
				+ localVarAdapter.getItemCount()
				+ globalListAdapter.getItemCount()
				+ localListAdapter.getItemCount();
	}
}
