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
package org.catrobat.catroid.test.pocketmusic.note.trackgrid;

import android.util.SparseArray;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRowPosition;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TrackGridTestDataFactory {

	private TrackGridTestDataFactory() {
	}

	public static TrackGrid createSimpleTrackGrid() {

		List<GridRow> gridRows = new ArrayList<GridRow>();

		SparseArray<List<GridRowPosition>> gridRowPositionC4 = new SparseArray<List<GridRowPosition>>();
		GridRowPosition gridRowPosition = new GridRowPosition(0, 0, NoteLength.QUARTER);
		List<GridRowPosition> gridRowPositionListC4 = new ArrayList<GridRowPosition>();
		gridRowPositionListC4.add(gridRowPosition);

		GridRowPosition gridRowPosition1 = new GridRowPosition(3, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) * 3, NoteLength.QUARTER);
		gridRowPositionListC4.add(gridRowPosition1);

		gridRowPositionC4.put(0, gridRowPositionListC4);

		SparseArray<List<GridRowPosition>> gridRowPositionF4 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionF4List = new ArrayList<GridRowPosition>();
		GridRowPosition gridRowPositionF4v1 = new GridRowPosition(2, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) * 2, NoteLength.QUARTER);
		gridRowPositionF4List.add(gridRowPositionF4v1);

		gridRowPositionF4.put(0, gridRowPositionF4List);

		SparseArray<List<GridRowPosition>> gridRowPositionE4 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionE4List = new ArrayList<GridRowPosition>();
		GridRowPosition gridRowPositionE4v1 = new GridRowPosition(1, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE), NoteLength.QUARTER);
		gridRowPositionE4List.add(gridRowPositionE4v1);

		gridRowPositionE4.put(0, gridRowPositionE4List);

		SparseArray<List<GridRowPosition>> gridRowPositionC5 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionC5List = new ArrayList<GridRowPosition>();
		GridRowPosition gridRowPositionC5v1 = new GridRowPosition(4, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) * 4, NoteLength.QUARTER);
		gridRowPositionC5List.add(gridRowPositionC5v1);

		gridRowPositionC5.put(1, gridRowPositionC5List);

		gridRows.add(new GridRow(NoteName.C4, gridRowPositionC4));
		gridRows.add(new GridRow(NoteName.E4, gridRowPositionE4));
		gridRows.add(new GridRow(NoteName.F4, gridRowPositionF4));
		gridRows.add(new GridRow(NoteName.C5, gridRowPositionC5));

		TrackGrid trackGrid = new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_4_4, gridRows);

		return trackGrid;
	}

	public static TrackGrid createTrackGridWithSeveralBreaks() {

		List<GridRow> gridRows = new ArrayList<GridRow>();
		SparseArray<List<GridRowPosition>> gridRowPositionC4 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionsC4List = new ArrayList<GridRowPosition>();

		GridRowPosition gridRowPosition1 = new GridRowPosition(9, NoteLength.SIXTEENTH.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) + NoteLength.HALF.toTicks(Project.DEFAULT_BEATS_PER_MINUTE),
				NoteLength.QUARTER);
		gridRowPosition1.setColumnStartIndex(9);
		gridRowPositionsC4List.add(gridRowPosition1);

		gridRowPositionC4.put(0, gridRowPositionsC4List);

		gridRows.add(new GridRow(NoteName.C4, gridRowPositionC4));

		TrackGrid trackGrid = new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_16_16, gridRows);

		return trackGrid;
	}

	public static TrackGrid createSemiComplexTrackGrid(MusicalInstrument instrument) {

		SparseArray<List<GridRowPosition>> gridRowPositionsC2 = new SparseArray<List<GridRowPosition>>();
		GridRowPosition gridRowPositionC2 = new GridRowPosition(0, 0, NoteLength.QUARTER);
		List<GridRowPosition> gridRowPositionListC2 = new ArrayList<GridRowPosition>();
		gridRowPositionListC2.add(gridRowPositionC2);

		gridRowPositionsC2.put(0, gridRowPositionListC2);

		SparseArray<List<GridRowPosition>> gridRowPositionsC1 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionC1List = new ArrayList<GridRowPosition>();
		GridRowPosition gridRowPositionC1v1 = new GridRowPosition(4, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE), NoteLength.QUARTER);

		GridRowPosition gridRowPositionC1v2 = new GridRowPosition(8, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) * 2, NoteLength.QUARTER_DOT);
		gridRowPositionC1List.add(gridRowPositionC1v1);
		gridRowPositionC1List.add(gridRowPositionC1v2);

		gridRowPositionsC1.put(0, gridRowPositionC1List);

		SparseArray<List<GridRowPosition>> gridRowPositionsD1 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionD1List = new ArrayList<GridRowPosition>();
		GridRowPosition gridRowPositionD1v1 = new GridRowPosition(4, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE), NoteLength.QUARTER);

		GridRowPosition gridRowPositionD1v2 = new GridRowPosition(8, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) * 2, NoteLength.QUARTER_DOT);
		gridRowPositionD1List.add(gridRowPositionD1v1);
		gridRowPositionD1List.add(gridRowPositionD1v2);

		gridRowPositionsD1.put(0, gridRowPositionD1List);

		SparseArray<List<GridRowPosition>> gridRowPositionsE1 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionE1v1List = new ArrayList<GridRowPosition>();
		List<GridRowPosition> gridRowPositionE1v2List = new ArrayList<GridRowPosition>();
		GridRowPosition gridRowPositionE1v1 = new GridRowPosition(14, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) * 2 + NoteLength.QUARTER_DOT.toTicks(Project.DEFAULT_BEATS_PER_MINUTE),
				NoteLength.QUARTER_DOT);
		gridRowPositionE1v1List.add(gridRowPositionE1v1);
		gridRowPositionE1v2List.add(gridRowPositionE1v1);

		gridRowPositionsE1.put(0, gridRowPositionE1v1List);
		gridRowPositionsE1.put(1, gridRowPositionE1v2List);

		SparseArray<List<GridRowPosition>> gridRowPositionsF1 = new SparseArray<List<GridRowPosition>>();
		List<GridRowPosition> gridRowPositionF1v1List = new ArrayList<GridRowPosition>();
		List<GridRowPosition> gridRowPositionF1v2List = new ArrayList<GridRowPosition>();
		GridRowPosition gridRowPositionF1v1 = new GridRowPosition(14, NoteLength.QUARTER.toTicks(Project
				.DEFAULT_BEATS_PER_MINUTE) * 2 + NoteLength.QUARTER_DOT.toTicks(Project.DEFAULT_BEATS_PER_MINUTE),
				NoteLength.QUARTER_DOT);
		gridRowPositionF1v1List.add(gridRowPositionF1v1);
		gridRowPositionF1v2List.add(gridRowPositionF1v1);

		gridRowPositionsF1.put(0, gridRowPositionF1v1List);
		gridRowPositionsF1.put(1, gridRowPositionF1v2List);

		List<GridRow> gridRows = new ArrayList<GridRow>();

		gridRows.add(new GridRow(NoteName.C2, gridRowPositionsC2));
		gridRows.add(new GridRow(NoteName.C1, gridRowPositionsC1));
		gridRows.add(new GridRow(NoteName.D1, gridRowPositionsD1));
		gridRows.add(new GridRow(NoteName.E1, gridRowPositionsE1));
		gridRows.add(new GridRow(NoteName.F1, gridRowPositionsF1));

		TrackGrid trackGrid = new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO, MusicalBeat
				.BEAT_16_16, gridRows);

		return trackGrid;
	}
}
