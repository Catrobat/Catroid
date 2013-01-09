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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationDialogStepThreeDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step3";

	private EditText city;
	private Button nextButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_register_city, container);

		city = (EditText) rootView.findViewById(R.id.city);
		nextButton = (Button) rootView.findViewById(R.id.next_button);
		nextButton.setEnabled(false);

		city.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (city.length() == 0) {
					nextButton.setEnabled(false);
				} else {
					nextButton.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleNextButtonClick();
			}
		});

		getDialog().setTitle(R.string.register_dialog_title);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		return rootView;
	}

	private void handleNextButtonClick() {

		String cityString = city.getText().toString();
		RegistrationData.INSTANCE.setCity(cityString);

		RegistrationDialogStepFourDialog registerStepFourDialog = new RegistrationDialogStepFourDialog();
		dismiss();
		registerStepFourDialog.show(getActivity().getSupportFragmentManager(),
				RegistrationDialogStepFourDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();
	}
}
