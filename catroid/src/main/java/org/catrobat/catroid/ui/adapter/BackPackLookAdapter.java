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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.zed.bdsclient.controller.BDSClientController;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.BackPackLookFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BackPackLookAdapter extends LookBaseAdapter implements ActionModeActivityAdapterInterface {

	private BackPackLookFragment backpackLookFragment;

	public BackPackLookAdapter(final Context context, int resource, int textViewResourceId, List<LookData> items,
			boolean showDetails, BackPackLookFragment backPackLookFragment) {
		super(context, resource, textViewResourceId, items, showDetails, true);
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
		List<LookData> looksToUnpack = new ArrayList<>();
		for (Integer checkedPosition : checkedLookPositions) {
			looksToUnpack.add(getItem(checkedPosition));
		}
		for (LookData lookData : looksToUnpack) {
			LookController.getInstance().unpack(lookData, backpackLookFragment.isDeleteUnpackedItems(), false);
		}

		boolean returnToScriptActivity = checkedLookPositions.size() > 0;
		backpackLookFragment.clearCheckedLooksAndEnableButtons();

		if (returnToScriptActivity) {
			((BackPackActivity) backpackLookFragment.getActivity()).returnToScriptActivity(ScriptActivity.FRAGMENT_LOOKS);
		}
	}
}
