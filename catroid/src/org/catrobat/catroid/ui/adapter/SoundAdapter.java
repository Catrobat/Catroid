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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SoundAdapter extends ArrayAdapter<SoundInfo> {

	protected ArrayList<SoundInfo> soundInfoItems;
	protected Context context;

	private OnSoundEditListener onSoundEditListener;

	public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundInfo> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		soundInfoItems = items;
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

		convertView.findViewById(R.id.sound_name).setTag(position);
		convertView.findViewById(R.id.btn_sound_play).setTag(position);
		convertView.findViewById(R.id.btn_sound_pause).setTag(position);

		if (soundInfo != null) {
			TextView soundNameTextView = (TextView) convertView.findViewById(R.id.sound_name);
			TextView soundDuration = (TextView) convertView.findViewById(R.id.sound_duration);
			ImageButton pauseSoundButton = (ImageButton) convertView.findViewById(R.id.btn_sound_pause);
			ImageButton playSoundButton = (ImageButton) convertView.findViewById(R.id.btn_sound_play);

			soundNameTextView.setText(soundInfo.getTitle());

			if (soundInfo.isPlaying) {
				playSoundButton.setVisibility(Button.GONE);
				pauseSoundButton.setVisibility(Button.VISIBLE);
			} else {
				playSoundButton.setVisibility(Button.VISIBLE);
				pauseSoundButton.setVisibility(Button.GONE);
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
					soundDuration.setText(String.format("%02d:%02d", minutes, seconds));
				} else {
					soundDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
				}
				//soundFileSize.setText(UtilFile.getSizeAsString(new File(soundInfo.getAbsolutePath())));
				new File(soundInfo.getAbsolutePath());

				tempPlayer.reset();
				tempPlayer.release();
			} catch (IOException e) {
				Log.e("CATROID", "Cannot get view.", e);
			}

			playSoundButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onSoundEditListener != null) {
						onSoundEditListener.onSoundPlay(v);
					}
				}
			});

			pauseSoundButton.setOnClickListener(new OnClickListener() {
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

	public interface OnSoundEditListener {

		public void onSoundRename(View v);

		public void onSoundPlay(View v);

		public void onSoundPause(View v);

		public void onSoundDelete(View v);

	}
}
