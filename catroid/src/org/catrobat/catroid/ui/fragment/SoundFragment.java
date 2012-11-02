/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.SoundActivity;
import org.catrobat.catroid.ui.adapter.IconMenuAdapter;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.adapter.SoundAdapter.OnSoundEditListener;
import org.catrobat.catroid.ui.dialogs.CustomIconContextMenu;
import org.catrobat.catroid.ui.dialogs.DeleteSoundDialog;
import org.catrobat.catroid.ui.dialogs.RenameSoundDialog;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog;
import org.catrobat.catroid.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class SoundFragment extends SherlockListFragment implements OnSoundEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, OnClickListener {

	private class CopyAudioFilesTask extends AsyncTask<String, Void, File> {
		private ProgressDialog progressDialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle(getString(R.string.loading));
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
				updateSoundAdapter(soundTitle, fileName);
			} else {
				Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
						getString(R.string.error_load_sound));
			}
		}
	}

	private static final String BUNDLE_ARGUMENTS_SELECTED_SOUND = "selected_sound";
	private static final int ID_LOADER_MEDIA_IMAGE = 1;
	private static final int FOOTER_ADD_SOUND_ALPHA_VALUE = 35;

	private static final int NO_DIALOG_FRAGMENT_ACTIVE = -1;
	private static final int CONTEXT_MENU_ITEM_RENAME = 0;
	private static final int CONTEXT_MENU_ITEM_DESCRIPTION = 1;
	private static final int CONTEXT_MENU_ITEM_DELETE = 2;
	private static final int CONTEXT_MENU_ITEM_COPY = 3;

	private int activeDialogId = NO_DIALOG_FRAGMENT_ACTIVE;

	public static final int REQUEST_SELECT_MUSIC = 0;

	private MediaPlayer mediaPlayer;
	private SoundAdapter adapter;
	private ArrayList<SoundInfo> soundInfoList;

	private SoundInfo selectedSoundInfo;

	private View viewBelowSoundlistNonScrollable;
	private View soundlistFooterView;

	private SoundDeletedReceiver soundDeletedReceiver;
	private SoundRenamedReceiver soundRenamedReceiver;

	private int currentSoundPosition = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sound, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			selectedSoundInfo = (SoundInfo) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}

		viewBelowSoundlistNonScrollable = getActivity().findViewById(R.id.view_below_soundlist_non_scrollable);
		viewBelowSoundlistNonScrollable.setOnClickListener(this);

		View footerView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sound_soundlist_footer,
				getListView(), false);
		soundlistFooterView = footerView.findViewById(R.id.soundlist_footerview);
		ImageView footerAddImage = (ImageView) footerView.findViewById(R.id.soundlist_footerview_add_image);
		footerAddImage.setAlpha(FOOTER_ADD_SOUND_ALPHA_VALUE);
		soundlistFooterView.setOnClickListener(this);
		getListView().addFooterView(footerView);

		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		adapter = new SoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item, soundInfoList);
		adapter.setOnSoundEditListener(this);
		setListAdapter(adapter);

		checkForCanceledFragment();
		//reattachDialogFragmentListener();
		initClickListener();
	}

	private void checkForCanceledFragment() {
		boolean canceledDialog = false;

		if (getFragmentManager().findFragmentByTag(RenameSoundDialog.DIALOG_FRAGMENT_TAG) == null
				&& activeDialogId == CONTEXT_MENU_ITEM_RENAME) {
			canceledDialog = true;
		} else if (getFragmentManager().findFragmentByTag(SetDescriptionDialog.DIALOG_FRAGMENT_TAG) == null
				&& activeDialogId == CONTEXT_MENU_ITEM_DESCRIPTION) {
			canceledDialog = true;
		}
		//		else if (getFragmentManager().findFragmentByTag(CopySoundDialog.DIALOG_FRAGMENT_TAG) == null
		//				&& activeDialogId == CONTEXT_MENU_ITEM_COPY) {
		//			canceledDialog = true;
		//		}
		if (canceledDialog) {
			activeDialogId = NO_DIALOG_FRAGMENT_ACTIVE;
		}
	}

	//TODO USE RECEIVER INSTEAD!!!!
	//	private void reattachDialogFragmentListener() {
	//		Fragment activeFragmentDialog;
	//
	//		if (activeDialogId != NO_DIALOG_FRAGMENT_ACTIVE) {
	//			switch (activeDialogId) {
	//				case CONTEXT_MENU_ITEM_RENAME:
	//					activeFragmentDialog = getFragmentManager()
	//							.findFragmentByTag(RenameSoundDialog.DIALOG_FRAGMENT_TAG);
	//					RenameSoundDialog displayingRenameSoundDialog = (RenameSoundDialog) activeFragmentDialog;
	//					//displayingRenameSoundDialog.setOnProjectRenameListener(SoundFragment.this);
	//					break;
	//				case CONTEXT_MENU_ITEM_DESCRIPTION:
	//					activeFragmentDialog = getFragmentManager().findFragmentByTag(
	//							SetDescriptionDialog.DIALOG_FRAGMENT_TAG);
	//					SetDescriptionDialog displayingSetDescriptionSoundDialog = (SetDescriptionDialog) activeFragmentDialog;
	//					//displayingSetDescriptionSoundDialog.setOnUpdateSoundDescriptionListener(SoundFragment.this);
	//					break;
	//			//				case CONTEXT_MENU_ITEM_COPY:
	//			//					activeFragmentDialog = getFragmentManager()
	//			//							.findFragmentByTag(CopyProjectDialog.DIALOG_FRAGMENT_TAG);
	//			//					CopyProjectDialog displayingCopyProjectDialog = (CopyProjectDialog) activeFragmentDialog;
	//			//					displayingCopyProjectDialog.setParentFragment(this);
	//			//					break;
	//			}
	//		}
	//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SELECTED_SOUND, selectedSoundInfo);
		super.onSaveInstanceState(outState);
	}

	public void startSelectSoundIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");

		Log.d("Catroid", "SoundFragmend: startSelectSoundIntent()");

		startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
				REQUEST_SELECT_MUSIC);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForSdCard(getActivity())) {
			return;
		}

		if (soundDeletedReceiver == null) {
			soundDeletedReceiver = new SoundDeletedReceiver();
		}

		if (soundRenamedReceiver == null) {
			soundRenamedReceiver = new SoundRenamedReceiver();
		}

		IntentFilter intentFilterDeleteSound = new IntentFilter(SoundActivity.ACTION_SOUND_DELETED);
		getActivity().registerReceiver(soundDeletedReceiver, intentFilterDeleteSound);

		IntentFilter intentFilterRenameSound = new IntentFilter(SoundActivity.ACTION_SOUND_RENAMED);
		getActivity().registerReceiver(soundRenamedReceiver, intentFilterRenameSound);

		stopSound();
		reloadAdapter();
		addSoundViewsSetClickableFlag(true);
	}

	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}
		stopSound();

		if (soundDeletedReceiver != null) {
			getActivity().unregisterReceiver(soundDeletedReceiver);
		}

		if (soundRenamedReceiver != null) {
			getActivity().unregisterReceiver(soundRenamedReceiver);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mediaPlayer = new MediaPlayer();
	}

	@Override
	public void onStop() {
		super.onStop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mediaPlayer = null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("Catroid", "SoundFragmend: onActivityResult() OK:" + resultCode + " request:" + requestCode);

		super.onActivityResult(requestCode, resultCode, data);

		//when new sound title is selected and ready to be added to the catroid project
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_MUSIC && data != null) {
			Bundle arguments = new Bundle();
			arguments.putParcelable(BUNDLE_ARGUMENTS_SELECTED_SOUND, data.getData());

			if (getLoaderManager().getLoader(ID_LOADER_MEDIA_IMAGE) == null) {
				getLoaderManager().initLoader(ID_LOADER_MEDIA_IMAGE, arguments, this);
				Log.d("Catroid", "SoundFragmend: onActivityResult() -> INIT!");
			} else {
				getLoaderManager().restartLoader(ID_LOADER_MEDIA_IMAGE, arguments, this);
				Log.d("Catroid", "SoundFragmend: onActivityResult() -> RESTART!");
			}
		}
	}

	@Override
	public void onSoundRename(View v) {
		handleSoundRenameButton(v);
	}

	@Override
	public void onSoundPlay(View v) {
		handlePlaySoundButton(v);
	}

	@Override
	public void onSoundPause(View v) {
		handlePauseSoundButton(v);
	}

	@Override
	public void onSoundDelete(View v) {
		handleDeleteSoundButton(v);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		Uri audioUri = null;
		if (arguments != null) {
			audioUri = (Uri) arguments.get(BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}

		String[] projection = { MediaStore.Audio.Media.DATA };
		return new CursorLoader(getActivity(), audioUri, projection, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		String audioPath = "";
		CursorLoader cursorLoader = (CursorLoader) loader;

		if (data == null) {
			audioPath = cursorLoader.getUri().getPath();
		} else {
			data.moveToFirst();
			audioPath = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA));
		}

		if (audioPath.equalsIgnoreCase("")) {
			Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
					getString(R.string.error_load_sound));
		} else {
			new CopyAudioFilesTask().execute(audioPath);
		}

		getLoaderManager().destroyLoader(ID_LOADER_MEDIA_IMAGE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.view_below_soundlist_non_scrollable:
				addSoundViewsSetClickableFlag(false);
				startSelectSoundIntent();
				break;
			case R.id.soundlist_footerview:
				addSoundViewsSetClickableFlag(false);
				startSelectSoundIntent();
				break;
		}
	}

	private void addSoundViewsSetClickableFlag(boolean setClickableFlag) {
		viewBelowSoundlistNonScrollable.setClickable(setClickableFlag);
		soundlistFooterView.setClickable(setClickableFlag);
	}

	private void reloadAdapter() {
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		adapter = new SoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item, soundInfoList);
		adapter.setOnSoundEditListener(this);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void updateSoundAdapter(String title, String fileName) {
		title = Utils.getUniqueSoundName(title);

		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setTitle(title);
		newSoundInfo.setSoundFileName(fileName);
		soundInfoList.add(newSoundInfo);
		adapter.notifyDataSetChanged();

		//scroll down the list to the new item:
		{
			final ListView listView = getListView();
			listView.post(new Runnable() {
				@Override
				public void run() {
					listView.setSelection(listView.getCount() - 1);
				}
			});
		}
	}

	// Does not rename the actual file, only the title in the SoundInfo
	public void handleSoundRenameButton(View v) {
		int position = (Integer) v.getTag();
		selectedSoundInfo = soundInfoList.get(position);

		RenameSoundDialog renameSoundDialog = RenameSoundDialog.newInstance(selectedSoundInfo.getTitle());
		renameSoundDialog.show(getFragmentManager(), RenameSoundDialog.DIALOG_FRAGMENT_TAG);
	}

	public void handlePlaySoundButton(View v) {
		final int position = (Integer) v.getTag();
		final SoundInfo soundInfo = soundInfoList.get(position);

		stopSound();
		if (!soundInfo.isPlaying) {
			startSound(soundInfo);
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				soundInfo.isPlaying = false;
				((SoundAdapter) getListAdapter()).notifyDataSetChanged();
			}
		});

		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void handlePauseSoundButton(View v) {
		final int position = (Integer) v.getTag();
		pauseSound(soundInfoList.get(position));
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void handleDeleteSoundButton(View v) {
		final int position = (Integer) v.getTag();
		stopSound();
		selectedSoundInfo = soundInfoList.get(position);

		DeleteSoundDialog deleteSoundDialog = DeleteSoundDialog.newInstance(position);
		deleteSoundDialog.show(getFragmentManager(), DeleteSoundDialog.DIALOG_FRAGMENT_TAG);
	}

	public void pauseSound(SoundInfo soundInfo) {
		mediaPlayer.pause();
		soundInfo.isPlaying = false;
	}

	public void stopSound() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}

		for (int i = 0; i < soundInfoList.size(); i++) {
			soundInfoList.get(i).isPlaying = false;
		}
	}

	public void startSound(SoundInfo soundInfo) {
		if (!soundInfo.isPlaying) {
			try {
				mediaPlayer.reset();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(soundInfo.getAbsolutePath());
				mediaPlayer.prepare();
				mediaPlayer.start();

				soundInfo.isPlaying = true;
			} catch (IOException e) {
				Log.e("CATROID", "Cannot start sound.", e);
			}
		}
	}

	private void initClickListener() {
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedSoundInfo = soundInfoList.get(position);
				currentSoundPosition = position;

				if (selectedSoundInfo != null) {
					showEditSoundContextDialog();
				}

				return true;
			}
		});
	}

	private void showEditSoundContextDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag(CustomIconContextMenu.DIALOG_FRAGMENT_TAG);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		CustomIconContextMenu dialog = CustomIconContextMenu.newInstance(selectedSoundInfo.getTitle());
		initCustomContextMenu(dialog);
		dialog.show(getFragmentManager(), CustomIconContextMenu.DIALOG_FRAGMENT_TAG);
	}

	private void initCustomContextMenu(CustomIconContextMenu iconContextMenu) {
		Resources resources = getResources();

		IconMenuAdapter adapter = new IconMenuAdapter(getActivity());
		adapter.addItem(resources, this.getString(R.string.copy), R.drawable.ic_context_copy, CONTEXT_MENU_ITEM_COPY);
		//		adapter.addItem(resources, this.getString(R.string.cut), R.drawable.ic_context_cut,
		//				CONTEXT_MENU_ITEM_CUT);
		//		adapter.addItem(resources, this.getString(R.string.insert_below), R.drawable.ic_context_insert_below,
		//				CONTEXT_MENU_ITEM_INSERT_BELOW);
		//		adapter.addItem(resources, this.getString(R.string.move), R.drawable.ic_context_move,
		//				CONTEXT_MENU_ITEM_MOVE);
		adapter.addItem(resources, this.getString(R.string.rename), R.drawable.ic_context_rename,
				CONTEXT_MENU_ITEM_RENAME);
		adapter.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu.setAdapter(adapter);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) {
				activeDialogId = menuId;
				switch (menuId) {
					case CONTEXT_MENU_ITEM_COPY:
						//						CopyProjectDialog dialogCopyProject = CopyProjectDialog.newInstance(projectToEdit.projectName);
						//						dialogCopyProject.setParentFragment(parentFragment);
						//						dialogCopyProject.show(getActivity().getSupportFragmentManager(),
						//								CopyProjectDialog.DIALOG_FRAGMENT_TAG);
						break;
					case CONTEXT_MENU_ITEM_RENAME:
						RenameSoundDialog dialogRenameSound = RenameSoundDialog.newInstance(selectedSoundInfo
								.getTitle());
						dialogRenameSound.show(getActivity().getSupportFragmentManager(),
								RenameSoundDialog.DIALOG_FRAGMENT_TAG);
						break;
					case CONTEXT_MENU_ITEM_DELETE:
						if (currentSoundPosition != -1) {
							DeleteSoundDialog deleteSoundDialog = DeleteSoundDialog.newInstance(currentSoundPosition);
							deleteSoundDialog.show(getActivity().getSupportFragmentManager(),
									DeleteSoundDialog.DIALOG_FRAGMENT_TAG);
						} else {
							//TODO ERROR-HANDLING
							Log.d("CATROID", "NO SOUND SELECTED!!!");
						}

						break;
				}
			}
		});
	}

	private class SoundDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SoundActivity.ACTION_SOUND_DELETED)) {
				reloadAdapter();
			}
		}
	}

	private class SoundRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SoundActivity.ACTION_SOUND_RENAMED)) {
				String newSoundTitle = intent.getExtras().getString(RenameSoundDialog.EXTRA_NEW_SOUND_TITLE);

				if (newSoundTitle != null && !newSoundTitle.equalsIgnoreCase("")) {
					selectedSoundInfo.setTitle(newSoundTitle);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
