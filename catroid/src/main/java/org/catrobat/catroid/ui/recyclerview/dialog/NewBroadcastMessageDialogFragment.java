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
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

public class NewBroadcastMessageDialogFragment extends TextInputDialogFragment {

	public static final String TAG = NewBroadcastMessageDialogFragment.class.getSimpleName();

	private NewItemInterface<String> newItemInterface;

	public NewBroadcastMessageDialogFragment() {
		super(R.string.dialog_new_broadcast_message_title, R.string.dialog_new_broadcast_message_name, null, false);
	}

	public NewBroadcastMessageDialogFragment(NewItemInterface<String> newItemInterface) {
		super(R.string.dialog_new_broadcast_message_title, R.string.dialog_new_broadcast_message_name, null, false);
		this.newItemInterface = newItemInterface;
	}

	@Override
	protected boolean onPositiveButtonClick() {
		String string = inputLayout.getEditText().getText().toString();
		newItemInterface.addItem(string);
		return true;
	}

	@Override
	protected void onNegativeButtonClick() {
	}
}
