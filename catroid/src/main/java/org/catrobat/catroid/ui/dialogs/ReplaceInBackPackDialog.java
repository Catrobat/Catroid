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

package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.catrobat.catroid.R;

public class ReplaceInBackPackDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_replace_in_backpack";

	private int replaceDialogMessage;
	private String objectName;
	private ReplaceInBackPackInterface replaceInBackPackInterface;


	public ReplaceInBackPackDialog(int replaceDialogMessage, String objectName, ReplaceInBackPackInterface
			replaceInBackPackInterface) {
		this.replaceDialogMessage = replaceDialogMessage;
		this.objectName = objectName;
		this.replaceInBackPackInterface = replaceInBackPackInterface;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.backpack);

		int itemCount = replaceInBackPackInterface.getCheckedItemCount();

		builder.setMessage(getResources().getQuantityString(replaceDialogMessage, itemCount, objectName));
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				replaceInBackPackInterface.packCheckedItems();
				replaceInBackPackInterface.clearCheckedItems();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				onCancel(dialog);
			}
		});

		return builder.create();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		replaceInBackPackInterface.clearCheckedItems();
		dismiss();
	}

	public interface ReplaceInBackPackInterface {

		void clearCheckedItems();
		int getCheckedItemCount();
		void packCheckedItems();
	}
}
