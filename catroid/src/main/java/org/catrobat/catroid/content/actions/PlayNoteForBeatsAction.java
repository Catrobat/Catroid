/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.pocketmusic.mididriver.MidiRunnable;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSignals;
import org.catrobat.catroid.pocketmusic.note.NoteName;

public class PlayNoteForBeatsAction extends TemporalAction {

	private Sprite sprite;
	private Formula note;
	private Formula durationInBeats;

	@Override
	protected void begin() {
		try {
			Float newDurationInBeats = durationInBeats == null ? Float.valueOf(0f) : durationInBeats.interpretFloat(sprite);
			long durationInMs = calculateDurationInMilliseconds(newDurationInBeats);
			int midiNoteValue = note.interpretInteger(sprite);
			Handler h = new Handler(Looper.getMainLooper());
			super.setDuration(durationInMs / 1000.0f);
			MidiRunnable midiRunnable = new MidiRunnable(MidiSignals.NOTE_ON,
					new NoteName(midiNoteValue), durationInMs, h,
					SoundManager.getInstance().getMidiNotePlayer(), null);
			h.post(midiRunnable);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}
	}

	@Override
	protected void update(float percent) {
	}

	private long calculateDurationInMilliseconds(double beats) {
		return (long) (beats * (60 * 1000 / sprite.midiSoundConfiguration.tempo));
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setNote(Formula note) {
		this.note = note;
	}

	public void setDurationInBeats(Formula durationInBeats) {
		this.durationInBeats = durationInBeats;
	}
}
