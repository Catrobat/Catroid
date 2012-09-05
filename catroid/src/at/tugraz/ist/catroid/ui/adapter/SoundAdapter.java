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
package at.tugraz.ist.catroid.ui.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.utils.UtilFile;

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
		convertView.findViewById(R.id.btn_sound_delete).setTag(position);

		if (soundInfo != null) {
			ImageView soundImage = (ImageView) convertView.findViewById(R.id.sound_img);
			TextView soundNameTextView = (TextView) convertView.findViewById(R.id.sound_name);
			Button pauseSoundButton = (Button) convertView.findViewById(R.id.btn_sound_pause);
			Button playSoundButton = (Button) convertView.findViewById(R.id.btn_sound_play);
			Button deleteSoundButton = (Button) convertView.findViewById(R.id.btn_sound_delete);
			TextView soundFileSize = (TextView) convertView.findViewById(R.id.sound_size);
			TextView soundDuration = (TextView) convertView.findViewById(R.id.sound_duration);

			if (soundInfo.isPlaying) {
				soundImage.setImageDrawable(context.getResources().getDrawable(R.drawable.speaker_playing));
				playSoundButton.setVisibility(Button.GONE);
				pauseSoundButton.setVisibility(Button.VISIBLE);
			} else {
				soundImage.setImageDrawable(context.getResources().getDrawable(R.drawable.speaker));
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

				soundDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
				soundFileSize.setText(UtilFile.getSizeAsString(new File(soundInfo.getAbsolutePath())));

				tempPlayer.reset();
				tempPlayer.release();
			} catch (IOException e) {
				Log.e("CATROID", "Cannot get view.", e);
			}

			soundNameTextView.setText(soundInfo.getTitle());

			soundNameTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onSoundEditListener != null) {
						onSoundEditListener.onSoundRename(v);
					}
				}
			});

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

			deleteSoundButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onSoundEditListener != null) {
						onSoundEditListener.onSoundDelete(v);
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
