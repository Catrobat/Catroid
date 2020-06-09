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

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.BEGINNER_BRICKS;

public class AddBrickFragment extends ListFragment {

	public static final String ADD_BRICK_FRAGMENT_TAG = AddBrickFragment.class.getSimpleName();

	private static final String BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category";

	private ScriptFragment scriptFragment;
	private CharSequence previousActionBarTitle;
	private PrototypeBrickAdapter adapter;
	private static int listIndexToFocus = -1;

	private boolean onlyBeginnerBricks() {
		return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(BEGINNER_BRICKS, false);
	}

	public static AddBrickFragment newInstance(String selectedCategory, ScriptFragment scriptFragment) {
		AddBrickFragment fragment = new AddBrickFragment();
		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY, selectedCategory);
		fragment.setArguments(arguments);
		fragment.scriptFragment = scriptFragment;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_brick_add, container, false);
		previousActionBarTitle = ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle();
		((AppCompatActivity) getActivity())
				.getSupportActionBar().setTitle(getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY));

		setupSelectedBrickCategory();
		return view;
	}

	private void setupSelectedBrickCategory() {
		Context context = getActivity();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		Sprite backgroundSprite = ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite();
		String selectedCategory = getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY);

		CategoryBricksFactory categoryBricksFactory;
		if (onlyBeginnerBricks()) {
			categoryBricksFactory = new CategoryBeginnerBricksFactory();
		} else {
			categoryBricksFactory = new CategoryBricksFactory();
		}

		List<Brick> brickList = categoryBricksFactory.getBricks(selectedCategory, backgroundSprite.equals(sprite),
				context);
		adapter = new PrototypeBrickAdapter(brickList);
		setListAdapter(adapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menu.findItem(R.id.comment_in_out).setVisible(false);
		super.onCreateOptionsMenu(menu, menuInflater);
	}

	@Override
	public void onDestroy() {
		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		boolean isRestoringPreviouslyDestroyedActivity = actionBar == null;
		if (!isRestoringPreviouslyDestroyedActivity) {
			actionBar.setTitle(previousActionBarTitle);
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		setupSelectedBrickCategory();
	}

	@Override
	public void onStart() {
		super.onStart();

		if (listIndexToFocus != -1) {
			getListView().setSelection(listIndexToFocus);
			listIndexToFocus = -1;
		}

		getListView().setOnItemClickListener((parent, view, position, id) -> addBrickToScript(adapter.getItem(position)));
	}

	public void addBrickToScript(Brick brickToAdd) {
		if ((ProjectManager.getInstance().getCurrentProject().isCastProject())
				&& CastManager.unsupportedBricks.contains(brickToAdd.getClass())) {
			ToastUtil.showError(getActivity(), R.string.error_unsupported_bricks_chromecast);
			return;
		}

		try {
			brickToAdd = brickToAdd.clone();
			scriptFragment.addBrick(brickToAdd);

			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			Fragment categoryFragment = getFragmentManager()
					.findFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);

			if (categoryFragment != null) {
				fragmentTransaction.remove(categoryFragment);
				getFragmentManager().popBackStack();
			}

			Fragment addBrickFragment = getFragmentManager().findFragmentByTag(ADD_BRICK_FRAGMENT_TAG);

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
