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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.common.TrackingConstants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SoundViewHolder;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.DeleteSoundDialog;
import org.catrobat.catroid.ui.dialogs.NewSoundDialog;
import org.catrobat.catroid.ui.dialogs.RenameSoundDialog;
import org.catrobat.catroid.ui.dynamiclistview.DynamicListView;
import org.catrobat.catroid.utils.DividerUtil;
import org.catrobat.catroid.utils.SnackBarUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.TrackingUtil;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class SoundFragment extends ScriptActivityFragment implements SoundBaseAdapter.OnSoundEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener, SoundController.OnBackpackSoundCompleteListener {

	public static final String TAG = SoundFragment.class.getSimpleName();

	private static int selectedSoundPosition = Constants.NO_POSITION;

	private static String actionModeTitle;

	private static String singleItemAppendixDeleteActionMode;
	private static String multipleItemAppendixDeleteActionMode;

	private MediaPlayer mediaPlayer;
	private SoundBaseAdapter adapter;

	private List<SoundInfo> soundInfoList;

	private SoundInfo selectedSoundInfo;

	private ListView listView;
	private Lock viewSwitchLock = new ViewSwitchLock();
	private SoundDeletedReceiver soundDeletedReceiver;
	private SoundRenamedReceiver soundRenamedReceiver;
	private SoundCopiedReceiver soundCopiedReceiver;

	private SoundsListInitReceiver soundsListInitReceiver;
	private SoundListTouchActionUpReceiver soundListTouchActionUpReceiver;

	private ActionMode actionMode;
	private View selectAllActionModeButton;

	private boolean isRenameActionMode;
	private boolean isResultHandled = false;
	private Activity activity;
	private String soundSource = null;
	private String soundName = null;

	private OnSoundInfoListChangedAfterNewListener soundInfoListChangedAfterNewListener;

	public void setOnSoundInfoListChangedAfterNewListener(OnSoundInfoListChangedAfterNewListener listener) {
		soundInfoListChangedAfterNewListener = listener;
	}

	private void setHandleAddButton() {
		ImageButton addButton = (ImageButton) getActivity().findViewById(R.id.button_add);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				handleAddButton();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SnackBarUtil.showHintSnackBar(getActivity(), R.string.hint_sounds);

		return inflater.inflate(R.layout.fragment_sounds, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
			soundInfoList = new ArrayList<>();
		}

		((DynamicListView) getListView()).setDataList(soundInfoList);

		adapter = new SoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item,
				R.id.fragment_sound_item_title_text_view, soundInfoList, false);

		adapter.setOnSoundEditListener(this);
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);
		((SoundAdapter) adapter).setSoundFragment(this);

		Utils.loadProjectIfNeeded(getActivity());
		//setHandleAddButton();

		// set adapter and soundInfoList for ev. unpacking
		BackPackListManager.getInstance().setCurrentSoundAdapter(adapter);

		DividerUtil.setDivider(getActivity(), listView);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(true);
		menu.findItem(R.id.unpacking).setVisible(false);
		menu.findItem(R.id.backpack).setVisible(true);
		if (BackPackListManager.getInstance().getAllBackPackedSounds().isEmpty()) {
			StorageHandler.getInstance().clearBackPackSoundDirectory();
		}

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

		if (soundListTouchActionUpReceiver == null) {
			soundListTouchActionUpReceiver = new SoundListTouchActionUpReceiver();
		}

		IntentFilter intentFilterRenameSound = new IntentFilter(ScriptActivity.ACTION_SOUND_RENAMED);
		getActivity().registerReceiver(soundRenamedReceiver, intentFilterRenameSound);

		IntentFilter intentFilterDeleteSound = new IntentFilter(ScriptActivity.ACTION_SOUND_DELETED);
		getActivity().registerReceiver(soundDeletedReceiver, intentFilterDeleteSound);

		IntentFilter intentFilterCopySound = new IntentFilter(ScriptActivity.ACTION_SOUND_COPIED);
		getActivity().registerReceiver(soundCopiedReceiver, intentFilterCopySound);

		IntentFilter intentFilterSoundsListInit = new IntentFilter(ScriptActivity.ACTION_SOUNDS_LIST_INIT);
		getActivity().registerReceiver(soundsListInitReceiver, intentFilterSoundsListInit);

		IntentFilter intentFilterSoundsListActionUp = new IntentFilter(ScriptActivity.ACTION_SOUND_TOUCH_ACTION_UP);
		getActivity().registerReceiver(soundListTouchActionUpReceiver, intentFilterSoundsListActionUp);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SoundController.SHARED_PREFERENCE_NAME, false));
		if (!this.isHidden()) {
			setHandleAddButton();
			handleAddButtonFromNew();
		}
		if (isResultHandled) {
			isResultHandled = false;

			ScriptActivity scriptActivity = (ScriptActivity) activity;
			if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()
					&& scriptActivity.getIsSoundFragmentHandleAddButtonHandled()) {
				SoundController.getInstance().switchToScriptFragment(SoundFragment.this, (ScriptActivity) activity);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			handleAddButtonFromNew();
		}
	}

	public void handleAddButtonFromNew() {
		ScriptActivity scriptActivity = (ScriptActivity) activity;
		if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()
				&& !scriptActivity.getIsSoundFragmentHandleAddButtonHandled()) {
			scriptActivity.setIsSoundFragmentHandleAddButtonHandled(true);
			adapter.notifyDataSetChanged();
			handleAddButton();
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

		if (soundListTouchActionUpReceiver != null) {
			getActivity().unregisterReceiver(soundListTouchActionUpReceiver);
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
		startActionMode(copyModeCallBack, false);
	}

	@Override
	public void startCommentOutActionMode() {
		// Comment out not supported
	}

	@Override
	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack, false);
	}

	@Override
	public void startRenameActionMode() {
		startActionMode(renameModeCallBack, true);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack, false);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback, boolean isRenameMode) {
		if (actionMode == null) {
			if (adapter.isEmpty()) {
				if (actionModeCallback.equals(copyModeCallBack)) {
					((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.copy));
				} else if (actionModeCallback.equals(deleteModeCallBack)) {
					((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
				} else if (actionModeCallback.equals(backPackModeCallBack)) {
					if (BackPackListManager.getInstance().getBackPackedSounds().isEmpty()) {
						((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.backpack));
					} else {
						openBackPack();
					}
				} else if (actionModeCallback.equals(renameModeCallBack)) {
					((ScriptActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.rename));
				}
			} else {
				SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
				actionMode = getActivity().startActionMode(actionModeCallback);
				unregisterForContextMenu(listView);
				BottomBar.hideBottomBar(getActivity());
				isRenameActionMode = isRenameMode;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
					if (soundInfoList.size() > 0) {
						soundName = soundInfoList.get(soundInfoList.size() - 1).getTitle();
					} else {
						Log.e(TAG, "Error: soundInfoList is empty! please fix.");
					}
					break;
				case SoundController.REQUEST_MEDIA_LIBRARY:
					soundSource = TrackingConstants.MEDIA_LIBRARY;
					String filePath = data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH);
					SoundController.getInstance().addSoundFromMediaLibrary(filePath, getActivity(), soundInfoList, this);
					SoundInfo soundInfo = soundInfoList.get(soundInfoList.size() - 1);
					soundName = soundInfo.getTitle();
					long lengthMilliseconds = SoundController.getInstance().getSoundFileLengthInMilliseconds(soundInfo);
					TrackingUtil.trackCreateSound(soundName, soundSource, lengthMilliseconds);
					break;
			}
			isResultHandled = true;
		}

		if (requestCode == SoundController.REQUEST_SELECT_MUSIC) {
			Log.d(TAG, "onActivityResult RequestMusic");
			setHandleAddButton();
		}

		if (resultCode == Activity.RESULT_CANCELED && ProjectManager.getInstance()
				.getComingFromScriptFragmentToSoundFragment()) {

			getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));

			ImageButton addButton = (ImageButton) getActivity().findViewById(R.id.button_add);
			addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					ScriptActivity scriptActivity = (ScriptActivity) activity;
					scriptActivity.getScriptFragment().handleAddButton();
				}
			});
			SoundController.getInstance().switchToScriptFragment(SoundFragment.this, (ScriptActivity) activity);
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
		UtilUi.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
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

		TextSizeUtil.enlargeActionMode(actionMode);
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

	private void openBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_SOUNDS);
		startActivity(intent);
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
	public boolean getShowDetails() {
		// TODO CHANGE THIS!!! (was just a quick fix)
		return adapter != null && adapter.getShowDetails();
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
	public void handleAddButton() {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		NewSoundDialog dialog = NewSoundDialog.newInstance();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (ProjectManager.getInstance().getComingFromScriptFragmentToSoundFragment()) {
					ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(false);
					activity.sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
					isResultHandled = true;
					SoundController.getInstance().switchToScriptFragment(SoundFragment.this, (ScriptActivity)
							activity);
				}
			}
		});
		dialog.showDialog(this);
	}

	public void addSoundRecord() {
		Intent intent = new Intent(getActivity(), SoundRecorderActivity.class);
		startActivityForResult(intent, SoundController.REQUEST_SELECT_OR_RECORD_SOUND);
		soundSource = TrackingConstants.RECORD;
	}

	public void addSoundChooseFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			disableGoogleDrive(intent);
		}
		startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
				SoundController.REQUEST_SELECT_MUSIC);
		soundSource = TrackingConstants.DEVICE;
		soundName = soundInfoList.get(soundInfoList.size() - 1).getTitle();
	}

	public void addSoundMediaLibrary() {
		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		String url = Utils.addUsernameAndTokenInfoToUrl(Constants.LIBRARY_SOUNDS_URL, getActivity());

		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		intent.putExtra(WebViewActivity.CALLING_ACTIVITY, TAG);
		startActivityForResult(intent, SoundController.REQUEST_MEDIA_LIBRARY);
	}

	public void addPocketMusic() {
		Intent intent = new Intent(getActivity(), PocketMusicActivity.class);
		startActivity(intent);
	}

	@TargetApi(19)
	private void disableGoogleDrive(Intent intent) {
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	}

	@Override
	public void showRenameDialog() {
		RenameSoundDialog renameSoundDialog = new RenameSoundDialog(R.string.rename_sound_dialog, R.string
				.sound_name, selectedSoundInfo.getTitle());
		renameSoundDialog.show(getFragmentManager(), RenameSoundDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showDeleteDialog() {
		DeleteSoundDialog deleteSoundDialog = DeleteSoundDialog.newInstance(selectedSoundPosition);
		deleteSoundDialog.show(getFragmentManager(), DeleteSoundDialog.DIALOG_FRAGMENT_TAG);
	}

	public void setSelectedSoundInfo(SoundInfo selectedSoundInfo) {
		this.selectedSoundInfo = selectedSoundInfo;
	}

	public List<SoundInfo> getSoundInfoList() {
		return soundInfoList;
	}

	@Override
	public void onBackpackSoundComplete(boolean startBackpackActivity) {
		openBackPack();
	}

	private class SoundRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SOUND_RENAMED)) {
				String newSoundTitle = intent.getExtras().getString(RenameSoundDialog.EXTRA_NEW_SOUND_TITLE);

				if (newSoundTitle != null && !newSoundTitle.equalsIgnoreCase("")) {
					selectedSoundInfo.setTitle(newSoundTitle);
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
		selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				for (int position = 0; position < soundInfoList.size(); position++) {
					adapter.addCheckedItem(position);
					adapter.notifyDataSetChanged();
				}
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

			TextSizeUtil.enlargeActionMode(mode);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
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

			TextSizeUtil.enlargeActionMode(mode);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			((SoundAdapter) adapter).onDestroyActionModeCopy(mode);
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

			TextSizeUtil.enlargeActionMode(mode);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			((SoundAdapter) adapter).onDestroyActionModeBackPack();
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

			TextSizeUtil.enlargeActionMode(mode);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedSoundsAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};

	private void showConfirmDeleteDialog() {
		int titleId;
		if (adapter.getAmountOfCheckedItems() == 1) {
			titleId = R.string.dialog_confirm_delete_sound_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_sounds_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_sound_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				adapter.addCheckedItemIfNotExists(selectedSoundPosition);
				SoundController.getInstance().deleteCheckedSounds(getActivity(), adapter, soundInfoList, mediaPlayer);
				adapter.notifyDataSetChanged();
				clearCheckedSoundsAndEnableButtons();
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
				clearCheckedSoundsAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void clearCheckedSoundsAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();
		adapter.notifyDataSetChanged();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.showBottomBar(getActivity());
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				ScriptActivity scriptActivity = (ScriptActivity) getActivity();
				if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()) {
					SoundController.getInstance().switchToScriptFragment(SoundFragment.this, (ScriptActivity) activity);

					return true;
				}
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

			TextSizeUtil.enlargeViewGroup(holder.soundFragmentButtonLayout);

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
				Log.e(TAG, "Cannot load sound.", e);
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
						soundInfoList, SoundFragment.this);

				long lengthMilliseconds = SoundController.getInstance().getSoundFileLengthInMilliseconds(newSoundInfo);
				TrackingUtil.trackCreateSound(newSoundInfo.getTitle(), soundSource, lengthMilliseconds);

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
						SoundController.getInstance().switchToScriptFragment(SoundFragment.this, (ScriptActivity) activity);
					}
				}
			} else {
				Utils.showErrorDialog(getActivity(), R.string.error_load_sound);
			}
		}
	}

	private class SoundsListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SOUNDS_LIST_INIT)) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	private class SoundListTouchActionUpReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SOUND_TOUCH_ACTION_UP)) {
				((DynamicListView) getListView()).notifyListItemTouchActionUp();
			}
		}
	}

	@Override
	public void handleCheckBoxClick(View view) {
	}

	@Override
	public void onSoundEdit(View view) {

		if (!BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
			return;
		}

		int position = getListView().getPositionForView(view);

		selectedSoundInfo = soundInfoList.get(position);

		if (selectedSoundInfo.getSoundFileName().matches(".*MUS-.*\\.midi")) {
			Intent intent = new Intent(getActivity(), PocketMusicActivity.class);

			intent.putExtra("FILENAME", selectedSoundInfo.getSoundFileName());
			intent.putExtra("TITLE", selectedSoundInfo.getTitle());

			startActivity(intent);
		}
	}
}
