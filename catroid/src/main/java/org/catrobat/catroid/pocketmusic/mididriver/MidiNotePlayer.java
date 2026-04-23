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

package org.catrobat.catroid.pocketmusic.mididriver;

import org.billthefarmer.mididriver.MidiDriver;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.Project;

public final class MidiNotePlayer implements MidiDriver.OnMidiStartListener {

	private final MidiDriver midiDriver;
	private static boolean initialized = false;

	public MidiNotePlayer() {
		midiDriver = new MidiDriver();
		midiDriver.setOnMidiStartListener(this);
	}

	@Override
	public void onMidiStart() {
		sendMidi(MidiSignals.PROGRAM_CHANGE.getSignalByte(), (byte) Project.DEFAULT_INSTRUMENT.getProgram());
		midiDriver.config();
	}

	public void start() {
		midiDriver.start();
		initialized = true;
	}

	public void stop() {
		midiDriver.stop();
	}

	private void sendMidi(int midiSignal, int message) {
		byte[] midiMessage = new byte[2];

		midiMessage[0] = (byte) midiSignal;
		midiMessage[1] = (byte) message;

		midiDriver.write(midiMessage);
	}

	public void sendMidi(int midiSignal, int note, int velocity) {
		byte[] midiMessage = new byte[3];

		midiMessage[0] = (byte) midiSignal;
		midiMessage[1] = (byte) note;
		midiMessage[2] = (byte) velocity;

		midiDriver.write(midiMessage);
	}

	public void setInstrument(byte channel, MusicalInstrument instrument) {
		byte status = MidiSignals.PROGRAM_CHANGE.getSignalByte();
		status |= channel;
		sendMidi(status, (byte) instrument.getProgram());
	}

	public void setVolume(byte channel, int volume) {
		byte status = MidiSignals.CONTROL_CHANGE.getSignalByte();
		status |= channel;
		sendMidi(status, MidiSignals.CHANNEL_VOLUME.getSignalByte(), volume);
	}

	public static boolean isInitialized() {
		return initialized;
	}
}
