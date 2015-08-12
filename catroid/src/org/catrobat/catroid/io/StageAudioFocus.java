/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.io;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

public class StageAudioFocus implements OnAudioFocusChangeListener {

	private AudioManager audioManager = null;
	private boolean isAudioFocusGranted = false;

	public static final String TAG = StageAudioFocus.class.getSimpleName();

	public StageAudioFocus(Context context) {
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	public void requestAudioFocus() {
		if (isAudioFocusGranted()) {
			return;
		}

		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			isAudioFocusGranted = true;
		} else {
			isAudioFocusGranted = false;
		}
	}

	public void releaseAudioFocus() {
		audioManager.abandonAudioFocus(this);
		isAudioFocusGranted = false;
	}

	public boolean isAudioFocusGranted() {
		return isAudioFocusGranted;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			releaseAudioFocus();
		}
	}
}
