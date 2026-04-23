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

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.bricks.brickspinner.PickableDrum;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager;
import org.catrobat.catroid.pocketmusic.note.Drum;

public class PlayDrumForBeatsAction extends TemporalAction {

	private Scope scope;
	private PickableDrum selectedDrum;
	private Formula beats;

	@Override
	protected void begin() {
		try {
			Drum drum = Drum.getDrumFromProgram(selectedDrum.getValue());
			float playedBeats = beats == null ? Float.valueOf(0f) : beats.interpretFloat(scope);
			MidiSoundManager.getInstance().playDrumForBeats(drum, playedBeats, scope.getSprite());
			super.setDuration((float) MidiSoundManager.getInstance().getDurationForBeats(playedBeats) / 1000);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setDrum(PickableDrum selectedDrum) {
		this.selectedDrum = selectedDrum;
	}

	public void setBeats(Formula beats) {
		this.beats = beats;
	}

	@Override
	protected void update(float percent) {
	}
}
