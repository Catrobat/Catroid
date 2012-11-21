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

import java.util.Calendar;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class RegistrationDialogStepFourDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step4";

	int birthdayDay = 0;
	int birthdayMonth = 0;
	int birthdayYear = 0;

	private Spinner monthSpinner;
	private Spinner yearSpinner;
	private Button nextButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_register_birthday, container);

		monthSpinner = (Spinner) rootView.findViewById(R.id.birthday_month);
		yearSpinner = (Spinner) rootView.findViewById(R.id.birthday_year);
		nextButton = (Button) rootView.findViewById(R.id.next_button);

		addItemsOnYearSpinner();
		addItemsOnMonthSpinner();

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

	private void addItemsOnYearSpinner() {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		String[] yearList = new String[currentYear - 1900];
		int position = 0;
		for (int start = 1900; start < currentYear; start++) {
			yearList[position] = Integer.toString(start);
			position++;
		}
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(),
				android.R.layout.simple_spinner_item, yearList);
		yearSpinner.setAdapter(adapter);
		yearSpinner.setSelection(position - 10);
	}

	private void addItemsOnMonthSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.months_array,
				android.R.layout.simple_spinner_item);
		monthSpinner.setAdapter(adapter);
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();
	}

	private void handleNextButtonClick() {
		String monthString = monthSpinner.getSelectedItem().toString();
		String yearString = yearSpinner.getSelectedItem().toString();
		RegistrationData.INSTANCE.setBirthdayMonth(monthString);
		RegistrationData.INSTANCE.setBirthdayYear(yearString);

		RegistrationDialogStepFiveDialog registerStepFiveDialog = new RegistrationDialogStepFiveDialog();
		dismiss();
		registerStepFiveDialog.show(getActivity().getSupportFragmentManager(),
				RegistrationDialogStepFiveDialog.DIALOG_FRAGMENT_TAG);
	}
}
