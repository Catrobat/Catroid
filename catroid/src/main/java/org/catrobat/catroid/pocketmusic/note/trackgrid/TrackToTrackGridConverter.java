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
package org.catrobat.catroid.pocketmusic.note.trackgrid;

import android.util.SparseArray;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TrackToTrackGridConverter {

	private TrackToTrackGridConverter() {
	}

	public static TrackGrid convertTrackToTrackGrid(Track track, MusicalBeat beat, int beatsPerMinute) {
		Map<NoteName, Long> openNotes = new HashMap<NoteName, Long>();
		Map<NoteName, GridRow> gridRows = new HashMap<NoteName, GridRow>();

		NoteLength minNoteLength = beat.getNoteLength();

		for (Long tick : track.getSortedTicks()) {
			for (NoteEvent noteEvent : track.getNoteEventsForTick(tick)) {
				NoteName noteName = noteEvent.getNoteName();

				if (gridRows.get(noteName) == null) {
					gridRows.put(noteName, new GridRow(noteName, new SparseArray<List<GridRowPosition>>()));
				}

				if (noteEvent.isNoteOn()) {
					openNotes.put(noteName, tick);
				} else {
					long openTick = openNotes.get(noteName);
					NoteLength length = NoteLength.getNoteLengthFromTickDuration(tick - openTick, beatsPerMinute);

					int columnStartIndex = (int) (openTick / minNoteLength.toTicks(beatsPerMinute));
					int startBeatIndex = columnStartIndex / beat.getTopNumber();
					int endBeatIndex = ((columnStartIndex + (int) ((tick - openTick)
							/ minNoteLength.toTicks(beatsPerMinute))) - 1) / beat.getTopNumber();

					GridRowPosition gridRowPosition = new GridRowPosition(columnStartIndex % beat.getTopNumber(), length);

					for (int i = startBeatIndex; i <= endBeatIndex; i++) {
						if (null == gridRows.get(noteName).getGridRowPositions().get(i)) {
							gridRows.get(noteName).getGridRowPositions().put(i, new ArrayList<GridRowPosition>());
						}

						gridRows.get(noteName).getGridRowPositions().get(i).add(gridRowPosition);
					}
				}
			}
		}

		return new TrackGrid(track.getKey(), track.getInstrument(), beat, new ArrayList<>(gridRows.values()));
	}
}
