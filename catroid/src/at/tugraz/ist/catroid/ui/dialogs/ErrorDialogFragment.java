/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import at.tugraz.ist.catroid.R;

public class ErrorDialogFragment extends DialogFragment {
	public static final String DIALOG_FRAGMENT_TAG = "error_dialog_fragment";

	public static ErrorDialogFragment newInstance(String errorMessage) {

		ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putString("errorMessage", errorMessage);
		errorDialogFragment.setArguments(arguments);

		return errorDialogFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setOnDismissListener(null);
		}
		super.onDestroyView();
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getActivity().getString(R.string.error));
		builder.setMessage(getArguments().getString("errorMessage"));
		builder.setNeutralButton(getActivity().getString(R.string.close), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		Dialog errorDialog = builder.create();
		return errorDialog;
	}
}
