package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class OrientationDialogFragment_Shruti extends DialogFragment {

	private EditText mEditText;

	public OrientationDialogFragment_Shruti() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_orientation_dialog_shruti, container);
		mEditText = (EditText) view.findViewById(R.id.txt_your_name);
		getDialog().setTitle("Hello");

		return view;
	}
}