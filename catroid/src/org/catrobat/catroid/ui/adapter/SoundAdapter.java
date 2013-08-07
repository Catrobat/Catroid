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
package org.catrobat.catroid.ui.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SoundAdapter extends ArrayAdapter<SoundInfo> implements ScriptActivityAdapterInterface {

	protected ArrayList<SoundInfo> soundInfoItems;
	protected Context context;

	private OnSoundEditListener onSoundEditListener;

	private int selectMode;
	private static long elapsedMilliSeconds;
	private static long currentPlayingBase;
	private boolean showDetails;
	private SortedSet<Integer> checkedSounds = new TreeSet<Integer>();

	private int currentPlayingPosition = Constants.NO_POSITION;

	public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundInfo> items, boolean showDetails) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.showDetails = showDetails;
		this.soundInfoItems = items;
		this.selectMode = ListView.CHOICE_MODE_NONE;
	}

	public void setOnSoundEditListener(OnSoundEditListener listener) {
		onSoundEditListener = listener;
	}

	private static class ViewHolder {
		private ImageButton playButton;
		private ImageButton pauseButton;
		private LinearLayout soundFragmentButtonLayout;
		private CheckBox checkbox;
		private TextView titleTextView;
		private TextView timeSeperatorTextView;
		private TextView timeDurationTextView;
		private TextView soundFileSizePrefixTextView;
		private TextView soundFileSizeTextView;
		private Chronometer timePlayedChronometer;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.fragment_sound_soundlist_item, null);

			holder = new ViewHolder();
			holder.playButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_play_image_button);
			holder.pauseButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_pause_image_button);

			holder.playButton.setVisibility(Button.VISIBLE);
			holder.pauseButton.setVisibility(Button.GONE);

			holder.soundFragmentButtonLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_sound_item_main_linear_layout);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_sound_item_checkbox);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_title_text_view);
			holder.timeSeperatorTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_time_seperator_text_view);
			holder.timeDurationTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_duration_text_view);
			holder.soundFileSizePrefixTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_size_prefix_text_view);
			holder.soundFileSizeTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_size_text_view);

			holder.timePlayedChronometer = (Chronometer) convertView
					.findViewById(R.id.fragment_sound_item_time_played_chronometer);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final SoundInfo soundInfo = soundInfoItems.get(position);

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
		return convertView;
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

	@Override
	public int getAmountOfCheckedItems() {
		return checkedSounds.size();
	}

	@Override
	public SortedSet<Integer> getCheckedItems() {
		return checkedSounds;
	}

	public void addCheckedItem(int position) {
		checkedSounds.add(position);
	}

	@Override
	public void clearCheckedItems() {
		checkedSounds.clear();
	}

	@Override
	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	@Override
	public boolean getShowDetails() {
		return showDetails;
	}

	public interface OnSoundEditListener {

		public void onSoundPlay(View view);

		public void onSoundPause(View view);

		public void onSoundChecked();
	}
}
