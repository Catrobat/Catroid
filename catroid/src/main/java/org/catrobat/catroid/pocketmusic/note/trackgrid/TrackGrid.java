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
package org.catrobat.catroid.pocketmusic.note.trackgrid;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import org.catrobat.catroid.pocketmusic.mididriver.MidiNotePlayer;
import org.catrobat.catroid.pocketmusic.mididriver.MidiPlayer;
import org.catrobat.catroid.pocketmusic.mididriver.MidiRunnable;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSignals;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.ui.PianoView;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;

import java.util.ArrayList;
import java.util.List;

import androidx.collection.SparseArrayCompat;

public class TrackGrid {

	private static final int INDEX_TO_COUNT_OFFSET = 1;
	private static final int SOUND_OFFSET = 10;
	private final MusicalKey key;
	private final MusicalInstrument instrument;
	private final MusicalBeat beat;
	private final List<GridRow> gridRows;
	private Handler handler;
	private MidiNotePlayer midiDriver;
	private List<MidiRunnable> playRunnables = new ArrayList<>();

	public TrackGrid(MusicalKey key, MusicalInstrument instrument, MusicalBeat beat, List<GridRow> gridRows) {
		this.key = key;
		this.instrument = instrument;
		this.beat = beat;
		this.gridRows = gridRows;
		handler = new Handler(Looper.getMainLooper());
		midiDriver = new MidiNotePlayer();
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

	public void updateGridRowPosition(NoteName noteName, int columnIndex, NoteLength noteLength, int tactIndex,
			boolean toggled) {
		GridRow gridRow = getGridRowForNoteName(noteName);
		if (null == gridRow) {
			gridRow = createNewGridRow(noteName);
		}
		if (gridRow.getGridRowPositions().indexOfKey(tactIndex) < 0) {
			appendNoteListAtPosition(gridRow.getGridRowPositions(), tactIndex);
		}
		List<GridRowPosition> currentGridRowPositions = gridRow.getGridRowPositions().get(tactIndex);
		int indexInList = GridRowPosition.getGridRowPositionIndexInList(currentGridRowPositions, columnIndex);
		if (toggled) {
			if (indexInList == -1) {
				Log.d("TrackGrid", String.format("Added GridRowPosition with name %s on Tact %d with columnIndex %d "
						+ "and noteLength %s. ", noteName.name(), tactIndex, columnIndex, noteLength.toString()));
				currentGridRowPositions.add(new GridRowPosition(columnIndex, noteLength));
				long playLength = NoteLength.QUARTER.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
				int adjustedMidiValue = MidiPlayer.getAdjustedMidiValue(Project.DEFAULT_INSTRUMENT,
						noteName.getMidi());
				handler.post(new MidiRunnable(MidiSignals.NOTE_ON, NoteName.getNoteNameFromMidiValue(adjustedMidiValue),
						playLength, handler, midiDriver, null, (byte) 0));
			}
		} else {
			if (indexInList >= 0) {
				currentGridRowPositions.remove(indexInList);
				Log.d("TrackGrid", String.format("Removed GridRowPosition with name %s on Tact %d with columnIndex %d "
								+ "and noteLength %s.", noteName.name(), tactIndex, columnIndex,
						noteLength.toString()));
				if (currentGridRowPositions.isEmpty()) {
					gridRow.getGridRowPositions().remove(tactIndex);
				}
			}
		}
	}

	private void appendNoteListAtPosition(SparseArrayCompat<List<GridRowPosition>> array, int tactIndex) {
		List<GridRowPosition> gridRowPositions = new ArrayList<>(beat.getTopNumber());
		array.append(tactIndex, gridRowPositions);
	}

	private GridRow createNewGridRow(NoteName noteName) {
		SparseArrayCompat<List<GridRowPosition>> array = new SparseArrayCompat<>();
		GridRow gridRow = new GridRow(noteName, array);
		gridRows.add(gridRow);
		return gridRow;
	}

	public int getTactCount() {
		int tactcount = 0;
		for (GridRow gridRow : gridRows) {
			SparseArrayCompat<List<GridRowPosition>> gridRowPositions = gridRow.getGridRowPositions();
			for (int i = 0; i < gridRowPositions.size(); i++) {
				int tactForGridRow = gridRowPositions.keyAt(i);
				if (tactForGridRow > tactcount) {
					tactcount = tactForGridRow;
				}
			}
		}
		return tactcount + INDEX_TO_COUNT_OFFSET;
	}

	public void startPlayback(PianoView pianoView) {
		playRunnables.clear();
		long playLength = NoteLength.QUARTER.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
		long currentTime = SystemClock.uptimeMillis();
		for (GridRow row : gridRows) {
			for (int i = 0; i < row.getGridRowPositions().size(); i++) {
				int tactIndex = row.getGridRowPositions().keyAt(i);
				long tactOffset = playLength * TrackRowView.quarterCount * tactIndex;
				List<GridRowPosition> gridRowPositions = row.getGridRowPositions().get(tactIndex);
				for (GridRowPosition position : gridRowPositions) {
					int adjustedMidiValue = MidiPlayer.getAdjustedMidiValue(Project.DEFAULT_INSTRUMENT,
									row.getNoteName().getMidi());
					MidiRunnable runnable = new MidiRunnable(MidiSignals.NOTE_ON, NoteName.getNoteNameFromMidiValue(adjustedMidiValue),
							playLength - SOUND_OFFSET, handler, midiDriver, pianoView, (byte) 0,
							row.getNoteName());

					handler.postAtTime(runnable, currentTime + tactOffset + playLength
							* position.getColumnStartIndex() + SOUND_OFFSET);
					playRunnables.add(runnable);
				}
			}
		}
	}

	public void stopPlayback(PianoView pianoView) {
		for (MidiRunnable r : playRunnables) {
			handler.removeCallbacks(r);
			int adjustedMidiValue = MidiPlayer.getAdjustedMidiValue(Project.DEFAULT_INSTRUMENT,
					r.getNoteName().getMidi());
			handler.post(new MidiRunnable(MidiSignals.NOTE_OFF, NoteName.getNoteNameFromMidiValue(adjustedMidiValue), 0, handler,
					midiDriver, pianoView, (byte) 0, r.getNoteName()));
		}
		playRunnables.clear();
	}
}
