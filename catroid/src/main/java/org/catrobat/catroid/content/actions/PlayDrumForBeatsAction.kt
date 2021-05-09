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
package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.bricks.brickspinner.PickableDrum
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager
import org.catrobat.catroid.pocketmusic.note.Drum

class PlayDrumForBeatsAction : TemporalAction() {
    private var scope: Scope? = null
    private var selectedDrum: PickableDrum? = null
    private var beats: Formula? = null
    override fun begin() {
        try {
            val drum = Drum.getDrumFromProgram(selectedDrum!!.value)
            val playedBeats =
                if (beats == null) java.lang.Float.valueOf(0f) else beats!!.interpretFloat(scope)
            MidiSoundManager.getInstance().playDrumForBeats(drum, playedBeats, scope!!.sprite)
            super.setDuration(
                MidiSoundManager.getInstance().getDurationForBeats(playedBeats).toFloat() / 1000
            )
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setDrum(selectedDrum: PickableDrum?) {
        this.selectedDrum = selectedDrum
    }

    fun setBeats(beats: Formula?) {
        this.beats = beats
    }

    override fun update(percent: Float) {}
}