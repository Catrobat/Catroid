/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter;
import org.catrobat.catroid.utils.ToastUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

public class UserDefinedBrickListFragment extends ListFragment implements View.OnClickListener {

	public static final String USER_DEFINED_BRICK_LIST_FRAGMENT_TAG =
			AddBrickFragment.class.getSimpleName();

	private AddBrickFragment.OnAddBrickListener addBrickListener;

	private ImageButton addUserDefinedBrickButton;
	private PrototypeBrickAdapter adapter;

	public static UserDefinedBrickListFragment newInstance(AddBrickFragment.OnAddBrickListener addBrickListener) {
		UserDefinedBrickListFragment fragment = new UserDefinedBrickListFragment();

		fragment.addBrickListener = addBrickListener;
		return fragment;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(R.string.categories);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_defined_brick_list, container, false);

		addUserDefinedBrickButton = view.findViewById(R.id.button_add_user_brick);
		addUserDefinedBrickButton.setOnClickListener(this);
		setHasOptionsMenu(true);
		setupUserDefinedBrickListView();

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(R.string.category_user_bricks);
			}
		}

		return view;
	}

	@Override
	public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.search).setVisible(false);
	}

	@Override
	public void onStart() {
		super.onStart();
		getListView().setOnItemClickListener((parent, view, position, id) -> addUserDefinedBrickToScript(adapter.getItem(position)));
	}

	private void setupUserDefinedBrickListView() {
		Context context = getActivity();
		CategoryBricksFactory categoryBricksFactory = new CategoryBricksFactory();

		if (context != null) {
			List<Brick> brickList =
					categoryBricksFactory.getBricks(getString(R.string.category_user_bricks), false, context);
			adapter = new PrototypeBrickAdapter(brickList);
			setListAdapter(adapter);
		}
	}

	@Override
	public void onClick(View v) {
		AddUserDefinedBrickFragment addUserDefinedBrickFragment =
				AddUserDefinedBrickFragment.Companion.newInstance(addBrickListener);

		UserDefinedBrick userDefinedBrick = new UserDefinedBrick();
		Bundle bundle = new Bundle();
		bundle.putSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT, userDefinedBrick);
		addUserDefinedBrickFragment.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager != null) {
			fragmentManager.beginTransaction()
					.add(R.id.fragment_container, addUserDefinedBrickFragment,
							AddUserDefinedBrickFragment.TAG)
					.addToBackStack(AddUserDefinedBrickFragment.TAG)
					.commit();
		}
	}

	private void addUserDefinedBrickToScript(Brick userDefinedBrickToAdd) {
		try {
			Brick clonedBrick = userDefinedBrickToAdd.clone();
			if (userDefinedBrickToAdd instanceof UserDefinedBrick) {
				((UserDefinedBrick) clonedBrick).setCallingBrick(true);
			}
			addBrickListener.addBrick(clonedBrick);

			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			Fragment categoryFragment = getFragmentManager()
					.findFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);

			if (categoryFragment != null) {
				fragmentTransaction.remove(categoryFragment);
				getFragmentManager().popBackStack();
			}

			Fragment addBrickFragment = getFragmentManager().findFragmentByTag(USER_DEFINED_BRICK_LIST_FRAGMENT_TAG);

			if (addBrickFragment != null) {
				fragmentTransaction.remove(addBrickFragment);
				getFragmentManager().popBackStack();
			}

			fragmentTransaction.commit();
		} catch (CloneNotSupportedException e) {
			Log.e(getTag(), e.getLocalizedMessage());
			ToastUtil.showError(getActivity(), R.string.error_adding_brick);
		}
	}
}
