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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.SoundInfoHistory;
import org.catrobat.catroid.content.commands.Command;
import org.catrobat.catroid.content.commands.SoundCommands;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SoundViewHolder;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.dialogs.DeleteSoundDialog;
import org.catrobat.catroid.ui.dialogs.NewSoundDialog;
import org.catrobat.catroid.ui.dialogs.RenameSoundDialog;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SoundFragment extends ScriptActivityFragment implements SoundBaseAdapter.OnSoundEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener {

	public static final String TAG = SoundFragment.class.getSimpleName();

	private static int selectedSoundPosition = Constants.NO_POSITION;

	private static String actionModeTitle;

	private static String singleItemAppendixDeleteActionMode;
	private static String multipleItemAppendixDeleteActionMode;

	private MediaPlayer mediaPlayer;
	private SoundBaseAdapter adapter;
	private ArrayList<SoundInfo> soundInfoList;
	private SoundInfo selectedSoundInfo;

	private ListView listView;

	private SoundDeletedReceiver soundDeletedReceiver;
	private SoundRenamedReceiver soundRenamedReceiver;
	private SoundCopiedReceiver soundCopiedReceiver;

	private SoundsListInitReceiver soundsListInitReceiver;

	private ActionMode actionMode;
	private View selectAllActionModeButton;

	private boolean isRenameActionMode;
	private boolean isResultHandled = false;
	private boolean isAddNewSoundButtonClicked = false;

	private OnSoundInfoListChangedAfterNewListener soundInfoListChangedAfterNewListener;

	public void setOnSoundInfoListChangedAfterNewListener(OnSoundInfoListChangedAfterNewListener listener) {
		soundInfoListChangedAfterNewListener = listener;
	}

	private void setHandleAddbutton() {
		ImageButton addButton = (ImageButton) getSherlockActivity().findViewById(R.id.button_add);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				handleAddButton();
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sounds, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();
		registerForContextMenu(listView);

		if (savedInstanceState != null) {
			selectedSoundInfo = (SoundInfo) savedInstanceState
					.getSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}
		try {
			soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, Log.getStackTraceString(nullPointerException));
			soundInfoList = new ArrayList<SoundInfo>();
		}

		adapter = new SoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item,
				R.id.fragment_sound_item_title_text_view, soundInfoList, false);

		adapter.setOnSoundEditListener(this);
		setListAdapter(adapter);
		((SoundAdapter) adapter).setSoundFragment(this);

		Utils.loadProjectIfNeeded(getActivity());
		setHandleAddbutton();

		// set adapter and soundInfoList for ev. unpacking
		BackPackListManager.getInstance().setCurrentSoundInfoList(soundInfoList);
		BackPackListManager.getInstance().setCurrentSoundAdapter(adapter);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(true);

		boolean visibility = false;
		if (BuildConfig.FEATURE_BACKPACK_ENABLED) {
			visibility = true;
		}
		menu.findItem(R.id.backpack).setVisible(visibility);
		menu.findItem(R.id.cut).setVisible(false);

		if (BuildConfig.FEATURE_BACKPACK_ENABLED && BackPackListManager.getInstance().getSoundInfoArrayList().size() > 0) {
			menu.findItem(R.id.unpacking).setVisible(true);
		} else {
			menu.findItem(R.id.unpacking).setVisible(false);

			StorageHandler.getInstance().clearBackPackSoundDirectory();
		}

		com.actionbarsherlock.view.MenuItem undo = menu.findItem(R.id.menu_undo);
		if (!getHistory().isUndoable()) {
			undo.setIcon(R.drawable.icon_undo_disabled);
			undo.setEnabled(false);
		} else {
			undo.setIcon(R.drawable.icon_undo);
			undo.setEnabled(true);
		}

		com.actionbarsherlock.view.MenuItem redo = menu.findItem(R.id.menu_redo);
		if (!getHistory().isRedoable()) {
			redo.setIcon(R.drawable.icon_redo_disabled);
			redo.setEnabled(false);
		} else {
			redo.setIcon(R.drawable.icon_redo);
			redo.setEnabled(true);
		}

		menu.findItem(R.id.menu_undo).setVisible(true);
		menu.findItem(R.id.menu_redo).setVisible(true);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, selectedSoundInfo);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		mediaPlayer = new MediaPlayer();
		updatePlayersInCommands(mediaPlayer);
		initClickListener();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!ProjectManager.getInstance().getHandleCorrectAddButton()) {
			setHandleAddbutton();
		} else {
			ProjectManager.getInstance().setHandleCorrectAddButton(false);
		}

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (soundRenamedReceiver == null) {
			soundRenamedReceiver = new SoundRenamedReceiver();
		}

		if (soundDeletedReceiver == null) {
			soundDeletedReceiver = new SoundDeletedReceiver();
		}

		if (soundCopiedReceiver == null) {
			soundCopiedReceiver = new SoundCopiedReceiver();
		}

		if (soundsListInitReceiver == null) {
			soundsListInitReceiver = new SoundsListInitReceiver();
		}

		IntentFilter intentFilterRenameSound = new IntentFilter(ScriptActivity.ACTION_SOUND_RENAMED);
		getActivity().registerReceiver(soundRenamedReceiver, intentFilterRenameSound);

		IntentFilter intentFilterDeleteSound = new IntentFilter(ScriptActivity.ACTION_SOUND_DELETED);
		getActivity().registerReceiver(soundDeletedReceiver, intentFilterDeleteSound);

		IntentFilter intentFilterCopySound = new IntentFilter(ScriptActivity.ACTION_SOUND_COPIED);
		getActivity().registerReceiver(soundCopiedReceiver, intentFilterCopySound);

		IntentFilter intentFilterSoundsListInit = new IntentFilter(ScriptActivity.ACTION_SOUNDS_LIST_INIT);
		getActivity().registerReceiver(soundsListInitReceiver, intentFilterSoundsListInit);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SoundController.SHARED_PREFERENCE_NAME, false));

		SoundController.getInstance().handleAddButtonFromNew(this);

		if (!SoundInfoHistory.getAllUndoRedoStatus()) {
			SoundInfoHistory.applyChanges(ProjectManager.getInstance().getCurrentProject().getName());
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			SoundController.getInstance().handleAddButtonFromNew(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
		}
		SoundController.getInstance().stopSound(mediaPlayer, soundInfoList);
		adapter.notifyDataSetChanged();

		if (soundRenamedReceiver != null) {
			getActivity().unregisterReceiver(soundRenamedReceiver);
		}

		if (soundDeletedReceiver != null) {
			getActivity().unregisterReceiver(soundDeletedReceiver);
		}

		if (soundCopiedReceiver != null) {
			getActivity().unregisterReceiver(soundCopiedReceiver);
		}

		if (soundsListInitReceiver != null) {
			getActivity().unregisterReceiver(soundsListInitReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SoundController.SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onStop() {
		super.onStop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mediaPlayer = null;
	}

	@Override
	public void startCopyActionMode() {
		if (actionMode == null) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
			actionMode = getSherlockActivity().startActionMode(copyModeCallBack);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
			isRenameActionMode = false;
		}
	}

	@Override
	public void startBackPackActionMode() {
		Log.d("TAG", "startBackPackActionMode");
		if (actionMode == null) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
			actionMode = getSherlockActivity().startActionMode(backPackModeCallBack);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
			isRenameActionMode = false;
		}
	}

	@Override
	public void startRenameActionMode() {
		if (actionMode == null) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
			actionMode = getSherlockActivity().startActionMode(renameModeCallBack);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
			isRenameActionMode = true;
		}
	}

	@Override
	public void startDeleteActionMode() {
		if (actionMode == null) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
			isRenameActionMode = false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		isAddNewSoundButtonClicked = false;
		//when new sound title is selected and ready to be added to the catroid project
		if (resultCode == Activity.RESULT_OK && data != null) {
			switch (requestCode) {
				case SoundController.REQUEST_SELECT_MUSIC:
					Bundle arguments = new Bundle();
					arguments.putParcelable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, data.getData());

					if (getLoaderManager().getLoader(SoundController.ID_LOADER_MEDIA_IMAGE) == null) {
						getLoaderManager().initLoader(SoundController.ID_LOADER_MEDIA_IMAGE, arguments, this);
					} else {
						getLoaderManager().restartLoader(SoundController.ID_LOADER_MEDIA_IMAGE, arguments, this);
					}
					break;
				case SoundController.REQUEST_MEDIA_LIBRARY:
					String filePath = data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH);
					int sizeBeforeAdd = soundInfoList.size();
					SoundController.getInstance().addSoundFromMediaLibrary(filePath, getActivity(), soundInfoList, this);
					if (soundInfoList.size() > sizeBeforeAdd) {
						ArrayList<SoundInfo> soundInfos = new ArrayList<>();
						soundInfos.add(soundInfoList.get(soundInfoList.size() - 1));
						SoundCommands.AddSoundCommand command = new SoundCommands.AddSoundCommand(soundInfos, mediaPlayer, adapter);
						getHistory().add(command);
						getSherlockActivity().invalidateOptionsMenu();
					}
			}
		}
		if (requestCode == SoundController.REQUEST_SELECT_MUSIC) {
			Log.d("SoundFragment", "onActivityResult RequestMusic");
			setHandleAddbutton();
		}

		if (resultCode == Activity.RESULT_CANCELED && ProjectManager.getInstance()
				.getComingFromScriptFragmentToSoundFragment()) {

			getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			ProjectManager.getInstance().setHandleCorrectAddButton(true);

			ImageButton addButton = (ImageButton) getSherlockActivity().findViewById(R.id.button_add);
			addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					ScriptActivity scriptActivity = (ScriptActivity) getActivity();
					scriptActivity.getScriptFragment().handleAddButton();
				}
			});
			SoundController.getInstance().switchToScriptFragment(SoundFragment.this);
		}
		ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(false);
	}

	@Override
	public void onSoundPlay(View view) {
		SoundController.getInstance().handlePlaySoundButton(view, soundInfoList, mediaPlayer, adapter);
	}

	@Override
	public void onSoundPause(View view) {
		handlePauseSoundButton(view);
	}

	@Override
	public void onSoundChecked() {
		if (isRenameActionMode || actionMode == null) {
			return;
		}

		updateActionModeTitle();
		Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(actionModeTitle);
		} else {
			String appendix = multipleItemAppendixDeleteActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixDeleteActionMode;
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		return SoundController.getInstance().onCreateLoader(id, arguments, getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		CopyAudioFilesTask task = new CopyAudioFilesTask();
		String audioPath = SoundController.getInstance().onLoadFinished(loader, data, getActivity());
		if (!audioPath.isEmpty()) {
			task.execute(audioPath);
			getLoaderManager().destroyLoader(SoundController.ID_LOADER_MEDIA_IMAGE);
		}

		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));

		isResultHandled = true;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	public void handlePauseSoundButton(View view) {
		final int position = (Integer) view.getTag();
		pauseSound(soundInfoList.get(position));
		adapter.notifyDataSetChanged();
	}

	public void pauseSound(SoundInfo soundInfo) {
		mediaPlayer.pause();
		soundInfo.isPlaying = false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		if (SoundController.getInstance().isSoundPlaying(mediaPlayer)) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
		}
		selectedSoundInfo = adapter.getItem(selectedSoundPosition);
		menu.setHeaderTitle(selectedSoundInfo.getTitle());
		adapter.addCheckedItem(((AdapterContextMenuInfo) menuInfo).position);

		getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_default, menu);
		menu.findItem(R.id.context_menu_copy).setVisible(true);
		menu.findItem(R.id.context_menu_unpacking).setVisible(false);
		menu.findItem(R.id.context_menu_move_up).setVisible(true);
		menu.findItem(R.id.context_menu_move_down).setVisible(true);
		menu.findItem(R.id.context_menu_move_to_top).setVisible(true);
		menu.findItem(R.id.context_menu_move_to_bottom).setVisible(true);

		menu.findItem(R.id.context_menu_move_down).setEnabled(selectedSoundPosition != soundInfoList.size() - 1);
		menu.findItem(R.id.context_menu_move_to_bottom).setEnabled(selectedSoundPosition != soundInfoList.size() - 1);

		menu.findItem(R.id.context_menu_move_up).setEnabled(selectedSoundPosition != 0);
		menu.findItem(R.id.context_menu_move_to_top).setEnabled(selectedSoundPosition != 0);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.context_menu_backpack:
				Intent intent = new Intent(getActivity(), BackPackActivity.class);
				intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, 2);
				intent.putExtra(BackPackActivity.BACKPACK_ITEM, true);
				BackPackListManager.setCurrentSoundInfo(selectedSoundInfo);
				BackPackListManager.getInstance().addSoundToActionBarSoundInfoArrayList(selectedSoundInfo);
				startActivity(intent);
				break;

			case R.id.context_menu_copy:
				int sizeBeforeCopy = soundInfoList.size();
				SoundInfo newSoundInfo = SoundController.getInstance().copySound(selectedSoundInfo, soundInfoList,
						adapter);
				if (soundInfoList.size() > sizeBeforeCopy) {
					ArrayList<SoundInfo> soundInfos = new ArrayList<>();
					soundInfos.add(soundInfoList.get(soundInfoList.size() - 1));
					SoundCommands.AddSoundCommand command = new SoundCommands.AddSoundCommand(soundInfos, mediaPlayer, adapter);
					getHistory().add(command);
					getSherlockActivity().invalidateOptionsMenu();
				}
				updateSoundAdapter(newSoundInfo);

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
				deleteSounds();
				break;
			case R.id.context_menu_move_down:
				moveSoundDown();
				break;
			case R.id.context_menu_move_to_bottom:
				moveSoundToBottom();
				break;
			case R.id.context_menu_move_up:
				moveSoundUp();
				break;
			case R.id.context_menu_move_to_top:
				moveSoundToTop();
				break;
		}
		return super.onContextItemSelected(item);
	}

	public void updateSoundAdapter(SoundInfo newSoundInfo) {

		if (soundInfoListChangedAfterNewListener != null) {
			soundInfoListChangedAfterNewListener.onSoundInfoListChangedAfterNew(newSoundInfo);
		}

		//scroll down the list to the new item:
		final ListView listView = getListView();
		listView.post(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});

		if (isResultHandled) {
			isResultHandled = false;

			ScriptActivity scriptActivity = (ScriptActivity) getActivity();
			if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()
					&& scriptActivity.getIsSoundFragmentHandleAddButtonHandled()) {
				SoundController.getInstance().switchToScriptFragment(this);
			}
		}
	}

	@Override
	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		// TODO CHANGE THIS!!! (was just a quick fix)
		if (adapter != null) {
			adapter.setShowDetails(showDetails);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean getShowDetails() {
		// TODO CHANGE THIS!!! (was just a quick fix)
		return adapter != null && adapter.getShowDetails();
	}

	@Override
	public void handleAddButton() {
		NewSoundDialog dialog = NewSoundDialog.newInstance();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (ProjectManager.getInstance().getComingFromScriptFragmentToSoundFragment() && !isAddNewSoundButtonClicked) {
					ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(false);
					getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
					SoundController.getInstance().switchToScriptFragment(SoundFragment.this);
				}
			}
		});
		dialog.showDialog(this);
	}

	public void addSoundRecord() {
		isAddNewSoundButtonClicked = true;
		Intent intent = new Intent(getActivity(), SoundRecorderActivity.class);
		startActivityForResult(intent, SoundController.REQUEST_SELECT_OR_RECORD_SOUND);
	}

	public void addSoundChooseFile() {
		isAddNewSoundButtonClicked = true;
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			disableGoogleDrive(intent);
		}
		startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
				SoundController.REQUEST_SELECT_MUSIC);
	}

	public void addSoundMediaLibrary() {
		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		String url = null;
		url = Constants.LIBRARY_SOUNDS_URL;
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		intent.putExtra(WebViewActivity.CALLING_ACTIVITY, TAG);
		startActivityForResult(intent, SoundController.REQUEST_MEDIA_LIBRARY);
	}

	@TargetApi(19)
	private void disableGoogleDrive(Intent intent) {
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	}

	@Override
	public void showRenameDialog() {
		RenameSoundDialog renameSoundDialog = RenameSoundDialog.newInstance(selectedSoundInfo.getTitle());
		renameSoundDialog.show(getFragmentManager(), RenameSoundDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	protected void showDeleteDialog() {
		DeleteSoundDialog deleteSoundDialog = DeleteSoundDialog.newInstance(selectedSoundPosition);
		deleteSoundDialog.show(getFragmentManager(), DeleteSoundDialog.DIALOG_FRAGMENT_TAG);
	}

	private class SoundRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SOUND_RENAMED)) {
				String newSoundTitle = intent.getExtras().getString(RenameSoundDialog.EXTRA_NEW_SOUND_TITLE);

				if (newSoundTitle != null && !newSoundTitle.equalsIgnoreCase("")) {
					SoundCommands.RenameSoundCommand command = new SoundCommands.RenameSoundCommand(selectedSoundInfo, newSoundTitle);
					command.execute();
					getHistory().add(command);
					getSherlockActivity().invalidateOptionsMenu();
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	private class SoundDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SOUND_DELETED)) {
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}

	private class SoundCopiedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(ScriptActivity.ACTION_SOUND_COPIED)) {
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getLayoutInflater(null), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				for (int position = 0; position < soundInfoList.size(); position++) {
					adapter.addCheckedItem(position);
				}
				adapter.notifyDataSetChanged();
				onSoundChecked();
			}
		});
	}

	private ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_SINGLE);
			mode.setTitle(R.string.rename);

			setActionModeActive(true);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			((SoundAdapter) adapter).onDestroyActionModeRename(mode, listView);
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
			singleItemAppendixDeleteActionMode = getString(R.string.category_sound);
			multipleItemAppendixDeleteActionMode = getString(R.string.sounds);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			((SoundAdapter) adapter).onDestroyActionModeCopy(mode);
			getSherlockActivity().invalidateOptionsMenu();
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

			actionModeTitle = getString(R.string.backpack);
			singleItemAppendixDeleteActionMode = getString(R.string.category_sound);
			multipleItemAppendixDeleteActionMode = getString(R.string.sounds);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			((SoundAdapter) adapter).onDestroyActionModeBackPack(mode);
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
			setActionModeActive(true);

			actionModeTitle = getString(R.string.delete);
			singleItemAppendixDeleteActionMode = getString(R.string.category_sound);
			multipleItemAppendixDeleteActionMode = getString(R.string.sounds);

			mode.setTitle(R.string.delete);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedSoundsAndEnableButtons();
			} else {
				deleteSounds();
			}
		}
	};

	private void initClickListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedSoundPosition = position;
				return false;
			}
		});
	}

	private void deleteSounds() {
		SoundCommands.DeleteSoundCommand command = new SoundCommands.DeleteSoundCommand(adapter, mediaPlayer);
		command.execute();
		getHistory().add(command);
		getSherlockActivity().invalidateOptionsMenu();
		clearCheckedSoundsAndEnableButtons();
	}

	public void clearCheckedSoundsAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.showBottomBar(getActivity());
	}

	public MediaPlayer getPlayer() {
		return mediaPlayer;
	}

	public SoundBaseAdapter getAdapter() {
		return adapter;
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				ScriptActivity scriptActivity = (ScriptActivity) getActivity();
				if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()) {
					SoundController.getInstance().switchToScriptFragment(this);

					return true;
				}
			default:
				break;
		}
		return false;
	}

	public View getView(int position, View convertView) {
		SoundViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(getActivity(), R.layout.fragment_sound_soundlist_item, null);

			holder = new SoundViewHolder();
			holder.playAndStopButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_image_button);
			holder.playAndStopButton.setImageResource(R.drawable.ic_media_play);
			holder.playAndStopButton.setContentDescription(getString(R.string.sound_play));

			holder.soundFragmentButtonLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_sound_item_main_linear_layout);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_sound_item_checkbox);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_title_text_view);
			holder.timeSeparatorTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_time_seperator_text_view);
			holder.soundFileSizePrefixTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_size_prefix_text_view);
			holder.soundFileSizeTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_size_text_view);

			holder.timePlayedChronometer = (Chronometer) convertView
					.findViewById(R.id.fragment_sound_item_time_played_chronometer);

			convertView.setTag(holder);
		} else {
			holder = (SoundViewHolder) convertView.getTag();
		}
		SoundController controller = SoundController.getInstance();
		controller.updateSoundLogic(getActivity(), position, holder, adapter);

		return convertView;
	}

	public interface OnSoundInfoListChangedAfterNewListener {

		void onSoundInfoListChangedAfterNew(SoundInfo soundInfo);
	}

	public class CopyAudioFilesTask extends AsyncTask<String, Void, File> {
		private ProgressDialog progressDialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle(R.string.loading);
			progressDialog.show();
		}

		@Override
		protected File doInBackground(String... path) {
			File file = null;
			try {
				file = StorageHandler.getInstance().copySoundFile(path[0]);
			} catch (IOException e) {
				Log.e("CATROID", "Cannot load sound.", e);
			}
			return file;
		}

		@Override
		protected void onPostExecute(File file) {
			progressDialog.dismiss();

			if (file != null) {
				String fileName = file.getName();
				String soundTitle = fileName.substring(fileName.indexOf('_') + 1, fileName.lastIndexOf('.'));
				SoundInfo newSoundInfo = SoundController.getInstance().updateSoundAdapter(soundTitle, fileName,
						soundInfoList, adapter);
				ArrayList<SoundInfo> sounds = new ArrayList<>();
				sounds.add(newSoundInfo);
				SoundCommands.AddSoundCommand command = new SoundCommands.AddSoundCommand(sounds, mediaPlayer, adapter);
				getHistory().add(command);
				getSherlockActivity().invalidateOptionsMenu();

				if (soundInfoListChangedAfterNewListener != null) {
					soundInfoListChangedAfterNewListener.onSoundInfoListChangedAfterNew(newSoundInfo);
				}

				//scroll down the list to the new item:
				final ListView listView = getListView();
				listView.post(new Runnable() {
					@Override
					public void run() {
						listView.setSelection(listView.getCount() - 1);
					}
				});

				if (isResultHandled) {
					isResultHandled = false;

					ScriptActivity scriptActivity = (ScriptActivity) getActivity();
					if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()
							&& scriptActivity.getIsSoundFragmentHandleAddButtonHandled()) {
						SoundController.getInstance().switchToScriptFragment(SoundFragment.this);
					}
				}
			} else {
				Utils.showErrorDialog(getActivity(), R.string.error_load_sound);
			}
		}
	}

	public void setSelectedSoundInfo(SoundInfo selectedSoundInfo) {
		this.selectedSoundInfo = selectedSoundInfo;
	}

	public ArrayList<SoundInfo> getSoundInfoList() {
		return soundInfoList;
	}

	private class SoundsListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SOUNDS_LIST_INIT)) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	public void startUndoActionMode() {
		getHistory().undo();
		getSherlockActivity().invalidateOptionsMenu();
		adapter.notifyDataSetChanged();
	}

	public void startRedoActionMode() {
		getHistory().redo();
		getSherlockActivity().invalidateOptionsMenu();
		adapter.notifyDataSetChanged();
	}

	private void moveSoundDown() {
		SoundCommands.MoveSoundCommand command = new SoundCommands.MoveSoundCommand(selectedSoundPosition + 1, selectedSoundPosition);
		command.execute();
		adapter.notifyDataSetChanged();
		getHistory().add(command);
		getSherlockActivity().invalidateOptionsMenu();
	}

	private void moveSoundToBottom() {
		SoundCommands.MoveSoundToBottomCommand command = new SoundCommands.MoveSoundToBottomCommand(selectedSoundPosition);
		command.execute();
		adapter.notifyDataSetChanged();
		getHistory().add(command);
		getSherlockActivity().invalidateOptionsMenu();
	}

	private void moveSoundUp() {
		SoundCommands.MoveSoundCommand command = new SoundCommands.MoveSoundCommand(selectedSoundPosition - 1, selectedSoundPosition);
		command.execute();
		adapter.notifyDataSetChanged();
		getHistory().add(command);
		getSherlockActivity().invalidateOptionsMenu();
	}

	private void moveSoundToTop() {
		SoundCommands.MoveSoundToTopCommand command = new SoundCommands.MoveSoundToTopCommand(selectedSoundPosition);
		command.execute();
		adapter.notifyDataSetChanged();
		getHistory().add(command);
		getSherlockActivity().invalidateOptionsMenu();
	}

	private SoundInfoHistory getHistory() {
		return SoundInfoHistory.getInstance(ProjectManager.getInstance().getCurrentSprite().getId());
	}

	private void updatePlayersInCommands(MediaPlayer player) {
		for (Command command : getHistory().getUndoStack()) {
			if (command instanceof SoundCommands.DeleteSoundCommand) {
				((SoundCommands.DeleteSoundCommand) command).updatePlayer(player);
			}
			if (command instanceof SoundCommands.AddSoundCommand) {
				((SoundCommands.AddSoundCommand) command).updateMediaPlayer(player);
			}
		}

		for (Command command : getHistory().getRedoStack()) {
			if (command instanceof SoundCommands.DeleteSoundCommand) {
				((SoundCommands.DeleteSoundCommand) command).updatePlayer(player);
			}
			if (command instanceof SoundCommands.AddSoundCommand) {
				((SoundCommands.AddSoundCommand) command).updateMediaPlayer(player);
			}
		}
	}
}
