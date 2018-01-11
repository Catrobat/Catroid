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
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.AllowedAfterDeadEndBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.DeadEndBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.commands.ChangeFormulaCommand;
import org.catrobat.catroid.content.commands.CommandFactory;
import org.catrobat.catroid.content.commands.OnFormulaChangedListener;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.UserBrickSpriteActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ScriptFragment extends ListFragment implements OnCategorySelectedListener, OnFormulaChangedListener {

	public static final String TAG = ScriptFragment.class.getSimpleName();

	private static final int ACTION_MODE_COPY = 0;
	private static final int ACTION_MODE_DELETE = 1;
	private static final int ACTION_MODE_BACKPACK = 2;
	private static final int ACTION_MODE_COMMENT_OUT = 3;

	protected boolean actionModeActive = false;

	private ActionMode actionMode;

	private BrickAdapter adapter;
	private BrickListView listView;

	private Sprite sprite;
	private Script scriptToEdit;

	private boolean deleteScriptFromContextMenu = false;

	private boolean backpackMenuIsVisible = true;

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTag(ACTION_MODE_DELETE);
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
				showConfirmDeleteDialog(false);
			}
			adapter.setActionMode(BrickAdapter.ActionModeEnum.NO_ACTION);
		}
	};

	private ActionMode.Callback commentOutModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTag(ACTION_MODE_COMMENT_OUT);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			clearCheckedBricksAndEnableButtons();
			adapter.setActionMode(BrickAdapter.ActionModeEnum.NO_ACTION);
		}
	};

	private ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTag(ACTION_MODE_COPY);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			List<Brick> checkedBricks = adapter.getCheckedBricks();

			for (Brick brick : checkedBricks) {
				copyBrick(brick);
				if (brick instanceof ScriptBrick) {
					break;
				}
			}
			clearCheckedBricksAndEnableButtons();
			adapter.setActionMode(BrickAdapter.ActionModeEnum.NO_ACTION);
		}
	};

	private ActionMode.Callback backPackModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTag(ACTION_MODE_BACKPACK);
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
				adapter.onDestroyActionModeBackPack();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = View.inflate(getActivity(), R.layout.fragment_script, null);
		listView = (BrickListView) rootView.findViewById(android.R.id.list);

		setupUiForUserBricks();
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scripts);
		return rootView;
	}

	private void setupUiForUserBricks() {
		if (getActivity() instanceof UserBrickSpriteActivity || isInUserBrickOverview()) {
			BottomBar.hidePlayButton(getActivity());
			ActionBar actionBar = getActivity().getActionBar();
			if (actionBar != null) {
				String title = getActivity().getString(R.string.category_user_bricks);
				actionBar.setTitle(title);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initListeners();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);

		if (getActivity() instanceof UserBrickSpriteActivity || isInUserBrickOverview()) {
			backpackMenuIsVisible = false;
		}
		menu.findItem(R.id.backpack).setVisible(backpackMenuIsVisible);
		handlePlayButtonVisibility();
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onStart() {
		super.onStart();
		BottomBar.showBottomBar(getActivity());
		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		setupUiForUserBricks();

		if (BackPackListManager.getInstance().isBackpackEmpty()) {
			BackPackListManager.getInstance().loadBackpack();
		}

		BottomBar.showBottomBar(getActivity());
		BottomBar.showPlayButton(getActivity());
		BottomBar.showAddButton(getActivity());
		initListeners();
		if (adapter != null) {
			adapter.resetAlphas();
		}
		handleInsertFromBackpack();
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (projectManager.getCurrentScene() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
			projectManager.getCurrentProject().updateMessageContainer(); // TODO: Find better place
		}
	}

	public BrickAdapter getAdapter() {
		return adapter;
	}

	@Override
	public BrickListView getListView() {
		return listView;
	}

	@Override
	public void onCategorySelected(String category) {
		AddBrickFragment addBrickFragment = AddBrickFragment.newInstance(category, this);
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragment_container, addBrickFragment,
				AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);

		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		adapter.notifyDataSetChanged();
	}

	public void updateAdapterAfterAddNewBrick(Brick brickToBeAdded) {
		backpackMenuIsVisible = true;
		int firstVisibleBrick = listView.getFirstVisiblePosition();
		int lastVisibleBrick = listView.getLastVisiblePosition();
		int position = ((1 + lastVisibleBrick - firstVisibleBrick) / 2);
		position += firstVisibleBrick;

		adapter.addNewBrick(position, brickToBeAdded, true);
		adapter.notifyDataSetChanged();
	}

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}

		adapter = new BrickAdapter(this, sprite, listView);

		if (getActivity() instanceof UserBrickSpriteActivity) {
			((UserBrickSpriteActivity) getActivity()).setupBrickAdapter(adapter);
			setupUiForUserBricks();
		}

		if (ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts() > 0) {
			ProjectManager.getInstance().setCurrentScript(((ScriptBrick) adapter.getItem(0)).getScriptSafe());
		}

		listView.setOnCreateContextMenuListener(this);
		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
	}

	private void showCategoryFragment() {
		BrickCategoryFragment brickCategoryFragment = new BrickCategoryFragment();
		brickCategoryFragment.setBrickAdapter(adapter);
		brickCategoryFragment.setOnCategorySelectedListener(this);
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.add(R.id.fragment_container, brickCategoryFragment,
				BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);

		fragmentTransaction.addToBackStack(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
		fragmentTransaction.commit();
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_category);

		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (listView.isCurrentlyDragging()) {
			listView.animateHoveringBrick();
			return true;
		}

		switch (item.getItemId()) {
			case android.R.id.home:
				getActivity().onBackPressed();
				return true;

			case R.id.backpack:
				startBackPackActionMode();
				break;

			case R.id.copy:
				startCopyActionMode();
				break;

			case R.id.comment_in_out:
				startCommentOutActionMode();
				break;

			case R.id.delete:
				startDeleteActionMode();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}

		return true;
	}

	public void startCopyActionMode() {
		startActionMode(copyModeCallBack);
	}

	public void startCommentOutActionMode() {
		startActionMode(commentOutModeCallBack);
	}

	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack);
	}

	public void startBackPackActionMode() {
		if (BackPackListManager.getInstance().getBackPackedScripts().isEmpty()) {
			startActionMode(backPackModeCallBack);
		} else if (adapter.isEmpty()) {
			switchToBackpack();
		} else {
			showBackpackModeChooser();
		}
	}

	private void startActionMode(ActionMode.Callback actionModeCallback) {
		if (adapter.isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
		} else {
			actionMode = getActivity().startActionMode(actionModeCallback);

			for (int i = adapter.listItemCount; i < adapter.getBrickList().size(); i++) {
				adapter.getView(i, null, getListView());
			}

			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());

			if (actionModeCallback.equals(copyModeCallBack)) {
				adapter.setActionMode(BrickAdapter.ActionModeEnum.COPY_DELETE);
			} else if (actionModeCallback.equals(deleteModeCallBack)) {
				adapter.setActionMode(BrickAdapter.ActionModeEnum.COPY_DELETE);
			} else if (actionModeCallback.equals(commentOutModeCallBack)) {
				adapter.setActionMode(BrickAdapter.ActionModeEnum.COMMENT_OUT);
				adapter.checkCommentedOutItems();
			} else if (actionModeCallback.equals(backPackModeCallBack)) {
				adapter.setActionMode(BrickAdapter.ActionModeEnum.BACKPACK);
			}

			adapter.setCheckboxVisibility();
			updateActionModeTitle();
		}
	}

	protected void showBackpackModeChooser() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		CharSequence[] items = new CharSequence[] {getString(R.string.pack), getString(R.string.unpack)};
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						startActionMode(backPackModeCallBack);
						break;
					case 1:
						switchToBackpack();
				}
			}
		});
		builder.setTitle(R.string.backpack_title);
		builder.setCancelable(true);
		builder.show();
	}

	private void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCRIPTS);
		startActivity(intent);
	}

	public void handleAddButton() {
		if (listView.isCurrentlyDragging()) {
			listView.animateHoveringBrick();
		} else {
			showCategoryFragment();
		}
	}

	public boolean isInUserBrickOverview() {
		return AddBrickFragment.addButtonHandler != null && BottomBar.isBottomBarVisible(getActivity());
	}

	public boolean getActionModeActive() {
		return actionModeActive;
	}

	public void setActionModeActive(boolean actionModeActive) {
		this.actionModeActive = actionModeActive;
	}

	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onFormulaChanged(FormulaBrick formulaBrick, Brick.BrickField brickField, Formula newFormula) {
		ChangeFormulaCommand changeFormulaCommand = CommandFactory.makeChangeFormulaCommand(formulaBrick, brickField,
				newFormula);
		changeFormulaCommand.execute();
		adapter.notifyDataSetChanged();
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		View selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode,
				menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				adapter.checkAllItems();
			}
		});
	}

	private void copyBrick(Brick brick) {
		if (brick instanceof NestingBrick
				&& (brick instanceof AllowedAfterDeadEndBrick || brick instanceof DeadEndBrick)) {
			return;
		}

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).getScriptSafe();
			try {
				Script clonedScript = scriptToEdit.clone();
				sprite.addScript(clonedScript);
				adapter.initBrickList();
				adapter.notifyDataSetChanged();
			} catch (CloneNotSupportedException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			return;
		}

		int brickId = adapter.getBrickList().indexOf(brick);
		if (brickId == -1) {
			return;
		}

		int newPosition = adapter.getCount();

		try {
			Brick copiedBrick = brick.clone();

			Script scriptList;
			if (adapter.getUserBrick() != null) {
				scriptList = ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript();
			} else {
				scriptList = ProjectManager.getInstance().getCurrentScript();
			}
			if (brick instanceof NestingBrick) {
				NestingBrick nestingBrickCopy = (NestingBrick) copiedBrick;
				nestingBrickCopy.initialize();

				for (NestingBrick nestingBrick : nestingBrickCopy.getAllNestingBrickParts(true)) {
					scriptList.addBrick((Brick) nestingBrick);
				}
			} else {
				scriptList.addBrick(copiedBrick);
			}

			adapter.addNewBrick(newPosition, copiedBrick, false);
			adapter.initBrickList();

			ProjectManager.getInstance().saveProject(getActivity().getApplicationContext());
			adapter.notifyDataSetChanged();
		} catch (CloneNotSupportedException exception) {
			Log.e(getTag(), "Copying a Brick failed", exception);
			ToastUtil.showError(getActivity(), R.string.error_copying_brick);
		}
	}

	private void deleteBrick(Brick brick) {

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).getScriptSafe();
			adapter.handleScriptDelete(sprite, scriptToEdit);
			return;
		}
		int brickId = adapter.getBrickList().indexOf(brick);
		if (brickId == -1) {
			return;
		}
		adapter.removeFromBrickListAndProject(brickId, true);
	}

	private void deleteCheckedBricks() {
		List<Brick> checkedBricks = adapter.getReversedCheckedBrickList();

		for (Brick brick : checkedBricks) {
			deleteBrick(brick);
		}
	}

	private void showConfirmDeleteDialog(boolean fromContextMenu) {
		this.deleteScriptFromContextMenu = fromContextMenu;
		int checkedItems;
		if (deleteScriptFromContextMenu) {
			checkedItems = 1;
		} else {
			checkedItems = adapter.getAmountOfCheckedItems();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getQuantityString(R.plurals.delete_bricks, checkedItems));
		builder.setMessage(R.string.dialog_confirm_delete);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (deleteScriptFromContextMenu) {
					adapter.handleScriptDelete(sprite, scriptToEdit);
				} else {
					deleteCheckedBricks();
					clearCheckedBricksAndEnableButtons();
				}
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
				if (!deleteScriptFromContextMenu) {
					clearCheckedBricksAndEnableButtons();
				}
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void clearCheckedBricksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.showBottomBar(getActivity());
		adapter.setActionMode(BrickAdapter.ActionModeEnum.NO_ACTION);
	}

	public void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		String completeTitle;
		switch ((Integer) actionMode.getTag()) {
			case ACTION_MODE_COPY:
				completeTitle = getResources().getQuantityString(R.plurals.number_of_bricks_to_copy,
						numberOfSelectedItems, numberOfSelectedItems);
				break;
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
			case ACTION_MODE_COMMENT_OUT:
				completeTitle = getString(R.string.comment_in_out);
				break;
			default:
				throw new IllegalArgumentException("Wrong or unhandled tag in ActionMode.");
		}

		int indexOfNumber = completeTitle.indexOf(' ') + 1;
		Spannable completeSpannedTitle = new SpannableString(completeTitle);
		if (!completeTitle.equals(getString(R.string.backpack)) && !completeTitle.equals(getString(R.string.comment_in_out))) {
			completeSpannedTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)),
					indexOfNumber, indexOfNumber + String.valueOf(numberOfSelectedItems).length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		actionMode.setTitle(completeSpannedTitle);
	}

	private void handleInsertFromBackpack() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int numberOfInsertedBricks = sharedPreferences.getInt(Constants.NUMBER_OF_BRICKS_INSERTED_FROM_BACKPACK, 0);
		if (numberOfInsertedBricks > 0) {
			adapter.animateUnpackingFromBackpack(numberOfInsertedBricks);
			sharedPreferences.edit().putInt(Constants.NUMBER_OF_BRICKS_INSERTED_FROM_BACKPACK, 0).commit();
		}
	}

	private void handlePlayButtonVisibility() {
		if (isInUserBrickOverview() || getActivity() instanceof UserBrickSpriteActivity) {
			BottomBar.hidePlayButton(getActivity());
		}
	}
}
