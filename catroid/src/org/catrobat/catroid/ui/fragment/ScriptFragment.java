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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.BrickAdapter.OnBrickEditListener;
import org.catrobat.catroid.ui.dialogs.AddBrickDialog;
import org.catrobat.catroid.ui.dialogs.BrickCategoryDialog;
import org.catrobat.catroid.ui.dialogs.BrickCategoryDialog.OnBrickCategoryDialogDismissCancelListener;
import org.catrobat.catroid.ui.dialogs.BrickCategoryDialog.OnCategorySelectedListener;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.view.ViewGroup;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

public class ScriptFragment extends ScriptActivityFragment implements OnCategorySelectedListener,
		OnBrickCategoryDialogDismissCancelListener, OnBrickEditListener {

	private static final String ARGUMENTS_SELECTED_CATEGORY = "selected_category";
	private static final String TAG = ScriptFragment.class.getSimpleName();

	private static String actionModeTitle;

	//these strings are just for the title (brick vs bricks..)
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
	private boolean createNewBrick;
	private boolean addScript;
	private boolean isCancelled;

	private NewBrickAddedReceiver brickAddedReceiver;
	private BrickListChangedReceiver brickListChangedReceiver;

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

		createNewBrick = true;
		addScript = false;
		isCancelled = false;

		if (savedInstanceState != null) {
			selectedCategory = savedInstanceState.getString(ARGUMENTS_SELECTED_CATEGORY);
		}

		initListeners();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
		menu.findItem(R.id.copy).setVisible(false);
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

		sprite = ProjectManager.INSTANCE.getCurrentSprite();
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

		ProjectManager projectManager = ProjectManager.INSTANCE;
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}

		if (brickAddedReceiver != null) {
			getActivity().unregisterReceiver(brickAddedReceiver);
		}

		if (brickListChangedReceiver != null) {
			getActivity().unregisterReceiver(brickListChangedReceiver);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.brick_list_view) {
			//selectedBrickData = adapter.getItem(selectedBrickPosition);
			menu.setHeaderTitle(R.string.script_context_menu_title);

			if (adapter.getItem(listView.getTouchedListPosition()) instanceof ScriptBrick) {
				scriptToEdit = ((ScriptBrick) adapter.getItem(listView.getTouchedListPosition()))
						.initScript(ProjectManager.INSTANCE.getCurrentSprite());
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.menu_script, menu);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
			case R.id.script_menu_delete: {
				adapter.handleScriptDelete(sprite, scriptToEdit);
				break;
			}
			case R.id.script_menu_copy: {
				//currently not supported
				break;
			}
		}

		return true;
	}

	public void setCreateNewBrick(boolean createNewBrick) {
		this.createNewBrick = createNewBrick;
	}

	public void setAddNewScript() {
		addNewScript = true;
	}

	public void setNewScript() {
		addScript = true;
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

		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		Fragment previousFragment = getFragmentManager().findFragmentByTag(AddBrickDialog.DIALOG_FRAGMENT_TAG);
		if (previousFragment != null) {
			fragmentTransaction.remove(previousFragment);
		}
		fragmentTransaction.addToBackStack(null);

		AddBrickDialog addBrickDialog = AddBrickDialog.newInstance(selectedCategory, this);
		addBrickDialog.show(fragmentTransaction, AddBrickDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onBrickCategoryDialogDismiss() {
		if (createNewBrick && !isCancelled && addScript) {
			setAddNewScript();
			addScript = false;
		}
		createNewBrick = true;
	}

	@Override
	public void onBrickCategoryDialogCancel() {
		isCancelled = true;
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
		sprite = ProjectManager.INSTANCE.getCurrentSprite();
		if (sprite == null) {
			return;
		}

		adapter = new BrickAdapter(getActivity(), sprite, listView);
		adapter.setOnBrickEditListener(this);

		if (ProjectManager.INSTANCE.getCurrentSprite().getNumberOfScripts() > 0) {
			ProjectManager.INSTANCE.setCurrentScript(((ScriptBrick) adapter.getItem(0)).initScript(sprite));
		}

		listView.setOnCreateContextMenuListener(this);
		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		addNewScript = false;
	}

	private void showCategoryDialog() {
		BrickCategoryDialog brickCategoryDialog = new BrickCategoryDialog();
		brickCategoryDialog.setOnCategorySelectedListener(ScriptFragment.this);
		brickCategoryDialog.setOnBrickCategoryDialogDismissCancelListener(ScriptFragment.this);
		brickCategoryDialog.show(getFragmentManager(), BrickCategoryDialog.DIALOG_FRAGMENT_TAG);

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
		// TODO Auto-generated method stub
	}

	@Override
	public void handleAddButton() {
		if (listView.isCurrentlyDragging()) {
			listView.animateHoveringBrick();
			return;
		}
		showCategoryDialog();
	}

	@Override
	public boolean getActionModeActive() {
		return actionModeActive;
	}

	@Override
	public int getSelectMode() {
		// TODO Auto-generated method stub
		return adapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		// TODO Auto-generated method stub
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void startDeleteActionMode() {
		// TODO Auto-generated method stub
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);

			for (int i = adapter.listItemCount; i < adapter.brickList.size(); i++) {
				adapter.getView(i, null, getListView());
			}

			unregisterForContextMenu(listView);
			BottomBar.disableButtons(getActivity());
			//isRenameActionMode = false;
			adapter.setCheckboxVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void showDeleteDialog() {
		// TODO Auto-generated method stub
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
			setSelectMode(Constants.MULTI_SELECT);
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
			//List<Brick> checkedBricks = adapter.getCheckedBrickList();
			List<Brick> checkedBricks = adapter.getReversedCheckedBrickList();

			for (Brick brick : checkedBricks) {
				deleteBrick(brick);
				//if (brick instanceof ScriptBrick) {	
				//checkedBricks = removeCheckedBricksFromScript(brick, checkedBricks);
				//}
			}
			setSelectMode(Constants.SELECT_NONE);
			adapter.clearCheckedItems();

			actionMode = null;
			setActionModeActive(false);

			registerForContextMenu(listView);
			BottomBar.enableButtons(getActivity());
		}
	};

	private void deleteBrick(Brick brick) {

		if (brick instanceof ScriptBrick) {
			//scriptToEdit = ((ScriptBrick) adapter.getItem(listView.getTouchedListPosition()))
			//		.initScript(ProjectManager.INSTANCE.getCurrentSprite());
			scriptToEdit = ((ScriptBrick) brick).initScript(ProjectManager.INSTANCE.getCurrentSprite());
			adapter.handleScriptDelete(sprite, scriptToEdit);
			return;
		}
		int brickId = adapter.brickList.indexOf(brick);
		//int brickPosition = listView.getPositionForView(brick.getView(getActivity(), brickId, adapter));
		if (brickId == -1) {
			return;
		}
		adapter.removeFromBrickListAndProject(brickId, true);
	}

	public List<Brick> removeCheckedBricksFromScript(Brick scriptBrick, List<Brick> checkedBricks) {
		//List<Brick> checkedBricks = adapter.getCheckedBricks();
		int index = checkedBricks.indexOf(scriptBrick) + 1;
		while (index < checkedBricks.size() && !(checkedBricks.get(index) instanceof ScriptBrick)) {
			checkedBricks.remove(index);
		}
		return checkedBricks;
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
