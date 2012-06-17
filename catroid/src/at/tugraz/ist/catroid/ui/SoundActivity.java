/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.SoundAdapter;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class SoundActivity extends SherlockListFragment {
	
	private static final String TAG = SoundActivity.class.getSimpleName();

	public MediaPlayer mediaPlayer;
	private SoundAdapter adapter;
	private ArrayList<SoundInfo> soundInfoList;
	
	private SoundDeletedReceiver soundDeletedReceiver;

	private final int REQUEST_SELECT_MUSIC = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_sound, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		adapter = new SoundAdapter(getActivity(), R.layout.activity_sound_soundlist_item, soundInfoList);
		setListAdapter(adapter);

		mediaPlayer = new MediaPlayer();
		setHasOptionsMenu(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		final MenuItem addItem = menu.findItem(R.id.menu_add);
		addItem.setIcon(R.drawable.ic_music);
		addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("audio/*");
				startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
						REQUEST_SELECT_MUSIC);

				return true;
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (!Utils.checkForSdCard(getActivity())) {
			return;
		}

		stopSound(null);
		reloadAdapter();
		
		if (soundDeletedReceiver == null) {
			soundDeletedReceiver = new SoundDeletedReceiver();
		}
		
		IntentFilter intentFilter = new IntentFilter(ScriptTabActivity.ACTION_SOUND_DELETED);
		getActivity().registerReceiver(soundDeletedReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}
		stopSound(null);
		
		if (soundDeletedReceiver != null) {
			getActivity().unregisterReceiver(soundDeletedReceiver);
		}
	}

	private void updateSoundAdapter(String title, String fileName) {

		title = Utils.getUniqueSoundName(title);

		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setTitle(title);
		newSoundInfo.setSoundFileName(fileName);
		soundInfoList.add(newSoundInfo);
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();

		//scroll down the list to the new item:
		{
			final ListView listView = getListView();
			listView.post(new Runnable() {
				public void run() {
					listView.setSelection(listView.getCount() - 1);
				}
			});
		}
	}

	public void pauseSound(SoundInfo soundInfo) {
		mediaPlayer.pause();

		soundInfo.isPlaying = false;
		soundInfo.isPaused = true;
	}

	public void stopSound(SoundInfo exceptionSoundInfo) {
		if (exceptionSoundInfo != null && exceptionSoundInfo.isPaused) {
			return;
		}
		mediaPlayer.stop();

		for (SoundInfo soundInfo : soundInfoList) {
			soundInfo.isPlaying = false;
			soundInfo.isPaused = false;
		}
	}

	public void startSound(SoundInfo soundInfo) {
		soundInfo.isPlaying = true;
		if (soundInfo.isPaused) {
			soundInfo.isPaused = false;
			mediaPlayer.start();
			return;
		}
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(soundInfo.getAbsolutePath());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//when new sound title is selected and ready to be added to the catroid project
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_MUSIC) {
			String audioPath = "";

			//copy music to catroid:
			try {

				//get real path of soundfile --------------------------
				{
					Uri audioUri = data.getData();
					String[] proj = { MediaStore.Audio.Media.DATA };
					Cursor actualSoundCursor = getActivity().managedQuery(audioUri, proj, null, null, null);

					if (actualSoundCursor != null) {
						int actualSoundColumnIndex = actualSoundCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
						actualSoundCursor.moveToFirst();
						audioPath = actualSoundCursor.getString(actualSoundColumnIndex);
					} else {
						audioPath = audioUri.getPath();
					}
					Log.i(TAG, "audiopath: " + audioPath);
				}
				//-----------------------------------------------------

				if (audioPath.equalsIgnoreCase("")) {
					throw new IOException();
				}
				File soundFile = StorageHandler.getInstance().copySoundFile(audioPath);
				String soundFileName = soundFile.getName();
				String soundTitle = soundFileName.substring(soundFileName.indexOf('_') + 1,
						soundFileName.lastIndexOf('.'));
				updateSoundAdapter(soundTitle, soundFileName);
			} catch (Exception e) {
				e.printStackTrace();
				Utils.displayErrorMessage(getActivity(), getActivity().getString(R.string.error_load_sound));
			}
		}
	}

	private void reloadAdapter() {
		this.soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		setListAdapter(new SoundAdapter(getActivity(), R.layout.activity_sound_soundlist_item, soundInfoList));
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

	// Does not rename the actual file, only the title in the SoundInfo
	public void handleSoundRenameButton(View v) {
		int position = (Integer) v.getTag();
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getActivity();
		scriptTabActivity.selectedSoundInfo = soundInfoList.get(position);
		scriptTabActivity.showDialog(ScriptTabActivity.DIALOG_RENAME_SOUND);
	}

	public void handlePlaySoundButton(View v) {
		final int position = (Integer) v.getTag();
		final SoundInfo soundInfo = soundInfoList.get(position);

		stopSound(soundInfo);
		startSound(soundInfo);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				soundInfo.isPlaying = false;
				soundInfo.isPaused = false;
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
		stopSound(null);
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getActivity();
		scriptTabActivity.selectedSoundInfo = soundInfoList.get(position);
		scriptTabActivity.selectedPosition = position;
		scriptTabActivity.showDialog(ScriptTabActivity.DIALOG_DELETE_SOUND);

	}
	
	private class SoundDeletedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptTabActivity.ACTION_SOUND_DELETED)) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
