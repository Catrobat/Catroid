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

package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;

public class NewListDialog extends NewDataDialog {

	private NewListInterface newListInterface;

	public NewListDialog(NewListInterface newListInterface) {
		this.newListInterface = newListInterface;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		Dialog dialog = super.onCreateDialog(bundle);
		makeList.setVisibility(View.GONE);
		dialog.setTitle(R.string.formula_editor_list_dialog_title);
		return dialog;
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String name = inputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		boolean isGlobal = (radioGroup.getCheckedRadioButtonId() == R.id.global);
		if (!isVariableNameValid(name, isGlobal)) {
			inputLayout.setError(getString(R.string.name_already_exists));
			return false;
		}

		UserList userList;

		if (isGlobal) {
			userList = dataContainer.addProjectUserList(name);
		} else {
			userList = dataContainer.addSpriteUserList(name);
		}

		newListInterface.onNewList(userList);
		return true;
	}

	public interface NewListInterface {

		void onNewList(UserList userList);
	}
}
