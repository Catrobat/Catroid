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
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.SoundAdapter;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class SoundActivity extends ListActivity {
	private static final String TAG = SoundActivity.class.getSimpleName();

	public MediaPlayer mediaPlayer;
	private ArrayList<SoundInfo> soundInfoList;

	private final int REQUEST_SELECT_MUSIC = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sound);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		setListAdapter(new SoundAdapter(this, R.layout.activity_sound_soundlist_item, soundInfoList));

		mediaPlayer = new MediaPlayer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}

		stopSound(null);
		reloadAdapter();

		//change actionbar:
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		ActivityHelper activityHelper = scriptTabActivity.activityHelper;
		if (activityHelper != null) {
			//set new functionality for actionbar add button:
			activityHelper.changeClickListener(R.id.btn_action_add_button, createAddSoundClickListener());
			//set new icon for actionbar plus button:
			activityHelper.changeButtonIcon(R.id.btn_action_add_button, R.drawable.ic_music);
		}

	}

	private View.OnClickListener createAddSoundClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("audio/*");
				startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
						REQUEST_SELECT_MUSIC);
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}
		stopSound(null);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
					Cursor actualSoundCursor = managedQuery(audioUri, proj, null, null, null);

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
				Utils.displayErrorMessage(this, this.getString(R.string.error_load_sound));
			}
		}
	}

	private void reloadAdapter() {
		this.soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		setListAdapter(new SoundAdapter(this, R.layout.activity_sound_soundlist_item, soundInfoList));
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

	// Does not rename the actual file, only the title in the SoundInfo
	public void handleSoundRenameButton(View v) {
		int position = (Integer) v.getTag();
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
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
		StorageHandler.getInstance().deleteFile(soundInfoList.get(position).getAbsolutePath());
		soundInfoList.remove(position);
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

}
