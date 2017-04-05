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
package org.catrobat.catroid.test.pocketmusic.note.trackgrid;

import android.util.SparseArray;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRowPosition;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;

import java.util.ArrayList;
import java.util.List;

public final class TrackGridTestDataFactory {

	private TrackGridTestDataFactory() {
	}

	private static SparseArray<List<GridRowPosition>> createGridRowPositionsGridRow(int[] columnStartIndices) {

		SparseArray<List<GridRowPosition>> gridRowContent = new SparseArray<>();

		for (int columnStartIndex : columnStartIndices) {
			int sparseArrayIndex = columnStartIndex / TrackRowView.QUARTER_COUNT;

			List<GridRowPosition> gridRowPositions = gridRowContent.get(sparseArrayIndex);
			if (gridRowPositions == null) {
				gridRowPositions = new ArrayList<>();
				gridRowContent.put(sparseArrayIndex, gridRowPositions);
			}

			gridRowPositions.add(new GridRowPosition(columnStartIndex % TrackRowView.QUARTER_COUNT,
					NoteLength.QUARTER));
			gridRowContent.put(sparseArrayIndex, gridRowPositions);
		}

		return gridRowContent;
	}

	public static TrackGrid createFirstOctaveOnlyTrackGrid() {

		List<GridRow> gridRows = new ArrayList<GridRow>();

		SparseArray<List<GridRowPosition>> gridRowPositionC1 = new SparseArray<List<GridRowPosition>>();

		List<GridRowPosition> gridRowPositionListC1 = new ArrayList<GridRowPosition>();
		gridRowPositionListC1.add(new GridRowPosition(0, NoteLength.QUARTER));
		gridRowPositionListC1.add(new GridRowPosition(1, NoteLength.QUARTER));
		gridRowPositionListC1.add(new GridRowPosition(2, NoteLength.QUARTER));
		gridRowPositionListC1.add(new GridRowPosition(3, NoteLength.QUARTER));

		gridRowPositionC1.put(0, gridRowPositionListC1);

		gridRows.add(new GridRow(NoteName.C1, gridRowPositionC1));

		return new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_4_4, gridRows);
	}

	public static TrackGrid createSimpleTrackGrid() {

		List<GridRow> gridRows = new ArrayList<GridRow>();

		gridRows.add(new GridRow(NoteName.C4, createGridRowPositionsGridRow(new int[] { 0, 3 })));
		gridRows.add(new GridRow(NoteName.E4, createGridRowPositionsGridRow(new int[] { 1 })));
		gridRows.add(new GridRow(NoteName.F4, createGridRowPositionsGridRow(new int[] { 2 })));
		gridRows.add(new GridRow(NoteName.C5, createGridRowPositionsGridRow(new int[] { 4 })));

		return new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_4_4, gridRows);
	}

	public static TrackGrid createTrackGridWithSeveralBreaks() {

		List<GridRow> gridRows = new ArrayList<GridRow>();

		gridRows.add(new GridRow(NoteName.C4, createGridRowPositionsGridRow(new int[] { 3 })));

		return new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_4_4, gridRows);
	}

	public static TrackGrid createSemiComplexTrackGrid() {

		List<GridRow> gridRows = new ArrayList<GridRow>();

		gridRows.add(new GridRow(NoteName.C5, createGridRowPositionsGridRow(new int[] { 0 })));
		gridRows.add(new GridRow(NoteName.C4, createGridRowPositionsGridRow(new int[] { 1, 2 })));
		gridRows.add(new GridRow(NoteName.D4, createGridRowPositionsGridRow(new int[] { 1, 2 })));
		gridRows.add(new GridRow(NoteName.E4, createGridRowPositionsGridRow(new int[] { 3 })));
		gridRows.add(new GridRow(NoteName.F4, createGridRowPositionsGridRow(new int[] { 3 })));

		return new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_4_4, gridRows);
	}

	public static TrackGrid createEmptyTrackGrid() {

		List<GridRow> gridRows = new ArrayList<GridRow>();
		return new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_4_4, gridRows);
	}
}
