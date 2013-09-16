/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.UserBrickScriptActivity;
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddBrickFragment extends SherlockListFragment {

	private static final String BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category";
	public static final String ADD_BRICK_FRAGMENT_TAG = "add_brick_fragment";
	private ScriptFragment scriptFragment;
	private CharSequence previousActionBarTitle;
	private PrototypeBrickAdapter adapter;
	private CategoryBricksFactory categoryBricksFactory = new CategoryBricksFactory();
	public static AddBrickFragment addButtonHandler = null;

	private static UserBrick brickToFocus;
	private static int listIndexToFocus = -1;
	private boolean cameDirectlyFromScriptActivity = false;

	public static void setBrickFocus(UserBrick b) {
		brickToFocus = b;
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
		View view = inflater.inflate(R.layout.fragment_brick_add, null);

		setUpActionBar();
		setupSelectedBrickCategory();

		return view;
	}

	private void setupSelectedBrickCategory() {
		Context context = getActivity();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		String selectedCategory = getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY);

		List<Brick> brickList = categoryBricksFactory.getBricks(selectedCategory, sprite, context);
		adapter = new PrototypeBrickAdapter(context, brickList);
		setListAdapter(adapter);

		if (selectedCategory.equals(context.getString(R.string.category_user_bricks))) {
			addButtonHandler = this;

			if (brickToFocus != null) {
				cameDirectlyFromScriptActivity = true;
				int i = 0;
				for (Brick brick : brickList) {
					UserBrick b = ((UserBrick) brick);
					if (brickToFocus.isInstanceOf(b)) {

						listIndexToFocus = i;
						animateBrick(b, adapter);

						brickToFocus = null;
						break;
					}
					i++;
				}
			}

			//Log.d("FOREST", "ABF.setupSelectedCategory: // enable add button");
			// enable add button
			BottomBar.showBottomBar(getActivity());
			BottomBar.hidePlayButton(getActivity());
		}
	}

	private void animateBrick(final Brick b, PrototypeBrickAdapter adapter) {
		Context context = getActivity();
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				b.setAnimationState(true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				b.setAnimationState(false);
			}
		});

		View view = b.getView(context, 0, adapter);

		view.startAnimation(animation);
	}

	public void handleAddButton() {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		int newBrickId = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getAndIncrementUserBrickId();
		UserBrick newBrick = new UserBrick(currentSprite, newBrickId);
		newBrick.addUIText(scriptFragment.getString(R.string.new_user_brick) + " "
				+ currentSprite.getNextNewUserBrickId());
		newBrick.addUILocalizedVariable(getActivity(), R.string.new_user_brick_variable);

		setupSelectedBrickCategory();

		getListView().setSelection(getListView().getCount());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void setUpActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		previousActionBarTitle = actionBar.getTitle();
		actionBar.setTitle(this.getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY));
	}

	private void resetActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(previousActionBarTitle);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.delete).setVisible(false);
		menu.findItem(R.id.copy).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onDestroy() {
		resetActionBar();
		addButtonHandler = null;

		if (cameDirectlyFromScriptActivity) {
			BottomBar.showBottomBar(getActivity());
			BottomBar.showPlayButton(getActivity());
		} else {
			BottomBar.hideBottomBar(getActivity());
		}

		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		addButtonHandler = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupSelectedBrickCategory();
		addButtonHandler = this;

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

				if (clickedBrick instanceof UserBrick) {
					clickedOnUserBrick(((UserBrick) clickedBrick), view);
				} else {
					addBrickToScript(clickedBrick);
				}
			}

		});
	}

	private void clickedOnUserBrick(final UserBrick clickedBrick, View view) {
		final Context context = getActivity();

		final List<CharSequence> items = new ArrayList<CharSequence>();

		items.add(context.getText(R.string.brick_context_dialog_add_to_script));

		items.add(context.getText(R.string.brick_context_dialog_edit_brick));

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
		view.setDrawingCacheEnabled(true);
		view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		view.buildDrawingCache(true);

		if (view.getDrawingCache() != null) {
			Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(drawingCacheEnabled);

			ImageView imageView = getGlowingBorder(bitmap);
			builder.setCustomTitle(imageView);
		}

		builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				CharSequence clickedItemText = items.get(item);
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_add_to_script))) {
					addBrickToScript(clickedBrick);
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_edit_brick))) {
					//Log.d("FOREST", "ABF.launchBrickScriptActivityOnBrick");
					launchBrickScriptActivityOnBrick(context, clickedBrick);
				}
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void addBrickToScript(Brick brickToBeAdded) {
		scriptFragment.updateAdapterAfterAddNewBrick(brickToBeAdded.clone());

		if (brickToBeAdded instanceof ScriptBrick) {
			Script script = ((ScriptBrick) brickToBeAdded).initScript(ProjectManager.getInstance().getCurrentSprite());
			ProjectManager.getInstance().setCurrentScript(script);
		}

		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		Fragment categoryFragment = getFragmentManager().findFragmentByTag(
				BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
		if (categoryFragment != null) {
			fragmentTransaction.remove(categoryFragment);
			getFragmentManager().popBackStack();
		}
		Fragment addBrickFragment = getFragmentManager().findFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
		if (addBrickFragment != null) {
			fragmentTransaction.remove(addBrickFragment);
			getFragmentManager().popBackStack();
		}
		fragmentTransaction.commit();

		BottomBar.showBottomBar(getActivity());
	}

	public void launchBrickScriptActivityOnBrick(Context context, Brick brick) {
		Intent intent = new Intent(context, UserBrickScriptActivity.class);
		UserBrickScriptActivity.setUserBrick(brick); // TODO USE BUNDLE INSTEAD!!?

		context.startActivity(intent);
	}

	public ImageView getGlowingBorder(Bitmap bitmap) {
		ImageView imageView = new ImageView(getActivity());
		imageView.setBackgroundColor(Color.TRANSPARENT);
		imageView.setId(R.id.drag_and_drop_list_view_image_view);

		Bitmap glowingBitmap = Bitmap.createBitmap(bitmap.getWidth() + 30, bitmap.getHeight() + 30,
				Bitmap.Config.ARGB_8888);
		Canvas glowingCanvas = new Canvas(glowingBitmap);
		Bitmap alpha = bitmap.extractAlpha();
		Paint paintBlur = new Paint();
		paintBlur.setColor(Color.WHITE);
		glowingCanvas.drawBitmap(alpha, 15, 15, paintBlur);
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(15.0f, BlurMaskFilter.Blur.OUTER);
		paintBlur.setMaskFilter(blurMaskFilter);
		glowingCanvas.drawBitmap(alpha, 15, 15, paintBlur);
		paintBlur.setMaskFilter(null);
		glowingCanvas.drawBitmap(bitmap, 15, 15, paintBlur);

		imageView.setImageBitmap(glowingBitmap);

		return imageView;
	}
}
