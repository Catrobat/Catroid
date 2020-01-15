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

package org.catrobat.catroid.pocketmusic.note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NoteName {

	public static final int DEFAULT_NOTE_MIDI = 24;
	public static final int MAX_NOTE_MIDI = 108;

	private static final int MIN_NOTE_MIDI = 21;
	private static final List<Integer> SIGNED_NOTES = Arrays.asList(1, 3, 6, 8, 10);
	private static final List<String> NOTE_NAMES_STARTS = Arrays.asList("C", "C", "D", "D", "E",
			"F", "F", "G", "G", "A", "A", "B");

	private final String name;
	private final int midi;
	private final boolean signed;

	public NoteName(int midi) {
		int midiToUse = midi;
		if (midi < MIN_NOTE_MIDI) {
			midiToUse = MIN_NOTE_MIDI;
		} else if (midi > MAX_NOTE_MIDI) {
			midiToUse = MAX_NOTE_MIDI;
		}
		this.midi = midiToUse;

		int octave = (getMidi() / 12) - 1;
		int noteInOctave = getMidi() % 12;
		signed = SIGNED_NOTES.contains(noteInOctave);
		name = NOTE_NAMES_STARTS.get(noteInOctave) + octave + (signed ? "S" : "");
	}

	public static List<NoteName> getAllPossibleOctaveStarts() {
		List<NoteName> octaveStaringNotes = new ArrayList<>();

		for (int i = MIN_NOTE_MIDI; i < MAX_NOTE_MIDI; i++) {
			if (i % 12 == 0) {
				octaveStaringNotes.add(new NoteName(i));
			}
		}

		return octaveStaringNotes;
	}

	public NoteName next() {
		return new NoteName(this.midi + 1);
	}

	public NoteName previous() {
		return new NoteName(this.midi - 1);
	}

	public String getName() {
		return name;
	}

	public int getMidi() {
		return midi;
	}

	public boolean isSigned() {
		return signed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NoteName noteName = (NoteName) o;
		return midi == noteName.midi;
	}

	@Override
	public int hashCode() {
		return Objects.hash(midi);
	}

	@Override
	public String toString() {
		return name;
	}
}
