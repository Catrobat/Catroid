/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseArray;

import org.catrobat.catroid.pocketmusic.mididriver.MidiDriver;
import org.catrobat.catroid.pocketmusic.mididriver.MidiRunnable;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSignals;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;

import java.util.ArrayList;
import java.util.List;

public class TrackGrid {

	private final MusicalKey key;
	private final MusicalInstrument instrument;
	private final MusicalBeat beat;
	private final List<GridRow> gridRows;
	private Handler handler;
	private MidiDriver midiDriver;
	private List<MidiRunnable> playRunnables = new ArrayList<>();

	public TrackGrid(MusicalKey key, MusicalInstrument instrument, MusicalBeat beat, List<GridRow> gridRows) {
		this.key = key;
		this.instrument = instrument;
		this.beat = beat;
		this.gridRows = gridRows;
		handler = new Handler();
		midiDriver = new MidiDriver();
	}

	public MusicalKey getKey() {
		return key;
	}

	public MusicalInstrument getInstrument() {
		return instrument;
	}

	public MusicalBeat getBeat() {
		return beat;
	}

	public List<GridRow> getGridRows() {
		return gridRows;
	}

	@Override
	public int hashCode() {
		int hashCode = 172;
		int primeWithGoodCollisionPrevention = 31;
		hashCode = primeWithGoodCollisionPrevention * hashCode + key.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + instrument.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + beat.hashCode();
		hashCode = primeWithGoodCollisionPrevention * hashCode + gridRows.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		TrackGrid reference = (TrackGrid) o;
		return reference.gridRows.containsAll(gridRows)
				&& gridRows.containsAll(reference.gridRows)
				&& reference.beat.equals(beat)
				&& reference.instrument.equals(instrument)
				&& reference.key.equals(key);
	}

	public GridRow getGridRowForNoteName(NoteName noteName) {
		for (GridRow gridRow : gridRows) {
			if (gridRow.getNoteName().equals(noteName)) {
				return gridRow;
			}
		}
		return null;
	}

	public void updateGridRowPosition(NoteName noteName, int columnIndex, NoteLength noteLength, boolean toggled) {
		GridRow gridRow = getGridRowForNoteName(noteName);
		if (null == gridRow) {
			List<GridRowPosition> gridRowPositions = new ArrayList<>();
			SparseArray<List<GridRowPosition>> array = new SparseArray<>();
			array.append(0, gridRowPositions);
			gridRow = new GridRow(noteName, array);
			gridRows.add(gridRow);
		}
		List<GridRowPosition> firstGridRowPositions = gridRow.getGridRowPositions().get(0);
		int indexInList = GridRowPosition.getGridRowPositionIndexInList(firstGridRowPositions, columnIndex);
		if (toggled) {
			if (indexInList == -1) {
				firstGridRowPositions.add(new GridRowPosition(columnIndex, noteLength));
				long playLength = NoteLength.QUARTER.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
				handler.post(new MidiRunnable(MidiSignals.NOTE_ON, noteName, playLength, handler, new MidiDriver()));
			}
		} else {
			if (indexInList >= 0) {
				firstGridRowPositions.remove(indexInList);
				if (firstGridRowPositions.isEmpty()) {
					gridRows.remove(gridRow);
				}
			}
		}
	}

	public void startPlayback() {
		playRunnables.clear();
		long playLength = NoteLength.QUARTER.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
		long currentTime = SystemClock.uptimeMillis();
		for (GridRow row : gridRows) {
			for (int i = 0; i < row.getGridRowPositions().size(); i++) {
				List<GridRowPosition> gridRowPositions = row.getGridRowPositions().get(row.getGridRowPositions()
						.keyAt(i));
				for (GridRowPosition position : gridRowPositions) {
					MidiRunnable runnable = new MidiRunnable(MidiSignals.NOTE_ON, row.getNoteName(), playLength - 10,
							handler, midiDriver);
					handler.postAtTime(runnable, currentTime + playLength * position.getColumnStartIndex() + 10);
					playRunnables.add(runnable);
				}
			}
		}
	}

	public void stopPlayback() {
		for (MidiRunnable r : playRunnables) {
			handler.removeCallbacks(r);
			handler.post(new MidiRunnable(MidiSignals.NOTE_OFF, r.getNoteName(), 0, handler, midiDriver));
		}
		playRunnables.clear();
	}
}
