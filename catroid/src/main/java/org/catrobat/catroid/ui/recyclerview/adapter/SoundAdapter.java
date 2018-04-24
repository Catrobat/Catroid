/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedVH;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SoundAdapter extends ExtendedRVAdapter<SoundInfo> {

	private MediaPlayer mediaPlayer = new MediaPlayer();

	public SoundAdapter(List<SoundInfo> items) {
		super(items);
	}

	@Override
	public void onBindViewHolder(final ExtendedVH holder, int position) {
		final SoundInfo item = items.get(position);

		holder.title.setText(item.getName());
		holder.image.setImageResource(R.drawable.ic_media_play_dark);

		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					holder.image.setImageResource(R.drawable.ic_media_play_dark);
					stopSound();
				} else {
					holder.image.setImageResource(R.drawable.ic_media_pause_dark);
					playSound(item);
					mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							holder.image.setImageResource(R.drawable.ic_media_play_dark);
						}
					});
				}
			}
		});

		Context context = holder.itemView.getContext();

		if (showDetails) {
			holder.details.setText(String.format(Locale.getDefault(),
					context.getString(R.string.sound_details),
					getSoundDuration(item),
					FileMetaDataExtractor.getSizeAsString(item.getFile(), context)));
		} else {
			holder.details.setText(String.format(Locale.getDefault(),
					context.getString(R.string.sound_duration),
					getSoundDuration(item)));
		}
	}

	private String getSoundDuration(SoundInfo sound) {
		MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
		metadataRetriever.setDataSource(sound.getFile().getAbsolutePath());

		long duration = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever
				.METADATA_KEY_DURATION));

		duration = (duration / 1000) == 0 ? 1 : (duration / 1000);
		return DateUtils.formatElapsedTime(duration);
	}

	private void playSound(SoundInfo sound) {
		try {
			mediaPlayer.release();
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(sound.getFile().getAbsolutePath());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			Log.e("[ERROR]", Log.getStackTraceString(e));
		}
	}

	private void stopSound() {
		mediaPlayer.stop();
	}
}
