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
package org.catrobat.catroid.test.pocketmusic.note;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackTest {

	@Test
	public void testGetInstrument() {
		Track track = TrackTestDataFactory.createTrack();

		assertEquals(MusicalInstrument.ACOUSTIC_GRAND_PIANO, track.getInstrument());
	}

	@Test
	public void testAddNoteEvent1() {
		Track track = TrackTestDataFactory.createTrack();
		track.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());

		assertEquals(1, track.size());
	}

	@Test
	public void testAddNoteEvent2() {
		Track track = TrackTestDataFactory.createTrack();
		track.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());
		track.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());

		assertEquals(1, track.size());
	}

	@Test
	public void testGetNoteEventsForTick() {
		Track track = TrackTestDataFactory.createTrack();
		long tick = 0;
		NoteEvent noteEvent = NoteEventTestDataFactory.createNoteEvent();
		track.addNoteEvent(tick, noteEvent);

		assertEquals(noteEvent, track.getNoteEventsForTick(tick).get(0));
	}

	@Test
	public void testEquals1() {
		long tick = 0;
		Track track1 = TrackTestDataFactory.createTrack();
		track1.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent());
		Track track2 = TrackTestDataFactory.createTrack();
		track2.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent());

		assertEquals(track1, track2);
	}

	@Test
	public void testEquals2() {
		long tick = 0;
		Track track1 = TrackTestDataFactory.createTrack();
		track1.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(NoteName.C1));
		Track track2 = TrackTestDataFactory.createTrack();
		track2.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(NoteName.C2));

		assertThat(track1, is(not(equalTo(track2))));
	}

	@Test
	public void testEquals3() {
		Track track1 = TrackTestDataFactory.createTrack();
		track1.addNoteEvent(0, NoteEventTestDataFactory.createNoteEvent());
		Track track2 = TrackTestDataFactory.createTrack();

		assertThat(track1, is(not(equalTo(track2))));
	}

	@Test
	public void testEquals4() {
		Track track1 = TrackTestDataFactory.createTrack(MusicalInstrument.ACCORDION);
		Track track2 = TrackTestDataFactory.createTrack(MusicalInstrument.ALTO_SAX);

		assertThat(track1, is(not(equalTo(track2))));
	}

	@Test
	public void testEquals5() {
		Track track1 = TrackTestDataFactory.createTrack(MusicalKey.BASS);
		Track track2 = TrackTestDataFactory.createTrack(MusicalKey.VIOLIN);

		assertThat(track1, is(not(equalTo(track2))));
	}

	@Test
	public void testEquals6() {
		Track track = TrackTestDataFactory.createTrack();

		assertThat(track, is(not(equalTo(null))));
	}

	@Test
	public void testEquals7() {
		Track track = TrackTestDataFactory.createTrack();

		assertFalse(track.equals(""));
	}

	@Test
	public void testToString() {
		Track track = TrackTestDataFactory.createTrack();
		String expectedString = "[Track] instrument=" + MusicalInstrument.ACOUSTIC_GRAND_PIANO
				+ " key=" + track.getKey()
				+ " size=" + track.size();

		assertEquals(expectedString, track.toString());
	}

	@Test
	public void testSetTickBasedOnTrack1() {
		Track track = TrackTestDataFactory.createTrack();

		assertEquals(0, track.getLastTick());
	}

	@Test
	public void testSetTickBasedOnTrack2() {
		Track track = TrackTestDataFactory.createTrack();
		long tick = 0;

		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(true));
		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);
		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(false));

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
		Track track = TrackTestDataFactory.createTrack();

		assertTrue(track.empty());
	}

	@Test
	public void testEmpty2() {
		Track track = TrackTestDataFactory.createTrack();
		long tick = 0;

		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent());

		assertFalse(track.empty());
	}

	@Test
	public void testGetTotalTimeInMilliseconds() {
		Track track = TrackTestDataFactory.createTrack();
		NoteLength noteLength = NoteLength.QUARTER;
		long expecteTotalTime = noteLength.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
		long tick = 0;

		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(true));
		tick += noteLength.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);
		track.addNoteEvent(tick, NoteEventTestDataFactory.createNoteEvent(false));

		assertEquals(expecteTotalTime, track.getTotalTimeInMilliseconds());
	}
}
