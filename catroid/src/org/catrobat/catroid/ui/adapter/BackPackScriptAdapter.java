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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.ui.BackPackGroupViewHolder;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackScriptController;
import org.catrobat.catroid.ui.fragment.BackPackScriptFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class BackPackScriptAdapter extends ArrayAdapter<String> implements ActionModeActivityAdapterInterface {

	protected SortedSet<Integer> checkedScriptGroups = new TreeSet<>();
	private BackPackScriptFragment backPackScriptFragment;
	private boolean showDetails;
	private int selectMode;

	public BackPackScriptAdapter(final Context context, int resource, int textViewResourceId, ArrayList<String> items,
			BackPackScriptFragment backPackScriptFragment) {
		super(context, resource, textViewResourceId, items);
		this.backPackScriptFragment = backPackScriptFragment;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (backPackScriptFragment == null) {
			return convertView;
		}
		return backPackScriptFragment.getView(position, convertView);
	}

	public void onDestroyActionModeUnpacking() {
		Boolean deleteUnpackedItems = backPackScriptFragment.isDeleteUnpackedItems();

		List<String> scriptGroupsToUnpack = new ArrayList<>();
		for (Integer scriptPosition : checkedScriptGroups) {
			scriptGroupsToUnpack.add(getItem(scriptPosition));
		}
		for (String scriptGroup : scriptGroupsToUnpack) {
			BackPackScriptController.getInstance().unpack(scriptGroup, deleteUnpackedItems, true,
					backPackScriptFragment.getActivity(), false);
		}

		backPackScriptFragment.clearCheckedItemsAndEnableButtons();
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public void setSelectMode(int mode) {
		this.selectMode = mode;
	}

	@Override
	public boolean getShowDetails() {
		return showDetails;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return checkedScriptGroups.size();
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return checkedScriptGroups;
	}

	@Override
	public void clearCheckedItems() {
		checkedScriptGroups.clear();
	}

	public void addCheckedItem(int position) {
		checkedScriptGroups.add(position);
	}

	public void updateScriptGroupLogic(int position, final BackPackGroupViewHolder holder) {
		final String scriptGroupName = getItem(position);

		if (scriptGroupName == null) {
			return;
		}
		holder.backPackGroupNameTextView.setTag(position);
		holder.backPackGroupElement.setTag(position);
		holder.backPackGroupNameTextView.setText(scriptGroupName);

		boolean checkboxIsVisible = handleCheckboxes(position, holder);
		handleDetails(scriptGroupName, holder);

		// Disable ImageView on active ActionMode
		if (checkboxIsVisible) {
			holder.backPackGroupImageView.setEnabled(false);
		} else {
			holder.backPackGroupImageView.setEnabled(true);
		}
		if (holder.backPackGroupElement.isClickable()) {
			holder.backPackGroupElement.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (getSelectMode() != ListView.CHOICE_MODE_NONE) {
						holder.checkbox.setChecked(!holder.checkbox.isChecked());
					}
				}
			});
		} else {
			holder.backPackGroupElement.setOnClickListener(null);
		}
	}

	private boolean handleCheckboxes(final int position, BackPackGroupViewHolder holder) {
		holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedItems();
					}
					checkedScriptGroups.add(position);
				} else {
					checkedScriptGroups.remove(position);
				}
				notifyDataSetChanged();

				backPackScriptFragment.onScriptGroupChecked();
			}
		});

		boolean checkboxIsVisible = false;

		if (getSelectMode() != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.backPackGroupElement.setBackgroundResource(R.drawable.button_background_shadowed);
			checkboxIsVisible = true;
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			holder.backPackGroupElement.setBackgroundResource(R.drawable.button_background_selector);
			clearCheckedItems();
		}

		if (checkedScriptGroups.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		return checkboxIsVisible;
	}

	private void handleDetails(String scriptGroup, BackPackGroupViewHolder holder) {
		if (getShowDetails()) {
			List<Script> scripts = BackPackListManager.getInstance().getBackPackedScripts().get(scriptGroup);
			if (scripts == null) {
				return;
			}
			Integer numberOfBricks = 0;
			for (Script script : scripts) {
				numberOfBricks += script.getBrickList().size() + 1;
			}
			holder.backPackGroupNumberOfBricksValue.setText(String.format(Locale.getDefault(), numberOfBricks.toString()));
			holder.backPackGroupDetailsLinearLayout.setVisibility(TextView.VISIBLE);
		} else {
			holder.backPackGroupDetailsLinearLayout.setVisibility(TextView.GONE);
		}
	}

	public void deleteCheckedScriptGroups() {
		int numberDeleted = 0;
		for (int position : checkedScriptGroups) {
			deleteScriptGroup(position - numberDeleted);
			++numberDeleted;
		}
		notifyDataSetChanged();
	}

	private void deleteScriptGroup(int position) {
		BackPackListManager.getInstance().removeItemFromScriptBackPack(getItem(position));
		remove(getItem(position));
	}
}
