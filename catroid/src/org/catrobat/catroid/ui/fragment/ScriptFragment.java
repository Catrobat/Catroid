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

import java.util.List;
import java.util.concurrent.locks.Lock;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.BrickAdapter.OnBrickEditListener;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import org.catrobat.catroid.utils.Utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

public class ScriptFragment extends ScriptActivityFragment implements OnCategorySelectedListener, OnBrickEditListener {

	private static final String ARGUMENTS_SELECTED_CATEGORY = "selected_category";
	public static final String TAG = ScriptFragment.class.getSimpleName();

	private static String actionModeTitle;

	private static String singleItemAppendixActionMode;
	private static String multipleItemAppendixActionMode;

	private static int selectedBrickPosition = Constants.NO_POSITION;

	private ActionMode actionMode;

	private BrickAdapter adapter;
	private DragAndDropListView listView;

	private Sprite sprite;
	private Script scriptToEdit;
	private String selectedCategory;

	private boolean addNewScript;

	private NewBrickAddedReceiver brickAddedReceiver;
	private BrickListChangedReceiver brickListChangedReceiver;

	private Lock viewSwitchLock = new ViewSwitchLock();

	private boolean deleteScriptFromContextMenu = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_script, null);

		listView = (DragAndDropListView) rootView.findViewById(R.id.brick_list_view);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			selectedCategory = savedInstanceState.getString(ARGUMENTS_SELECTED_CATEGORY);
		}

		initListeners();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
		menu.findItem(R.id.edit_in_pocket_paint).setVisible(false);
		menu.findItem(R.id.copy).setVisible(true);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(ARGUMENTS_SELECTED_CATEGORY, selectedCategory);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}

		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (brickAddedReceiver == null) {
			brickAddedReceiver = new NewBrickAddedReceiver();
		}

		if (brickListChangedReceiver == null) {
			brickListChangedReceiver = new BrickListChangedReceiver();
		}

		IntentFilter filterBrickAdded = new IntentFilter(ScriptActivity.ACTION_NEW_BRICK_ADDED);
		getActivity().registerReceiver(brickAddedReceiver, filterBrickAdded);

		IntentFilter filterBrickListChanged = new IntentFilter(ScriptActivity.ACTION_BRICK_LIST_CHANGED);
		getActivity().registerReceiver(brickListChangedReceiver, filterBrickListChanged);

		initListeners();
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (brickAddedReceiver != null) {
			getActivity().unregisterReceiver(brickAddedReceiver);
		}

		if (brickListChangedReceiver != null) {
			getActivity().unregisterReceiver(brickListChangedReceiver);
		}
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
			projectManager.getCurrentProject().removeUnusedBroadcastMessages(); // TODO: Find better place
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

		if (view.getId() == R.id.brick_list_view) {
			menu.setHeaderTitle(R.string.script_context_menu_title);

			if (adapter.getItem(listView.getTouchedListPosition()) instanceof ScriptBrick) {
				scriptToEdit = ((ScriptBrick) adapter.getItem(listView.getTouchedListPosition()))
						.initScript(ProjectManager.getInstance().getCurrentSprite());
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.menu_script, menu);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
			case R.id.script_menu_delete: {

				showConfirmDeleteDialog(true);
				break;
			}
			case R.id.script_menu_copy: {
				//currently not supported
				break;
			}
		}

		return true;
	}

	public void setAddNewScript() {
		addNewScript = true;
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
		selectedCategory = category;
		AddBrickFragment addBrickFragment = AddBrickFragment.newInstance(selectedCategory, this);
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.script_fragment_container, addBrickFragment,
				AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		adapter.notifyDataSetChanged();

	}

	public void updateAdapterAfterAddNewBrick(Brick brickToBeAdded) {
		if (addNewScript) {
			addNewScript = false;
		} else {
			int firstVisibleBrick = listView.getFirstVisiblePosition();
			int lastVisibleBrick = listView.getLastVisiblePosition();
			int position = ((1 + lastVisibleBrick - firstVisibleBrick) / 2);
			position += firstVisibleBrick;
			adapter.addNewBrick(position, brickToBeAdded);
		}
		adapter.notifyDataSetChanged();
	}

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}

		getSherlockActivity().findViewById(R.id.button_add).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleAddButton();
			}
		});

		adapter = new BrickAdapter(getActivity(), sprite, listView);
		adapter.setOnBrickEditListener(this);

		if (ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts() > 0) {
			ProjectManager.getInstance().setCurrentScript(((ScriptBrick) adapter.getItem(0)).initScript(sprite));
		}

		listView.setOnCreateContextMenuListener(this);
		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		addNewScript = false;
	}

	private void showCategoryFragment() {
		BrickCategoryFragment brickCategoryFragment = new BrickCategoryFragment();
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

		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(copyModeCallBack);

			for (int i = adapter.listItemCount; i < adapter.getBrickList().size(); i++) {
				adapter.getView(i, null, getListView());
			}

			unregisterForContextMenu(listView);
			BottomBar.disableButtons(getActivity());
			adapter.setCheckboxVisibility(View.VISIBLE);
			adapter.setActionMode(true);
		}

	}

	@Override
	public void handleAddButton() {
		if (!viewSwitchLock.tryLock()) {
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
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void startDeleteActionMode() {

		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);

			for (int i = adapter.listItemCount; i < adapter.getBrickList().size(); i++) {
				adapter.getView(i, null, getListView());
			}

			unregisterForContextMenu(listView);
			BottomBar.disableButtons(getActivity());
			adapter.setCheckboxVisibility(View.VISIBLE);
			adapter.setActionMode(true);
		}
	}

	@Override
	public void startEditInPocketPaintActionMode() {
	}

	@Override
	protected void showDeleteDialog() {

		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(selectedBrickPosition);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}

	private class NewBrickAddedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_NEW_BRICK_ADDED)) {
				Brick brickToBeAdded = null;
				Object tempObject = intent.getExtras().get("added_brick");
				if (tempObject instanceof Brick) {
					brickToBeAdded = (Brick) tempObject;
				}

				if (brickToBeAdded == null) {
					Log.w(TAG, "NewBrickAddedReceiver: no Brick given in extras");
					return;
				}
				updateAdapterAfterAddNewBrick(brickToBeAdded);
			}
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

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.delete);
			singleItemAppendixActionMode = getString(R.string.brick_single);
			multipleItemAppendixActionMode = getString(R.string.brick_multiple);

			mode.setTitle(actionModeTitle);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			if (adapter.getAmountOfCheckedItems() == 0) {
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
			setActionModeActive(true);

			actionModeTitle = getString(R.string.copy);
			singleItemAppendixActionMode = getString(R.string.brick_single);
			multipleItemAppendixActionMode = getString(R.string.brick_multiple);

			mode.setTitle(actionModeTitle);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
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
			setSelectMode(ListView.CHOICE_MODE_NONE);
			adapter.clearCheckedItems();

			actionMode = null;
			setActionModeActive(false);

			registerForContextMenu(listView);
			BottomBar.enableButtons(getActivity());
			adapter.setActionMode(false);

		}
	};

	private void copyBrick(Brick brick) {

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).initScript(ProjectManager.getInstance().getCurrentSprite());

			Script clonedScript = scriptToEdit.copyScriptForSprite(sprite);

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

		Brick copy = brick.clone();

		Script scriptList;
		scriptList = ProjectManager.getInstance().getCurrentScript();
		scriptList.addBrick(copy);
		adapter.addNewMultipleBricks(newPosition, copy);

		ProjectManager.getInstance().saveProject();
		adapter.notifyDataSetChanged();
	}

	private void deleteBrick(Brick brick) {

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).initScript(ProjectManager.getInstance().getCurrentSprite());
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
		String yes = getActivity().getString(R.string.yes);
		String no = getActivity().getString(R.string.no);
		String title = "";
		if ((deleteScriptFromContextMenu && scriptToEdit.getBrickList().size() == 0)
				|| adapter.getAmountOfCheckedItems() == 1) {
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
				if (deleteScriptFromContextMenu) {
					adapter.handleScriptDelete(sprite, scriptToEdit);
				} else {
					deleteCheckedBricks();
					clearCheckedBricksAndEnableButtons();
				}
			}
		});
		builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
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
		adapter.clearCheckedItems();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.enableButtons(getActivity());
		adapter.setActionMode(false);
	}

	@Override
	public void onBrickEdit(View view) {

	}

	@Override
	public void onBrickChecked() {

		if (actionMode == null) {
			return;
		}

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
}
