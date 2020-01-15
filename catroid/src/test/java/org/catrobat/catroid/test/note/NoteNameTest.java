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
package org.catrobat.catroid.test.note;

import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class NoteNameTest {

	@Test
	public void testNext1() {
		NoteName noteName = new NoteName(23);
		NoteName nextNoteName = new NoteName(24);

		assertEquals(nextNoteName, noteName.next());
	}

	@Test
	public void testNext2() {
		NoteName lastNoteName = new NoteName(108);

		assertEquals(lastNoteName, lastNoteName.next());
	}

	@Test
	public void testPrevious1() {
		NoteName noteName = new NoteName(24);
		NoteName previousNoteName = new NoteName(23);

		assertEquals(previousNoteName, noteName.previous());
	}

	@Test
	public void testPrevious2() {
		NoteName firstNoteName = new NoteName(21);

		assertEquals(firstNoteName, firstNoteName.previous());
	}

	@Test
	public void testIsSigned1() {
		NoteName noteName = new NoteName(24);

		assertFalse(noteName.isSigned());
	}

	@Test
	public void testIsSigned2() {
		NoteName noteName = new NoteName(25);

		assertTrue(noteName.isSigned());
	}

	@Test
	public void testGetNoteNameFromMidiValue1() {
		NoteName expectedNoteName = new NoteName(21);
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = new NoteName(midiValue);

		assertEquals(actualNoteName, expectedNoteName);
	}

	@Test
	public void testGetNoteNameFromMidiValue2() {
		NoteName expectedNoteName = new NoteName(108);
		int midiValue = expectedNoteName.getMidi();

		NoteName actualNoteName = new NoteName(midiValue);

		assertEquals(actualNoteName, expectedNoteName);
	}

	@Test
	public void testGetNoteNameFromMidiValue4() {
		NoteName expectedNoteName = new NoteName(NoteName.MAX_NOTE_MIDI);

		NoteName actualNoteName = new NoteName(1337);

		assertEquals(actualNoteName, expectedNoteName);
	}

	@Test
	public void testGetAllPossibleOctaveStarts() {
		int numberOfStartingNotes = 7;
		NoteName firstNoteName = new NoteName(NoteName.DEFAULT_NOTE_MIDI);
		NoteName lastNoteName = new NoteName(96);

		List<NoteName> octaveStarts = NoteName.getAllPossibleOctaveStarts();

		assertEquals(numberOfStartingNotes, octaveStarts.size());
		assertEquals(firstNoteName, octaveStarts.get(0));
		assertEquals(lastNoteName, octaveStarts.get(octaveStarts.size() - 1));
	}
}
