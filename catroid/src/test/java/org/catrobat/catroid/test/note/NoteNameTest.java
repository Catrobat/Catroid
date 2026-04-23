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
package org.catrobat.catroid.test.note;

import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class NoteNameTest {

	@Test
	public void testMidi() {
		NoteName[] noteNames = new NoteName[] {NoteName.C1, NoteName.C2, NoteName.C3, NoteName.C4,
				NoteName.C5, NoteName.C6, NoteName.C7, NoteName.C8};

		int startMidi = 24;
		int increment = 12;

		for (int i = 0; i < noteNames.length; i++) {
			int midi = startMidi + i * increment;
			assertEquals(midi, noteNames[i].getMidi());
		}
	}

	@Test
	public void testNext1() {
		NoteName noteName = NoteName.B0;
		NoteName nextNoteName = NoteName.C1;

		assertEquals(nextNoteName, noteName.next());
	}

	@Test
	public void testNext2() {
		NoteName lastNoteName = NoteName.EXT130;

		assertEquals(lastNoteName, lastNoteName.next());
	}

	@Test
	public void testPrevious1() {
		NoteName noteName = NoteName.C1;
		NoteName previousNoteName = NoteName.B0;

		assertEquals(previousNoteName, noteName.previous());
	}

	@Test
	public void testPrevious2() {
		NoteName firstNoteName = NoteName.EXT0;

		assertEquals(firstNoteName, firstNoteName.previous());
	}

	@Test
	public void testIsSigned1() {
		NoteName noteName = NoteName.C1;

		assertFalse(noteName.isSigned());
	}

	@Test
	public void testIsSigned2() {
		NoteName noteName = NoteName.C1S;

		assertTrue(noteName.isSigned());
	}

	@Test
	public void testGetNoteNameFromMidiValue1() {
		NoteName expectedNoteName = NoteName.A0;
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(midiValue);

		assertEquals(actualNoteName, expectedNoteName);
	}

	@Test
	public void testGetNoteNameFromMidiValue2() {
		NoteName expectedNoteName = NoteName.C8;
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(midiValue);

		assertEquals(actualNoteName, expectedNoteName);
	}

	@Test
	public void testGetNoteNameFromMidiValue3() {
		NoteName expectedNoteName = NoteName.C4;
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(midiValue);

		assertEquals(actualNoteName, expectedNoteName);
	}

	@Test
	public void testGetNoteNameFromMidiValue4() {
		NoteName expectedNoteName = NoteName.DEFAULT_NOTE_NAME;

		NoteName actualNoteName = NoteName.getNoteNameFromMidiValue(1337);

		assertEquals(actualNoteName, expectedNoteName);
	}

	@Test
	public void testCalculateDistanceCountingNoneSignedNotesOnly1() {
		NoteName noteName1 = NoteName.D1;
		NoteName noteName2 = NoteName.C1S;
		int expectedDistance = 1;

		assertEquals(expectedDistance, NoteName.calculateDistanceCountingNoneSignedNotesOnly(noteName1, noteName2));
	}

	@Test
	public void testCalculateDistanceCountingNoneSignedNotesOnly2() {
		NoteName noteName1 = NoteName.C1;
		NoteName noteName2 = NoteName.C1S;
		int expectedDistance = 0;

		assertEquals(expectedDistance, NoteName.calculateDistanceCountingNoneSignedNotesOnly(noteName1, noteName2));
	}

	@Test
	public void testCalculateDistanceCountingNoneSignedNotesOnly3() {
		NoteName noteName1 = NoteName.D3;
		NoteName noteName2 = NoteName.B3;
		int expectedDistance = -5;

		assertEquals(expectedDistance, NoteName.calculateDistanceCountingNoneSignedNotesOnly(noteName1, noteName2));
	}

	@Test
	public void testCalculateDistanceToMiddleLineCountingSignedNotesOnly1() {
		NoteName noteName = NoteName.B4;
		MusicalKey key = MusicalKey.VIOLIN;
		int expectedDistance = 0;

		assertEquals(expectedDistance, NoteName.calculateDistanceToMiddleLineCountingSignedNotesOnly(key, noteName));
	}

	@Test
	public void testCalculateDistanceToMiddleLineCountingSignedNotesOnly2() {
		NoteName noteName = NoteName.C5;
		MusicalKey key = MusicalKey.VIOLIN;
		int expectedDistance = -1;

		assertEquals(expectedDistance, NoteName.calculateDistanceToMiddleLineCountingSignedNotesOnly(key, noteName));
	}

	@Test
	public void testCalculateDistanceToMiddleLineCountingSignedNotesOnly3() {
		NoteName noteName = NoteName.A4;
		MusicalKey key = MusicalKey.VIOLIN;
		int expectedDistance = 1;

		assertEquals(expectedDistance, NoteName.calculateDistanceToMiddleLineCountingSignedNotesOnly(key, noteName));
	}
}
