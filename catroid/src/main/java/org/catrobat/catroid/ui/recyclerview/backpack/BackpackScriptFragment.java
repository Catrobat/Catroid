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

package org.catrobat.catroid.ui.recyclerview.backpack;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.adapter.ScriptAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

public class BackpackScriptFragment extends BackpackRecyclerViewFragment<String> {

	public static final String TAG = BackpackScriptFragment.class.getSimpleName();

	private ScriptController scriptController = new ScriptController();

	@Override
	protected void initializeAdapter() {
		List<String> items = BackPackListManager.getInstance().getBackPackedScriptGroups();
		adapter = new ScriptAdapter(items);
		onAdapterReady();
	}

	@Override
	protected void unpackItems(List<String> selectedItems) {
		finishActionMode();
		try {
			for (String item : selectedItems) {
				List<Script> scripts = BackPackListManager.getInstance().getAllBackPackedScripts().get(item);
				for (Script script : scripts) {
					scriptController.unpack(script, ProjectManager.getInstance().getCurrentSprite());
				}
			}
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.unpacked_scripts,
					selectedItems.size(),
					selectedItems.size()));
			getActivity().finish();
		} catch (IOException | CloneNotSupportedException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	protected int getDeleteAlertTitle() {
		return R.plurals.delete_scripts;
	}

	@Override
	protected void deleteItems(List<String> selectedItems) {
		finishActionMode();
		for (String item : selectedItems) {
			BackPackListManager.getInstance().removeItemFromScriptBackPack(item);
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_scripts,
				selectedItems.size(),
				selectedItems.size()));

		BackPackListManager.getInstance().saveBackpack();
		if (adapter.getItems().isEmpty()) {
			getActivity().finish();
		}
	}

	@Override
	protected String getItemName(String item) {
		return item;
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_scripts_title,
				selectedItemCnt,
				selectedItemCnt));
	}
}
