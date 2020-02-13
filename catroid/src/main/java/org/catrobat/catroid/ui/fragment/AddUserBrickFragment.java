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

import org.catrobat.catroid.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AddUserBrickFragment extends Fragment {

	public static final String ADD_USER_BRICK_FRAGMENT_TAG = AddBrickFragment.class.getSimpleName();

	private Button addLabel;
	private Button addInput;

	public static AddUserBrickFragment newInstance() {
		AddUserBrickFragment fragment = new AddUserBrickFragment();

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_new_user_brick, container, false);
		addLabel = view.findViewById(R.id.add_label);
		addInput = view.findViewById(R.id.add_input);
		addLabel.setOnClickListener(v -> handleAddLabel());
		addInput.setOnClickListener(v -> handleAddInput());

		((AppCompatActivity) getActivity())
				.getSupportActionBar().setTitle("Add new User Brick");

		return view;
	}

	private void handleAddLabel() {
		int i = 1;
	}

	private void handleAddInput() {
		int i = 1;
	}
}
