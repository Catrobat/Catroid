/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup.LayoutParams;

import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;

public class DialogAlreadyRegistered extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_already_registered";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.already_registered)
				.setNegativeButton(R.string.register, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						handleRegisterClick();
					}
				}).setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						handleAlreadyRegisteredClick();
					}
				}).create();

		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		return alertDialog;
	}

	private void handleAlreadyRegisteredClick() {
		LoginDialog loginDialog = new LoginDialog();
		loginDialog.show(getActivity().getSupportFragmentManager(), LoginDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
	}

	private void handleRegisterClick() {
		RegistrationDialogStepOneDialog registrationDialogStepOneDialog = new RegistrationDialogStepOneDialog();
		registrationDialogStepOneDialog.show(getActivity().getSupportFragmentManager(),
				RegistrationDialogStepOneDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
	}

	@Override
	public void onRegistrationComplete(boolean success) {
		dismiss();
	}
}
