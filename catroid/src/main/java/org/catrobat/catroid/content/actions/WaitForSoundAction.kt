/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
    private var soundFilePath: String? = null
    private var soundManager = SoundManager.getInstance()
    private var midiSoundManager = MidiSoundManager.getInstance()
    private var soundStopped = false
    fun setSoundFilePath(soundFilePath: String?) {
        this.soundFilePath = soundFilePath
    }

    override fun update(percent: Float) {
        if (soundFilePath != null && !midiSoundManager.startedSoundfilePaths.isEmpty()) {
            val spriteSoundFilePath = SoundFilePathWithSprite(soundFilePath, getScope()!!.sprite)
            val recentlyStarted = midiSoundManager.startedSoundfilePaths
            if (recentlyStarted.contains(spriteSoundFilePath) && !midiSoundManager.isSoundInSpritePlaying(
                    getScope()!!.sprite, soundFilePath
                )
            ) {
                recentlyStarted.remove(spriteSoundFilePath)
                finish()
                soundStopped = true
                return
            }
        }
        if (soundFilePath != null && !soundManager.recentlyStoppedSoundfilePaths.isEmpty()) {
            val spriteSoundFilePath = SoundFilePathWithSprite(soundFilePath, getScope()!!.sprite)
            val recentlyStopped = soundManager.recentlyStoppedSoundfilePaths
            if (recentlyStopped.contains(spriteSoundFilePath)) {
                recentlyStopped.remove(spriteSoundFilePath)
                finish()
                soundStopped = true
            }
        }
    }

    override fun end() {
        for (mediaPlayer in soundManager.mediaPlayers) {
            if (mediaPlayer.isPlaying && mediaPlayer.startedBySprite === getScope()!!.sprite && mediaPlayer.pathToSoundFile == soundFilePath && !soundStopped) {
                restart()
                time = mediaPlayer.currentPosition.toFloat()
            }
        }
        for (midiPlayer in midiSoundManager.midiPlayers) {
            if (midiPlayer.isPlaying && midiPlayer.startedBySprite === getScope()!!.sprite && midiPlayer.pathToSoundFile == soundFilePath && !soundStopped) {
                restart()
                time = midiPlayer.currentPosition.toFloat()
            }
        }
    }

    @VisibleForTesting
    fun setSoundManager(soundManager: SoundManager) {
        this.soundManager = soundManager
    }

    @VisibleForTesting
    fun setMidiSoundManager(midiSoundManager: MidiSoundManager) {
        this.midiSoundManager = midiSoundManager
    }

    companion object {
        val TAG = WaitForSoundAction::class.java.simpleName
    }
}