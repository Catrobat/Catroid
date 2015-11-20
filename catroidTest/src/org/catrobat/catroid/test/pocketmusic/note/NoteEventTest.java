/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteName;

public class NoteEventTest extends AndroidTestCase {

	public void testEquals1() {
		NoteEvent noteEvent1 = NoteEventTestDataFactory.createNoteEvent();
		NoteEvent noteEvent2 = NoteEventTestDataFactory.createNoteEvent();

		assertTrue("Notes not equal", noteEvent1.equals(noteEvent2));
	}

	public void testEquals2() {
		NoteEvent noteEvent1 = NoteEventTestDataFactory.createNoteEvent(NoteName.C1);
		NoteEvent noteEvent2 = NoteEventTestDataFactory.createNoteEvent(NoteName.C2);

		assertFalse("Notes not equal", noteEvent1.equals(noteEvent2));
	}

	public void testEquals3() {
		NoteName noteName = NoteName.C1;
		NoteEvent noteEvent1 = NoteEventTestDataFactory.createNoteEvent(noteName, true);
		NoteEvent noteEvent2 = NoteEventTestDataFactory.createNoteEvent(noteName, false);

		assertFalse("Notes not equal", noteEvent1.equals(noteEvent2));
	}

	public void testEquals4() {
		NoteEvent noteEvent1 = NoteEventTestDataFactory.createNoteEvent(NoteName.C1, true);
		NoteEvent noteEvent2 = NoteEventTestDataFactory.createNoteEvent(NoteName.C2, false);

		assertFalse("Notes not equal", noteEvent1.equals(noteEvent2));
	}

	public void testEquals5() {
		NoteEvent noteEvent = NoteEventTestDataFactory.createNoteEvent();

		assertFalse("Notes not equal", noteEvent.equals(null));
	}

	public void testEquals6() {
		NoteEvent noteEvent = NoteEventTestDataFactory.createNoteEvent();

		assertFalse("Notes not equal", noteEvent.equals(""));
	}

	public void testToString() {
		NoteEvent noteEvent = NoteEventTestDataFactory.createNoteEvent();
		String expectedString = "[NoteEvent] noteName= " + noteEvent.getNoteName()
				+ " noteOn=" + noteEvent.isNoteOn();

		assertEquals("Failed to convert to String", expectedString, noteEvent.toString());
	}

	public void testCopyNoteEvent() {
		NoteEvent noteEvent = NoteEventTestDataFactory.createNoteEvent();
		NoteEvent copyNoteEvent = new NoteEvent(noteEvent);

		assertTrue("Copy failed", noteEvent != copyNoteEvent);
		assertTrue("Copy failed", noteEvent.equals(copyNoteEvent));
	}
}
