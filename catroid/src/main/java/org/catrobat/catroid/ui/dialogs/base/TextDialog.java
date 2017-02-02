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

package org.catrobat.catroid.ui.dialogs.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public abstract class TextDialog extends DialogFragment {

	protected int title;
	protected String message;
	protected int okText;
	protected int cancelText;

	public TextDialog(int title, String message, int okText, int cancelText) {
		this.title = title;
		this.message = message;
		this.okText = okText;
		this.cancelText = cancelText;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(okText, null);
		builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				onCancel(dialog);
			}
		});

		final AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (handlePositiveButtonClick()) {
							dismiss();
						}
					}
				});
			}
		});
		return alertDialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		handleNegativeButtonClick();
		dismiss();
	}

	protected abstract boolean handlePositiveButtonClick();

	protected abstract void handleNegativeButtonClick();
}
