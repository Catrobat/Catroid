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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AddUserBrickFragment extends Fragment {

	public static final String TAG = AddUserBrickFragment.class.getSimpleName();

	private UserDefinedBrick userDefinedBrick;
	private View userBrickView;
	private LinearLayout userBrickSpace;

	private Button addLabel;
	private Button addInput;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_new_user_brick, container, false);
		userBrickSpace = view.findViewById(R.id.user_brick_space);

		addLabel = view.findViewById(R.id.button_add_label);
		addInput = view.findViewById(R.id.button_add_input);

		addLabel.setOnClickListener(v -> handleAddLabel());
		addInput.setOnClickListener(v -> handleAddInput());

		Bundle arguments = getArguments();
		if (arguments != null) {
			userDefinedBrick =
					(UserDefinedBrick) getArguments().getSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT);
			if (userDefinedBrick != null) {
				userBrickView = userDefinedBrick.getView(getActivity());
				userBrickSpace.addView(userBrickView);
			}
		}

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(R.string.brick_add_new_user_brick);
			}
		}

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(R.string.category_user_bricks);
			}
		}
	}

	private void handleAddLabel() {
		userDefinedBrick.addLabel(getResources().getString(R.string.brick_user_defined_default_label));
		updateBrickView();
	}

	private void handleAddInput() {
		AddInputToUserBrickFragment addInputToUserBrickFragment = new AddInputToUserBrickFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT, userDefinedBrick);
		addInputToUserBrickFragment.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager != null) {
			fragmentManager.beginTransaction()
					.add(R.id.fragment_container, addInputToUserBrickFragment, AddInputToUserBrickFragment.TAG)
					.addToBackStack(AddInputToUserBrickFragment.TAG)
					.commit();
		}
	}

	void addInputToUserBrick(Nameable input) {
		userDefinedBrick.addInput(input);
		updateBrickView();
	}

	private void updateBrickView() {
		userBrickSpace.removeView(userBrickView);
		userBrickView = userDefinedBrick.getView(getActivity());
		userBrickSpace.addView(userBrickView);
	}
}
