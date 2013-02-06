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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.adapter.SpriteAdapter.OnSpriteCheckedListener;
import org.catrobat.catroid.ui.dialogs.RenameSpriteDialog;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

public class SpritesListFragment extends SherlockListFragment implements OnSpriteCheckedListener {

	private static final String BUNDLE_ARGUMENTS_SPRITE_TO_EDIT = "sprite_to_edit";
	private static final String SHARED_PREFERENCE_NAME = "showDetailsProjects";

	private static String deleteActionModeTitle;
	private static String singleItemAppendixDeleteActionMode;
	private static String multipleItemAppendixDeleteActionMode;

	private SpriteAdapter spriteAdapter;
	private ArrayList<Sprite> spriteList;
	private Sprite spriteToEdit;

	private SpriteRenamedReceiver spriteRenamedReceiver;
	private SpritesListChangedReceiver spritesListChangedReceiver;
	private SpritesListInitReceiver spritesListInitReceiver;

	private ActionMode actionMode;

	private boolean actionModeActive = false;
	private boolean isRenameActionMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sprites_list, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());
		if (savedInstanceState != null) {
			spriteToEdit = (Sprite) savedInstanceState.get(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT);
		}

		try {
			Utils.loadProjectIfNeeded(getActivity(), (ErrorListenerInterface) getActivity());
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
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Adapter adapter = getListAdapter();

		spriteToEdit = (Sprite) adapter.getItem(info.position);

		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(spriteToEdit) == 0) {
			return;
		}

		menu.setHeaderTitle(spriteToEdit.getName());

		getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_default, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.copy:
				break;

			case R.id.cut:
				break;

			case R.id.insert_below:
				break;

			case R.id.move:
				break;

			case R.id.rename:
				showRenameDialog();
				break;

			case R.id.delete:
				deleteSprite();
				break;

		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onSpriteChecked() {
		if (isRenameActionMode || actionMode == null) {
			return;
		}

		int numberOfSelectedItems = spriteAdapter.getAmountOfCheckedSprites();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(deleteActionModeTitle);
		} else {
			String appendix = multipleItemAppendixDeleteActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixDeleteActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = deleteActionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = deleteActionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	public void startRenameActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(renameModeCallBack);
			setBottomBarActivated(false);
			isRenameActionMode = true;
		}
	}

	public void startDeleteActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);
			setBottomBarActivated(false);
			isRenameActionMode = false;
		}
	}

	public Sprite getSpriteToEdit() {
		return spriteToEdit;
	}

	public void handleCheckBoxClick(View view) {
		int position = getListView().getPositionForView(view);
		getListView().setItemChecked(position, ((CheckBox) view.findViewById(R.id.checkbox)).isChecked());
	}

	public void showRenameDialog() {
		RenameSpriteDialog dialog = RenameSpriteDialog.newInstance(spriteToEdit.getName());
		dialog.show(getFragmentManager(), RenameSpriteDialog.DIALOG_FRAGMENT_TAG);
	}

	public void deleteSprite() {
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.getCurrentProject().getSpriteList().remove(spriteToEdit);
		deleteSpriteFiles();
		if (projectManager.getCurrentSprite() != null && projectManager.getCurrentSprite().equals(spriteToEdit)) {
			projectManager.setCurrentSprite(null);
		}
	}

	public void setSelectMode(int selectMode) {
		spriteAdapter.setSelectMode(selectMode);
		spriteAdapter.notifyDataSetChanged();
	}

	public int getSelectMode() {
		return spriteAdapter.getSelectMode();
	}

	public void setShowDetails(boolean showDetails) {
		spriteAdapter.setShowDetails(showDetails);
		spriteAdapter.notifyDataSetChanged();
	}

	public boolean getShowDetails() {
		return spriteAdapter.getShowDetails();
	}

	public boolean getActionModeActive() {
		return actionModeActive;
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
			setSelectMode(Constants.MULTI_SELECT);

			actionModeActive = true;

			deleteActionModeTitle = getString(R.string.delete);
			singleItemAppendixDeleteActionMode = getString(R.string.sprite);
			multipleItemAppendixDeleteActionMode = getString(R.string.sprites);

			mode.setTitle(deleteActionModeTitle);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Set<Integer> checkedSprites = spriteAdapter.getCheckedSprites();
			Iterator<Integer> iterator = checkedSprites.iterator();
			int numDeleted = 0;
			while (iterator.hasNext()) {
				int position = iterator.next();
				spriteToEdit = (Sprite) getListView().getItemAtPosition(position - numDeleted);
				deleteSprite();
				numDeleted++;
			}
			setSelectMode(Constants.SELECT_NONE);
			spriteAdapter.clearCheckedSprites();

			actionMode = null;
			actionModeActive = false;

			setBottomBarActivated(true);
		}
	};

	private ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(Constants.SINGLE_SELECT);
			mode.setTitle(getString(R.string.rename));

			actionModeActive = true;

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Set<Integer> checkedSprites = spriteAdapter.getCheckedSprites();
			Iterator<Integer> iterator = checkedSprites.iterator();
			if (iterator.hasNext()) {
				int position = iterator.next();
				spriteToEdit = (Sprite) getListView().getItemAtPosition(position);
				showRenameDialog();
			}
			setSelectMode(Constants.SELECT_NONE);
			spriteAdapter.clearCheckedSprites();

			actionMode = null;
			actionModeActive = false;

			setBottomBarActivated(true);
		}
	};

	private void initListeners() {
		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		spriteAdapter = new SpriteAdapter(getActivity(), R.layout.activity_project_spritelist_item, R.id.sprite_title,
				spriteList);

		spriteAdapter.setOnSpriteCheckedListener(this);
		setListAdapter(spriteAdapter);
		getListView().setTextFilterEnabled(true);
		getListView().setDivider(null);
		getListView().setDividerHeight(0);

		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProjectManager.getInstance().setCurrentSprite(spriteAdapter.getItem(position));
				Intent intent = new Intent(getActivity(), ProgramMenuActivity.class);
				startActivity(intent);
			}
		});

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				spriteToEdit = spriteList.get(position);

				// as long as background sprite is always the first one, we're fine
				if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(spriteToEdit) == 0) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	private void deleteSpriteFiles() {
		List<LookData> lookDataList = spriteToEdit.getLookDataList();
		List<SoundInfo> soundInfoList = spriteToEdit.getSoundList();

		for (LookData currentLookData : lookDataList) {
			StorageHandler.getInstance().deleteFile(currentLookData.getAbsolutePath());
		}

		for (SoundInfo currentSoundInfo : soundInfoList) {
			StorageHandler.getInstance().deleteFile(currentSoundInfo.getAbsolutePath());
		}
	}

	private void setBottomBarActivated(boolean isActive) {
		Utils.setBottomBarActivated(getActivity(), isActive);
	}
}
