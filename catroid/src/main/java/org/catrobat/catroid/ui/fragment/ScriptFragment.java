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

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
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
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewScriptGroupDialog;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class ScriptFragment extends ListFragment implements OnCategorySelectedListener, OnFormulaChangedListener,
		NewScriptGroupDialog.BackpackScriptInterface {

	public static final String TAG = ScriptFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, BACKPACK, COPY, DELETE, ENABLE_DISABLE})
	@interface ActionModeType {}
	private static final int NONE = 0;
	private static final int BACKPACK = 1;
	private static final int COPY = 2;
	private static final int DELETE = 3;
	private static final int ENABLE_DISABLE = 4;

	@ActionModeType
	private int actionModeType = NONE;

	private ActionMode actionMode;

	private BrickAdapter adapter;
	private BrickListView listView;

	private Sprite sprite;
	private Script scriptToEdit;

	private ScriptController scriptController = new ScriptController();

	private ActionMode.Callback callback = new ActionMode.Callback() {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);

			switch (actionModeType) {
				case BACKPACK:
					adapter.setActionMode(BrickAdapter.ActionModeEnum.BACKPACK);
					break;
				case COPY:
				case DELETE:
					adapter.setActionMode(BrickAdapter.ActionModeEnum.COPY_DELETE);
					break;
				case ENABLE_DISABLE:
					adapter.setActionMode(BrickAdapter.ActionModeEnum.COMMENT_OUT);
					adapter.checkCommentedOutItems();
					break;
				case NONE:
					actionMode.finish();
					return false;
			}

			for (int i = adapter.listItemCount; i < adapter.getBrickList().size(); i++) {
				adapter.getView(i, null, getListView());
			}

			adapter.setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			adapter.setCheckboxVisibility();
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			if (actionModeType == ENABLE_DISABLE) {
				menu.findItem(R.id.confirm).setVisible(false);
				return true;
			}
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
				case R.id.confirm:
					handleContextualAction();
					break;
				default:
					return false;
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			resetActionModeParameters();
		}
	};

	private void handleContextualAction() {
		if (adapter.getAmountOfCheckedItems() == 0) {
			actionMode.finish();
		}

		switch (actionModeType) {
			case BACKPACK:
				showNewScriptGroupDialog();
				break;
			case COPY:
				copyBricks();
				break;
			case DELETE:
				showDeleteAlert(false);
				break;
			case ENABLE_DISABLE:
				actionMode.finish();
				break;
			case NONE:
				actionMode.finish();
				break;
		}
	}

	private void resetActionModeParameters() {
		adapter.setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();
		actionModeType = NONE;

		registerForContextMenu(listView);
		BottomBar.showBottomBar(getActivity());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_script, null);
		listView = view.findViewById(android.R.id.list);
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scripts);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initListeners();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		currentProject.getBroadcastMessageContainer().update();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

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
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (projectManager.getCurrentScene() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
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
			case R.id.backpack:
				prepareActionMode(BACKPACK);
				break;
			case R.id.copy:
				prepareActionMode(COPY);
				break;
			case R.id.comment_in_out:
				prepareActionMode(ENABLE_DISABLE);
				break;
			case R.id.delete:
				prepareActionMode(DELETE);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	protected void prepareActionMode(@ActionModeType int type) {
		if (type == BACKPACK) {
			if (BackPackListManager.getInstance().getBackPackedScriptGroups().isEmpty()) {
				startActionMode(BACKPACK);
			} else if (adapter.isEmpty()) {
				switchToBackpack();
			} else {
				showBackpackModeChooser();
			}
		} else {
			startActionMode(type);
		}
	}

	private void startActionMode(@ActionModeType int type) {
		if (adapter.isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
		} else {
			actionModeType = type;
			actionMode = getActivity().startActionMode(callback);
			updateActionModeTitle();
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	public void finishActionMode() {
		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	protected void showBackpackModeChooser() {
		CharSequence[] items = new CharSequence[] {getString(R.string.pack), getString(R.string.unpack)};
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.backpack_title)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								startActionMode(BACKPACK);
								break;
							case 1:
								switchToBackpack();
						}
					}
				})
				.show();
	}

	public void handleAddButton() {
		if (listView.isCurrentlyDragging()) {
			listView.animateHoveringBrick();
		} else {
			showCategoryFragment();
		}
	}

	public void showNewScriptGroupDialog() {
		NewScriptGroupDialog dialog = new NewScriptGroupDialog(this);
		dialog.show(getFragmentManager(), NewScriptGroupDialog.TAG);
	}

	@Override
	public void packItems(String name) {
		try {
			scriptController.pack(name, adapter.getCheckedBricks());
			finishActionMode();
			ToastUtil.showSuccess(getActivity(), getString(R.string.packed_script_group));
			switchToBackpack();
		} catch (IOException | CloneNotSupportedException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			finishActionMode();
		}
	}

	private void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCRIPTS);
		startActivity(intent);
	}

	@Override
	public void cancelPacking() {
		finishActionMode();
	}

	@Override
	public void onFormulaChanged(FormulaBrick formulaBrick, Brick.BrickField brickField, Formula newFormula) {
		ChangeFormulaCommand changeFormulaCommand = CommandFactory.makeChangeFormulaCommand(formulaBrick, brickField,
				newFormula);
		changeFormulaCommand.execute();
		adapter.notifyDataSetChanged();
	}

	private void copyBricks() {
		List<Brick> checkedBricks = adapter.getCheckedBricks();

		for (Brick brick : checkedBricks) {
			copyBrick(brick);
			if (brick instanceof ScriptBrick) {
				break;
			}
		}

		actionMode.finish();
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

	private void showDeleteAlert(final boolean fromContextMenu) {
		int checkedItems = fromContextMenu ? 1 : adapter.getAmountOfCheckedItems();
		new AlertDialog.Builder(getActivity())
				.setTitle(getResources().getQuantityString(R.plurals.delete_bricks, checkedItems))
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						if (fromContextMenu) {
							adapter.handleScriptDelete(sprite, scriptToEdit);
						} else {
							deleteCheckedBricks();
							finishActionMode();
						}
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setCancelable(false)
				.create()
				.show();
	}

	public void updateActionModeTitle() {
		int selectedItemCnt = adapter.getAmountOfCheckedItems();

		switch (actionModeType) {
			case BACKPACK:
				actionMode.setTitle(getResources().getQuantityString(R.plurals.number_of_bricks_to_backpack,
						selectedItemCnt, selectedItemCnt));
				break;
			case COPY:
				actionMode.setTitle(getResources().getQuantityString(R.plurals.number_of_bricks_to_copy,
						selectedItemCnt, selectedItemCnt));
				break;
			case DELETE:
				actionMode.setTitle(getResources().getQuantityString(R.plurals.number_of_bricks_to_delete,
						selectedItemCnt, selectedItemCnt));
				break;
			case ENABLE_DISABLE:
				actionMode.setTitle(getString(R.string.comment_in_out));
				break;
			case NONE:
		}
	}
}
