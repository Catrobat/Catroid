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
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.RegistrationData;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.RegistrationTask;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.web.ServerCalls;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public class LoginDialog extends DialogFragment implements OnRegistrationCompleteListener {

    private static final String PASSWORD_FORGOTTEN_PATH = "catroid/passwordrecovery?username=";
    public static final String DIALOG_FRAGMENT_TAG = "dialog_login";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private FragmentActivity fragmentActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_login, null);

        usernameEditText = (EditText) view.findViewById(R.id.username);
        passwordEditText = (EditText) view.findViewById(R.id.password);
        emailEditText = (EditText) view.findViewById(R.id.email);

        usernameEditText.setText(RegistrationData.getInstance().getUserName());
        passwordEditText.setText(RegistrationData.getInstance().getPassword());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String email = sharedPreferences.getString(Constants.EMAIL, Constants.NO_EMAIL);
        if(email.equals(Constants.NO_EMAIL)){
            email = UtilDeviceInfo.getUserEmail(getActivity());
        }

        emailEditText.setText(email);

        Dialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle(R.string.login_dialog_title)
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handleLoginButtonClick();
                    }
                }).setNegativeButton(R.string.password_forgotten, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handlePasswordForgottenButtonClick();
                    }
                })
                .create();

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        return alertDialog;
    }

    @Override
    public void onRegistrationComplete(boolean success) {
        if (success) {
            UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
            uploadProjectDialog.show(fragmentActivity.getSupportFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
        } else {
            LoginDialog loginDialog = new LoginDialog();
            loginDialog.show(fragmentActivity.getSupportFragmentManager(),
                    LoginDialog.DIALOG_FRAGMENT_TAG);
        }

    }

    private void handleLoginButtonClick() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        fragmentActivity = getActivity();
        RegistrationTask registrationTask = new RegistrationTask(fragmentActivity, username, password);
        registrationTask.setOnRegistrationCompleteListener(this);
        registrationTask.execute();
    }

    private void handlePasswordForgottenButtonClick() {
        String username = usernameEditText.getText().toString();
        String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTP : Constants.BASE_URL_HTTPS;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + PASSWORD_FORGOTTEN_PATH + username));
        getActivity().startActivity(browserIntent);
    }
}
