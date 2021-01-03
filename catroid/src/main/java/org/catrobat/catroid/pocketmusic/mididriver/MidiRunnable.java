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
package org.catrobat.catroid.pocketmusic.mididriver;

import android.os.Handler;

import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.ui.PianoView;

public class MidiRunnable implements Runnable {

	private final MidiSignals signal;
	private final NoteName noteName;
	private final long duration;
	private final Handler handler;
	private final MidiNotePlayer midiNotePlayer;
	private final PianoView pianoView;
	private final byte channel;
	private long scheduledTime = 0;
	private boolean manualNoteOff = false;
	private NoteName pianoRow;

	public MidiRunnable(MidiSignals signal, NoteName noteName, long duration, Handler handler,
			MidiNotePlayer midiNotePlayer, PianoView pianoView, byte channel) {
		this.signal = signal;
		this.noteName = noteName;
		this.duration = duration;
		this.handler = handler;
		this.midiNotePlayer = midiNotePlayer;
		this.pianoView = pianoView;
		this.channel = channel;
	}

	public MidiRunnable(MidiSignals signal, NoteName noteName, long duration, Handler handler,
			MidiNotePlayer midiNotePlayer, PianoView pianoView, byte channel, NoteName pianoRow) {
		this.signal = signal;
		this.noteName = noteName;
		this.duration = duration;
		this.handler = handler;
		this.midiNotePlayer = midiNotePlayer;
		this.pianoView = pianoView;
		this.channel = channel;
		this.pianoRow = pianoRow;
	}

	void setManualNoteOff(boolean manual) {
		manualNoteOff = manual;
	}

	void setScheduledTime(long time) {
		scheduledTime = time;
	}

	long getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public void run() {
		byte status = signal.getSignalByte();
		status |= channel;
		midiNotePlayer.sendMidi(status, noteName.getMidi(), 127);
		if (pianoView != null && pianoRow != null) {
			pianoView.setButtonColor(pianoRow, MidiSignals.NOTE_ON.equals(signal));
		}
		if (signal.equals(MidiSignals.NOTE_ON) && !manualNoteOff) {
			handler.postDelayed(new MidiRunnable(MidiSignals.NOTE_OFF, noteName, duration, handler, midiNotePlayer,
					pianoView, channel, pianoRow), duration);
		}
	}

	public NoteName getNoteName() {
		return noteName;
	}
}
