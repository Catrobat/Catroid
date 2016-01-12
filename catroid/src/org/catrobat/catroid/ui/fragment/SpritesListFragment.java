/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.io.LoadProjectTask.OnLoadProjectCompleteListener;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.CapitalizedTextView;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.adapter.SpriteBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.LegoNXTSensorConfigInfoDialog;
import org.catrobat.catroid.ui.dialogs.RenameSpriteDialog;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class SpritesListFragment extends ScriptActivityFragment implements SpriteBaseAdapter.OnSpriteEditListener,
		OnLoadProjectCompleteListener {

	public static final String TAG = SpritesListFragment.class.getSimpleName();
	public static final String SHARED_PREFERENCE_NAME = "showDetailsProjects";
	private static final String BUNDLE_ARGUMENTS_SPRITE_TO_EDIT = "sprite_to_edit";

	private static String multiSelectActionModeTitle;
	private static String singleItemAppendixMultiSelectActionMode;
	private static String multipleItemAppendixMultiSelectActionMode;
	public boolean isLoading = false;
	private SpriteAdapter spriteAdapter;
	private ArrayList<Sprite> spriteList;
	private Sprite spriteToEdit;
	private int spritePosition;
	private SpriteRenamedReceiver spriteRenamedReceiver;
	private SpritesListChangedReceiver spritesListChangedReceiver;
	private SpritesListInitReceiver spritesListInitReceiver;

	private ActionMode actionMode;
	private View selectAllActionModeButton;
	private boolean isRenameActionMode;
	private boolean isBackPackActionMode;
	private String programName;
	private boolean selectAll = true;

	private LoadProjectTask loadProjectTask;
	private boolean fragmentStartedFirstTime = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		programName = getActivity().getIntent().getStringExtra(Constants.PROJECTNAME_TO_LOAD);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sprites_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());
		if (savedInstanceState != null) {
			spriteToEdit = (Sprite) savedInstanceState.get(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT);
		}

		try {
			Utils.loadProjectIfNeeded(getActivity());
		} catch (ClassCastException exception) {
			Log.e("CATROID", getActivity().toString() + " does not implement ErrorListenerInterface", exception);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT, spriteToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		initListeners();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (programName != null
				&& ((projectManager.getCurrentProject() != null && !projectManager.getCurrentProject().getName()
				.equals(programName)) || projectManager.getCurrentProject() == null)) {

			getActivity().findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.progress_circle).bringToFront();
			getActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
			getActivity().findViewById(R.id.bottom_bar).setVisibility(View.GONE);

			isLoading = true;

			loadProjectTask = new LoadProjectTask(getActivity(), programName, true, true);
			loadProjectTask.setOnLoadProjectCompleteListener(this);
			loadProjectTask.execute();
		} else if (projectManager.getCurrentProject() != null && projectManager.getCurrentProject().getName()
				.equals(programName) && fragmentStartedFirstTime) {
			showInfoFragmentIfNeeded();
		}

		fragmentStartedFirstTime = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		StorageHandler.getInstance().fillChecksumContainer();

		if (spriteRenamedReceiver == null) {
			spriteRenamedReceiver = new SpriteRenamedReceiver();
		}

		if (spritesListChangedReceiver == null) {
			spritesListChangedReceiver = new SpritesListChangedReceiver();
		}

		if (spritesListInitReceiver == null) {
			spritesListInitReceiver = new SpritesListInitReceiver();
		}

		IntentFilter intentFilterSpriteRenamed = new IntentFilter(ScriptActivity.ACTION_SPRITE_RENAMED);
		getActivity().registerReceiver(spriteRenamedReceiver, intentFilterSpriteRenamed);

		IntentFilter intentFilterSpriteListChanged = new IntentFilter(ScriptActivity.ACTION_SPRITES_LIST_CHANGED);
		getActivity().registerReceiver(spritesListChangedReceiver, intentFilterSpriteListChanged);

		IntentFilter intentFilterSpriteListInit = new IntentFilter(ScriptActivity.ACTION_SPRITES_LIST_INIT);
		getActivity().registerReceiver(spritesListInitReceiver, intentFilterSpriteListInit);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		getActivity().getIntent().removeExtra(Constants.PROJECTNAME_TO_LOAD);
		if (loadProjectTask != null) {
			loadProjectTask.cancel(true);
			ProjectManager.getInstance().cancelLoadProject();
		}

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
		}

		if (spriteRenamedReceiver != null) {
			getActivity().unregisterReceiver(spriteRenamedReceiver);
		}

		if (spritesListChangedReceiver != null) {
			getActivity().unregisterReceiver(spritesListChangedReceiver);
		}

		if (spritesListInitReceiver != null) {
			getActivity().unregisterReceiver(spritesListInitReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		spriteToEdit = spriteAdapter.getItem(info.position);
		spritePosition = info.position;
		spriteAdapter.addCheckedSprite(info.position);

		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(spriteToEdit) == 0) {
			return;
		}

		menu.setHeaderTitle(spriteToEdit.getName());

		getActivity().getMenuInflater().inflate(R.menu.context_menu_default, menu);
		menu.findItem(R.id.context_menu_copy).setVisible(true);
		menu.findItem(R.id.context_menu_unpacking).setVisible(false);
		menu.findItem(R.id.context_menu_backpack).setVisible(true);
		menu.findItem(R.id.context_menu_move_up).setVisible(true);
		menu.findItem(R.id.context_menu_move_down).setVisible(true);
		menu.findItem(R.id.context_menu_move_to_top).setVisible(true);
		menu.findItem(R.id.context_menu_move_to_bottom).setVisible(true);

		menu.findItem(R.id.context_menu_move_down).setEnabled(!(spritePosition == spriteList.size() - 1));
		menu.findItem(R.id.context_menu_move_to_bottom).setEnabled(!(spritePosition == spriteList.size() - 1));

		menu.findItem(R.id.context_menu_move_up).setEnabled(!(spritePosition == 1));
		menu.findItem(R.id.context_menu_move_to_top).setEnabled(!(spritePosition == 1));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_copy:
				copySprite();
				break;

			case R.id.context_menu_backpack:
				BackPackSpriteController.getInstance().backpack(spriteToEdit, false);
				switchToBackPack();
				break;

			case R.id.context_menu_cut:
				break;

			case R.id.context_menu_insert_below:
				break;

			case R.id.context_menu_move:
				break;

			case R.id.context_menu_rename:
				showRenameDialog();
				break;

			case R.id.context_menu_delete:
				showConfirmDeleteDialog();
				break;

			case R.id.context_menu_move_down:
				moveSpriteDown();
				break;

			case R.id.context_menu_move_up:
				moveSpriteUp();
				break;
			case R.id.context_menu_move_to_bottom:
				moveSpriteToBottom();
				break;
			case R.id.context_menu_move_to_top:
				moveSpriteToTop();
				break;
		}
		return super.onContextItemSelected(item);
	}

	public void switchToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_SPRITES);
		startActivity(intent);
	}

	@Override
	public void onSpriteChecked() {
		if (isRenameActionMode || actionMode == null) {
			return;
		}

		updateActionModeTitle();
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = spriteAdapter.getAmountOfCheckedItems();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(multiSelectActionModeTitle);
		} else {
			String appendix = multipleItemAppendixMultiSelectActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixMultiSelectActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = multiSelectActionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = multiSelectActionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	@Override
	public void onSpriteEdit(int position) {
		if (isRenameActionMode) {
			spriteToEdit = spriteAdapter.getItem(position);
			showRenameDialog();
		} else {
			ProjectManager.getInstance().setCurrentSprite(spriteAdapter.getItem(position));
			Intent intent = new Intent(getActivity(), ProgramMenuActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void startCopyActionMode() {
		startActionMode(copyModeCallBack, false, false);
	}

	@Override
	public void startRenameActionMode() {
		startActionMode(renameModeCallBack, true, false);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack, false, false);
	}

	@Override
	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack, false, true);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback, boolean isRenameMode, boolean isBackPackMode) {
		if (actionMode == null) {
			if (spriteAdapter.getCount() == 1 && !actionModeCallback.equals(backPackModeCallBack)) {
				if (actionModeCallback.equals(copyModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.copy));
				} else if (actionModeCallback.equals(deleteModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
				} else if (actionModeCallback.equals(renameModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.rename));
				}
			} else {
				actionMode = getActivity().startActionMode(actionModeCallback);
				BottomBar.hideBottomBar(getActivity());
				isRenameActionMode = isRenameMode;
				isBackPackActionMode = isBackPackMode;
			}
		}
	}

	@Override
	public void handleAddButton() {
		//handled in ProjectActivity
	}

	private void moveSpriteDown() {
		Collections.swap(spriteList, spritePosition + 1, spritePosition);
		spriteAdapter.notifyDataSetChanged();
	}

	private void moveSpriteUp() {
		Collections.swap(spriteList, spritePosition - 1, spritePosition);
		spriteAdapter.notifyDataSetChanged();
	}

	private void moveSpriteToBottom() {
		for (int i = spritePosition; i < spriteList.size() - 1; i++) {
			Collections.swap(spriteList, i, i + 1);
		}
		spriteAdapter.notifyDataSetChanged();
	}

	private void moveSpriteToTop() {
		for (int i = spritePosition; i > 1; i--) {
			Collections.swap(spriteList, i, i - 1);
		}
		spriteAdapter.notifyDataSetChanged();
	}

	public void handleCheckBoxClick(View view) {
		int position = getListView().getPositionForView(view);
		getListView().setItemChecked(position, ((CheckBox) view.findViewById(R.id.sprite_checkbox)).isChecked());
	}

	public void copySprite() {
		Sprite copiedSprite = spriteToEdit.clone();
		copiedSprite.setName(getSpriteName(spriteToEdit.getName().concat(getString(R.string.copy_sprite_name_suffix)),
				0));

		ProjectManager projectManager = ProjectManager.getInstance();

		projectManager.addSprite(copiedSprite);
		projectManager.setCurrentSprite(copiedSprite);

		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_SPRITES_LIST_CHANGED));
		Log.d("Sprite copied", copiedSprite.toString());
	}

	@Override
	public void showRenameDialog() {
		RenameSpriteDialog dialog = RenameSpriteDialog.newInstance(spriteToEdit.getName());
		dialog.show(getFragmentManager(), RenameSpriteDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	protected void showDeleteDialog() {
	}

	private void showConfirmDeleteDialog() {
		int titleId;
		if (spriteAdapter.getAmountOfCheckedItems() == 1) {
			titleId = R.string.dialog_confirm_delete_object_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_objects_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_object_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedSprites();
				clearCheckedSpritesAndEnableButtons();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedSpritesAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void deleteSprite() {
		ProjectManager projectManager = ProjectManager.getInstance();
		DataContainer dataContainer = projectManager.getCurrentProject().getDataContainer();

		deleteSpriteFiles();
		dataContainer.cleanVariableListForSprite(spriteToEdit);
		dataContainer.cleanUserListForSprite(spriteToEdit);

		if (projectManager.getCurrentSprite() != null && projectManager.getCurrentSprite().equals(spriteToEdit)) {
			projectManager.setCurrentSprite(null);
		}
		projectManager.getCurrentProject().getSpriteList().remove(spriteToEdit);
	}

	private void deleteCheckedSprites() {
		int numDeleted = 0;
		for (int position : spriteAdapter.getCheckedItems()) {
			spriteToEdit = (Sprite) getListView().getItemAtPosition(position - numDeleted);
			deleteSprite();
			numDeleted++;
		}
	}

	private void clearCheckedSpritesAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		spriteAdapter.clearCheckedItems();

		actionMode = null;
		actionModeActive = false;

		BottomBar.showBottomBar(getActivity());
	}

	@Override
	public int getSelectMode() {
		return spriteAdapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		spriteAdapter.setSelectMode(selectMode);
		spriteAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean getShowDetails() {
		return spriteAdapter.getShowDetails();
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		spriteAdapter.setShowDetails(showDetails);
		spriteAdapter.notifyDataSetChanged();
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAll = true;
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			CapitalizedTextView selectAllView = (CapitalizedTextView) selectAllActionModeButton.findViewById(R.id.select_all);

			@Override
			public void onClick(View view) {
				if (selectAll) {
					int startPosition = 1;
					if (isBackPackActionMode) {
						startPosition = 0;
					}
					while (startPosition < spriteList.size()) {
						spriteAdapter.addCheckedSprite(startPosition);
						startPosition++;
					}
					spriteAdapter.notifyDataSetChanged();
					onSpriteChecked();
					selectAll = false;
					selectAllView.setText(R.string.deselect_all);
				} else {
					spriteAdapter.clearCheckedItems();
					spriteAdapter.notifyDataSetChanged();
					onSpriteChecked();
					selectAll = true;
					selectAllView.setText(R.string.select_all);
				}
			}
		});
	}

	private class SpriteRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SPRITE_RENAMED)) {
				String newSpriteName = intent.getExtras().getString(RenameSpriteDialog.EXTRA_NEW_SPRITE_NAME);
				spriteToEdit.setName(newSpriteName);
			}
		}
	}

	private class SpritesListChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SPRITES_LIST_CHANGED)) {
				spriteAdapter.notifyDataSetChanged();
				final ListView listView = getListView();
				listView.post(new Runnable() {
					@Override
					public void run() {
						listView.setSelection(listView.getCount() - 1);
					}
				});
			}
		}
	}

	private class SpritesListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SPRITES_LIST_INIT)) {
				spriteAdapter.notifyDataSetChanged();
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

			actionModeActive = true;

			multiSelectActionModeTitle = getString(R.string.delete);
			singleItemAppendixMultiSelectActionMode = getString(R.string.sprite);
			multipleItemAppendixMultiSelectActionMode = getString(R.string.sprites);

			mode.setTitle(multiSelectActionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (spriteAdapter.getAmountOfCheckedItems() == 0) {
				clearCheckedSpritesAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};

	private ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_SINGLE);

			mode.setTitle(R.string.rename);
			actionModeActive = true;
			isRenameActionMode = true;
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			isRenameActionMode = false;
			Set<Integer> checkedSprites = spriteAdapter.getCheckedItems();
			Iterator<Integer> iterator = checkedSprites.iterator();
			if (iterator.hasNext()) {
				int position = iterator.next();
				spriteToEdit = (Sprite) getListView().getItemAtPosition(position);
				showRenameDialog();
			}
			clearCheckedSpritesAndEnableButtons();
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

			actionModeActive = true;

			multiSelectActionModeTitle = getString(R.string.copy);
			singleItemAppendixMultiSelectActionMode = getString(R.string.sprite);
			multipleItemAppendixMultiSelectActionMode = getString(R.string.sprites);

			mode.setTitle(multiSelectActionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			for (int position : spriteAdapter.getCheckedItems()) {
				spriteToEdit = (Sprite) getListView().getItemAtPosition(position);
				copySprite();
			}
			clearCheckedSpritesAndEnableButtons();
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

			multiSelectActionModeTitle = getString(R.string.backpack);
			singleItemAppendixMultiSelectActionMode = getString(R.string.sprite);
			multipleItemAppendixMultiSelectActionMode = getString(R.string.sprites);

			mode.setTitle(multiSelectActionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			for (int position : spriteAdapter.getCheckedItems()) {
				spriteToEdit = (Sprite) getListView().getItemAtPosition(position);
				BackPackSpriteController.getInstance().backpack(spriteToEdit, false);
			}
			if (!spriteAdapter.getCheckedItems().isEmpty()) {
				switchToBackPack();
			}
			clearCheckedSpritesAndEnableButtons();
			isBackPackActionMode = false;
		}
	};

	private void initListeners() {
		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		spriteAdapter = new SpriteAdapter(getActivity(), R.layout.activity_project_spritelist_item,
				R.id.project_activity_sprite_title, spriteList);
		spriteAdapter.setSpritesListFragment(this);

		spriteAdapter.setOnSpriteEditListener(this);
		setListAdapter(spriteAdapter);
		getListView().setTextFilterEnabled(true);
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
	}

	private void deleteSpriteFiles() {
		for (LookData currentLookData : spriteToEdit.getLookDataList()) {
			StorageHandler.getInstance().deleteFile(currentLookData.getAbsolutePath(), false);
		}

		for (SoundInfo currentSoundInfo : spriteToEdit.getSoundList()) {
			StorageHandler.getInstance().deleteFile(currentSoundInfo.getAbsolutePath(), false);
		}
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity) {
		initListeners();
		spriteAdapter.notifyDataSetChanged();
		isLoading = false;
		getActivity().findViewById(R.id.progress_circle).setVisibility(View.GONE);
		getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);

		showInfoFragmentIfNeeded();
	}

	private void showInfoFragmentIfNeeded() {
		if (needToShowLegoNXTInfoDialog()) {
			DialogFragment dialog = new LegoNXTSensorConfigInfoDialog();
			dialog.show(this.getActivity().getFragmentManager(), LegoNXTSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
	}

	private boolean needToShowLegoNXTInfoDialog() {
		boolean isLegoNXTInfoDialogDisabled = SettingsActivity.getShowLegoMindstormsSensorInfoDialog(this.getActivity().getApplicationContext());
		Project project = ProjectManager.getInstance().getCurrentProject();

		return !isLegoNXTInfoDialogDisabled && (project.getRequiredResources() & Brick.BLUETOOTH_LEGO_NXT) != 0;
	}

	@Override
	public void onLoadProjectFailure() {
		getActivity().onBackPressed();
	}

	public boolean isBackPackActionMode() {
		return isBackPackActionMode;
	}

	public Sprite getSpriteToEdit() {
		return spriteToEdit;
	}

	private static String getSpriteName(String name, int nextNumber) {
		String newName;
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (Sprite sprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
			if (sprite.getName().equals(newName)) {
				return getSpriteName(name, ++nextNumber);
			}
		}
		return newName;
	}
}
