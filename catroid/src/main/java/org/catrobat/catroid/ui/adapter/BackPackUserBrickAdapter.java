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
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.BackPackGroupViewHolder;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackUserBrickController;
import org.catrobat.catroid.ui.fragment.BackPackUserBrickFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class BackPackUserBrickAdapter extends ArrayAdapter<String> implements ActionModeActivityAdapterInterface {

	protected SortedSet<Integer> checkedUserBrickGroups = new TreeSet<>();
	private BackPackUserBrickFragment backPackUserBrickFragment;
	private boolean showDetails;
	private int selectMode;

	public BackPackUserBrickAdapter(final Context context, int resource, int textViewResourceId, ArrayList<String> items,
			BackPackUserBrickFragment backPackUserBrickFragment) {
		super(context, resource, textViewResourceId, items);
		this.backPackUserBrickFragment = backPackUserBrickFragment;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (backPackUserBrickFragment == null) {
			return convertView;
		}
		return backPackUserBrickFragment.getView(position, convertView);
	}

	public void onDestroyActionModeUnpacking() {
		Boolean deleteUnpackedItems = false;

		List<String> userBrickGroupsToUnpack = new ArrayList<>();
		for (Integer userBrickPosition : checkedUserBrickGroups) {
			userBrickGroupsToUnpack.add(getItem(userBrickPosition));
		}
		for (String userBrickGroup : userBrickGroupsToUnpack) {
			BackPackUserBrickController.getInstance().unpack(userBrickGroup, deleteUnpackedItems, backPackUserBrickFragment.getActivity());
		}

		backPackUserBrickFragment.clearCheckedItems();
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
		return checkedUserBrickGroups.size();
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return checkedUserBrickGroups;
	}

	@Override
	public void clearCheckedItems() {
		checkedUserBrickGroups.clear();
	}

	public void addCheckedItem(int position) {
		checkedUserBrickGroups.add(position);
	}

	public void updateUserBrickGroupLogic(int position, final BackPackGroupViewHolder holder) {
		final String userBrickGroupName = getItem(position);

		if (userBrickGroupName == null) {
			return;
		}
		holder.backPackGroupNameTextView.setTag(position);
		holder.backPackGroupElement.setTag(position);
		holder.backPackGroupNameTextView.setText(userBrickGroupName);

		boolean checkboxIsVisible = handleCheckboxes(position, holder);
		handleDetails(userBrickGroupName, holder);

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
					checkedUserBrickGroups.add(position);
				} else {
					checkedUserBrickGroups.remove(position);
				}
				notifyDataSetChanged();

				backPackUserBrickFragment.onUserBrickGroupChecked();
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

		if (checkedUserBrickGroups.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		return checkboxIsVisible;
	}

	private void handleDetails(String userBrickGroup, BackPackGroupViewHolder holder) {
		if (getShowDetails()) {
			List<UserBrick> userBricks = BackPackListManager.getInstance().getBackPackedUserBricks().get(userBrickGroup);
			if (userBricks == null) {
				return;
			}
			Integer numberOfBricks = 0;
			for (UserBrick userBrick : userBricks) {
				numberOfBricks += userBrick.getDefinitionBrick().getUserScript().getBrickList().size() + 1;
			}
			holder.backPackGroupNumberOfBricksValue.setText(String.format(Locale.getDefault(), numberOfBricks.toString()));
			holder.backPackGroupDetailsLinearLayout.setVisibility(TextView.VISIBLE);
		} else {
			holder.backPackGroupDetailsLinearLayout.setVisibility(TextView.GONE);
		}
	}

	public void deleteCheckedUserBrickGroups() {
		int numberDeleted = 0;
		for (int position : checkedUserBrickGroups) {
			deleteUserBrickGroup(position - numberDeleted);
			++numberDeleted;
		}
		notifyDataSetChanged();
	}

	private void deleteUserBrickGroup(int position) {
		BackPackListManager.getInstance().removeItemFromUserBrickBackPack(getItem(position));
		remove(getItem(position));
	}
}
