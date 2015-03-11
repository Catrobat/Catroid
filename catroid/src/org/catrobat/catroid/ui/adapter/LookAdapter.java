/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.ActionMode;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.LookFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class LookAdapter extends LookBaseAdapter implements ScriptActivityAdapterInterface {

	private LookFragment lookFragment;

	public LookAdapter(final Context context, int resource, int textViewResourceId, ArrayList<LookData> items,
			boolean showDetails) {
		super(context, resource, textViewResourceId, items, showDetails);
	}

	public LookAdapter(final Context context, AttributeSet attributeSet, int currentPlayingposition) {
		super(context, currentPlayingposition);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (lookFragment == null) {
			return convertView;
		}
		return lookFragment.getView(position, convertView);
	}

	public void onDestroyActionModeBackPack(ActionMode mode) {
		Iterator<Integer> iterator = checkedLooks.iterator();
		while (iterator.hasNext()) {
			int position = iterator.next();
			BackPackListManager.getInstance().addLookToActionBarLookDataArrayList(lookDataItems.get(position));
		}

		if (!checkedLooks.isEmpty()) {
			Intent intent = new Intent(lookFragment.getActivity(), BackPackActivity.class);
			intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, 1);
			intent.putExtra(BackPackActivity.BACKPACK_ITEM, true);
			lookFragment.getActivity().startActivity(intent);
		}

		lookFragment.clearCheckedLooksAndEnableButtons();
	}

	public void setLookFragment(LookFragment lookFragment) {
		this.lookFragment = lookFragment;
	}
}
