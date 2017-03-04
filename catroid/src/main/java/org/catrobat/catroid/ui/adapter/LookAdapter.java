/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.LookFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LookAdapter extends LookBaseAdapter implements ActionModeActivityAdapterInterface,
		LookController.OnBackpackLookCompleteListener {

	private static final int INVALID_ID = -1;

	private LookFragment lookFragment;

	private HashMap<LookData, Integer> idMap = new HashMap<>();

	public LookAdapter(final Context context, int resource, int textViewResourceId, List<LookData> items,
			boolean showDetails) {
		super(context, resource, textViewResourceId, items, showDetails, false);
		for (int i = 0; i < items.size(); ++i) {
			idMap.put(items.get(i), i);
		}
	}

	@Override
	public long getItemId(int position) {
		if (position < 0 || position >= idMap.size()) {
			return INVALID_ID;
		}

		LookData item = getItem(position);
		if (!idMap.containsKey(item)) {
			idMap.clear();
			for (int i = 0; i < getCount(); i++) {
				idMap.put(getItem(i), i);
			}
		}

		return idMap.get(item);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (getCount() != idMap.size()) {
			idMap.clear();
			for (int i = 0; i < getCount(); i++) {
				idMap.put(getItem(i), i);
			}
		}
	}

	public void hardSetIdMapForTesting() {
		if (getCount() != idMap.size()) {
			idMap.clear();
			for (int i = 0; i < getCount(); i++) {
				idMap.put(getItem(i), i);
			}
		}
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (lookFragment == null) {
			return convertView;
		}
		return lookFragment.getView(position, convertView);
	}

	public void onDestroyActionModeBackPack() {
		List<LookData> lookDataListToBackpack = new ArrayList<>();
		for (Integer position : checkedLookPositions) {
			lookDataListToBackpack.add(lookDataItems.get(position));
		}

		boolean looksAlreadyInBackpack = LookController.getInstance().checkLookReplaceInBackpack(lookDataListToBackpack);

		if (!lookDataListToBackpack.isEmpty()) {
			if (!looksAlreadyInBackpack) {
				for (LookData lookDataToBackpack : lookDataListToBackpack) {
					LookController.getInstance().backPackVisibleLook(lookDataToBackpack);
					onBackpackLookComplete(true);
				}
			} else {
				LookController.getInstance().setOnBackpackLookCompleteListener(this);
				LookController.getInstance().showBackPackReplaceDialog(lookDataListToBackpack, lookFragment.getActivity());
			}
		} else {
			lookFragment.clearCheckedLooksAndEnableButtons();
		}
	}

	public void setLookFragment(LookFragment lookFragment) {
		this.lookFragment = lookFragment;
	}

	@Override
	public void onBackpackLookComplete(boolean startBackpackActivity) {
		if (!checkedLookPositions.isEmpty() && startBackpackActivity) {
			Intent intent = new Intent(lookFragment.getActivity(), BackPackActivity.class);
			intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_LOOKS);
			lookFragment.getActivity().startActivity(intent);
		}
		lookFragment.clearCheckedLooksAndEnableButtons();
	}
}
