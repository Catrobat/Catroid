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

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class RegistrationDialogStepThreeDialog extends DialogFragment implements OnRegistrationCompleteListener,
		DatePickerDialog.OnDateSetListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step3";

	int birthdayDay = 0;
	int birthdayMonth = 0;
	int birthdayYear = 0;

	private TextView birthday;
	private DatePicker datePicker;
	private Button changeButton;
	private Button nextButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_register_birthday, container);

		birthday = (TextView) rootView.findViewById(R.id.birthday_date);
		nextButton = (Button) rootView.findViewById(R.id.next_button);
		changeButton = (Button) rootView.findViewById(R.id.change_button);
		datePicker = (DatePicker) rootView.findViewById(R.id.date_picker);

		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleNextButtonClick();
			}
		});

		changeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//getActivity().showDialog(DATE_DIALOG_ID);
			}
		});

		updateCalendar();

		getDialog().setTitle(R.string.register_dialog_title);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		return rootView;
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			birthdayYear = selectedYear;
			birthdayMonth = selectedMonth;
			birthdayDay = selectedDay;

			// set selected date into textview
			birthday.setText(new StringBuilder().append(birthdayDay).append(".").append(birthdayMonth + 1).append(".")
					.append(birthdayYear));

			// set selected date into datepicker also
			datePicker.init(birthdayYear, birthdayMonth, birthdayDay, null);

		}
	};

	//@Override
	//public Dialog onCreateDialog(Bundle savedInstanceState) {
	//return new DatePickerDialog(getActivity(), datePickerListener, birthdayDay, birthdayDay, birthdayDay);
	//}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	};

	public void setCurrentDateOnView() {
		final Calendar c = Calendar.getInstance();
		birthdayYear = c.get(Calendar.YEAR);
		birthdayMonth = c.get(Calendar.MONTH);
		birthdayDay = c.get(Calendar.DAY_OF_MONTH);

		// set current date into textview
		birthday.setText(new StringBuilder().append(birthdayDay).append(".").append(birthdayMonth + 1).append(".")
				.append(birthdayYear));

		// set current date into datepicker
		datePicker.init(birthdayYear, birthdayMonth, birthdayDay, null);

	}

	private void updateCalendar() {
		birthday.setText(birthdayDay + "." + (birthdayMonth) + "." + birthdayYear);
	}

	private void handleNextButtonClick() {
		RegistrationData.INSTANCE.setBirthday(birthdayDay + "." + birthdayMonth + "." + birthdayYear);

		RegistrationDialogStepFourDialog registerStepFourDialog = new RegistrationDialogStepFourDialog();
		dismiss();
		registerStepFourDialog.show(getActivity().getSupportFragmentManager(),
				RegistrationDialogStepFourDialog.DIALOG_FRAGMENT_TAG);
	}
}
