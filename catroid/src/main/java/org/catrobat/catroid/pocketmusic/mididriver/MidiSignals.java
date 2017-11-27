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

public enum MidiSignals {

	MIDI_TIME_CODE((byte) 0xF1),
	SONG_POSITION_POBYTEER((byte) 0xF2),
	SONG_SELECT((byte) 0xF3),
	TUNE_REQUEST((byte) 0xF6),
	END_OF_EXCLUSIVE((byte) 0xF7),
	TIMING_CLOCK((byte) 0xF8),
	START((byte) 0xFA),
	CONTINUE((byte) 0xFB),
	STOP((byte) 0xFC),
	ACTIVE_SENSING((byte) 0xFE),
	SYSTEM_RESET((byte) 0xFF),
	NOTE_OFF((byte) 0x80),
	NOTE_ON((byte) 0x90),
	POLY_PRESSURE((byte) 0xA0),
	CONTROL_CHANGE((byte) 0xB0),
	PROGRAM_CHANGE((byte) 0xC0),
	CHANNEL_PRESSURE((byte) 0xD0),
	PITCH_BEND((byte) 0xE0);

	private byte signalByte;

	MidiSignals(byte signalByte) {
		this.signalByte = signalByte;
	}

	public byte getSignalByte() {
		return signalByte;
	}
}
