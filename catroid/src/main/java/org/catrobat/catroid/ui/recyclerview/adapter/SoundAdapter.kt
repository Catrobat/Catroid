/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.ui.recyclerview.adapter

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.text.format.DateUtils
import android.util.Log
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedViewHolder
import org.catrobat.catroid.utils.FileMetaDataExtractor
import java.io.IOException
import java.util.Locale

private const val SOUND_DURATION_DIVISOR = 1000

class SoundAdapter(items: List<SoundInfo?>?) : ExtendedRVAdapter<SoundInfo?>(items) {

    private var mediaPlayer = MediaPlayer()
    private var currentPlaying: SoundInfo? = null
    private var currentPlayingPosition: Int = 0

    override fun onBindViewHolder(holder: ExtendedViewHolder?, position: Int) {
        val item = items[position]

        holder?.title?.text = item?.name
        holder?.image?.setImageResource(R.drawable.ic_media_play_dark)

        holder?.image?.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                holder.image.setImageResource(R.drawable.ic_media_play_dark)
                stopSound()
                if (item != currentPlaying) {
                    notifyItemChanged(currentPlayingPosition)
                    setAndPlaySound(holder, position, item)
                }
            } else {
                setAndPlaySound(holder, position, item)
            }
        }

        val context = holder?.itemView?.context ?: return
        item ?: return

        if (showDetails) {
            holder.details?.text = String.format(
                Locale.getDefault(),
                context.getString(R.string.sound_details),
                getSoundDuration(item),
                FileMetaDataExtractor.getSizeAsString(item.file, context)
            )
        } else {
            holder.details?.text = String.format(
                Locale.getDefault(),
                context.getString(R.string.sound_duration),
                getSoundDuration(item)
            )
        }
    }

    private fun setAndPlaySound(holder: ExtendedViewHolder?, position: Int, item: SoundInfo?) {
        holder?.image?.setImageResource(R.drawable.ic_media_pause_dark)
        playSound(item)
        mediaPlayer.setOnCompletionListener { holder?.image?.setImageResource(R.drawable.ic_media_play_dark) }
        currentPlaying = item
        currentPlayingPosition = position
    }

    private fun getSoundDuration(sound: SoundInfo): String {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(sound.file?.absolutePath)

        var duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong() ?: 0

        duration =
            if (duration / SOUND_DURATION_DIVISOR == 0L) 1 else duration / SOUND_DURATION_DIVISOR
        return DateUtils.formatElapsedTime(duration)
    }

    private fun playSound(sound: SoundInfo?) {
        try {
            mediaPlayer.release()
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(sound?.file?.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            Log.e("[ERROR]", Log.getStackTraceString(e))
        }
    }

    override fun stopSound() {
        mediaPlayer.stop()
    }
}
