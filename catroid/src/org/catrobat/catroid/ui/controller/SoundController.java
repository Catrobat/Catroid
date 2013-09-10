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
package org.catrobat.catroid.ui.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SoundViewHolder;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

public class SoundController {

	public static final String BUNDLE_ARGUMENTS_SELECTED_SOUND = "selected_sound";
	public static final String SHARED_PREFERENCE_NAME = "showDetailsSounds";
	public static final int ID_LOADER_MEDIA_IMAGE = 1;
	public static final int REQUEST_SELECT_MUSIC = 0;

	private static SoundController instance;

	public static SoundController getInstance() {
		if (instance == null) {
			instance = new SoundController();
		}

		return instance;
	}

	public void updateSoundLogic(final int position, SoundViewHolder holder, final SoundBaseAdapter soundAdapter) {
		Log.v("Adapter *********", ".........");
		final SoundInfo soundInfo = soundAdapter.getSoundInfoItems().get(position);

		if (soundInfo != null) {
			holder.getPlayButton().setTag(position);
			holder.getPauseButton().setTag(position);
			holder.getTitleTextView().setText(soundInfo.getTitle());

			holder.getCheckbox().setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						if (soundAdapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE) {
							soundAdapter.clearCheckedItems();
						}
						soundAdapter.getCheckedSounds().add(position);
					} else {
						soundAdapter.getCheckedSounds().remove(position);
					}
					soundAdapter.notifyDataSetChanged();

					if (soundAdapter.getOnSoundEditListener() != null) {
						soundAdapter.getOnSoundEditListener().onSoundChecked();
					}
				}
			});

			if (soundAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
				holder.getCheckbox().setVisibility(View.VISIBLE);
				holder.getCheckbox().setVisibility(View.VISIBLE);
				holder.getSoundFragmentButtonLayout().setBackgroundResource(R.drawable.button_background_shadowed);
			} else {
				holder.getCheckbox().setVisibility(View.GONE);
				holder.getCheckbox().setVisibility(View.GONE);
				holder.getSoundFragmentButtonLayout().setBackgroundResource(R.drawable.button_background_selector);
				holder.getCheckbox().setChecked(false);
				soundAdapter.clearCheckedItems();
			}

			if (soundAdapter.getCheckedSounds().contains(position)) {
				holder.getCheckbox().setChecked(true);
			} else {
				holder.getCheckbox().setChecked(false);
			}

			try {
				MediaPlayer tempPlayer = new MediaPlayer();
				tempPlayer.setDataSource(soundInfo.getAbsolutePath());
				tempPlayer.prepare();

				long milliseconds = tempPlayer.getDuration();
				int seconds = (int) ((milliseconds / 1000) % 60);
				int minutes = (int) ((milliseconds / 1000) / 60);
				int hours = (int) ((milliseconds / 1000) / 3600);

				if (hours == 0) {
					holder.getTimeDurationTextView().setText(String.format("%02d:%02d", minutes, seconds));
				} else {
					holder.getTimeDurationTextView().setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
				}

				if (soundAdapter.getCurrentPlayingPosition() == Constants.NO_POSITION) {

					SoundBaseAdapter.setElapsedMilliSeconds(0);
				} else {

					SoundBaseAdapter.setElapsedMilliSeconds(SystemClock.elapsedRealtime()
							- SoundBaseAdapter.getCurrentPlayingBase());
				}

				if (soundInfo.isPlaying) {
					holder.getPlayButton().setVisibility(Button.GONE);
					holder.getPauseButton().setVisibility(Button.VISIBLE);

					holder.getTimeSeperatorTextView().setVisibility(TextView.VISIBLE);
					holder.getTimePlayedChronometer().setVisibility(Chronometer.VISIBLE);

					if (soundAdapter.getCurrentPlayingPosition() == Constants.NO_POSITION) {
						startPlayingSound(holder.getTimePlayedChronometer(), position, soundAdapter);
					} else if ((position == soundAdapter.getCurrentPlayingPosition())
							&& (SoundBaseAdapter.getElapsedMilliSeconds() > (milliseconds - 1000))) {
						stopPlayingSound(soundInfo, holder.getTimePlayedChronometer(), soundAdapter);
					} else {
						continuePlayingSound(holder.getTimePlayedChronometer(), SystemClock.elapsedRealtime());
					}
				} else {
					holder.getPlayButton().setVisibility(Button.VISIBLE);
					holder.getPauseButton().setVisibility(Button.GONE);

					holder.getTimeSeperatorTextView().setVisibility(TextView.GONE);
					holder.getTimePlayedChronometer().setVisibility(Chronometer.GONE);

					if (position == soundAdapter.getCurrentPlayingPosition()) {
						stopPlayingSound(soundInfo, holder.getTimePlayedChronometer(), soundAdapter);
					}
				}

				if (soundAdapter.getShowDetails()) {
					holder.getSoundFileSizeTextView().setText(
							UtilFile.getSizeAsString(new File(soundInfo.getAbsolutePath())));
					holder.getSoundFileSizeTextView().setVisibility(TextView.VISIBLE);
					holder.getSoundFileSizePrefixTextView().setVisibility(TextView.VISIBLE);
				} else {
					holder.getSoundFileSizeTextView().setVisibility(TextView.GONE);
					holder.getSoundFileSizePrefixTextView().setVisibility(TextView.GONE);
				}

				tempPlayer.reset();
				tempPlayer.release();
			} catch (IOException e) {
				Log.e("CATROID", "Cannot get view.", e);
			}

			if (soundAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
				holder.getPlayButton().setOnClickListener(null);
				holder.getPauseButton().setOnClickListener(null);
			} else {
				holder.getPlayButton().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (soundAdapter.getOnSoundEditListener() != null) {
							soundAdapter.getOnSoundEditListener().onSoundPlay(view);
						}
					}
				});

				holder.getPauseButton().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (soundAdapter.getOnSoundEditListener() != null) {
							soundAdapter.getOnSoundEditListener().onSoundPause(view);
						}
					}
				});
			}
		}
	}

	private void startPlayingSound(Chronometer chronometer, int position, final SoundBaseAdapter soundAdapter) {
		soundAdapter.setCurrentPlayingPosition(position);

		SoundBaseAdapter.setCurrentPlayingBase(SystemClock.elapsedRealtime());

		continuePlayingSound(chronometer, SoundBaseAdapter.getCurrentPlayingBase());
	}

	private void continuePlayingSound(Chronometer chronometer, long base) {
		chronometer.setBase(base);
		chronometer.start();
	}

	private void stopPlayingSound(SoundInfo soundInfo, Chronometer chronometer, final SoundBaseAdapter soundAdapter) {
		chronometer.stop();
		chronometer.setBase(SystemClock.elapsedRealtime());

		soundAdapter.setCurrentPlayingPosition(Constants.NO_POSITION);

		soundInfo.isPlaying = false;
	}

	public void backPackSound(SoundInfo selectedSoundInfo, BackPackSoundFragment backPackSoundActivity,
			ArrayList<SoundInfo> soundInfoList, SoundBaseAdapter adapter) {

		/*
		 * Toast.makeText(backPackSoundActivity.getSherlockActivity(),
		 * "Sound " + selectedSoundInfo.getTitle() + " copied into backpack", Toast.LENGTH_SHORT).show();
		 */

		copySoundBackPack(selectedSoundInfo, soundInfoList, adapter);
	}

	private void copySoundBackPack(SoundInfo selectedSoundInfo, ArrayList<SoundInfo> soundInfoList,
			SoundBaseAdapter adapter) {

		try {
			StorageHandler.getInstance().copySoundFileBackPack(selectedSoundInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}

		updateBackPackActivity(selectedSoundInfo.getTitle(), selectedSoundInfo.getSoundFileName(), soundInfoList,
				adapter);
	}

	public SoundInfo copySound(SoundInfo selectedSoundInfo, ArrayList<SoundInfo> soundInfoList, SoundBaseAdapter adapter) {

		try {
			StorageHandler.getInstance().copySoundFile(selectedSoundInfo.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return updateSoundAdapter(selectedSoundInfo.getTitle(), selectedSoundInfo.getSoundFileName(), soundInfoList,
				adapter);
	}

	public void copySound(int position, ArrayList<SoundInfo> soundInfoList, SoundBaseAdapter adapter) {

		SoundInfo soundInfo = soundInfoList.get(position);

		try {
			StorageHandler.getInstance().copySoundFile(soundInfo.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		SoundController.getInstance().updateSoundAdapter(soundInfo.getTitle(), soundInfo.getSoundFileName(),
				soundInfoList, adapter);

	}

	private void deleteSound(int position, ArrayList<SoundInfo> soundInfoList, Activity activity) {
		StorageHandler.getInstance().deleteFile(soundInfoList.get(position).getAbsolutePath());

		soundInfoList.remove(position);
		ProjectManager.getInstance().getCurrentSprite().setSoundList(soundInfoList);

		activity.sendBroadcast(new Intent(ScriptActivity.ACTION_SOUND_DELETED));
	}

	public void deleteCheckedSounds(Activity activity, SoundBaseAdapter adapter, ArrayList<SoundInfo> soundInfoList,
			MediaPlayer mediaPlayer) {
		SortedSet<Integer> checkedSounds = adapter.getCheckedItems();
		Iterator<Integer> iterator = checkedSounds.iterator();
		SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
		int numberDeleted = 0;
		while (iterator.hasNext()) {
			int position = iterator.next();
			deleteSound(position - numberDeleted, soundInfoList, activity);
			++numberDeleted;
		}
	}

	public SoundInfo updateBackPackActivity(String title, String fileName, ArrayList<SoundInfo> soundInfoList,
			SoundBaseAdapter adapter) {
		title = Utils.getUniqueSoundName(title);

		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setTitle(title);
		newSoundInfo.setSoundFileName(fileName);
		soundInfoList.add(newSoundInfo);

		adapter.notifyDataSetChanged();
		return newSoundInfo;
	}

	public SoundInfo updateSoundAdapter(String title, String fileName, ArrayList<SoundInfo> soundInfoList,
			SoundBaseAdapter adapter) {
		title = Utils.getUniqueSoundName(title);

		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setTitle(title);
		newSoundInfo.setSoundFileName(fileName);
		soundInfoList.add(newSoundInfo);

		adapter.notifyDataSetChanged();
		return newSoundInfo;
	}

	public boolean isSoundPlaying(MediaPlayer mediaPlayer) {
		return mediaPlayer.isPlaying();
	}

	public void stopSound(MediaPlayer mediaPlayer, ArrayList<SoundInfo> soundInfoList) {
		if (isSoundPlaying(mediaPlayer)) {
			mediaPlayer.stop();
		}

		for (int i = 0; i < soundInfoList.size(); i++) {
			soundInfoList.get(i).isPlaying = false;
		}
	}

	public void handlePlaySoundButton(View view, ArrayList<SoundInfo> soundInfoList, MediaPlayer mediaPlayer,
			final SoundBaseAdapter adapter) {
		final int position = (Integer) view.getTag();
		final SoundInfo soundInfo = soundInfoList.get(position);

		stopSound(mediaPlayer, soundInfoList);
		if (!soundInfo.isPlaying) {
			startSound(soundInfo, mediaPlayer);
			adapter.notifyDataSetChanged();
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				soundInfo.isPlaying = false;
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void stopSoundAndUpdateList(MediaPlayer mediaPlayer, ArrayList<SoundInfo> soundInfoList,
			SoundBaseAdapter adapter) {
		if (!isSoundPlaying(mediaPlayer)) {
			return;
		}
		stopSound(mediaPlayer, soundInfoList);
		adapter.notifyDataSetChanged();
	}

	public void startSound(SoundInfo soundInfo, MediaPlayer mediaPlayer) {
		if (!soundInfo.isPlaying) {
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(soundInfo.getAbsolutePath());
				mediaPlayer.prepare();
				mediaPlayer.start();

				soundInfo.isPlaying = true;
			} catch (IOException e) {
				Log.e("CATROID", "Cannot start sound.", e);
			}
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle arguments, Activity activity) {
		Uri audioUri = null;

		if (arguments != null) {
			audioUri = (Uri) arguments.get(BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}
		String[] projection = { MediaStore.Audio.Media.DATA };
		return new CursorLoader(activity, audioUri, projection, null, null, null);
	}

	public String onLoadFinished(Loader<Cursor> loader, Cursor data, Activity activity) {
		String audioPath = "";
		CursorLoader cursorLoader = (CursorLoader) loader;

		if (data == null) {
			audioPath = cursorLoader.getUri().getPath();
		} else {
			data.moveToFirst();
			audioPath = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA));
		}

		if (audioPath.equalsIgnoreCase("")) {
			Utils.showErrorDialog(activity, activity.getString(R.string.error_load_sound));
			audioPath = "";
			return audioPath;
		} else {
			return audioPath;
		}

	}

	public void handleAddButtonFromNew(SoundFragment soundFragment) {
		ScriptActivity scriptActivity = (ScriptActivity) soundFragment.getActivity();
		if (scriptActivity.getIsSoundFragmentFromPlaySoundBrickNew()
				&& !scriptActivity.getIsSoundFragmentHandleAddButtonHandled()) {
			scriptActivity.setIsSoundFragmentHandleAddButtonHandled(true);
			soundFragment.handleAddButton();
		}
	}

	public void switchToScriptFragment(SoundFragment soundFragment) {
		ScriptActivity scriptActivity = (ScriptActivity) soundFragment.getActivity();
		scriptActivity.setCurrentFragment(ScriptActivity.FRAGMENT_SCRIPTS);

		FragmentTransaction fragmentTransaction = scriptActivity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.hide(soundFragment);
		fragmentTransaction.show(scriptActivity.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG));
		fragmentTransaction.commit();

		scriptActivity.setIsSoundFragmentFromPlaySoundBrickNewFalse();
		scriptActivity.setIsSoundFragmentHandleAddButtonHandled(false);
	}

}
