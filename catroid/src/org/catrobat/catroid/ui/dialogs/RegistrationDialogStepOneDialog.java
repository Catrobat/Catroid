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

import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CompoundButton;
import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.RegistrationData;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;

public class RegistrationDialogStepOneDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step1";
    private static final String MALE = "male";
    private static final String FEMALE = "female";
    private static final String OTHER = "other";
	private RadioButton maleRadioButton;
	private RadioButton femaleRadioButton;
	private RadioButton otherGenderRadioButton;
	private EditText otherGenderEdittext;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_gender, null);
        View titleView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_gender_title, null);

		maleRadioButton = (RadioButton) rootView.findViewById(R.id.dialog_register_gender_radiobutton_male);
		femaleRadioButton = (RadioButton) rootView.findViewById(R.id.dialog_register_gender_radiobutton_female);
		otherGenderRadioButton = (RadioButton) rootView.findViewById(R.id.dialog_register_gender_radiobutton_other);
		otherGenderEdittext = (EditText) rootView.findViewById(R.id.dialog_register_gender_edittext_other);

		initializeRadioButtons();

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
				.setTitle(R.string.register_dialog_title)
				.setPositiveButton(R.string.next_registration_step, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handleNextButtonClick();
                    }
                }).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						handleBackClick();
					}
				})
                .setCustomTitle(titleView)
                .create();

		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button nextButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);

                if(!(maleRadioButton.isChecked() || femaleRadioButton.isChecked() || otherGenderRadioButton.isChecked())){
                    nextButton.setEnabled(false);
                }

                maleRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        nextButton.setEnabled(true);
                    }
                });
                femaleRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        nextButton.setEnabled(true);
                    }
                });
                otherGenderRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        nextButton.setEnabled(true);
                    }
                });
                otherGenderEdittext.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        nextButton.setEnabled(true);
                        otherGenderRadioButton.setChecked(true);
                        maleRadioButton.setChecked(false);
                        femaleRadioButton.setChecked(false);
                        return true;
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

    private void initializeRadioButtons() {
        maleRadioButton.setChecked(false);
        femaleRadioButton.setChecked(false);
        otherGenderRadioButton.setChecked(false);

        maleRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleRadioButton.setChecked(false);
                otherGenderRadioButton.setChecked(false);
            }
        });

        femaleRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                maleRadioButton.setChecked(false);
                otherGenderRadioButton.setChecked(false);
            }
        });

        otherGenderRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                maleRadioButton.setChecked(false);
                femaleRadioButton.setChecked(false);
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(otherGenderEdittext, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        otherGenderEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                maleRadioButton.setChecked(false);
                femaleRadioButton.setChecked(false);
                otherGenderRadioButton.setChecked(true);
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(otherGenderEdittext, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
        });

        String gender = RegistrationData.getInstance().getGender();
        if(gender != null && !gender.isEmpty()){
            if(gender == MALE){
                maleRadioButton.setChecked(true);
            }else if(gender == FEMALE){
                femaleRadioButton.setChecked(true);
            }else {
                otherGenderRadioButton.setChecked(true);
                otherGenderEdittext.setText(gender);
            }
        }
	}

	private void handleNextButtonClick() {
	    setRegistrationData();
        dismiss();
		RegistrationDialogStepTwoDialog registerStepTwoDialog = new RegistrationDialogStepTwoDialog();
		registerStepTwoDialog.show(getActivity().getSupportFragmentManager(),
				RegistrationDialogStepTwoDialog.DIALOG_FRAGMENT_TAG);

	}

    private void setRegistrationData(){
        if (maleRadioButton.isChecked()) {
            RegistrationData.getInstance().setGender(MALE);
        } else if (femaleRadioButton.isChecked()) {
            RegistrationData.getInstance().setGender(FEMALE);
        } else {
            String gender = otherGenderEdittext.getText().toString();
            if (gender.isEmpty()) {
                gender = OTHER;
            }
            RegistrationData.getInstance().setGender(gender);
        }
    }

    private void handleBackClick() {
        RegistrationData.getInstance().clearData();
        dismiss();
    }

    @Override
	public void onRegistrationComplete(boolean success) {
		dismiss();
	}
}
