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

import java.io.File;
import java.io.IOException;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.utils.UtilFile;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author patrick, stefan
 * 
 */
public class SoundController {

	private static SoundAdapter soundAdapter;

	private static final SoundController instance = new SoundController();

	public void setUpSoundController(SoundAdapter soundAdapter) {

		SoundController.soundAdapter = soundAdapter;
	}

	public static SoundController getInstance() {
		return instance;
	}

	public void updateSoundLogic(int position) {

		final SoundInfo soundInfo = soundAdapter.getSoundInfoItems().get(position);

		if (soundInfo != null) {
			holder.playButton.setTag(position);
			holder.pauseButton.setTag(position);
			holder.titleTextView.setText(soundInfo.getTitle());

			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						if (selectMode == ListView.CHOICE_MODE_SINGLE) {
							clearCheckedItems();
						}
						checkedSounds.add(position);
					} else {
						checkedSounds.remove(position);
					}
					notifyDataSetChanged();

					if (onSoundEditListener != null) {
						onSoundEditListener.onSoundChecked();
					}
				}
			});

			if (selectMode != ListView.CHOICE_MODE_NONE) {
				holder.checkbox.setVisibility(View.VISIBLE);
				holder.checkbox.setVisibility(View.VISIBLE);
				holder.soundFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_shadowed);
			} else {
				holder.checkbox.setVisibility(View.GONE);
				holder.checkbox.setVisibility(View.GONE);
				holder.soundFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_selector);
				holder.checkbox.setChecked(false);
				clearCheckedItems();
			}

			if (checkedSounds.contains(position)) {
				holder.checkbox.setChecked(true);
			} else {
				holder.checkbox.setChecked(false);
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
					holder.timeDurationTextView.setText(String.format("%02d:%02d", minutes, seconds));
				} else {
					holder.timeDurationTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
				}

				if (currentPlayingPosition == Constants.NO_POSITION) {
					elapsedMilliSeconds = 0;
				} else {
					elapsedMilliSeconds = SystemClock.elapsedRealtime() - currentPlayingBase;
				}

				if (soundInfo.isPlaying) {
					holder.playButton.setVisibility(Button.GONE);
					holder.pauseButton.setVisibility(Button.VISIBLE);

					holder.timeSeperatorTextView.setVisibility(TextView.VISIBLE);
					holder.timePlayedChronometer.setVisibility(Chronometer.VISIBLE);

					if (currentPlayingPosition == Constants.NO_POSITION) {
						startPlayingSound(holder.timePlayedChronometer, position);
					} else if ((position == currentPlayingPosition) && (elapsedMilliSeconds > (milliseconds - 1000))) {
						stopPlayingSound(soundInfo, holder.timePlayedChronometer);
					} else {
						continuePlayingSound(holder.timePlayedChronometer, SystemClock.elapsedRealtime());
					}
				} else {
					holder.playButton.setVisibility(Button.VISIBLE);
					holder.pauseButton.setVisibility(Button.GONE);

					holder.timeSeperatorTextView.setVisibility(TextView.GONE);
					holder.timePlayedChronometer.setVisibility(Chronometer.GONE);

					if (position == currentPlayingPosition) {
						stopPlayingSound(soundInfo, holder.timePlayedChronometer);
					}
				}

				if (showDetails) {
					holder.soundFileSizeTextView
							.setText(UtilFile.getSizeAsString(new File(soundInfo.getAbsolutePath())));
					holder.soundFileSizeTextView.setVisibility(TextView.VISIBLE);
					holder.soundFileSizePrefixTextView.setVisibility(TextView.VISIBLE);
				} else {
					holder.soundFileSizeTextView.setVisibility(TextView.GONE);
					holder.soundFileSizePrefixTextView.setVisibility(TextView.GONE);
				}

				tempPlayer.reset();
				tempPlayer.release();
			} catch (IOException e) {
				Log.e("CATROID", "Cannot get view.", e);
			}

			if (selectMode != ListView.CHOICE_MODE_NONE) {
				holder.playButton.setOnClickListener(null);
				holder.pauseButton.setOnClickListener(null);
			} else {
				holder.playButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (onSoundEditListener != null) {
							onSoundEditListener.onSoundPlay(view);
						}
					}
				});

				holder.pauseButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (onSoundEditListener != null) {
							onSoundEditListener.onSoundPause(view);
						}
					}
				});
			}
		}
	}

	private void startPlayingSound(Chronometer chronometer, int position) {
		currentPlayingPosition = position;
		currentPlayingBase = SystemClock.elapsedRealtime();

		continuePlayingSound(chronometer, currentPlayingBase);
	}

	private void continuePlayingSound(Chronometer chronometer, long base) {
		chronometer.setBase(base);
		chronometer.start();
	}

	private void stopPlayingSound(SoundInfo soundInfo, Chronometer chronometer) {
		chronometer.stop();
		chronometer.setBase(SystemClock.elapsedRealtime());

		currentPlayingPosition = Constants.NO_POSITION;

		soundInfo.isPlaying = false;
	}

}
