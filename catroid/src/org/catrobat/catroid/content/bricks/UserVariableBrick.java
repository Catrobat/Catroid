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

package org.catrobat.catroid.content.bricks;

import android.widget.Spinner;

import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;

public abstract class UserVariableBrick extends FormulaBrick implements NewDataDialog.NewVariableDialogListener {

	protected UserVariable userVariable;
	public boolean inUserBrick = false;

	private void updateUserVariableIfDeleted(UserVariableAdapterWrapper userVariableAdapterWrapper) {
		if (userVariable != null && (userVariableAdapterWrapper.getPositionOfItem(userVariable) == 0)) {
			userVariable = null;
		}
	}

	protected void setSpinnerSelection(Spinner variableSpinner, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = (UserVariableAdapterWrapper) variableSpinner
				.getAdapter();

		updateUserVariableIfDeleted(userVariableAdapterWrapper);

		if (userVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(userVariable), true);
		} else if (newUserVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(newUserVariable), true);
			userVariable = newUserVariable;
		} else {
			variableSpinner.setSelection(userVariableAdapterWrapper.getCount() - 1, true);
			userVariable = userVariableAdapterWrapper.getItem(userVariableAdapterWrapper.getCount() - 1);
		}
	}

	@Override
	public void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = ((UserVariableAdapterWrapper) spinnerToUpdate
				.getAdapter());
		userVariableAdapterWrapper.notifyDataSetChanged();
		setSpinnerSelection(spinnerToUpdate, newUserVariable);
	}

	public void setInUserBrick(boolean inUserBrick) {
		this.inUserBrick = inUserBrick;
	}
}
