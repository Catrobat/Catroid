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

package org.catrobat.catroid.ui.recyclerview.dialog;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.controller.BackpackListManager;

import java.util.List;

public class NewScriptGroupDialog extends TextInputDialogFragment {

	public static final String TAG = NewScriptGroupDialog.class.getSimpleName();

	private BackpackScriptInterface backpackInterface;

	public NewScriptGroupDialog() {
	}

	public NewScriptGroupDialog(BackpackScriptInterface backpackInterface) {
		super(R.string.new_group, R.string.script_group_label, null, false);
		this.backpackInterface = backpackInterface;
	}

	@Override
	protected boolean onPositiveButtonClick() {
		String name = inputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (getScope().contains(name)) {
			inputLayout.setError(getString(R.string.name_already_exists));
			return false;
		} else {
			backpackInterface.packItems(name);
			return true;
		}
	}

	@Override
	protected void onNegativeButtonClick() {
		backpackInterface.cancelPacking();
	}

	private List<String> getScope() {
		return BackpackListManager.getInstance().getBackpackedScriptGroups();
	}

	public interface BackpackScriptInterface {

		void packItems(String name);
		void cancelPacking();
	}
}
