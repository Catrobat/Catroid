/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

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
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.BrickAdapter.OnBrickCheckedListener;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class ScriptFragment extends ScriptActivityFragment implements OnCategorySelectedListener,
		OnBrickCheckedListener {

	public static final String TAG = ScriptFragment.class.getSimpleName();

	private static final int ACTION_MODE_COPY = 0;
	private static final int ACTION_MODE_DELETE = 1;

	private static int selectedBrickPosition = Constants.NO_POSITION;

	private ActionMode actionMode;
	private View selectAllActionModeButton;

	private BrickAdapter adapter;
	private DragAndDropListView listView;

	private Sprite sprite;
	private Script scriptToEdit;

	private BrickListChangedReceiver brickListChangedReceiver;

	private Lock viewSwitchLock = new ViewSwitchLock();

	private boolean deleteScriptFromContextMenu = false;

	private AdapterView.OnItemClickListener defaultOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
			if (!viewSwitchLock.tryLock()) {
				return;
			}

			adapter.clearToAnimatePositions();

			final int itemPosition = position;//calculateItemPositionAndTouchPointY(view);
			final List<CharSequence> items = new ArrayList<CharSequence>();

			final Brick brick = (Brick) adapter.getItem(position);
			if (brick instanceof ScriptBrick) {
				int scriptIndex = adapter.getScriptIndexFromProject(itemPosition);
				ProjectManager.getInstance().setCurrentScript(sprite.getScript(scriptIndex));
			}

			if (!(brick instanceof DeadEndBrick)
					&& !(brick instanceof ScriptBrick)) {
				items.add(getText(R.string.brick_context_dialog_move_brick));
			}
			if ((brick instanceof UserBrick)) {
				items.add(getText(R.string.brick_context_dialog_show_source));
			}
			if (brick instanceof NestingBrick) {
				items.add(getText(R.string.brick_context_dialog_animate_bricks));
			}
			if (!(brick instanceof ScriptBrick)) {
				items.add(getText(R.string.brick_context_dialog_copy_brick));
				items.add(getText(R.string.brick_context_dialog_delete_brick));
			} else {
				items.add(getText(R.string.brick_context_dialog_delete_script));
			}
			if (brickHasAFormula(brick)) {
				items.add(getText(R.string.brick_context_dialog_formula_edit_brick));
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
			view.setDrawingCacheEnabled(true);
			view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
			view.buildDrawingCache(true);

			if (view.getDrawingCache() != null) {
				Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
				view.setDrawingCacheEnabled(drawingCacheEnabled);

				ImageView imageView = listView.getGlowingBorder(bitmap);
				builder.setCustomTitle(imageView);
			}

			builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					CharSequence clickedItemText = items.get(item);
					if (clickedItemText.equals(getText(R.string.brick_context_dialog_move_brick))) {
						view.performLongClick();
					} else if (clickedItemText.equals(getText(R.string.brick_context_dialog_show_source))) {
						launchAddBrickAndSelectBrickAt(getActivity(), itemPosition);
					} else if (clickedItemText.equals(getText(R.string.brick_context_dialog_copy_brick))) {
						copyBrickListAndProject(itemPosition, brick);
					} else if (clickedItemText.equals(getResources().getText(R.string.brick_context_dialog_delete_brick))
							|| clickedItemText.equals(getText(R.string.brick_context_dialog_delete_script))) {
						showConfirmDeleteDialog(itemPosition, brick);
					} else if (clickedItemText.equals(getText(R.string.brick_context_dialog_animate_bricks))) {
						//TODO: Illya Boyko: Is not this the same brick?
//						int itemPosition = calculateItemPositionAndTouchPointY(view);
//						Brick brick = brickList.get(itemPosition);
						if (brick instanceof NestingBrick) {
							adapter.animateBricks(((NestingBrick) brick).getAllNestingBrickParts(true));
						}
					} else if (clickedItemText.equals(getText(R.string.brick_context_dialog_formula_edit_brick))) {
						clickedEditFormula(brick, view);
					}

				}
			});
			builder.show();
		}

		public boolean brickHasAFormula(Brick brick) {
			boolean multiFormulaValid = false;
			if (brick instanceof UserBrick) {
				multiFormulaValid = ((UserBrick) brick).getFormulas().size() > 0;
			}
			return (brick instanceof FormulaBrick || multiFormulaValid);
		}

		protected void copyBrickListAndProject(int itemPosition, Brick origin) {
			Brick copy;
			try {
				copy = origin.clone();
				adapter.addNewBrick(itemPosition, copy, true);
				adapter.notifyDataSetChanged();
			} catch (CloneNotSupportedException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		/*TODO: IllyaBoyko: This is almost the same method as in fragment.*/
		private void showConfirmDeleteDialog(final int itemPosition, final Brick brick) {
			int titleId;

			if (brick instanceof ScriptBrick) {
				titleId = R.string.dialog_confirm_delete_script_title;
			} else {
				titleId = R.string.dialog_confirm_delete_brick_title;
			}

			AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
			builder.setTitle(titleId);
			builder.setMessage(R.string.dialog_confirm_delete_brick_message);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					if (brick instanceof ScriptBrick) {
						Script scriptToDelete = ((ScriptBrick) brick).getScriptSafe();
						handleScriptDelete(sprite, scriptToDelete);
					} else {
						adapter.removeFromBrickListAndProject(itemPosition, false);
					}
				}
			});
			builder.setNegativeButton(R.string.no, null);

			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}

		private void clickedEditFormula(Brick brick, View view) {
			Formula formula = null;
			if (brick instanceof FormulaBrick) {
				formula = ((FormulaBrick) brick).getFormula();
			}
			if (brick instanceof UserBrick) {
				List<Formula> formulas = ((UserBrick) brick).getFormulas();
				if (formulas.size() > 0) {
					formula = formulas.get(0);
				}
			}

			if (formula != null) {
				FormulaEditorFragment.showFragment(view, brick, formula);
			}
		}

		public void launchAddBrickAndSelectBrickAt(Context context, int index) {
			int[] temp = adapter.getScriptAndBrickIndexFromProject(index);
			Script script = ProjectManager.getInstance().getCurrentSprite().getScript(temp[0]);
			if (script != null) {
				Brick brick = script.getBrick(temp[1]);

				if (!viewSwitchLock.tryLock()) {
					return;
				}

				if (brick instanceof UserBrick) {
					AddBrickFragment.setBrickFocus(((UserBrick) brick));
				}

				ScriptFragment.this.onCategorySelected(context.getString(R.string.category_user_bricks));
			}
		}

	};
	private AdapterView.OnItemLongClickListener defaultOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if (adapter.getItem(position) instanceof DeadEndBrick) {
				return true;
			}

			return listView.performDragging(view, position);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_script, container, false);

		listView = (DragAndDropListView) rootView.findViewById(android.R.id.list);
		listView.setOnItemClickListener(defaultOnItemClickListener);
		listView.setOnItemLongClickListener(defaultOnItemLongClickListener);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
		menu.findItem(R.id.backpack).setVisible(false);
		menu.findItem(R.id.unpacking).setVisible(false);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.delete).setVisible(true);
		menu.findItem(R.id.copy).setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onStart() {
		super.onStart();
		BottomBar.showBottomBar(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (brickListChangedReceiver == null) {
			brickListChangedReceiver = new BrickListChangedReceiver();
		}

		IntentFilter filterBrickListChanged = new IntentFilter(ScriptActivity.ACTION_BRICK_LIST_CHANGED);
		getActivity().registerReceiver(brickListChangedReceiver, filterBrickListChanged);

		BottomBar.showBottomBar(getActivity());
		BottomBar.showPlayButton(getActivity());
		BottomBar.showAddButton(getActivity());
		initListeners();
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (brickListChangedReceiver != null) {
			getActivity().unregisterReceiver(brickListChangedReceiver);
		}
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
			projectManager.getCurrentProject().removeUnusedBroadcastMessages(); // TODO: Find better place
		}
	}

	public BrickAdapter getAdapter() {
		return adapter;
	}

	@Override
	public DragAndDropListView getListView() {
		return listView;
	}

	@Override
	public void onCategorySelected(String category) {
		AddBrickFragment addBrickFragment = AddBrickFragment.newInstance(category, this);
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.script_fragment_container, addBrickFragment,
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

		//TODO: allow recursive userbricks if its possible
		if (adapter.getUserBrick() != null && brickToBeAdded instanceof UserBrick) {// && ((UserBrick) brickToBeAdded).getDefinitionBrick().equals(ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick())) {
			Toast toast = null;
			if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
				toast = Toast.makeText(getActivity().getApplicationContext(), R.string.recursive_user_brick_forbidden, Toast.LENGTH_LONG);
			} else {
				toast.setText(R.string.recursive_user_brick_forbidden);
			}
			toast.show();
		} else {
			adapter.addNewBrick(position, brickToBeAdded, true);
			adapter.notifyDataSetChanged();
		}
	}

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}

		getSherlockActivity().findViewById(R.id.button_add).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				handleAddButton();
			}
		});

		adapter = new BrickAdapter(getActivity(), sprite, listView);
		adapter.setOnBrickCheckedListener(this);
		ScriptActivity activity = (ScriptActivity) getActivity();
		activity.setupBrickAdapter(adapter);

		if (ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts() > 0) {
			ProjectManager.getInstance().setCurrentScript(((ScriptBrick) adapter.getItem(0)).getScriptSafe());
		}

		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
	}

	private void showCategoryFragment() {
		BrickCategoryFragment brickCategoryFragment = new BrickCategoryFragment();
		brickCategoryFragment.setBrickAdapter(adapter);
		brickCategoryFragment.setOnCategorySelectedListener(this);
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.add(R.id.script_fragment_container, brickCategoryFragment,
				BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);

		fragmentTransaction.addToBackStack(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
		fragmentTransaction.commit();

		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean getShowDetails() {
		//Currently no showDetails option
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		//Currently no showDetails option
	}

	@Override
	protected void showRenameDialog() {
		//Rename not supported
	}

	@Override
	public void startRenameActionMode() {
		//Rename not supported
	}

	@Override
	public void startCopyActionMode() {
		startActionMode(copyModeCallBack);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback) {
		actionMode = getSherlockActivity().startActionMode(actionModeCallback);

		unregisterForContextMenu(listView);
		BottomBar.hideBottomBar(getActivity());
		updateActionModeTitle();
	}

	@Override
	public void handleAddButton() {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		// addButtonHandler != null when the user brick category is open in the AddBrickFragment
		if (AddBrickFragment.addButtonHandler != null) {
			AddBrickFragment.addButtonHandler.handleAddButton();
			return;
		}

		if (listView.isCurrentlyDragging()) {
			listView.animateHoveringBrick();
			return;
		}

		showCategoryFragment();
	}

	@Override
	public boolean getActionModeActive() {
		return actionModeActive;
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		boolean isActionMode = selectMode != ListView.CHOICE_MODE_NONE;

		listView.setItemsCanFocus(!isActionMode);
		listView.setChoiceMode(selectMode);
		//!!! HACK
		//!!! After setChoiceMode executed, we can set OnItemClickListener to list view.
		//Listener is needed only when selection is enable.
		// TODO: When the Application minSdk will be >= 11. Then it can be replaces with multi_choice_modal mode and
		// MultiChoiceActionMode!
		if (isActionMode) {
			//No Dragging in selection mode.
			listView.setOnItemLongClickListener(null);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (adapter.isActionMode()) {
						// In action mode
						adapter.handleCheck(position, listView.isItemChecked(position));
					}
				}
			});
		} else {
			listView.setOnItemClickListener(defaultOnItemClickListener);
			listView.setOnItemLongClickListener(defaultOnItemLongClickListener);
			listView.clearChoices();
		}
		setActionModeActive(isActionMode);
		adapter.setSelectMode(selectMode);
		adapter.setActionMode(isActionMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void showDeleteDialog() {

		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(selectedBrickPosition);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}

	/**
	 * Called when back is pressed and action mode is active.
	 */
	public void beforeCancelActionMode() {
		if(listView.getChoiceMode() != ListView.CHOICE_MODE_NONE) {
			listView.clearChoices();
		}
	}

	private class BrickListChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_BRICK_LIST_CHANGED)) {
				adapter.updateProjectBrickList();
			}
		}
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getLayoutInflater(null), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				adapter.checkAllItems();
			}
		});
	}

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			mode.setTag(ACTION_MODE_DELETE);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			if (listView.getCheckedItemCount() == 0) {
				clearCheckedBricksAndEnableButtons();
			} else {
				showConfirmDeleteDialog(false);
			}
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

			mode.setTag(ACTION_MODE_COPY);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
			for (int i = 0; i < checkedItemPositions.size(); i++) {
				if (checkedItemPositions.valueAt(i)) {
					Brick brick = (Brick) listView.getAdapter().getItem(checkedItemPositions.keyAt(i));
					copyBrick(brick);
					if (brick instanceof ScriptBrick) {
						break;
					}
				}
			}

			clearCheckedBricksAndEnableButtons();
		}
	};

	private void copyBrick(Brick brick) {
		if (brick instanceof NestingBrick
				&& (brick instanceof AllowedAfterDeadEndBrick || brick instanceof DeadEndBrick)) {
			return;
		}

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).getScriptSafe();

			Script clonedScript = scriptToEdit.copyScriptForSprite(sprite, sprite.getUserBrickList());

			sprite.addScript(clonedScript);
			adapter.initBrickList();
			adapter.notifyDataSetChanged();

			return;
		}

		int brickId = adapter.getBrickList().indexOf(brick);
		if (brickId == -1) {
			return;
		}

		int newPosition = adapter.getCount();

		try {
			Brick copiedBrick = brick.clone();

			Script scriptList = null;
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

			ProjectManager.getInstance().saveProject();
			adapter.notifyDataSetChanged();
		} catch (CloneNotSupportedException exception) {
			Log.e(getTag(), "Copying a Brick failed", exception);
			Toast.makeText(getActivity(), R.string.error_copying_brick, Toast.LENGTH_SHORT).show();
		}
	}

	private void deleteBrick(Brick brick) {

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).getScriptSafe();
			handleScriptDelete(sprite, scriptToEdit);
			return;
		}
		int brickId = adapter.getBrickList().indexOf(brick);
		if (brickId == -1) {
			return;
		}
		adapter.removeFromBrickListAndProject(brickId, true);
	}

	public void handleScriptDelete(Sprite spriteToEdit, Script scriptToDelete) {
		spriteToEdit.removeScript(scriptToDelete);
		if (spriteToEdit.getNumberOfScripts() == 0) {
			ProjectManager.getInstance().setCurrentScript(null);
		} else {
			int lastScriptIndex = spriteToEdit.getNumberOfScripts() - 1;
			Script lastScript = spriteToEdit.getScript(lastScriptIndex);
			ProjectManager.getInstance().setCurrentScript(lastScript);
		}
		adapter.updateProjectBrickList();
	}

	private void deleteCheckedBricks() {
		if (listView.getCheckedItemCount() > 0) {
			SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
			for (int i = checkedItemPositions.size() - 1; i >= 0; i--) {
				if (checkedItemPositions.valueAt(i)) {
					deleteBrick((Brick) listView.getAdapter().getItem(checkedItemPositions.keyAt(i)));
				}
			}
		}
	}

	private void showConfirmDeleteDialog(boolean fromContextMenu) {
		this.deleteScriptFromContextMenu = fromContextMenu;
		int titleId;
		if ((deleteScriptFromContextMenu && scriptToEdit.getBrickList().size() == 0)
				|| listView.getCheckedItemCount() == 1) {
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
				if (deleteScriptFromContextMenu) {
					handleScriptDelete(sprite, scriptToEdit);
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

	private void clearCheckedBricksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);

		registerForContextMenu(listView);
		BottomBar.showBottomBar(getActivity());
	}

	@Override
	public void onBrickChecked() {
		updateActionModeTitle();
		Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && listView.getCheckedItemCount() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = listView.getCheckedItemCount();

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
			default:
				throw new IllegalArgumentException("Wrong or unhandled tag in ActionMode.");
		}

		int indexOfNumber = completeTitle.indexOf(' ') + 1;
		Spannable completeSpannedTitle = new SpannableString(completeTitle);
		completeSpannedTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)),
				indexOfNumber, indexOfNumber + String.valueOf(numberOfSelectedItems).length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		actionMode.setTitle(completeSpannedTitle);
	}

	@Override
	public void startBackPackActionMode() {

	}

}
