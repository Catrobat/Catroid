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
import org.catrobat.catroid.ui.adapter.SoundAdapter.ViewHolder;
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

	private static SoundController instance;

	public void setUpSoundController(SoundAdapter soundAdapter) {

		SoundController.soundAdapter = soundAdapter;
	}

	public static SoundController getInstance() {
		if (instance == null) {
			instance = new SoundController();
		}

		return instance;
	}

	public void updateSoundLogic(final int position, ViewHolder holder) {
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

					SoundAdapter.setElapsedMilliSeconds(0);
				} else {

					SoundAdapter.setElapsedMilliSeconds(SystemClock.elapsedRealtime()
							- SoundAdapter.getCurrentPlayingBase());
				}

				if (soundInfo.isPlaying) {
					holder.getPlayButton().setVisibility(Button.GONE);
					holder.getPauseButton().setVisibility(Button.VISIBLE);

					holder.getTimeSeperatorTextView().setVisibility(TextView.VISIBLE);
					holder.getTimePlayedChronometer().setVisibility(Chronometer.VISIBLE);

					if (soundAdapter.getCurrentPlayingPosition() == Constants.NO_POSITION) {
						startPlayingSound(holder.getTimePlayedChronometer(), position);
					} else if ((position == soundAdapter.getCurrentPlayingPosition())
							&& (SoundAdapter.getElapsedMilliSeconds() > (milliseconds - 1000))) {
						stopPlayingSound(soundInfo, holder.getTimePlayedChronometer());
					} else {
						continuePlayingSound(holder.getTimePlayedChronometer(), SystemClock.elapsedRealtime());
					}
				} else {
					holder.getPlayButton().setVisibility(Button.VISIBLE);
					holder.getPauseButton().setVisibility(Button.GONE);

					holder.getTimeSeperatorTextView().setVisibility(TextView.GONE);
					holder.getTimePlayedChronometer().setVisibility(Chronometer.GONE);

					if (position == soundAdapter.getCurrentPlayingPosition()) {
						stopPlayingSound(soundInfo, holder.getTimePlayedChronometer());
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

	private void startPlayingSound(Chronometer chronometer, int position) {
		soundAdapter.setCurrentPlayingPosition(position);

		SoundAdapter.setCurrentPlayingBase(SystemClock.elapsedRealtime());

		continuePlayingSound(chronometer, SoundAdapter.getCurrentPlayingBase());
	}

	private void continuePlayingSound(Chronometer chronometer, long base) {
		chronometer.setBase(base);
		chronometer.start();
	}

	private void stopPlayingSound(SoundInfo soundInfo, Chronometer chronometer) {
		chronometer.stop();
		chronometer.setBase(SystemClock.elapsedRealtime());

		soundAdapter.setCurrentPlayingPosition(Constants.NO_POSITION);

		soundInfo.isPlaying = false;
	}

}
