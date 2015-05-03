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
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.BackPackLookFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class BackPackLookAdapter extends LookBaseAdapter implements ScriptActivityAdapterInterface {

	private BackPackLookFragment backpackLookFragment;
	private FragmentActivity currentFragmentActivity;

	public BackPackLookAdapter(final Context context, int resource, int textViewResourceId, ArrayList<LookData> items,
			boolean showDetails, BackPackLookFragment backPackLookFragment) {
		super(context, resource, textViewResourceId, items, showDetails);
		this.backpackLookFragment = backPackLookFragment;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (backpackLookFragment == null) {
			return convertView;
		}
		return this.backpackLookFragment.getView(position, convertView);
	}

	public void onDestroyActionModeUnpacking() {
		Iterator<Integer> iterator = checkedLooks.iterator();
		while (iterator.hasNext()) {
			int position = iterator.next();
			LookController.getInstance().copyLook(position, BackPackListManager.getCurrentLookDataArrayList(), currentFragmentActivity, BackPackListManager.getInstance().getCurrentLookFragment());
		}
		backpackLookFragment.clearCheckedLooksAndEnableButtons();
	}

	public void setCurrentActivity(FragmentActivity activity) {
		this.currentFragmentActivity = activity;
	}
}
