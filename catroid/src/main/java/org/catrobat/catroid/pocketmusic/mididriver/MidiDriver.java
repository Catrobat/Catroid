/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;

public final class MidiDriver implements NativeMidiDriver.OnMidiStartListener {

	private final NativeMidiDriver nativeMidiDriver;

	public MidiDriver() {
		nativeMidiDriver = new NativeMidiDriver();
		nativeMidiDriver.setOnMidiStartListener(this);
	}

	@Override
	public void onMidiStart() {
		sendMidi(MidiSignals.PROGRAM_CHANGE.getSignalByte(),
				MusicalInstrument.ACOUSTIC_GRAND_PIANO.getInstrumentByte());
		nativeMidiDriver.config();
	}

	public void start() {
		nativeMidiDriver.start();
	}

	public void stop() {
		nativeMidiDriver.stop();
	}

	public void sendMidi(int midiSignal, int instrument) {
		byte[] midiMessage = new byte[2];

		midiMessage[0] = (byte) midiSignal;
		midiMessage[1] = (byte) instrument;

		nativeMidiDriver.write(midiMessage);
	}

	public void sendMidi(int midiSignal, int note, int velocity) {
		byte[] midiMessage = new byte[3];

		midiMessage[0] = (byte) midiSignal;
		midiMessage[1] = (byte) note;
		midiMessage[2] = (byte) velocity;

		nativeMidiDriver.write(midiMessage);
	}
}
