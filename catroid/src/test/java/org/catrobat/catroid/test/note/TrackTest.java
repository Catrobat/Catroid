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

import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;

import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class TrackTest {

	@Test
	public void testGetInstrument() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertEquals(MusicalInstrument.ACOUSTIC_GRAND_PIANO, track.getInstrument());
	}

	@Test
	public void testAddNoteEvent1() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		track.addNoteEvent(0, new NoteEvent(NoteName.C4, true));

		assertEquals(1, track.size());
	}

	@Test
	public void testAddNoteEvent2() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		track.addNoteEvent(0, new NoteEvent(NoteName.C4, true));
		track.addNoteEvent(0, new NoteEvent(NoteName.C4, true));

		assertEquals(1, track.size());
	}

	@Test
	public void testGetNoteEventsForTick() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		long tick = 0;
		NoteEvent noteEvent = new NoteEvent(NoteName.C4, true);
		track.addNoteEvent(tick, noteEvent);

		assertEquals(noteEvent, track.getNoteEventsForTick(tick).get(0));
	}

	@Test
	public void testEquals1() {
		long tick = 0;
		Track track1 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		track1.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));
		Track track2 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		track2.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		assertEquals(track1, track2);
	}

	@Test
	public void testEquals2() {
		long tick = 0;
		Track track1 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		track1.addNoteEvent(tick, new NoteEvent(NoteName.C1, true));
		Track track2 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		track2.addNoteEvent(tick, new NoteEvent(NoteName.C2, true));

		assertNotEquals(track2, track1);
	}

	@Test
	public void testEquals3() {
		Track track1 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		track1.addNoteEvent(0, new NoteEvent(NoteName.C4, true));
		Track track2 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertNotEquals(track2, track1);
	}

	@Test
	public void testEquals4() {
		Track track1 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACCORDION);
		Track track2 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ALTO_SAX);

		assertNotEquals(track2, track1);
	}

	@Test
	public void testEquals5() {
		Track track1 = new Track(MusicalKey.BASS, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		Track track2 = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertNotEquals(track2, track1);
	}

	@Test
	public void testEquals6() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertNotNull(track);
	}

	@Test
	public void testEquals7() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertNotEquals("", track);
	}

	@Test
	public void testToString() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		String expectedString = "[Track] instrument=" + MusicalInstrument.ACOUSTIC_GRAND_PIANO
				+ " key=" + track.getKey()
				+ " size=" + track.size();

		assertEquals(expectedString, track.toString());
	}

	@Test
	public void testSetTickBasedOnTrack1() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertEquals(0, track.getLastTick());
	}

	@Test
	public void testSetTickBasedOnTrack2() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		long tick = 0;

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));
		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));

		assertEquals(tick, track.getLastTick());
	}

	@Test
	public void testCopyTrack() {
		Track track = TrackTestDataFactory.createSimpleTrack();
		Track copyTrack = new Track(track);

		assertNotSame(track, copyTrack);
		assertEquals(track, copyTrack);
	}

	@Test
	public void testEmpty1() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		assertTrue(track.empty());
	}

	@Test
	public void testEmpty2() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		long tick = 0;

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		assertFalse(track.empty());
	}

	@Test
	public void testGetTotalTimeInMilliseconds() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		NoteLength noteLength = NoteLength.QUARTER;
		long expecteTotalTime = noteLength.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
		long tick = 0;

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));
		tick += noteLength.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));

		assertEquals(expecteTotalTime, track.getTotalTimeInMilliseconds());
	}
}
