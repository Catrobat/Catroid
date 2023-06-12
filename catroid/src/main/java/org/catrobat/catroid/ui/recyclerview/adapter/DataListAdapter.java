/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.os.Build;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class DataListAdapter extends RecyclerView.Adapter<CheckableViewHolder> implements RVAdapter.SelectionListener {

	public boolean allowMultiSelection = true;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({USER_DEFINED_BRICK_INPUTS, VAR_MULTIPLAYER, VAR_GLOBAL, VAR_LOCAL})
	@interface DataType {}

	private static final int USER_DEFINED_BRICK_INPUTS = 0;
	private static final int VAR_MULTIPLAYER = 1;
	private static final int VAR_GLOBAL = 2;
	private static final int VAR_LOCAL = 3;

	private final UserDefinedBrickInputRVAdapter userDefinedBrickInputAdapter;
	private final UserDataRVAdapter<UserVariable> multiplayerVarAdapter;
	private final UserDataRVAdapter<UserVariable> globalVarAdapter;
	private final UserDataRVAdapter<UserVariable> localVarAdapter;

	private RVAdapter.SelectionListener selectionListener;

	public DataListAdapter(List<UserDefinedBrickInput> userDefinedBrickInputs,
			List<UserVariable> multiplayerVars,
			List<UserVariable> globalVars,
			List<UserVariable> localVars) {

		userDefinedBrickInputAdapter = new UserDefinedBrickInputRVAdapter(userDefinedBrickInputs);
		userDefinedBrickInputAdapter.setSelectionListener(this);

		multiplayerVarAdapter = new UserDataRVAdapter<UserVariable>(multiplayerVars) {
			@Override
			public void onBindViewHolder(CheckableViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline))
							.setText(R.string.multiplayer_vars_headline);
				}
			}

			@Override
			protected void onCheckBoxClick(int position) {
				super.onCheckBoxClick(getRelativeItemPosition(position, VAR_MULTIPLAYER));
			}
		};
		multiplayerVarAdapter.setSelectionListener(this);

		globalVarAdapter = new UserDataRVAdapter<UserVariable>(globalVars) {
			@Override
			public void onBindViewHolder(CheckableViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline))
							.setText(R.string.global_vars_headline);
				}
			}

			@Override
			protected void onCheckBoxClick(int position) {
				super.onCheckBoxClick(getRelativeItemPosition(position, VAR_GLOBAL));
			}
		};
		globalVarAdapter.setSelectionListener(this);

		localVarAdapter = new UserDataRVAdapter<UserVariable>(localVars) {
			@Override
			public void onBindViewHolder(CheckableViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);
				if (position == 0) {
					((TextView) holder.itemView.findViewById(R.id.headline))
							.setText(R.string.local_vars_headline);
				}
			}

			@Override
			protected void onCheckBoxClick(int position) {
				super.onCheckBoxClick(getRelativeItemPosition(position, VAR_LOCAL));
			}
		};
		localVarAdapter.setSelectionListener(this);
	}

	private int getRelativeItemPosition(int position, @DataType int dataType) {
		switch (dataType) {
			case USER_DEFINED_BRICK_INPUTS:
				return position;
			case VAR_MULTIPLAYER:
				return position - userDefinedBrickInputAdapter.getItemCount();
			case VAR_GLOBAL:
				return position - (userDefinedBrickInputAdapter.getItemCount()
						+ multiplayerVarAdapter.getItemCount());
			case VAR_LOCAL:
				return position - (userDefinedBrickInputAdapter.getItemCount()
						+ multiplayerVarAdapter.getItemCount()
						+ globalVarAdapter.getItemCount());
			default:
				throw new IllegalArgumentException("DataType is not specified: this would throw an index out of "
						+ "bounds exception.");
		}
	}

	private @DataType int getDataType(int position) {
		if (position < userDefinedBrickInputAdapter.getItemCount()) {
			return USER_DEFINED_BRICK_INPUTS;
		}
		if (position < (userDefinedBrickInputAdapter.getItemCount()
				+ multiplayerVarAdapter.getItemCount())) {
			return VAR_MULTIPLAYER;
		}
		if (position < (userDefinedBrickInputAdapter.getItemCount()
				+ multiplayerVarAdapter.getItemCount()
				+ globalVarAdapter.getItemCount())) {
			return VAR_GLOBAL;
		}
		if (position < (userDefinedBrickInputAdapter.getItemCount()
				+ multiplayerVarAdapter.getItemCount()
				+ globalVarAdapter.getItemCount()
				+ localVarAdapter.getItemCount())) {
			return VAR_LOCAL;
		}
		throw new IndexOutOfBoundsException("None of the sub adapters provides this position. size:" + getItemCount()
				+ "index: " + position);
	}

	public void showCheckBoxes(boolean visible) {
		multiplayerVarAdapter.showCheckBoxes = visible;
		multiplayerVarAdapter.showSettings = !visible;
		globalVarAdapter.showCheckBoxes = visible;
		globalVarAdapter.showSettings = !visible;
		localVarAdapter.showCheckBoxes = visible;
		localVarAdapter.showSettings = !visible;
	}

	public void setSelectionListener(RVAdapter.SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setOnItemClickListener(RVAdapter.OnItemClickListener onItemClickListener) {
		userDefinedBrickInputAdapter.setOnItemClickListener(onItemClickListener);
		multiplayerVarAdapter.setOnItemClickListener(onItemClickListener);
		globalVarAdapter.setOnItemClickListener(onItemClickListener);
		localVarAdapter.setOnItemClickListener(onItemClickListener);
	}

	@NonNull
	@Override
	public CheckableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, @LayoutRes int viewType) {
		switch (viewType) {
			case R.layout.view_holder_variable_with_headline:
			case R.layout.view_holder_variable:
				return globalVarAdapter.onCreateViewHolder(parent, viewType);
			default:
				throw new IllegalArgumentException("ViewType was not defined correctly.");
		}
	}

	@Override
	public @LayoutRes
	int getItemViewType(int position) {
		@DataType
		int dataType = getDataType(position);
		position = getRelativeItemPosition(position, dataType);
		switch (dataType) {
			case USER_DEFINED_BRICK_INPUTS:
			case VAR_MULTIPLAYER:
			case VAR_GLOBAL:
			case VAR_LOCAL:
				return position == 0 ? R.layout.view_holder_variable_with_headline : R.layout.view_holder_variable;
		}
		throw new ArrayIndexOutOfBoundsException("position is not within any of the adapters");
	}

	@Override
	public void onBindViewHolder(@NonNull CheckableViewHolder holder, int position) {
		@DataType
		int dataType = getDataType(position);
		position = getRelativeItemPosition(position, dataType);

		if (dataType == VAR_GLOBAL) {
			globalVarAdapter.onBindViewHolder(holder, position);
		} else if (dataType == VAR_LOCAL) {
			localVarAdapter.onBindViewHolder(holder, position);
		} else if (dataType == VAR_MULTIPLAYER) {
			multiplayerVarAdapter.onBindViewHolder(holder, position);
		} else if (dataType == USER_DEFINED_BRICK_INPUTS) {
			userDefinedBrickInputAdapter.onBindViewHolder(holder, position);
		}
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		selectionListener.onSelectionChanged(multiplayerVarAdapter.getSelectedItems().size()
				+ globalVarAdapter.getSelectedItems().size()
				+ localVarAdapter.getSelectedItems().size());
	}

	public void updateDataSet() {
		userDefinedBrickInputAdapter.notifyDataSetChanged();
		multiplayerVarAdapter.notifyDataSetChanged();
		globalVarAdapter.notifyDataSetChanged();
		localVarAdapter.notifyDataSetChanged();
		notifyDataSetChanged();
	}

	public void clearSelection() {
		multiplayerVarAdapter.clearSelection();
		globalVarAdapter.clearSelection();
		localVarAdapter.clearSelection();
		notifyDataSetChanged();
	}

	public void selectAll() {
		multiplayerVarAdapter.selectAll();
		globalVarAdapter.selectAll();
		localVarAdapter.selectAll();
		notifyDataSetChanged();
	}

	public void remove(UserData item) {
		UserVariable userVariable = (UserVariable) item;

		if (!globalVarAdapter.remove(userVariable) && !localVarAdapter.remove(userVariable)) {
			multiplayerVarAdapter.remove(userVariable);
		}
		File projectDir = ProjectManager.getInstance().getCurrentProject().getDirectory();
		new DeviceVariableAccessor(projectDir).removeDeviceValue(userVariable);

		if (!ProjectManager.getInstance().getCurrentProject().removeUserVariable(userVariable.getName())) {
			ProjectManager.getInstance().getCurrentSprite().getUserVariableList().remove(userVariable);
		}
		notifyDataSetChanged();
	}

	public List<UserData> getItems() {
		List<UserData> items = new ArrayList<>();
		items.addAll(userDefinedBrickInputAdapter.getItems());
		items.addAll(multiplayerVarAdapter.getItems());
		items.addAll(globalVarAdapter.getItems());
		items.addAll(localVarAdapter.getItems());
		return items;
	}

	public List<UserVariable> getVariables() {
		List<UserVariable> items = new ArrayList<>();
		items.addAll(multiplayerVarAdapter.getItems());
		items.addAll(globalVarAdapter.getItems());
		items.addAll(localVarAdapter.getItems());
		return items;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public List<UserVariable> getLists() {
		List<UserVariable> items = new ArrayList<>();
		items.addAll(globalVarAdapter.getItems().stream()
				.filter(UserVariable::isList)
				.collect(Collectors.toList()));
		items.addAll(localVarAdapter.getItems().stream()
				.filter(UserVariable::isList)
				.collect(Collectors.toList()));
		return items;
	}

	public List<UserData> getSelectedItems() {
		List<UserData> selectedItems = new ArrayList<>();
		selectedItems.addAll(multiplayerVarAdapter.getSelectedItems());
		selectedItems.addAll(globalVarAdapter.getSelectedItems());
		selectedItems.addAll(localVarAdapter.getSelectedItems());
		return selectedItems;
	}

	public void setSelection(UserData item, boolean selection) {
		UserVariable userVariable = (UserVariable) item;

		if (!globalVarAdapter.setSelection(userVariable, selection)
				&& !localVarAdapter.setSelection(userVariable, selection)) {
			multiplayerVarAdapter.setSelection(userVariable, selection);
		}
	}

	public void toggleSelection(UserData item) {
		UserVariable userVariable = (UserVariable) item;
		if (!globalVarAdapter.toggleSelection(userVariable)
				&& !localVarAdapter.toggleSelection(userVariable)) {
			multiplayerVarAdapter.toggleSelection(userVariable);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return userDefinedBrickInputAdapter.getItemCount()
				+ multiplayerVarAdapter.getItemCount()
				+ globalVarAdapter.getItemCount()
				+ localVarAdapter.getItemCount();
	}

	public int getSelectedItemCount() {
		return multiplayerVarAdapter.getSelectedItemCount()
				+ globalVarAdapter.getSelectedItemCount()
				+ localVarAdapter.getSelectedItemCount();
	}
}
