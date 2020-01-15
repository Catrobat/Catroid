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
package org.catrobat.catroid.test.note.trackgrid;

import android.support.v4.util.SparseArrayCompat;

import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRowPosition;

import java.util.ArrayList;
import java.util.List;

public final class GridRowTestDataFactory {

	private static final NoteName NOTE_NAME_C4 = new NoteName(60);

	private GridRowTestDataFactory() {
	}

	public static GridRow createGridRowWithOnePosition() {
		SparseArrayCompat<List<GridRowPosition>> gridRowPositionSparseArray = new SparseArrayCompat<>();

		List<GridRowPosition> gridRowPositionList = new ArrayList<>();
		gridRowPositionList.add(new GridRowPosition(0, NoteLength.QUARTER));
		gridRowPositionSparseArray.put(0, gridRowPositionList);

		return new GridRow(NOTE_NAME_C4, gridRowPositionSparseArray);
	}

	public static GridRow createGridRowWithDuplicatePositions() {
		SparseArrayCompat<List<GridRowPosition>> gridRowPositionSparseArray = new SparseArrayCompat<>();

		List<GridRowPosition> gridRowPositionList = new ArrayList<>();

		gridRowPositionList.add(new GridRowPosition(0, NoteLength.QUARTER));
		gridRowPositionList.add(new GridRowPosition(0, NoteLength.QUARTER));
		gridRowPositionSparseArray.put(0, gridRowPositionList);

		return new GridRow(NOTE_NAME_C4, gridRowPositionSparseArray);
	}

	public static GridRow createGridRowWithDifferentPositions() {
		SparseArrayCompat<List<GridRowPosition>> gridRowPositionSparseArray = new SparseArrayCompat<>();

		List<GridRowPosition> gridRowPositionList = new ArrayList<>();

		gridRowPositionList.add(new GridRowPosition(0, NoteLength.QUARTER));
		gridRowPositionList.add(new GridRowPosition(4, NoteLength.QUARTER));
		gridRowPositionSparseArray.put(0, gridRowPositionList);

		return new GridRow(NOTE_NAME_C4, gridRowPositionSparseArray);
	}
}
