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
package org.catrobat.catroid.ui.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.utils.UtilFile;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SoundAdapter extends ArrayAdapter<SoundInfo> {

	protected ArrayList<SoundInfo> soundInfoItems;
	protected Context context;

	private CheckBox checkbox;

	private OnSoundEditListener onSoundEditListener;

	private int selectMode;
	private boolean showDetails;
	private Set<Integer> checkedSounds = new HashSet<Integer>();

	public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundInfo> items, boolean showDetails) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.showDetails = showDetails;
		soundInfoItems = items;
		selectMode = Constants.SELECT_NONE;
	}

	public void setOnSoundEditListener(OnSoundEditListener listener) {
		onSoundEditListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.fragment_sound_soundlist_item, null);
		}
		final SoundInfo soundInfo = soundInfoItems.get(position);

		if (soundInfo != null) {
			TextView titleTextView = (TextView) convertView.findViewById(R.id.sound_title);
			TextView timeSeperatorTextView = (TextView) convertView.findViewById(R.id.sound_time_seperator);
			TextView timeDurationTextView = (TextView) convertView.findViewById(R.id.sound_duration);
			TextView soundFileSizeTextView = (TextView) convertView.findViewById(R.id.sound_size);

			Chronometer timePlayedChronometer = (Chronometer) convertView
					.findViewById(R.id.sound_chronometer_time_played);

			ImageButton playButton = (ImageButton) convertView.findViewById(R.id.btn_sound_play);
			ImageButton pauseButton = (ImageButton) convertView.findViewById(R.id.btn_sound_pause);

			titleTextView.setTag(position);
			playButton.setTag(position);
			pauseButton.setTag(position);

			titleTextView.setText(soundInfo.getTitle());

			checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (selectMode == Constants.MULTI_SELECT) {
						if (isChecked) {
							checkedSounds.add(position);
						} else {
							checkedSounds.remove(position);
						}
					} else if (selectMode == Constants.SINGLE_SELECT) {
						if (isChecked) {
							clearCheckedSounds();
							checkedSounds.add(position);
						} else {
							checkedSounds.remove(position);
						}
						notifyDataSetChanged();
					}
				}
			});

			if (selectMode != Constants.SELECT_NONE) {
				checkbox.setVisibility(View.VISIBLE);
			} else {
				checkbox.setVisibility(View.GONE);
				checkbox.setChecked(false);
				clearCheckedSounds();
			}

			if (checkedSounds.contains(position)) {
				checkbox.setChecked(true);
			} else {
				checkbox.setChecked(false);
			}

			try {
				MediaPlayer tempPlayer = new MediaPlayer();
				tempPlayer.setDataSource(soundInfo.getAbsolutePath());
				tempPlayer.prepare();

				long milliseconds = tempPlayer.getDuration();
				int seconds = (int) ((milliseconds / 1000) % 60);
				int minutes = (int) ((milliseconds / 1000) / 60);
				int hours = (int) ((milliseconds / 1000) / 3600);

				String duration = "";

				if (hours == 0) {
					duration = String.format("%02d:%02d", minutes, seconds);
				} else {
					duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				}
				timeDurationTextView.setText(duration);

				if (soundInfo.isPlaying) {
					playButton.setVisibility(Button.GONE);
					pauseButton.setVisibility(Button.VISIBLE);

					if (timePlayedChronometer.getText().equals("00:00")) {
						timeSeperatorTextView.setVisibility(TextView.VISIBLE);

						timePlayedChronometer.setVisibility(Chronometer.VISIBLE);
						timePlayedChronometer.setBase(SystemClock.elapsedRealtime());
						timePlayedChronometer.start();

						Log.d("CATROID", "-----START----- ");
						Log.d("CATROID", "time after start: " + timePlayedChronometer.getText());
					} else if (timePlayedChronometer.getText().equals(duration)) {
						Log.d("CATROID", "NORMAL END!");
						timeSeperatorTextView.setVisibility(TextView.GONE);
						timePlayedChronometer.setVisibility(Chronometer.GONE);
						timePlayedChronometer.stop();
						timePlayedChronometer.setBase(SystemClock.elapsedRealtime());
					}
				} else {
					playButton.setVisibility(Button.VISIBLE);
					pauseButton.setVisibility(Button.GONE);

					if (!timePlayedChronometer.getText().equals("00:00")) {
						timeSeperatorTextView.setVisibility(TextView.GONE);
						timePlayedChronometer.setVisibility(Chronometer.GONE);
						timePlayedChronometer.stop();
						timePlayedChronometer.setBase(SystemClock.elapsedRealtime());

						Log.d("CATROID", "------STOP----- ");
						Log.d("CATROID", "time after stop: " + timePlayedChronometer.getText());
					}
				}

				if (showDetails) {
					soundFileSizeTextView.setText(getContext().getString(R.string.sound_size) + " "
							+ UtilFile.getSizeAsString(new File(soundInfo.getAbsolutePath())));
					soundFileSizeTextView.setVisibility(TextView.VISIBLE);
				} else {
					soundFileSizeTextView.setVisibility(TextView.GONE);
				}

				tempPlayer.reset();
				tempPlayer.release();
			} catch (IOException e) {
				Log.e("CATROID", "Cannot get view.", e);
			}

			playButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onSoundEditListener != null) {
						onSoundEditListener.onSoundPlay(v);
					}
				}
			});

			pauseButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onSoundEditListener != null) {
						onSoundEditListener.onSoundPause(v);
					}
				}
			});
		}
		return convertView;
	}

	public Set<Integer> getCheckedSounds() {
		return checkedSounds;
	}

	public void clearCheckedSounds() {
		checkedSounds.clear();
	}

	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	public int getSelectMode() {
		return selectMode;
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public boolean getShowDetails() {
		return showDetails;
	}

	public interface OnSoundEditListener {

		public void onSoundPlay(View v);

		public void onSoundPause(View v);
	}
}
