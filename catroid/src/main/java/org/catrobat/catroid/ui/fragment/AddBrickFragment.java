/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BackPackModeListener;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.DeleteModeListener;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.UserBrickScriptActivity;
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.UserBrickNameDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilUi;

import java.util.ArrayList;
import java.util.List;

public class AddBrickFragment extends ListFragment implements DeleteModeListener, BackPackModeListener, PrototypeBrickAdapter.OnBrickCheckedListener, UserBrickNameDialog.UserBrickNameDialogInterface {

	private static final String BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category";
	public static final String ADD_BRICK_FRAGMENT_TAG = AddBrickFragment.class.getSimpleName();
	private ScriptFragment scriptFragment;
	private CharSequence previousActionBarTitle;
	private PrototypeBrickAdapter adapter;
	private CategoryBricksFactory categoryBricksFactory = new CategoryBricksFactory();
	public static AddBrickFragment addButtonHandler = null;
	protected boolean actionModeActive = false;
	private static String actionModeTitle;

	private static String singleItemAppendixActionMode;
	private static String multipleItemAppendixActionMode;

	private ActionMode actionMode;

	private static int listIndexToFocus = -1;
	private View selectAllActionModeButton;

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
		setupUiForUserBricks();

		return view;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.backpack).setVisible(scriptFragment.isInUserBrickOverview());
		super.onPrepareOptionsMenu(menu);
	}

	private void setupSelectedBrickCategory() {
		Context context = getActivity();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		String selectedCategory = getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY);

		List<Brick> brickList = categoryBricksFactory.getBricks(selectedCategory, sprite, context);
		adapter = new PrototypeBrickAdapter(context, scriptFragment, this, brickList);
		adapter.setOnBrickCheckedListener(this);
		setListAdapter(adapter);

		if (selectedCategory != null && selectedCategory.equals(getActivity().getString(R.string.category_user_bricks))) {
			addButtonHandler = this;

			ScriptActivity activity = (ScriptActivity) scriptFragment.getActivity();
			activity.setDeleteModeListener(this);
			activity.setBackPackModeListener(this);

			BottomBar.showBottomBar(getActivity());
			BottomBar.hidePlayButton(getActivity());
		}
	}

	public void handleAddButton() {

		UserBrickNameDialog userBrickNameDialog = new UserBrickNameDialog();
		userBrickNameDialog.setUserBrickNameDialogInterface(this);
		userBrickNameDialog.show(getActivity().getFragmentManager(), UserBrickNameDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onUserBrickNameEntered(String userBrickName) {
		addUserBrick(userBrickName);
	}

	private void addUserBrick(String name) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick newBrick = new UserBrick();
		currentSprite.addUserBrick(newBrick);

		UserScriptDefinitionBrick definitionBrick = newBrick.getDefinitionBrick();
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		String variableName = dataContainer.getUniqueVariableName(getActivity());

		definitionBrick.addUIText(name);
		definitionBrick.addUILocalizedVariable(variableName);

		dataContainer.addUserBrickVariableToUserBrick(newBrick, variableName, 0);
		ProjectManager.getInstance().setCurrentUserBrick(newBrick);

		setupSelectedBrickCategory();

		getListView().setSelection(getListView().getCount());
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		String selectedCategory = getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY);
		if (selectedCategory != null && selectedCategory.equals(getActivity().getString(R.string.category_user_bricks))) {
			menu.findItem(R.id.delete).setVisible(true);
		}
		menu.findItem(R.id.copy).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onDestroy() {
		resetActionBar();
		addButtonHandler = null;

		BottomBar.hideBottomBar(getActivity());

		ScriptActivity activity = (ScriptActivity) scriptFragment.getActivity();
		if (activity != null) {
			activity.setDeleteModeListener(null);
			activity.setBackPackModeListener(null);
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		addButtonHandler = null;
		ScriptActivity activity = (ScriptActivity) scriptFragment.getActivity();
		activity.setDeleteModeListener(null);
		activity.setBackPackModeListener(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupSelectedBrickCategory();
		addButtonHandler = this;
		setupUiForUserBricks();

		ScriptActivity activity = (ScriptActivity) scriptFragment.getActivity();
		activity.setDeleteModeListener(this);
		activity.setBackPackModeListener(this);
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
					showUserBricksView(((UserBrick) clickedBrick), view);
					BottomBar.showBottomBar(getActivity());
				} else {
					try {
						Brick brickToBeAdded = clickedBrick.clone();
						addBrickToScript(brickToBeAdded);
					} catch (CloneNotSupportedException cloneNotSupportedException) {
						Log.e(getTag(), "CloneNotSupportedException!", cloneNotSupportedException);
					}
				}
			}
		});
	}

	private void setupUiForUserBricks() {
		if (getActivity() instanceof UserBrickScriptActivity || scriptFragment.isInUserBrickOverview()) {
			BottomBar.hidePlayButton(getActivity());
			ActionBar actionBar = getActivity().getActionBar();
			if (actionBar != null) {
				String title = getActivity().getString(R.string.category_user_bricks);
				actionBar.setTitle(title);
			}
		}
	}

	private void showUserBricksView(final UserBrick clickedBrick, View view) {
		final Context context = getActivity();

		final CharSequence addToScript = context.getText(R.string.brick_context_dialog_add_to_script);
		final CharSequence editBrick = context.getText(R.string.brick_context_dialog_edit_brick);
		final CharSequence deleteBrick = context.getText(R.string.brick_context_dialog_delete_brick);
		final CharSequence backpackBrick = context.getText(R.string.backpack_add);
		final List<CharSequence> items = new ArrayList<>();
		items.add(addToScript);
		items.add(editBrick);
		items.add(backpackBrick);
		items.add(deleteBrick);

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
				if (clickedItemText.equals(addToScript)) {
					UserBrick newBrick = (UserBrick) clickedBrick.clone();
					addBrickToScript(newBrick);
				} else if (clickedItemText.equals(editBrick)) {
					clickedBrick.updateUserBrickParametersAndVariables();
					launchUserBrickScriptActivity(context, clickedBrick);
				} else if (clickedItemText.equals(backpackBrick)) {
					adapter.backpackSingleUserBrick(clickedBrick);
				} else if (clickedItemText.equals(deleteBrick)) {
					deleteBrick(clickedBrick);
				}
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void addBrickToScript(Brick brickToBeAdded) {
		try {
			scriptFragment.updateAdapterAfterAddNewBrick(brickToBeAdded.clone());

			if (brickToBeAdded instanceof ScriptBrick) {
				Script script = ((ScriptBrick) brickToBeAdded).getScriptSafe();
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
		} catch (CloneNotSupportedException exception) {
			Log.e(getTag(), "Adding a Brick was not possible because cloning it from the preview failed",
					exception);
			ToastUtil.showError(getActivity(), R.string.error_adding_brick);
		}
	}

	@Override
	public void startDeleteActionMode() {
		if (actionMode == null) { // ??
			startActionMode(deleteModeCallBack);
		}
	}

	@Override
	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback) {
		if (adapter.isEmpty()) {
			if (actionModeCallback.equals(deleteModeCallBack)) {
				((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
			} else if (actionModeCallback.equals(backPackModeCallBack)) {
				if (BackPackListManager.getInstance().getBackPackedUserBrickGroups().isEmpty()) {
					((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.backpack));
				} else {
					openBackPack();
				}
			}
		} else {
			actionMode = getActivity().startActionMode(actionModeCallback);

			unregisterForContextMenu(getListView());
			BottomBar.hideBottomBar(getActivity());
			adapter.setCheckboxVisibility(View.VISIBLE);
			//updateActionModeTitle();
		}
	}

	private ActionMode.Callback backPackModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			actionModeActive = true;

			actionModeTitle = getString(R.string.backpack);
			singleItemAppendixActionMode = getString(R.string.brick_single);
			multipleItemAppendixActionMode = getString(R.string.brick_multiple);
			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			adapter.setCheckboxVisibility(View.VISIBLE);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedBricksAndEnableButtons();
			} else {
				adapter.onDestroyActionModeBackPack();
			}
		}
	};

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			actionModeActive = true;

			actionModeTitle = getString(R.string.delete);
			singleItemAppendixActionMode = getString(R.string.brick_single);
			multipleItemAppendixActionMode = getString(R.string.brick_multiple);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedBricksAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};

	private void openBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_USERBRICKS);
		startActivity(intent);
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode,
				menu);
		selectAllActionModeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				adapter.checkAllItems();
			}
		});
	}

	public void setSelectMode(int selectMode) {
		adapter.notifyDataSetChanged();
	}

	private void deleteBrick(Brick brick) {
		int brickId = adapter.getBrickList().indexOf(brick);
		if (brickId != -1) {
			adapter.removeUserBrick(brick);
			ScriptFragment scriptFragment = (ScriptFragment)
					getFragmentManager().findFragmentByTag(ScriptFragment.TAG);
			scriptFragment.getAdapter().updateProjectBrickList();
		}
	}

	private void deleteCheckedBricks() {
		List<Brick> checkedBricks = adapter.getReversedCheckedBrickList();

		for (Brick brick : checkedBricks) {
			deleteBrick(brick);
		}
	}

	private void showConfirmDeleteDialog() {
		String yes = getActivity().getString(R.string.yes);
		String no = getActivity().getString(R.string.no);
		String title = "";
		if (adapter.getAmountOfCheckedItems() == 1) {
			title = getActivity().getString(R.string.dialog_confirm_delete_brick_title);
		} else {
			title = getActivity().getString(R.string.dialog_confirm_delete_multiple_bricks_title);
		}

		String message = getActivity().getString(R.string.dialog_confirm_delete_brick_message);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedBricks();
				clearCheckedBricksAndEnableButtons();
			}
		});
		builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedBricksAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void clearCheckedBricksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		actionModeActive = false;

		registerForContextMenu(getListView());
		BottomBar.showBottomBar(getActivity());
	}

	public void onBrickChecked() {
		if (actionMode == null) {
			return;
		}
		updateActionModeTitle();
		UtilUi.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(actionModeTitle);
		} else {
			String appendix = multipleItemAppendixActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = actionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = actionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	public static void launchUserBrickScriptActivity(Context context, UserBrick userBrick) {
		Intent intent = new Intent(context, UserBrickScriptActivity.class);
		ProjectManager.getInstance().setCurrentUserBrick(userBrick);
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

	public boolean isActionModeActive() {
		return actionModeActive;
	}
}
