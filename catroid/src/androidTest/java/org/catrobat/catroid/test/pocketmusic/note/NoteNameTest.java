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
package org.catrobat.catroid.test.pocketmusic.note;

import android.test.AndroidTestCase;

import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteName;

public class NoteNameTest extends AndroidTestCase {

	public void testMidi() {
		NoteName[] noteNames = new NoteName[] {NoteName.C1, NoteName.C2, NoteName.C3, NoteName.C4,
				NoteName.C5, NoteName.C6, NoteName.C7, NoteName.C8};

		int startMidi = 24;
		int increment = 12;

		for (int i = 0; i < noteNames.length; i++) {
			int midi = startMidi + i * increment;
			assertEquals("Midi test failed", midi, noteNames[i].getMidi());
		}
	}

	public void testNext1() {
		NoteName noteName = NoteName.B0;
		NoteName nextNoteName = NoteName.C1;

		assertEquals("Next note not valid", nextNoteName, noteName.next());
	}

	public void testNext2() {
		NoteName lastNoteName = NoteName.C8;

		assertEquals("Next note not valid", lastNoteName, lastNoteName.next());
	}

	public void testPrevious1() {
		NoteName noteName = NoteName.C1;
		NoteName previousNoteName = NoteName.B0;

		assertEquals("Previous note not valid", previousNoteName, noteName.previous());
	}

	public void testPrevious2() {
		NoteName firstNoteName = NoteName.A0;

		assertEquals("Previous note not valid", firstNoteName, firstNoteName.previous());
	}

	public void testIsSigned1() {
		NoteName noteName = NoteName.C1;

		assertFalse("Note not signed correctly", noteName.isSigned());
	}

	public void testIsSigned2() {
		NoteName noteName = NoteName.C1S;

		assertTrue("Note not signed correctly", noteName.isSigned());
	}

	public void testGetNoteNameFromMidiValue1() {
		NoteName expectedNoteName = NoteName.A0;
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(midiValue);

		assertEquals("Invalid Note name", actualNoteName, expectedNoteName);
	}

	public void testGetNoteNameFromMidiValue2() {
		NoteName expectedNoteName = NoteName.C8;
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(midiValue);

		assertEquals("Invalid Note name", actualNoteName, expectedNoteName);
	}

	public void testGetNoteNameFromMidiValue3() {
		NoteName expectedNoteName = NoteName.C4;
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(midiValue);

		assertEquals("Invalid Note name", actualNoteName, expectedNoteName);
	}

	public void testGetNoteNameFromMidiValue4() {
		NoteName expectedNoteName = NoteName.DEFAULT_NOTE_NAME;

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(1337);

		assertEquals("Invalid Note name", actualNoteName, expectedNoteName);
	}

	public void testCalculateDistanceCountingNoneSignedNotesOnly1() {
		NoteName noteName1 = NoteName.D1;
		NoteName noteName2 = NoteName.C1S;
		int expectedDistance = 1;

		assertEquals("Failed to calculate distance", expectedDistance, NoteName
				.calculateDistanceCountingNoneSignedNotesOnly(noteName1, noteName2));
	}

	public void testCalculateDistanceCountingNoneSignedNotesOnly2() {
		NoteName noteName1 = NoteName.C1;
		NoteName noteName2 = NoteName.C1S;
		int expectedDistance = 0;

		assertEquals("Failed to calculate distance", expectedDistance, NoteName.calculateDistanceCountingNoneSignedNotesOnly(noteName1, noteName2));
	}

	public void testCalculateDistanceCountingNoneSignedNotesOnly3() {
		NoteName noteName1 = NoteName.D3;
		NoteName noteName2 = NoteName.B3;
		int expectedDistance = -5;

		assertEquals("Failed to calculate distance", expectedDistance, NoteName.calculateDistanceCountingNoneSignedNotesOnly(noteName1, noteName2));
	}

	public void testCalculateDistanceToMiddleLineCountingSignedNotesOnly1() {
		NoteName noteName = NoteName.B4;
		MusicalKey key = MusicalKey.VIOLIN;
		int expectedDistance = 0;

		assertEquals("Failed to calculate distance", expectedDistance, NoteName
				.calculateDistanceToMiddleLineCountingSignedNotesOnly(key, noteName));
	}

	public void testCalculateDistanceToMiddleLineCountingSignedNotesOnly2() {
		NoteName noteName = NoteName.C5;
		MusicalKey key = MusicalKey.VIOLIN;
		int expectedDistance = -1;

		assertEquals("Failed to calculate distance", expectedDistance, NoteName
				.calculateDistanceToMiddleLineCountingSignedNotesOnly(key, noteName));
	}

	public void testCalculateDistanceToMiddleLineCountingSignedNotesOnly3() {
		NoteName noteName = NoteName.A4;
		MusicalKey key = MusicalKey.VIOLIN;
		int expectedDistance = 1;

		assertEquals("Failed to calculate distance", expectedDistance, NoteName
				.calculateDistanceToMiddleLineCountingSignedNotesOnly(key, noteName));
	}
}
