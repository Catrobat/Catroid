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

import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;

public class TrackTest extends AndroidTestCase {

	public void testGetInstrument() {
		Track track = TrackTestDataFactory.createTrack();

		assertEquals("Failed to create Track", MusicalInstrument.ACOUSTIC_GRAND_PIANO, track.getInstrument());
	}

	public void testAddNoteEvent1() {
		Track track = TrackTestDataFactory.createTrack();
		track.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());

		assertEquals("Failed to add note Event", 1, track.size());
	}

	public void testAddNoteEvent2() {
		Track track = TrackTestDataFactory.createTrack();
		track.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());
		track.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());

		assertEquals("Failed to add note Event", 2, track.size());
	}

	public void testGetNoteEventsForTick() {
		Track track = TrackTestDataFactory.createTrack();
		long tick = 0;
		NoteEvent noteEvent = NoteEventTestDataFactory.createNoteEvent();
		track.addNoteEvent(tick, noteEvent);

		assertEquals("Failed to get Note event for Tick", noteEvent, track.getNoteEventsForTick(tick).get(0));
	}

	public void testEquals1() {
		long tick = 0;
		Track track1 = TrackTestDataFactory.createTrack();
		track1.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent());
		Track track2 = TrackTestDataFactory.createTrack();
		track2.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent());

		assertTrue("Equal comparison failed", track1.equals(track2));
	}

	public void testEquals2() {
		long tick = 0;
		Track track1 = TrackTestDataFactory.createTrack();
		track1.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(NoteName.C1));
		Track track2 = TrackTestDataFactory.createTrack();
		track2.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(NoteName.C2));

		assertFalse("Equal comparison failed", track1.equals(track2));
	}

	public void testEquals3() {
		Track track1 = TrackTestDataFactory.createTrack();
		track1.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());
		Track track2 = TrackTestDataFactory.createTrack();

		assertFalse("Equal comparison failed", track1.equals(track2));
	}

	public void testEquals4() {
		Track track1 = TrackTestDataFactory.createTrack(MusicalInstrument.ACCORDION);
		Track track2 = TrackTestDataFactory.createTrack(MusicalInstrument.ALTO_SAX);

		assertFalse("Equal comparison failed", track1.equals(track2));
	}

	public void testEquals5() {
		Track track1 = TrackTestDataFactory.createTrack(MusicalKey.BASS);
		Track track2 = TrackTestDataFactory.createTrack(MusicalKey.VIOLIN);

		assertFalse("Equal comparison failed", track1.equals(track2));
	}

	public void testEquals6() {
		Track track = TrackTestDataFactory.createTrack();

		assertFalse("Equal comparison failed", track.equals(null));
	}

	public void testEquals7() {
		Track track = TrackTestDataFactory.createTrack();

		assertFalse("Equal comparison failed", track.equals(""));
	}

	public void testToString() {
		Track track = TrackTestDataFactory.createTrack();
		String expectedString = "[Track] instrument=" + MusicalInstrument.ACOUSTIC_GRAND_PIANO
				+ " key=" + track.getKey()
				+ " size=" + track.size();

		assertEquals("Failed to create String from track", expectedString, track.toString());
	}

	public void testSetTickBasedOnTrack1() {
		Track track = TrackTestDataFactory.createTrack();

		assertEquals("Failed to set Tick based on Track", 0, track.getLastTick());
	}

	public void testSetTickBasedOnTrack2() {
		Track track = TrackTestDataFactory.createTrack();
		long tick = 0;

		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(true));
		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);
		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(false));

		assertEquals("Failed to set Tick based on Track", tick, track.getLastTick());
	}

	public void testCopyTrack() {
		Track track = TrackTestDataFactory.createSimpleTrack();
		Track copyTrack = new Track(track);

		assertTrue("Failed to create copy from Track", track != copyTrack);
		assertTrue("Copied Track not equal to original Track", track.equals(copyTrack));
	}

	public void testEmpty1() {
		Track track = TrackTestDataFactory.createTrack();

		assertTrue("Track not empty", track.empty());
	}

	public void testEmpty2() {
		Track track = TrackTestDataFactory.createTrack();
		long tick = 0;

		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent());

		assertFalse("Track not empty", track.empty());
	}

	public void testGetTotalTimeInMilliseconds() {
		Track track = TrackTestDataFactory.createTrack();
		NoteLength noteLength = NoteLength.QUARTER;
		long expecteTotalTime = noteLength.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
		long tick = 0;

		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(true));
		tick += noteLength.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);
		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(false));

		assertEquals("Failed to get total time in Milliseconds", expecteTotalTime, track.getTotalTimeInMilliseconds());
	}
}
