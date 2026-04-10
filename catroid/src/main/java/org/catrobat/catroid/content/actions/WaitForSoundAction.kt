/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.content.actions

import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.content.SoundFilePathWithSprite
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager

class WaitForSoundAction : WaitAction() {

    var soundFilePath: String? = null

    @VisibleForTesting
    var soundManager: SoundManager = SoundManager.getInstance()

    @VisibleForTesting
    var midiSoundManager: MidiSoundManager = MidiSoundManager.getInstance()

    private var soundStopped = false

    override fun update(percent: Float) {
        val path = soundFilePath ?: return
        val sprite = scope.sprite

        val startedPaths = midiSoundManager.startedSoundfilePaths
        if (startedPaths.isNotEmpty()) {
            val spriteSoundFilePath = SoundFilePathWithSprite(path, sprite)
            if (spriteSoundFilePath in startedPaths &&
                !midiSoundManager.isSoundInSpritePlaying(sprite, path)
            ) {
                startedPaths.remove(spriteSoundFilePath)
                finish()
                soundStopped = true
                return
            }
        }

        val stoppedPaths = soundManager.recentlyStoppedSoundfilePaths
        if (stoppedPaths.isNotEmpty()) {
            val spriteSoundFilePath = SoundFilePathWithSprite(path, sprite)
            if (spriteSoundFilePath in stoppedPaths) {
                stoppedPaths.remove(spriteSoundFilePath)
                finish()
                soundStopped = true
            }
        }
    }

    override fun end() {
        val path = soundFilePath ?: return
        val sprite = scope.sprite

        for (mediaPlayer in soundManager.mediaPlayers) {
            if (mediaPlayer.isPlaying &&
                mediaPlayer.startedBySprite === sprite &&
                mediaPlayer.pathToSoundFile == path &&
                !soundStopped
            ) {
                restart()
                setTime(mediaPlayer.currentPosition)
            }
        }

        for (midiPlayer in midiSoundManager.midiPlayers) {
            if (midiPlayer.isPlaying &&
                midiPlayer.startedBySprite === sprite &&
                midiPlayer.pathToSoundFile == path &&
                !soundStopped
            ) {
                restart()
                setTime(midiPlayer.currentPosition)
            }
        }
    }

    companion object {
        @JvmField
        val TAG: String = WaitForSoundAction::class.java.simpleName
    }
}
