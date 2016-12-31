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
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BackPackModeListener;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.DeleteModeListener;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.BrickBaseAdapter;
import org.catrobat.catroid.ui.adapter.UserBrickAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.UserBrickNameDialog;
import org.catrobat.catroid.utils.UtilUi;

import java.util.List;

public class UserBrickFragment extends ScriptActivityFragment implements DeleteModeListener, BackPackModeListener, UserBrickNameDialog
		.UserBrickNameDialogInterface {

	public static final String TAG = UserBrickFragment.class.getSimpleName();

	private static final int ACTION_MODE_DELETE = 1;
	private static final int ACTION_MODE_BACKPACK = 2;

	private UserBrickAdapter adapter;

	private static int listIndexToFocus = -1;
	private ActionMode actionMode;

	private String actionModeTitle;

	private ScriptFragment scriptFragment;

	public static UserBrickFragment newInstance(ScriptFragment scriptFragment) {
		UserBrickFragment fragment = new UserBrickFragment();
		fragment.scriptFragment = scriptFragment;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_brick_add, container, false);

		setUpActionBar();
		adapter = new UserBrickAdapter(this, scriptFragment.getAdapter());
		setListAdapter(adapter);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.backpack).setVisible(true);
		menu.findItem(R.id.delete).setVisible(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupUserBricks();
		setUpActionBar();
		adapter.initBrickList();
	}

	private void setUpActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(getActivity().getString(R.string.category_user_bricks));
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menu.findItem(R.id.comment_in_out).setVisible(false);
		super.onCreateOptionsMenu(menu, menuInflater);
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
				Brick clickedBrick = (Brick) adapter.getItem(position);
				try {
					Brick brickToBeAdded = clickedBrick.clone();
					addBrickToScript(brickToBeAdded);
				} catch (CloneNotSupportedException cloneNotSupportedException) {
					Log.e(getTag(), "CloneNotSupportedException!", cloneNotSupportedException);
				}
			}
		});

		BottomBar.showBottomBar(getActivity());
		BottomBar.hidePlayButton(getActivity());
	}

	@Override
	public void onDestroy() {
		resetActionBar();
		((ScriptActivity) getActivity()).setCurrentFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		super.onDestroy();
	}

	public void addBrickToScript(Brick brickToBeAdded) {
		scriptFragment.updateAdapterAfterAddNewBrick(brickToBeAdded);
		((ScriptActivity) getActivity()).setCurrentFragment(ScriptActivity.FRAGMENT_SCRIPTS);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		Fragment categoryFragment = fragmentManager.findFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
		if (categoryFragment != null) {
			fragmentTransaction.remove(categoryFragment);
			fragmentManager.popBackStack();
		}

		Fragment userBrickFragment = fragmentManager.findFragmentByTag(UserBrickFragment.TAG);
		if (userBrickFragment != null) {
			fragmentTransaction.remove(userBrickFragment);
			fragmentManager.popBackStack();
		}
		fragmentTransaction.commit();
	}

	private void resetActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(getActivity().getString(R.string.categories));
		}
	}

	public void handleAddButton() {
		UserBrickNameDialog userBrickNameDialog = new UserBrickNameDialog();
		userBrickNameDialog.setUserBrickNameDialogInterface(this);
		userBrickNameDialog.show(getActivity().getFragmentManager(), UserBrickNameDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void handleCheckBoxClick(View view) {
	}

	@Override
	public void showRenameDialog() {
		//not supported
	}

	@Override
	public void showDeleteDialog() {
		int titleId;
		if (adapter.getCount() == 1) {
			titleId = R.string.dialog_confirm_delete_brick_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_bricks_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_brick_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedBricks();
				clearCheckedBricksAndEnableButtons();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				clearCheckedBricksAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void deleteCheckedBricks() {
		List<Brick> checkedBricks = adapter.getReversedCheckedBrickList();

		for (Brick brick : checkedBricks) {
			adapter.deleteBrick((UserBrick) brick);
		}
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

		setupUserBricks();

		getListView().setSelection(getListView().getCount());
	}

	private void setupUserBricks() {
		BottomBar.showBottomBar(getActivity());
		BottomBar.showAddButton(getActivity());
		BottomBar.hidePlayButton(getActivity());

		adapter.initBrickList();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack);
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

			for (int i = 0; i < adapter.getBrickList().size(); i++) {
				adapter.getView(i, null, getListView());
			}

			unregisterForContextMenu(getListView());
			BottomBar.hideBottomBar(getActivity());

			if (actionModeCallback.equals(deleteModeCallBack)) {
				adapter.setActionMode(BrickBaseAdapter.ActionModeEnum.COPY_DELETE);
			} else if (actionModeCallback.equals(backPackModeCallBack)) {
				adapter.setActionMode(BrickBaseAdapter.ActionModeEnum.BACKPACK);
			}

			adapter.setCheckboxVisibility(true);
			updateActionModeTitle();
		}
	}

	public void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		String completeTitle;
		switch ((Integer) actionMode.getTag()) {
			case ACTION_MODE_DELETE:
				completeTitle = getResources().getQuantityString(R.plurals.number_of_bricks_to_delete,
						numberOfSelectedItems, numberOfSelectedItems);
				break;
			case ACTION_MODE_BACKPACK:
				completeTitle = getResources().getQuantityString(R.plurals.number_of_bricks_to_backpack,
						numberOfSelectedItems, numberOfSelectedItems);
				if (numberOfSelectedItems == 0) {
					completeTitle = getString(R.string.backpack);
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong or unhandled tag in ActionMode.");
		}

		int indexOfNumber = completeTitle.indexOf(' ') + 1;
		Spannable completeSpannedTitle = new SpannableString(completeTitle);
		if (!completeTitle.equals(getString(R.string.backpack))) {
			completeSpannedTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)),
					indexOfNumber, indexOfNumber + String.valueOf(numberOfSelectedItems).length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		actionMode.setTitle(completeSpannedTitle);
	}

	public void clearCheckedBricksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		actionModeActive = false;

		registerForContextMenu(getListView());
		BottomBar.showBottomBar(getActivity());
	}

	private void openBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_USERBRICKS);
		startActivity(intent);
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		View selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				adapter.checkAllItems();
				updateActionModeTitle();
			}
		});
	}

	@Override
	public boolean getShowDetails() {
		//not supported
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		//not supported
	}

	public void setSelectMode(int selectMode) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public int getSelectMode() {
		return 0;
	}

	@Override
	public void startCopyActionMode() {
		//not supported
	}

	@Override
	public void startCommentOutActionMode() {
		//not supported
	}

	@Override
	public void startRenameActionMode() {
		//not supported
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

			mode.setTag(ACTION_MODE_BACKPACK);
			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			adapter.setCheckboxVisibility(true);
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
			adapter.setActionMode(BrickAdapter.ActionModeEnum.NO_ACTION);
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

			mode.setTag(ACTION_MODE_DELETE);
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
				showDeleteDialog();
			}
			adapter.setActionMode(BrickAdapter.ActionModeEnum.NO_ACTION);
		}
	};
}
