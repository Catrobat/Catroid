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
import android.content.DialogInterface;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.RegistrationData;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;

public class RegistrationDialogStepOneDialog extends DialogFragment implements OnRegistrationCompleteListener {

    public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step1";

    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private RadioButton otherGenderRadioButton;
    private EditText otherGenderEdittext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_gender, null);

        maleRadioButton = (RadioButton) rootView.findViewById(R.id.gender_male);
        femaleRadioButton = (RadioButton) rootView.findViewById(R.id.gender_female);
        otherGenderRadioButton = (RadioButton) rootView.findViewById(R.id.gender_other);
        otherGenderEdittext = (EditText) rootView.findViewById(R.id.gender_other_edittext);

        initializeRadioButtons();

        Dialog alertDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
                .setTitle(R.string.register_dialog_title)
                .setNeutralButton(R.string.next_registration_step, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handleNextButtonClick();
                    }
                }).setNegativeButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handleAlreadyRegisteredClick();
                    }
                }).create();

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        return alertDialog;
    }

    private void initializeRadioButtons() {
        maleRadioButton.setChecked(true);
        femaleRadioButton.setChecked(false);
        otherGenderRadioButton.setChecked(false);
        otherGenderEdittext.setEnabled(false);

        maleRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleRadioButton.setChecked(false);
                otherGenderRadioButton.setChecked(false);
                otherGenderEdittext.setActivated(false);
                otherGenderEdittext.setEnabled(false);
            }
        });

        femaleRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                maleRadioButton.setChecked(false);
                otherGenderRadioButton.setChecked(false);
                otherGenderEdittext.setActivated(false);
                otherGenderEdittext.setEnabled(false);
            }
        });

        otherGenderRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                maleRadioButton.setChecked(false);
                femaleRadioButton.setChecked(false);
                otherGenderEdittext.setEnabled(true);
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(otherGenderEdittext, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void handleNextButtonClick() {

        if (maleRadioButton.isChecked()) {
            RegistrationData.getInstance().setGender("male");
        } else if (femaleRadioButton.isChecked()) {
            RegistrationData.getInstance().setGender("male");
        } else {
            String gender = otherGenderEdittext.getText().toString();
            if(gender.isEmpty()){
                gender = "other";
            }
            RegistrationData.getInstance().setGender(gender);
        }

        RegistrationDialogStepTwoDialog registerStepTwoDialog = new RegistrationDialogStepTwoDialog();
        dismiss();
        registerStepTwoDialog.show(getActivity().getSupportFragmentManager(),
                RegistrationDialogStepTwoDialog.DIALOG_FRAGMENT_TAG);
    }

    private void handleAlreadyRegisteredClick() {
        LoginDialog login = new LoginDialog();
        dismiss();
        login.show(getActivity().getSupportFragmentManager(), LoginDialog.DIALOG_FRAGMENT_TAG);
    }

    @Override
    public void onRegistrationComplete() {
        dismiss();
    }
}
