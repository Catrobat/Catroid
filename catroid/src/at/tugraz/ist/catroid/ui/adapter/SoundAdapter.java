/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.adapter;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.SoundActivity;

public class SoundAdapter extends ArrayAdapter<SoundInfo> {
	protected ArrayList<SoundInfo> soundInfoItems;
	protected SoundActivity activity;

	public SoundAdapter(final SoundActivity activity, int textViewResourceId, ArrayList<SoundInfo> items) {
		super(activity, textViewResourceId, items);
		this.activity = activity;
		soundInfoItems = items;
	}

	@Override
	public View getView(final int position, View convView, ViewGroup parent) {

		View convertView = convView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_sound_soundlist_item, null);
		}

		final SoundInfo soundInfo = soundInfoItems.get(position);

		if (soundInfo != null) {
			final ImageView soundImage = (ImageView) convertView.findViewById(R.id.sound_img);
			final TextView soundNameTextView = (TextView) convertView.findViewById(R.id.sound_name);
			final Button renameSoundButton = (Button) convertView.findViewById(R.id.btn_sound_rename);
			final Button stopSoundButton = (Button) convertView.findViewById(R.id.btn_sound_stop);
			final Button playSoundButton = (Button) convertView.findViewById(R.id.btn_sound_play);
			Button deleteSoundButton = (Button) convertView.findViewById(R.id.btn_sound_delete);
			TextView soundFileSize = (TextView) convertView.findViewById(R.id.sound_size);
			TextView soundDuration = (TextView) convertView.findViewById(R.id.sound_duration);

			playSoundButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_media_play, 0, 0);
			stopSoundButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_media_stop, 0, 0);
			renameSoundButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_edit, 0, 0);
			deleteSoundButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_trash, 0, 0);

			if (soundInfo.isPlaying) {
				soundImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.speaker_playing));
				playSoundButton.setVisibility(Button.GONE);
				stopSoundButton.setVisibility(Button.VISIBLE);
			} else {
				soundImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.speaker));
				playSoundButton.setVisibility(Button.VISIBLE);
				stopSoundButton.setVisibility(Button.GONE);
			}

			//setting filesize and duration
			try {
				MediaPlayer tempPlayer = new MediaPlayer();
				tempPlayer.setDataSource(soundInfo.getAbsolutePath());
				tempPlayer.prepare();

				//setting duration TextView:
				long milliseconds = tempPlayer.getDuration();
				int seconds = (int) ((milliseconds / 1000) % 60);
				int minutes = (int) ((milliseconds / 1000) / 60);
				int hours = (int) ((milliseconds / 1000) / 3600);
				String secondsString = seconds < 10 ? "0" + Integer.toString(seconds) : Integer.toString(seconds);
				String minutesString = minutes < 10 ? "0" + Integer.toString(minutes) : Integer.toString(minutes);
				String hoursString = hours < 10 ? "0" + Integer.toString(hours) : Integer.toString(hours);
				soundDuration.setText(hoursString + ":" + minutesString + ":" + secondsString);
				//setting filesize TextView:
				float fileSizeInKB = new File(soundInfo.getAbsolutePath()).length() / 1024;
				String fileSizeString;
				if (fileSizeInKB > 1024) {
					DecimalFormat decimalFormat = new DecimalFormat("#.00");
					fileSizeString = decimalFormat.format(fileSizeInKB / 1024) + " MB";
				} else {
					fileSizeString = Long.toString((long) fileSizeInKB) + " KB";
				}
				soundFileSize.setText(fileSizeString);
			} catch (IOException e) {
				e.printStackTrace();
			}

			soundNameTextView.setText(soundInfo.getTitle());

			//rename: does not rename the actual file (only the title in the SoundInfo)
			renameSoundButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					activity.selectedSoundInfo = soundInfo;
					activity.showDialog(Consts.DIALOG_RENAME_SOUND);
				}
			});

			playSoundButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					activity.stopSound();
					activity.startSound(soundInfo);

					activity.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							soundInfo.isPlaying = false;
							notifyDataSetChanged();
						}
					});

					notifyDataSetChanged();
				}
			});

			stopSoundButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					activity.stopSound();
					notifyDataSetChanged();
				}
			});

			deleteSoundButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					activity.stopSound();
					soundInfoItems.remove(soundInfo);
					StorageHandler.getInstance().deleteFile(soundInfo.getAbsolutePath());
					notifyDataSetChanged();
				}
			});

		}
		return convertView;
	}
}
