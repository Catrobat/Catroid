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
import android.widget.ImageButton;

import org.catrobat.catroid.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

public class UserBrickListFragment extends ListFragment implements View.OnClickListener {

	public static final String USER_BRICK_LIST_FRAGMENT_TAG =
			AddBrickFragment.class.getSimpleName();

	private ImageButton addUserBrickButton;

	public static UserBrickListFragment newInstance() {
		UserBrickListFragment fragment = new UserBrickListFragment();

		return fragment;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
		if (appCompatActivity != null && appCompatActivity.getSupportActionBar() != null) {
			appCompatActivity.getSupportActionBar().setTitle(R.string.categories);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_brick_list, container, false);
		setHasOptionsMenu(true);
		addUserBrickButton = view.findViewById(R.id.add_user_brick);
		addUserBrickButton.setOnClickListener(this);

		AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
		if (appCompatActivity != null && appCompatActivity.getSupportActionBar() != null) {
			appCompatActivity.getSupportActionBar().setTitle(R.string.category_user_bricks);
		}

		return view;
	}

	@Override
	public void onClick(View v) {
		AddUserBrickFragment addUserBrickFragment = AddUserBrickFragment.newInstance();
		String tag = AddUserBrickFragment.ADD_USER_BRICK_FRAGMENT_TAG;

		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager != null) {
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_container, addUserBrickFragment, tag)
					.addToBackStack(null)
					.commit();
		}
	}
}
