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

package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.content.MediaPlayerWithSoundDetails;
import org.catrobat.catroid.content.SoundFilePathWithSprite;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.pocketmusic.mididriver.MidiPlayer;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager;

import java.util.Set;

import androidx.annotation.VisibleForTesting;

public class WaitForSoundAction extends WaitAction {
	public static final String TAG = WaitForSoundAction.class.getSimpleName();
	private String soundFilePath;
	private SoundManager soundManager = SoundManager.getInstance();
	private MidiSoundManager midiSoundManager = MidiSoundManager.getInstance();
	private boolean soundStopped = false;

	public void setSoundFilePath(String soundFilePath) {
		this.soundFilePath = soundFilePath;
	}

	@Override
	protected void update(float percent) {
		if (soundFilePath != null && !midiSoundManager.getStartedSoundfilePaths().isEmpty()) {
			SoundFilePathWithSprite spriteSoundFilePath = new SoundFilePathWithSprite(soundFilePath, scope.getSprite());
			Set<SoundFilePathWithSprite> recentlyStarted = midiSoundManager.getStartedSoundfilePaths();
			if (recentlyStarted.contains(spriteSoundFilePath) && !midiSoundManager.isSoundInSpritePlaying(scope.getSprite(), soundFilePath)) {
				recentlyStarted.remove(spriteSoundFilePath);
				finish();
				soundStopped = true;
				return;
			}
		}
		if (soundFilePath != null && !soundManager.getRecentlyStoppedSoundfilePaths().isEmpty()) {
			SoundFilePathWithSprite spriteSoundFilePath =
					new SoundFilePathWithSprite(soundFilePath, scope.getSprite());
			Set<SoundFilePathWithSprite> recentlyStopped =
					soundManager.getRecentlyStoppedSoundfilePaths();
			if (recentlyStopped.contains(spriteSoundFilePath)) {
				recentlyStopped.remove(spriteSoundFilePath);
				finish();
				soundStopped = true;
			}
		}
	}

	@Override
	protected void end() {
		for (MediaPlayerWithSoundDetails mediaPlayer : soundManager.getMediaPlayers()) {
			if (mediaPlayer.isPlaying() && mediaPlayer.getStartedBySprite() == scope.getSprite() && mediaPlayer.getPathToSoundFile().equals(soundFilePath) && !soundStopped) {
				restart();
				setTime(mediaPlayer.getCurrentPosition());
			}
		}
		for (MidiPlayer midiPlayer : midiSoundManager.getMidiPlayers()) {
			if (midiPlayer.isPlaying() && midiPlayer.getStartedBySprite() == scope.getSprite() && midiPlayer.getPathToSoundFile().equals(soundFilePath) && !soundStopped) {
				restart();
				setTime(midiPlayer.getCurrentPosition());
			}
		}
	}

	@VisibleForTesting
	public void setSoundManager(SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	@VisibleForTesting
	public void setMidiSoundManager(MidiSoundManager midiSoundManager) {
		this.midiSoundManager = midiSoundManager;
	}
}
