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

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Spinner;

public class RegistrationDialogStepTwoDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step2";

	private Spinner countrySpinner;
	private Button nextButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_register_country, container);

		countrySpinner = (Spinner) rootView.findViewById(R.id.country);
		nextButton = (Button) rootView.findViewById(R.id.next_button);

		addItemsOnCountrySpinner();

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

	private void addItemsOnCountrySpinner() {
		List<String> list = new ArrayList<String>();
		String[] countries = getResources().getStringArray(R.array.countries_array);
		for (int position = 0; position < countries.length; position++) {
			list.add(countries[position]);
		}
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();
	}

	private void handleNextButtonClick() {
		String countryString = countrySpinner.getSelectedItem().toString();
		RegistrationData.INSTANCE.setBirthdayYear(countryString);

		RegistrationDialogStepThreeDialog registerStepThreeDialog = new RegistrationDialogStepThreeDialog();
		dismiss();
		registerStepThreeDialog.show(getActivity().getSupportFragmentManager(),
				RegistrationDialogStepThreeDialog.DIALOG_FRAGMENT_TAG);
	}
}
