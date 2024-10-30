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
package org.catrobat.catroid.content.actions

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager

class PlaySoundAtAction : TemporalAction() {
    lateinit var sprite: Sprite
    lateinit var offset: Formula
    var sound: SoundInfo? = null
    var scope: Scope? = null

    override fun update(percent: Float) {
        var offsetMilliseconds: Int
        try {
            offsetMilliseconds = (offset.interpretFloat(scope) * SECONDS_TO_MILLISECONDS).toInt()
        } catch (exception: InterpretationException) {
            Log.d(TAG, "Failed to interpret Delay", exception)
            return
        }
        if (sprite.soundList.contains(sound)) {

            sound?.let {
                if (it.isMidiFile) {
                    MidiSoundManager.getInstance().playSoundFileWithStartTime(
                        it.file.absolutePath, sprite, offsetMilliseconds
                    )
                } else {
                    SoundManager.getInstance().playSoundFileWithStartTime(
                        it.file.absolutePath, sprite, offsetMilliseconds
                    )
                }
            }
        }
    }

    companion object {
        val TAG: String = PlaySoundAction::class.java.simpleName
        const val SECONDS_TO_MILLISECONDS: Int = 1000
    }

    @VisibleForTesting
    fun runWithMockedSoundManager(manager: SoundManager): Float {
        var offsetMilliseconds: Int
        try {
            offsetMilliseconds = (offset.interpretFloat(scope) * SECONDS_TO_MILLISECONDS).toInt()
        } catch (exception: InterpretationException) {
            Log.d(TAG, "Failed to interpret Delay", exception)
            return -1.0f
        }

        if (sprite.soundList.contains(sound)) {

            sound?.let {
                    manager.playSoundFileWithStartTime(
                        it.file.absolutePath, sprite, offsetMilliseconds
                    )
            }
            return manager.getDurationOfSoundFile(sound?.file?.absolutePath) - offsetMilliseconds
        }
        return -1.0f
    }
}
