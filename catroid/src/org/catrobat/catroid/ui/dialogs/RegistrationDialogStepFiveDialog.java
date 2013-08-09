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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.RegistrationData;
import org.catrobat.catroid.transfers.RegistrationTask;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegistrationDialogStepFiveDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step5";

	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText passwordConfirmationEditText;
	private CheckBox showPassword;
    private TextView termsOfUseLinkTextView;
    private AlertDialog alertDialog;

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_username_password, null);

		usernameEditText = (EditText) rootView.findViewById(R.id.username);
		passwordEditText = (EditText) rootView.findViewById(R.id.password);
		passwordConfirmationEditText = (EditText) rootView.findViewById(R.id.password_confirmation);
		showPassword = (CheckBox) rootView.findViewById(R.id.show_password);
        termsOfUseLinkTextView = (TextView) rootView.findViewById(R.id.register_terms_link);

        String termsOfUseUrl = getActivity().getString(R.string.about_link_template, Constants.CATROBAT_TERMS_OF_USE_URL,
                getString(R.string.register_pocketcode_terms_of_use_text));
        termsOfUseLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        termsOfUseLinkTextView.setText(Html.fromHtml(termsOfUseUrl));

		usernameEditText.setText("");
		passwordEditText.setText("");
		passwordConfirmationEditText.setText("");
		showPassword.setChecked(false);

		showPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (showPassword.isChecked()) {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
					passwordConfirmationEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordConfirmationEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

        alertDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
                .setTitle(R.string.register_dialog_title)
                .setNeutralButton(R.string.next_registration_step, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handleRegisterButtonClick();
                    }
                }).create();

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        return alertDialog;
	}

	private void handleRegisterButtonClick() {

		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		String passwordConfirmation = passwordConfirmationEditText.getText().toString();

		if (!password.equals(passwordConfirmation)) {
			new Builder(getActivity()).setTitle(R.string.register_error)
					.setMessage(R.string.register_password_mismatch).setPositiveButton(android.R.string.ok, null).show();
			return;
		}
        Context context = getActivity();
		RegistrationTask registrationTask = new RegistrationTask(context, username, password, alertDialog);
		registrationTask.setOnRegistrationCompleteListener(this);
		registrationTask.execute();
	}

	@Override
	public void onRegistrationComplete() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		RegistrationData.getInstance().setUserName(username);
		RegistrationData.getInstance().setPassword(password);

		RegistrationDialogStepSixDialog registerStepSixDialog = new RegistrationDialogStepSixDialog();
		dismiss();
		registerStepSixDialog.show(getActivity().getSupportFragmentManager(),
                RegistrationDialogStepSixDialog.DIALOG_FRAGMENT_TAG);
	}
}
