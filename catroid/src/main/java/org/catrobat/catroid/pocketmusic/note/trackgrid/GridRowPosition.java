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

import org.catrobat.catroid.pocketmusic.note.NoteLength;

public class GridRowPosition {

	private int columnStartIndex;
	private final long startTicksInTrack;
	private final NoteLength noteLength;

	public GridRowPosition(int columnStartIndex, long startTicksInTrack, NoteLength noteLength) {
		this.columnStartIndex = columnStartIndex;
		this.startTicksInTrack = startTicksInTrack;
		this.noteLength = noteLength;
	}

	public int getColumnStartIndex() {
		return columnStartIndex;
	}

	public NoteLength getNoteLength() {
		return noteLength;
	}

	public void setColumnStartIndex(int columnStartIndex) {
		this.columnStartIndex = columnStartIndex;
	}

	public long getStartTicksInTrack() {
		return startTicksInTrack;
	}

	@Override
	public int hashCode() {
		int hashCode = 23;
		int primeWithGoodCollisionPrevention = 31;
		hashCode = primeWithGoodCollisionPrevention * hashCode + columnStartIndex;
		hashCode = primeWithGoodCollisionPrevention * hashCode + (int) startTicksInTrack;
		hashCode = primeWithGoodCollisionPrevention * hashCode + noteLength.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		GridRowPosition reference = (GridRowPosition) o;
		return reference.columnStartIndex == columnStartIndex
				&& reference.startTicksInTrack == startTicksInTrack
				&& reference.noteLength.equals(noteLength);
	}
}
