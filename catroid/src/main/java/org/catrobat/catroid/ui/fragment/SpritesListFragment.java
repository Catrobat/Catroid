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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.zed.bdsclient.controller.BDSClientController;
import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.CapitalizedTextView;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.RenameSpriteDialog;
import org.catrobat.catroid.ui.dynamiclistview.DynamicExpandableListView;
import org.catrobat.catroid.utils.DividerUtil;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.TrackingUtil;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SpritesListFragment extends Fragment implements SpriteAdapter.OnSpriteEditListener,
		BackPackSpriteController.OnBackpackSpriteCompleteListener, ListItemActionsInterface {

	public static final String TAG = SpritesListFragment.class.getSimpleName();
	public static final String SHARED_PREFERENCE_NAME = "showDetailsProjects";
	private static final String BUNDLE_ARGUMENTS_SPRITE_TO_EDIT = "sprite_to_edit";

	private static String multiSelectActionModeTitle;
	private static String singleItemAppendixMultiSelectActionMode;
	private static String multipleItemAppendixMultiSelectActionMode;
	private SpriteAdapter spriteAdapter;
	private Sprite spriteToEdit;
	private SpriteRenamedReceiver spriteRenamedReceiver;
	private SpritesListChangedReceiver spritesListChangedReceiver;
	private SpritesListInitReceiver spritesListInitReceiver;
	private SpriteListTouchActionUpReceiver spriteListTouchActionUpReceiver;

	private ActionMode actionMode;
	private View selectAllActionModeButton;
	private boolean isRenameActionMode;
	private boolean isBackPackActionMode;
	private boolean isCopyActionMode;
	private boolean isDeleteActionMode;
	private boolean selectAll = true;
	private boolean actionModeActive = false;

	private Button okButton;
	private DynamicExpandableListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View spriteListFragment = inflater.inflate(R.layout.fragment_sprites_list, container, false);
		TextSizeUtil.enlargeViewGroup((ViewGroup) spriteListFragment);
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_objects);
		return spriteListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = (DynamicExpandableListView) getActivity().findViewById(R.id.fragment_sprites_list_listview);
		listView.setAdapter(spriteAdapter);
		listView.getUtilDynamicListView().setSpritesListFragment(this);

		registerForContextMenu(getListView());
		if (savedInstanceState != null) {
			spriteToEdit = (Sprite) savedInstanceState.get(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT);
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
		DividerUtil.setDivider(getActivity(), getListView());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ProjectManager.getInstance().getCurrentProject().isScenesEnabled()) {
			getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentScene().getName());
		} else {
			getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentProject().getName());
		}

		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (BackPackListManager.getInstance().isBackpackEmpty()) {
			BackPackListManager.getInstance().loadBackpack();
		}

		if (spriteRenamedReceiver == null) {
			spriteRenamedReceiver = new SpriteRenamedReceiver();
		}

		if (spritesListChangedReceiver == null) {
			spritesListChangedReceiver = new SpritesListChangedReceiver();
		}

		if (spritesListInitReceiver == null) {
			spritesListInitReceiver = new SpritesListInitReceiver();
		}

		if (spriteListTouchActionUpReceiver == null) {
			spriteListTouchActionUpReceiver = new SpriteListTouchActionUpReceiver();
		}

		IntentFilter intentFilterSpriteRenamed = new IntentFilter(ScriptActivity.ACTION_SPRITE_RENAMED);
		getActivity().registerReceiver(spriteRenamedReceiver, intentFilterSpriteRenamed);

		IntentFilter intentFilterSpriteListChanged = new IntentFilter(ScriptActivity.ACTION_SPRITES_LIST_CHANGED);
		getActivity().registerReceiver(spritesListChangedReceiver, intentFilterSpriteListChanged);

		IntentFilter intentFilterSpriteListInit = new IntentFilter(ScriptActivity.ACTION_SPRITES_LIST_INIT);
		getActivity().registerReceiver(spritesListInitReceiver, intentFilterSpriteListInit);

		IntentFilter intentFilterSpriteListTouchActionUp = new IntentFilter(ScriptActivity
				.ACTION_SPRITE_TOUCH_ACTION_UP);
		getActivity().registerReceiver(spriteListTouchActionUpReceiver, intentFilterSpriteListTouchActionUp);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));

		reExpandAllGroups();
	}

	private void reExpandAllGroups() {
		for (int groupPosition : spriteAdapter.getGroupSpritePositions()) {
			Sprite sprite = (Sprite) spriteAdapter.getGroup(groupPosition);
			if (!listView.isGroupExpanded(groupPosition) && sprite instanceof GroupSprite && ((GroupSprite) sprite).shouldBeExpanded()) {
				listView.expandGroup(groupPosition);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

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

		if (spriteListTouchActionUpReceiver != null) {
			getActivity().unregisterReceiver(spriteListTouchActionUpReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
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
		updateSelectAllView();
	}

	private void updateSelectAllView() {
		CapitalizedTextView selectAllView = (CapitalizedTextView) selectAllActionModeButton.findViewById(R.id.select_all);

		Scene scene = ProjectManager.getInstance().getCurrentScene();
		int numberOfNonGroupItems = scene.getSpriteList().size() - spriteAdapter.getNumberOfGroups();
		int numberOfCheckedItems = spriteAdapter.getCheckedItems().size();

		if ((isBackPackActionMode && numberOfCheckedItems < numberOfNonGroupItems)
				|| (!isBackPackActionMode && numberOfCheckedItems < (numberOfNonGroupItems - 1))) {
			selectAll = true;
			selectAllView.setVisibility(View.VISIBLE);
			selectAllView.setText(R.string.select_all);
		} else if (numberOfCheckedItems > 0) {
			selectAll = false;
			selectAllView.setVisibility(View.VISIBLE);
			selectAllView.setText(R.string.deselect_all);
		}
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
	public void onSpriteEdit(int groupPosition, int childPosition) {

		if (isRenameActionMode) {
			spriteToEdit = (Sprite) spriteAdapter.getChild(groupPosition, childPosition);
			showRenameDialog();
		} else {
			Sprite currentSprite = (Sprite) spriteAdapter.getChild(groupPosition, childPosition);
			ProjectManager.getInstance().setCurrentSprite(currentSprite);
			Intent intent = new Intent(getActivity(), ProgramMenuActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onSpriteEdit(int groupPosition) {

		if (isRenameActionMode) {
			spriteToEdit = (Sprite) spriteAdapter.getGroup(groupPosition);
			showRenameDialog();
		} else {
			Sprite currentSprite = (Sprite) spriteAdapter.getGroup(groupPosition);
			ProjectManager.getInstance().setCurrentSprite(currentSprite);
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
			List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
			if (spriteList.size() == 1 && !actionModeCallback.equals(backPackModeCallBack)) {
				if (actionModeCallback.equals(copyModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.copy));
				} else if (actionModeCallback.equals(deleteModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
				} else if (actionModeCallback.equals(renameModeCallBack)) {
					((ProjectActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.rename));
				}
			} else {
				expandAllGroups();
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

	public void copySprite() {
		spriteToEdit.setConvertToSingleSprite(true);
		Sprite copiedSprite = spriteToEdit.clone();
		spriteToEdit.setConvertToSingleSprite(false);

		String oldName = copiedSprite.getName();
		copiedSprite.setName(getSpriteName(spriteToEdit.getName().concat(getString(R.string.copy_sprite_name_suffix)),
				0));
		String newName = copiedSprite.getName();
		copiedSprite.renameCopiedSpriteInCollisionFormulas(oldName, newName, getActivity());
		copiedSprite.updateCollisionBroadcastMessages(oldName, newName);

		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.addSprite(copiedSprite);
		projectManager.setCurrentSprite(copiedSprite);

		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_SPRITES_LIST_CHANGED));
		Log.d(TAG, copiedSprite.toString());
		TrackingUtil.trackSprite(copiedSprite.toString(), "CopySprite");
	}

	@Override
	public void showRenameDialog() {
		RenameSpriteDialog dialog = RenameSpriteDialog.newInstance(spriteToEdit.getName());
		dialog.show(getFragmentManager(), RenameSpriteDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showDeleteDialog() {
		final Context context = getActivity();

		final CharSequence deleteGroupSpriteOnly = context.getText(R.string.ungroup);
		final CharSequence deleteGroupAndSprites = context.getText(R.string.group_objects_delete);
		final List<CharSequence> items = new ArrayList<>();
		items.add(deleteGroupSpriteOnly);
		items.add(deleteGroupAndSprites);
		String title = context.getText(R.string.delete).toString();

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CharSequence clickedItemText = items.get(which);
				if (clickedItemText.equals(deleteGroupSpriteOnly)) {
					deleteCheckedSprites(false);
				} else if (clickedItemText.equals(deleteGroupAndSprites)) {
					deleteCheckedSprites(true);
				}
				clearCheckedSpritesAndEnableButtons();
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				clearCheckedSpritesAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void deleteCheckedSprites(boolean deleteGroupItemSprites) {
		int numDeleted = 0;
		for (int position : spriteAdapter.getCheckedItems()) {
			spriteToEdit = getSpriteList().get(position - numDeleted);
			if (spriteToEdit instanceof GroupSprite) {
				if (!deleteGroupItemSprites) {
					convertGroupItemsToSingleSprites(position - numDeleted);
				} else {
					numDeleted += spriteAdapter.getChildrenCount(spriteToEdit);
				}
				deleteGroup(deleteGroupItemSprites);
				numDeleted++;
			} else {
				deleteSprite();
				numDeleted++;
			}
		}
	}

	private void deleteGroup(boolean deleteSprites) {
		int numberOfGroupItemSprites = 0;
		int indexOfGroupSprite = getSpriteList().indexOf(spriteToEdit);

		for (int groupItemPosition = indexOfGroupSprite + 1; groupItemPosition < getSpriteList().size();
				groupItemPosition++) {
			Sprite currentSprite = getSpriteList().get(groupItemPosition);
			if (currentSprite instanceof GroupItemSprite) {
				numberOfGroupItemSprites++;
			} else {
				break;
			}
		}

		for (int groupItemPosition = indexOfGroupSprite + numberOfGroupItemSprites;
				groupItemPosition > indexOfGroupSprite; groupItemPosition--) {
			spriteToEdit = getSpriteList().get(groupItemPosition);
			if (deleteSprites) {
				deleteSprite();
			} else {
				spriteToEdit.setConvertToSingleSprite(true);
				SingleSprite clonedSprite = (SingleSprite) spriteToEdit.clone();
				getSpriteList().add(groupItemPosition, clonedSprite);
			}
		}

		spriteToEdit = getSpriteList().get(indexOfGroupSprite);
		deleteSprite();
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
				if (isGroupWithoutGroupItemsChecked()) {
					showDeleteDialog();
				} else {
					deleteCheckedSprites(false);
					clearCheckedSpritesAndEnableButtons();
				}
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedSpritesAndEnableButtons();
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				clearCheckedSpritesAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private boolean isGroupWithoutGroupItemsChecked() {
		Set<Integer> checkedItems = spriteAdapter.getCheckedItems();
		for (int position : checkedItems) {
			Sprite sprite = getSpriteList().get(position);
			if (sprite instanceof GroupSprite) {
				int childrenCount = spriteAdapter.getChildrenCount(sprite);
				boolean groupItemSpriteChecked = false;
				for (int childPosition = position + 1; childPosition < position + 1 + childrenCount; childPosition++) {
					if (checkedItems.contains(childPosition)) {
						groupItemSpriteChecked = true;
					}
				}
				if (childrenCount > 0 && !groupItemSpriteChecked) {
					return true;
				}
			}
		}
		return false;
	}

	public void deleteSprite() {
		ProjectManager projectManager = ProjectManager.getInstance();
		DataContainer dataContainer = projectManager.getCurrentScene().getDataContainer();

		for (LookData currentLookData : spriteToEdit.getLookDataList()) {
			currentLookData.getCollisionInformation().cancelCalculation();
		}

		TrackingUtil.trackDeleteSprite(spriteToEdit);

		deleteSpriteFiles();
		dataContainer.cleanVariableListForSprite(spriteToEdit);
		dataContainer.cleanUserListForSprite(spriteToEdit);

		if (projectManager.getCurrentSprite() != null && projectManager.getCurrentSprite().equals(spriteToEdit)) {
			projectManager.setCurrentSprite(null);
		}
		projectManager.getCurrentScene().getSpriteList().remove(spriteToEdit);
	}

	private void convertGroupItemsToSingleSprites(int groupPosition) {
		int childCount = spriteAdapter.getChildCountWithGroupSpriteIndex(groupPosition);
		for (int position = groupPosition + 1; position < groupPosition + 1 + childCount; position++) {
			getSpriteList().get(position).setConvertToSingleSprite(true);
			Sprite cloneSprite = getSpriteList().get(position).clone();
			getSpriteList().remove(position);
			getSpriteList().add(position, cloneSprite);
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
	public boolean getActionModeActive() {
		return actionModeActive;
	}

	@Override
	public void setActionModeActive(boolean actionModeActive) {
		this.actionModeActive = actionModeActive;
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

	public boolean shouldSpriteBeChecked(int flatPosition) {
		Sprite sprite = getSpriteList().get(flatPosition);
		if ((isBackPackActionMode && !(sprite instanceof GroupSprite))
				|| ((isCopyActionMode) && !(sprite instanceof GroupSprite) && flatPosition > 0)
				|| ((isRenameActionMode) && flatPosition > 0)
				|| ((isDeleteActionMode) && flatPosition > 0)) {
			return true;
		}
		return false;
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAll = true;
		selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			CapitalizedTextView selectAllView = (CapitalizedTextView) selectAllActionModeButton.findViewById(R.id.select_all);

			@Override
			public void onClick(View view) {
				if (selectAll) {
					int startPosition = 1;
					if (isBackPackActionMode) {
						startPosition = 0;
					}
					while (startPosition < getSpriteList().size()) {
						if (shouldSpriteBeChecked(startPosition)) {
							spriteAdapter.addCheckedSprite(startPosition);
						}
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

	private void expandAllGroups() {
		for (int groupPosition : spriteAdapter.getGroupSpritePositions()) {
			if (!listView.isGroupExpanded(groupPosition)) {
				listView.expandGroup(groupPosition);
			}
		}
		spriteAdapter.setExpandedIndicatorsForAllGroupSprites(true);
	}

	public void collapseAllGroups() {
		for (int groupPosition : spriteAdapter.getGroupSpritePositions()) {
			if (listView.isGroupExpanded(groupPosition)) {
				listView.collapseGroup(groupPosition);
			}
		}
		spriteAdapter.setExpandedIndicatorsForAllGroupSprites(false);
	}

	private class SpriteRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SPRITE_RENAMED)) {
				String newSpriteName = intent.getExtras().getString(RenameSpriteDialog.EXTRA_NEW_SPRITE_NAME);
				String oldSpriteName = spriteToEdit.getName();
				renameSpritesInCollisionFormulas(oldSpriteName, newSpriteName, getActivity());
				spriteToEdit.rename(newSpriteName);
				spriteAdapter.replaceItemInIdMap(oldSpriteName, newSpriteName);
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

	private class SpriteListTouchActionUpReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SPRITE_TOUCH_ACTION_UP)) {
				getListView().notifyListItemTouchActionUp();
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
			isDeleteActionMode = true;

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
			isDeleteActionMode = false;
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
				spriteToEdit = getSpriteList().get(position);
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
			isCopyActionMode = true;

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			for (int position : spriteAdapter.getCheckedItems()) {
				spriteToEdit = getSpriteList().get(position);
				copySprite();
			}
			clearCheckedSpritesAndEnableButtons();
			isCopyActionMode = false;
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
			List<Sprite> spriteListToBackpack = new ArrayList<>();
			for (Integer position : spriteAdapter.getCheckedItems()) {
				spriteToEdit = getSpriteList().get(position);
				spriteListToBackpack.add(spriteToEdit);
			}

			boolean spritesAlreadyInBackpack = BackPackSpriteController.getInstance().checkSpriteReplaceInBackpack(spriteListToBackpack);

			if (!spriteListToBackpack.isEmpty()) {
				if (!spritesAlreadyInBackpack) {
					for (Sprite spriteToBackpack : spriteListToBackpack) {
						BackPackSpriteController.getInstance().backpackVisibleSprite(spriteToBackpack);
						onBackpackSpriteComplete(true);
					}
				} else {
					BackPackSpriteController.getInstance().setOnBackpackSpriteCompleteListener(SpritesListFragment.this);
					BackPackSpriteController.getInstance().showBackPackReplaceDialog(spriteListToBackpack, getActivity());
				}
			} else {
				clearCheckedSpritesAndEnableButtons();
			}
		}
	};

	@Override
	public void onBackpackSpriteComplete(boolean startBackpackActivity) {
		if (!spriteAdapter.getCheckedItems().isEmpty() && startBackpackActivity) {
			switchToBackPack();
		}
		clearCheckedSpritesAndEnableButtons();
		isBackPackActionMode = false;
	}

	private void initListeners() {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		getListView().setDataList(spriteList);
		getListView().isForSpriteList();
		spriteAdapter = new SpriteAdapter(getActivity(), spriteList);
		spriteAdapter.setSpritesListFragment(this);

		spriteAdapter.setOnSpriteEditListener(this);
		listView.setAdapter(spriteAdapter);
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

	public boolean isBackPackActionMode() {
		return isBackPackActionMode;
	}

	public boolean isCopyActionMode() {
		return isCopyActionMode;
	}

	public boolean isDeleteActionMode() {
		return isDeleteActionMode;
	}

	private static String getSpriteName(String name, int nextNumber) {
		String newName;
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (Sprite sprite : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			if (sprite.getName().equals(newName)) {
				return getSpriteName(name, ++nextNumber);
			}
		}
		return newName;
	}

	public void showNewObjectGroupDialog() {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(R.string.new_group);
		View view = View.inflate(getActivity(), R.layout.new_group_dialog, null);
		builder.setView(view);
		final EditText groupNameEditText = (EditText) view.findViewById(R.id.new_group_dialog_group_name);
		int currentNumberOfGroups = spriteAdapter.getNumberOfGroups();
		String text = getString(R.string.group) + " " + (currentNumberOfGroups + 1);
		groupNameEditText.setText(text);
		UtilUi.positionCursorForEditText(groupNameEditText);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String groupName = groupNameEditText.getText().toString().trim();
				List<String> takenNames = spriteAdapter.getGroupNames();
				takenNames.addAll(spriteAdapter.getNonGroupNames());
				if (takenNames.contains(groupName)) {
					showNameForGroupAlreadyGivenDialog();
				} else {
					createGroup(groupName);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		final AlertDialog alertDialog = builder.create();

		groupNameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence groupName, int start, int before, int count) {
				if (groupName.toString().trim().isEmpty()) {
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				TextSizeUtil.enlargeViewGroup((ViewGroup) alertDialog.getWindow().getDecorView().getRootView());
			}
		});

		alertDialog.show();
		okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
	}

	private void showNameForGroupAlreadyGivenDialog() {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(R.string.new_group);
		View view = View.inflate(getActivity(), R.layout.new_group_name_given_dialog, null);
		builder.setView(view);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				showNewObjectGroupDialog();
			}
		});

		final AlertDialog alertDialog = builder.create();

		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				TextSizeUtil.enlargeViewGroup((ViewGroup) alertDialog.getWindow().getDecorView().getRootView());
			}
		});

		alertDialog.show();
	}

	private void createGroup(String groupName) {
		TrackingUtil.trackSprite(groupName, "CreateGroup");
		GroupSprite groupSprite = new GroupSprite(groupName);
		getSpriteList().add(groupSprite);
		spriteAdapter.notifyDataSetChanged();
		ToastUtil.showSuccess(getActivity(), R.string.group_created);
	}

	public DynamicExpandableListView getListView() {
		return listView;
	}

	public SpriteAdapter getSpriteAdapter() {
		return spriteAdapter;
	}

	private List<Sprite> getSpriteList() {
		return spriteAdapter.getSpriteList();
	}

	private void renameSpritesInCollisionFormulas(String oldName, String newName, Context context) {

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		for (Sprite sprite : spriteList) {
			for (Script currentScript : sprite.getScriptList()) {
				if (currentScript == null) {
					return;
				}
				List<Brick> brickList = currentScript.getBrickList();
				for (Brick brick : brickList) {
					if (brick instanceof UserBrick) {
						List<Formula> formulaList = ((UserBrick) brick).getFormulas();
						for (Formula formula : formulaList) {
							formula.updateCollisionFormulas(oldName, newName, context);
						}
					}
					if (brick instanceof FormulaBrick) {
						List<Formula> formulaList = ((FormulaBrick) brick).getFormulas();
						for (Formula formula : formulaList) {
							formula.updateCollisionFormulas(oldName, newName, context);
						}
					}
				}
			}
		}
	}
}
