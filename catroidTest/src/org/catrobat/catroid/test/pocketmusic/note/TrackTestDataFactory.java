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

import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;

public final class TrackTestDataFactory {

	private TrackTestDataFactory() {
	}

	public static Track createTrack(MusicalKey key, MusicalInstrument instrument) {
		return new Track(key, instrument);
	}

	public static Track createTrack() {
		return createTrack(MusicalInstrument.ACOUSTIC_GRAND_PIANO);
	}

	public static Track createTrack(MusicalInstrument instrument) {
		return new Track(MusicalKey.VIOLIN, instrument);
	}

	public static Track createTrack(MusicalKey key) {
		return new Track(key, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
	}

	public static Track createSimpleTrack() {
		Track track = createTrack();

		long tick = 0;

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.E4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.E4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.F4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.F4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C5, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C5, false));

		return track;
	}

	public static Track createTrackWithBreak() {
		Track track = TrackTestDataFactory.createTrack();

		long tick = 0;

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));
		track.addNoteEvent(tick, new NoteEvent(NoteName.D4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.D4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.E4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.E4, false));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));

		return track;
	}

	public static Track createTrackWithSeveralBreaks() {
		Track track = TrackTestDataFactory.createTrack(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		long tick = 0;

		tick += NoteLength.HALF.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);
		tick += NoteLength.SIXTEENTH.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));

		return track;
	}

	public static Track createSemiComplexTrack(MusicalInstrument instrument) {
		Track track = new Track(MusicalKey.VIOLIN, instrument);

		long tick = 0;

		track.addNoteEvent(tick, new NoteEvent(NoteName.C2, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C2, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C1, true));
		track.addNoteEvent(tick, new NoteEvent(NoteName.D1, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C1, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.D1, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C1, true));
		track.addNoteEvent(tick, new NoteEvent(NoteName.D1, true));

		tick += NoteLength.QUARTER_DOT.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C1, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.D1, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.E1, true));
		track.addNoteEvent(tick, new NoteEvent(NoteName.F1, true));

		tick += NoteLength.QUARTER_DOT.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.E1, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.F1, false));

		return track;
	}

	public static Track createTrackWithAccords() {
		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		long tick = 0;

		track.addNoteEvent(tick, new NoteEvent(NoteName.C3, true));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C3, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C3, true));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C3, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C3, true));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, true));

		tick += NoteLength.QUARTER.toTicks(Project.DEFAULT_BEATS_PER_MINUTE);

		track.addNoteEvent(tick, new NoteEvent(NoteName.C3, false));
		track.addNoteEvent(tick, new NoteEvent(NoteName.C4, false));

		return track;
	}
}
