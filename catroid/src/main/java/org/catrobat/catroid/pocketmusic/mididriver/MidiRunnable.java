/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.pocketmusic.mididriver;

import android.os.Handler;

import org.catrobat.catroid.pocketmusic.note.NoteName;

public class MidiRunnable implements Runnable {

	private final MidiSignals signal;
	private final NoteName noteName;
	private final long duration;
	private final Handler handler;
	private final MidiNotePlayer midiNotePlayer;

	public MidiRunnable(MidiSignals signal, NoteName noteName, long duration, Handler handler, MidiNotePlayer midiNotePlayer) {
		this.signal = signal;
		this.noteName = noteName;
		this.duration = duration;
		this.handler = handler;
		this.midiNotePlayer = midiNotePlayer;
	}

	@Override
	public void run() {
		midiNotePlayer.sendMidi(signal.getSignalByte(), noteName.getMidi(), 24);
		if (signal.equals(MidiSignals.NOTE_ON)) {
			handler.postDelayed(new MidiRunnable(MidiSignals.NOTE_OFF, noteName, duration, handler, midiNotePlayer),
					duration);
		}
	}

	public NoteName getNoteName() {
		return noteName;
	}
}
