/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AddInputToUserBrickFragment extends Fragment {

	public static final String TAG = AddInputToUserBrickFragment.class.getSimpleName();

	private AppCompatActivity activity;
	private TextInputEditText addInputUserBrickEditText;
	private TextInputLayout addInputUserBrickTextLayout;

	private MenuItem nextItem;

	private UserDefinedBrick userDefinedBrick;
	private TextView userBrickTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_input_to_user_brick, container, false);

		addInputUserBrickEditText = view.findViewById(R.id.input_user_brick_edit_field);
		addInputUserBrickTextLayout = view.findViewById(R.id.input_user_brick_text_layout);
		LinearLayout userBrickSpace = view.findViewById(R.id.user_brick_space);

		Bundle arguments = getArguments();
		if (arguments != null) {
			userDefinedBrick = (UserDefinedBrick) getArguments().getSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT);
			if (userDefinedBrick != null) {
				View userBrickView = userDefinedBrick.getView(getActivity());
				userBrickSpace.addView(userBrickView);
				userBrickTextView = userDefinedBrick.currentInputEditText;
			}
		}

		activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				activity.getSupportActionBar().setTitle("Your Bricks");
			}
		}

		addInputUserBrickEditText.setText(userBrickTextView.getText());
		addInputUserBrickEditText.addTextChangedListener(new InputTextWatcher());

		userDefinedBrick.scrollToBottom();

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		showStandardSystemKeyboard();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		hideStandardSystemKeyboard();
	}

	@Override
	public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_next, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(@NonNull Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		nextItem = menu.findItem(R.id.next);
		nextItem.setVisible(true);
		nextItem.setEnabled(true);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.next) {
			FragmentManager fragmentManager = getFragmentManager();
			if (fragmentManager != null) {
				AddUserBrickFragment addUserBrickFragment = (AddUserBrickFragment)
						getFragmentManager().findFragmentByTag(AddUserBrickFragment.TAG);
				getFragmentManager().popBackStackImmediate();
				if (addUserBrickFragment != null) {
					addUserBrickFragment.addInputToUserBrick(new StringOption(addInputUserBrickEditText.getText().toString()));
				}
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void setNextItemEnabled(boolean enabled) {
		nextItem.setEnabled(enabled);
	}

	private void showStandardSystemKeyboard() {
		if (activity != null) {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}

	private void hideStandardSystemKeyboard() {
		if (activity != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			View focusedView = activity.getCurrentFocus();
			if (focusedView != null) {
				inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
			}
		}
	}
	private class InputTextWatcher implements TextWatcher {

		private boolean isNameUnique(String name) {
			for (Nameable item : userDefinedBrick.getInputList()) {
				if (item.getName().equals(name)) {
					return false;
				}
			}
			return true;
		}

		String validateName(String name) {
			if (name.isEmpty()) {
				return getString(R.string.name_empty);
			}

			name = name.trim();

			if (name.isEmpty()) {
				return getString(R.string.name_consists_of_spaces_only);
			}

			if (!isNameUnique(name)) {
				return getString(R.string.name_already_exists);
			}

			return null;
		}
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			userDefinedBrick.scrollToBottom();
		}

		@Override
		public void afterTextChanged(Editable editable) {
			String error = validateName(editable.toString());
			userBrickTextView.setText(editable.toString());
			addInputUserBrickTextLayout.setError(error);
			setNextItemEnabled(error == null);
		}
	}
}
