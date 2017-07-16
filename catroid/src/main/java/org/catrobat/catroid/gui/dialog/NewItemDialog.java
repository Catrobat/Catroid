/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.gui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.catrobat.catroid.R;

public class NewItemDialog extends DialogFragment {

	public static final String TAG = NewItemDialog.class.getSimpleName();

	private int title;
	private NewItemInterface newItemInterface;
	private TextInputLayout input;

	public NewItemDialog(int title, NewItemInterface newItemInterface) {
		this.title = title;
		this.newItemInterface = newItemInterface;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_new_item, container, false);

		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
		toolbar.setTitle(title);

		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		setHasOptionsMenu(true);

		input = (TextInputLayout) view.findViewById(R.id.input);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.dialog_new_options, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.btnSave:
				handleSaveButton();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				input.getEditText().addTextChangedListener(getTextWatcher());
			}
		});

		return dialog;
	}

	private void handleSaveButton() {
		String name = input.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			input.setError(getString(R.string.name_consists_of_spaces_only));
			return;
		}

		if (newItemInterface.isItemNameUnique(name)) {
			newItemInterface.addItem(name);
			getFragmentManager().popBackStack();
		} else {
			input.setError(getString(R.string.name_already_exists));
		}
	}

	private TextWatcher getTextWatcher() {
		return new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				input.setError(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
	}

	public interface NewItemInterface {

		boolean isItemNameUnique(String name);

		void addItem(String name);
	}
}
