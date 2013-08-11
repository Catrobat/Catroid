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
import android.content.DialogInterface;
import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.RegistrationData;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import org.catrobat.catroid.utils.UtilDeviceInfo;

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

public class RegistrationDialogStepTwoDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_register_step2";

	private Spinner countrySpinner;

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_country, null);

		countrySpinner = (Spinner) view.findViewById(R.id.country);

		addItemsOnCountrySpinner();

        Dialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle(R.string.register_dialog_title)
                .setPositiveButton(R.string.next_registration_step, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handleNextButtonClick();
                    }
                }).create();

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		return alertDialog;
	}

	private void addItemsOnCountrySpinner() {
		String[] countryList = getActivity().getResources().getStringArray(R.array.countries_array);

		for (int position = countryList.length - 1; position >= 0; position--) {
			String currentItem = countryList[position];
			int countryPosition = currentItem.indexOf("/") + 1;
			String newCountryString = currentItem.substring(countryPosition, currentItem.length());
			countryList[position] = newCountryString;
		}

		ArrayAdapter<CharSequence> countryAdapter = new ArrayAdapter<CharSequence>(getActivity(),
				android.R.layout.simple_spinner_item, countryList);
		countrySpinner.setAdapter(countryAdapter);
		String userCountry = UtilDeviceInfo.getUserCountryCode(getActivity());
		int localCountryPosition = 0;
		if (!userCountry.equals("")) {
			localCountryPosition = findSpinnerValuePosition(userCountry);
			countrySpinner.setSelection(localCountryPosition);
		}
	}

	private int findSpinnerValuePosition(String value) {
		String[] countryList = getActivity().getResources().getStringArray(R.array.countries_array);
		int position = 0;
		for (int stringArrayPosition = 0; stringArrayPosition <= countryList.length; stringArrayPosition++) {
			String currentItem = countryList[position];
			int countryPosition = currentItem.indexOf("/");
			String countryCode = currentItem.substring(0, countryPosition);
			if (countryCode.equals(value.toLowerCase())) {
				return position;
			}
			position++;
		}
		return 0;
	}

	private void handleNextButtonClick() {
		int countryPosition = countrySpinner.getSelectedItemPosition();
		String countryCodeString = getCountryCodeFromCountryId(countryPosition);
		RegistrationData.getInstance().setCountryCode(countryCodeString);

		RegistrationDialogStepThreeDialog registerStepThreeDialog = new RegistrationDialogStepThreeDialog();
		dismiss();
		registerStepThreeDialog.show(getFragmentManager(),
				RegistrationDialogStepThreeDialog.DIALOG_FRAGMENT_TAG);
	}

	private String getCountryCodeFromCountryId(int position) {
		String[] countryList = getActivity().getResources().getStringArray(R.array.countries_array);
		String country = countryList[position];
		int countryPosition = country.indexOf("/");
		String countryCode = country.substring(0, countryPosition);
		return countryCode;
	}

    @Override
    public void onRegistrationComplete(boolean success) {
        dismiss();
    }
}
