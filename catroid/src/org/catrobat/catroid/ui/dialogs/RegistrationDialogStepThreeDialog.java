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

import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.RegistrationData;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import org.catrobat.catroid.utils.UtilDeviceInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationDialogStepThreeDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step3";

	private EditText emailEditText;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_email, null);
        View titleView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_email_title, null);

		emailEditText = (EditText) view.findViewById(R.id.dialog_register_edittext_email);
		final String userEmail = UtilDeviceInfo.getUserEmail(getActivity());

		final Dialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view)
                .setCustomTitle(titleView)
				.setPositiveButton(R.string.next_registration_step, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
					}
                }).setNegativeButton(R.string.previous_registration_step, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handleBackClick();
                    }
                })
                .create();

		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (userEmail == null || userEmail.isEmpty()) {
                    ((AlertDialog) alertDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
                    emailEditText.setText(userEmail);
				}
				emailEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (emailEditText.length() == 0) {
							((AlertDialog) alertDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
						} else {
							((AlertDialog) alertDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
						}
					}
				});

				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						handleNextButtonClick();
					}
				});
			}
		});

		return alertDialog;
	}

	private void handleNextButtonClick() {

		String emailString = emailEditText.getText().toString().trim();
		RegistrationData.getInstance().setEmail(emailString);

		RegistrationDialogStepFourDialog registerStepFourDialog = new RegistrationDialogStepFourDialog();
		registerStepFourDialog.show(getActivity().getSupportFragmentManager(),
				RegistrationDialogStepFourDialog.DIALOG_FRAGMENT_TAG);
	}

    private void handleBackClick() {
        RegistrationDialogStepTwoDialog registerStepTwoDialog = new RegistrationDialogStepTwoDialog();
        registerStepTwoDialog.show(getActivity().getSupportFragmentManager(),
                RegistrationDialogStepTwoDialog.DIALOG_FRAGMENT_TAG);
    }

	@Override
	public void onRegistrationComplete(boolean success) {
		dismiss();
	}
}
