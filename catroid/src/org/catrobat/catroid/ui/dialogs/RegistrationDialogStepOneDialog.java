/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;

public class RegistrationDialogStepOneDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step1";

	private RadioButton maleRadioButton;
	private RadioButton femaleRadioButton;
	private Button nextButton;
	private Button alreadyRegisteredButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_register_gender, container);

		maleRadioButton = (RadioButton) rootView.findViewById(R.id.gender_male);
		femaleRadioButton = (RadioButton) rootView.findViewById(R.id.gender_female);
		nextButton = (Button) rootView.findViewById(R.id.next_button);
		alreadyRegisteredButton = (Button) rootView.findViewById(R.id.login_button);

		maleRadioButton.setChecked(true);
		femaleRadioButton.setChecked(false);

		maleRadioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				femaleRadioButton.setChecked(false);
			}
		});

		femaleRadioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				maleRadioButton.setChecked(false);
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleNextButtonClick();
			}
		});

		alreadyRegisteredButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleAlreadyRegisteredClick();
			}
		});

		getDialog().setTitle(R.string.register_dialog_title);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		return rootView;
	}

	private void handleNextButtonClick() {

		boolean isMale = maleRadioButton.isChecked();

		if (isMale) {
			RegistrationData.INSTANCE.setGender("male");
		} else {
			RegistrationData.INSTANCE.setGender("female");
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
