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
package org.catrobat.catroid.ui.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class AddBrickFragment extends ListFragment {

	public static final String BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category";
	public static final String ADD_BRICK_FRAGMENT_TAG = AddBrickFragment.class.getSimpleName();
	private ScriptFragment scriptFragment;
	private CharSequence previousActionBarTitle;
	private PrototypeBrickAdapter adapter;
	private CategoryBricksFactory categoryBricksFactory = new CategoryBricksFactory();
	public static AddBrickFragment addButtonHandler = null;

	private static int listIndexToFocus = -1;

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

		setUpActionBar();
		setupSelectedBrickCategory();

		return view;
	}

	private void setupSelectedBrickCategory() {
		Context context = getActivity();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		String selectedCategory = getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY);

		List<Brick> brickList = categoryBricksFactory.getBricks(selectedCategory, sprite, context);
		adapter = new PrototypeBrickAdapter(context, scriptFragment, this, brickList, selectedCategory);
		setListAdapter(adapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void setUpActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(true);
			previousActionBarTitle = actionBar.getTitle();
			actionBar.setTitle(this.getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY));
		}
	}

	private void resetActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(previousActionBarTitle);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menu.findItem(R.id.comment_in_out).setVisible(false);
		super.onCreateOptionsMenu(menu, menuInflater);
	}

	@Override
	public void onDestroy() {
		resetActionBar();
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

		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Brick clickedBrick = adapter.getItem(position);
				try {
					Brick brickToBeAdded = clickedBrick.clone();
					addBrickToScript(brickToBeAdded);
				} catch (CloneNotSupportedException cloneNotSupportedException) {
					Log.e(getTag(), "CloneNotSupportedException!", cloneNotSupportedException);
				}
			}
		});
	}

	public void addBrickToScript(Brick brickToBeAdded) {
		try {
			scriptFragment.updateAdapterAfterAddNewBrick(brickToBeAdded.clone());

			if ((ProjectManager.getInstance().getCurrentProject().isCastProject())
					&& CastManager.unsupportedBricks.contains(brickToBeAdded.getClass())) {
				ToastUtil.showError(getActivity(), R.string.error_unsupported_bricks_chromecast);
				return;
			}

			if (brickToBeAdded instanceof ScriptBrick) {
				Script script = ((ScriptBrick) brickToBeAdded).getScriptSafe();
				ProjectManager.getInstance().setCurrentScript(script);
			}

			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			Fragment categoryFragment = getFragmentManager().findFragmentByTag(BaseBrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
			if (categoryFragment != null) {
				fragmentTransaction.remove(categoryFragment);
				getFragmentManager().popBackStack();
			}
			Fragment addBrickFragment = getFragmentManager().findFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
			if (addBrickFragment != null) {
				fragmentTransaction.remove(addBrickFragment);
				getFragmentManager().popBackStack();
			}

			Utils.getTrackingUtilProxy().trackAddBrick(addBrickFragment, brickToBeAdded);

			fragmentTransaction.commit();
		} catch (CloneNotSupportedException exception) {
			Log.e(getTag(), "Adding a Brick was not possible because cloning it from the preview failed", exception);
			ToastUtil.showError(getActivity(), R.string.error_adding_brick);
		}
	}
}
